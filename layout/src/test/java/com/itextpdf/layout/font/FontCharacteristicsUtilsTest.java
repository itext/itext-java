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
package com.itextpdf.layout.font;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class FontCharacteristicsUtilsTest extends ExtendedITextTest {
    @Test
    public void testNormalizingThinFontWeight() {
        Assertions.assertEquals(100, FontCharacteristicsUtils.normalizeFontWeight((short) -10000));

        Assertions.assertEquals(100, FontCharacteristicsUtils.normalizeFontWeight((short) 0));

        Assertions.assertEquals(100, FontCharacteristicsUtils.normalizeFontWeight((short) 50));

        Assertions.assertEquals(100, FontCharacteristicsUtils.normalizeFontWeight((short) 100));
    }

    @Test
    public void testNormalizingHeavyFontWeight() {
        Assertions.assertEquals(900, FontCharacteristicsUtils.normalizeFontWeight((short) 900));

        Assertions.assertEquals(900, FontCharacteristicsUtils.normalizeFontWeight((short) 1600));

        Assertions.assertEquals(900, FontCharacteristicsUtils.normalizeFontWeight((short) 23000));
    }

    @Test
    public void testNormalizingNormalFontWeight() {
        Assertions.assertEquals(200, FontCharacteristicsUtils.normalizeFontWeight((short) 220));

        Assertions.assertEquals(400, FontCharacteristicsUtils.normalizeFontWeight((short) 456));

        Assertions.assertEquals(500, FontCharacteristicsUtils.normalizeFontWeight((short) 550));

        Assertions.assertEquals(600, FontCharacteristicsUtils.normalizeFontWeight((short) 620));

        Assertions.assertEquals(700, FontCharacteristicsUtils.normalizeFontWeight((short) 780));
    }

    @Test
    public void testParsingIncorrectFontWeight() {
        Assertions.assertEquals((short) -1, FontCharacteristicsUtils.parseFontWeight(""));

        Assertions.assertEquals((short) -1, FontCharacteristicsUtils.parseFontWeight(null));

        Assertions.assertEquals((short) -1, FontCharacteristicsUtils.parseFontWeight("dfgdgdfgdfgdf"));

        Assertions.assertEquals((short) -1, FontCharacteristicsUtils.parseFontWeight("italic"));
    }

    @Test
    public void testParsingNumberFontWeight() {
        Assertions.assertEquals((short) 100, FontCharacteristicsUtils.parseFontWeight("-1"));

        Assertions.assertEquals((short) 100, FontCharacteristicsUtils.parseFontWeight("50"));

        Assertions.assertEquals((short) 300, FontCharacteristicsUtils.parseFontWeight("360"));

        Assertions.assertEquals((short) 900, FontCharacteristicsUtils.parseFontWeight("25000"));
    }


    @Test
    public void testParseAllowedFontWeight() {
        Assertions.assertEquals((short) 400, FontCharacteristicsUtils.parseFontWeight("normal"));

        Assertions.assertEquals((short) 700, FontCharacteristicsUtils.parseFontWeight("bold"));
    }
}
