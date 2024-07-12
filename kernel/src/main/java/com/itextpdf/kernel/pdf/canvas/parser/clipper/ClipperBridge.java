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
package com.itextpdf.kernel.pdf.canvas.parser.clipper;

import com.itextpdf.kernel.geom.Subpath;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains variety of methods allowing to convert iText
 * abstractions into the abstractions of the Clipper library and vise versa.
 * <p>
 * For example:
 * <ul>
 *     <li>{@link PolyTree} to {@link com.itextpdf.kernel.geom.Path}
 *     <li>{@link com.itextpdf.kernel.geom.Point} to {@link Point.LongPoint}
 *     <li>{@link Point.LongPoint} to {@link com.itextpdf.kernel.geom.Point}
 * </ul>
 */
public final class ClipperBridge {

    /**
     * Since the clipper library uses integer coordinates, we should convert
     * our floating point numbers into fixed point numbers by multiplying by
     * this coefficient. Vary it to adjust the preciseness of the calculations.
     */
    //TODO DEVSIX-5770 make this constant a single non-static configuration
    public static double floatMultiplier = Math.pow(10, 14);

    private ClipperBridge() {
        //empty constructor
    }

    /**
     * Converts Clipper library {@link PolyTree} abstraction into iText
     * {@link com.itextpdf.kernel.geom.Path} object.
     *
     * @param result {@link PolyTree} object to convert
     * @return resultant {@link com.itextpdf.kernel.geom.Path} object
     */
    public static com.itextpdf.kernel.geom.Path convertToPath(PolyTree result) {
        com.itextpdf.kernel.geom.Path path = new com.itextpdf.kernel.geom.Path();
        PolyNode node = result.getFirst();

        while (node != null) {
            addContour(path, node.getContour(), !node.isOpen());
            node = node.getNext();
        }

        return path;
    }

    /**
     * Adds iText {@link com.itextpdf.kernel.geom.Path} to the given {@link IClipper} object.
     * @param clipper The {@link IClipper} object.
     * @param path The {@link com.itextpdf.kernel.geom.Path} object to be added to the {@link IClipper}.
     * @param polyType See {@link IClipper.PolyType}.
     */
    public static void addPath(IClipper clipper, com.itextpdf.kernel.geom.Path path, IClipper.PolyType polyType) {
        for (Subpath subpath : path.getSubpaths()) {
            if (!subpath.isSinglePointClosed() && !subpath.isSinglePointOpen()) {
                List<com.itextpdf.kernel.geom.Point> linearApproxPoints = subpath.getPiecewiseLinearApproximation();
                clipper.addPath(new Path(convertToLongPoints(linearApproxPoints)), polyType, subpath.isClosed());
            }
        }
    }

    /**
     * Adds all iText {@link Subpath}s of the iText {@link com.itextpdf.kernel.geom.Path} to the {@link ClipperOffset} object with one
     * note: it doesn't add degenerate subpaths.
     *
     * @param offset   the {@link ClipperOffset} object to add all iText {@link Subpath}s that are not degenerated.
     * @param path     {@link com.itextpdf.kernel.geom.Path} object, containing the required {@link Subpath}s
     * @param joinType {@link IClipper} join type. The value could be {@link IClipper.JoinType#BEVEL}, {@link IClipper.JoinType#ROUND},
     *                 {@link IClipper.JoinType#MITER}
     * @param endType  {@link IClipper} end type. The value could be {@link IClipper.EndType#CLOSED_POLYGON},
     *                 {@link IClipper.EndType#CLOSED_LINE}, {@link IClipper.EndType#OPEN_BUTT}, {@link IClipper.EndType#OPEN_SQUARE},
     *                 {@link IClipper.EndType#OPEN_ROUND}
     * @return {@link java.util.List} consisting of all degenerate iText {@link Subpath}s of the path.
     */
    public static List<Subpath> addPath(ClipperOffset offset, com.itextpdf.kernel.geom.Path path, IClipper.JoinType joinType, IClipper.EndType endType) {
        List<Subpath> degenerateSubpaths = new ArrayList<>();

        for (Subpath subpath : path.getSubpaths()) {
            if (subpath.isDegenerate()) {
                degenerateSubpaths.add(subpath);
                continue;
            }

            if (!subpath.isSinglePointClosed() && !subpath.isSinglePointOpen()) {
                IClipper.EndType et;

                if (subpath.isClosed()) {
                    // Offsetting is never used for path being filled
                    et = IClipper.EndType.CLOSED_LINE;
                } else {
                    et = endType;
                }

                List<com.itextpdf.kernel.geom.Point> linearApproxPoints = subpath.getPiecewiseLinearApproximation();
                offset.addPath(new Path(convertToLongPoints(linearApproxPoints)), joinType, et);
            }
        }

        return degenerateSubpaths;
    }

