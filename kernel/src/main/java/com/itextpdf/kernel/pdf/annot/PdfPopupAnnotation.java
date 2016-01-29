package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

public class PdfPopupAnnotation extends PdfAnnotation {

    protected PdfAnnotation parent;

    public PdfPopupAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfPopupAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
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
