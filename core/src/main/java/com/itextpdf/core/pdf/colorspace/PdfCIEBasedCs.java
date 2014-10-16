package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfObjectWrapper;

abstract public class PdfCIEBasedCs extends PdfObjectWrapper<PdfArray> {

    public PdfCIEBasedCs(PdfArray pdfObject) {
        super(pdfObject);
    }

    public PdfCIEBasedCs(PdfArray pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }
}
