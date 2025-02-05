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
public class KeepAllSplitCharactersTest extends ExtendedITextTest {

    @Test
    public void dashAtStartTest() {
        Assertions.assertTrue(isSplitCharacter(new int[]{'-', 'a'}, 0));
    }

    @Test
    public void minusSignAtStartTest() {
        Assertions.assertFalse(isSplitCharacter(new int[]{'-', '5'}, 0));
    }

    @Test
    public void dashBeforeLetterInTheMiddleTest() {
        Assertions.assertTrue(isSplitCharacter(new int[]{'a', ' ', '-', 'a'}, 2));
    }

    @Test
    // TODO: DEVSIX-4863 minus sign for digests should not be split
    public void minusSignInTheMiddleTest() {
        Assertions.assertTrue(isSplitCharacter(new int[]{'a', ' ', '-', '5'}, 2));
    }

    @Test
    public void dashBeforeDigitInTheMiddleTest() {
        Assertions.assertTrue(isSplitCharacter(new int[]{'a', 'a', '-', '5'}, 2));
    }

    @Test
    public void dashAtTheEndTest() {
        int[] unicodes = new int[]{'a', '-'};
        Assertions.assertTrue(isSplitCharacter(unicodes, unicodes.length - 1));
    }

    @Test
    public void dashCharacterTest() {
        Assertions.assertTrue(isSplitCharacter(new int[]{'a', '-', 'a'}, 1));
    }

    @Test
    public void noUnicodeTest() {
        Assertions.assertFalse(isSplitCharacter(new int[]{'a', -1, 'a'}, 1));
    }

    @Test
    public void unicode2010CharacterTest() {
        Assertions.assertTrue(isSplitCharacter(new int[]{'a', '\u2010', 'a'}, 1));
    }

    @Test
    public void unicode2003CharacterTest() {
        Assertions.assertTrue(isSplitCharacter(new int[]{'a', '\u2003', 'a'}, 1));
    }

    @Test
    public void unicode2e81CharacterTest() {
        Assertions.assertFalse(isSplitCharacter(new int[]{'a', '\u2e81', 'a'}, 1));
    }

    private static boolean isSplitCharacter(int[] unicodes, int glyphPosition) {
        return new KeepAllSplitCharacters().isSplitCharacter(createGlyphLine(unicodes), glyphPosition);
    }

    private static GlyphLine createGlyphLine(int[] unicodes) {
        List<Glyph> glyphs = new ArrayList<>();
        for (int unicode : unicodes) {
            glyphs.add(new Glyph(1, unicode));
        }
        return new GlyphLine(glyphs);
    }
}
