/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
    public boolean contains(Object o) {
        return set.contains(o) || super.contains(o);
    }

    @Override
    public boolean remove(Object o) {
        return set.remove(o) || super.remove(o);
    }

    @Override
    public Iterator<Map.Entry<PdfName, PdfObject>> iterator() {
        return new DirectIterator(set.iterator());
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public void clear() {
        set.clear();
    }

    private static class DirectIterator implements Iterator<Map.Entry<PdfName, PdfObject>> {
        Iterator<Map.Entry<PdfName, PdfObject>> parentIterator;

        public DirectIterator(Iterator<Map.Entry<PdfName, PdfObject>> parentIterator) {
            this.parentIterator = parentIterator;
        }

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

    private static class DirectEntry implements Map.Entry<PdfName, PdfObject> {

        Map.Entry<PdfName, PdfObject> entry;

        DirectEntry(Map.Entry<PdfName, PdfObject> entry) {
            this.entry = entry;
        }

        @Override
        public PdfName getKey() {
            return entry.getKey();
        }

        @Override
        public PdfObject getValue() {
            PdfObject obj = entry.getValue();
            if (obj != null && obj.isIndirectReference()) {
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
