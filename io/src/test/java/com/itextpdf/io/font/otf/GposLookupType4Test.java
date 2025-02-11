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
public class GposLookupType4Test extends ExtendedITextTest {

    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/GposLookupType4Test/";

    @Test
    public void verifyMarkToBaseAttachment() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + "Padauk-Regular.ttf");
        GlyphPositioningTableReader gposTableReader = fontProgram.getGposTable();
        GposLookupType4 lookup = (GposLookupType4) gposTableReader.getLookupTable(192);
        List<Glyph> glyphs = Arrays.asList(new Glyph(fontProgram.getGlyphByCode(163)), new Glyph(fontProgram.getGlyphByCode(207)),
                new Glyph(fontProgram.getGlyphByCode(213)));
        GlyphLine gl = new GlyphLine(glyphs);
        gl.setIdx(2);

        Assertions.assertEquals(0, gl.get(2).getXPlacement());
        Assertions.assertEquals(0, gl.get(2).getAnchorDelta());

        lookup.transformOne(gl);

        Assertions.assertEquals(364, gl.get(2).getXPlacement());
        Assertions.assertEquals(-2, gl.get(2).getAnchorDelta());
    }
}
