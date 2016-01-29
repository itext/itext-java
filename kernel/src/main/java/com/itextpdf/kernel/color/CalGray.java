package com.itextpdf.kernel.color;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;

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
