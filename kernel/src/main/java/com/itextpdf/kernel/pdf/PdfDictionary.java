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

import com.itextpdf.kernel.geom.Rectangle;

import java.util.*;

/**
 * A representation of a Dictionary as described by the PDF Specification. A Dictionary is a mapping between keys
 * and values. Keys are {@link com.itextpdf.kernel.pdf.PdfName PdfNames} and the values are
 * {@link com.itextpdf.kernel.pdf.PdfObject PdfObjects}. Each key can only be associated with one value and
 * adding a new value to an existing key will override the previous value. A value of null should be ignored when
 * the PdfDocument is closed.
 */
public class PdfDictionary extends PdfObject {

    private static final long serialVersionUID = -1122075818690871644L;
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
        return map.size() == 0;
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
        return map.values().contains(value);
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
        if (direct != null && direct.getType() == PdfObject.ARRAY)
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
        if (direct != null && direct.getType() == PdfObject.DICTIONARY)
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
        if (direct != null && direct.getType() == PdfObject.STREAM)
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
        if (direct != null && direct.getType() == PdfObject.NUMBER)
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
        if (direct != null && direct.getType() == PdfObject.NAME)
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
        if (direct != null && direct.getType() == PdfObject.STRING)
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
        if (direct != null && direct.getType() == PdfObject.BOOLEAN)
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
        Float floatNumber = null;
        if (number != null) {
            floatNumber = number.floatValue();
        }
        return floatNumber;
    }

    /**
     * Returns the value associated to this key as an Integer. If the value isn't a Pdfnumber, null is returned.
     *
     * @param key the key of which the associated value needs to be returned
     * @return Integer associated with this key
     */
    public Integer getAsInt(PdfName key) {
        PdfNumber number = getAsNumber(key);
        Integer intNumber = null;
        if (number != null) {
            intNumber = number.intValue();
        }
        return intNumber;
    }

    /**
     * Returns the value associated to this key as a Boolean. If the value isn't a PdfBoolean, null is returned.
     *
     * @param key the key of which the associated value needs to be returned
     * @return Boolean associated with this key
     */
    public Boolean getAsBool(PdfName key) {
        PdfBoolean b = getAsBoolean(key);
        Boolean booleanValue = null;
        if (b != null) {
            booleanValue = b.getValue();
        }

        return booleanValue;
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
        assert value != null;
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
     * @param asDirects if false, collection will contain {@link PdfIndirectReference} instances
     * for the indirect objects in dictionary, otherwise it will contain collection of direct objects.
     * @return a Collection holding all the values
     */
    public Collection<PdfObject> values(boolean asDirects) {
        if (asDirects) {
            return values();
        } else {
            return map.values();
        }
    }

    /**
     * Returns all the values of this map in a Collection.
     * <br>
     * <b>NOTE:</b> since 7.0.1 it returns collection of direct objects.
     * If you want to get {@link PdfIndirectReference} instances for the indirect objects value,
     * you shall use {@link #values(boolean)} method.
     *
     * @return a Collection holding all the values
     */
    public Collection<PdfObject> values() {
        return new PdfDictionaryValues(map.values());
    }

    /**
     * Returns a Set holding the key-value pairs as Map#Entry objects.
     * <br>
     * <b>NOTE:</b> since 7.0.1 it returns collection of direct objects.
     * If you want to get {@link PdfIndirectReference} instances for the indirect objects value,
     * you shall use {@link #get(PdfName, boolean)} method.
     *
     * @return a Set of Map.Entry objects
     */
    public Set<Map.Entry<PdfName, PdfObject>> entrySet() {
        return new PdfDictionaryEntrySet(map.entrySet());
    }

    @Override
    public byte getType() {
        return DICTIONARY;
    }

    @Override
    public String toString() {
        if (!isFlushed()) {
            String string = "<<";
            for (Map.Entry<PdfName, PdfObject> entry : map.entrySet()) {
                PdfIndirectReference indirectReference = entry.getValue().getIndirectReference();
                string = string + entry.getKey().toString() + " " + (indirectReference == null ? entry.getValue().toString() : indirectReference.toString()) + " ";
            }
            string += ">>";
            return string;
        } else {
            return indirectReference.toString();
        }
    }

    /**
     * Creates clones of the dictionary in the current document.
     * It's possible to pass a list of keys to exclude when cloning.
     *
     * @param excludeKeys list of objects to exclude when cloning dictionary.
     * @return cloned dictionary.
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
     * Copies dictionary to specified document.
     * It's possible to pass a list of keys to exclude when copying.
     *
     * @param document    document to copy dictionary to.
     * @param excludeKeys list of objects to exclude when copying dictionary.
     * @param allowDuplicating {@link PdfObject#copyTo(PdfDocument, boolean)}
     * @return copied dictionary.
     */
    public PdfDictionary copyTo(PdfDocument document, List<PdfName> excludeKeys, boolean allowDuplicating) {
        Map<PdfName, PdfObject> excluded = new TreeMap<>();
        for (PdfName key : excludeKeys) {
            PdfObject obj = map.get(key);
            if (obj != null)
                excluded.put(key, map.remove(key));
        }
        PdfDictionary dictionary = (PdfDictionary) copyTo(document, allowDuplicating);
        map.putAll(excluded);
        return dictionary;
    }

    /**
     *
     * @param asDirect true is to extract direct object always.
     */
    public PdfObject get(PdfName key, boolean asDirect) {
        if (!asDirect)
            return map.get(key);
        else {
            PdfObject obj = map.get(key);
            if (obj != null && obj.getType() == INDIRECT_REFERENCE)
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
    protected PdfObject newInstance() {
        return new PdfDictionary();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfDictionary dictionary = (PdfDictionary) from;
        for (Map.Entry<PdfName, PdfObject> entry : dictionary.map.entrySet()) {
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
