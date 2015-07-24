package com.itextpdf.canvas;

public class PdfCanvasConstants {

    public static final class TextRenderingMode {
        private TextRenderingMode() {}

        public static final int TEXT_RENDERING_MODE_FILL = 0;
        public static final int TEXT_RENDERING_MODE_STROKE = 1;
        public static final int TEXT_RENDERING_MODE_FILL_STROKE = 2;
        public static final int TEXT_RENDERING_MODE_INVISIBLE = 3;
        public static final int TEXT_RENDERING_MODE_FILL_CLIP = 4;
        public static final int TEXT_RENDERING_MODE_STROKE_CLIP = 5;
        public static final int TEXT_RENDERING_MODE_FILL_STROKE_CLIP = 6;
        public static final int TEXT_RENDERING_MODE_CLIP = 7;
    }

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
