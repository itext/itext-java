package com.itextpdf.core.pdf.navigation;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

public class PdfNamedDestination extends PdfDestination<PdfName> {

    public PdfNamedDestination(String name) {
        this(new PdfName(name));
    }

    public PdfNamedDestination(PdfName pdfObject) {
        super(pdfObject);
    }

    public PdfNamedDestination(PdfName pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }
}
