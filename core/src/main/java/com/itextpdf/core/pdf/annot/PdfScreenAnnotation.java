package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;

public class PdfScreenAnnotation extends PdfAnnotation {

    public PdfScreenAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfScreenAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Screen;
    }
}
