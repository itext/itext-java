package com.itextpdf.kernel.color;

import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;

public class Indexed extends Color {

    public Indexed(PdfObject pdfObject, int colorValue) {
        super(pdfObject, new float[] {colorValue});
    }

    public Indexed(PdfObject pdfObject) {
        super(pdfObject);
    }

    public Indexed(PdfColorSpace colorSpace, int colorValue) {
        super(colorSpace, new float[] {colorValue});
    }


}
