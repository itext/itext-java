package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

abstract public class PdfDeviceCs extends PdfColorSpace<PdfName> {

    public PdfDeviceCs(PdfName pdfObject) {
        super(pdfObject);
    }

    public PdfDeviceCs(PdfName pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }
}
