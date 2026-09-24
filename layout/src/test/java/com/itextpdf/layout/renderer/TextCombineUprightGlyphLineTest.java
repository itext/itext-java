/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class TextCombineUprightGlyphLineTest extends ExtendedITextTest {
    @Test
    public void eachRunIsOnePlaceholderAndNewlinesArePreservedTest() {
        String[] sources = {"", "12 34-56", "12\r\n34\n", "\n\r\n\r12\n"};
        String[] expected = {"", "\uFFFC", "\uFFFC\r\n\uFFFC\n", "\uFFFC\n\uFFFC\r\n\uFFFC\r\uFFFC\n"};
        for (int i = 0; i < sources.length; ++i) {
            GlyphLine source = glyphs(sources[i]);
            TextCombineUprightGlyphLine placeholders = new TextCombineUprightGlyphLine(source, 800, -200);
            Assertions.assertEquals(expected[i], placeholders.toString());
            Assertions.assertEquals(sources[i], placeholders.restore(placeholders).toString());
            Assertions.assertEquals(sources[i], source.toString());
        }
    }

    @Test
    public void restoreKeepsSourceOffsetsShapingAndActualTextTest() {
        GlyphLine source = glyphs("[[12\r\n34\n]]");
        source.setStart(2);
        source.setEnd(9);
        source.get(2).setXAdvance((short) 75);
        source.setActualText(2, 4, "twelve");
        TextCombineUprightGlyphLine placeholders = new TextCombineUprightGlyphLine(source, 900, -250);

        GlyphLine placed = new GlyphLine(placeholders);
        placed.setEnd(1);
        GlyphLine restored = placeholders.restore(placed);
        Assertions.assertEquals(2, restored.getStart());
        Assertions.assertEquals(4, restored.getEnd());
        Assertions.assertEquals("twelve", restored.toString());
        Assertions.assertSame(source.get(2), restored.get(2));
        Assertions.assertEquals(75, restored.get(2).getXAdvance());

        Assertions.assertEquals(6, placeholders.getSourcePosition(3));
        GlyphLine overflow = new GlyphLine(placeholders);
        overflow.setStart(3);
        Assertions.assertEquals("34\n", placeholders.restore(overflow).toString());
        Assertions.assertEquals("twelve\r\n34\n", source.toString());
    }

    @Test
    public void emptyRunAndUnplacedLineRestoreToEmptySourceRangesTest() {
        GlyphLine source = glyphs("\r\n12");
        TextCombineUprightGlyphLine placeholders = new TextCombineUprightGlyphLine(source, 800, -200);
        GlyphLine placed = new GlyphLine(placeholders);
        placed.setEnd(1);
        Assertions.assertEquals("", placeholders.restore(placed).toString());
        placed.setStart(-1);
        placed.setEnd(-1);
        Assertions.assertEquals("", placeholders.restore(placed).toString());
        Assertions.assertEquals("\r\n12", source.toString());
    }

    private static GlyphLine glyphs(String text) {
        GlyphLine result = new GlyphLine();
        for (int i = 0; i < text.length(); ++i) {
            result.add(new Glyph(text.charAt(i), 500, text.charAt(i)));
        }
        result.setEnd(result.size());
        return result;
    }
}
