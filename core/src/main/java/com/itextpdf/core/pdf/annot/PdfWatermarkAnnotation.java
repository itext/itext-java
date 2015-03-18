package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

public class PdfWatermarkAnnotation extends PdfAnnotation {

    public PdfWatermarkAnnotation(PdfDocument document, Rectangle rect) throws PdfException {
        super(document, rect);
    }

    public PdfWatermarkAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() throws PdfException {
        return PdfName.Watermark;
    }

    public PdfWatermarkAnnotation setFixedPrint(PdfFixedPrint fixedPrint){
        return put(PdfName.FixedPrint, fixedPrint);
    }

    public PdfDictionary getFixedPrint() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.FixedPrint);
    }
}
