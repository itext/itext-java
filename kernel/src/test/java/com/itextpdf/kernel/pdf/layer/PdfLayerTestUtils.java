package com.itextpdf.kernel.pdf.layer;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

class PdfLayerTestUtils {

    public static void addTextInsideLayer(IPdfOCG layer, PdfCanvas canvas, String text, float x, float y) {
        canvas
                .beginLayer(layer)
                .beginText()
                .moveText(x, y)
                .showText(text)
                .endText()
                .endLayer();
    }
}
