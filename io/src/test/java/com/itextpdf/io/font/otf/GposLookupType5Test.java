/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
public class GposLookupType5Test extends ExtendedITextTest {

    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/GposLookupType5Test/";

    @Test
    public void verifyMarkToBaseAttachment() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + "KhmerOS.ttf");
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();
        GposLookupType5 lookup = (GposLookupType5) gposTableReader.getLookupTable(0);
        List<Glyph> glyphs = Arrays.asList(new Glyph(fontProgram.getGlyphByCode(445)), new Glyph(fontProgram.getGlyphByCode(394)));
        GlyphLine gl = new GlyphLine(glyphs);
        gl.idx = 1;
        lookup.transformOne(gl);

        Assert.assertEquals(2, gl.size());
        Assert.assertEquals(445, gl.get(0).getCode());
        Assert.assertEquals(394, gl.get(1).getCode());
        Assert.assertEquals(-1, gl.get(1).getAnchorDelta());
        Assert.assertEquals(756, gl.get(1).getXPlacement());
    }

    @Test
    // TODO on completion of DEVSIX-3732 this test will probably have to be refactored
    //  since we will have to emulate previous substitutions and populate the substitution info
    //  to the glyph line so that mark is attached to the correct component of a ligature
    public void testSelectingCorrectAttachmentAlternative() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + "NotoNaskhArabic-Regular.ttf");
        GlyphLine glyphLine = new GlyphLine(Arrays.asList(fontProgram.getGlyphByCode(513), fontProgram.getGlyphByCode(75)));
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();
        GposLookupType5 lookup = (GposLookupType5) gposTableReader.getLookupTable(3);

        glyphLine.idx = 1;
        lookup.transformOne(glyphLine);

        Assert.assertEquals(2, glyphLine.size());
        Assert.assertEquals(513, glyphLine.get(0).getCode());
        Assert.assertEquals(75, glyphLine.get(1).getCode());
        Assert.assertEquals(-1, glyphLine.get(1).getAnchorDelta());
        Assert.assertEquals(-22, glyphLine.get(1).getXPlacement());
    }

    @Test
    public void testThatNoTransformationsAppliedForNonRelevantGlyphs() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + "NotoNaskhArabic-Regular.ttf");
        GlyphLine glyphLine = new GlyphLine(Arrays.asList(fontProgram.getGlyph('1'), fontProgram.getGlyphByCode(75)));
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();
        GposLookupType5 lookup = (GposLookupType5) gposTableReader.getLookupTable(3);

        glyphLine.idx = 1;
        lookup.transformOne(glyphLine);

        Assert.assertEquals(2, glyphLine.size());
        Assert.assertEquals(1490, glyphLine.get(0).getCode());
        Assert.assertEquals(75, glyphLine.get(1).getCode());
        Assert.assertEquals(0, glyphLine.get(1).getAnchorDelta());
        Assert.assertEquals(0, glyphLine.get(1).getXPlacement());
    }

}
