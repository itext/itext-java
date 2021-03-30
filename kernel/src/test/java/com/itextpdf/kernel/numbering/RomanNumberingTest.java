/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class RomanNumberingTest extends ExtendedITextTest {
    @Test
    public void negativeConvertTest() {
        Assert.assertEquals("-vi", RomanNumbering.convert(-6));
    }

    @Test
    public void zeroConvertTest() {
        Assert.assertEquals("", RomanNumbering.convert(0));
    }

    @Test
    public void convertTest() {
        Assert.assertEquals("mdclxvi", RomanNumbering.convert(1666));
        Assert.assertEquals("mcmlxxxiii", RomanNumbering.convert(1983));
        Assert.assertEquals("mmm", RomanNumbering.convert(3000));
        Assert.assertEquals("|vi|", RomanNumbering.convert(6000));
        Assert.assertEquals("|vi|dccxxxiii", RomanNumbering.convert(6733));
    }

    @Test
    public void toRomanTest() {
        String expected = "dcclvi";
        Assert.assertEquals(expected.toUpperCase(), RomanNumbering.toRoman(756, true));
        Assert.assertEquals(expected.toLowerCase(), RomanNumbering.toRoman(756, false));
    }

    @Test
    public void toRomanUpperCaseTest() {
        Assert.assertEquals("CCCLXXXVI", RomanNumbering.toRomanUpperCase(386));
    }

    @Test
    public void toRomanLowerCaseTest() {
        Assert.assertEquals("xxvi", RomanNumbering.toRomanLowerCase(26));
    }
}
