package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.PdfEncodings;

import java.util.HashMap;
import java.util.Map;

public class PdfDocumentInfo extends PdfObjectWrapper<PdfDictionary> {

    public PdfDocumentInfo(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject == null ? new PdfDictionary() : pdfObject);
        if (pdfDocument.getWriter() != null) {
            this.pdfObject.makeIndirect(pdfDocument);
        }
    }

    public PdfDocumentInfo(PdfDictionary pdfObject) throws PdfException {
        this(pdfObject, null);
    }

    public PdfDocumentInfo(PdfDocument pdfDocument) throws PdfException {
        this(new PdfDictionary(), pdfDocument);
    }

    public PdfDocumentInfo setTitle(String title) {
        pdfObject.put(PdfName.Title, new PdfString(title, PdfEncodings.TEXT_UNICODE));
        return this;
    }

    public PdfDocumentInfo setAuthor(String author) {
        pdfObject.put(PdfName.Author, new PdfString(author, PdfEncodings.TEXT_UNICODE));
        return this;
    }

    public PdfDocumentInfo setSubject(String subject) {
        pdfObject.put(PdfName.Subject, new PdfString(subject, PdfEncodings.TEXT_UNICODE));
        return this;
    }

    public PdfDocumentInfo setKeywords(String keywords) {
        pdfObject.put(PdfName.Keywords, new PdfString(keywords, PdfEncodings.TEXT_UNICODE));
        return this;
    }

    public PdfDocumentInfo setCreator(String creator) {
        pdfObject.put(PdfName.Creator, new PdfString(creator, PdfEncodings.TEXT_UNICODE));
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

    public PdfDocumentInfo addCreationDate() {
        this.getPdfObject().put(PdfName.CreationDate, new PdfDate().getPdfObject());
        return this;
    }

    public PdfDocumentInfo addModDate() {
        this.getPdfObject().put(PdfName.ModDate, new PdfDate().getPdfObject());
        return this;
    }

    public void setMoreInfo(HashMap<String, String> moreInfo) {
        if (moreInfo != null) {
            for (Map.Entry<String, String> entry : moreInfo.entrySet()) {
                String key = entry.getKey();
                PdfName keyName = new PdfName(key);
                String value = entry.getValue();
                if (value == null) {
                    getPdfObject().remove(keyName);
                } else {
                    getPdfObject().put(keyName, new PdfString(value, PdfEncodings.TEXT_UNICODE));
                }
            }
        }
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
