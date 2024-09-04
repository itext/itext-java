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
public class GsubLookupType4Test extends ExtendedITextTest {

    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/GsubLookupType4Test/";

    @Test
    public void testNoIndexOutOfBound() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "DejaVuSansMono.ttf");
        GlyphSubstitutionTableReader gsubTableReader = fontProgram.getGsubTable();

        List<Glyph> glyphs = Arrays.asList(new Glyph(1, 1, 1),
                                           new Glyph(1, 1, 1),
                                           new Glyph(1, 1, 1),
                                           new Glyph(1, 1, 1),
                                           new Glyph(1, 1, 1),
                                           new Glyph(1, 1, 1));

        GlyphLine gl = new GlyphLine(glyphs);
        gl.setIdx(gl.getEnd());

        GsubLookupType4 lookup = (GsubLookupType4) gsubTableReader.getLookupTable(6);

        //Assert that no exception is thrown if gl.idx = gl.end
        Assertions.assertFalse(lookup.transformOne(gl));
    }

    @Test
    public void noTransformationTest() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "DejaVuSansMono.ttf");
        GlyphSubstitutionTableReader gsubTableReader = fontProgram.getGsubTable();

        List<Glyph> glyphs = Arrays.asList(new Glyph(1, 1, 1),
                new Glyph(1, 1, 1),
                new Glyph(1, 1, 1),
                new Glyph(1, 1, 1),
                new Glyph(1, 1, 1),
                new Glyph(1, 1, 1));

        GlyphLine gl = new GlyphLine(glyphs);
        gl.setIdx(3);

        GsubLookupType4 lookup = (GsubLookupType4) gsubTableReader.getLookupTable(6);
        Assertions.assertFalse(lookup.transformOne(gl));
    }
}
