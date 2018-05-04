package com.itextpdf.svg.processors.impl;

import com.itextpdf.svg.processors.ISvgProcessor;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;

import java.util.Map;

/**
 * A wrapper class that encapsulates processing results of {@link ISvgProcessor} objects.
 */
public class DefaultSvgProcessorResult implements ISvgProcessorResult {

    private Map<String, ISvgNodeRenderer> namedObjects;
    private ISvgNodeRenderer root;

    public DefaultSvgProcessorResult(Map<String, ISvgNodeRenderer> namedObjects, ISvgNodeRenderer root) {
        this.namedObjects = namedObjects;
        this.root = root;
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
    public boolean equals(Object o){
        if(o == null || (!o.getClass().equals(this.getClass()))){
            return false;
        }
        DefaultSvgProcessorResult otherResult = (DefaultSvgProcessorResult) o;
        return otherResult.getNamedObjects().equals(this.getNamedObjects()) && otherResult.getRootRenderer().equals(this.getRootRenderer());
    }

    @Override
    public int hashCode(){
        int hash = getNamedObjects().hashCode() + 42*getRootRenderer().hashCode();
        return hash;
    }

}
