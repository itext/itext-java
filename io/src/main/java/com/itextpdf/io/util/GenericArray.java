/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.io.util;

import java.util.ArrayList;
import java.util.List;

/**
 * A fixed-size, generic array backed by a list.
 *
 * @param <T> the element type
 */
public class GenericArray<T> {

    private final List<T> array;

    /**
     * Creates an array whose entries are initially {@code null}.
     *
     * @param size the number of entries
     */
    public GenericArray(int size) {
        array = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            array.add(null);
        }
    }

    /**
     * Returns the element at an index.
     *
     * @param index the zero-based index
     *
     * @return the element at {@code index}
     */
    public T get(int index) {
        return array.get(index);
    }

    /**
     * Replaces the element at an index.
     *
     * @param index   the zero-based index
     * @param element the replacement element
     *
     * @return the previously stored element
     */
    public T set(int index, T element) {
        return array.set(index, element);
    }
}
