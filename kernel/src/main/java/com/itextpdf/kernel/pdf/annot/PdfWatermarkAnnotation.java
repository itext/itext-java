package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

public class PdfWatermarkAnnotation extends PdfAnnotation {

    public PdfWatermarkAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfWatermarkAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
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
