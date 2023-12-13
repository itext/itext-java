/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
