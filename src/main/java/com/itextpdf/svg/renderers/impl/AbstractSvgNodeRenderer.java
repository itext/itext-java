package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.renderers.ISvgNodeRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 */
public abstract class AbstractSvgNodeRenderer implements ISvgNodeRenderer {

    private ISvgNodeRenderer parent;
    private final List<ISvgNodeRenderer> children = new ArrayList<>();
    protected Map<String, String> attributesAndStyles;

    @Override
    public void setParent(ISvgNodeRenderer parent) {
        this.parent = parent;
    }

    @Override
    public ISvgNodeRenderer getParent() {
        return parent;
    }

    @Override
    public final void addChild(ISvgNodeRenderer child) {
        // final method, in order to disallow adding null
        if (child != null) {
            children.add(child);
        }
    }

    @Override
    public final List<ISvgNodeRenderer> getChildren() {
        // final method, in order to disallow modifying the List
        return Collections.unmodifiableList(children);
    }

    @Override
    public void setAttributesAndStyles(Map<String,String> attributesAndStyles){
        this.attributesAndStyles = attributesAndStyles;
    }
}
