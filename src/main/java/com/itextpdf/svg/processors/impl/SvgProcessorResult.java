package com.itextpdf.svg.processors.impl;

import com.itextpdf.layout.font.FontSet;
import com.itextpdf.svg.processors.ISvgProcessor;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import java.util.Map;

/**
 * A wrapper class that encapsulates processing results of {@link ISvgProcessor} objects.
 */
public class SvgProcessorResult implements ISvgProcessorResult {

    private Map<String, ISvgNodeRenderer> namedObjects;
    private ISvgNodeRenderer root;
    private FontSet fontSet;

    public SvgProcessorResult(Map<String, ISvgNodeRenderer> namedObjects, ISvgNodeRenderer root, FontSet fontSet) {
        this.namedObjects = namedObjects;
        this.root = root;
        this.fontSet = fontSet;
    }

    @Override
    public Map<String, ISvgNodeRenderer> getNamedObjects() {
        return namedObjects;
    }

    @Override
    public ISvgNodeRenderer getRootRenderer() {
        return root;
    }

    @Override
    public FontSet getFontSet() {
        return fontSet;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || (!o.getClass().equals(this.getClass()))) {
            return false;
        }
        SvgProcessorResult otherResult = (SvgProcessorResult) o;
        return otherResult.getNamedObjects().equals(this.getNamedObjects()) && otherResult.getRootRenderer().equals(this.getRootRenderer());
    }

    @Override
    public int hashCode() {
        int hash = getNamedObjects().hashCode() + 43 * getRootRenderer().hashCode();
        return hash;
    }

}
