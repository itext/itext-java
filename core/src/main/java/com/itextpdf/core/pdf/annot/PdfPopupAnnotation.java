package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.*;

public class PdfPopupAnnotation extends PdfAnnotation {

    protected PdfAnnotation parent;

    public PdfPopupAnnotation(PdfDocument document, Rectangle rect) {
        super(document, rect);
    }

    public PdfPopupAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    public PdfName getSubtype() {
        return PdfName.Popup;
    }

    public PdfDictionary getParentObject() {
        return getPdfObject().getAsDictionary(PdfName.Parent);
    }

    public PdfAnnotation getParent() {
        return parent;
    }

    public PdfPopupAnnotation setParent(PdfAnnotation parent) {
        this.parent = parent;
        return put(PdfName.Parent, parent);
    }

}
