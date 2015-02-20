package com.itextpdf.core.pdf.navigation;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfObjectWrapper;

public class PdfDestination<T extends PdfObject> extends PdfObjectWrapper<T> {

    public PdfDestination(T pdfObject) {
        super(pdfObject);
    }

    public PdfDestination(T pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }
}
