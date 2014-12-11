package com.itextpdf.canvas.colors;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.colorspace.PdfCieBasedCs;

public class CalGray extends Color {

    public CalGray(PdfCieBasedCs.CalGray cs, float value) {
        super(cs, new float[]{value});
    }

    public CalGray(PdfDocument document, float[] whitePoint, float value) throws PdfException {
        super(new PdfCieBasedCs.CalGray(document, whitePoint), new float[]{value});
    }

    public CalGray(PdfDocument document, float[] whitePoint, float[] blackPoint, float gamma, float value) throws PdfException {
        this(new PdfCieBasedCs.CalGray(document, whitePoint, blackPoint, gamma), value);
    }

}
