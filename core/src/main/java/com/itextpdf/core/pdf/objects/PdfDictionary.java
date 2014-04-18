package com.itextpdf.core.pdf.objects;

import com.itextpdf.core.pdf.PdfDocument;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PdfDictionary extends PdfObject implements Map<PdfName, PdfObject> {

    protected HashMap<PdfName, PdfObject> map = new HashMap<PdfName, PdfObject>();

    public PdfDictionary() {
        super();
    }

    public PdfDictionary(PdfDocument doc) {
        super(doc);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public PdfObject get(Object key) {
        return map.get(key);
    }

    @Override
    public PdfObject put(PdfName key, PdfObject value) {
        return map.put(key, value);
    }

    @Override
    public PdfObject remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends PdfName, ? extends PdfObject> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<PdfName> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<PdfObject> values() {
        return map.values();
    }

    @Override
    public Set<Entry<PdfName, PdfObject>> entrySet() {
        return map.entrySet();
    }
}
