package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.geom.Rectangle;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PdfDictionary extends PdfObject {

    private Map<PdfName, PdfObject> map = new TreeMap<>();

    public PdfDictionary() {
        super();
    }

    public PdfDictionary(Map<PdfName, PdfObject> map) {
        this.map.putAll(map);
    }

    public PdfDictionary(Set<Map.Entry<PdfName, PdfObject>> entrySet) {
        for(Map.Entry<PdfName, PdfObject> entry : entrySet) {
            this.map.put(entry.getKey(), entry.getValue());
        }
    }

    public PdfDictionary(PdfDictionary dictionary) {
        map.putAll(dictionary.map);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(PdfName key) {
        return map.containsKey(key);
    }

    public boolean containsValue(PdfObject value) {
        return map.containsValue(value);
    }

    public PdfObject get(PdfName key) {
        return get(key, true);
    }

    public PdfArray getAsArray(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.Array)
            return (PdfArray)direct;
        return null;
    }

    public PdfDictionary getAsDictionary(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.Dictionary)
            return (PdfDictionary)direct;
        return null;
    }

    public PdfStream getAsStream(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.Stream)
            return (PdfStream)direct;
        return null;
    }

    public PdfNumber getAsNumber(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.Number)
            return (PdfNumber)direct;
        return null;
    }

    public PdfName getAsName(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.Name)
            return (PdfName)direct;
        return null;
    }

    public PdfString getAsString(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.String)
            return (PdfString)direct;
        return null;
    }

    public PdfBoolean getAsBoolean(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.Boolean)
            return (PdfBoolean)direct;
        return null;
    }

    public Rectangle getAsRectangle(PdfName key) {
        PdfArray a = getAsArray(key);
        return a == null ? null : a.toRectangle();
    }

    public Float getAsFloat(PdfName key) {
        PdfNumber number = getAsNumber(key);
        return number == null ? null : number.getFloatValue();
    }

    public Integer getAsInt(PdfName key) {
        PdfNumber number = getAsNumber(key);
        return number == null ? null : number.getIntValue();
    }

    public Boolean getAsBool(PdfName key) {
        PdfBoolean b = getAsBoolean(key);
        return b == null ? null : b.getValue();
    }

    public PdfObject put(PdfName key, PdfObject value) {
        return map.put(key, value);
    }

    public PdfObject remove(PdfName key) {
        return map.remove(key);
    }

    public void putAll(PdfDictionary d) {
        map.putAll(d.map);
    }

    public void clear() {
        map.clear();
    }

    public Set<PdfName> keySet() {
        return map.keySet();
    }

    public Collection<PdfObject> values() {
        return map.values();
    }

    public Set<Map.Entry<PdfName, PdfObject>> entrySet() {
        return map.entrySet();
    }

    @Override
    public int getType() {
        return Dictionary;
    }

    @Override
    public String toString() {
        String string = "<<";
        for (Map.Entry<PdfName, PdfObject> entry : entrySet()) {
            PdfIndirectReference indirectReference = entry.getValue().getIndirectReference();
            string = string + entry.getKey().toString() + " " + (indirectReference == null ? entry.getValue().toString() : indirectReference.toString()) + " ";
        }
        string += ">>";
        return string;
    }

    /**
     * Creates clones of the dictionary in the current document.
     * It's possible to pass a list of keys to exclude when cloning.
     *
     * @param excludeKeys list of objects to exclude when cloning dictionary.
     * @return cloned dictionary.
     * @throws PdfException
     */
    public PdfDictionary clone(List<PdfName> excludeKeys) {
        Map<PdfName, PdfObject> excluded = new TreeMap<PdfName, PdfObject>();
        for (PdfName key : excludeKeys) {
            PdfObject obj = map.get(key);
            if (obj != null)
                excluded.put(key, map.remove(key));
        }
        PdfDictionary dictionary = (PdfDictionary) clone();
        map.putAll(excluded);
        return dictionary;
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfDictionary makeIndirect(PdfDocument document) {
        return super.makeIndirect(document);
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfDictionary makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        return super.makeIndirect(document, reference);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document document to copy object to.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfDictionary copyToDocument(PdfDocument document) {
        return super.copyToDocument(document, true);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document         document to copy object to.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will be assigned.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfDictionary copyToDocument(PdfDocument document, boolean allowDuplicating) {
        return super.copyToDocument(document, allowDuplicating);
    }

    /**
     * Copies dictionary to specified document.
     * It's possible to pass a list of keys to exclude when copying.
     *
     * @param document    document to copy dictionary to.
     * @param excludeKeys list of objects to exclude when copying dictionary.
     * @param allowDuplicating {@link PdfObject}
     * @return copied dictionary.
     * @throws PdfException
     */
    public PdfDictionary copyToDocument(PdfDocument document, List<PdfName> excludeKeys, boolean allowDuplicating) {
        Map<PdfName, PdfObject> excluded = new TreeMap<PdfName, PdfObject>();
        for (PdfName key : excludeKeys) {
            PdfObject obj = map.get(key);
            if (obj != null)
                excluded.put(key, map.remove(key));
        }
        PdfDictionary dictionary = copyToDocument(document, allowDuplicating);
        map.putAll(excluded);
        return dictionary;
    }

    /**
     *
     * @param asDirect true is to extract direct object always.
     * @throws PdfException
     */
    public PdfObject get(PdfName key, boolean asDirect) {
        if (!asDirect)
            return map.get(key);
        else {
            PdfObject obj = map.get(key);
            if (obj != null && obj.getType() == IndirectReference)
                return ((PdfIndirectReference)obj).getRefersTo(true);
            else
                return obj;
        }
    }

    /**
     * This method merges different fields from two dictionaries into the current one
     * @param other a dictionary whose fields should be merged into the current dictionary.
     */
    public void mergeDifferent(PdfDictionary other){
        for (PdfName key : other.keySet()){
            if(!containsKey(key))
                put(key, other.get(key));
        }
    }

    @Override
    protected PdfDictionary newInstance() {
        return new PdfDictionary();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfDictionary dictionary = (PdfDictionary) from;
        for (Map.Entry<PdfName, PdfObject> entry : dictionary.entrySet()) {
            map.put(entry.getKey(), entry.getValue().processCopying(document, false));
        }
    }

    /**
     * Release content of PdfDictionary.
     */
    protected void releaseContent() {
        map = null;
    }
}
