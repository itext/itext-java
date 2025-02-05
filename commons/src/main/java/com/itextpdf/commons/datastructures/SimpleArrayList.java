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

import java.util.ArrayList;

/**
 * Portable implementation of {@link ArrayList}.
 *
 * @param <T> the type of elements in this list
 */
public class SimpleArrayList<T> implements ISimpleList<T> {

    private final ArrayList<T> list;

    /**
     * Creates a new instance of {@link SimpleArrayList}.
     */
    public SimpleArrayList() {
        this.list = new ArrayList<>();
    }

    /**
     * Creates a new instance of {@link SimpleArrayList} with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     */
    public SimpleArrayList(int initialCapacity) {
        this.list = new ArrayList<>(initialCapacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(T element) {
        list.add(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(int index, T element) {
        list.add(index, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(int index) {
        return list.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T set(int index, T element) {
        T value = list.get(index);
        list.set(index, element);
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(Object element) {
        return list.indexOf((T) element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(int index) {
        list.remove(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return list.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
