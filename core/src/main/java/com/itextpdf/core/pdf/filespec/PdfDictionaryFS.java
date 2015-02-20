package com.itextpdf.core.pdf.filespec;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfObjectWrapper;

public class PdfDictionaryFS extends PdfFileSpec<PdfDictionary> {

    public PdfDictionaryFS(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfDictionaryFS(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }
}
