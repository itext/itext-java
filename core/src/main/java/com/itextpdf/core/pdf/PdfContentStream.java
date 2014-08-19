package com.itextpdf.core.pdf;

public class PdfContentStream extends PdfStream {

    protected PdfResources resources = null;

    protected PdfContentStream(PdfDocument doc) {
        super(doc);
    }

    public PdfResources getResources() {
        return resources;
    }

    protected void setResources(PdfResources resources) {
        this.resources = resources;
    }
}
