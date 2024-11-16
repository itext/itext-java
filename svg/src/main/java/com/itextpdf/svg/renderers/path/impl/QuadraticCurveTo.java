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
package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implements quadratic Bezier curveTo(Q) attribute of SVG's path element
 */
public class QuadraticCurveTo extends AbstractPathShape implements IControlPointCurve {

    static final int ARGUMENT_SIZE = 4;

    public QuadraticCurveTo() {
        this(false);
    }

    public QuadraticCurveTo(boolean relative) {
        this(relative, new DefaultOperatorConverter());
    }

    public QuadraticCurveTo(boolean relative, IOperatorConverter copier) {
        super(relative, copier);
    }

    /**
     * Draws a quadratic Bezier curve from the current point to (x,y) using (x1,y1) as the control point
     */
    @Override
    public void draw() {
        float x1 = parseHorizontalLength(coordinates[0]);
        float y1 = parseVerticalLength(coordinates[1]);
        float x = parseHorizontalLength(coordinates[2]);
        float y = parseVerticalLength(coordinates[3]);
        context.getCurrentCanvas().curveTo(x1, y1, x, y);
    }

    @Override
    public void setCoordinates(String[] inputCoordinates, Point startPoint) {
        // startPoint will be used when relative quadratic curve is implemented
        if (inputCoordinates.length < ARGUMENT_SIZE) {
            throw new IllegalArgumentException(MessageFormatUtil.format(SvgExceptionMessageConstant.QUADRATIC_CURVE_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0, Arrays.toString(coordinates)));
        }
        coordinates = new String[ARGUMENT_SIZE];
        System.arraycopy(inputCoordinates, 0, coordinates, 0, ARGUMENT_SIZE);
        double[] initialPoint = new double[] {startPoint.getX(), startPoint.getY()};
        if (isRelative()) {
            coordinates = copier.makeCoordinatesAbsolute(coordinates, initialPoint);
        }
    }

    @Override
    public Point getLastControlPoint() {
        return createPoint(coordinates[0], coordinates[1]);
    }

    @Override
    public Rectangle getPathShapeRectangle(Point lastPoint) {
        Point controlPoint = getLastControlPoint();
        Point endingPoint = getEndingPoint();
        double[] points = getBezierMinMaxPoints(lastPoint.getX(), lastPoint.getY(),
                controlPoint.getX(), controlPoint.getY(),
                endingPoint.getX(), endingPoint.getY());
        return new Rectangle((float) CssUtils.convertPxToPts(points[0]),
                (float) CssUtils.convertPxToPts(points[1]),
                (float) CssUtils.convertPxToPts(points[2] - points[0]),
                (float) CssUtils.convertPxToPts(points[3] - points[1]));
    }

    /**
     * The algorithm is similar to cubic curve in the method CurveTo#getBezierMinMaxPoints,
     * but the initial function is f(t) = (1 - t)^2*P0 + 2*(1-t)*t*P1 + t^2*P2, 0 <= t <= 1
     *
     * @param x0 x coordinate of the starting point
     * @param y0 y coordinate of the starting point
     * @param x1 x coordinate of the control point
     * @param y1 y coordinate of the control point
     * @param x2 x coordinate of the ending point
     * @param y2 y coordinate of the ending point
     * @return array of {xMin, yMin, xMax, yMax} values
     */
    private static double[] getBezierMinMaxPoints(double x0, double y0, double x1, double y1, double x2, double y2) {
        double xMin = Math.min(x0, x2);
        double yMin = Math.min(y0, y2);
        double xMax = Math.max(x0, x2);
        double yMax = Math.max(y0, y2);
        double[] extremeTValues = getExtremeTValues(x0, y0, x1, y1, x2, y2);
        for (double t : extremeTValues) {
            double xValue = calculateExtremeCoordinate(t, x0, x1, x2);
            double yValue = calculateExtremeCoordinate(t, y0, y1, y2);
            xMin = Math.min(xValue, xMin);
            yMin = Math.min(yValue, yMin);
            xMax = Math.max(xValue, xMax);
            yMax = Math.max(yValue, yMax);
        }
        return new double[] {xMin, yMin, xMax, yMax};
    }

    /**
     * Calculate values of t at which the function reaches its extreme points. To do this, we get the derivative of the
     * function and equate it to 0:
     * f'(t) = 2a*t + b. t can only be in the range [0, 1] and it discarded otherwise.
     *
     * @param x0 x coordinate of the starting point
     * @param y0 y coordinate of the starting point
     * @param x1 x coordinate of the control point
     * @param y1 y coordinate of the control point
     * @param x2 x coordinate of the ending point
     * @param y2 y coordinate of the ending point
     * @return array of theta values corresponding to extreme points
     */
    private static double[] getExtremeTValues(double x0, double y0, double x1, double y1, double x2, double y2) {
        List<Double> tValuesList = new ArrayList<>();
        addTValueToList(getTValue(x0, x1, x2), tValuesList);
        addTValueToList(getTValue(y0, y1, y2), tValuesList);
        double[] tValuesArray = new double[tValuesList.size()];
        for (int i = 0; i < tValuesList.size(); i++) {
            tValuesArray[i] = tValuesList.get(i);
        }
        return tValuesArray;
    }

    /**
     * Check that t is in the range [0, 1] and add it to list
     * @param t value of t
     * @param tValuesList list storing t values
     */
    private static void addTValueToList(double t,  List<Double> tValuesList) {
        if (0 <= t && t <= 1) {
            tValuesList.add(t);
        }
    }

    private static double getTValue(double p0, double p1, double p2) {
        double b = 2 * p1 - 2 * p0;
        double a = p0 - 2 * p1 + p2;
        return -b / (2 * a);
    }

    private static double calculateExtremeCoordinate(double t, double p0, double p1, double p2) {
        double minusT = 1 - t;
        return  (minusT * minusT * p0) + (2 * minusT * t * p1) + (t * t * p2);
    }
}
