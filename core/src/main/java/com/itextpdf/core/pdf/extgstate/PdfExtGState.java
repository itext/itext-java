package com.itextpdf.core.pdf.extgstate;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfObjectWrapper;

public class PdfExtGState extends PdfObjectWrapper<PdfDictionary> {

    public PdfExtGState(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfExtGState(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }
}
