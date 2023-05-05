/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class FontCharacteristicsUtilsTest extends ExtendedITextTest {
    @Test
    public void testNormalizingThinFontWeight() {
        Assert.assertEquals(100, FontCharacteristicsUtils.normalizeFontWeight((short) -10000));

        Assert.assertEquals(100, FontCharacteristicsUtils.normalizeFontWeight((short) 0));

        Assert.assertEquals(100, FontCharacteristicsUtils.normalizeFontWeight((short) 50));

        Assert.assertEquals(100, FontCharacteristicsUtils.normalizeFontWeight((short) 100));
    }

    @Test
    public void testNormalizingHeavyFontWeight() {
        Assert.assertEquals(900, FontCharacteristicsUtils.normalizeFontWeight((short) 900));

        Assert.assertEquals(900, FontCharacteristicsUtils.normalizeFontWeight((short) 1600));

        Assert.assertEquals(900, FontCharacteristicsUtils.normalizeFontWeight((short) 23000));
    }

    @Test
    public void testNormalizingNormalFontWeight() {
        Assert.assertEquals(200, FontCharacteristicsUtils.normalizeFontWeight((short) 220));

        Assert.assertEquals(400, FontCharacteristicsUtils.normalizeFontWeight((short) 456));

        Assert.assertEquals(500, FontCharacteristicsUtils.normalizeFontWeight((short) 550));

        Assert.assertEquals(600, FontCharacteristicsUtils.normalizeFontWeight((short) 620));

        Assert.assertEquals(700, FontCharacteristicsUtils.normalizeFontWeight((short) 780));
    }

    @Test
    public void testParsingIncorrectFontWeight() {
        Assert.assertEquals((short) -1, FontCharacteristicsUtils.parseFontWeight(""));

        Assert.assertEquals((short) -1, FontCharacteristicsUtils.parseFontWeight(null));

        Assert.assertEquals((short) -1, FontCharacteristicsUtils.parseFontWeight("dfgdgdfgdfgdf"));

        Assert.assertEquals((short) -1, FontCharacteristicsUtils.parseFontWeight("italic"));
    }

    @Test
    public void testParsingNumberFontWeight() {
        Assert.assertEquals((short) 100, FontCharacteristicsUtils.parseFontWeight("-1"));

        Assert.assertEquals((short) 100, FontCharacteristicsUtils.parseFontWeight("50"));

        Assert.assertEquals((short) 300, FontCharacteristicsUtils.parseFontWeight("360"));

        Assert.assertEquals((short) 900, FontCharacteristicsUtils.parseFontWeight("25000"));
    }


    @Test
    public void testParseAllowedFontWeight() {
        Assert.assertEquals((short) 400, FontCharacteristicsUtils.parseFontWeight("normal"));

        Assert.assertEquals((short) 700, FontCharacteristicsUtils.parseFontWeight("bold"));
    }
}
