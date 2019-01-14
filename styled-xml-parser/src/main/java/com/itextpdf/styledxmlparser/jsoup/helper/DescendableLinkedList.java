/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.styledxmlparser.jsoup.helper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Provides a descending iterator and other 1.6 methods to allow support on the 1.5 JRE.
 * @param <E> Type of elements
 */
public class DescendableLinkedList<E> extends LinkedList<E> {

    /**
     * Create a new DescendableLinkedList.
     */
    public DescendableLinkedList() {
        super();
    }

    /**
     * Add a new element to the start of the list.
     * @param e element to add
     */
    public void push(E e) {
        addFirst(e);
    }

    /**
     * Look at the last element, if there is one.
     * @return the last element, or null
     */
    public E peekLast() {
        return size() == 0 ? null : getLast();
    }

    /**
     * Remove and return the last element, if there is one
     * @return the last element, or null
     */
    public E pollLast() {
        return size() == 0 ? null : removeLast();
    }

    /**
     * Get an iterator that starts and the end of the list and works towards the start.
     * @return an iterator that starts and the end of the list and works towards the start.
     */
    public Iterator<E> descendingIterator() {
        return new DescendingIterator<E>(size());
    }

    private class DescendingIterator<E> implements Iterator<E> {
        private final ListIterator<E> iter;

        @SuppressWarnings("unchecked")
        private DescendingIterator(int index) {
            iter = (ListIterator<E>) listIterator(index);
        }

        /**
         * Check if there is another element on the list.
         * @return if another element
         */
        public boolean hasNext() {
            return iter.hasPrevious();
        }

        /**
         * Get the next element.
         * @return the next element.
         */
        public E next() {
            return iter.previous();
        }

        /**
         * Remove the current element.
         */
        public void remove() {
            iter.remove();
        }
    }
}
