package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Point2D;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.parser.clipper.Clipper;
import com.itextpdf.kernel.parser.clipper.ClipperBridge;
import com.itextpdf.kernel.parser.clipper.DefaultClipper;
import com.itextpdf.kernel.parser.clipper.PolyTree;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;

import java.util.Arrays;
import java.util.List;

/**
 * Keeps all the parameters of the graphics state.
 * @since	2.1.4
 */
public class GraphicsState {

    /** The current character spacing. */
    float characterSpacing;
    /** The current word spacing. */
    float wordSpacing;
    /** The current horizontal scaling */
    float horizontalScaling;
    /** The current leading. */
    float leading;
    /** The active font. */
    PdfFont font;
    /** The current font size. */
    float fontSize;
    /** The current render mode. */
    int renderMode;
    /** The current text rise */
    float rise;
    /** The current knockout value. */
    boolean knockout;
    /** The current color space for stroke. */
    PdfName colorSpaceFill;
    /** The current color space for stroke. */
    PdfName colorSpaceStroke;
    /** The current fill color. */
    Color fillColor;
    /** The current stroke color. */
    Color strokeColor;

    /** The current transformation matrix. */
    private Matrix ctm;

    /** The line width for stroking operations */
    private float lineWidth;

    /**
     * The line cap style. For possible values
     * see {@link PdfCanvasConstants}
     */
    private int lineCapStyle;

    /**
     * The line join style. For possible values
     * see {@link PdfCanvasConstants}
     */
    private int lineJoinStyle;

    /** The mitir limit value */
    private float miterLimit;

    /** The line dash pattern */
    private LineDashPattern lineDashPattern;

    /** The current clipping path */
    private Path clippingPath;

    /**
     * Constructs a new Graphics State object with the default values.
     */
    public GraphicsState(){
        ctm = new Matrix();
        characterSpacing = 0;
        wordSpacing = 0;
        horizontalScaling = 1.0f;
        leading = 0;
        font = null;
        fontSize = 0;
        renderMode = 0;
        rise = 0;
        knockout = true;
        colorSpaceFill = null;
        colorSpaceStroke = null;
        fillColor = null;
        strokeColor = null;
        lineWidth = 1.0f;
        lineCapStyle = PdfCanvasConstants.LineCapStyle.BUTT;
        lineJoinStyle = PdfCanvasConstants.LineJoinStyle.MITER;
        miterLimit = 10.0f;
    }

    /**
     * Copy constructor.
     * @param source	another GraphicsState object
     */
    public GraphicsState(GraphicsState source){
        // note: some of the following are immutable, so it is safe to copy them as-is
        ctm = source.ctm;
        characterSpacing = source.characterSpacing;
        wordSpacing = source.wordSpacing;
        horizontalScaling = source.horizontalScaling;
        leading = source.leading;
        font = source.font;
        fontSize = source.fontSize;
        renderMode = source.renderMode;
        rise = source.rise;
        knockout = source.knockout;
        colorSpaceFill = source.colorSpaceFill;
        colorSpaceStroke = source.colorSpaceStroke;
        fillColor = source.fillColor;
        strokeColor = source.strokeColor;
        lineWidth = source.lineWidth;
        lineCapStyle = source.lineCapStyle;
        lineJoinStyle = source.lineJoinStyle;
        miterLimit = source.miterLimit;

        if (source.lineDashPattern != null) {
            lineDashPattern = new LineDashPattern(source.lineDashPattern.getDashArray(), source.lineDashPattern.getDashPhase());
        }

        if (source.clippingPath != null) {
            clippingPath = new Path(source.clippingPath);
        }
    }

    /**
     * Getter for the current transformation matrix
     * @return the ctm
     * @since iText 5.0.1
     */
    public Matrix getCtm() {
        return ctm;
    }

    /**
     * Setter for the current transformation matrix.
     * @since 5.5.7
     */
    public void updateCtm(Matrix newCtm) {
        ctm = newCtm.multiply(ctm);

        if (clippingPath != null) {
            transformClippingPath(newCtm);
        }
    }

    /**
     * Getter for the character spacing.
     * @return the character spacing
     * @since iText 5.0.1
     */
    public float getCharacterSpacing() {
        return characterSpacing;
    }

    /**
     * Getter for the word spacing
     * @return the word spacing
     * @since iText 5.0.1
     */
    public float getWordSpacing() {
        return wordSpacing;
    }

    /**
     * Getter for the horizontal scaling
     * @return the horizontal scaling
     * @since iText 5.0.1
     */
    public float getHorizontalScaling() {
        return horizontalScaling;
    }

    /**
     * Getter for the leading
     * @return the leading
     * @since iText 5.0.1
     */
    public float getLeading() {
        return leading;
    }

    /**
     * Getter for the font
     * @return the font
     * @since iText 5.0.1
     */
    public PdfFont getFont() {
        return font;
    }

    /**
     * Getter for the font size
     * @return the font size
     * @since iText 5.0.1
     */
    public float getFontSize() {
        return fontSize;
    }

