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

import com.itextpdf.test.ExtendedITextTest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ConcurrentWeakMapTest extends ExtendedITextTest {
    @Test
    public void sizeTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);
        map.put(3, 0);
        map.put(6, 2);
        map.put(5, 2);
        Assertions.assertEquals(3, map.size());
    }

    @Test
    public void isEmptyMapNotEmptyTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);
        Assertions.assertFalse(map.isEmpty());
    }

    @Test
    public void isEmptyMapEmptyTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        Assertions.assertTrue(map.isEmpty());
    }

    @Test
    public void containsKeyTrueTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);
        Assertions.assertTrue(map.containsKey(5));
    }

    @Test
    public void containsKeyFalseTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);
        Assertions.assertFalse(map.containsKey(6));
    }

    @Test
    public void containsValueTrueTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);
        Assertions.assertTrue(map.containsValue(6));
    }

    @Test
    public void containsValueFalseTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);
        Assertions.assertFalse(map.containsValue(5));
    }

    @Test
    public void getTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);
        Assertions.assertEquals(6, (int) map.get(5));
    }

    @Test
    public void putTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);
        Assertions.assertEquals(6, (int) map.put(5, 10));
    }

    @Test
    public void removeTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);
        Assertions.assertEquals(6, (int) map.remove(5));
    }

    @Test
    public void putAllTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);
        
        Map<Integer, Integer> anotherMap = new HashMap<>();
        anotherMap.put(5, 10);
        anotherMap.put(4, 3);
        anotherMap.put(3, 7);
        
        map.putAll(anotherMap);
        
        Assertions.assertEquals(10, (int) map.get(5));
        Assertions.assertEquals(3, map.size());
    }

    @Test
    public void clearTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);
        map.put(3, 5);
        map.put(2, 8);
        
        map.clear();
        
        Assertions.assertEquals(0, map.size());
    }

    @Test
    public void keySetTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);

        Map<Integer, Integer> anotherMap = new HashMap<>();
        anotherMap.put(5, 10);
        anotherMap.put(4, 3);
        anotherMap.put(3, 7);

        map.putAll(anotherMap);
        
        Assertions.assertEquals(anotherMap.keySet(), map.keySet());
    }

    @Test
    public void valuesTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);

        Map<Integer, Integer> anotherMap = new HashMap<>();
        anotherMap.put(5, 10);
        anotherMap.put(4, 3);
        anotherMap.put(3, 7);

        map.putAll(anotherMap);

        Collection<Integer> values = map.values();
        Assertions.assertEquals(3, values.size());
        Assertions.assertTrue(values.contains(10));
        Assertions.assertFalse(values.contains(6));
    }

    @Test
    public void entrySetTest() {
        ConcurrentWeakMap<Integer, Integer> map = new ConcurrentWeakMap<>();
        map.put(5, 6);

        Map<Integer, Integer> anotherMap = new HashMap<>();
        anotherMap.put(5, 10);
        anotherMap.put(4, 3);
        anotherMap.put(3, 7);

        map.putAll(anotherMap);

        Assertions.assertEquals(anotherMap.entrySet(), map.entrySet());
    }
}
