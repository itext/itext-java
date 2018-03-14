package com.itextpdf.svg.css.impl;

import com.itextpdf.styledxmlparser.AttributeConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.ICssContext;
import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.parse.CssRuleSetParser;
import com.itextpdf.styledxmlparser.css.parse.CssStyleSheetParser;
import com.itextpdf.styledxmlparser.node.IAttribute;
import com.itextpdf.styledxmlparser.node.IDataNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.ITextNode;
import com.itextpdf.svg.SvgTagConstants;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Default CSS resolver implementation.
 */
public class DefaultSvgStyleResolver implements ICssResolver {

    private CssStyleSheet internalStyleSheet;

    /**
     * Creates a DefaultSvgStyleResolver. This constructor will instantiate its internal style sheet and it
     * will collect the css declarations from the provided node.
     *
     * @param rootNode node to collect css from
     */
    public DefaultSvgStyleResolver(INode rootNode) {
        internalStyleSheet = new CssStyleSheet();
        collectCssDeclarations(rootNode);
    }

    @Override
    public Map<String, String> resolveStyles(INode node, ICssContext context) {
        Map<String, String> styles = new HashMap<>();
        //Load in defaults
        //TODO (RND-865): Figure out if defaults are necessary
        //Load in from collected style sheets
        List<CssDeclaration> styleSheetDeclarations = internalStyleSheet.getCssDeclarations(node, MediaDeviceDescription.createDefault());
        for (CssDeclaration ssd : styleSheetDeclarations) {
            styles.put(ssd.getProperty(), ssd.getExpression());
        }
        //Load in inherited declarations from parent
        //TODO: RND-880
        //Load in attributes declarations
        if (node instanceof IElementNode) {
            IElementNode eNode = (IElementNode) node;
            for (IAttribute attr : eNode.getAttributes()) {
                processAttribute(attr, styles);
            }
        }

        return styles;
    }

    private void processAttribute(IAttribute attr, Map<String, String> styles) {
        //Style attribute needs to be parsed further
        if (attr.getKey().equals(AttributeConstants.STYLE)) {
            Map<String, String> parsed = parseStylesFromStyleAttribute(attr.getValue());
            for (Map.Entry<String, String> style : parsed.entrySet()) {
                styles.put(style.getKey(), style.getValue());
            }
        } else {
            styles.put(attr.getKey(), attr.getValue());
        }
    }

    private Map<String, String> parseStylesFromStyleAttribute(String style) {
        Map<String, String> parsed = new HashMap<>();
        List<CssDeclaration> declarations = CssRuleSetParser.parsePropertyDeclarations(style);
        for (CssDeclaration declaration : declarations) {
            parsed.put(declaration.getProperty(), declaration.getExpression());
        }
        return parsed;
    }

    private void collectCssDeclarations(INode rootNode) {
        internalStyleSheet = new CssStyleSheet();
        LinkedList<INode> q = new LinkedList<>();
        if (rootNode != null) {
            q.add(rootNode);
        }
        while (! q.isEmpty()) {
            INode currentNode = q.getFirst();
            q.removeFirst();
            if (currentNode instanceof IElementNode) {
                IElementNode headChildElement = (IElementNode) currentNode;
                if (headChildElement.name().equals(SvgTagConstants.STYLE)) {//XML parser will parse style tag contents as text nodes
                    if (currentNode.childNodes().size() > 0 && ( currentNode.childNodes().get(0) instanceof IDataNode || currentNode.childNodes().get(0) instanceof ITextNode )) {
                        String styleData;
                        if (currentNode.childNodes().get(0) instanceof IDataNode) {
                            styleData = ( (IDataNode) currentNode.childNodes().get(0) ).getWholeData();
                        } else {
                            styleData = ( (ITextNode) currentNode.childNodes().get(0) ).wholeText();
                        }
                        CssStyleSheet styleSheet = CssStyleSheetParser.parse(styleData);
                        //TODO(RND-863): mediaquery wrap
                        //styleSheet = wrapStyleSheetInMediaQueryIfNecessary(headChildElement, styleSheet);
                        internalStyleSheet.appendCssStyleSheet(styleSheet);
                    }
                }
                //TODO(RND-864): resolution of external style sheets via the link tag
            }

            for (INode child : currentNode.childNodes()) {
                if (child instanceof IElementNode) {
                    q.add(child);
                }
            }
        }
    }
}



