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

/**
 * Interface for a simple list abstraction.
 * <p>
 * This interface is a subset of the {@link java.util.List} interface.
 * It is intended to be used in cases where the full {@link java.util.List} interface is not needed.
 *
 * @param <T> The type of elements in this list.
 */
public interface ISimpleList<T> {
    /**
     * Adds an element to the end of the list.
     *
     * @param element the element to add
     */
    void add(T element);

    /**
     * Adds an element to the list at the specified index.
     *
     * @param index   the index at which to add the element
     * @param element the element to add
     */
    void add(int index, T element);

    /**
     * Returns the element at the specified index.
     *
     * @param index the index of the element to return
     * @return the element at the specified index
     */
    T get(int index);

    /**
     * Replaces the element at the specified index with the specified element.
     *
     * @param index   the index of the element to replace
     * @param element the element to be stored at the specified index
     * @return the element previously at the specified index
     */
    T set(int index, T element);

    /**
     * Returns the index of the first occurrence of the specified element in the list,
     * or -1 if the list does not contain the element.
     *
     * @param element the element to search for
     * @return the index of the first occurrence of the specified element in the list,
     * or -1 if the list does not contain the element
     */
    int indexOf(Object element);

    /**
     * Removes the element at the specified index.
     *
     * @param index the index of the element to be removed
     */
    void remove(int index);

    /**
     * Returns the number of elements in the list.
     *
     * @return the number of elements in the list
     */
    int size();

    /**
     * Returns {@code true} if the list contains no elements, false otherwise.
     *
     * @return {@code true} if the list contains no elements, false otherwise
     */
    boolean isEmpty();
}
