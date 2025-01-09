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

import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class GposLookupType1Test extends ExtendedITextTest {
    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/GposLookupType1Test/";

    @Test
    public void verifyXAdvanceIsAppliedSubFormat1() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "NotoSansMyanmar-Regular.ttf");
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();
        GposLookupType1 lookup = (GposLookupType1) gposTableReader.getLookupTable(29);
        List<Glyph> glyphs = Arrays.asList(new Glyph(fontProgram.getGlyphByCode(174)),
                new Glyph(fontProgram.getGlyphByCode(5)));

        GlyphLine gl = new GlyphLine(glyphs);
        gl.setIdx(0);

        Assertions.assertEquals(0, gl.get(0).getXAdvance());

        Assertions.assertTrue(lookup.transformOne(gl));

        Assertions.assertEquals(219, gl.get(0).getXAdvance());
    }

    @Test
    public void verifyPositionIsNotAppliedForIrrelevantGlyphSubFormat1() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "NotoSansMyanmar-Regular.ttf");
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();
        GposLookupType1 lookup = (GposLookupType1) gposTableReader.getLookupTable(29);
        List<Glyph> glyphs = Arrays.asList(new Glyph(fontProgram.getGlyphByCode(5)),
                new Glyph(fontProgram.getGlyphByCode(174)));

        GlyphLine gl = new GlyphLine(glyphs);
        gl.setIdx(0);

        Assertions.assertEquals(0, gl.get(0).getXAdvance());

        Assertions.assertFalse(lookup.transformOne(gl));

        Assertions.assertEquals(0, gl.get(0).getXAdvance());
    }

    @Test
    public void verifyDifferentXAdvanceIsAppliedSubFormat2() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "NotoSansMyanmar-Regular.ttf");
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();
        GposLookupType1 lookup = (GposLookupType1) gposTableReader.getLookupTable(16);

        List<Glyph> glyphs = Arrays.asList(new Glyph(fontProgram.getGlyphByCode(401)),
                new Glyph(fontProgram.getGlyphByCode(5)));
        GlyphLine gl = new GlyphLine(glyphs);
        Assertions.assertEquals(0, gl.get(0).getXAdvance());
        Assertions.assertTrue(lookup.transformOne(gl));
        Assertions.assertEquals(109, gl.get(0).getXAdvance());

        // Subtable type 2 defines different GposValueRecords for different coverage glyphs

        glyphs = Arrays.asList(new Glyph(fontProgram.getGlyphByCode(508)),
                new Glyph(fontProgram.getGlyphByCode(5)));
        gl = new GlyphLine(glyphs);
        Assertions.assertEquals(0, gl.get(0).getXAdvance());
        Assertions.assertTrue(lookup.transformOne(gl));
        Assertions.assertEquals(158, gl.get(0).getXAdvance());
    }

}
