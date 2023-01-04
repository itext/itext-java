/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.font;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;


@Category(UnitTest.class)
public class FontProgramTest extends ExtendedITextTest {
    private static final String notExistingFont = "some-font.ttf";

    @Test
    public void exceptionMessageTest() throws IOException {
        Exception e = Assert.assertThrows(java.io.IOException.class,
                () -> FontProgramFactory.createFont(notExistingFont)
        );
        Assert.assertEquals(MessageFormatUtil.format(com.itextpdf.io.exceptions.IOException._1NotFoundAsFileOrResource, notExistingFont), e.getMessage());
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
