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
package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;

import java.util.Arrays;
import java.util.List;

/**
 * Implements elliptical curveTo (A) segment of SVG's path element. Implemented in PDF as Bézier curves.
 * Edge cases &amp; value correction below always refer to https://www.w3.org/TR/SVG11/implnote.html#ArcImplementationNotes
 * For some calculations we need double precision floating point math, so we have forced all calculations to use double.
 * However, float comparison is used instead of double comparison, because close coordinates can be considered equal.
 */
public class EllipticalCurveTo extends AbstractPathShape {

    static final int ARGUMENT_SIZE = 7;

    private Point startPoint;

    /**
     * Creates an absolute Elliptical curveTo.
     */
    public EllipticalCurveTo() {
        this(false);
    }

    /**
     * Creates a Elliptical curveTo. Set argument to true to create a relative EllipticalCurveTo.
     *
     * @param relative whether this is a relative EllipticalCurveTo or not
     */
    public EllipticalCurveTo(boolean relative) {
        super(relative);
    }

    @Override
    public void setCoordinates(String[] inputCoordinates, Point previous) {
        startPoint = previous;
        if (inputCoordinates.length < ARGUMENT_SIZE) {
            throw new IllegalArgumentException(MessageFormatUtil.format(SvgLogMessageConstant.ARC_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0, Arrays.toString(inputCoordinates)));
        }
        coordinates = new String[ARGUMENT_SIZE];
        System.arraycopy(inputCoordinates, 0, coordinates, 0, ARGUMENT_SIZE);

        double[] initialPoint = new double[]{previous.getX(), previous.getY()};
        // ignore partial argument groups, which do not form a fixed set of 7 elements
        if (isRelative()) {
            String[] relativeEndPoint = {inputCoordinates[5], inputCoordinates[6]};
            String[] absoluteEndPoint = copier.makeCoordinatesAbsolute(relativeEndPoint, initialPoint);
            coordinates[5] = absoluteEndPoint[0];
            coordinates[6] = absoluteEndPoint[1];
        }
    }

    @Override
    public void draw(PdfCanvas canvas) {
        Point start = new Point(startPoint.x * .75, startPoint.y * .75); // pixels to points
        double rx = Math.abs(CssUtils.parseAbsoluteLength(coordinates[0]));
        double ry = Math.abs(CssUtils.parseAbsoluteLength(coordinates[1]));

        // φ is taken mod 360 degrees.
        double rotation = Double.parseDouble(coordinates[2]) % 360.0;
        // rotation argument is given in degrees, but we need radians for easier trigonometric calculations
        rotation = Math.toRadians(rotation);

        // binary flags (Value correction: any nonzero value for either of the flags fA or fS is taken to mean the value 1.)
        boolean largeArc = !CssUtils.compareFloats((float) CssUtils.parseFloat(coordinates[3]), 0);
        boolean sweep = !CssUtils.compareFloats((float) CssUtils.parseFloat(coordinates[4]), 0);

        Point end = new Point(CssUtils.parseAbsoluteLength(coordinates[5]), CssUtils.parseAbsoluteLength(coordinates[6]));

        if (CssUtils.compareFloats(start.x, end.x) && CssUtils.compareFloats(start.y, end.y)) {
            /* edge case: If the endpoints (x1, y1) and (x2, y2) are identical,
             * then this is equivalent to omitting the elliptical arc segment entirely.
             */
            return;
        }
        if (CssUtils.compareFloats(rx, 0) || CssUtils.compareFloats(ry, 0)) {
            /* edge case: If rx = 0 or ry = 0 then this arc is treated as a straight line segment (a "lineto")
             * joining the endpoints.
             */
            canvas.lineTo(end.x, end.y);
        } else {
            /* This is the first step of calculating a rotated elliptical path.
            We must simulate a transformation on the end-point in order to calculate appropriate EllipseArc angles;
            if we don't do this, then the EllipseArc class will calculate the correct bounding rectangle,
            but an incorrect starting angle and/or extent.
            */
            EllipseArc arc;
            if (CssUtils.compareFloats(rotation, 0)) {
                arc = EllipseArc.getEllipse(start, end, rx, ry, sweep, largeArc);
            } else {
                AffineTransform normalizer = AffineTransform.getRotateInstance(-rotation);
                normalizer.translate(-start.x, -start.y);
                Point newArcEnd = normalizer.transform(end, null);
                newArcEnd.translate(start.x, start.y);
                arc = EllipseArc.getEllipse(start, newArcEnd, rx, ry, sweep, largeArc);
            }
            Point[][] points = makePoints(PdfCanvas.bezierArc(arc.ll.x, arc.ll.y, arc.ur.x, arc.ur.y, arc.startAng, arc.extent));
            /** This is the second step of calculating a rotated elliptical path.
             We must rotate all points returned by {@link PdfCanvas#bezierArc} around the starting point of the arc.
             An added bit of complexity is that {@link PdfCanvas#bezierArc} will force a clockwise order of the
             control points, whereas we need counterclockwise if sweep is false (*).
             This is important for clipping and filling, when the non-zero winding rule is used.
             Thus, we must rotate around the actual first point, which is the last point of the last partial path if sweep is false
             (*) SVG's axis system uses top left as the origin, whereas PDF uses bottom left - so counterclockwise and clockwise are switched.
             */
            if (sweep) {
                points = rotate(points, rotation, points[0][0]);
                for (int i = 0; i < points.length; i++) {
                    drawCurve(canvas, points[i][1], points[i][2], points[i][3]);
                }
            } else {
                points = rotate(points, rotation, points[points.length - 1][3]);
                for (int i = points.length - 1; i >= 0; i--) {
                    drawCurve(canvas, points[i][2], points[i][1], points[i][0]);
                }
            }
        }
    }

