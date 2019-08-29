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
public class ClipperBridge {

    /**
     * Since the clipper library uses integer coordinates, we should convert
     * our floating point numbers into fixed point numbers by multiplying by
     * this coefficient. Vary it to adjust the preciseness of the calculations.
     */
    public static double floatMultiplier = Math.pow(10, 14);

    /**
     * Converts Clipper library {@link PolyTree} abstraction into iText
     * {@link com.itextpdf.kernel.geom.Path} object.
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

    /**
     * @deprecated use {@link #addPolygonToClipper} instead.
     */
    @Deprecated
    public static void addRectToClipper(IClipper clipper, com.itextpdf.kernel.geom.Point[] rectVertices, IClipper.PolyType polyType) {
        clipper.addPath(new Path(convertToLongPoints(new ArrayList<>(Arrays.asList(rectVertices)))), polyType, true);
    }
}
