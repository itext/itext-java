/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.PdfEncodings;

import java.util.Map;

public class PdfDocumentInfo {

    static final PdfName[] PDF20_DEPRECATED_KEYS = new PdfName[] {PdfName.Title, PdfName.Author, PdfName.Subject,
            PdfName.Keywords, PdfName.Creator, PdfName.Producer, PdfName.Trapped};


    private final PdfDictionary infoDictionary;

    /**
     * Create a PdfDocumentInfo based on the passed PdfDictionary.
     *
     * @param pdfObject PdfDictionary containing PdfDocumentInfo
     */
    PdfDocumentInfo(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        infoDictionary = pdfObject;
        if (pdfDocument.getWriter() != null) {
            infoDictionary.makeIndirect(pdfDocument);
        }
    }

    public PdfDocumentInfo setTitle(String title) {
        return put(PdfName.Title, new PdfString(title, PdfEncodings.UNICODE_BIG));
    }

    public PdfDocumentInfo setAuthor(String author) {
        return put(PdfName.Author, new PdfString(author, PdfEncodings.UNICODE_BIG));
    }

    public PdfDocumentInfo setSubject(String subject) {
        return put(PdfName.Subject, new PdfString(subject, PdfEncodings.UNICODE_BIG));
    }

    public PdfDocumentInfo setKeywords(String keywords) {
        return put(PdfName.Keywords, new PdfString(keywords, PdfEncodings.UNICODE_BIG));
    }

    public PdfDocumentInfo setCreator(String creator) {
        return put(PdfName.Creator, new PdfString(creator, PdfEncodings.UNICODE_BIG));
    }

    /**
     * Sets a producer line for the {@link PdfDocument} described by this instance.
     *
     * @param producer is a new producer line to set
     * @return this instance
     */
    public PdfDocumentInfo setProducer(String producer) {
        getPdfObject().put(PdfName.Producer, new PdfString(producer, PdfEncodings.UNICODE_BIG));
        return this;
    }

    public PdfDocumentInfo setTrapped(PdfName trapped) {
        return put(PdfName.Trapped, trapped);
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

    public PdfName getTrapped() {
        return infoDictionary.getAsName(PdfName.Trapped);
    }

    public PdfDocumentInfo addCreationDate() {
        return put(PdfName.CreationDate, new PdfDate().getPdfObject());
    }

    /**
     * Remove creation date from the document info dictionary.
     *
     * @return this instance.
     */
    public PdfDocumentInfo removeCreationDate() {
        infoDictionary.remove(PdfName.CreationDate);
        return this;
    }

    public PdfDocumentInfo addModDate() {
        return put(PdfName.ModDate, new PdfDate().getPdfObject());
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
            infoDictionary.remove(keyName);
            infoDictionary.setModified();
        } else {
            put(keyName, new PdfString(value, PdfEncodings.UNICODE_BIG));
        }
    }

    public String getMoreInfo(String key) {
        return getStringValue(new PdfName(key));
    }

    PdfDictionary getPdfObject() {
        return infoDictionary;
    }

    PdfDocumentInfo put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        getPdfObject().setModified();
        return this;
    }

    private String getStringValue(PdfName name) {
        PdfString pdfString = infoDictionary.getAsString(name);
        return pdfString != null ? pdfString.toUnicodeString() : null;
    }

}
