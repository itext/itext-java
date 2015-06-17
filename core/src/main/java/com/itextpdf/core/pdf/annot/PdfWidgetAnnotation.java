package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;

public class PdfWidgetAnnotation extends PdfAnnotation {

    public PdfWidgetAnnotation(PdfDocument document, Rectangle rect) {
        super(document, rect);
    }

    public PdfWidgetAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Widget;
    }

    public PdfWidgetAnnotation setParent(PdfObject parent) {
        return put(PdfName.Parent, parent);
    }

    public PdfWidgetAnnotation setHighlightMode(String mode) {
        return put(PdfName.H, new PdfName(mode));
    }

    public PdfName getHighlightMode() {
        return getPdfObject().getAsName(PdfName.H);
    }
}
