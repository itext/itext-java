package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
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

       /*Set line width when provided*/
        float Strokewidth = getAttribute( attributesAndStyles, SvgTagConstants.CSS_STROKE_WIDTH_PROPERTY );
        if (Strokewidth != 0)
            canvas.setLineWidth( Strokewidth );
        //TODO apply stroke when provided
        try {
            if (attributesAndStyles.size() > 0) {
                canvas.moveTo( getAttribute( attributesAndStyles, SvgTagConstants.X1 ),
                        getAttribute( attributesAndStyles, SvgTagConstants.Y1 ) )
                        .lineTo( getAttribute( attributesAndStyles, SvgTagConstants.X2 ),
                                getAttribute( attributesAndStyles, SvgTagConstants.Y2 ) )
                        .closePathStroke();
            }
        } catch (NumberFormatException e) {
            throw new SvgProcessingException( SvgLogMessageConstant.FLOAT_PARSING_NAN, e );
        }
    }

    private float getAttribute(Map<String, String> attributes, String key) {
        String value = attributes.get( key );
        if (value != null && !value.isEmpty()) {
            return Float.valueOf( attributes.get( key ) );
        }
        return 0;
    }
}
