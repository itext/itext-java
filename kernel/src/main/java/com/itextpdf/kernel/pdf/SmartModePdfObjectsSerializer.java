package com.itextpdf.kernel.pdf;

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
            ByteBufferOutputStream bb = new ByteBufferOutputStream();
            int level = 100;
            serObject(obj, bb, level, serializedCache);
            content = bb.toByteArray();
        }
        return new SerializedObjectContent(content);
    }

    private void serObject(PdfObject obj, ByteBufferOutputStream bb, int level, Map<PdfIndirectReference, byte[]> serializedCache) {
        if (level <= 0) {
            return;
        }
        if (obj == null) {
            bb.append("$Lnull");
            return;
        }
        PdfIndirectReference reference = null;
        ByteBufferOutputStream savedBb = null;

        if (obj.isIndirectReference()) {
            reference = (PdfIndirectReference) obj;
            byte[] cached = serializedCache.get(reference);
            if (cached != null) {
                bb.append(cached);
                return;
            } else {
                savedBb = bb;
                bb = new ByteBufferOutputStream();
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
            // TODO getBuffer? won't it contain garbage also?
            serializedCache.put(reference, bb.getBuffer());
            savedBb.append(bb);
        }
    }

    private void serDic(PdfDictionary dic, ByteBufferOutputStream bb, int level,
                        Map<PdfIndirectReference, byte[]> serializedCache) {
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

    private void serArray(PdfArray array, ByteBufferOutputStream bb, int level,
                          Map<PdfIndirectReference, byte[]> serializedCache) {
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
}
