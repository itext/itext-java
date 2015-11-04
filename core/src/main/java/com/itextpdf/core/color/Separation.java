package com.itextpdf.core.color;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.colorspace.PdfColorSpace;
import com.itextpdf.core.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.core.pdf.function.PdfFunction;

public class Separation extends Color {

    public Separation(PdfSpecialCs.Separation cs, float value) {
        super(cs, new float[]{value});
    }

    public Separation(PdfDocument document, String name, PdfColorSpace alternateCs, PdfFunction tintTransform, float value) {
        this(new PdfSpecialCs.Separation(document, name, alternateCs, tintTransform), value);
    }

}
