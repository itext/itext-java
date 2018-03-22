package com.itextpdf.svg.renderers.impl;


import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgAttributeConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

import java.util.HashMap;
import java.util.List;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;rect&gt; tag.
 */
public class RectangleSvgNodeRenderer extends AbstractSvgNodeRenderer {

    public RectangleSvgNodeRenderer(){
        attributesAndStyles = new HashMap<String, String>();
    }

    @Override
    protected void doDraw(SvgDrawContext context) {
        PdfCanvas cv = context.getCurrentCanvas();
        cv.writeLiteral("% rect\n");
        float x = CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.X_ATTRIBUTE));
        float y = CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.Y_ATTRIBUTE));
        float width = CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.WIDTH_ATTRIBUTE));
        float height = CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.HEIGHT_ATTRIBUTE));

        boolean rxPresent = false;
        boolean ryPresent = false;
        float rx = 0f;
        float ry = 0f;
        if (attributesAndStyles.containsKey(SvgAttributeConstants.RX_ATTRIBUTE)) {
            rx = CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.RX_ATTRIBUTE));
            rxPresent = true;
        }
        if (attributesAndStyles.containsKey(SvgAttributeConstants.RY_ATTRIBUTE)) {
            ry = CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.RY_ATTRIBUTE));
            ryPresent = true;
        }

        boolean singleValuePresent = (rxPresent && !ryPresent) || (!rxPresent && ryPresent);

        // these checks should happen in all cases
        rx = checkRadius(rx, width);
        ry = checkRadius(ry, height);
        if (!rxPresent && !ryPresent) {
            cv.rectangle(x, y, width, height);
        } else if (singleValuePresent) {
            cv.writeLiteral("% circle rounded rect\n");
            // only look for radius in case of circular rounding
            float radius = findCircularRadius(rx, ry, width, height);
            cv.roundRectangle(x, y, width, height, radius);
        } else {
            cv.writeLiteral("% ellipse rounded rect\n");
            // TODO (DEVSIX-1878): this should actually be refactored into PdfCanvas.roundRectangle()

            /*

			y+h    ->    ____________________________
						/                            \
					   /                              \
			y+h-ry -> /                                \
					  |                                |
					  |                                |
					  |                                |
					  |                                |
			y+ry   -> \                                /
					   \                              /
			y      ->   \____________________________/  
					  ^  ^                          ^  ^
					  x  x+rx                  x+w-rx  x+w

             */
            cv.moveTo(x + rx, y);
            cv.lineTo(x + width - rx, y);
            arc(x + width - 2 * rx, y, x + width, y + 2 * ry, -90, 90, cv);
            cv.lineTo(x + width, y + height - ry);
            arc(x + width, y + height - 2 * ry, x + width - 2 * rx, y + height, 0, 90, cv);
            cv.lineTo(x + rx, y + height);
            arc(x + 2 * rx, y + height, x, y + height - 2 * ry, 90, 90, cv);
            cv.lineTo(x, y + ry);
            arc(x, y + 2 * ry, x + 2 * rx, y, 180, 90, cv);
            cv.closePath();
        }
    }

    private void arc(final float x1, final float y1, final float x2, final float y2, final float startAng, final float extent, PdfCanvas cv) {
        List<double[]> ar = PdfCanvas.bezierArc(x1, y1, x2, y2, startAng, extent);
        if (ar.isEmpty()) {
            return;
        }
        double pt[];
        for (int k = 0; k < ar.size(); ++k) {
            pt = ar.get(k);
            cv.curveTo(pt[2], pt[3], pt[4], pt[5], pt[6], pt[7]);
        }
    }

    /**
     * a radius must be positive, and cannot be more than half the distance in
     * the dimension it is for.
     *
     * e.g. rx &lt;= width / 2
     */
    float checkRadius(float radius, float distance) {
        if (radius <= 0f) {
            return 0f;
        }
        if (radius > distance / 2f) {
            return distance / 2f;
        }
        return radius;
    }

    /**
     * In case of a circular radius, the calculation in {@link #checkRadius}
     * isn't enough: the radius cannot be more than half of the <b>smallest</b>
     * dimension.
     *
     * This method assumes that {@link #checkRadius} has already run, and it is
     * silently assumed (though not necessary for this method) that either
     * {@code rx} or {@code ry} is zero.
     */
    float findCircularRadius(float rx, float ry, float width, float height) {
        // see https://www.w3.org/TR/SVG/shapes.html#RectElementRYAttribute
        float maxRadius = Math.min(width, height) / 2f;
        float biggestRadius = Math.max(rx, ry);
        return Math.min(maxRadius, biggestRadius);
    }
}