    /**
     * Getter for the render mode
     * @return the renderMode
     * @since iText 5.0.1
     */
    public int getRenderMode() {
        return renderMode;
    }

    /**
     * Getter for text rise
     * @return the text rise
     * @since iText 5.0.1
     */
    public float getRise() {
        return rise;
    }

    /**
     * Getter for knockout
     * @return the knockout
     * @since iText 5.0.1
     */
    public boolean isKnockout() {
        return knockout;
    }

    /**
     * Gets the current color space for fill operations
     */
    public PdfName getColorSpaceFill() {
        return colorSpaceFill;
    }

    /**
     * Gets the current color space for stroke operations
     */
    public PdfName getColorSpaceStroke() {
        return colorSpaceStroke;
    }

    /**
     * Gets the current fill color
     * @return a BaseColor
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * Gets the current stroke color
     * @return a BaseColor
     */
    public Color getStrokeColor() {
        return strokeColor;
    }

    /**
     * Getter for the line width.
     * @return The line width
     * @since 5.5.6
     */
    public float getLineWidth() {
        return lineWidth;
    }

    /**
     * Setter for the line width.
     * @param lineWidth New line width.
     * @since 5.5.6
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * Getter for the line cap style.
     * For possible values see {@link PdfCanvasConstants}
     * @return The line cap style.
     * @since 5.5.6
     */
    public int getLineCapStyle() {
        return lineCapStyle;
    }

    /**
     * Setter for the line cap style.
     * For possible values see {@link PdfCanvasConstants}
     * @param lineCapStyle New line cap style.
     * @since 5.5.6
     */
    public void setLineCapStyle(int lineCapStyle) {
        this.lineCapStyle = lineCapStyle;
    }

    /**
     * Getter for the line join style.
     * For possible values see {@link PdfCanvasConstants}
     * @return The line join style.
     * @since 5.5.6
     */
    public int getLineJoinStyle() {
        return lineJoinStyle;
    }

    /**
     * Setter for the line join style.
     * For possible values see {@link PdfCanvasConstants}
     * @param lineJoinStyle New line join style.
     * @since 5.5.6
     */
    public void setLineJoinStyle(int lineJoinStyle) {
        this.lineJoinStyle = lineJoinStyle;
    }

    /**
     * Getter for the miter limit value.
     * @return The miter limit.
     * @since 5.5.6
     */
    public float getMiterLimit() {
        return miterLimit;
    }

    /**
     * Setter for the miter limit value.
     * @param miterLimit New miter limit.
     * @since 5.5.6
     */
    public void setMiterLimit(float miterLimit) {
        this.miterLimit = miterLimit;
    }

    /**
     * Getter for the line dash pattern.
     * @return The line dash pattern.
     * @since 5.5.6
     */
    public LineDashPattern getLineDashPattern() {
        return lineDashPattern;
    }

    /**
     * Setter for the line dash pattern.
     * @param lineDashPattern New line dash pattern.
     * @since 5.5.6
     */
    public void setLineDashPattern(LineDashPattern lineDashPattern) {
        this.lineDashPattern = new LineDashPattern(lineDashPattern.getDashArray(), lineDashPattern.getDashPhase());
    }

    /**
     * Sets the current clipping path to the specified path.
     * <br/>
     * <strong>Note:</strong>This method doesn't modify existing clipping path,
     * it simply replaces it with the new one instead.
     * @param clippingPath New clipping path.
     * @since 5.5.7
     */
    public void setClippingPath(Path clippingPath) {
        Path pathCopy = new Path(clippingPath);
        pathCopy.closeAllSubpaths();
        this.clippingPath = pathCopy;
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
     * @since 5.5.7
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
     * if you want to get it in default user space, apply transformation matrix ({@link GraphicsState#getCtm()}).
     * @return The current clipping path.
     * @since 5.5.7
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
        Point2D[] transformedPoints = transformPoints(newCtm, true, segBasePts.toArray(new Point2D[segBasePts.size()]));

        if (segment instanceof BezierCurve) {
            newSegment = new BezierCurve(Arrays.asList(transformedPoints));
        } else {
            newSegment = new Line(transformedPoints[0], transformedPoints[1]);
        }

        return newSegment;
    }

    private Point2D[] transformPoints(Matrix transormationMatrix, boolean inverse, Point2D... points) {
        AffineTransform t = new AffineTransform(
                transormationMatrix.get(Matrix.I11), transormationMatrix.get(Matrix.I12),
                transormationMatrix.get(Matrix.I21), transormationMatrix.get(Matrix.I22),
                transormationMatrix.get(Matrix.I31), transormationMatrix.get(Matrix.I32)
        );
        Point2D[] transformed = new Point2D[points.length];

        if (inverse) {
            try {
                t = t.createInverse();
            } catch (NoninvertibleTransformException e) {
                throw new RuntimeException(e);
            }
        }

        t.transform(points, 0, transformed, 0, points.length);

        return transformed;
    }
}