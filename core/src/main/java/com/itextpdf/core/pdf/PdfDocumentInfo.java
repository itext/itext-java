package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;

public class PdfDocumentInfo extends PdfObjectWrapper<PdfDictionary> {

    public PdfDocumentInfo(PdfDictionary pdfObject) {
        super(pdfObject == null ? new PdfDictionary() : pdfObject);
    }

    public PdfDocumentInfo(PdfDocument pdfDocument) {
        super(new PdfDictionary(), pdfDocument);
    }

    public PdfDocumentInfo(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        this(pdfObject);
        this.pdfObject.makeIndirect(pdfDocument);
    }

    public PdfDocumentInfo setTitle(String title) {
        pdfObject.put(PdfName.Title, new PdfString(title));
        return this;
    }

    public PdfDocumentInfo setAuthor(String author) {
        pdfObject.put(PdfName.Author, new PdfString(author));
        return this;
    }

    public PdfDocumentInfo setSubject(String subject) {
        pdfObject.put(PdfName.Subject, new PdfString(subject));
        return this;
    }

    public PdfDocumentInfo setKeywords(String keywords) {
        pdfObject.put(PdfName.Keywords, new PdfString(keywords));
        return this;
    }

    public PdfDocumentInfo setCreator(String creator) {
        pdfObject.put(PdfName.Creator, new PdfString(creator));
        return this;
    }

    public String getTitle() throws PdfException {
        return getStringValue(PdfName.Title);
    }

    public String getAuthor() throws PdfException {
        return getStringValue(PdfName.Author);
    }

    public String getSubject() throws PdfException {
        return getStringValue(PdfName.Subject);
    }

    public String getKeywords() throws PdfException {
        return getStringValue(PdfName.Keywords);
    }

    public String getCreator() throws PdfException {
        return getStringValue(PdfName.Creator);
    }

    @Override
    public void flush() throws PdfException {
        pdfObject.flush(false);
    }

    private String getStringValue(PdfName name) throws PdfException {
        PdfString pdfString = pdfObject.getAsString(name);
        return pdfString != null ? pdfString.getValue() : null;
    }
}
