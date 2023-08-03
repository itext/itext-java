/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.MarkerVertexType;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.IMarkerCapable;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCoordinateUtils;
import com.itextpdf.svg.utils.SvgCssUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;polyline&gt; tag.
 */
public class PolylineSvgNodeRenderer extends AbstractSvgNodeRenderer implements IMarkerCapable {

    /**
     * A List of {@link Point} objects representing the path to be drawn by the polyline tag
     */
    protected List<Point> points = new ArrayList<>();

    protected List<Point> getPoints() {
        return this.points;
    }

    /**
     * Parses a string of space separated x,y pairs into individual {@link Point} objects and appends them to{@link
     * PolylineSvgNodeRenderer#points}.
     * Throws an {@link SvgProcessingException} if pointsAttribute does not have a valid list of numerical x,y pairs.
     *
     * @param pointsAttribute A string of space separated x,y value pairs
     */
    protected void setPoints(String pointsAttribute) {
        if (pointsAttribute == null) {
            return;
        }

        List<String> points = SvgCssUtils.splitValueList(pointsAttribute);
        if (points.size() % 2 != 0) {
            throw new SvgProcessingException(SvgExceptionMessageConstant.POINTS_ATTRIBUTE_INVALID_LIST)
                    .setMessageParams(pointsAttribute);
        }

        this.points.clear();
        float x, y;
        for (int i = 0; i < points.size(); i = i + 2) {
            x = CssDimensionParsingUtils.parseAbsoluteLength(points.get(i));
            y = CssDimensionParsingUtils.parseAbsoluteLength(points.get(i + 1));
            this.points.add(new Point(x, y));
        }
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        setPoints(getAttribute(SvgConstants.Attributes.POINTS));
        if (points.size() > 1) {
            Point firstPoint = points.get(0);
            double minX = firstPoint.getX();
            double minY = firstPoint.getY();
            double maxX = minX;
            double maxY = minY;

            for (int i = 1; i < points.size(); ++i) {
                Point current = points.get(i);

                double currentX = current.getX();
                minX = Math.min(minX, currentX);
                maxX = Math.max(maxX, currentX);

                double currentY = current.getY();
                minY = Math.min(minY, currentY);
                maxY = Math.max(maxY, currentY);
            }

            double width = maxX - minX;
            double height = maxY - minY;

            return new Rectangle((float) minX, (float) minY, (float) width, (float) height);
        } else {
            return null;
        }
    }

    /**
     * Draws this element to a canvas-like object maintained in the context.
     *
     * @param context the object that knows the place to draw this element and maintains its state
     */
    @Override
    protected void doDraw(SvgDrawContext context) {
        String pointsAttribute = attributesAndStyles.containsKey(SvgConstants.Attributes.POINTS) ? attributesAndStyles
                .get(SvgConstants.Attributes.POINTS) : null;
        setPoints(pointsAttribute);

        PdfCanvas canvas = context.getCurrentCanvas();
        canvas.writeLiteral("% polyline\n");
        if (points.size() > 1) {
            Point currentPoint = points.get(0);
            canvas.moveTo(currentPoint.getX(), currentPoint.getY());
            for (int x = 1; x < points.size(); x++) {
                currentPoint = points.get(x);
                canvas.lineTo(currentPoint.getX(), currentPoint.getY());
            }
        }
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        PolylineSvgNodeRenderer copy = new PolylineSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }

    @Override
    public void drawMarker(SvgDrawContext context, final MarkerVertexType markerVertexType) {
        Point point = null;
        if (MarkerVertexType.MARKER_START.equals(markerVertexType)) {
            point = points.get(0);
        } else if (MarkerVertexType.MARKER_END.equals(markerVertexType)) {
            point = points.get(points.size() - 1);
        }
        if (point != null) {
            String moveX = SvgCssUtils.convertDoubleToString(CssUtils.convertPtsToPx(point.x));
            String moveY = SvgCssUtils.convertDoubleToString(CssUtils.convertPtsToPx(point.y));
            MarkerSvgNodeRenderer.drawMarker(context, moveX, moveY, markerVertexType, this);
        }
    }

    @Override
    public double getAutoOrientAngle(MarkerSvgNodeRenderer marker, boolean reverse) {
        if (points.size() > 1) {
            Vector v = new Vector(0, 0, 0);
            if (SvgConstants.Attributes.MARKER_END.equals(marker.attributesAndStyles.get(SvgConstants.Tags.MARKER))) {
                Point lastPoint = points.get(points.size() - 1);
                Point secondToLastPoint = points.get(points.size() - 2);
                v = new Vector((float) (lastPoint.getX() - secondToLastPoint.getX()),
                        (float) (lastPoint.getY() - secondToLastPoint.getY()), 0f);
            } else if (SvgConstants.Attributes.MARKER_START
                    .equals(marker.attributesAndStyles.get(SvgConstants.Tags.MARKER))) {
                Point firstPoint = points.get(0);
                Point secondPoint = points.get(1);
                v = new Vector((float) (secondPoint.getX() - firstPoint.getX()),
                        (float) (secondPoint.getY() - firstPoint.getY()), 0f);
            }
            Vector xAxis = new Vector(1, 0, 0);
            double rotAngle = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(xAxis, v);
            return v.get(1) >= 0 && !reverse ? rotAngle : rotAngle * -1f;
        }
        return 0;
    }
}
