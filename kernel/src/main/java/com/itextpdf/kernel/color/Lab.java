package com.itextpdf.kernel.color;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;

public class Lab extends Color {

    public Lab(PdfCieBasedCs.Lab cs, float[] value) {
        super(cs, value);
    }

    public Lab(PdfDocument document, float[] whitePoint, float[] value) {
        super(new PdfCieBasedCs.Lab(document, whitePoint), value);
    }

    public Lab(PdfDocument document, float[] whitePoint, float[] blackPoint, float[] range, float[] value) {
        this(new PdfCieBasedCs.Lab(document, whitePoint, blackPoint, range), value);
    }

}
