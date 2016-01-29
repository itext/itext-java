package com.itextpdf.core.pdf;

import com.itextpdf.core.PdfException;
import com.itextpdf.core.geom.Rectangle;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * A representation of a Dictionary as described by the PDF Specification. A Dictionary is a mapping between keys
 * and values. Keys are {@link com.itextpdf.core.pdf.PdfName PdfNames} and the values are
 * {@link com.itextpdf.core.pdf.PdfObject PdfObjects}. Each key can only be associated with one value and
 * adding a new value to an existing key will override the previous value. A value of null should be ignored when
 * the PdfDocument is closed.
 */
public class PdfDictionary extends PdfObject {

    private Map<PdfName, PdfObject> map = new TreeMap<>();

    /**
     * Creates a new PdfDictionary instance.
     */
    public PdfDictionary() {
        super();
    }

    /**
     * Creates a new PdfDictionary instance. This constructor inserts the content of the specified Map into this
     * PdfDictionary instance.
     *
     * @param map Map containing values to be inserted into PdfDictionary
     */
    public PdfDictionary(Map<PdfName, PdfObject> map) {
        this.map.putAll(map);
    }

    /**
     * Creates a new PdfDictionary instance. This constructor inserts the content of the specified Set into this
     * PdfDictionary instance.
     *
     * @param entrySet Set containing Map#Entries to be inserted into PdfDictionary
     */
    public PdfDictionary(Set<Map.Entry<PdfName, PdfObject>> entrySet) {
        for(Map.Entry<PdfName, PdfObject> entry : entrySet) {
            this.map.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Creates a new PdfDictionary instance. This constructor inserts the content of the specified PdfDictionary
     * into this PdfDictionary instance.
     *
     * @param dictionary PdfDictionary containing values to be inserted into PdfDictionary
     */
    public PdfDictionary(PdfDictionary dictionary) {
        map.putAll(dictionary.map);
    }

    /**
     * Returns the number of key-value pairs in this PdfDictionary.
     *
     * @return number of key-value pairs
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns true if there are no key-value pairs in this PdfDictionary.
     *
     * @return true if there are no key-value pairs in this PdfDictionary
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns true if this PdfDictionary contains the specified key.
     *
     * @param key the key to check
     * @return true if key is present in the PdfDictionary
     */
    public boolean containsKey(PdfName key) {
        return map.containsKey(key);
    }

    /**
     * Returns true if this PdfDictionary contains the specified value.
     *
     * @param value the value to check
     * @return true if value is present in the PdfDictionary
     */
    public boolean containsValue(PdfObject value) {
        return map.containsValue(value);
    }

    /**
     * Returns the value associated to this key.
     *
     * @param key the key of which the associated value needs to be returned
     * @return the value associated with this key
     */
    public PdfObject get(PdfName key) {
        return get(key, true);
    }

    /**
     * Returns the value associated to this key as a PdfArray. If the value isn't a PdfArray, null is returned.
     *
     * @param key the key of which the associated value needs to be returned
     * @return PdfArray associated with this key
     */
    public PdfArray getAsArray(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.Array)
            return (PdfArray)direct;
        return null;
    }

    /**
     * Returns the value associated to this key as a PdfDictionary. If the value isn't a PdfDictionary, null is returned.
     *
     * @param key the key of which the associated value needs to be returned
     * @return PdfDictionary associated with this key
     */
    public PdfDictionary getAsDictionary(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.Dictionary)
            return (PdfDictionary)direct;
        return null;
    }

