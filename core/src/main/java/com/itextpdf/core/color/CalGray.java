package com.itextpdf.core.color;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.colorspace.PdfCieBasedCs;

public class CalGray extends Color {

    public CalGray(PdfCieBasedCs.CalGray cs, float value) {
        super(cs, new float[]{value});
    }

    public CalGray(PdfDocument document, float[] whitePoint, float value) {
        super(new PdfCieBasedCs.CalGray(document, whitePoint), new float[]{value});
    }

    public CalGray(PdfDocument document, float[] whitePoint, float[] blackPoint, float gamma, float value) {
        this(new PdfCieBasedCs.CalGray(document, whitePoint, blackPoint, gamma), value);
    }

}
