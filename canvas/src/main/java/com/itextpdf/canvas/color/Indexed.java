package com.itextpdf.canvas.color;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.colorspace.PdfColorSpace;

public class Indexed extends Color {

    public Indexed(PdfObject pdfObject, int colorValue) throws PdfException {
        super(pdfObject, new float[] {colorValue});
    }

    public Indexed(PdfObject pdfObject) throws PdfException {
        super(pdfObject);
    }

    public Indexed(PdfColorSpace colorSpace, int colorValue) {
        super(colorSpace, new float[] {colorValue});
    }


}
