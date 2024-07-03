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
package com.itextpdf.layout.splitting;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.test.ExtendedITextTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class BreakAllSplitCharactersTest extends ExtendedITextTest {

    private static final char charWithFalse = '\u201b';

    @Test
    public void lastCharTest() {
        Assertions.assertFalse(isSplitCharacter(new int[]{charWithFalse, charWithFalse, charWithFalse}, 1));
        Assertions.assertTrue(isSplitCharacter(new int[]{charWithFalse, charWithFalse, charWithFalse}, 2));
    }

    @Test
    public void currentIsNotUnicodeTest() {
        Assertions.assertTrue(isSplitCharacter(new int[]{charWithFalse, -1, charWithFalse}, 1));
    }

    @Test
    public void nextIsNotUnicodeTest() {
        Assertions.assertTrue(isSplitCharacter(new int[]{charWithFalse, charWithFalse, -1}, 1));
    }

    @Test
    public void beforeSpaceTest() {
        Assertions.assertTrue(isSplitCharacter(new int[]{'a', 'a', ' '}, 0));
        Assertions.assertFalse(isSplitCharacter(new int[]{'a', 'a', ' '}, 1));
        Assertions.assertTrue(isSplitCharacter(new int[]{'a', ' ', ' '}, 1));
        Assertions.assertTrue(isSplitCharacter(new int[]{'a', '-', ' '}, 1));
        Assertions.assertTrue(isSplitCharacter(new int[]{'a', '\u2010', ' '}, 1));
        Assertions.assertTrue(isSplitCharacter(new int[]{'a', '\u2004', ' '}, 1));
    }

    @Test
    public void beforeSymbolTest() {
        Assertions.assertFalse(isSplitCharacter(new int[]{charWithFalse, charWithFalse}, 0));
        Assertions.assertTrue(isSplitCharacter(new int[]{charWithFalse, 'a'}, 0));
        // non spacing mark
        Assertions.assertTrue(isSplitCharacter(new int[]{charWithFalse, '\u0303'}, 0));
        // combining mark
        Assertions.assertTrue(isSplitCharacter(new int[]{charWithFalse, '\u093e'}, 0));
        // enclosing mark
        Assertions.assertTrue(isSplitCharacter(new int[]{charWithFalse, '\u0488'}, 0));
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
