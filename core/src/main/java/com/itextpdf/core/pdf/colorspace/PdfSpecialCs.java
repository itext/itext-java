package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfObjectWrapper;

abstract public class PdfSpecialCs extends PdfObjectWrapper<PdfArray> {

    public PdfSpecialCs(PdfArray pdfObject) {
        super(pdfObject);
    }

    public PdfSpecialCs(PdfArray pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }
}
