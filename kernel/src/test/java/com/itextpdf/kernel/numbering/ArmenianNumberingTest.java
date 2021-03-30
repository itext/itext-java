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
public class ArmenianNumberingTest extends ExtendedITextTest {
    @Test
    public void negativeToArmenianTest() {
        Assert.assertEquals("", ArmenianNumbering.toArmenian(-10));
    }

    @Test
    public void zeroToArmenianTest() {
        Assert.assertEquals("", ArmenianNumbering.toArmenian(0));
    }

    @Test
    public void toArmenianTest() {
        Assert.assertEquals("\u0554\u054B\u0542\u0539", ArmenianNumbering.toArmenian(9999));
        Assert.assertEquals("\u0552\u054A\u0540\u0534", ArmenianNumbering.toArmenian(7874));
    }

    @Test
    public void numberGreaterThan9999toArmenianTest() {
        Assert.assertEquals("\u0554\u0554\u0554\u0554\u0554\u0554\u0554\u0532", ArmenianNumbering.toArmenian(63002));
    }
}
