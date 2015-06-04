package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

public class PdfWatermarkAnnotation extends PdfAnnotation {

    public PdfWatermarkAnnotation(PdfDocument document, Rectangle rect) {
        super(document, rect);
    }

    public PdfWatermarkAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Watermark;
    }

    public PdfWatermarkAnnotation setFixedPrint(PdfFixedPrint fixedPrint){
        return put(PdfName.FixedPrint, fixedPrint);
    }

    public PdfDictionary getFixedPrint() {
        return getPdfObject().getAsDictionary(PdfName.FixedPrint);
    }
}
