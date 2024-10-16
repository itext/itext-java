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

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class BiMapTest extends ExtendedITextTest {

    @Test
    public void sizeTest01() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        Assertions.assertEquals(0, map.size());
    }

    @Test
    public void sizeTest02() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assertions.assertEquals(1, map.size());
    }

    @Test
    public void isEmptyTest01() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assertions.assertFalse(map.isEmpty());
    }


    @Test
    public void putTest() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assertions.assertEquals(1, (int) map.getByKey("a"));
        Assertions.assertEquals("a", map.getByValue(1));
    }

    @Test
    public void putOnExistingKey() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.put("a", 2);
        Assertions.assertEquals(2, (int) map.getByKey("a"));
        Assertions.assertEquals("a", map.getByValue(2));
    }

    @Test
    public void putOnExistingValue() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.put("b", 1);
        Assertions.assertEquals(1, (int) map.getByKey("b"));
        Assertions.assertEquals("b", map.getByValue(1));
    }

    @Test
    public void putOnExistingKeyAndValue() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.put("a", 1);
        Assertions.assertEquals(1, (int) map.getByKey("a"));
        Assertions.assertEquals("a", map.getByValue(1));
    }

    @Test
    public void putMultipleValues() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        Assertions.assertEquals(1, (int) map.getByKey("a"));
        Assertions.assertEquals("a", map.getByValue(1));
        Assertions.assertEquals(2, (int) map.getByKey("b"));
        Assertions.assertEquals("b", map.getByValue(2));
        Assertions.assertEquals(3, (int) map.getByKey("c"));
        Assertions.assertEquals("c", map.getByValue(3));
        Assertions.assertEquals(3, map.size());
    }


    @Test
    public void clearTest() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.clear();
        Assertions.assertEquals(0, map.size());
    }

    @Test
    public void containsKeyTest() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assertions.assertTrue(map.containsKey("a"));
    }

    @Test
    public void containsValueTest() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assertions.assertTrue(map.containsValue(1));
    }

    @Test
    public void getByValue() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assertions.assertEquals(1, (int) map.getByKey("a"));
    }

    @Test
    public void getByKey() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assertions.assertEquals("a", map.getByValue(1));
    }

    @Test
    public void removeByKey() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.removeByKey("a");
        Assertions.assertEquals(0, map.size());
    }

    @Test
    public void removeByValue() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.removeByValue(1);
        Assertions.assertEquals(0, map.size());
    }

    @Test
    public void removeOnEmptyMap() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.removeByKey("a");
        map.removeByValue(1);
        Assertions.assertEquals(0, map.size());
    }

}
