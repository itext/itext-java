package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
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
    public LineSvgNodeRenderer() {
    }

    @Override
    public void doDraw(SvgDrawContext context) {
        PdfCanvas canvas = context.getCurrentCanvas();

        try {
            if (attributesAndStyles.size() > 0) {
                float x1 = 0f;
                float y1 = 0f;
                float x2 = 0f;
                float y2 = 0f;

                if (attributesAndStyles.containsKey(SvgTagConstants.X1)) {
                    x1 = getAttribute(attributesAndStyles, SvgTagConstants.X1);
                }

                if (attributesAndStyles.containsKey(SvgTagConstants.Y1)) {
                    y1 = getAttribute(attributesAndStyles, SvgTagConstants.Y1);
                }

                if (attributesAndStyles.containsKey(SvgTagConstants.X2)) {
                    x2 = getAttribute(attributesAndStyles, SvgTagConstants.X2);
                }

                if (attributesAndStyles.containsKey(SvgTagConstants.Y2)) {
                    y2 = getAttribute(attributesAndStyles, SvgTagConstants.Y2);
                }

                canvas.moveTo(x1, y1).lineTo(x2, y2);
            }
        } catch (NumberFormatException e) {
            throw new SvgProcessingException(SvgLogMessageConstant.FLOAT_PARSING_NAN, e);
        }
    }

    @Override
    protected boolean canElementFill() {
        return false;
    }

    private float getAttribute(Map<String, String> attributes, String key) {
        String value = attributes.get(key);
        if (value != null && !value.isEmpty()) {
            return CssUtils.parseAbsoluteLength(attributes.get(key));
        }
        return 0;
    }
}
