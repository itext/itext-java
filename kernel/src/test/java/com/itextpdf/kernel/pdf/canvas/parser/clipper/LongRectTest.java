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
package com.itextpdf.kernel.pdf.canvas.parser.clipper;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class LongRectTest extends ExtendedITextTest {

    @Test
    public void defaultConstructorTest() {
        final LongRect rect = new LongRect();

        Assertions.assertEquals(0, rect.right);
        Assertions.assertEquals(0, rect.bottom);
        Assertions.assertEquals(0, rect.left);
        Assertions.assertEquals(0, rect.top);
    }

    @Test
    public void longParamConstructorTest() {
        final LongRect rect = new LongRect(5, 15, 6, 10);

        Assertions.assertEquals(5, rect.left);
        Assertions.assertEquals(15, rect.top);
        Assertions.assertEquals(6, rect.right);
        Assertions.assertEquals(10, rect.bottom);
    }

    @Test
    public void copyConstructorTest() {
        final LongRect rect = new LongRect(5, 15, 6, 10);

        final LongRect newRect = new LongRect(rect);

        Assertions.assertEquals(10, newRect.bottom);
        Assertions.assertEquals(6, newRect.right);
        Assertions.assertEquals(5, newRect.left);
        Assertions.assertEquals(15, newRect.top);
    }
}
