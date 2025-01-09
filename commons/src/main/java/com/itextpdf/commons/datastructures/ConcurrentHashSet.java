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
package com.itextpdf.commons.datastructures;


import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.Collection;

/**
 * Concurrent hash set implementation.
 *
 * @param <V> type of the values
 */
public class ConcurrentHashSet<V> implements Set<V> {

    private final Set<V> set = Collections.synchronizedSet(new HashSet<>());

    public ConcurrentHashSet() {
        //empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return set.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return  set.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<V> iterator() {
        throw new UnsupportedOperationException(CommonsExceptionMessageConstant.UNSUPPORTED_OPERATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException(CommonsExceptionMessageConstant.UNSUPPORTED_OPERATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(V o) {
        return set.add(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o) {
        return set.remove(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection c) {
        return set.addAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        set.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        return set.equals(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return set.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void forEach(Consumer<? super V> consumer) {
        set.forEach(consumer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException(CommonsExceptionMessageConstant.UNSUPPORTED_OPERATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException(CommonsExceptionMessageConstant.UNSUPPORTED_OPERATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(Collection c) {
        throw new UnsupportedOperationException(CommonsExceptionMessageConstant.UNSUPPORTED_OPERATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V[] toArray(Object[] a) {
        throw new UnsupportedOperationException(CommonsExceptionMessageConstant.UNSUPPORTED_OPERATION);
    }
}
