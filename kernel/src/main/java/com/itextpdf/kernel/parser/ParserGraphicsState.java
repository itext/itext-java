package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.BezierCurve;
import com.itextpdf.kernel.geom.Line;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Path;
import com.itextpdf.kernel.geom.Point2D;
import com.itextpdf.kernel.geom.Shape;
import com.itextpdf.kernel.geom.Subpath;
import com.itextpdf.kernel.parser.clipper.Clipper;
import com.itextpdf.kernel.parser.clipper.ClipperBridge;
import com.itextpdf.kernel.parser.clipper.DefaultClipper;
import com.itextpdf.kernel.parser.clipper.PolyTree;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import java.util.Arrays;
import java.util.List;

/**
 * Internal class which is essentially a {@link CanvasGraphicsState} which supports tracking of
 * clipping path state and changes.
 */
public class ParserGraphicsState extends CanvasGraphicsState {
    // NOTE: From the spec default value of this field should be the boundary of the entire imageable portion of the output page.
    private Path clippingPath;

    /**
     * Internal empty & default constructor.
     */
    ParserGraphicsState() {

    }

    /**
     * Copy constructor.
     * @param source the Graphics State to copy from
     */
    ParserGraphicsState(final ParserGraphicsState source) {
        super(source);

        if (source.clippingPath != null) {
            clippingPath = new Path(source.clippingPath);
        }
    }

    /**
     * Sets the current clipping path to the specified path.
     * <br/>
     * <strong>Note:</strong>This method doesn't modify existing clipping path,
     * it simply replaces it with the new one instead.
     * @param clippingPath New clipping path.
     */
    public void setClippingPath(Path clippingPath) {
        Path pathCopy = new Path(clippingPath);
        pathCopy.closeAllSubpaths();
        this.clippingPath = pathCopy;
    }

    @Override
    public void updateCtm(Matrix newCtm) {
        super.updateCtm(newCtm);

        if (clippingPath != null) {
            transformClippingPath(newCtm);
        }
    }

    /**
     * Intersects the current clipping path with the given path.
     * <br/>
     * <strong>Note:</strong> Coordinates of the given path should be in
     * the transformed user space.
     * @param path The path to be intersected with the current clipping path.
     * @param fillingRule The filling rule which should be applied to the given path.
     *                    It should be either {@link PdfCanvasConstants.FillingRule#EVEN_ODD} or
     *                    {@link PdfCanvasConstants.FillingRule#NONZERO_WINDING}
     */
    public void clip(Path path, int fillingRule) {
        if (clippingPath == null || clippingPath.isEmpty()) {
            return;
        }

        Path pathCopy = new Path(path);
        pathCopy.closeAllSubpaths();

        Clipper clipper = new DefaultClipper();
        ClipperBridge.addPath(clipper, clippingPath, Clipper.PolyType.SUBJECT);
        ClipperBridge.addPath(clipper, pathCopy, Clipper.PolyType.CLIP);

        PolyTree resultTree = new PolyTree();
        clipper.execute(Clipper.ClipType.INTERSECTION, resultTree, Clipper.PolyFillType.NON_ZERO, ClipperBridge.getFillType(fillingRule));

        clippingPath = ClipperBridge.convertToPath(resultTree);
    }

    /**
     * Getter for the current clipping path.
     * <br/>
     * <strong>Note:</strong> The returned clipping path is in the transformed user space, so
     * if you want to get it in default user space, apply transformation matrix ({@link CanvasGraphicsState#getCtm()}).
     * @return The current clipping path.
     */
    public Path getClippingPath() {
        return clippingPath;
    }

    private void transformClippingPath(Matrix newCtm) {
        Path path = new Path();

        for (Subpath subpath : clippingPath.getSubpaths()) {
            Subpath transformedSubpath = transformSubpath(subpath, newCtm);
            path.addSubpath(transformedSubpath);
        }

        clippingPath = path;
    }

    private Subpath transformSubpath(Subpath subpath, Matrix newCtm) {
        Subpath newSubpath = new Subpath();
        newSubpath.setClosed(subpath.isClosed());

        for (Shape segment : subpath.getSegments()) {
            Shape transformedSegment = transformSegment(segment, newCtm);
            newSubpath.addSegment(transformedSegment);
        }

        return newSubpath;
    }

    private Shape transformSegment(Shape segment, Matrix newCtm) {
        Shape newSegment;
        List<Point2D> segBasePts = segment.getBasePoints();
        Point2D[] transformedPoints = transformPoints(newCtm, segBasePts.toArray(new Point2D[segBasePts.size()]));

        if (segment instanceof BezierCurve) {
            newSegment = new BezierCurve(Arrays.asList(transformedPoints));
        } else {
            newSegment = new Line(transformedPoints[0], transformedPoints[1]);
        }

        return newSegment;
    }

    private Point2D[] transformPoints(Matrix transformationMatrix, Point2D... points) {
        try {

            AffineTransform t = new AffineTransform(
                    transformationMatrix.get(Matrix.I11), transformationMatrix.get(Matrix.I12),
                    transformationMatrix.get(Matrix.I21), transformationMatrix.get(Matrix.I22),
                    transformationMatrix.get(Matrix.I31), transformationMatrix.get(Matrix.I32)
            );
            t = t.createInverse();

            Point2D[] transformed = new Point2D[points.length];
            t.transform(points, 0, transformed, 0, points.length);
            return transformed;

        } catch (NoninvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }
}
