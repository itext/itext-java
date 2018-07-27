package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

/**
 * Tags mapped onto this renderer won't be drawn and will be excluded from the renderer tree when processed.
 * Different from being added to the ignored list as this Renderer will allow its children to be processed.
 */
public class NoDrawOperationSvgNodeRenderer extends AbstractSvgNodeRenderer {

    @Override
    protected void doDraw(SvgDrawContext context) {
        throw new UnsupportedOperationException(SvgLogMessageConstant.DRAW_NO_DRAW);
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        NoDrawOperationSvgNodeRenderer copy = new NoDrawOperationSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }

}
