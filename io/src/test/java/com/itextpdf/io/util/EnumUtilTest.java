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
package com.itextpdf.io.util;

import com.itextpdf.test.ExtendedITextTest;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class EnumUtilTest extends ExtendedITextTest {
    @Test
    public void testEnumUtilSameAmount() {
        Assertions.assertEquals(3, EnumUtil.getAllValuesOfEnum(TestEnum1.class).size());
    }

    @Test
    public void testEnumUtilSameValues() {
        List<TestEnum1> list = EnumUtil.getAllValuesOfEnum(TestEnum1.class);
        Assertions.assertTrue(list.contains(TestEnum1.A));
        Assertions.assertTrue(list.contains(TestEnum1.B));
        Assertions.assertTrue(list.contains(TestEnum1.C));
        Assertions.assertEquals(TestEnum1.A, list.get(0));
        Assertions.assertEquals(TestEnum1.B, list.get(1));
        Assertions.assertEquals(TestEnum1.C, list.get(2));
    }
}

enum TestEnum1 {
    A, B, C
}
