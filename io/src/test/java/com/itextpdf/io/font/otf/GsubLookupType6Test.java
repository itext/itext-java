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
public class GsubLookupType6Test extends ExtendedITextTest {

    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/GsubLookupType6Test/";

    @Test
    public void testSubstitutionApplied() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "Padauk-Regular.ttf");
        GlyphSubstitutionTableReader gsubTableReader = fontProgram.getGsubTable();

        List<Glyph> glyphs = Arrays.asList(fontProgram.getGlyphByCode(233),
                fontProgram.getGlyphByCode(167), fontProgram.getGlyphByCode(207),
                fontProgram.getGlyphByCode(149), fontProgram.getGlyphByCode(207),
                fontProgram.getGlyphByCode(186), fontProgram.getGlyphByCode(229),
                fontProgram.getGlyphByCode(248));

        GlyphLine gl = new GlyphLine(glyphs);

        GsubLookupType6 lookup = (GsubLookupType6) gsubTableReader.getLookupTable(57);

        Assert.assertEquals(233, gl.get(0).getCode());
        Assert.assertTrue(lookup.transformLine(gl));
        Assert.assertEquals(234, gl.get(0).getCode());
    }

    @Test
    public void testSubstitutionNotApplied() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "Padauk-Regular.ttf");
        GlyphSubstitutionTableReader gsubTableReader = fontProgram.getGsubTable();

        List<Glyph> glyphs = Arrays.asList(fontProgram.getGlyphByCode(233),
                fontProgram.getGlyphByCode(167), fontProgram.getGlyphByCode(207),
                fontProgram.getGlyphByCode(149), fontProgram.getGlyphByCode(207),
                fontProgram.getGlyphByCode(186), fontProgram.getGlyphByCode(229),
                fontProgram.getGlyphByCode(248));

        GlyphLine gl = new GlyphLine(glyphs);

        GsubLookupType6 lookup = (GsubLookupType6) gsubTableReader.getLookupTable(54);

        Assert.assertEquals(233, gl.get(0).getCode());
        Assert.assertFalse(lookup.transformLine(gl));
        Assert.assertEquals(233, gl.get(0).getCode());
    }


}
