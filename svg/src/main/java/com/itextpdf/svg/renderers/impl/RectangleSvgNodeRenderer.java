/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.renderers.impl;


import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

import java.util.HashMap;
import java.util.List;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;rect&gt; tag.
 */
public class RectangleSvgNodeRenderer extends AbstractSvgNodeRenderer {

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
        float x=0.0f,y=0.0f;
        if(getAttribute(SvgConstants.Attributes.X)!=null) {
             x = CssUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.X));
        }
        if(getAttribute(SvgConstants.Attributes.Y)!=null) {
            y = CssUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.Y));
        }
        float width = CssUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.WIDTH));
        float height = CssUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.HEIGHT));

        boolean rxPresent = false;
        boolean ryPresent = false;
        float rx = 0f;
        float ry = 0f;
        if (attributesAndStyles.containsKey(SvgConstants.Attributes.RX)) {
            rx = CssUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.RX));
            rxPresent = true;
        }
        if (attributesAndStyles.containsKey(SvgConstants.Attributes.RY)) {
            ry = CssUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.RY));
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
