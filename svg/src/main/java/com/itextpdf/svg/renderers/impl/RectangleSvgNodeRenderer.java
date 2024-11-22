/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

import java.util.HashMap;
import java.util.List;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;rect&gt; tag.
 */
public class RectangleSvgNodeRenderer extends AbstractSvgNodeRenderer {

    private float x = 0f;
    private float y = 0f;
    private float width;
    private float height;
    private boolean rxPresent = false;
    private boolean ryPresent = false;
    private float rx = 0f;
    private float ry = 0f;

    /**
     * Constructs a RectangleSvgNodeRenderer.
     */
    public RectangleSvgNodeRenderer(){
        attributesAndStyles = new HashMap<>();
    }

    @Override
    protected void doDraw(SvgDrawContext context) {
        PdfCanvas cv = context.getCurrentCanvas();
        cv.writeLiteral("% rect\n");
        setParameters();
        boolean singleValuePresent = (rxPresent && !ryPresent) || (!rxPresent && ryPresent);

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

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        setParameters();
        return new Rectangle(this.x, this.y, this.width, this.height);
    }

    private void setParameters() {
        if(getAttribute(SvgConstants.Attributes.X)!=null) {
            x = CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.X));
        }
        if(getAttribute(SvgConstants.Attributes.Y)!=null) {
            y = CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.Y));
        }
        width = CssDimensionParsingUtils.parseAbsoluteLength(getAttributeOrDefault(SvgConstants.Attributes.WIDTH, "0"));
        height = CssDimensionParsingUtils.parseAbsoluteLength(getAttributeOrDefault(SvgConstants.Attributes.HEIGHT, "0"));

        if (attributesAndStyles.containsKey(SvgConstants.Attributes.RX)) {
            rx = checkRadius(CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.RX)), width);
            rxPresent = true;
        }
        if (attributesAndStyles.containsKey(SvgConstants.Attributes.RY)) {
            ry = checkRadius(CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.RY)), height);
            ryPresent = true;
        }
    }

    private void arc(final float x1, final float y1, final float x2, final float y2, final float startAng, final float extent, PdfCanvas cv) {
        List<double[]> ar = PdfCanvas.bezierArc(x1, y1, x2, y2, startAng, extent);
        if (!ar.isEmpty()) {
            double pt[];
            for (int k = 0; k < ar.size(); ++k) {
                pt = ar.get(k);
                cv.curveTo(pt[2], pt[3], pt[4], pt[5], pt[6], pt[7]);
            }
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

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        RectangleSvgNodeRenderer copy = new RectangleSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }

}