    /**
     * Returns the value associated to this key as a PdfStream. If the value isn't a PdfStream, null is returned.
     *
     * @param key the key of which the associated value needs to be returned
     * @return PdfStream associated with this key
     */
    public PdfStream getAsStream(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.Stream)
            return (PdfStream)direct;
        return null;
    }

    /**
     * Returns the value associated to this key as a PdfNumber. If the value isn't a PdfNumber, null is returned.
     *
     * @param key the key of which the associated value needs to be returned
     * @return PdfNumber associated with this key
     */
    public PdfNumber getAsNumber(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.Number)
            return (PdfNumber)direct;
        return null;
    }

    /**
     * Returns the value associated to this key as a PdfName. If the value isn't a PdfName, null is returned.
     *
     * @param key the key of which the associated value needs to be returned
     * @return PdfName associated with this key
     */
    public PdfName getAsName(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.Name)
            return (PdfName)direct;
        return null;
    }

    /**
     * Returns the value associated to this key as a PdfString. If the value isn't a PdfString, null is returned.
     *
     * @param key the key of which the associated value needs to be returned
     * @return PdfString associated with this key
     */
    public PdfString getAsString(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.String)
            return (PdfString)direct;
        return null;
    }

    /**
     * Returns the value associated to this key as a PdfBoolean. If the value isn't a PdfBoolean, null is returned.
     *
     * @param key the key of which the associated value needs to be returned
     * @return PdfBoolean associated with this key
     */
    public PdfBoolean getAsBoolean(PdfName key) {
        PdfObject direct = get(key, true);
        if (direct != null && direct.getType() == PdfObject.Boolean)
            return (PdfBoolean)direct;
        return null;
    }

    /**
     * Returns the value associated to this key as a Rectangle. If the value isn't a PdfArray of which the
     * firt four elements are PdfNumbers, null is returned.
     *
     * @param key the key of which the associated value needs to be returned
     * @return PdfArray associated with this key
     * @see PdfArray#toRectangle()
     */
    public Rectangle getAsRectangle(PdfName key) {
        PdfArray a = getAsArray(key);
        return a == null ? null : a.toRectangle();
    }

    /**
     * Returns the value associated to this key as a Float. If the value isn't a Pdfnumber, null is returned.
     *
     * @param key the key of which the associated value needs to be returned
     * @return Float associated with this key
     */
    public Float getAsFloat(PdfName key) {
        PdfNumber number = getAsNumber(key);
        return number == null ? null : number.getFloatValue();
    }

    /**
     * Returns the value associated to this key as an Integer. If the value isn't a Pdfnumber, null is returned.
     *
     * @param key the key of which the associated value needs to be returned
     * @return Integer associated with this key
     */
    public Integer getAsInt(PdfName key) {
        PdfNumber number = getAsNumber(key);
        return number == null ? null : number.getIntValue();
    }

    /**
     * Returns the value associated to this key as a Boolean. If the value isn't a PdfBoolean, null is returned.
     *
     * @param key the key of which the associated value needs to be returned
     * @return Boolean associated with this key
     */
    public Boolean getAsBool(PdfName key) {
        PdfBoolean b = getAsBoolean(key);
        return b == null ? null : b.getValue();
    }

    /**
     * Inserts the value into this PdfDictionary and associates it with the specified key. If the key is already
     * present in this PdfDictionary, this method will override the old value with the specified one.
     *
     * @param key key to insert or to override
     * @param value the value to associate with the specified key
     * @return the previous PdfObject associated with this key
     */
    public PdfObject put(PdfName key, PdfObject value) {
        return map.put(key, value);
    }

    /**
     * Removes the specified key from this PdfDictionary.
     *
     * @param key key to be removed
     * @return the removed value associated with the specified key
     */
    public PdfObject remove(PdfName key) {
        return map.remove(key);
    }

    /**
     * Inserts all the key-value pairs into this PdfDictionary.
     *
     * @param d PdfDictionary holding the key-value pairs to be copied
     */
    public void putAll(PdfDictionary d) {
        map.putAll(d.map);
    }

    /**
     * Removes all key-value pairs from this PdfDictionary.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Returns all the keys of this PdfDictionary as a Set.
     *
     * @return Set of keys
     */
    public Set<PdfName> keySet() {
        return map.keySet();
    }

    /**
     * Returns all the values of this map in a Collection.
     *
     * @return a Collection holding all the values
     */
    public Collection<PdfObject> values() {
        return map.values();
    }

    /**
     * Returns a Set holding the key-value pairs as Map#Entry objects.
     *
     * @return a Set of Map.Entry objects
     */
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
        Map<PdfName, PdfObject> excluded = new TreeMap<>();
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
        Map<PdfName, PdfObject> excluded = new TreeMap<>();
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
