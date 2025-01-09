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
package com.itextpdf.kernel.numbering;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class GreekAlphabetNumberingTest extends ExtendedITextTest {

    @Test
    public void testUpperCase() {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= 25; i++) {
            builder.append(GreekAlphabetNumbering.toGreekAlphabetNumber(i, true));
        }
        // 25th symbol is `AA`, i.e. alphabet has 24 letters.
        Assertions.assertEquals("ΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩΑΑ", builder.toString());
    }

    @Test
    public void testLowerCase() {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= 25; i++) {
            builder.append(GreekAlphabetNumbering.toGreekAlphabetNumber(i, false));
        }
        // 25th symbol is `αα`, i.e. alphabet has 24 letters.
        Assertions.assertEquals("αβγδεζηθικλμνξοπρστυφχψωαα", builder.toString());
    }

    @Test
    public void testUpperCaseSymbol() {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= 25; i++) {
            builder.append(GreekAlphabetNumbering.toGreekAlphabetNumber(i, true, true));
        }
        // Symbol font use regular WinAnsi codes for greek letters.
        Assertions.assertEquals("ABGDEZHQIKLMNXOPRSTUFCYWAA", builder.toString());
    }

    @Test
    public void testLowerCaseSymbol() {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= 25; i++) {
            builder.append(GreekAlphabetNumbering.toGreekAlphabetNumber(i, false, true));
        }
        // Symbol font use regular WinAnsi codes for greek letters.
        Assertions.assertEquals("abgdezhqiklmnxoprstufcywaa", builder.toString());
    }

    @Test
    public void intIsNotEnoughForInternalCalculationsTest() {
        Assertions.assertEquals("ζλαββωσ", GreekAlphabetNumbering.toGreekAlphabetNumberLowerCase(1234567890));
    }
}
