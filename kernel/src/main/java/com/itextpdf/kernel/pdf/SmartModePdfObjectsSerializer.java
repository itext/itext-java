/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.kernel.exceptions.PdfException;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

class SmartModePdfObjectsSerializer {
    private MessageDigest sha512;
    private HashMap<SerializedObjectContent, PdfIndirectReference> serializedContentToObj = new HashMap<>();

    SmartModePdfObjectsSerializer() {
        try {
            sha512 = MessageDigest.getInstance("SHA-512");
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
                bb.append(sha512.digest(((PdfStream) obj).getBytes(false)));
            }
        } else if (obj.isDictionary()) {
            serDic((PdfDictionary) obj, bb, level - 1, serializedCache);
        } else if (obj.isArray()) {
            serArray((PdfArray) obj, bb, level - 1, serializedCache);
        } else if (obj.isString()) {
            bb.append("$S").append(obj.toString());
        } else if (obj.isName()) {
            bb.append("$N").append(obj.toString());
        } else {
            // PdfNull case is also here
            bb.append("$L").append(obj.toString());
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
        // ignore recursive call
        return key.equals(PdfName.P) && (dic.get(key).isIndirectReference() || dic.get(key).isDictionary())
                || key.equals(PdfName.Parent);
    }

    private static class SelfReferenceException extends Exception {
    }
}
