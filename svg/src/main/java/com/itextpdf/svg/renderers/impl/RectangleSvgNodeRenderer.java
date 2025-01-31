/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

import java.util.HashMap;

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
        setParameters(context);
        boolean singleValuePresent = (rxPresent && !ryPresent) || (!rxPresent && ryPresent);

        AffineTransform transform = applyNonScalingStrokeTransform(context);
        if (!rxPresent && !ryPresent) {
            Point[] points = new Rectangle(x, y, width, height).toPointsArray();
            if (transform != null) {
                transform.transform(points, 0, points, 0, points.length);

                if (Math.abs(transform.getShearX()) > 0 || Math.abs(transform.getShearY()) > 0) {
                    int i = 0;
                    cv.moveTo(points[i].getX(), points[i++].getY())
                            .lineTo(points[i].getX(), points[i++].getY())
                            .lineTo(points[i].getX(), points[i++].getY())
                            .lineTo(points[i].getX(), points[i].getY())
                            .closePath();
                    return;
                }
            }
            cv.rectangle(points[0].getX(),
                    points[0].getY(),
                    points[1].getX() - points[0].getX(),
                    points[2].getY() - points[0].getY());
        } else if (singleValuePresent) {
            cv.writeLiteral("% circle rounded rect\n");
            // Only look for radius in case of circular rounding.
            float radius = findCircularRadius(rx, ry, width, height);
            cv.roundRectangle(x, y, width, height, radius, radius, transform);
        } else {
            cv.writeLiteral("% ellipse rounded rect\n");
            cv.roundRectangle(x, y, width, height, rx, ry, transform);
        }
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        setParameters(context);
        return new Rectangle(this.x, this.y, this.width, this.height);
    }

    private void setParameters(SvgDrawContext context) {
        if(getAttribute(SvgConstants.Attributes.X)!=null) {
            x = parseHorizontalLength(getAttribute(SvgConstants.Attributes.X), context);
        }
        if(getAttribute(SvgConstants.Attributes.Y)!=null) {
            y = parseVerticalLength(getAttribute(SvgConstants.Attributes.Y), context);
        }
        width = parseHorizontalLength(getAttribute(SvgConstants.Attributes.WIDTH), context);
        height = parseVerticalLength(getAttribute(SvgConstants.Attributes.HEIGHT), context);

        if (attributesAndStyles.containsKey(SvgConstants.Attributes.RX)) {
            float rawRadius =parseHorizontalLength(getAttribute(SvgConstants.Attributes.RX), context);
            rx = checkRadius(rawRadius, width);
            rxPresent = rawRadius >= 0.0f;
        }
        if (attributesAndStyles.containsKey(SvgConstants.Attributes.RY)) {
            float rawRadius = parseVerticalLength(getAttribute(SvgConstants.Attributes.RY), context);
            ry = checkRadius(rawRadius, height);
            ryPresent = rawRadius >= 0.0f;
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
