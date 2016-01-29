package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;

public class PdfSquareAnnotation extends PdfMarkupAnnotation {

    public PdfSquareAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfSquareAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Square;
    }
}
