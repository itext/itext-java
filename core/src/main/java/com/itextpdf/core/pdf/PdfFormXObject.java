package com.itextpdf.core.pdf;

public class PdfFormXObject extends PdfContentStream implements IPdfXObject {

    public PdfFormXObject(PdfDocument doc) {
        super(doc);
        setResources(new PdfResources());
        put(PdfName.Resources, getResources());
    }

    /**
     * Creates form XObject from page content
     *
     * @param page
     */
    public PdfFormXObject(PdfPage page) {
        this(page.getDocument());
    }

}
