package com.itextpdf.core.pdf;

public class PdfFormXObject extends PdfContentStream implements IPdfXObject {

    public PdfFormXObject(PdfDocument doc) {
        super(doc);
    }

    /**
     * Creates form XObject from page content
     *
     * @param page
     */
    public PdfFormXObject(PdfPage page) {
        this(page.getPdfDocument());
    }

}
