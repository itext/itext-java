/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.svg.processors.impl;

import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.ITextNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.SvgConstants.Tags;
import com.itextpdf.svg.css.impl.SvgStyleResolver;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.ISvgProcessor;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.impl.font.SvgFontProcessor;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.INoDrawSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.factories.DefaultSvgNodeRendererFactory;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;
import com.itextpdf.svg.renderers.impl.AbstractGradientSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.DefsSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.ForeignObjectNodeRenderer;
import com.itextpdf.svg.renderers.impl.ISvgTextNodeRenderer;
import com.itextpdf.svg.renderers.impl.StopSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.TextSvgBranchRenderer;
import com.itextpdf.svg.utils.SvgTextUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Default implementation of {@link ISvgProcessor}.
 * This implementation traverses the {@link INode} tree depth-first,
 * using a stack to recreate a tree of {@link ISvgNodeRenderer} with the same structure.
 */
public class DefaultSvgProcessor implements ISvgProcessor {

    private ProcessorState processorState;

    private ICssResolver cssResolver;
    private ISvgNodeRendererFactory rendererFactory;
    private Map<String, ISvgNodeRenderer> namedObjects;
    private SvgProcessorContext context;

    /**
     * Instantiates a DefaultSvgProcessor object.
     */
    public DefaultSvgProcessor() {
    }

    @Override
    public ISvgProcessorResult process(INode root, ISvgConverterProperties converterProps)
            throws SvgProcessingException {
        if (root == null) {
            throw new SvgProcessingException(SvgExceptionMessageConstant.I_NODE_ROOT_IS_NULL);
        }
        if (converterProps == null) {
            converterProps = new SvgConverterProperties();
        }
        //Setup processorState
        performSetup(root, converterProps);

        //Find root
        IElementNode svgRoot = findFirstElement(root, SvgConstants.Tags.SVG);

        if (svgRoot != null) {
            //Iterate over children
            executeDepthFirstTraversal(svgRoot);
            ISvgNodeRenderer rootSvgRenderer = createResultAndClean();
            return new SvgProcessorResult(namedObjects, rootSvgRenderer, context);
        } else {
            throw new SvgProcessingException(SvgExceptionMessageConstant.NO_ROOT);
        }
    }

    /**
     * Handles the foreign object handling, currently we just render the plain text content
     * for nodes which use the xhtml namespace. Content not having this namespace will not be displayed.
     *
     * @param node the {@code foreignObject} node
     */
    protected void visitForeignObject(INode node) {
        if (node instanceof IElementNode) {
            IElementNode element = (IElementNode) node;
            if (!isXhtmlElement(element)) {
                return;
            }
            String name = element.name();
            name = name.substring(name.indexOf(':') + 1);
            if ("script".equalsIgnoreCase(name) || Tags.STYLE.equalsIgnoreCase(name)) {
                return;
            }
            for (INode child : node.childNodes()) {
                visitForeignObject(child);
            }
        } else if (node instanceof ITextNode && node.parentNode() instanceof IElementNode) {
            IElementNode parent = (IElementNode) node.parentNode();
            boolean shouldShowText = isXhtmlElement(parent) || Tags.FOREIGN_OBJECT.equalsIgnoreCase(parent.name());
            if (shouldShowText) {
                processText((ITextNode) node);
            }
        }
    }

    /**
     * Load in configuration, set initial processorState and create/fill-in context of the processor
     *
     * @param converterProps that contains configuration properties and operations
     */
    void performSetup(INode root, ISvgConverterProperties converterProps) {
        processorState = new ProcessorState();
        if (converterProps.getRendererFactory() != null) {
            rendererFactory = converterProps.getRendererFactory();
        } else {
            rendererFactory = new DefaultSvgNodeRendererFactory();
        }
        context = new SvgProcessorContext(converterProps);
        cssResolver = new SvgStyleResolver(root, context);
        new SvgFontProcessor(context).addFontFaceFonts(cssResolver);
        namedObjects = new HashMap<>();
    }

    /**
     * Start the depth-first traversal of the INode tree, pushing the results on the stack
     *
     * @param startingNode node to start on
     */
    void executeDepthFirstTraversal(INode startingNode) {
        //Create and push rootNode
        if (startingNode instanceof IElementNode && !rendererFactory.isTagIgnored((IElementNode) startingNode)) {
            IElementNode rootElementNode = (IElementNode) startingNode;

            ISvgNodeRenderer startingRenderer = rendererFactory.createSvgNodeRendererForTag(rootElementNode, null);
            if (startingRenderer != null) {
                Map<String, String> attributesAndStyles = cssResolver.resolveStyles(startingNode,
                        context.getCssContext());
                rootElementNode.setStyles(attributesAndStyles);
                startingRenderer.setAttributesAndStyles(attributesAndStyles);
                processorState.push(startingRenderer);
                for (INode rootChild : startingNode.childNodes()) {
                    visit(rootChild);
                }
            }
        }
    }

    /**
     * Extract result from internal processorState and clean up afterwards
     *
     * @return Root renderer of the processed SVG
     */
    private ISvgNodeRenderer createResultAndClean() {
        return processorState.pop();
    }

