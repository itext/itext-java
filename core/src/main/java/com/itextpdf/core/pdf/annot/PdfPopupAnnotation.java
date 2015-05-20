package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

public class PdfPopupAnnotation extends PdfAnnotation {

    protected PdfAnnotation parent;

    public PdfPopupAnnotation(PdfDocument document, Rectangle rect) throws PdfException {
        super(document, rect);
    }

    public PdfPopupAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    public PdfName getSubtype() throws PdfException {
        return PdfName.Popup;
    }

    public PdfDictionary getParentObject() throws PdfException {
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
