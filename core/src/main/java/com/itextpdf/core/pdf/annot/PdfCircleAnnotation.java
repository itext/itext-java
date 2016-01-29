package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;

public class PdfCircleAnnotation extends PdfMarkupAnnotation {

    public PdfCircleAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfCircleAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Circle;
    }
}
