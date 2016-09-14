package com.itextpdf.kernel.pdf;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

class PdfDictionaryEntrySet extends AbstractSet<Map.Entry<PdfName, PdfObject>> {

    private final Set<Map.Entry<PdfName, PdfObject>> set;

    PdfDictionaryEntrySet(Set<Map.Entry<PdfName, PdfObject>> set) {
        this.set = set;
    }

    @Override
    public Iterator<Map.Entry<PdfName, PdfObject>> iterator() {
        return new DirectIterator();
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public void clear() {
        set.clear();
    }

    private class DirectIterator implements Iterator<Map.Entry<PdfName, PdfObject>> {
        Iterator<Map.Entry<PdfName, PdfObject>> parentIterator = set.iterator();

        @Override
        public boolean hasNext() {
            return parentIterator.hasNext();
        }

        @Override
        public Map.Entry<PdfName, PdfObject> next() {
            return new DirectEntry(parentIterator.next());
        }

        @Override
        public void remove() {
            parentIterator.remove();
        }
    }

    private class DirectEntry implements Map.Entry<PdfName, PdfObject> {

        Map.Entry<PdfName, PdfObject> entry;

        public DirectEntry(Map.Entry<PdfName, PdfObject> entry) {
            this.entry = entry;
        }

        @Override
        public PdfName getKey() {
            return entry.getKey();
        }

        @Override
        public PdfObject getValue() {
            PdfObject obj = entry.getValue();
            if (obj.isIndirectReference()) {
                obj = ((PdfIndirectReference) obj).getRefersTo(true);
            }
            return obj;
        }

        @Override
        public PdfObject setValue(PdfObject value) {
            return entry.setValue(value);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry)o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 != null && k1.equals(k2)) {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 != null && v1.equals(v2))
                    return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
        }
    }
}
