package com.itextpdf.core.pdf;

public class PdfFormXObject extends PdfObjectWrapper<PdfStream> implements IPdfXObject {

    public PdfFormXObject(PdfStream pdfObject) {
        super(pdfObject);
    }

    public PdfFormXObject(PdfDocument doc) {
        this(new PdfStream(doc));
        pdfObject.put(PdfName.Resources, new PdfResources());
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
        return (PdfResources)pdfObject.get(PdfName.Resources);
    }

}
