package com.itextpdf.core.pdf;

public class PdfFormXObject extends PdfObjectWrapper<PdfStream> implements IPdfXObject {

    protected PdfResources resources = new PdfResources();

    public PdfFormXObject(PdfStream pdfObject) {
        super(pdfObject);
    }

    public PdfFormXObject(PdfDocument doc) {
        this(new PdfStream(doc));
        pdfObject.put(PdfName.Resources, resources.getPdfObject());
    }

    /**
     * Creates form XObject from page content.
     *
     * @param page
     */
    public PdfFormXObject(PdfPage page) {
        this(page.getDocument());
    }

    public PdfResources getResources() {
        return resources;
    }

}
