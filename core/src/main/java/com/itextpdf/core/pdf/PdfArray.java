package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.geom.Rectangle;

import java.util.*;

public class PdfArray extends PdfObject implements Collection<PdfObject> {

    private List<PdfObject> list;

    public PdfArray() {
        super();
        list = new ArrayList<>();
    }

    public PdfArray(PdfObject obj) {
        this();
        list.add(obj);
    }

    public PdfArray(Rectangle rectangle) {
        list = new ArrayList<>(4);
        add(new PdfNumber(rectangle.getLeft()));
        add(new PdfNumber(rectangle.getBottom()));
        add(new PdfNumber(rectangle.getRight()));
        add(new PdfNumber(rectangle.getTop()));
    }

    public PdfArray(List<? extends PdfObject> objects) {
        list = new ArrayList<>(objects.size());
        for (PdfObject element : objects)
            add(element);
    }

    public PdfArray(float[] numbers) {
        list = new ArrayList<>(numbers.length);
        for (float f : numbers) {
            list.add(new PdfNumber(f));
        }
    }

    public PdfArray(double[] numbers) {
        list = new ArrayList<>(numbers.length);
        for (double f : numbers) {
            list.add(new PdfNumber(f));
        }
    }

    public PdfArray(int[] numbers) {
        list = new ArrayList<>(numbers.length);
        for (float i : numbers) {
            list.add(new PdfNumber(i));
        }
    }

    public PdfArray(boolean[] values) {
        list = new ArrayList<>(values.length);
        for (boolean b : values) {
            list.add(new PdfBoolean(b));
        }
    }

    public PdfArray(List<String> strings, boolean asNames) {
        list = new ArrayList<>(strings.size());
        for (String s : strings) {
            list.add(asNames ? new PdfName(s) : new PdfString(s));
        }
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<PdfObject> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(PdfObject pdfObject) {
        return list.add(pdfObject);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends PdfObject> c) {
        return list.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends PdfObject> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    public PdfObject get(int index) {
        return get(index, true);
    }

    public PdfObject set(int index, PdfObject element) {
        return list.set(index, element);
    }

    public void add(int index, PdfObject element) {
        list.add(index, element);
    }

    public PdfObject remove(int index) {
        return list.remove(index);
    }

    public int indexOf(PdfObject o) {
        return list.indexOf(o);
    }

    public int lastIndexOf(PdfObject o) {
        return list.lastIndexOf(o);
    }

    public ListIterator<PdfObject> listIterator() {
        return list.listIterator();
    }

    public ListIterator<PdfObject> listIterator(int index) {
        return list.listIterator(index);
    }

    public List<PdfObject> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public byte getType() {
        return Array;
    }

    @Override
    public String toString() {
        String string = "[";
        for (PdfObject entry : this) {
            PdfIndirectReference indirectReference = entry.getIndirectReference();
            string = string + (indirectReference == null ? entry.toString() : indirectReference.toString()) + " ";
        }
        string += "]";
        return string;
    }

    /**
     * @param asDirect true is to extract direct object always.
     * @throws PdfException
     */
    public PdfObject get(int index, boolean asDirect) {
        if (!asDirect)
            return list.get(index);
        else {
            PdfObject obj = list.get(index);
            if (obj.getType() == IndirectReference)
                return ((PdfIndirectReference) obj).getRefersTo(true);
            else
                return obj;
        }
    }

    public PdfArray getAsArray(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.Array)
            return (PdfArray) direct;
        return null;
    }

    public PdfDictionary getAsDictionary(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.Dictionary)
            return (PdfDictionary) direct;
        return null;
    }

    public PdfStream getAsStream(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.Stream)
            return (PdfStream) direct;
        return null;
    }

    public PdfNumber getAsNumber(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.Number)
            return (PdfNumber) direct;
        return null;
    }

    public PdfName getAsName(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.Name)
            return (PdfName) direct;
        return null;
    }

    public PdfString getAsString(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.String)
            return (PdfString) direct;
        return null;
    }

    public PdfBoolean getAsBoolean(int index) {
        PdfObject direct = get(index, true);
        if (direct != null && direct.getType() == PdfObject.Boolean)
            return (PdfBoolean) direct;
        return null;
    }

    public Rectangle getAsRectangle(int index) {
        PdfArray a = getAsArray(index);
        return a == null ? null : a.toRectangle();
    }

    public Float getAsFloat(int index) {
        PdfNumber number = getAsNumber(index);
        return number == null ? null : number.getFloatValue();
    }

    public Integer getAsInt(int index) {
        PdfNumber number = getAsNumber(index);
        return number == null ? null : number.getIntValue();
    }

    public Boolean getAsBool(int index) {
        PdfBoolean b = getAsBoolean(index);
        return b == null ? null : b.getValue();
    }

    public Rectangle toRectangle() {
        try {
            float x1 = getAsNumber(0).getFloatValue();
            float y1 = getAsNumber(1).getFloatValue();
            float x2 = getAsNumber(2).getFloatValue();
            float y2 = getAsNumber(3).getFloatValue();
            return new Rectangle(x1, y1, x2 - x1, y2 - y1);
        } catch (Exception e) {
            throw new PdfException(PdfException.CannotConvertPdfArrayToRectanle, e, this);
        }
    }

    @Override
    protected PdfArray newInstance() {
        return new PdfArray();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfArray array = (PdfArray) from;
        for (PdfObject entry : array) {
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
