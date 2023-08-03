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
package com.itextpdf.io.font;

import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import java.io.IOException;


@Category(UnitTest.class)
public class FontProgramTest extends ExtendedITextTest {
    private static final String notExistingFont = "some-font.ttf";

    @Test
    public void exceptionMessageTest() throws IOException {
        Exception e = Assert.assertThrows(java.io.IOException.class,
                () -> FontProgramFactory.createFont(notExistingFont)
        );
        Assert.assertEquals(MessageFormatUtil.format(IoExceptionMessageConstant.NOT_FOUND_AS_FILE_OR_RESOURCE, notExistingFont), e.getMessage());
    }

    @Test
    public void boldTest() throws IOException {
        FontProgram fp = FontProgramFactory.createFont(StandardFonts.HELVETICA);
        fp.setBold(true);
        Assert.assertTrue("Bold expected", (fp.getPdfFontFlags() & (1 << 18)) != 0);
        fp.setBold(false);
        Assert.assertTrue("Not Bold expected", (fp.getPdfFontFlags() & (1 << 18)) == 0);
    }

    @Test
    public void registerDirectoryOpenTypeTest() {
        FontProgramFactory.clearRegisteredFonts();
        FontProgramFactory.clearRegisteredFontFamilies();
        FontCache.clearSavedFonts();
        FontProgramFactory.registerFontDirectory("./src/test/resources/com/itextpdf/io/font/otf/");

        Assert.assertEquals(43, FontProgramFactory.getRegisteredFonts().size());
        Assert.assertNull(FontCache.getFont("./src/test/resources/com/itextpdf/io/font/otf/FreeSansBold.ttf"));
        Assert.assertTrue(FontProgramFactory.getRegisteredFonts().contains("free sans lihavoitu"));
    }

    @Test
    public void registerDirectoryType1Test() throws IOException {
        FontProgramFactory.registerFontDirectory("./src/test/resources/com/itextpdf/io/font/type1/");
        FontProgram computerModern = FontProgramFactory.createRegisteredFont("computer modern");
        FontProgram cmr10 = FontProgramFactory.createRegisteredFont("cmr10");
        Assert.assertNotNull(computerModern);
        Assert.assertNotNull(cmr10);
    }
}
