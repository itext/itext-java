package com.itextpdf.svg.dummy.renderers.impl;

import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

import java.util.Map;

/**
 * A dummy implementation of {@link ISvgNodeRenderer} for testing purposes
 */
public class DummySvgNodeRenderer implements ISvgNodeRenderer {

    ISvgNodeRenderer parent;
    String name;

    public DummySvgNodeRenderer() {
        this("dummy");
    }

    public DummySvgNodeRenderer(String name) {
        this.name = name;
    }

    @Override
    public void setParent(ISvgNodeRenderer parent) {
        this.parent = parent;
    }

    @Override
    public ISvgNodeRenderer getParent() {
        return parent;
    }

    @Override
    public void draw(SvgDrawContext context) {
        System.out.println(name + ": Drawing in dummy node");
    }

    @Override
    public void setAttributesAndStyles(Map<String, String> attributesAndStyles) {

    }

    @Override
    public String getAttribute(String key) {
        if (SvgTagConstants.WIDTH.equalsIgnoreCase(key) || SvgTagConstants.HEIGHT.equalsIgnoreCase(key)) {
            return "10";
        }

        return "";
    }

    @Override
    public void setAttribute(String key, String value) {
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DummySvgNodeRenderer)) {
            return false;
        }
        //Name
        DummySvgNodeRenderer otherDummy = (DummySvgNodeRenderer) o;
        return this.name.equals(otherDummy.name);
    }

}
