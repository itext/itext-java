package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

public class PdfStampAnnotation extends  PdfMarkupAnnotation {

    public PdfStampAnnotation(PdfDocument document, Rectangle rect) throws PdfException {
        super(document, rect);
    }

    public PdfStampAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() throws PdfException {
        return PdfName.Stamp;
    }

    public PdfStampAnnotation setStampName(PdfName name){
        return put (PdfName.Name, name);
    }

    public PdfName getStampName() throws PdfException {
        return getPdfObject().getAsName(PdfName.Name);
    }
}
