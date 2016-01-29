package com.itextpdf.core.parser.clipper;

import com.itextpdf.core.geom.Point2D;
import com.itextpdf.core.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.core.parser.Subpath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class contains variety of methods allowing to convert iText
 * abstractions into the abstractions of the Clipper library and vise versa.
 * <p>
 * For example:
 * <ul>
 *     <li>{@link PolyTree} to {@link com.itextpdf.core.parser.Path}</li>
 *     <li>{@link Point2D} to {@link com.itextpdf.core.parser.clipper.Point.LongPoint}</li>
 *     <li>{@link com.itextpdf.core.parser.clipper.Point.LongPoint} to {@link Point2D}</li>
 * </ul>
 * </p>
 * @since 5.5.7
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
     * {@link com.itextpdf.core.parser.Path} object.
     */
    public static com.itextpdf.core.parser.Path convertToPath(PolyTree result) {
        com.itextpdf.core.parser.Path path = new com.itextpdf.core.parser.Path();
        PolyNode node = result.getFirst();

        while (node != null) {
            addContour(path, node.getContour(), !node.isOpen());
            node = node.getNext();
        }

        return path;
    }

    /**
     * Adds iText {@link com.itextpdf.core.parser.Path} to the given {@link Clipper} object.
     * @param clipper The {@link Clipper} object.
     * @param path The {@link com.itextpdf.core.parser.Path} object to be added to the {@link Clipper}.
     * @param polyType See {@link com.itextpdf.core.parser.clipper.Clipper.PolyType}.
     */
    public static void addPath(Clipper clipper, com.itextpdf.core.parser.Path path, Clipper.PolyType polyType) {
        for (Subpath subpath : path.getSubpaths()) {
            if (!subpath.isSinglePointClosed() && !subpath.isSinglePointOpen()) {
                List<Point2D> linearApproxPoints = subpath.getPiecewiseLinearApproximation();
                clipper.addPath(new Path(convertToLongPoints(linearApproxPoints)), polyType, subpath.isClosed());
            }
        }
    }

    /**
     * Converts list of {@link Point.LongPoint} objects into list of
     * {@link Point2D} objects.
     */
    public static List<Point2D> convertToFloatPoints(List<Point.LongPoint> points) {
        List<Point2D> convertedPoints = new ArrayList<>(points.size());

        for (Point.LongPoint point : points) {
            convertedPoints.add(new Point2D.Float(
                    (float) (point.getX() / floatMultiplier),
                    (float) (point.getY() / floatMultiplier)
            ));
        }

        return convertedPoints;
    }

    /**
     * Converts list of {@link Point2D} objects into list of
     * {@link com.itextpdf.core.parser.clipper.Point.LongPoint} objects.
     */
    public static List<Point.LongPoint> convertToLongPoints(List<Point2D> points) {
        List<Point.LongPoint> convertedPoints = new ArrayList<>(points.size());

        for (Point2D point : points) {
            convertedPoints.add(new Point.LongPoint(
                    floatMultiplier * point.getX(),
                    floatMultiplier * point.getY()
            ));
        }

        return new com.itextpdf.core.parser.clipper.Path(convertedPoints);
    }

    /**
     * Converts iText line join style constant into the corresponding constant
     * of the Clipper library.
     * @param lineJoinStyle iText line join style constant. See {@link PdfCanvasConstants}
     * @return Clipper line join style constant.
     */
    public static Clipper.JoinType getJoinType(int lineJoinStyle) {
        switch (lineJoinStyle) {
            case PdfCanvasConstants.LineJoinStyle.BEVEL:
                return Clipper.JoinType.BEVEL;

            case PdfCanvasConstants.LineJoinStyle.MITER:
                return Clipper.JoinType.MITER;
        }

        return Clipper.JoinType.ROUND;
    }

    /**
     * Converts iText line cap style constant into the corresponding constant
     * of the Clipper library.
     * @param lineCapStyle iText line cap style constant. See {@link PdfCanvasConstants}
     * @return Clipper line cap (end type) style constant.
     */
    public static Clipper.EndType getEndType(int lineCapStyle) {
        switch (lineCapStyle) {
            case PdfCanvasConstants.LineCapStyle.BUTT:
                return Clipper.EndType.OPEN_BUTT;

            case PdfCanvasConstants.LineCapStyle.PROJECTING_SQUARE:
                return Clipper.EndType.OPEN_SQUARE;
        }

        return Clipper.EndType.OPEN_ROUND;
    }

    /**
     * Converts iText filling rule constant into the corresponding constant
     * of the Clipper library .
     * @param fillingRule Either {@link com.itextpdf.core.pdf.canvas.PdfCanvasConstants.FillingRule#NONZERO_WINDING} or
     *                    {@link com.itextpdf.core.pdf.canvas.PdfCanvasConstants.FillingRule#EVEN_ODD}.
     * @return Clipper fill type constant.
     */
    public static Clipper.PolyFillType getFillType(int fillingRule) {
        Clipper.PolyFillType fillType = Clipper.PolyFillType.NON_ZERO;

        if (fillingRule == PdfCanvasConstants.FillingRule.EVEN_ODD) {
            fillType = Clipper.PolyFillType.EVEN_ODD;
        }

        return fillType;
    }

    private static void addContour(com.itextpdf.core.parser.Path path, List<Point.LongPoint> contour, Boolean close) {
        List<Point2D> floatContour = convertToFloatPoints(contour);
        Iterator<Point2D> iter = floatContour.iterator();

        Point2D point = iter.next();
        path.moveTo((float) point.getX(), (float) point.getY());

        while (iter.hasNext()) {
            point = iter.next();
            path.lineTo((float) point.getX(), (float) point.getY());
        }

        if (close) {
            path.closeSubpath();
        }
    }
}
