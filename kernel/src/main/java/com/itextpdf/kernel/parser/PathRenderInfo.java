package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Path;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.FillingRule;

/**
 * Contains information relating to painting current path.
 */
public class PathRenderInfo implements EventData {

    /**
     * End the path object without filling or stroking it. This operator shall be a path-painting no-op,
     * used primarily for the side effect of changing the current clipping path
     */
    public static final int NO_OP = 0;

    /**
     * Value specifying stroke operation to perform on the current path.
     */
    public static final int STROKE = 1;

    /**
     * Value specifying fill operation to perform on the current path. When the fill operation
     * is performed it should use either nonzero winding or even-odd rule.
     */
    public static final int FILL = 2;

    private Path path;
    private int operation;
    private int rule;
    private boolean isClip;
    private int clippingRule;
    private CanvasGraphicsState gs;

    /**
     * @param path      The path to be rendered.
     * @param operation One of the possible combinations of {@link #STROKE} and {@link #FILL} values or {@link #NO_OP}
     * @param rule      Either {@link FillingRule#NONZERO_WINDING} or {@link FillingRule#EVEN_ODD}.
     * @param isClip    True indicates that current path modifies the clipping path, false - if not.
     * @param clipRule  Either {@link FillingRule#NONZERO_WINDING} or {@link FillingRule#EVEN_ODD}.
     * @param gs        The graphics state.
     */
    public PathRenderInfo(Path path, int operation, int rule, boolean isClip, int clipRule, CanvasGraphicsState gs) {
        this.path = path;
        this.operation = operation;
        this.rule = rule;
        this.gs = gs;
        this.isClip = isClip;
        this.clippingRule = clipRule;
    }

    /**
     * If the operation is {@link #NO_OP} then the rule is ignored,
     * otherwise {@link FillingRule#NONZERO_WINDING} is used by default.
     * With this constructor path is considered as not modifying clipping path.
     *
     * See {@link #PathRenderInfo(Path, int, int, boolean, int, CanvasGraphicsState)}
     */
    public PathRenderInfo(Path path, int operation, CanvasGraphicsState gs) {
        this(path, operation, FillingRule.NONZERO_WINDING, false, FillingRule.NONZERO_WINDING, gs);
    }

    /**
     * @return The {@link Path} to be rendered.
     */
    public Path getPath() {
        return path;
    }

    /**
     * @return <CODE>int</CODE> value which is either {@link #NO_OP} or one of possible
     * combinations of {@link #STROKE} and {@link #FILL}
     */
    public int getOperation() {
        return operation;
    }

    /**
     * @return Either {@link FillingRule#NONZERO_WINDING} or {@link FillingRule#EVEN_ODD}.
     */
    public int getRule() {
        return rule;
    }

    /**
     * @return true indicates that current path modifies the clipping path, false - if not.
     */
    public boolean isPathModifiesClippingPath() {
        return isClip;
    }

    /**
     * @return Either {@link FillingRule#NONZERO_WINDING} or {@link FillingRule#EVEN_ODD}.
     */
    public int getClippingRule() {
        return clippingRule;
    }

    /**
     * @return Current transformation matrix.
     */
    public Matrix getCtm() {
        return gs.getCtm();
    }

    public float getLineWidth() {
        return gs.getLineWidth();
    }

    public int getLineCapStyle() {
        return gs.getLineCapStyle();
    }

    public int getLineJoinStyle() {
        return gs.getLineJoinStyle();
    }

    public float getMiterLimit() {
        return gs.getMiterLimit();
    }

    public PdfArray getLineDashPattern() {
        return gs.getDashPattern();
    }

    public Color getStrokeColor() { return  gs.getStrokeColor(); }

    public Color getFillColor() { return gs.getFillColor(); }
}
