package com.itextpdf.svg.dummy.renderers.impl;

import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.SvgDrawContext;

public class DummyProcessableSvgNodeRenderer extends DummySvgNodeRenderer {
    
    private boolean processed = false;
    
    @Override
    public void draw(SvgDrawContext context) {
        if (processed) {
            throw new SvgProcessingException("Cannot process svg renderer twice");
        }
        processed = true;
    }
    
    public boolean isProcessed() {
        return processed;
    }
}