    /**
     * Converts list of {@link Point.LongPoint} objects into list of
     * {@link com.itextpdf.kernel.geom.Point} objects.
     *
     * @param points the list of {@link Point.LongPoint} objects to convert
     * @return the resultant list of {@link com.itextpdf.kernel.geom.Point} objects.
     */
    public static List<com.itextpdf.kernel.geom.Point> convertToFloatPoints(List<Point.LongPoint> points) {
        List<com.itextpdf.kernel.geom.Point> convertedPoints = new ArrayList<>(points.size());

        for (Point.LongPoint point : points) {
            convertedPoints.add(new com.itextpdf.kernel.geom.Point(
                    point.getX() / floatMultiplier,
                    point.getY() / floatMultiplier
            ));
        }

        return convertedPoints;
    }

    /**
     * Converts list of {@link com.itextpdf.kernel.geom.Point} objects into list of
     * {@link Point.LongPoint} objects.
     *
     * @param points the list of {@link com.itextpdf.kernel.geom.Point} objects to convert
     * @return the resultant list of {@link Point.LongPoint} objects.
     */
    public static List<Point.LongPoint> convertToLongPoints(List<com.itextpdf.kernel.geom.Point> points) {
        List<Point.LongPoint> convertedPoints = new ArrayList<>(points.size());

        for (com.itextpdf.kernel.geom.Point point : points) {
            convertedPoints.add(new Point.LongPoint(
                    floatMultiplier * point.getX(),
                    floatMultiplier * point.getY()
            ));
        }

        return convertedPoints;
    }

    /**
     * Converts iText line join style constant into the corresponding constant
     * of the Clipper library.
     * @param lineJoinStyle iText line join style constant. See {@link PdfCanvasConstants}
     * @return Clipper line join style constant.
     */
    public static IClipper.JoinType getJoinType(int lineJoinStyle) {
        switch (lineJoinStyle) {
            case PdfCanvasConstants.LineJoinStyle.BEVEL:
                return IClipper.JoinType.BEVEL;

            case PdfCanvasConstants.LineJoinStyle.MITER:
                return IClipper.JoinType.MITER;
        }

        return IClipper.JoinType.ROUND;
    }

    /**
     * Converts iText line cap style constant into the corresponding constant
     * of the Clipper library.
     * @param lineCapStyle iText line cap style constant. See {@link PdfCanvasConstants}
     * @return Clipper line cap (end type) style constant.
     */
    public static IClipper.EndType getEndType(int lineCapStyle) {
        switch (lineCapStyle) {
            case PdfCanvasConstants.LineCapStyle.BUTT:
                return IClipper.EndType.OPEN_BUTT;

            case PdfCanvasConstants.LineCapStyle.PROJECTING_SQUARE:
                return IClipper.EndType.OPEN_SQUARE;
        }

        return IClipper.EndType.OPEN_ROUND;
    }

    /**
     * Converts iText filling rule constant into the corresponding constant
     * of the Clipper library.
     * @param fillingRule Either {@link com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.FillingRule#NONZERO_WINDING} or
     *                    {@link com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.FillingRule#EVEN_ODD}.
     * @return Clipper fill type constant.
     */
    public static IClipper.PolyFillType getFillType(int fillingRule) {
        IClipper.PolyFillType fillType = IClipper.PolyFillType.NON_ZERO;

        if (fillingRule == PdfCanvasConstants.FillingRule.EVEN_ODD) {
            fillType = IClipper.PolyFillType.EVEN_ODD;
        }

        return fillType;
    }

