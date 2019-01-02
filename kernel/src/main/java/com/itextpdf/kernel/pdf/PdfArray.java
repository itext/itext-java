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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A representation of an array as described in the PDF specification. A PdfArray can contain any
 * subclass of {@link com.itextpdf.kernel.pdf.PdfObject}.
 */
public class PdfArray extends PdfObject implements Iterable<PdfObject> {

    private static final long serialVersionUID = 1617495612878046869L;

    protected List<PdfObject> list;

    /**
     * Create a new, empty PdfArray.
     */
    public PdfArray() {
        super();
        list = new ArrayList<>();
    }

    /**
     * Create a new PdfArray with the provided PdfObject as the first item in the
     * array.
     *
     * @param obj first item in the array
     */
    public PdfArray(PdfObject obj) {
        this();
        list.add(obj);
    }

    /**
     * Create a new PdfArray. The array is filled with the items of the provided PdfArray.
     *
     * @param arr PdfArray containing items that will added to this PdfArray
     */
    public PdfArray(PdfArray arr) {
        this();
        list.addAll(arr.list);
    }

    /**
     * Create a new PdfArray. The array is filled with the four values of the Rectangle in the
     * following order: left, bottom, right, top.
     *
     * @param rectangle Rectangle whose 4 values will be added to the PdfArray
     */
    public PdfArray(Rectangle rectangle) {
        list = new ArrayList<>(4);
        add(new PdfNumber(rectangle.getLeft()));
        add(new PdfNumber(rectangle.getBottom()));
        add(new PdfNumber(rectangle.getRight()));
        add(new PdfNumber(rectangle.getTop()));
    }

    /**
     * Create a new PdfArray. The PdfObjects in the list will be added to the PdfArray.
     *
     * @param objects List of PdfObjects to be added to this PdfArray
     */
    public PdfArray(List<? extends PdfObject> objects) {
        list = new ArrayList<>(objects.size());
        for (PdfObject element : objects)
            add(element);
    }

    /**
     * Create a new PdfArray filled with the values in the float[] as {@link com.itextpdf.kernel.pdf.PdfNumber}.
     *
     * @param numbers values to be added to this PdfArray
     */
    public PdfArray(float[] numbers) {
        list = new ArrayList<>(numbers.length);
        for (float f : numbers) {
            list.add(new PdfNumber(f));
        }
    }

    /**
     * Create a new PdfArray filled with the values in the double[] as {@link com.itextpdf.kernel.pdf.PdfNumber}.
     *
     * @param numbers values to be added to this PdfArray
     */
    public PdfArray(double[] numbers) {
        list = new ArrayList<>(numbers.length);
        for (double f : numbers) {
            list.add(new PdfNumber(f));
        }
    }

    /**
     * Create a new PdfArray filled with the values in the int[] as {@link com.itextpdf.kernel.pdf.PdfNumber}.
     *
     * @param numbers values to be added to this PdfArray
     */
    public PdfArray(int[] numbers) {
        list = new ArrayList<>(numbers.length);
        for (float i : numbers) {
            list.add(new PdfNumber(i));
        }
    }

    /**
     * Create a new PdfArray filled with the values in the boolean[] as {@link com.itextpdf.kernel.pdf.PdfBoolean}.
     *
     * @param values values to be added to this PdfArray
     */
    public PdfArray(boolean[] values) {
        list = new ArrayList<>(values.length);
        for (boolean b : values) {
            list.add(PdfBoolean.valueOf(b));
        }
    }

    /**
     * Create a new PdfArray filled with a list of Strings. The boolean value decides if the Strings
     * should be added as {@link com.itextpdf.kernel.pdf.PdfName} (true) or as {@link com.itextpdf.kernel.pdf.PdfString} (false).
     *
     * @param strings list of strings to be added to the list
     * @param asNames indicates whether the strings should be added as PdfName (true) or as PdfString (false)
     */
    public PdfArray(List<String> strings, boolean asNames) {
        list = new ArrayList<>(strings.size());
        for (String s : strings) {
            list.add(asNames ? (PdfObject) new PdfName(s) : new PdfString(s));
        }
    }

