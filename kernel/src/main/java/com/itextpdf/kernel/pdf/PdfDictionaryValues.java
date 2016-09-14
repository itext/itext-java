package com.itextpdf.kernel.pdf;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

class PdfDictionaryValues extends AbstractCollection<PdfObject> {

    private final Collection<PdfObject> collection;

    PdfDictionaryValues(Collection<PdfObject> collection) {
        this.collection = collection;
    }

    @Override
    public boolean add(PdfObject object) {
        return collection.add(object);
    }

    @Override
    public boolean contains(Object o) {
        if (collection.contains(o))
            return true;
        if (o != null) {
            if (((PdfObject) o).getIndirectReference() != null
                && collection.contains(((PdfObject) o).getIndirectReference())) {
                return true;
            } else if (((PdfObject) o).isIndirectReference()
                    && collection.contains(((PdfIndirectReference) o).getRefersTo())) {
                return true;
            }
        }
        return false;

    }

    @Override
    public boolean remove(Object o) {
        if (collection.remove(o))
            return true;
        if (o != null) {
            if (((PdfObject) o).getIndirectReference() != null
                    && collection.remove(((PdfObject) o).getIndirectReference())) {
                return true;
            } else if (((PdfObject) o).isIndirectReference()
                    && collection.remove(((PdfIndirectReference) o).getRefersTo())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return collection.size();
    }

    @Override
    public void clear() {
        collection.clear();
    }

    @Override
    public Iterator<PdfObject> iterator() {
        return new DirectIterator();
    }

    private class DirectIterator implements Iterator<PdfObject> {
        Iterator<PdfObject> parentIterator = collection.iterator();

        @Override
        public boolean hasNext() {
            return parentIterator.hasNext();
        }

        @Override
        public PdfObject next() {
            PdfObject obj = parentIterator.next();
            if (obj.isIndirectReference()) {
                obj = ((PdfIndirectReference) obj).getRefersTo(true);
            }
            return obj;
        }

        @Override
        public void remove() {
            parentIterator.remove();
        }
    }
}