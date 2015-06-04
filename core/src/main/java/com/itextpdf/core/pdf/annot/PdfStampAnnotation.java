package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

public class PdfStampAnnotation extends  PdfMarkupAnnotation {

    public PdfStampAnnotation(PdfDocument document, Rectangle rect) {
        super(document, rect);
    }

    public PdfStampAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Stamp;
    }

    public PdfStampAnnotation setStampName(PdfName name){
        return put (PdfName.Name, name);
    }

    public PdfName getStampName() {
        return getPdfObject().getAsName(PdfName.Name);
    }
}
