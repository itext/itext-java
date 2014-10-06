package com.itextpdf.core.pdf;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PdfDictionary extends PdfObject implements Map<PdfName, PdfObject> {

    protected Map<PdfName, PdfObject> map = new TreeMap<PdfName, PdfObject>();

    public PdfDictionary() {
        super();
    }

    public PdfDictionary(Map<PdfName, PdfObject> map) {
        for (Entry<PdfName, PdfObject> entry : map.entrySet())
            this.map.put(entry.getKey(), entry.getValue()) ;
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

    @Override
    public byte getType() {
        return Dictionary;
    }

    @Override
    public String toString() {
        String string = "<<";
        for (Entry<PdfName, PdfObject> entry : entrySet()) {
            PdfIndirectReference indirectReference = entry.getValue().getIndirectReference();
            string = string + entry.getKey().toString() + " " + (indirectReference == null ? entry.getValue().toString() : indirectReference.toString()) + " ";
        }
        string += ">>";
        return string;
    }

    @Override
    protected PdfDictionary newInstance() {
        return new PdfDictionary();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        PdfDictionary dictionary = (PdfDictionary)from;
        for (Entry<PdfName, PdfObject> entry : dictionary.entrySet()) {
            map.put(entry.getKey(), entry.getValue().copy(document));
        }
    }
}
