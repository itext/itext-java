package com.itextpdf.canvas.color;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.colorspace.PdfCieBasedCs;

public class CalRgb extends Color {

    public CalRgb(PdfCieBasedCs.CalRgb cs, float[] value) {
        super(cs, value);
    }

    public CalRgb(PdfDocument document, float[] whitePoint, float[] value) throws PdfException {
        super(new PdfCieBasedCs.CalRgb(document, whitePoint), value);
    }

    public CalRgb(PdfDocument document, float[] whitePoint, float[] blackPoint, float[] gamma, float[] matrix, float[] value) throws PdfException {
        this(new PdfCieBasedCs.CalRgb(document, whitePoint, blackPoint, gamma, matrix), value);
    }

}
