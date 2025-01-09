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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class DefaultSplitCharacterTest extends ExtendedITextTest {
    static List<Glyph> glyphs = new ArrayList<>();

    @BeforeAll
    public static void setup() {
        glyphs.add(new Glyph(1, '-'));
        glyphs.add(new Glyph(1, '5'));
        glyphs.add(new Glyph(1, '2'));

        glyphs.add(new Glyph(1, '5'));
        glyphs.add(new Glyph(1, '-'));
        glyphs.add(new Glyph(1, '5'));

        glyphs.add(new Glyph(1, '5'));
        glyphs.add(new Glyph(1, '7'));
        glyphs.add(new Glyph(1, '-'));

        glyphs.add(new Glyph(1, '-'));
        glyphs.add(new Glyph(1, '-'));
        glyphs.add(new Glyph(1, '7'));

        glyphs.add(new Glyph(1, '5'));
        glyphs.add(new Glyph(1, '-'));
        glyphs.add(new Glyph(1, '-'));
    }

    @Test
    public void beginCharacterTest() {
        Assertions.assertFalse(isPsplitCharacter(0));
    }

    @Test
    public void middleCharacterTest() {
        Assertions.assertTrue(isPsplitCharacter(4));
    }

    @Test
    public void lastCharacterTest() {
        Assertions.assertTrue(isPsplitCharacter(8));
    }

    @Test
    public void firstMiddleCharacterTest() {
        Assertions.assertTrue(isPsplitCharacter(9));
    }

    @Test
    public void lastMiddleCharacterTest() {
        Assertions.assertTrue(isPsplitCharacter(14));
    }

    private static boolean isPsplitCharacter(int glyphPos) {
        GlyphLine text = new GlyphLine(glyphs);
        return new DefaultSplitCharacters().isSplitCharacter(text, glyphPos);
    }
}
