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
package com.itextpdf.kernel.font;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class Type3FontTest extends ExtendedITextTest {

    @Test
    public void addGlyphTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, 1, 600, null, null);

        Assertions.assertEquals(1, font.getNumberOfGlyphs());
    }

    @Test
    public void addGlyphsWithDifferentUnicodeTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, 1, 600, null, null);
        font.addGlyph(2, 2, 600, null, null);

        Assertions.assertEquals(2, font.getNumberOfGlyphs());
        Assertions.assertEquals(1, font.getGlyphByCode(1).getUnicode());
        Assertions.assertEquals(2, font.getGlyphByCode(2).getUnicode());
    }

    @Test
    public void addGlyphsWithDifferentCodesTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, -1, 600, null, null);
        font.addGlyph(2, -1, 700, null, null);

        Assertions.assertEquals(2, font.getNumberOfGlyphs());
        Assertions.assertEquals(600, font.getGlyphByCode(1).getWidth());
        Assertions.assertEquals(700, font.getGlyphByCode(2).getWidth());
    }

    @Test
    public void replaceGlyphsWithSameUnicodeTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, 1, 600, null, null);
        font.addGlyph(2, 1, 600, null, null);

        Assertions.assertEquals(1, font.getNumberOfGlyphs());
        Assertions.assertEquals(2, font.getGlyph(1).getCode());
    }

    @Test
    public void replaceGlyphWithSameCodeTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, -1, 600, null, null);
        font.addGlyph(1, -1, 700, null, null);

        Assertions.assertEquals(1, font.getNumberOfGlyphs());
        Assertions.assertEquals(700, font.getGlyphByCode(1).getWidth());
    }

    @Test
    public void notAddGlyphWithSameCodeEmptyUnicodeFirstTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, -1, 600, null, null);
        font.addGlyph(1, 100, 600, null, null);

        Assertions.assertEquals(1, font.getNumberOfGlyphs());
        Assertions.assertEquals(1, font.getGlyph(100).getCode());
        Assertions.assertEquals(100, font.getGlyphByCode(1).getUnicode());
    }

    @Test
    public void replaceGlyphWithSameCodeEmptyUnicodeLastTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, 100, 600, null, null);
        font.addGlyph(1, -1, 600, null, null);

        Assertions.assertNull(font.getGlyph(-1));
        Assertions.assertNull(font.getGlyph(100));
        Assertions.assertEquals(1, font.getNumberOfGlyphs());
        Assertions.assertEquals(-1, font.getGlyphByCode(1).getUnicode());
    }
}
