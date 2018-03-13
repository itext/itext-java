package com.itextpdf.svg.dummy.css.impl;

import com.itextpdf.styledxmlparser.css.ICssContext;
import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.svg.css.SvgCssContext;

import java.util.HashMap;
import java.util.Map;

public class DummyCssResolver implements ICssResolver {
    @Override
    public Map<String, String> resolveStyles(INode node, ICssContext context) {
        return new HashMap<String,String>();
    }
}
