package com.itextpdf.core.parser;

/**
 * Contains information relating to painting current path.
 *
 * @since 5.5.6
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
    private GraphicsState gs;

    /**
     * @param path      The path to be rendered.
     * @param operation One of the possible combinations of {@link #STROKE} and {@link #FILL} values or {@link #NO_OP}
     * @param rule      Either {@link PdfContentByte#NONZERO_WINDING_RULE} or {@link PdfContentByte#EVEN_ODD_RULE}.
     * @param gs        The graphics state.
     */
    public PathRenderInfo(Path path, int operation, int rule, GraphicsState gs) {
        this.path = path;
        this.operation = operation;
        this.rule = rule;
        this.gs = gs;
    }

    /**
     * If the operation is {@link #NO_OP} then the rule is ignored,
     * otherwise {@link PdfContentByte#NONZERO_WINDING_RULE} is used by default.
     *
     * See {@link #PathRenderInfo(Path, int, int, GraphicsState)}
     */
    public PathRenderInfo(Path path, int operation, GraphicsState gs) {
        this(path, operation, PdfCanvasConstants.FillingRule.NONZERO_WINDING, gs);
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
     * @return Either {@link PdfContentByte#NONZERO_WINDING_RULE} or {@link PdfContentByte#EVEN_ODD_RULE}.
     */
    public int getRule() {
        return rule;
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

    public LineDashPattern getLineDashPattern() {
        return gs.getLineDashPattern();
    }
}
