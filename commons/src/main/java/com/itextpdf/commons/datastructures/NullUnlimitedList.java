/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import java.util.HashMap;
import java.util.Map;

/**
 * The class represents a list which allows null elements, but doesn't allocate a memory for them, in the rest of
 * cases it behaves like usual {@link ArrayList} and should have the same complexity (because keys are unique
 * integers, so collisions are impossible).
 *
 * @param <T> elements of the list
 */
public final class NullUnlimitedList<T> implements ISimpleList<T> {
    private final Map<Integer, T> map = new HashMap<>();
    private int size = 0;

    /**
     * Creates a new instance of {@link NullUnlimitedList}.
     */
    public NullUnlimitedList() {
        // Empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(T element) {
        if (element == null) {
            size++;
        } else {
            int position = size++;
            map.put(position, element);
        }
    }

    /**
     * {@inheritDoc}
     * In worth scenario O(n^2) but it is mostly impossible because keys shouldn't have
     * collisions at all (they are integers). So in average should be O(n).
     */
    @Override
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            return;
        }
        size++;
        // Shifts the element currently at that position (if any) and any
        // subsequent elements to the right (adds one to their indices).
        T previous = map.get(index);
        for (int i = index + 1; i < size; i++) {
            T currentToAdd = previous;
            previous = map.get(i);
            this.set(i, currentToAdd);
        }

        this.set(index, element);
    }

    /**
     * {@inheritDoc}
     * average O(1), worth O(n) (mostly impossible in case when keys are integers)
     */
    @Override
    public T get(int index) {
        return map.get(index);
    }

    /**
     * {@inheritDoc}
     * average O(1), worth O(n) (mostly impossible in case when keys are integers)
     */
    @Override
    public T set(int index, T element) {
        if (element == null) {
            map.remove(index);
        } else {
            map.put(index, element);
        }
        return element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(Object element) {
        if (element == null) {
            for (int i = 0; i < size; i++) {
                if (!map.containsKey(i)) {
                    return i;
                }
            }
            return -1;
        }
        for (Map.Entry<Integer, T> entry : map.entrySet()) {
            if (element.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * In worth scenario O(n^2) but it is mostly impossible because keys shouldn't have
     * collisions at all (they are integers). So in average should be O(n).
     *
     * @param index the index of the element to be removed
     */
    @Override
    public void remove(int index) {
        if (index < 0 || index >= size) {
            return;
        }
        map.remove(index);
        // Shifts any subsequent elements to the left (subtracts one from their indices).
        T previous = map.get(size - 1);
        final int offset = 2;
        for (int i = size - offset; i >= index; i--) {
            T current = previous;
            previous = map.get(i);
            this.set(i, current);
        }
        map.remove(--size);
    }

    /**
     * @return the size of the list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * @return true if the list is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

}
