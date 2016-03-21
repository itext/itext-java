package com.itextpdf.kernel.color;

import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;

public class CalGray extends Color {

    public CalGray(PdfCieBasedCs.CalGray cs, float value) {
        super(cs, new float[]{value});
    }

    public CalGray(float[] whitePoint, float value) {
        super(new PdfCieBasedCs.CalGray(whitePoint), new float[]{value});
    }

    public CalGray(float[] whitePoint, float[] blackPoint, float gamma, float value) {
        this(new PdfCieBasedCs.CalGray(whitePoint, blackPoint, gamma), value);
    }

}
