package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

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
