package com.itextpdf.svg.css.impl;

import com.itextpdf.styledxmlparser.AttributeConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.ICssContext;
import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.parse.CssRuleSetParser;
import com.itextpdf.styledxmlparser.css.parse.CssStyleSheetParser;
import com.itextpdf.styledxmlparser.jsoup.nodes.TextNode;
import com.itextpdf.styledxmlparser.node.IAttribute;
import com.itextpdf.styledxmlparser.node.IDataNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.ITextNode;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.css.SvgCssContext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultSvgStyleResolver implements ICssResolver {
    private CssStyleSheet internalStyleSheet;


    public DefaultSvgStyleResolver(INode rootNode) {
        internalStyleSheet = new CssStyleSheet();
        collectCssDeclarations(rootNode);
    }

    @Override
    public Map<String, String> resolveStyles(INode node, ICssContext context) {
        Map<String, String> styles = new HashMap<>();
        //Load in defaults
        //TODO: Figure out if defaults are necessary
        //Load in from collected style sheets
        //TODO: quick band-aid for loading in internal style sheets
        List<CssDeclaration> styleSheetDeclarations = internalStyleSheet.getCssDeclarations(node, MediaDeviceDescription.createDefault());
        for (CssDeclaration ssd:styleSheetDeclarations) {
            styles.put(ssd.getProperty(),ssd.getExpression());
        }
        //Load in inherited declarations from parent
        //TODO: parent inheritance
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
        while (!q.isEmpty()) {
            INode currentNode = q.getFirst();
            q.removeFirst();
            if (currentNode instanceof IElementNode) {
                IElementNode headChildElement = (IElementNode) currentNode;
                if (headChildElement.name().equals(SvgTagConstants.STYLE)) {//XML parser will parse style tag contents as text nodes
                    if (currentNode.childNodes().size() > 0 && (currentNode.childNodes().get(0) instanceof IDataNode||currentNode.childNodes().get(0)  instanceof ITextNode)) {
                        String styleData;
                        if(currentNode.childNodes().get(0) instanceof IDataNode) {
                            styleData = ((IDataNode) currentNode.childNodes().get(0)).getWholeData();
                        }else{
                            styleData = ((ITextNode) currentNode.childNodes().get(0)).wholeText();
                        }
                        CssStyleSheet styleSheet = CssStyleSheetParser.parse(styleData);
                        //TODO: mediaquery wrap
                        //styleSheet = wrapStyleSheetInMediaQueryIfNecessary(headChildElement, styleSheet);
                        internalStyleSheet.appendCssStyleSheet(styleSheet);
                    }
                }
                //TODO resolution of external style sheets via the link tag
            }

            for (INode child : currentNode.childNodes()) {
                if (child instanceof IElementNode) {
                    q.add(child);
                }
            }
        }
    }

    /**
     * Wraps a {@link CssMediaRule} into the style sheet if the head child element has a media attribute.
     *
     * @param headChildElement the head child element
     * @param styleSheet       the style sheet
     * @return the css style sheet
     */
//    private CssStyleSheet wrapStyleSheetInMediaQueryIfNecessary(IElementNode headChildElement, CssStyleSheet styleSheet) {
//        String mediaAttribute = headChildElement.getAttribute(AttributeConstants.MEDIA);
//        if (mediaAttribute != null && mediaAttribute.length() > 0) {
//            CssMediaRule mediaRule = new CssMediaRule(mediaAttribute);
//            mediaRule.addStatementsToBody(styleSheet.getStatements());
//            styleSheet = new CssStyleSheet();
//            styleSheet.addStatement(mediaRule);
//        }
//        return styleSheet;
//    }
}



