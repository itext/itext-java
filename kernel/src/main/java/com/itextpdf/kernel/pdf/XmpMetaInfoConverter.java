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
import com.itextpdf.kernel.xmp.PdfConst;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.options.PropertyOptions;
import com.itextpdf.kernel.xmp.properties.XMPProperty;

class XmpMetaInfoConverter {

    private XmpMetaInfoConverter() {
    }

    static void appendMetadataToInfo(XMPMeta meta, PdfDocumentInfo info) {
        if (meta != null) {
            try {
                XMPProperty title = meta.getLocalizedText(XMPConst.NS_DC, PdfConst.Title, XMPConst.X_DEFAULT, XMPConst.X_DEFAULT);
                if (title != null) {
                    info.setTitle(title.getValue());
                }

                String author = fetchArrayIntoString(meta, XMPConst.NS_DC, PdfConst.Creator);
                if (author != null) {
                    info.setAuthor(author);
                }

                // We assume that pdf:keywords has precedence over dc:subject
                XMPProperty keywords = meta.getProperty(XMPConst.NS_PDF, PdfConst.Keywords);
                if (keywords != null) {
                    info.setKeywords(keywords.getValue());
                } else {
                    String keywordsStr = fetchArrayIntoString(meta, XMPConst.NS_DC, PdfConst.Subject);
                    if (keywordsStr != null) {
                        info.setKeywords(keywordsStr);
                    }
                }

                XMPProperty subject = meta.getLocalizedText(XMPConst.NS_DC, PdfConst.Description, XMPConst.X_DEFAULT, XMPConst.X_DEFAULT);
                if (subject != null) {
                    info.setSubject(subject.getValue());
                }

                XMPProperty creator = meta.getProperty(XMPConst.NS_XMP, PdfConst.CreatorTool);
                if (creator != null) {
                    info.setCreator(creator.getValue());
                }

                XMPProperty producer = meta.getProperty(XMPConst.NS_PDF, PdfConst.Producer);
                if (producer != null) {
                    info.put(PdfName.Producer, new PdfString(producer.getValue(), PdfEncodings.UNICODE_BIG));
                }

                XMPProperty trapped = meta.getProperty(XMPConst.NS_PDF, PdfConst.Trapped);
                if (trapped != null) {
                    info.setTrapped(new PdfName(trapped.getValue()));
                }
            } catch (XMPException ignored) {
            }

        }
    }

    static void appendDocumentInfoToMetadata(PdfDocumentInfo info, XMPMeta xmpMeta) throws XMPException {
        PdfDictionary docInfo = info.getPdfObject();
        if (docInfo != null) {
            PdfName key;
            PdfObject obj;
            String value;
            for (PdfName pdfName : docInfo.keySet()) {
                key = pdfName;
                obj = docInfo.get(key);
                if (obj == null)
                    continue;
                if (obj.isString()) {
                    value = ((PdfString) obj).toUnicodeString();
                } else if (obj.isName()) {
                    value = ((PdfName)obj).getValue();
                } else {
                    continue;
                }
                if (PdfName.Title.equals(key)) {
                    xmpMeta.setLocalizedText(XMPConst.NS_DC, PdfConst.Title, XMPConst.X_DEFAULT, XMPConst.X_DEFAULT, value);
                } else if (PdfName.Author.equals(key)) {
                    for (String v : value.split(",|;")) {
                        if (v.trim().length() > 0) {
                            appendArrayItemIfDoesNotExist(xmpMeta, XMPConst.NS_DC, PdfConst.Creator, v.trim(), PropertyOptions.ARRAY_ORDERED);
                        }
                    }
                } else if (PdfName.Subject.equals(key)) {
                    xmpMeta.setLocalizedText(XMPConst.NS_DC, PdfConst.Description, XMPConst.X_DEFAULT, XMPConst.X_DEFAULT, value);
                } else if (PdfName.Keywords.equals(key)) {
                    for (String v : value.split(",|;")) {
                        if (v.trim().length() > 0) {
                            appendArrayItemIfDoesNotExist(xmpMeta, XMPConst.NS_DC, PdfConst.Subject, v.trim(), PropertyOptions.ARRAY);
                        }
                    }
                    xmpMeta.setProperty(XMPConst.NS_PDF, PdfConst.Keywords, value);
                } else if (PdfName.Creator.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_XMP, PdfConst.CreatorTool, value);
                } else if (PdfName.Producer.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_PDF, PdfConst.Producer, value);
                } else if (PdfName.CreationDate.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_XMP, PdfConst.CreateDate, PdfDate.getW3CDate(value));
                } else if (PdfName.ModDate.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_XMP, PdfConst.ModifyDate, PdfDate.getW3CDate(value));
                } else if (PdfName.Trapped.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_PDF, PdfConst.Trapped, value);
                }
            }
        }
    }

    private static void appendArrayItemIfDoesNotExist(XMPMeta meta, String ns, String arrayName, String value, int arrayOption) throws XMPException {
        int currentCnt = meta.countArrayItems(ns, arrayName);
        for (int i = 0; i < currentCnt; i++) {
            XMPProperty item = meta.getArrayItem(ns, arrayName, i + 1);
            if (value.equals(item.getValue())) {
                return;
            }
        }
        meta.appendArrayItem(ns, arrayName, new PropertyOptions(arrayOption), value, null);
    }

    private static String fetchArrayIntoString(XMPMeta meta, String ns, String arrayName) throws XMPException {
        int keywordsCnt = meta.countArrayItems(ns, arrayName);
        StringBuilder sb = null;
        for (int i = 0; i < keywordsCnt; i++) {
            XMPProperty curKeyword = meta.getArrayItem(ns, arrayName, i + 1);
            if (sb == null) {
                sb = new StringBuilder();
            } else if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(curKeyword.getValue());
        }
        return sb != null ? sb.toString() : null;
    }

}
