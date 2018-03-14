package com.itextpdf.svg.dummy.css.impl;

import com.itextpdf.styledxmlparser.css.ICssContext;
import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.styledxmlparser.node.IAttribute;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.HashMap;
import java.util.Map;

public class DummyCssResolver implements ICssResolver {
    @Override
    public Map<String, String> resolveStyles(INode node, ICssContext context) {
        Map<String, String> styles = new HashMap<>();

        if (node instanceof IElementNode) {
            IElementNode eNode = (IElementNode) node;

            for (IAttribute attr : eNode.getAttributes()) {
                styles.put(attr.getKey(), attr.getValue());
            }
        }

        return styles;
    }
}
