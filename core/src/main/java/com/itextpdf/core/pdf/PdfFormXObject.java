package com.itextpdf.core.pdf;

public class PdfFormXObject extends PdfXObject {

    protected PdfResources resources = new PdfResources();

    public PdfFormXObject(PdfDocument document) {
        this(new PdfStream(document), document);
        pdfObject.put(PdfName.Resources, resources.getPdfObject());
    }

    public PdfFormXObject(PdfStream pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
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
