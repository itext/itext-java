package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;

public class PdfStampAnnotation extends  PdfMarkupAnnotation {

    public PdfStampAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfStampAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
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
