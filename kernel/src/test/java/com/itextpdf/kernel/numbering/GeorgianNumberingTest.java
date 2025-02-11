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
public class GeorgianNumberingTest extends ExtendedITextTest {
    @Test
    public void negativeToGeorgianTest() {
        Assertions.assertEquals("", GeorgianNumbering.toGeorgian(-10));
    }

    @Test
    public void zeroToGeorgianTest() {
        Assertions.assertEquals("", GeorgianNumbering.toGeorgian(0));
    }

    @Test
    public void toGeorgianTest() {
        Assertions.assertEquals("\u10F5", GeorgianNumbering.toGeorgian(10000));
        Assertions.assertEquals("\u10F4\u10E8\u10F2\u10D6", GeorgianNumbering.toGeorgian(7967));
    }

    @Test
    public void numberGreaterThan10000toGeorgianTest() {
        Assertions.assertEquals("\u10F5\u10F5\u10F5\u10F5\u10F5\u10F5\u10D2", GeorgianNumbering.toGeorgian(60003));;
    }
}
