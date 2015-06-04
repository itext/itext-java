package com.itextpdf.core.pdf.filespec;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;

public class PdfDictionaryFS extends PdfFileSpec<PdfDictionary> {

    public PdfDictionaryFS(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfDictionaryFS(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }
}
