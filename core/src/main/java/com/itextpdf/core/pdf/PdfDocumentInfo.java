package com.itextpdf.core.pdf;

public class PdfDocumentInfo extends PdfDictionary {

    protected PdfDocumentInfo(PdfDocument doc) {
        super();
        makeIndirect(doc);
    }

    public PdfDocumentInfo setTitle(String title) {
        put(PdfName.Title, new PdfString(title));
        return this;
    }

    public PdfDocumentInfo setAuthor(String author) {
        put(PdfName.Author, new PdfString(author));
        return this;
    }

    public PdfDocumentInfo setSubject(String subject) {
        put(PdfName.Subject, new PdfString(subject));
        return this;
    }

    public PdfDocumentInfo setKeywords(String keywords) {
        put(PdfName.Keywords, new PdfString(keywords));
        return this;
    }

    public PdfDocumentInfo setCreator(String creator) {
        put(PdfName.Creator, new PdfString(creator));
        return this;
    }

}
