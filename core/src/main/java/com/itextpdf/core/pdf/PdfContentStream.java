package com.itextpdf.core.pdf;

import com.itextpdf.core.pdf.objects.PdfStream;

public class PdfContentStream extends PdfStream {

    public PdfContentStream(PdfDocument doc) {
        super(doc);
    }

    /**
     * Adds new content stream before the current one.
     *
     * @return newly added stream.
     */
    public PdfContentStream addBefore() {
        return new PdfContentStream(getPdfDocument());
    }

    /**
     * Adds new content stream after the current one.
     *
     * @return newly added stream.
     */
    public PdfContentStream addAfter() {
        return new PdfContentStream(getPdfDocument());
    }

}
