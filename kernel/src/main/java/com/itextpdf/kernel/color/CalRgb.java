package com.itextpdf.kernel.color;

import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;

public class CalRgb extends Color {

    public CalRgb(PdfCieBasedCs.CalRgb cs, float[] value) {
        super(cs, value);
    }

    public CalRgb(float[] whitePoint, float[] value) {
        super(new PdfCieBasedCs.CalRgb(whitePoint), value);
    }

    public CalRgb(float[] whitePoint, float[] blackPoint, float[] gamma, float[] matrix, float[] value) {
        this(new PdfCieBasedCs.CalRgb(whitePoint, blackPoint, gamma, matrix), value);
    }

}
