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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
