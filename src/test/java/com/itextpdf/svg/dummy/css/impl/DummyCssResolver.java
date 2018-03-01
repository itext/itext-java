package com.itextpdf.svg.dummy.css.impl;

import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.svg.css.CssContext;
import com.itextpdf.svg.css.ICssResolver;

import java.util.HashMap;
import java.util.Map;

public class DummyCssResolver implements ICssResolver {
    @Override
    public Map<String, String> resolveStyles(INode node, CssContext context) {
        return new HashMap<String,String>();
    }
}