    /**
     * Create a new PdfArray. The PdfObjects in the iterable object will be added to the PdfArray.
     *
     * @param objects List of PdfObjects to be added to this PdfArray
     * @param initialCapacity Initial capacity of this PdfArray
     */
    public PdfArray(Iterable<? extends PdfObject> objects, int initialCapacity) {
        list = new ArrayList<>(initialCapacity);
        for (PdfObject element : objects)
            add(element);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.size() == 0;
    }

    public boolean contains(PdfObject o) {
        if (list.contains(o))
            return true;
        if (o == null)
            return false;
        for (PdfObject pdfObject : this) {
            if (PdfObject.equalContent(o, pdfObject)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an iterator over an array of PdfObject elements.
     * <br>
     * <b>NOTE:</b> since 7.0.1 it returns collection of direct objects.
     * If you want to get {@link PdfIndirectReference} instances for the indirect objects value,
     * you shall use {@link #get(int, boolean)} method.
     *
     * @return an Iterator.
     */
    public Iterator<PdfObject> iterator() {
        return new PdfArrayDirectIterator(list);
    }

    public void add(PdfObject pdfObject) {
        list.add(pdfObject);
    }

    /**
     * Adds the specified PdfObject at the specified index. All objects after this index will be shifted by 1.
     *
     * @param index   position to insert the PdfObject
     * @param element PdfObject to be added
     * @see java.util.List#add(int, Object)
     */
    public void add(int index, PdfObject element) {
        list.add(index, element);
    }

    /**
     * Sets the PdfObject at the specified index in the PdfArray.
     *
     * @param index   the position to set the PdfObject
     * @param element PdfObject to be added
     * @return true if the operation changed the PdfArray
     * @see java.util.List#set(int, Object)
     */
    public PdfObject set(int index, PdfObject element) {
        return list.set(index, element);
    }

    /**
     * Adds the Collection of PdfObjects.
     *
     * @param c the Collection of PdfObjects to be added
     * @see java.util.List#addAll(java.util.Collection)
     */
    public void addAll(Collection<PdfObject> c) {
        list.addAll(c);
    }

    /**
     * Adds content of the {@code PdfArray}.
     *
     * @param a the {@code PdfArray} to be added
     * @see java.util.List#addAll(java.util.Collection)
     */
    public void addAll(PdfArray a) {
        if (a != null) addAll(a.list);
    }

    /**
     * Gets the (direct) PdfObject at the specified index.
     *
     * @param index index of the PdfObject in the PdfArray
     * @return the PdfObject at the position in the PdfArray
     */
    public PdfObject get(int index) {
        return get(index, true);
    }

    /**
     * Removes the PdfObject at the specified index.
     *
     * @param index position of the PdfObject to be removed
     * @see java.util.List#remove(int)
     */
    public void remove(int index) {
        list.remove(index);
    }

    /**
     * Removes the first occurrence of the specified PdfObject, if it is present.
     *
     * @param o a PdfObject to be removed
     * @see java.util.List#remove(Object)
     */
    public void remove(PdfObject o) {
        if (list.remove(o))
            return;
        if (o == null)
            return;
        for (PdfObject pdfObject : list) {
            if (PdfObject.equalContent(o, pdfObject)) {
                list.remove(pdfObject);
                break;
            }
        }
    }

    public void clear() {
        list.clear();
    }

    /**
     * Gets the first index of the specified PdfObject.
     *
     * @param o PdfObject to find the index of
     * @return index of the PdfObject
     * @see java.util.List#indexOf(Object)
     */
    public int indexOf(PdfObject o) {
        if (o == null)
            return list.indexOf(null);
        int index = 0;
        for (PdfObject pdfObject : this) {
            if (PdfObject.equalContent(o, pdfObject)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * Returns a sublist of this PdfArray, starting at fromIndex (inclusive) and ending at toIndex (exclusive).
     *
     * @param fromIndex the position of the first element in the sublist (inclusive)
     * @param toIndex   the position of the last element in the sublist (exclusive)
     * @return List of PdfObjects
     * @see java.util.List#subList(int, int)
     */
    public List<PdfObject> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public byte getType() {
        return ARRAY;
    }

    @Override
    public String toString() {
        String string = "[";
        for (PdfObject entry : list) {
            PdfIndirectReference indirectReference = entry.getIndirectReference();
            string = string + (indirectReference == null ? entry.toString() : indirectReference.toString()) + " ";
        }
        string += "]";
        return string;
    }

    /**
     * @param asDirect true is to extract direct object always.
     */
    public PdfObject get(int index, boolean asDirect) {
        if (!asDirect)
            return list.get(index);
        else {
            PdfObject obj = list.get(index);
            if (obj.getType() == INDIRECT_REFERENCE)
                return ((PdfIndirectReference) obj).getRefersTo(true);
            else
                return obj;
        }
    }

    /**
     * Returns the element at the specified index as a PdfArray. If the element isn't a PdfArray, null is returned.
     *
     * @param index position of the element to be returned
     * @return the element at the index as a PdfArray
     */
    public PdfArray getAsArray(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.ARRAY)
            return (PdfArray) direct;
        return null;
    }


    /**
     * Returns the element at the specified index as a PdfDictionary. If the element isn't a PdfDictionary, null is returned.
     *
     * @param index position of the element to be returned
     * @return the element at the index as a PdfDictionary
     */
    public PdfDictionary getAsDictionary(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.DICTIONARY)
            return (PdfDictionary) direct;
        return null;
    }


    /**
     * Returns the element at the specified index as a PdfStream. If the element isn't a PdfStream, null is returned.
     *
     * @param index position of the element to be returned
     * @return the element at the index as a PdfStream
     */
    public PdfStream getAsStream(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.STREAM)
            return (PdfStream) direct;
        return null;
    }


    /**
     * Returns the element at the specified index as a PdfNumber. If the element isn't a PdfNumber, null is returned.
     *
     * @param index position of the element to be returned
     * @return the element at the index as a PdfNumber
     */
    public PdfNumber getAsNumber(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.NUMBER)
            return (PdfNumber) direct;
        return null;
    }


    /**
     * Returns the element at the specified index as a PdfName. If the element isn't a PdfName, null is returned.
     *
     * @param index position of the element to be returned
     * @return the element at the index as a PdfName
     */
    public PdfName getAsName(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.NAME)
            return (PdfName) direct;
        return null;
    }


    /**
     * Returns the element at the specified index as a PdfString. If the element isn't a PdfString, null is returned.
     *
     * @param index position of the element to be returned
     * @return the element at the index as a PdfString
     */
    public PdfString getAsString(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.STRING)
            return (PdfString) direct;
        return null;
    }

    /**
     * Returns the element at the specified index as a PdfBoolean. If the element isn't a PdfBoolean, null is returned.
     *
     * @param index position of the element to be returned
     * @return the element at the index as a PdfBoolean
     */
    public PdfBoolean getAsBoolean(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.BOOLEAN)
            return (PdfBoolean) direct;
        return null;
    }

