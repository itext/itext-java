package com.itextpdf.core.pdf.objects;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;

import java.io.IOException;
import java.util.*;

public class PdfArray extends PdfObject implements List<PdfObject> {

    protected List<PdfObject> list = new ArrayList<PdfObject>();

    public PdfArray() {
        super(PdfObject.Array);
    }

    public PdfArray(PdfDocument doc) {
        super(doc, PdfObject.Array);
    }

    public PdfArray(Rectangle rectangle) {
        this(null, rectangle);
    }

    public PdfArray(PdfDocument doc, Rectangle rectangle) {
        this(doc);
        add(new PdfNumber(rectangle.getX()));
        add(new PdfNumber(rectangle.getY()));
        add(new PdfNumber(rectangle.getX() + rectangle.getWidth()));
        add(new PdfNumber(rectangle.getY() + rectangle.getHeight()));
    }

    public PdfArray(List<PdfObject> objects) {
        this(null, objects);
    }

    public PdfArray(PdfDocument doc, List<PdfObject> objects) {
        this(doc);
        list = objects;
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

    @Override
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

    @Override
    public PdfObject get(int index) {
        return list.get(index);
    }

    @Override
    public PdfObject set(int index, PdfObject element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, PdfObject element) {
        list.add(index, element);
    }

    @Override
    public PdfObject remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<PdfObject> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<PdfObject> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<PdfObject> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public boolean flush() throws IOException, PdfException {
        if (flushed)
            return true;
        PdfWriter writer = pdfDocument.getWriter();
        for (PdfObject object : this) {
            if (object.getIndirectReference() != null) {
                writer.add(object.getIndirectReference());
            }
        }
        super.flush();
        if (flushed && list != null) {
            clear();
            list = null;
        }
        return flushed;
    }

}
