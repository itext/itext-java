/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.PdfEncodings;

import java.io.Serializable;
import java.util.Map;

public class PdfDocumentInfo implements Serializable {

    static final PdfName PDF20_DEPRECATED_KEYS[] = new PdfName[] {PdfName.Title, PdfName.Author, PdfName.Subject, PdfName.Keywords,
            PdfName.Creator, PdfName.Producer, PdfName.Trapped};

    private static final long serialVersionUID = -21957940280527125L;

    private PdfDictionary infoDictionary;

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

    /**
     * Create a default, empty PdfDocumentInfo and link it to the passed PdfDocument
     *
     * @param pdfDocument document the info will belong to
     */
    PdfDocumentInfo(PdfDocument pdfDocument) {
        this(new PdfDictionary(), pdfDocument);
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
