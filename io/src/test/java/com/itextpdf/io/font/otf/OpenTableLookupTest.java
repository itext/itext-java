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
package com.itextpdf.io.font.otf;

import com.itextpdf.io.font.otf.OpenTableLookup.GlyphIndexer;
import com.itextpdf.test.ExtendedITextTest;

import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class OpenTableLookupTest extends ExtendedITextTest {

    @Test
    public void idxTest() {
        GlyphIndexer glyphIndexer = new GlyphIndexer();
        glyphIndexer.setIdx(2);

        Assertions.assertEquals(2, glyphIndexer.getIdx());
    }

    @Test
    public void glyphTest() {
        Glyph glyph = new Glyph(200, 200, 200);

        GlyphIndexer glyphIndexer = new GlyphIndexer();
        glyphIndexer.setGlyph(glyph);

        Assertions.assertEquals(200, glyphIndexer.getGlyph().getWidth());
        Assertions.assertEquals(200, glyphIndexer.getGlyph().getCode());
        Assertions.assertEquals(200, glyphIndexer.getGlyph().getUnicode());
    }

    @Test
    public void glyphLineTest() {
        Glyph glyph = new Glyph(200, 200, 200);
        GlyphLine glyphLine = new GlyphLine(Collections.singletonList(glyph));

        GlyphIndexer glyphIndexer = new GlyphIndexer();
        glyphIndexer.setLine(glyphLine);

        Assertions.assertEquals(0, glyphIndexer.getLine().getIdx());
        Assertions.assertEquals(0, glyphIndexer.getLine().getStart());
        Assertions.assertEquals(1, glyphIndexer.getLine().getEnd());
    }
}