    /**
     * Returns the first four elements of this array as a PdfArray. The first four values need to be
     * PdfNumbers, if not a PdfException will be thrown.
     *
     * @return Rectangle of the first four values
     * @throws com.itextpdf.kernel.PdfException if one of the first values isn't a PdfNumber
     */
    public Rectangle toRectangle() {
        try {
            float x1 = getAsNumber(0).floatValue();
            float y1 = getAsNumber(1).floatValue();
            float x2 = getAsNumber(2).floatValue();
            float y2 = getAsNumber(3).floatValue();
            float llx, lly, urx, ury;
            //Find the lower-left and upper-right of these 4 points
            llx = Math.min(x1,x2);
            lly = Math.min(y1,y2);
            urx = Math.max(x1,x2);
            ury = Math.max(y1,y2);
             
            return new Rectangle(llx, lly, urx - llx, ury - lly);
        } catch (Exception e) {
            throw new PdfException(PdfException.CannotConvertPdfArrayToRectanle, e, this);
        }
    }

    /**
     * Returns this array as an array of floats. Will throw a PdfException when it encounters an issue.
     *
     * @return this array as an array of floats
     * @throws com.itextpdf.kernel.PdfException if one of the values isn't a number
     */
    public float[] toFloatArray() {
        try {
            float[] rslt = new float[size()];
            for (int k = 0; k < rslt.length; ++ k) {
                rslt[k] = getAsNumber(k).floatValue();
            }
            return rslt;
        } catch (Exception e) {
            throw new PdfException(PdfException.CannotConvertPdfArrayToFloatArray, e, this);
        }
    }