    /**
     * This convenience method rotates a given set of points around a given point
     *
     * @param list     the input list
     * @param rotation the rotation angle, in radians
     * @param rotator  the point to rotate around
     * @return the list of rotated points
     */
    static Point[][] rotate(Point[][] list, double rotation, Point rotator) {
        if (!CssUtils.compareFloats(rotation, 0)) {
            Point[][] result = new Point[list.length][];
            AffineTransform transRotTrans = AffineTransform.getRotateInstance(rotation, rotator.x, rotator.y);

            for (int i = 0; i < list.length; i++) {
                Point[] input = list[i];
                Point[] row = new Point[input.length];

                for (int j = 0; j < input.length; j++) {
                    row[j] = transRotTrans.transform(input[j], null);
                }
                result[i] = row;
            }
            return result;
        }
        return list;
    }

    String[] getCoordinates() {
        return coordinates;
    }

    private static void drawCurve(PdfCanvas canvas, Point cp1, Point cp2, Point end) {
        canvas.curveTo(cp1.x, cp1.y, cp2.x, cp2.y, end.x, end.y);
    }

    private Point[][] makePoints(List<double[]> input) {
        Point[][] result = new Point[input.size()][];
        for (int i = 0; i < input.size(); i++) {
            result[i] = new Point[input.get(i).length / 2];
            for (int j = 0; j < input.get(i).length; j += 2) {
                result[i][j / 2] = new Point(input.get(i)[j], input.get(i)[j + 1]);
            }
        }
        return result;
    }


    /**
     * Converts between two types of definitions of an arc:
     * The input is an arc defined by two points and the two semi-axes of the ellipse.
     * The output is an arc defined by a bounding rectangle, the starting angle,
     * and the angular extent of the ellipse that is to be drawn.
     * The output is an intermediate step to calculating the Bézier curve(s) that approximate(s) the elliptical arc,
     * which happens in {@link PdfCanvas}.
     */
    static class EllipseArc {
        final Point ll, ur;
        final double startAng, extent;

        EllipseArc(Point center, final double a, final double b, final double startAng, final double extent) {
            ll = new Point(center.x - a, center.y - b);
            ur = new Point(center.x + a, center.y + b);
            this.startAng = startAng;
            this.extent = extent;
        }

        static EllipseArc getEllipse(Point start, Point end, double a, double b, boolean sweep, boolean largeArc) {
            double r1 = (start.x - end.x) / (-2.0 * a);
            double r2 = (start.y - end.y) / (2.0 * b);

            double factor = Math.sqrt(r1 * r1 + r2 * r2);
            if (factor > 1) {
                /* If rx, ry and φ are such that there is no solution (basically, the ellipse is not big enough
                 * to reach from (x1, y1) to (x2, y2)) then the ellipse's semi-axes are scaled up uniformly
                 * until there is exactly one solution (until the ellipse is just big enough).
                 */
                return getEllipse(start, end, a * factor, b * factor, sweep, largeArc);
            }

            double between1 = Math.atan(r1 / r2);
            double between2 = Math.asin(factor);

            EllipseArc result = calculatePossibleMiddle(start, end, a, b, between1 + between2, sweep, largeArc);
            if (result != null) {
                return result;
            }
            result = calculatePossibleMiddle(start, end, a, b, Math.PI + between1 - between2, sweep, largeArc);
            if (result != null) {
                return result;
            }
            result = calculatePossibleMiddle(start, end, a, b, Math.PI + between1 + between2, sweep, largeArc);
            if (result != null) {
                return result;
            }
            result = calculatePossibleMiddle(start, end, a, b, between1 - between2, sweep, largeArc);
            if (result != null) {
                return result;
            }
            throw new SvgProcessingException(SvgExceptionMessageConstant.COULD_NOT_DETERMINE_MIDDLE_POINT_OF_ELLIPTICAL_ARC);
        }

