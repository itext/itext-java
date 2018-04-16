/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.processors.impl;

import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.ITextNode;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.css.SvgCssContext;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.ISvgProcessor;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;
import com.itextpdf.svg.renderers.impl.TextSvgNodeRenderer;
import com.itextpdf.svg.utils.SvgTextUtil;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link ISvgProcessor}.
 * This implementation traverses the {@link INode} tree depth-first,
 * using a stack to recreate a tree of {@link ISvgNodeRenderer} with the same structure.
 */
public class DefaultSvgProcessor implements ISvgProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSvgProcessor.class);

    private ProcessorState processorState;

    //Processor context
    private ICssResolver cssResolver;
    private SvgCssContext cssContext;
    private ISvgNodeRendererFactory rendererFactory;
    private ISvgConverterProperties defaultProps;

    /**
     * Instantiates a DefaultSvgProcessor object.
     */
    public DefaultSvgProcessor() {
    }

    @Override
    public ISvgNodeRenderer process(INode root) throws SvgProcessingException {
        return process(root, new DefaultSvgConverterProperties(root));
    }

    @Override
    public ISvgNodeRenderer process(INode root, ISvgConverterProperties converterProps) throws SvgProcessingException {
        if (root == null) {
            throw new SvgProcessingException(SvgLogMessageConstant.INODEROOTISNULL);
        }
        //Setup processorState
        if (converterProps != null) {
            performSetup(converterProps);
        } else {
            performSetup(new DefaultSvgConverterProperties(root));
        }
        //Find root
        IElementNode svgRoot = findFirstElement(root, SvgTagConstants.SVG);

        if (svgRoot != null) {
            //Iterate over children
            executeDepthFirstTraversal(svgRoot);

            ISvgNodeRenderer rootSvgRenderer = createResultAndClean();

            return rootSvgRenderer;
        } else {
            throw new SvgProcessingException(SvgLogMessageConstant.NOROOT);
        }
    }

    /**
     * Load in configuration, set initial processorState and create/fill-in context of the processor
     *
     * @param converterProps that contains configuration properties and operations
     */
    private void performSetup(ISvgConverterProperties converterProps) {
        processorState = new ProcessorState();
        if (converterProps.getCssResolver() != null) {
            cssResolver = converterProps.getCssResolver();
        } else {
            cssResolver = defaultProps.getCssResolver();
        }
        if (converterProps.getRendererFactory() != null) {
            rendererFactory = converterProps.getRendererFactory();
        } else {
            rendererFactory = defaultProps.getRendererFactory();
        }
        cssContext = new SvgCssContext();
        //TODO(RND-865): resolve/initialize CSS context
    }

    /**
     * Start the depth-first traversal of the INode tree, pushing the results on the stack
     *
     * @param startingNode node to start on
     */
    private void executeDepthFirstTraversal(INode startingNode) {
        //Create and push rootNode
        if (startingNode instanceof IElementNode && !rendererFactory.isTagIgnored((IElementNode) startingNode)) {
            IElementNode rootElementNode = (IElementNode) startingNode;

            ISvgNodeRenderer startingRenderer = rendererFactory.createSvgNodeRendererForTag(rootElementNode, null);
            startingRenderer.setAttributesAndStyles(cssResolver.resolveStyles(startingNode, cssContext));
            processorState.push(startingRenderer);
            for (INode rootChild : startingNode.childNodes()) {
                visit(rootChild);
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
     * - Assign styles(CSS & attributes) to element
     * - Create Renderer based on element
     * - push & pop renderer to stack
     * Visit responsibilities for text nodes
     * - add text to parent object
     *
     * @param node INode to visit
     */
    private void visit(INode node) {
        if (node instanceof IElementNode) {
            IElementNode element = (IElementNode) node;

            if (!rendererFactory.isTagIgnored(element)) {
                ISvgNodeRenderer renderer = createRenderer(element, processorState.top());
                if (renderer != null) {
                    renderer.setAttributesAndStyles(cssResolver.resolveStyles(node, cssContext));

                    // this check should be superfluous, but better safe than sorry
                    if (processorState.top() instanceof IBranchSvgNodeRenderer) {
                        ( (IBranchSvgNodeRenderer) processorState.top() ).addChild(renderer);
                    }

                    processorState.push(renderer);
                }

                for (INode childNode : element.childNodes()) {
                    visit(childNode);
                }

                if ( renderer != null ) {
                    processorState.pop();
                }
            }
        } else if (processAsText(node)) {
            processText((ITextNode) node);
        }
    }

    /**
     * Create renderer based on the passed SVG tag and assign its parent
     *
     * @param tag    SVG tag with all style attributes already assigned
     * @param parent renderer of the parent tag
     * @return Configured renderer for the tag
     */
    private ISvgNodeRenderer createRenderer(IElementNode tag, ISvgNodeRenderer parent) {
        return rendererFactory.createSvgNodeRendererForTag(tag, parent);
    }

    /**
     * Check if this node is a text node that needs to be processed by the parent
     *
     * @param node node to check
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

        if (parentRenderer != null && parentRenderer instanceof TextSvgNodeRenderer) {
            // when svg is parsed by jsoup it leaves all whitespace in text element as is. Meaning that
            // tab/space indented xml files will retain their tabs and spaces.
            // The following regex replaces all whitespace with a single space.
            //TODO(RND-906) evaluate regex and trim methods
            String trimmedText = textNode.wholeText().replaceAll("\\s+", " ");
            //Trim leading whitespace
            trimmedText = SvgTextUtil.trimLeadingWhitespace(trimmedText);
            //Trim trailing whitespace
            trimmedText = SvgTextUtil.trimTrailingWhitespace(trimmedText);
            parentRenderer.setAttribute(SvgTagConstants.TEXT_CONTENT, trimmedText);
        }
    }

    /**
     * Find the first element in the node-tree that corresponds with the passed tag-name. Search is performed depth-first
     *
     * @param node    root-node to start with
     * @param tagName name of the tag that needs to be fonund
     * @return IElementNode
     */
    private static IElementNode findFirstElement(INode node, String tagName) {
        LinkedList<INode> q = new LinkedList<>();
        q.add(node);

        while (!q.isEmpty()) {
            INode currentNode = q.getFirst();
            q.removeFirst();

            if (currentNode == null) {
                return null;
            }

            if (currentNode instanceof IElementNode && ( (IElementNode) currentNode ).name() != null && ( (IElementNode) currentNode ).name().equals(tagName)) {
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
