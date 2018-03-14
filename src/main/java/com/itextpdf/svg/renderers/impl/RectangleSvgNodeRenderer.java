package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;rect&gt; tag.
 */
public class RectangleSvgNodeRenderer extends AbstractSvgNodeRenderer {

    @Override
    protected void doDraw(SvgDrawContext context) {
        PdfCanvas canvas = context.getCurrentCanvas();

        float x = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgTagConstants.X));
        float y = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgTagConstants.Y));
        float width = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgTagConstants.WIDTH));
        float height = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgTagConstants.HEIGHT));

        Rectangle rect = new Rectangle(x, y, width, height);
        canvas.rectangle(rect);
    }
    
}
