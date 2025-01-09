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
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class GposLookupType5Test extends ExtendedITextTest {

    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/GposLookupType5Test/";

    @Test
    public void verifyMarkToBaseAttachment() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + "KhmerOS.ttf");
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();
        GposLookupType5 lookup = (GposLookupType5) gposTableReader.getLookupTable(0);
        List<Glyph> glyphs = Arrays.asList(new Glyph(fontProgram.getGlyphByCode(445)), new Glyph(fontProgram.getGlyphByCode(394)));
        GlyphLine gl = new GlyphLine(glyphs);
        gl.setIdx(1);
        lookup.transformOne(gl);

        Assertions.assertEquals(2, gl.size());
        Assertions.assertEquals(445, gl.get(0).getCode());
        Assertions.assertEquals(394, gl.get(1).getCode());
        Assertions.assertEquals(-1, gl.get(1).getAnchorDelta());
        Assertions.assertEquals(756, gl.get(1).getXPlacement());
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

        glyphLine.setIdx(1);
        lookup.transformOne(glyphLine);

        Assertions.assertEquals(2, glyphLine.size());
        Assertions.assertEquals(513, glyphLine.get(0).getCode());
        Assertions.assertEquals(75, glyphLine.get(1).getCode());
        Assertions.assertEquals(-1, glyphLine.get(1).getAnchorDelta());
        Assertions.assertEquals(-22, glyphLine.get(1).getXPlacement());
    }

    @Test
    public void testThatNoTransformationsAppliedForNonRelevantGlyphs() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + "NotoNaskhArabic-Regular.ttf");
        GlyphLine glyphLine = new GlyphLine(Arrays.asList(fontProgram.getGlyph('1'), fontProgram.getGlyphByCode(75)));
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();
        GposLookupType5 lookup = (GposLookupType5) gposTableReader.getLookupTable(3);

        glyphLine.setIdx(1);
        lookup.transformOne(glyphLine);

        Assertions.assertEquals(2, glyphLine.size());
        Assertions.assertEquals(1490, glyphLine.get(0).getCode());
        Assertions.assertEquals(75, glyphLine.get(1).getCode());
        Assertions.assertEquals(0, glyphLine.get(1).getAnchorDelta());
        Assertions.assertEquals(0, glyphLine.get(1).getXPlacement());
    }

    @Test
    public void idxBiggerThanLineEndTest() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + "NotoNaskhArabic-Regular.ttf");
        GlyphLine glyphLine = new GlyphLine(Collections.singletonList(fontProgram.getGlyph(203)));
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();
        GposLookupType5 lookup = (GposLookupType5) gposTableReader.getLookupTable(3);

        glyphLine.setIdx(10);

        Assertions.assertFalse(lookup.transformOne(glyphLine));
    }
}
