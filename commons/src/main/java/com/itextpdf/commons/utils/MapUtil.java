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
package com.itextpdf.commons.utils;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Utility class for work with collections. Not for public use.
 */
public final class MapUtil {

    private static final int HASH_MULTIPLIER = 31;

    private MapUtil() { }

    /**
     * Checks if two {@link Map maps} are equal: the are of the same types and has equal number of stored
     * entries and both has the same set of keys ans each key is associated with an appropriate
     * value.
     *
     * @param m1 is the first map
     * @param m2 is the second map
     * @param <K> is a type of keys
     * @param <V> is a type of values
     *
     * @return {@code true} if maps are equal and {@code false} otherwise
     */
    public static <K, V> boolean equals(Map<K, V> m1, Map<K, V> m2) {
        if (m1 == m2) {
            return true;
        }
        if (m1 == null || m2 == null) {
            return false;
        }

        if (! m1.getClass().equals(m2.getClass())) {
            return false;
        }

        if (m1.size() != m2.size()) {
            return false;
        }

        for (Map.Entry<K, V> entry : m1.entrySet()) {
            final V obj1 = entry.getValue();
            final V obj2 = m2.get(entry.getKey());
            if (!m2.containsKey(entry.getKey()) || !Objects.equals(obj1, obj2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Merges data from source Map into destination Map using provided function if key exists in both Maps.
     * If key doesn't exist in destination Map in will be putted directly.
     *
     * @param destination Map to which data will be merged.
     * @param source Map from which data will be taken.
     * @param valuesMerger function which will be used to merge Maps values.
     * @param <K> is a type of keys
     * @param <V> is a type of values
     */
    public static <K, V> void merge(Map<K, V> destination, Map<K, V> source, BiFunction<V, V, V> valuesMerger) {
        if (destination == source) {
            return;
        }
        for (Map.Entry<K, V> entry : source.entrySet()) {
            V value = destination.get(entry.getKey());
            if (value == null) {
                destination.put(entry.getKey(), entry.getValue());
            } else {
                destination.put(entry.getKey(), valuesMerger.apply(value, entry.getValue()));
            }
        }
    }

    /**
     * Calculates the hash code of the {@link Map map}.
     *
     * @param m1 is the map
     * @param <K> is a type of keys
     * @param <V> is a type of values
     *
     * @return the hash code of the {@link Map map}.
     */
    public static <K, V> int getHashCode(Map<K, V> m1) {
        if (null == m1) {
            return 0;
        }

        int hash = 0;
        for (Map.Entry<K, V> entry : m1.entrySet()) {
            final K key = entry.getKey();
            final V value = entry.getValue();
            hash = HASH_MULTIPLIER * hash + (key == null ? 0 : key.hashCode());
            hash = HASH_MULTIPLIER * hash + (value == null ? 0 : value.hashCode());
        }

        return hash;
    }

    /**
     * Puts value to map if the value is not null.
     *
     * @param map the map in which value can be pushed
     * @param key the key
     * @param value the value
     * @param <K> is a type of key
     * @param <V> is a type of value
     */
    public static <K, V> void putIfNotNull(Map<K, V> map, K key, V value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
