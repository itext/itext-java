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
package com.itextpdf.io.font;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class FontEncodingTest extends ExtendedITextTest {

    @Test
    public void notSetDifferenceToMinus1IndexTest() {
        FontEncoding encoding = FontEncoding.createEmptyFontEncoding();

        String[] initialDifferences = (String[]) encoding.differences.clone();
        encoding.setDifference(-1, "a");

        Assert.assertArrayEquals(initialDifferences, encoding.differences);
    }

    @Test
    public void notSetDifferenceTo256IndexTest() {
        FontEncoding encoding = FontEncoding.createEmptyFontEncoding();

        String[] initialDifferences = (String[]) encoding.differences.clone();
        encoding.setDifference(256, "a");

        Assert.assertArrayEquals(initialDifferences, encoding.differences);
    }

    @Test
    public void setDifferenceToZeroIndexTest() {
        FontEncoding encoding = FontEncoding.createEmptyFontEncoding();

        encoding.setDifference(0, "a");

        Assert.assertEquals("a", encoding.differences[0]);
    }

    @Test
    public void setDifferenceTo255IndexTest() {
        FontEncoding encoding = FontEncoding.createEmptyFontEncoding();

        encoding.setDifference(255, "a");

        Assert.assertEquals("a", encoding.differences[255]);
    }

    @Test
    public void getNullDifferenceTest() {
        FontEncoding encoding = FontEncoding.createEmptyFontEncoding();

        Assert.assertNull(encoding.getDifference(0));
    }

    @Test
    public void setDifferenceAndGetTest() {
        FontEncoding encoding = FontEncoding.createEmptyFontEncoding();

        encoding.setDifference(0, "a");

        Assert.assertEquals("a", encoding.getDifference(0));
    }
}