        static EllipseArc calculatePossibleMiddle(Point start, Point end, double a, double b, double startToCenterAngle, boolean sweep, boolean largeArc) {

            double x0 = start.x - a * Math.cos(startToCenterAngle);
            double y0 = start.y - b * Math.sin(startToCenterAngle);
            Point center = new Point(x0, y0);

            double check = Math.pow(((end.x - center.x) / a), 2) + Math.pow(((end.y - center.y) / b), 2);

            /* If center is an actual candidate for a middle point, then the value of check will be very close to 1.0.
             * Otherwise it is always larger than 1.
             * Due to floating point math, we need to introduce an epsilon value.
             */
            if (CssUtils.compareFloats(check, 1.0)) {

                double theta1 = calculateAngle(start, center, a, b);
                double theta2 = calculateAngle(end, center, a, b);
                double startAngl = 0;
                double extent = 0;

                // round the difference, to catch edge cases with floating point math around the value 180
                long deltaTheta = (long) Math.abs(Math.round(theta2 - theta1));
                //both points are on the ellipse, but is this the middle, looked for?
                if (largeArc) { //turn more than 180 degrees
                    if (sweep) {

                        if ((theta2 > theta1) && (deltaTheta >= 180)) {
                            startAngl = theta1;
                            extent = theta2 - theta1;
                        }
                        if ((theta1 > theta2) && (deltaTheta <= 180)) {
                            startAngl = theta1;
                            extent = 360 - theta1 + theta2;
                        }
                    } else {
                        if ((theta2 > theta1) && (deltaTheta <= 180)) {
                            startAngl = theta2;
                            extent = 360 - theta2 + theta1; //or the same extent but negative and start at p1
                        }
                        if ((theta1 > theta2) && (deltaTheta >= 180)) {
                            startAngl = theta2;
                            extent = theta1 - theta2;
                        }
                    }
                } else {
                    if (sweep) {
                        if ((theta2 > theta1) && (deltaTheta <= 180)) {
                            startAngl = theta1;
                            extent = theta2 - theta1;
                        }
                        if ((theta1 > theta2) && (deltaTheta >= 180)) {
                            startAngl = theta1;
                            extent = 360 - theta1 + theta2;
                        }

                    } else {
                        if ((theta2 > theta1) && (deltaTheta >= 180)) {
                            startAngl = theta2;
                            extent = 360 - theta2 + theta1; //or the same extent but negative and start at p1
                        }
                        if ((theta1 > theta2) && (deltaTheta <= 180)) {
                            startAngl = theta2;
                            extent = theta1 - theta2;
                        }
                    }
                }

                if (startAngl >= 0 && extent > 0) {
                    return new EllipseArc(center, a, b, startAngl, extent);
                }
            }
            return null;
        }

        static double calculateAngle(Point pt, Point center, double a, double b) {
            double result = Math.pow(((pt.x - center.x) / a), 2.0) + Math.pow(((pt.y - center.y) / b), 2.0);

            double cos = (pt.x - center.x) / a;
            double sin = (pt.y - center.y) / b;
            // catch very small floating point errors and keep cos between [-1, 1], so we can calculate the arc cosine
            cos = Math.max(Math.min(cos, 1.0), -1.0);

            if ((cos >= 0 && sin >= 0) || (cos < 0 && sin >= 0)) {
                result = toDegrees(Math.acos(cos));
            }
            if ((cos >= 0 && sin < 0) || (cos < 0 && sin < 0)) {
                result = 360 - toDegrees(Math.acos(cos));
            }
            return result;
        }

        static double toDegrees(double radians) {
            return radians * 180.0 / Math.PI;
        }

    }
}
