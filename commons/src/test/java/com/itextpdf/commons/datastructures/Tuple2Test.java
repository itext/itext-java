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
package com.itextpdf.commons.datastructures;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("UnitTest")
public class Tuple2Test extends ExtendedITextTest {

    @Test
    public void testTuple2_StringInt() {
        Tuple2<String, Integer> tuple = new Tuple2<>("test", 1);
        assertEquals("test", tuple.getFirst());
        assertEquals(Integer.valueOf(1), tuple.getSecond());
    }

    @Test
    public void testTuple2_ToString() {
        Tuple2<String, Integer> tuple = new Tuple2<>("test", 1);
        assertEquals("Tuple2{first=test, second=1}", tuple.toString());
    }

    @Test
    public void testTuple2_TestWithNullFirstValue() {
        Tuple2<String, Integer> tuple = new Tuple2<>(null, 1);
        assertNull(tuple.getFirst());
        assertEquals(Integer.valueOf(1), tuple.getSecond());
    }

    @Test
    public void equalsTest() {
        Tuple2<String, Integer> tuple1 = new Tuple2<>("test", 1);
        Tuple2<String, Integer> tuple2 = new Tuple2<>("test", 1);
        assertEquals(tuple1, tuple2);
    }

    @Test
    public void equalsSameTest() {
        Tuple2<String, Integer> tuple = new Tuple2<>("test", 1);
        assertEquals(tuple, tuple);
    }

    @Test
    public void equalsNullTest() {
        Tuple2<String, Integer> tuple = new Tuple2<>("test", 1);
        assertNotEquals(tuple, null);
    }

    @Test
    public void notEqualsTest() {
        Tuple2<String, Integer> tuple1 = new Tuple2<>("test", 1);
        Tuple2<String, Integer> tuple2 = new Tuple2<>("test", 2);
        Tuple2<String, Integer> tuple3 = new Tuple2<>("test2", 2);
        assertNotEquals(tuple1, tuple2);
        assertNotEquals(tuple2, tuple3);
        assertNotEquals(tuple1, tuple3);
    }

    @Test
    public void equalsWithCustomTest() {
        Tuple2<String, Integer> tuple1 = new Tuple2<>("test", 1);
        Tuple2<String, Integer> tuple2 = new CustomTuple2<>("test", 1);
        Tuple2<String, Integer> tuple3 = new CustomTuple2<>("test", 1);
        assertNotEquals(tuple1, tuple2);
        assertEquals(tuple2, tuple3);
    }

    private static class CustomTuple2<T1, T2> extends Tuple2<T1, T2> {
        public CustomTuple2(T1 test, T2 i) {
            super(test, i);
        }
    }
}