    /**
     * Adds polygon path based on array of {@link com.itextpdf.kernel.geom.Point} (internally converting
     * them by {@link #convertToLongPoints}) and adds this path to {@link IClipper} instance, treating the path as
     * a closed polygon.
     * <p>
     * The return value will be false if the path is invalid for clipping. A path is invalid for clipping when:
     * <ul>
     * <li>it has less than 3 vertices;
     * <li>the vertices are all co-linear.
     * </ul>
     * @param clipper {@link IClipper} instance to which the created polygon path will be added.
     * @param polyVertices an array of {@link com.itextpdf.kernel.geom.Point} which will be internally converted
     *                     to clipper path and added to the clipper instance.
     * @param polyType either {@link IClipper.PolyType#SUBJECT} or {@link IClipper.PolyType#CLIP} denoting whether added
     *                 path is a subject of clipping or a part of the clipping polygon.
     * @return true if polygon path was successfully added, false otherwise.
     */
    public static boolean addPolygonToClipper(IClipper clipper, com.itextpdf.kernel.geom.Point[] polyVertices, IClipper.PolyType polyType) {
        return clipper.addPath(new Path(convertToLongPoints(new ArrayList<>(Arrays.asList(polyVertices)))), polyType, true);
    }

    /**
     * Adds polyline path based on array of {@link com.itextpdf.kernel.geom.Point} (internally converting
     * them by {@link #convertToLongPoints}) and adds this path to {@link IClipper} instance, treating the path as
     * a polyline (an open path in terms of clipper library). This path is added to the subject of future clipping.
     * Polylines cannot be part of clipping polygon.
     * <p>
     * The return value will be false if the path is invalid for clipping. A path is invalid for clipping when:
     * <ul>
     * <li>it has less than 2 vertices;
     * </ul>
     * @param clipper {@link IClipper} instance to which the created polyline path will be added.
     * @param lineVertices an array of {@link com.itextpdf.kernel.geom.Point} which will be internally converted
     *                     to clipper path and added to the clipper instance.
     * @return true if polyline path was successfully added, false otherwise.
     */
    public static boolean addPolylineSubjectToClipper(IClipper clipper, com.itextpdf.kernel.geom.Point[] lineVertices) {
        return clipper.addPath(new Path(convertToLongPoints(new ArrayList<>(Arrays.asList(lineVertices)))), IClipper.PolyType.SUBJECT, false);
    }

    /**
     * Calculates the width of the rectangle represented by the {@link LongRect} object.
     * @param rect the {@link LongRect} object representing the rectangle.
     *
     * @return the width of the rectangle.
     */
    public static float longRectCalculateWidth(LongRect rect) {
        return (float) (Math.abs(rect.left - rect.right) / ClipperBridge.floatMultiplier);
    }

    /**
     * Calculates the height of the rectangle represented by the {@link LongRect} object.
     * @param rect the {@link LongRect} object representing the rectangle.
     *
     * @return the height of the rectangle.
     */
    public static float longRectCalculateHeight(LongRect rect) {
        return (float) (Math.abs(rect.top - rect.bottom) / ClipperBridge.floatMultiplier);
    }

    static void addContour(com.itextpdf.kernel.geom.Path path, List<Point.LongPoint> contour, boolean close) {
        List<com.itextpdf.kernel.geom.Point> floatContour = convertToFloatPoints(contour);
        com.itextpdf.kernel.geom.Point point = floatContour.get(0);
        path.moveTo((float) point.getX(), (float) point.getY());

        for (int i = 1; i < floatContour.size(); i++) {
            point = floatContour.get(i);
            path.lineTo((float) point.getX(), (float) point.getY());
        }

        if (close) {
            path.closeSubpath();
        }
    }
}
