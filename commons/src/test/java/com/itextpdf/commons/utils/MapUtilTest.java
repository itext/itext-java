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
package com.itextpdf.commons.utils;

import com.itextpdf.test.ExtendedITextTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class MapUtilTest extends ExtendedITextTest {

    @Test
    public void nullMapsAreEqualTest() {
        Assertions.assertTrue(MapUtil.equals(null, null));
    }

    @Test
    public void nullMapIsNotEqualToEmptyMapTest() {
        Assertions.assertFalse(MapUtil.equals(new HashMap<String, String>(), null));
        Assertions.assertFalse(MapUtil.equals(null, new HashMap<String, String>()));
    }

    @Test
    public void mapsOfDifferentTypesAreNotEqualTest() {
        Assertions.assertFalse(MapUtil.equals(new HashMap<String, String>(), new TreeMap<>()));
    }

    @Test
    public void mapsOfDifferentSizeAreNotEqualTest() {
        Map<String, String> m1 = new HashMap<>();
        m1.put("m1", "m1");

        Map<String, String> m2 = new HashMap<>();
        m2.put("m1", "m1");
        m2.put("m2", "m2");

        Assertions.assertFalse(MapUtil.equals(m1, m2));
    }

    @Test
    public void nullValueInMapTest() {
        Map<String, String> m1 = Collections.<String, String>singletonMap("nullKey", null);
        Map<String, String> m2 = Collections.singletonMap("notNullKey", "notNull");

        Assertions.assertFalse(MapUtil.equals(m1, m2));
    }

    @Test
    public void mapsWithDifferentKeysAreNotEqualTest() {
        Map<String, String> m1 = new HashMap<>();
        m1.put("m1", "value");

        Map<String, String> m2 = new HashMap<>();
        m2.put("m2", "value");
        Assertions.assertFalse(MapUtil.equals(m1, m2));
    }

    @Test
    public void mapsWithDifferentValuesAreNotEqualTest() {
        Map<String, String> m1 = new HashMap<>();
        m1.put("key", "m1");

        Map<String, String> m2 = new HashMap<>();
        m2.put("key", "m2");
        Assertions.assertFalse(MapUtil.equals(m1, m2));
    }

    @Test
    public void equalArraysTest() {
        Map<String, String> m1 = new HashMap<>();
        m1.put("key", "value");

        Map<String, String> m2 = new HashMap<>();
        m2.put("key", "value");
        Assertions.assertTrue(MapUtil.equals(m1, m2));
    }

    @Test
    public void putIfNotNullTest() {
        Map<String, String> m1 = new HashMap<>();
        MapUtil.putIfNotNull(m1, "key", null);
        Assertions.assertTrue(m1.isEmpty());
        MapUtil.putIfNotNull(m1, "key", "value");
        Assertions.assertFalse(m1.isEmpty());
        Assertions.assertEquals("value", m1.get("key"));

    }

    @Test
    public void nullMapsEqualEqualHashCodeTest() {
        Assertions.assertEquals(MapUtil.getHashCode((Map<String, String>)null), MapUtil.getHashCode((Map<String, String>)null));
    }

    @Test
    public void nullMapEmptyMapDiffHashCodeTest() {
        Assertions.assertEquals(MapUtil.getHashCode((Map<String, String>)null), MapUtil.getHashCode(new HashMap<String, String>()));
    }

    @Test
    public void mapsOfDifferentTypesHashCodeTest() {
        Assertions.assertEquals(MapUtil.getHashCode(new TreeMap<>()),
                MapUtil.getHashCode(new HashMap<String, String>()));
    }

    @Test
    public void equalMapsHashCodeTest() {
        Map<String, String> m1 = new HashMap<>();
        m1.put("key", "value");

        Map<String, String> m2 = new HashMap<>();
        m2.put("key", "value");
        Assertions.assertEquals(MapUtil.getHashCode(m1), MapUtil.getHashCode(m2));
    }

    @Test
    public void mapsMergeTest() {
        Map<Integer, Integer> destination = new HashMap<>();
        destination.put(1, 5);
        destination.put(2, 5);
        destination.put(4, 5);
        Map<Integer, Integer> source = new HashMap<>();
        source.put(1, 10);
        source.put(2, 10);
        source.put(3, 10);
        MapUtil.merge(destination, Collections.unmodifiableMap(source), (d, s) -> d + s);

        Map<Integer, Integer> expectedMap = new HashMap<>();
        expectedMap.put(1, 15);
        expectedMap.put(2, 15);
        expectedMap.put(3, 10);
        expectedMap.put(4, 5);
        Assertions.assertEquals(expectedMap, destination);
    }

    @Test
    public void sameMapsMergeTest() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 5);
        map.put(2, 5);
        map.put(4, 5);
        Map<Integer, Integer> expectedMap = new HashMap<>(map);

        MapUtil.merge(map, map, (d, s) -> d + s);
        Assertions.assertEquals(expectedMap, map);
    }
}
