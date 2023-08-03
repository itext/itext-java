/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class OpenTypeGdefTableReaderTest extends ExtendedITextTest {
    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/OpenTypeGdefTableReaderTest/";

    @Test
    public void testLookupFlagWithMarkAttachmentTypeAndMarkGlyphWithoutMarkAttachmentClass() throws IOException {
        String fontName = "Padauk-Regular.ttf";
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + fontName);
        OpenTypeGdefTableReader gdef = fontProgram.getGdefTable();
        int glyphCode = 207;
        Assert.assertEquals(OtfClass.GLYPH_MARK, gdef.getGlyphClassTable().getOtfClass(glyphCode));
        Assert.assertTrue(gdef.isSkip(glyphCode, (1 << 8) | OpenTypeGdefTableReader.FLAG_IGNORE_BASE));
    }

    @Test
    public void testLookupFlagWithMarkAttachmentTypeAndMarkGlyphWithSameMarkAttachmentClass() throws IOException {
        String fontName = "Padauk-Regular.ttf";
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + fontName);
        OpenTypeGdefTableReader gdef = fontProgram.getGdefTable();
        int glyphCode = 151;
        Assert.assertEquals(OtfClass.GLYPH_MARK, gdef.getGlyphClassTable().getOtfClass(glyphCode));
        Assert.assertFalse(gdef.isSkip(glyphCode, (1 << 8) | OpenTypeGdefTableReader.FLAG_IGNORE_BASE));
    }

    @Test
    public void testLookupFlagWithMarkAttachmentTypeAndBaseGlyph() throws IOException {
        String fontName = "Padauk-Regular.ttf";
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + fontName);
        OpenTypeGdefTableReader gdef = fontProgram.getGdefTable();
        int glyphCode = 165;
        Assert.assertEquals(OtfClass.GLYPH_BASE, gdef.getGlyphClassTable().getOtfClass(glyphCode));
        Assert.assertFalse(gdef.isSkip(glyphCode, (1 << 8)));
    }

}
