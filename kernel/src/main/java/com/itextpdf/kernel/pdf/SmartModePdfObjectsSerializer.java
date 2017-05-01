package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class SmartModePdfObjectsSerializer {

    private MessageDigest md5;
    private HashMap<SerializedObjectContent, PdfIndirectReference> serializedContentToObj = new HashMap<>();

    SmartModePdfObjectsSerializer() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }

    public void saveSerializedObject(SerializedObjectContent objectKey, PdfIndirectReference reference) {
        serializedContentToObj.put(objectKey, reference);
    }

    public PdfIndirectReference getSavedSerializedObject(SerializedObjectContent serializedContent) {
        if (serializedContent != null) {
            return serializedContentToObj.get(serializedContent);
        } else {
            return null;
        }
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
        PdfDocument.IndirectRefDescription indRefKey = null;

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
                md5.reset();
                bb.append(md5.digest(((PdfStream) obj).getBytes(false)));
            }
        } else if (obj.isDictionary()) {
            serDic((PdfDictionary) obj, bb, level - 1, serializedCache);
        } else if (obj.isArray()) {
            serArray((PdfArray) obj, bb, level - 1, serializedCache);
        } else if (obj.isString()) {
            bb.append("$S").append(obj.toString());
        } else if (obj.isName()) {
            bb.append("$N").append(obj.toString());
        } else
            bb.append("$L").append(obj.toString()); // PdfNull case is also here

        if (savedBb != null) {
            serializedCache.put(reference, bb.getBuffer());
            savedBb.append(bb);
        }
    }

    private void serDic(PdfDictionary dic, ByteBufferOutputStream bb, int level,
                        Map<PdfIndirectReference, byte[]> serializedCache) {
        bb.append("$D");
        if (level <= 0)
            return;
        PdfName[] keys = new PdfName[dic.keySet().size()];
        keys = dic.keySet().toArray(keys);
        Arrays.sort(keys);
        for (Object key : keys) {
            if (key.equals(PdfName.P) && (dic.get((PdfName) key).isIndirectReference()
                    || dic.get((PdfName) key).isDictionary()) || key.equals(PdfName.Parent)) {// ignore recursive call
                continue;
            }
            serObject((PdfObject) key, bb, level, serializedCache);
            serObject(dic.get((PdfName) key, false), bb, level, serializedCache);

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
}
