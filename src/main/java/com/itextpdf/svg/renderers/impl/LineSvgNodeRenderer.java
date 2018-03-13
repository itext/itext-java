package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

import java.util.Map;
/**
 * {@link ISvgNodeRenderer} implementation for the &lt;line&gt; tag.
 */
public class LineSvgNodeRenderer extends AbstractSvgNodeRenderer {
    private float x1, x2, y1, y2;


    public LineSvgNodeRenderer() {
    }

    public LineSvgNodeRenderer(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void doDraw(SvgDrawContext context) {
        PdfCanvas canvas = context.getCurrentCanvas();

        canvas.moveTo( getAttribute( attributesAndStyles, SvgTagConstants.X1 ),
                getAttribute( attributesAndStyles, SvgTagConstants.Y1 ) )
                .lineTo( getAttribute( attributesAndStyles, SvgTagConstants.X2 ),
                        getAttribute( attributesAndStyles, SvgTagConstants.Y2 ) );

    }

    public float getX1() {
        return x1;
    }

    public float getX2() {
        return x2;
    }

    public float getY1() {
        return y1;
    }

    public float getY2() {
        return y2;
    }

    private float getAttribute(Map<String, String> attributes, String key) {
        String value = attributes.get( key );
        if (value != null && !value.isEmpty()) {
            return Float.valueOf( attributes.get( key ) );
        }
        return 0;
    }
}
