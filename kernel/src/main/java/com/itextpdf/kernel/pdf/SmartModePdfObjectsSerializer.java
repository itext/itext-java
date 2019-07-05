/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.kernel.PdfException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

class SmartModePdfObjectsSerializer implements Serializable {

    private static final long serialVersionUID = 2502203520776244051L;

    private transient MessageDigest md5;
    private HashMap<SerializedObjectContent, PdfIndirectReference> serializedContentToObj = new HashMap<>();

    SmartModePdfObjectsSerializer() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }

    public void saveSerializedObject(SerializedObjectContent serializedContent, PdfIndirectReference objectReference) {
        serializedContentToObj.put(serializedContent, objectReference);
    }

    public PdfIndirectReference getSavedSerializedObject(SerializedObjectContent serializedContent) {
        if (serializedContent != null) {
            return serializedContentToObj.get(serializedContent);
        }
        return null;
    }

    public SerializedObjectContent serializeObject(PdfObject obj) {
        if (!obj.isStream() && !obj.isDictionary()) {
            return null;
        }
        PdfIndirectReference indRef = obj.getIndirectReference();
        assert indRef != null;
        Map<PdfIndirectReference, byte[]> serializedCache = indRef.getDocument().serializedObjectsCache;

        byte[] content = serializedCache.get(indRef);
        if (content == null) {
            ByteBuffer bb = new ByteBuffer();
            int level = 100;
            try {
                serObject(obj, bb, level, serializedCache);
            } catch (SelfReferenceException e) {
                return null;
            }
            content = bb.toByteArray();
        }
        return new SerializedObjectContent(content);
    }

    private void serObject(PdfObject obj, ByteBuffer bb, int level, Map<PdfIndirectReference, byte[]> serializedCache) throws SelfReferenceException {
        if (level <= 0) {
            return;
        }
        if (obj == null) {
            bb.append("$Lnull");
            return;
        }
        PdfIndirectReference reference = null;
        ByteBuffer savedBb = null;

        if (obj.isIndirectReference()) {
            reference = (PdfIndirectReference) obj;
            byte[] cached = serializedCache.get(reference);
            if (cached != null) {
                bb.append(cached);
                return;
            } else {

                if (serializedCache.keySet().contains(reference)) {
                    //referencing itself
                    throw new SelfReferenceException();
                }
                serializedCache.put(reference, null);

                savedBb = bb;
                bb = new ByteBuffer();
                obj = reference.getRefersTo();
            }
        }

        if (obj.isStream()) {
            serDic((PdfDictionary) obj, bb, level - 1, serializedCache);
            bb.append("$B");
            if (level > 0) {
                bb.append(md5.digest(((PdfStream) obj).getBytes(false)));
            }
        } else if (obj.isDictionary()) {
            serDic((PdfDictionary) obj, bb, level - 1, serializedCache);
        } else if (obj.isArray()) {
            serArray((PdfArray) obj, bb, level - 1, serializedCache);
        } else if (obj.isString()) {
            bb.append("$S").append(obj.toString()); // TODO specify length for strings, streams, may be names?
        } else if (obj.isName()) {
            bb.append("$N").append(obj.toString());
        } else {
            bb.append("$L").append(obj.toString()); // PdfNull case is also here
        }

        if (savedBb != null) {
            serializedCache.put(reference, bb.toByteArray());
            savedBb.append(bb.getInternalBuffer(), 0, bb.size());
        }
    }

    private void serDic(PdfDictionary dic, ByteBuffer bb, int level,
                        Map<PdfIndirectReference, byte[]> serializedCache) throws SelfReferenceException {
        bb.append("$D");
        if (level <= 0)
            return;
        for (PdfName key : dic.keySet()) {
            if (isKeyRefersBack(dic, key)) {
                continue;
            }
            serObject(key, bb, level, serializedCache);
            serObject(dic.get(key, false), bb, level, serializedCache);

        }
        bb.append("$\\D");
    }

    private void serArray(PdfArray array, ByteBuffer bb, int level,
                          Map<PdfIndirectReference, byte[]> serializedCache) throws SelfReferenceException {
        bb.append("$A");
        if (level <= 0)
            return;
        for (int k = 0; k < array.size(); ++k) {
            serObject(array.get(k, false), bb, level, serializedCache);
        }
        bb.append("$\\A");
    }

    private boolean isKeyRefersBack(PdfDictionary dic, PdfName key) {
        // TODO review this method?
        // ignore recursive call
        return key.equals(PdfName.P) && (dic.get(key).isIndirectReference() || dic.get(key).isDictionary())
                || key.equals(PdfName.Parent);
    }

    private static class SelfReferenceException extends Exception {
    }
}
