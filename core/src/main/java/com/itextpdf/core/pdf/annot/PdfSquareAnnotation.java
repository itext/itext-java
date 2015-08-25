package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

public class PdfSquareAnnotation extends PdfMarkupAnnotation {

    public PdfSquareAnnotation(PdfDocument document, Rectangle rect) {
        super(document, rect);
    }

    public PdfSquareAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Square;
    }
}
