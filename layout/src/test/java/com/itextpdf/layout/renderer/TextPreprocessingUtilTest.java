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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TextPreprocessingUtilTest extends ExtendedITextTest {
    private static PdfFont pdfFont;

    @BeforeClass
    public static void initializeFont() throws IOException {
        pdfFont = PdfFontFactory.createFont();
    }

    @Test
    public void enSpaceTest() {
        specialWhitespaceGlyphTest('\u2002');
    }

    @Test
    public void emSpaceTest() {
        specialWhitespaceGlyphTest('\u2003');
    }

    @Test
    public void thinSpaceTest() {
        specialWhitespaceGlyphTest('\u2009');
    }

    @Test
    public void horizontalTabulationTest() {
        specialWhitespaceGlyphTest('\t');
    }

    @Test
    public void regularSymbolTest() {
        GlyphLine glyphLine = new GlyphLine();
        Glyph regularGlyph = pdfFont.getGlyph('a');
        glyphLine.add(0, regularGlyph);

        TextPreprocessingUtil.replaceSpecialWhitespaceGlyphs(glyphLine, pdfFont);

        Glyph glyph = glyphLine.get(0);
        Assert.assertEquals(regularGlyph, glyph);
    }

    private void specialWhitespaceGlyphTest(int unicode) {
        GlyphLine glyphLine = new GlyphLine();
        // Create a new glyph, because it is a special glyph, and it is not contained in the regular font
        glyphLine.add(0, new Glyph(0, unicode));

        TextPreprocessingUtil.replaceSpecialWhitespaceGlyphs(glyphLine, pdfFont);

        Glyph glyph = glyphLine.get(0);
        Glyph space = pdfFont.getGlyph('\u0020');
        Assert.assertEquals(space.getCode(), glyph.getCode());
        Assert.assertEquals(space.getWidth(), glyph.getWidth());
        Assert.assertEquals(space.getUnicode(), glyph.getUnicode());
        Assert.assertArrayEquals(TextUtil.convertFromUtf32(unicode), glyph.getChars());
    }
}