    /**
     * Returns this array as an array of doubles. Will throw a PdfException when it encounters an issue.
     *
     * @return this array as an array of doubles
     * @throws com.itextpdf.kernel.PdfException if one of the values isn't a number
     */
    public double[] toDoubleArray() {
        try {
            double[] rslt = new double[size()];
            for (int k = 0; k < rslt.length; ++ k) {
                rslt[k] = getAsNumber(k).doubleValue();
            }
            return rslt;
        } catch (Exception e) {
            throw new PdfException(PdfException.CannotConvertPdfArrayToDoubleArray, e, this);
        }
    }

    /**
     * Returns this array as an array of longs. Will throw a PdfException when it encounters an issue.
     *
     * @return this array as an array of longs
     * @throws com.itextpdf.kernel.PdfException if one of the values isn't a number
     */
    public long[] toLongArray() {
        try {
            long[] rslt = new long[size()];
            for (int k = 0; k < rslt.length; ++ k) {
                rslt[k] = getAsNumber(k).longValue();
            }
            return rslt;
        } catch (Exception e) {
            throw new PdfException(PdfException.CannotConvertPdfArrayToLongArray, e, this);
        }
    }

    /**
     * Returns this array as an array of ints. Will throw a PdfException when it encounters an issue.
     *
     * @return this array as an array of ints
     * @throws com.itextpdf.kernel.PdfException if one of the values isn't a number
     */
    public int[] toIntArray() {
        try {
            int[] rslt = new int[size()];
            for (int k = 0; k < rslt.length; ++ k) {
                rslt[k] = getAsNumber(k).intValue();
            }
            return rslt;
        } catch (Exception e) {
            throw new PdfException(PdfException.CannotConvertPdfArrayToIntArray, e, this);
        }
    }

    /**
     * Returns this array as an array of booleans. Will throw a PdfException when it encounters an issue.
     *
     * @return this array as an array of booleans
     * @throws com.itextpdf.kernel.PdfException if one of the values isn't a boolean
     */
    public boolean[] toBooleanArray() {
        boolean[] rslt = new boolean[size()];
        PdfBoolean tmp;
        for (int k = 0; k < rslt.length; ++ k) {
            tmp = getAsBoolean(k);
            if (tmp == null) {
                throw new PdfException(PdfException.CannotConvertPdfArrayToBooleanArray, this);
            }
            rslt[k] = tmp.getValue();
        }
        return rslt;
    }

    @Override
    protected PdfObject newInstance() {
        return new PdfArray();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfArray array = (PdfArray) from;
        for (PdfObject entry : array.list) {
            add(entry.processCopying(document, false));
        }
    }

    /**
     * Release content of PdfArray.
     */
    protected void releaseContent() {
        list = null;
    }
}
