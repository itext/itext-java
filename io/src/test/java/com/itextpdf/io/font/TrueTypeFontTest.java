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
package com.itextpdf.io.font;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TrueTypeFontTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/io/font/TrueTypeFontTest/";

    @Test
    public void notoSansJpCmapTest() throws IOException, InterruptedException {
        // 信
        char jpChar = '\u4FE1';

        FontProgram fontProgram = FontProgramFactory.createFont(sourceFolder + "NotoSansJP-Regular.otf");
        Glyph glyph = fontProgram.getGlyph(jpChar);

        Assert.assertArrayEquals(new char[] {jpChar}, glyph.getUnicodeChars());
        Assert.assertEquals(20449, glyph.getUnicode());

        // TODO DEVSIX-5767 actual expected value is 0x27d3
        Assert.assertEquals(0x0a72, glyph.getCode());
    }
}
