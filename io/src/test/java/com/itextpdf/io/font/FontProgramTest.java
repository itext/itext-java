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
package com.itextpdf.io.font;

import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.io.IOException;


@Tag("UnitTest")
public class FontProgramTest extends ExtendedITextTest {
    private static final String notExistingFont = "some-font.ttf";

    @Test
    public void exceptionMessageTest() throws IOException {
        Exception e = Assertions.assertThrows(java.io.IOException.class,
                () -> FontProgramFactory.createFont(notExistingFont)
        );
        Assertions.assertEquals(MessageFormatUtil.format(IoExceptionMessageConstant.NOT_FOUND_AS_FILE_OR_RESOURCE, notExistingFont), e.getMessage());
    }

    @Test
    public void boldTest() throws IOException {
        FontProgram fp = FontProgramFactory.createFont(StandardFonts.HELVETICA);
        fp.setBold(true);
        Assertions.assertTrue((fp.getPdfFontFlags() & (1 << 18)) != 0, "Bold expected");
        fp.setBold(false);
        Assertions.assertTrue((fp.getPdfFontFlags() & (1 << 18)) == 0, "Not Bold expected");
    }

    @Test
    public void registerDirectoryOpenTypeTest() {
        FontProgramFactory.clearRegisteredFonts();
        FontProgramFactory.clearRegisteredFontFamilies();
        FontCache.clearSavedFonts();
        FontProgramFactory.registerFontDirectory("./src/test/resources/com/itextpdf/io/font/otf/");

        Assertions.assertEquals(43, FontProgramFactory.getRegisteredFonts().size());
        Assertions.assertNull(FontCache.getFont("./src/test/resources/com/itextpdf/io/font/otf/FreeSansBold.ttf"));
        Assertions.assertTrue(FontProgramFactory.getRegisteredFonts().contains("free sans lihavoitu"));
    }

    @Test
    public void registerDirectoryType1Test() throws IOException {
        FontProgramFactory.registerFontDirectory("./src/test/resources/com/itextpdf/io/font/type1/");
        FontProgram computerModern = FontProgramFactory.createRegisteredFont("computer modern");
        FontProgram cmr10 = FontProgramFactory.createRegisteredFont("cmr10");
        Assertions.assertNotNull(computerModern);
        Assertions.assertNotNull(cmr10);
    }

    @Test
    public void cidFontWithCmapTest() throws IOException {
        char space = ' ';

        FontProgram fp = FontProgramFactory.createFont("KozMinPro-Regular", "UniJIS-UCS2-HW-H", true);
        Glyph glyph = fp.getGlyph(space);

        Assertions.assertArrayEquals(new char[] {space}, glyph.getUnicodeChars());
        Assertions.assertEquals(32, glyph.getUnicode());
        Assertions.assertEquals(231, glyph.getCode());
        Assertions.assertEquals(500, glyph.getWidth());

        fp = FontProgramFactory.createFont("KozMinPro-Regular", null, true);
        glyph = fp.getGlyph(space);

        Assertions.assertArrayEquals(new char[] {space}, glyph.getUnicodeChars());
        Assertions.assertEquals(32, glyph.getUnicode());
        Assertions.assertEquals(1, glyph.getCode());
        Assertions.assertEquals(278, glyph.getWidth());
    }
}
