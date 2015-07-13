package com.itextpdf.canvas;

public class PdfCanvasConstants {
    /** A possible text rendering value */
    public static final int TextRenderModeFill = 0;
    /** A possible text rendering value */
    public static final int TextRenderModeStroke = 1;
    /** A possible text rendering value */
    public static final int TextRenderModeFillStroke = 2;
    /** A possible text rendering value */
    public static final int TextRenderModeInvisible = 3;
    /** A possible text rendering value */
    public static final int TextRenderModeFillClip = 4;
    /** A possible text rendering value */
    public static final int TextRenderModeStrokeClip = 5;
    /** A possible text rendering value */
    public static final int TextRenderModeFillStrokeClip = 6;
    /** A possible text rendering value */
    public static final int TextRenderModeClip = 7;

    public static class LineCapStyle {
        public static final int BUTT = 0;
        public static final int ROUND = 1;
        public static final int PROJECTING_SQUARE = 2;
    }

    public static class LineJoinStyle {
        public static final int MITER = 0;
        public static final int ROUND = 1;
        public static final int BEVEL = 2;
    }
}
