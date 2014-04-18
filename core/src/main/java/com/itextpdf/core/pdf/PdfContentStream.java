package com.itextpdf.core.pdf;

import com.itextpdf.core.pdf.objects.PdfStream;

public class PdfContentStream extends PdfStream {

    public PdfContentStream(PdfDocument doc) {
        super(doc);
    }

    public PdfContentStream addBefore() {
        return new PdfContentStream(getPdfDocument());
    }

    public PdfContentStream addAfter() {
        return new PdfContentStream(getPdfDocument());
    }

}