    /**
     * Recursive visit of the object tree, depth-first, processing the visited node and calling visit on its children.
     * Visit responsibilities for element nodes:
     * - Assign styles(CSS and attributes) to element
     * - Create Renderer based on element
     * - push and pop renderer to stack
     * Visit responsibilities for text nodes
     * - add text to parent object
     *
     * @param node INode to visit
     */
    private void visit(INode node) {
        if (processorState.top() instanceof ForeignObjectNodeRenderer) {
            // Foreign gets flattened to only text, even for tag names shared with SVG.
            visitForeignObject(node);
            return;
        }
        if (node instanceof IElementNode) {
            IElementNode element = (IElementNode) node;

            if (!rendererFactory.isTagIgnored(element)) {
                ISvgNodeRenderer parentRenderer = processorState.top();
                ISvgNodeRenderer renderer = rendererFactory.createSvgNodeRendererForTag(element, parentRenderer);
                if (renderer != null) {
                    final Map<String, String> styles = cssResolver.resolveStyles(node, context.getCssContext());
                    // For inheritance
                    element.setStyles(styles);
                    // For drawing operations
                    renderer.setAttributesAndStyles(styles);

                    String attribute = renderer.getAttribute(SvgConstants.Attributes.ID);
                    if (attribute != null) {
                        namedObjects.put(attribute, renderer);
                    }

                    if (renderer instanceof StopSvgNodeRenderer) {
                        if (parentRenderer instanceof AbstractGradientSvgNodeRenderer) {
                            // It is necessary to add StopSvgNodeRenderer only as a child of gradient renderer,
                            // because StopSvgNodeRenderer performs an auxiliary function and should not be drawn at all
                            ((AbstractGradientSvgNodeRenderer) parentRenderer).addChild(renderer);
                        }
                    } else if (!(renderer instanceof INoDrawSvgNodeRenderer)
                            && !(parentRenderer instanceof DefsSvgNodeRenderer)) {
                        // DefsSvgNodeRenderer should not have parental relationship with any renderer, it only
                        // serves as a storage
                        if (parentRenderer instanceof IBranchSvgNodeRenderer) {
                            ((IBranchSvgNodeRenderer) parentRenderer).addChild(renderer);
                        } else if (parentRenderer instanceof TextSvgBranchRenderer
                                && renderer instanceof ISvgTextNodeRenderer) {
                            // Text branch node renderers only accept ISvgTextNodeRenderers
                            ((TextSvgBranchRenderer) parentRenderer).addChild((ISvgTextNodeRenderer) renderer);
                        }
                    }

                    processorState.push(renderer);
                }

                for (INode childNode : element.childNodes()) {
                    visit(childNode);
                }

                if (renderer != null) {
                    processorState.pop();
                }
            }
        } else if (processAsText(node)) {
            processText((ITextNode) node);
        }
    }

    private static boolean isXhtmlElement(IElementNode element) {
        String name = element.name();
        int prefixEnd = name.indexOf(':');
        String namespaceAttribute = prefixEnd < 0 ? SvgConstants.Attributes.XMLNS
                : (SvgConstants.Attributes.XMLNS + ":" + name.substring(0, prefixEnd));
        for (INode current = element; current != null; current = current.parentNode()) {
            if (!(current instanceof IElementNode)) {
                continue;
            }
            String namespace = ((IElementNode) current).getAttribute(namespaceAttribute);
            if (namespace == null) {
                continue;
            }
            return "http://www.w3.org/1999/xhtml".equals(namespace);
        }
        return false;
    }


    /**
     * Check if this node is a text node that needs to be processed by the parent
     *
     * @param node node to check
     *
     * @return true if the node should be processed as text, false otherwise
     */
    private boolean processAsText(INode node) {
        return node instanceof ITextNode;
    }

    /**
     * Process the text contained in the text-node
     *
     * @param textNode node containing text to process
     */
    private void processText(ITextNode textNode) {
        ISvgNodeRenderer parentRenderer = this.processorState.top();

        boolean isForeignObject = parentRenderer instanceof ForeignObjectNodeRenderer;
        if (parentRenderer instanceof TextSvgBranchRenderer || isForeignObject) {
            String wholeText = textNode.wholeText();
            if (!"".equals(wholeText) && (isForeignObject || !SvgTextUtil.isOnlyWhiteSpace(wholeText))) {
                final IElementNode textLeafElement = new JsoupElementNode(new Element(Tag.valueOf(Tags.TEXT_LEAF), ""));
                ISvgTextNodeRenderer textLeaf = (ISvgTextNodeRenderer) this.rendererFactory
                        .createSvgNodeRendererForTag(textLeafElement, parentRenderer);
                textLeaf.setParent(parentRenderer);
                textLeaf.setAttribute(SvgConstants.Attributes.TEXT_CONTENT, wholeText);
                if (isForeignObject) {
                    ((ForeignObjectNodeRenderer) parentRenderer).addChild(textLeaf);
                } else {
                    ((TextSvgBranchRenderer) parentRenderer).addChild(textLeaf);
                }
            }
        }
    }

    /**
     * Find the first element in the node-tree that corresponds with the passed tag-name. Search is performed
     * depth-first
     *
     * @param node    root-node to start with
     * @param tagName name of the tag that needs to be fonund
     *
     * @return IElementNode
     */
    IElementNode findFirstElement(INode node, String tagName) {
        LinkedList<INode> q = new LinkedList<>();
        q.add(node);

        while (!q.isEmpty()) {
            INode currentNode = q.getFirst();
            q.removeFirst();

            if (currentNode == null) {
                return null;
            }

            if (currentNode instanceof IElementNode && ((IElementNode) currentNode).name() != null
                    && ((IElementNode) currentNode).name().equals(tagName)) {
                return (IElementNode) currentNode;
            }

            for (INode child : currentNode.childNodes()) {
                if (child instanceof IElementNode) {
                    q.add(child);
                }
            }
        }

        return null;
    }
}
