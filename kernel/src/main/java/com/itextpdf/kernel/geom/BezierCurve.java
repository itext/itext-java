/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.kernel.geom;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Bezier curve.
 */
public class BezierCurve implements IShape {

    private static final long serialVersionUID = -2158496565016776969L;
    /**
     * If the distance between a point and a line is less than
     * this constant, then we consider the point lies on the line.
     */
    public static double curveCollinearityEpsilon = 1.0e-30;

    /**
     * In the case when neither the line ((x1, y1), (x4, y4)) passes
     * through both (x2, y2) and (x3, y3) nor (x1, y1) = (x4, y4) we
     * use the square of the sum of the distances mentioned below in
     * compare to this field as the criterion of good approximation.
     *     1. The distance between the line and (x2, y2)
     *     2. The distance between the line and (x3, y3)
     */
    public static double distanceToleranceSquare = 0.025D;

    /**
     * The Manhattan distance is used in the case when either the line
     * ((x1, y1), (x4, y4)) passes through both (x2, y2) and (x3, y3)
     * or (x1, y1) = (x4, y4). The essential observation is that when
     * the curve is a uniform speed straight line from end to end, the
     * control points are evenly spaced from beginning to end. Our measure
     * of how far we deviate from that ideal uses distance of the middle
     * controls: point 2 should be halfway between points 1 and 3; point 3
     * should be halfway between points 2 and 4.
     */
    public static double distanceToleranceManhattan = 0.4D;

    private final List<Point> controlPoints;

    /**
     * Constructs new bezier curve.
     * @param controlPoints Curve's control points.
     */
    public BezierCurve(List<Point> controlPoints) {
        this.controlPoints = new ArrayList<>(controlPoints);
    }

    /**
     * {@inheritDoc}
     */
    public List<Point> getBasePoints() {
        return controlPoints;
    }

    /**
     * You can adjust precision of the approximation by varying the following
     * parameters: {@link #curveCollinearityEpsilon}, {@link #distanceToleranceSquare},
     * {@link #distanceToleranceManhattan}
     *
     * @return {@link java.util.List} containing points of piecewise linear approximation
     *         for this bezier curve.
     */
    public List<Point> getPiecewiseLinearApproximation() {
        List<Point> points = new ArrayList<>();
        points.add(controlPoints.get(0));

        recursiveApproximation(controlPoints.get(0).getX(), controlPoints.get(0).getY(),
                controlPoints.get(1).getX(), controlPoints.get(1).getY(),
                controlPoints.get(2).getX(), controlPoints.get(2).getY(),
                controlPoints.get(3).getX(), controlPoints.get(3).getY(), points);

        points.add(controlPoints.get(controlPoints.size() - 1));
        return points;
    }

    // Based on the De Casteljau's algorithm
    private void recursiveApproximation(double x1, double y1, double x2, double y2,
                                        double x3, double y3, double x4, double y4, List<Point> points) {
        // Subdivision using the De Casteljau's algorithm (t = 0.5)
        double x12 = (x1 + x2) / 2;
        double y12 = (y1 + y2) / 2;
        double x23 = (x2 + x3) / 2;
        double y23 = (y2 + y3) / 2;
        double x34 = (x3 + x4) / 2;
        double y34 = (y3 + y4) / 2;
        double x123 = (x12 + x23) / 2;
        double y123 = (y12 + y23) / 2;
        double x234 = (x23 + x34) / 2;
        double y234 = (y23 + y34) / 2;
        double x1234 = (x123 + x234) / 2;
        double y1234 = (y123 + y234) / 2;

        double dx = x4 - x1;
        double dy = y4 - y1;

        // Constructs the line passing through (x1, y1) and (x4, y4)
        // |Ax2 + By2 + C|, where Ax+By+C is the equation for the line mentioned above
        double d2 = Math.abs(((x2 - x4) * dy - (y2 - y4) * dx));

        // |Ax3 + Bx3 + C|
        double d3 = Math.abs(((x3 - x4) * dy - (y3 - y4) * dx));

        // True if neither the line passes through both (x2, y2) and (x3, y3)
        // nor (x1, y1) = (x4, y4)
        if (d2 > curveCollinearityEpsilon || d3 > curveCollinearityEpsilon) {
            // True if the square of the distance between (x2, y2) and the line plus
            // the distance between (x3, y3) and the line is lower than the tolerance square
            if ((d2 + d3) * (d2 + d3) <= distanceToleranceSquare * (dx * dx + dy * dy)) {
                points.add(new Point(x1234, y1234));
                return;
            }

        } else {
            if ((Math.abs(x1 + x3 - x2 - x2) + Math.abs(y1 + y3 - y2 - y2) +
                    Math.abs(x2 + x4 - x3 - x3) + Math.abs(y2 + y4 - y3 - y3)) <= distanceToleranceManhattan) {
                points.add(new Point(x1234, y1234));
                return;
            }
        }

        recursiveApproximation(x1, y1, x12, y12, x123, y123, x1234, y1234, points);
        recursiveApproximation(x1234, y1234, x234, y234, x34, y34, x4, y4, points);
    }
}
