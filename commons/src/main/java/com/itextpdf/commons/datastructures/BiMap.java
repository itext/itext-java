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

import java.util.HashMap;
import java.util.Map;

/**
 * A simple bi-directional map.
 *
 * @param <K> the type of the first key
 * @param <V> the type of the second key
 */
public final class BiMap<K, V> {

    private final Map<K, V> map = new HashMap<K, V>();
    private final Map<V, K> inverseMap = new HashMap<V, K>();

    /**
     * Creates a new {@link BiMap} instance.
     */
    public BiMap() {
        // empty constructor
    }

    /**
     * Puts the entry into the map.
     * If the key already exists, the value will be overwritten.
     * If the value already exists, the key will be overwritten.
     * If both key and value already exist, the entry will be overwritten.
     * If neither key nor value already exist, the entry will be added.
     *
     * @param k the key
     * @param v the value
     */
    public void put(K k, V v) {
        map.put(k, v);
        inverseMap.put(v, k);
    }

    /**
     * Gets the value by key.
     *
     * @param value the key
     *
     * @return the value
     */
    public V getByKey(K value) {
        return map.get(value);
    }

    /**
     * Gets the key by value.
     *
     * @param key the value
     *
     * @return the key
     */
    public K getByValue(V key) {
        return inverseMap.get(key);
    }

    /**
     * Removes the entry by key.
     *
     * @param k the key
     */
    public void removeByKey(K k) {
        V v = map.remove(k);
        if (v != null) {
            inverseMap.remove(v);
        }
    }

    /**
     * Removes the entry by value.
     *
     * @param v the value
     */
    public void removeByValue(V v) {
        K k = inverseMap.remove(v);
        if (k != null) {
            map.remove(k);
        }
    }

    /**
     * Gets the size of the map.
     *
     * @return the size of the map
     */
    public int size() {
        return map.size();
    }

    /**
     * removes all entries from the map.
     */
    public void clear() {
        map.clear();
        inverseMap.clear();
    }

    /**
     * Checks if the map is empty.
     *
     * @return true, if the map is empty
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }


    /**
     * Checks if the map contains the key.
     *
     * @param k the key
     *
     * @return true, if the map contains the key
     */
    public boolean containsKey(K k) {
        return map.containsKey(k);
    }

    /**
     * Checks if the map contains the value.
     *
     * @param v the value
     *
     * @return true, if the map contains the value
     */
    public boolean containsValue(V v) {
        return inverseMap.containsKey(v);
    }
}
