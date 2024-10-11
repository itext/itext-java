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
public class GposLookupType7Test extends ExtendedITextTest {
    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/GposLookupType7Test/";

    @Test
    public void verifyXAdvanceIsAppliedForContextualPositioning() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "NotoSansMyanmar-Regular.ttf");
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();
        GposLookupType7 lookup = (GposLookupType7) gposTableReader.getLookupTable(28);
        List<Glyph> glyphs = Arrays.asList(fontProgram.getGlyphByCode(25),
                fontProgram.getGlyphByCode(174), fontProgram.getGlyphByCode(5), fontProgram.getGlyphByCode(411));

        GlyphLine gl = new GlyphLine(glyphs);

        Assertions.assertEquals(0, gl.get(1).getXAdvance());
        Assertions.assertTrue(lookup.transformLine(gl));
        Assertions.assertEquals(219, gl.get(1).getXAdvance());
    }

    @Test
    public void verifyXAdvanceIsNotAppliedForUnsatisfiedContextualPositioning() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "NotoSansMyanmar-Regular.ttf");
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();
        GposLookupType7 lookup = (GposLookupType7) gposTableReader.getLookupTable(28);
        List<Glyph> glyphs = Arrays.asList(fontProgram.getGlyphByCode(1),
                fontProgram.getGlyphByCode(174), fontProgram.getGlyphByCode(5), fontProgram.getGlyphByCode(411));

        GlyphLine gl = new GlyphLine(glyphs);

        Assertions.assertFalse(lookup.transformLine(gl));
        for (int i = 0; i < gl.size(); i++) {
            Assertions.assertEquals(0, gl.get(i).getXAdvance());
            Assertions.assertEquals(0, gl.get(i).getYAdvance());
        }
    }

}
