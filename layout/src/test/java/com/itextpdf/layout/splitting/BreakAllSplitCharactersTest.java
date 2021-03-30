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
package com.itextpdf.layout.splitting;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BreakAllSplitCharactersTest extends ExtendedITextTest {

    private static final char charWithFalse = '\u201b';

    @Test
    public void lastCharTest() {
        Assert.assertFalse(isSplitCharacter(new int[]{charWithFalse, charWithFalse, charWithFalse}, 1));
        Assert.assertTrue(isSplitCharacter(new int[]{charWithFalse, charWithFalse, charWithFalse}, 2));
    }

    @Test
    public void currentIsNotUnicodeTest() {
        Assert.assertTrue(isSplitCharacter(new int[]{charWithFalse, -1, charWithFalse}, 1));
    }

    @Test
    public void nextIsNotUnicodeTest() {
        Assert.assertTrue(isSplitCharacter(new int[]{charWithFalse, charWithFalse, -1}, 1));
    }

    @Test
    public void beforeSpaceTest() {
        Assert.assertTrue(isSplitCharacter(new int[]{'a', 'a', ' '}, 0));
        Assert.assertFalse(isSplitCharacter(new int[]{'a', 'a', ' '}, 1));
        Assert.assertTrue(isSplitCharacter(new int[]{'a', ' ', ' '}, 1));
        Assert.assertTrue(isSplitCharacter(new int[]{'a', '-', ' '}, 1));
        Assert.assertTrue(isSplitCharacter(new int[]{'a', '\u2010', ' '}, 1));
        Assert.assertTrue(isSplitCharacter(new int[]{'a', '\u2004', ' '}, 1));
    }

    @Test
    public void beforeSymbolTest() {
        Assert.assertFalse(isSplitCharacter(new int[]{charWithFalse, charWithFalse}, 0));
        Assert.assertTrue(isSplitCharacter(new int[]{charWithFalse, 'a'}, 0));
        // non spacing mark
        Assert.assertTrue(isSplitCharacter(new int[]{charWithFalse, '\u0303'}, 0));
        // combining mark
        Assert.assertTrue(isSplitCharacter(new int[]{charWithFalse, '\u093e'}, 0));
        // enclosing mark
        Assert.assertTrue(isSplitCharacter(new int[]{charWithFalse, '\u0488'}, 0));
    }

    private static boolean isSplitCharacter(int[] unicodes, int glyphPosition) {
        return new BreakAllSplitCharacters().isSplitCharacter(createGlyphLine(unicodes), glyphPosition);
    }

    private static GlyphLine createGlyphLine(int[] unicodes) {
        List<Glyph> glyphs = new ArrayList<>();
        for (int unicode : unicodes) {
            glyphs.add(new Glyph(1, unicode));
        }
        return new GlyphLine(glyphs);
    }
}
