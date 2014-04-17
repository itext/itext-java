package com.itextpdf.core.pdf;

import com.itextpdf.core.pdf.objects.PdfDictionary;

public class PdfDocumentInfo extends PdfDictionary {

    protected PdfDocumentInfo(PdfDocument doc) {
        super(doc);
    }

    public PdfDocumentInfo setTitle(String title) {
        return this;
    }

    public PdfDocumentInfo setAuthor(String author) {
        return this;
    }

    public PdfDocumentInfo setSubject(String subject) {
        return this;
    }

    public PdfDocumentInfo setKeywords(String keywords) {
        return this;
    }

    public PdfDocumentInfo setCreator(String creator) {
        return this;
    }

}
