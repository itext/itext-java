package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

public class PdfCircleAnnotation extends PdfMarkupAnnotation {

    public PdfCircleAnnotation(PdfDocument document, Rectangle rect) {
        super(document, rect);
    }

    public PdfCircleAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Circle;
    }
}
