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
package com.itextpdf.styledxmlparser.jsoup.helper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation of ArrayList that watches out for changes to the contents.
 */
public abstract class ChangeNotifyingArrayList<E> extends ArrayList<E> {
    public ChangeNotifyingArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public abstract void onContentsChanged();

    @Override
    public E set(int index, E element) {
        onContentsChanged();
        return super.set(index, element);
    }

    @Override
    public boolean add(E e) {
        onContentsChanged();
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        onContentsChanged();
        super.add(index, element);
    }

    @Override
    public E remove(int index) {
        onContentsChanged();
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        onContentsChanged();
        return super.remove(o);
    }

    @Override
    public void clear() {
        onContentsChanged();
        super.clear();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        onContentsChanged();
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        onContentsChanged();
        return super.addAll(index, c);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        onContentsChanged();
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        onContentsChanged();
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        onContentsChanged();
        return super.retainAll(c);
    }

}
