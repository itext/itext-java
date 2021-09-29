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
package com.itextpdf.io.font.otf;

import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class GposLookupType8Test extends ExtendedITextTest {

    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/GposLookupType8Test/";

    @Test
    public void verifyXAdvanceIsAppliedForContextualPositioning() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "Padauk-Regular.ttf");
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();

        List<Glyph> glyphs = Arrays.asList(fontProgram.getGlyphByCode(233), fontProgram.getGlyphByCode(163),
                fontProgram.getGlyphByCode(158), fontProgram.getGlyphByCode(227));
        GlyphLine gl = new GlyphLine(glyphs);

        GposLookupType8 lookup = (GposLookupType8) gposTableReader.getLookupTable(92);

        Assert.assertEquals(0, gl.get(2).getXAdvance());
        Assert.assertTrue(lookup.transformLine(gl));
        Assert.assertEquals(28, gl.get(2).getXAdvance());
    }

    @Test
    public void verifyXAdvanceIsAppliedForPosTableLookup8Format2() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "NotoSansMyanmar-Regular.ttf");
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();

        List<Glyph> glyphs = Arrays.asList(fontProgram.getGlyphByCode(29),
                fontProgram.getGlyphByCode(26),
                fontProgram.getGlyphByCode(431),
                fontProgram.getGlyphByCode(415),
                fontProgram.getGlyphByCode(199),
                fontProgram.getGlyphByCode(26),
                fontProgram.getGlyphByCode(407),
                fontProgram.getGlyphByCode(210),
                fontProgram.getGlyphByCode(417));
        GlyphLine gl = new GlyphLine(glyphs);

        GposLookupType8 lookup = (GposLookupType8) gposTableReader.getLookupTable(0);

        Assert.assertEquals(0, gl.get(1).getXAdvance());
        Assert.assertTrue(lookup.transformLine(gl));
        Assert.assertEquals(134, gl.get(1).getXAdvance());
    }

    @Test
    public void verifyXAdvanceIsNotAppliedForUnsatisfiedContextualPositioning() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "Padauk-Regular.ttf");
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();

        List<Glyph> glyphs = Arrays.asList(fontProgram.getGlyphByCode(233), fontProgram.getGlyphByCode(163),
                fontProgram.getGlyphByCode(158), fontProgram.getGlyphByCode(233));
        GlyphLine gl = new GlyphLine(glyphs);

        GposLookupType8 lookup = (GposLookupType8) gposTableReader.getLookupTable(92);

        Assert.assertFalse(lookup.transformLine(gl));
        for (int i = 0; i < gl.size(); i++) {
            Assert.assertEquals(0, gl.get(i).getXAdvance());
            Assert.assertEquals(0, gl.get(i).getYAdvance());
        }
    }

}
