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
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
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

    private static final double EPS = 0.00001;

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
            throw new IllegalArgumentException(
                    MessageFormatUtil.format(
                            SvgExceptionMessageConstant.ARC_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0,
                            Arrays.toString(inputCoordinates)));
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
    public void draw() {
        Point start = new Point(startPoint.getX() * .75, startPoint.getY() * .75); // pixels to points
        double rx = Math.abs(parseHorizontalLength(coordinates[0]));
        double ry = Math.abs(parseVerticalLength(coordinates[1]));

        // φ is taken mod 360 degrees.
        double rotation = Double.parseDouble(coordinates[2]) % 360.0;
        // rotation argument is given in degrees, but we need radians for easier trigonometric calculations
        rotation = Math.toRadians(rotation);

        // binary flags (Value correction: any nonzero value for either of the flags fA or fS is taken to mean the value 1.)
        boolean largeArc = !CssUtils.compareFloats((float) CssDimensionParsingUtils.parseFloat(coordinates[3]), 0);
        boolean sweep = !CssUtils.compareFloats((float) CssDimensionParsingUtils.parseFloat(coordinates[4]), 0);

        Point end = new Point(parseHorizontalLength(coordinates[5]), parseVerticalLength(coordinates[6]));

        if (CssUtils.compareFloats(start.getX(), end.getX()) && CssUtils.compareFloats(start.getY(), end.getY())) {
            /* edge case: If the endpoints (x1, y1) and (x2, y2) are identical,
             * then this is equivalent to omitting the elliptical arc segment entirely.
             */
            return;
        }
        if (CssUtils.compareFloats(rx, 0) || CssUtils.compareFloats(ry, 0)) {
            /* edge case: If rx = 0 or ry = 0 then this arc is treated as a straight line segment (a "lineto")
             * joining the endpoints.
             */
            context.getCurrentCanvas().lineTo(end.getX(), end.getY());
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
                normalizer.translate(-start.getX(), -start.getY());
                Point newArcEnd = normalizer.transform(end, null);
                newArcEnd.move(start.getX(), start.getY());
                arc = EllipseArc.getEllipse(start, newArcEnd, rx, ry, sweep, largeArc);
            }
            Point[][] points = makePoints(PdfCanvas.bezierArc(arc.ll.getX(), arc.ll.getY(), arc.ur.getX(), arc.ur.getY(), arc.startAng, arc.extent));
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
                    drawCurve(context.getCurrentCanvas(), points[i][1], points[i][2], points[i][3]);
                }
            } else {
                points = rotate(points, rotation, points[points.length - 1][3]);
                for (int i = points.length - 1; i >= 0; i--) {
                    drawCurve(context.getCurrentCanvas(), points[i][2], points[i][1], points[i][0]);
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
            AffineTransform transRotTrans = AffineTransform.getRotateInstance(rotation, rotator.getX(), rotator.getY());

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
        canvas.curveTo(cp1.getX(), cp1.getY(), cp2.getX(), cp2.getY(), end.getX(), end.getY());
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
            ll = new Point(center.getX() - a, center.getY() - b);
            ur = new Point(center.getX() + a, center.getY() + b);
            this.startAng = startAng;
            this.extent = extent;
        }

        static EllipseArc getEllipse(Point start, Point end, double a, double b, boolean sweep, boolean largeArc) {
            double r1 = (start.getX() - end.getX()) / (-2.0 * a);
            double r2 = (start.getY() - end.getY()) / (2.0 * b);

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

            double x0 = start.getX() - a * Math.cos(startToCenterAngle);
            double y0 = start.getY() - b * Math.sin(startToCenterAngle);
            Point center = new Point(x0, y0);

            double check = Math.pow(((end.getX() - center.getX()) / a), 2) + Math.pow(((end.getY() - center.getY()) / b), 2);

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
            double result = Math.pow(((pt.getX() - center.getX()) / a), 2.0) + Math.pow(((pt.getY() - center.getY()) / b), 2.0);

            double cos = (pt.getX() - center.getX()) / a;
            double sin = (pt.getY() - center.getY()) / b;
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

    @Override
    public Rectangle getPathShapeRectangle(Point lastPoint) {
        double[] points = getEllipticalArcMinMaxPoints(lastPoint.getX(), lastPoint.getY(),
                getCoordinate(0), getCoordinate(1), getCoordinate(2),
                getCoordinate(3) != 0, getCoordinate(4) != 0,
                getCoordinate(5), getCoordinate(6));
        return new Rectangle((float) CssUtils.convertPxToPts(points[0]),
                (float) CssUtils.convertPxToPts(points[1]),
                (float) CssUtils.convertPxToPts(points[2] - points[0]),
                (float) CssUtils.convertPxToPts(points[3] - points[1]));
    }

    private double getCoordinate(int index) {
        // casting to double fot porting compatibility
        return (double) CssDimensionParsingUtils.parseDouble(coordinates[index]);
    }

    /**
     * Algorithm to find elliptical arc bounding box:
     * 1. Compute extremes using parametric description of the whole ellipse
     * We use parametric description of ellipse:
     * x(theta) = cx + rx*cos(theta)*cos(phi) - ry*sin(theta)*sin(phi)
     * y(theta) = cy + rx*cos(theta)*sin(phi) + ry*sin(theta)*cos(phi)
     * After obtaining the derivative and equating it to zero, we get two solutions for x:
     * theta = -atan(ry*tan(phi)/rx) and theta = M_PI -atan(ry*tan(phi)/rx)
     * and two solutions for y:
     * theta = atan(ry/(tan(phi)*rx)) and theta = M_PI + atan(ry/(tan(phi)*rx))
     * Then to get theta values we need to know cx and cy - the coordinates of the center of the ellipse.
     * 2. Compute the center of the ellipse
     * Method {@link EllipticalCurveTo#getEllipseCenterCoordinates}
     * 3. Determine the bounding box of the whole ellipse
     * When we know cx and cy values we can get the bounding box of whole ellipse. That done in the method
     * {@link EllipticalCurveTo#getEllipseCenterCoordinates}.
     * 4. Find tightest possible bounding box
     * Check that given points is on the arc using polar coordinates of points. Method {@link
     * EllipticalCurveTo#isPointOnTheArc}.
     *
     * @param x1       x coordinate of the starting point
     * @param y1       y coordinate of the starting point
     * @param rx       x radius
     * @param ry       y radius
     * @param phi      x-axis rotation
     * @param largeArc large arc flag. If this is true, then one of the two larger arc sweeps will be chosen (greater than or equal to 180 degrees)
     * @param sweep    sweep flag. If sweep flag is true, then the arc will be drawn in a "positive-angle" direction and if false - in a "negative-angle" direction
     * @param x2       x coordinate of ending point
     * @param y2       y coordinate of ending point
     * @return array of {xMin, yMin, xMax, yMax} values
     */
    private double[] getEllipticalArcMinMaxPoints(double x1, double y1,
            double rx, double ry, double phi, boolean largeArc, boolean sweep, double x2, double y2) {
        phi = Math.toRadians(phi);
        rx = Math.abs(rx);
        ry = Math.abs(ry);

        if (rx == 0.0 || ry == 0.0) {
            return new double[] {Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2)};
        }

        double[] centerCoordinatesAndRxRy = getEllipseCenterCoordinates(x1, y1, rx, ry, phi, largeArc, sweep, x2, y2);
        // the case when radicant is less than 0 and cannot be recalculated. See getEllipseCenterCoordinates() for more info.
        if (centerCoordinatesAndRxRy == null) {
            return new double[] {Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2)};
        }
        double cx = centerCoordinatesAndRxRy[0];
        double cy = centerCoordinatesAndRxRy[1];
        // rx and ry values returned cause they can be changed if radicant < 0
        rx = centerCoordinatesAndRxRy[2];
        ry = centerCoordinatesAndRxRy[3];

        double[][] extremeCoordinatesAndThetas = getExtremeCoordinatesAndAngles(rx, ry, phi, cx, cy);
        double[] extremeCoordinates = extremeCoordinatesAndThetas[0];
        double[] angles = extremeCoordinatesAndThetas[1];
        double xMin = extremeCoordinates[0];
        double yMin = extremeCoordinates[1];
        double xMax = extremeCoordinates[2];
        double yMax = extremeCoordinates[3];
        double xMinAngle = angles[0];
        double yMinAngle = angles[1];
        double xMaxAngle = angles[2];
        double yMaxAngle = angles[3];

        // angles of starting and ending points calculated regarding to centre of ellipse
        double angle1 = getAngleBetweenVectors(x1 - cx, y1 - cy);
        double angle2 = getAngleBetweenVectors(x2 - cx, y2 - cy);

        // In case the sweep flag is false,  the angles are decreasing when the ellipse is drawn.
        // So we can just swap them to choose another arc.
        if (!sweep) {
            double temp = angle1;
            angle1 = angle2;
            angle2 = temp;
        }

        // We have difficulty with the fact that the angle of 0 radians is the same as the one of 2*M_PI radians.
        // This passage through the 2*M_PI / 0 border is not very easy to handle directly.
        // That is why we swap the points in case where angle1 > angle2 and will not look in this case for absence of the
        // extreme points on the arc, but for their presence on the complement arc that would close the ellipse.
        boolean otherArc = angle1 > angle2;
        if (otherArc) {
            double temp = angle1;
            angle1 = angle2;
            angle2 = temp;
        }

        // If, for example, xMin does not lie on the arc, the new xMin will be the minimum
        // of the x coordinates of the starting and ending points. The same is valid for all other cases.
        if (!isPointOnTheArc(xMinAngle, angle1, angle2, otherArc)) {
            xMin = Math.min(x1, x2);
        }
        if (!isPointOnTheArc(xMaxAngle, angle1, angle2, otherArc)) {
            xMax = Math.max(x1, x2);
        }
        if (!isPointOnTheArc(yMinAngle, angle1, angle2, otherArc)) {
            yMin = Math.min(y1, y2);
        }
        if (!isPointOnTheArc(yMaxAngle, angle1, angle2, otherArc)) {
            yMax = Math.max(y1, y2);
        }
        return new double[] {xMin, yMin, xMax, yMax};
    }

    /**
     * Calculate the center coordinates of the whole ellipse.
     * Also return rx, ry values since they can be changed in this method.
     * Algorithm for calculation centre coordinates: https://www.w3.org/TR/SVG/implnote.html#ArcConversionEndpointToCenter
     *
     * @param x1       x coordinate of the starting point
     * @param y1       y coordinate of the starting point
     * @param rx       x radius
     * @param ry       y radius
     * @param phi      x-axis rotation
     * @param largeArc large arc flag
     * @param sweep    sweep flag
     * @param x2       x coordinate of ending point
     * @param y2       y coordinate of ending point
     * @return the array of {cx, cy, rx, ry} values
     */
    private double[] getEllipseCenterCoordinates(double x1, double y1,
            double rx, double ry, double phi, boolean largeArc, boolean sweep, double x2, double y2) {
        double x1Prime = Math.cos(phi) * (x1 - x2) / 2 + Math.sin(phi) * (y1 - y2) / 2;
        double y1Prime = -Math.sin(phi) * (x1 - x2) / 2 + Math.cos(phi) * (y1 - y2) / 2;
        double radicant = (rx * rx * ry * ry - rx * rx * y1Prime * y1Prime - ry * ry * x1Prime * x1Prime);
        radicant /= (rx * rx * y1Prime * y1Prime + ry * ry * x1Prime * x1Prime);

        double cxPrime = 0.0;
        double cyPrime = 0.0;
        if (radicant < 0.0) {
            double ratio = rx / ry;
            radicant = y1Prime * y1Prime + x1Prime * x1Prime / (ratio * ratio);
            if (radicant < 0.0) {
                return null;
            }
            ry = Math.sqrt(radicant);
            rx = ratio * ry;
        } else {
            double factor = (largeArc == sweep ? -1.0 : 1.0) * Math.sqrt(radicant);
            cxPrime = factor * rx * y1Prime / ry;
            cyPrime = -factor * ry * x1Prime / rx;
        }

        double cx = cxPrime * Math.cos(phi) - cyPrime * Math.sin(phi) + (x1 + x2) / 2;
        double cy = cxPrime * Math.sin(phi) + cyPrime * Math.cos(phi) + (y1 + y2) / 2;
        // rx and ry values returned cause they can be changed if radicant < 0
        return new double[] {cx, cy, rx, ry};
    }

    /**
     * Calculate extremes of the ellipse function and corresponding angles.
     * Angles are calculated relative to the center of the ellipse.
     *
     * @param rx  x radius
     * @param ry  y radius
     * @param phi x-axis rotation
     * @param cx  x coordinate of ellipse center
     * @param cy  y coordinate of ellipse center
     * @return array of extreme coordinate and array of angles corresponding to these coordinates.
     */
    private double[][] getExtremeCoordinatesAndAngles(double rx, double ry, double phi, double cx, double cy) {
        double xMin, yMin, xMax, yMax;
        double xMinAngle, yMinAngle, xMaxAngle, yMaxAngle;
        if (anglesAreEquals(phi, 0) || anglesAreEquals(phi, Math.PI)) {
            xMin = cx - rx;
            xMinAngle = getAngleBetweenVectors(-rx, 0);
            xMax = cx + rx;
            xMaxAngle = getAngleBetweenVectors(rx, 0);
            yMin = cy - ry;
            yMinAngle = getAngleBetweenVectors(0, -ry);
            yMax = cy + ry;
            yMaxAngle = getAngleBetweenVectors(0, ry);
        } else if (anglesAreEquals(phi, Math.PI / 2.0) || anglesAreEquals(phi, 3.0 * Math.PI / 2.0)) {
            xMin = cx - ry;
            xMinAngle = getAngleBetweenVectors(-ry, 0);
            xMax = cx + ry;
            xMaxAngle = getAngleBetweenVectors(ry, 0);
            yMin = cy - rx;
            yMinAngle = getAngleBetweenVectors(0, -rx);
            yMax = cy + rx;
            yMaxAngle = getAngleBetweenVectors(0, rx);
        } else {
            // get theta values
            double txMin = -Math.atan(ry * Math.tan(phi) / rx);
            double txMax = Math.PI - Math.atan(ry * Math.tan(phi) / rx);
            // get x values substituting theta and center coordinates to the ellipse function
            xMin = cx + rx * Math.cos(txMin) * Math.cos(phi) - ry * Math.sin(txMin) * Math.sin(phi);
            xMax = cx + rx * Math.cos(txMax) * Math.cos(phi) - ry * Math.sin(txMax) * Math.sin(phi);
            if (xMin > xMax) {
                double temp = xMin;
                xMin = xMax;
                xMax = temp;
                temp = txMin;
                txMin = txMax;
                txMax = temp;
            }
            // calculate angles corresponding to extremes
            double tempY = cy + rx * Math.cos(txMin) * Math.sin(phi) + ry * Math.sin(txMin) * Math.cos(phi);
            xMinAngle = getAngleBetweenVectors(xMin - cx, tempY - cy);
            tempY = cy + rx * Math.cos(txMax) * Math.sin(phi) + ry * Math.sin(txMax) * Math.cos(phi);
            xMaxAngle = getAngleBetweenVectors(xMax - cx, tempY - cy);

            // get theta values
            double tyMin = Math.atan(ry / (Math.tan(phi) * rx));
            double tyMax = Math.atan(ry / (Math.tan(phi) * rx)) + Math.PI;
            // get y values substituting theta and center coordinates to the ellipse function
            yMin = cy + rx * Math.cos(tyMin) * Math.sin(phi) + ry * Math.sin(tyMin) * Math.cos(phi);
            yMax = cy + rx * Math.cos(tyMax) * Math.sin(phi) + ry * Math.sin(tyMax) * Math.cos(phi);
            if (yMin > yMax) {
                double temp = yMin;
                yMin = yMax;
                yMax = temp;
                temp = tyMin;
                tyMin = tyMax;
                tyMax = temp;
            }
            // calculate angles corresponding to extremes
            double tmpX = cx + rx * Math.cos(tyMin) * Math.cos(phi) - ry * Math.sin(tyMin) * Math.sin(phi);
            yMinAngle = getAngleBetweenVectors(tmpX - cx, yMin - cy);
            tmpX = cx + rx * Math.cos(tyMax) * Math.cos(phi) - ry * Math.sin(tyMax) * Math.sin(phi);
            yMaxAngle = getAngleBetweenVectors(tmpX - cx, yMax - cy);
        }

        // extremes
        double[] coordinates = new double[] {xMin, yMin, xMax, yMax};
        // corresponding angles
        double[] angles = new double[] {xMinAngle, yMinAngle, xMaxAngle, yMaxAngle};
        return new double[][] {coordinates, angles};
    }

    /**
     * Check that angle corresponding to extreme points is on the current arc.
     * For this we check that this angle is between the angles of starting and ending points.
     *
     * @param pointAngle angle to check
     * @param angle1     angle of the first extreme point if ellipse(starting or ending)
     * @param angle2     angle of the second extreme point if ellipse(starting or ending)
     * @param otherArc   if we should check that point is placed on the other arc of the current ellipse
     * @return true if point is on the arc
     */
    private boolean isPointOnTheArc(double pointAngle, double angle1, double angle2, boolean otherArc) {
        boolean isThetaBetweenAngles = angle1 <= pointAngle && angle2 >= pointAngle;
        // in case of other we should make sure that the point is not on the arc
        return otherArc != isThetaBetweenAngles;
    }

    /**
     * Return the angle between the vector (1, 0) and the line specified by points (0, 0) and (bx, by) in range [ 0,
     * Pi/2 ] U [ 3*Pi/2, 2*Pi).
     * As the angle between vectors should cover the whole circle, i.e. [0, 2* Pi).
     * General formula to find angle between two vectors is formula F.6.5.4 on the https://www.w3.org/TR/SVG/implnote.html#ArcConversionEndpointToCenter.
     *
     * @param bx x coordinate of the vector ending point
     * @param by y coordinate of the vector ending point
     * @return calculated angle between vectors
     */
    private double getAngleBetweenVectors(double bx, double by) {
        return (2 * Math.PI + (by > 0.0 ? 1.0 : -1.0) * Math.acos(bx / Math.sqrt(bx * bx + by * by))) % (2 * Math.PI);
    }

    private boolean anglesAreEquals(double angle1, double angle2) {
        return Math.abs(angle1 - angle2) < EPS;
    }
}
