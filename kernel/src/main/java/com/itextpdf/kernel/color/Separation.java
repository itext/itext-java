package com.itextpdf.kernel.color;

import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.function.PdfFunction;

public class Separation extends Color {

    public Separation(PdfSpecialCs.Separation cs, float value) {
        super(cs, new float[]{value});
    }

    public Separation(String name, PdfColorSpace alternateCs, PdfFunction tintTransform, float value) {
        this(new PdfSpecialCs.Separation(name, alternateCs, tintTransform), value);
    }

}
