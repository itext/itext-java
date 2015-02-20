package com.itextpdf.core.pdf.navigation;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfString;

public class PdfStringDestination extends PdfDestination<PdfString> {

    public PdfStringDestination(String string) {
        this(new PdfString(string));
    }

    public PdfStringDestination(PdfString pdfObject) {
        super(pdfObject);
    }

    public PdfStringDestination(PdfString pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }
}
