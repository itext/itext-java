package com.itextpdf.core.pdf;

public class PdfFormXObject extends PdfStream implements IPdfXObject {

    public PdfFormXObject(PdfDocument doc) {
        super(doc);
        put(PdfName.Resources, new PdfResources());
    }

    /**
     * Creates form XObject from page content
     *
     * @param page
     */
    public PdfFormXObject(PdfPage page) {
        this(page.getDocument());
    }

    public PdfResources getResources() {
        return (PdfResources)get(PdfName.Resources);
    }

}
