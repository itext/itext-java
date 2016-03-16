package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.PdfEncodings;

import java.util.Map;

public class PdfDocumentInfo extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -21957940280527123L;

	public PdfDocumentInfo(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject == null ? new PdfDictionary() : pdfObject);
        if (pdfDocument.getWriter() != null) {
            this.getPdfObject().makeIndirect(pdfDocument);
        }
        setForbidRelease();
    }

    public PdfDocumentInfo(PdfDictionary pdfObject) {
        this(pdfObject, null);
    }

    public PdfDocumentInfo(PdfDocument pdfDocument) {
        this(new PdfDictionary(), pdfDocument);
    }

    public PdfDocumentInfo setTitle(String title) {
        getPdfObject().put(PdfName.Title, new PdfString(title));
        return this;
    }

    public PdfDocumentInfo setAuthor(String author) {
        getPdfObject().put(PdfName.Author, new PdfString(author));
        return this;
    }

    public PdfDocumentInfo setSubject(String subject) {
        getPdfObject().put(PdfName.Subject, new PdfString(subject));
        return this;
    }

    public PdfDocumentInfo setKeywords(String keywords) {
        getPdfObject().put(PdfName.Keywords, new PdfString(keywords));
        return this;
    }

    public PdfDocumentInfo setCreator(String creator) {
        getPdfObject().put(PdfName.Creator, new PdfString(creator));
        return this;
    }

    public PdfDocumentInfo setProducer(String creator) {
        getPdfObject().put(PdfName.Producer, new PdfString(creator));
        return this;
    }

    public String getTitle() {
        return getStringValue(PdfName.Title);
    }

    public String getAuthor() {
        return getStringValue(PdfName.Author);
    }

    public String getSubject() {
        return getStringValue(PdfName.Subject);
    }

    public String getKeywords() {
        return getStringValue(PdfName.Keywords);
    }

    public String getCreator() {
        return getStringValue(PdfName.Creator);
    }

    public String getProducer() {
        return getStringValue(PdfName.Producer);
    }

    public PdfDocumentInfo addCreationDate() {
        this.getPdfObject().put(PdfName.CreationDate, new PdfDate().getPdfObject());
        return this;
    }

    public PdfDocumentInfo addModDate() {
        this.getPdfObject().put(PdfName.ModDate, new PdfDate().getPdfObject());
        return this;
    }

    public void setMoreInfo(Map<String, String> moreInfo) {
        if (moreInfo != null) {
            for (Map.Entry<String, String> entry : moreInfo.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                setMoreInfo(key, value);
            }
        }
    }

    public void setMoreInfo(String key, String value) {
        PdfName keyName = new PdfName(key);
        if (value == null) {
            getPdfObject().remove(keyName);
        } else {
            getPdfObject().put(keyName, new PdfString(value, PdfEncodings.UnicodeBig));
        }
    }

    @Override
    public void flush() {
        getPdfObject().flush(false);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    private String getStringValue(PdfName name) {
        PdfString pdfString = getPdfObject().getAsString(name);
        return pdfString != null ? pdfString.getValue() : null;
    }
}
