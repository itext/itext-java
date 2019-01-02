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
