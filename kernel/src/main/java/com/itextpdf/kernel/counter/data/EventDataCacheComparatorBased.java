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
package com.itextpdf.kernel.counter.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Comparator-based implementation of {@link IEventDataCache}.
 * Merges data with the same signature by increasing its count.
 * Retrieve the smallest element based on comparator.
 *
 * Not thread safe.
 *
 * @param <T> the data signature type
 * @param <V> the data type
 */
public class EventDataCacheComparatorBased<T, V extends EventData<T>> implements IEventDataCache<T, V> {

    private Map<T, V> map = new HashMap<>();
    private Set<V> orderedCache;

    public EventDataCacheComparatorBased(Comparator<V> comparator) {
        orderedCache = new TreeSet<>(comparator);
    }

    @Override
    public void put(V data) {
        if (data != null) {
            V old = map.put(data.getSignature(), data);
            if (old != null) {
                orderedCache.remove(old);
                data.mergeWith(old);
                orderedCache.add(data);
            } else {
                orderedCache.add(data);
            }
        }
    }

    @Override
    public V retrieveNext() {
        for (V data : orderedCache) {
            if (data != null) {
                map.remove(data.getSignature());
                orderedCache.remove(data);
                return data;
            }
        }
        return null;
    }

    @Override
    public List<V> clear() {
        ArrayList<V> result = new ArrayList<>(map.values());
        map.clear();
        orderedCache.clear();
        return result;
    }
}
