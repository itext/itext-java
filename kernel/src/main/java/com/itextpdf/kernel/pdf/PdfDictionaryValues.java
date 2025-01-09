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
        if (o == null)
            return false;
        for (PdfObject pdfObject : this) {
            if (PdfObject.equalContent((PdfObject) o, pdfObject)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (collection.remove(o))
            return true;
        if (o == null)
            return false;
        Iterator<PdfObject> it = iterator();
        while (it.hasNext()) {
            if (PdfObject.equalContent((PdfObject) o, it.next())) {
                it.remove();
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
        return new DirectIterator(collection.iterator());
    }

    private static class DirectIterator implements Iterator<PdfObject> {
        Iterator<PdfObject> parentIterator;

        DirectIterator(Iterator<PdfObject> parentIterator) {
            this.parentIterator = parentIterator;
        }

        @Override
        public boolean hasNext() {
            return parentIterator.hasNext();
        }

        @Override
        public PdfObject next() {
            PdfObject obj = parentIterator.next();
            if (obj != null && obj.isIndirectReference()) {
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
