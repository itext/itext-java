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
package com.itextpdf.io.font.otf;

import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class GposLookupType2Test extends ExtendedITextTest {
    private static final String FONT_FOLDER = "./src/test/resources/com/itextpdf/io/font/";
    private static final String DEJAVU_FONT_PATH =FONT_FOLDER + "DejaVuSans.ttf";

    @Test
    public void idxEqualToEndLineGpos2Test() throws IOException {
        TrueTypeFont font = new TrueTypeFont(DEJAVU_FONT_PATH);

        GlyphPositioningTableReader gposTableReader = font.getGposTable();
        GposLookupType2 lookup = (GposLookupType2) gposTableReader.getLookupTable(15);


        List<Glyph> glyphs = Arrays.asList(new Glyph(font.getGlyphByCode(174)),
                new Glyph(font.getGlyphByCode(5)));
        GlyphLine gl = new GlyphLine(glyphs);
        gl.setIdx(2);

        boolean transform = lookup.transformOne(gl);
        Assertions.assertFalse(transform);
    }

    @Test
    public void idxSmallerThanEndLineGpos2Test() throws IOException {
        TrueTypeFont font = new TrueTypeFont(DEJAVU_FONT_PATH);

        GlyphPositioningTableReader gposTableReader = font.getGposTable();
        GposLookupType2 lookup = (GposLookupType2) gposTableReader.getLookupTable(15);


        List<Glyph> glyphs = Arrays.asList(new Glyph(font.getGlyphByCode(174)),
                new Glyph(font.getGlyphByCode(5)));
        GlyphLine gl = new GlyphLine(glyphs);
        gl.setIdx(0);

        boolean transform = lookup.transformOne(gl);
        Assertions.assertFalse(transform);
    }

    @Test
    // We test here GPOS Lookup Type 2 (Pair Adjustment) Format 1 (adjustments for glyph pairs)
    public void subformat1TransformTest() throws IOException {
        TrueTypeFont font = new TrueTypeFont(FONT_FOLDER + "NotoSansKhmer-Regular.ttf");

        GlyphPositioningTableReader gposTableReader = font.getGposTable();
        GposLookupType2 lookup = (GposLookupType2) gposTableReader.getLookupTable(31);


        List<Glyph> glyphs = Arrays.asList(new Glyph(font.getGlyphByCode(387)), new Glyph(font.getGlyphByCode(434)));
        GlyphLine gl = new GlyphLine(glyphs);
        gl.setIdx(0);

        Assertions.assertEquals(0, gl.get(0).getXAdvance());
        boolean transform = lookup.transformOne(gl);
        Assertions.assertTrue(transform);
        Assertions.assertEquals(50, gl.get(0).getXAdvance());
    }

    @Test
    // We test here GPOS Lookup Type 2 (Pair Adjustment) Format 1 (class pair adjustment)
    public void subformat2TransformTest() throws IOException {
        TrueTypeFont font = new TrueTypeFont(DEJAVU_FONT_PATH);

        GlyphPositioningTableReader gposTableReader = font.getGposTable();
        GposLookupType2 lookup = (GposLookupType2) gposTableReader.getLookupTable(15);


        List<Glyph> glyphs = Arrays.asList(new Glyph(font.getGlyphByCode(4960)), new Glyph(font.getGlyphByCode(4970)));
        GlyphLine gl = new GlyphLine(glyphs);
        gl.setIdx(0);

        Assertions.assertEquals(0, gl.get(0).getXAdvance());
        boolean transform = lookup.transformOne(gl);
        Assertions.assertTrue(transform);
        Assertions.assertEquals(-45, gl.get(0).getXAdvance());
    }
}
