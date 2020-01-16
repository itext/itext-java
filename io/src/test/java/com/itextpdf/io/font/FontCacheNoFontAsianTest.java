/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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

import com.itextpdf.io.IOException;
import com.itextpdf.io.font.cmap.CMapCidUni;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class FontCacheNoFontAsianTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Before
    public void before() {
        FontCache.clearSavedFonts();
    }

    @Test
    public void clearFontCacheTest() {
        String fontName = "FreeSans.ttf";
        Assert.assertNull(FontCache.getFont(fontName));

        FontProgram fontProgram = new FontProgramMock();
        FontCache.saveFont(fontProgram, fontName);
        Assert.assertEquals(fontProgram, FontCache.getFont(fontName));

        FontCache.clearSavedFonts();
        Assert.assertNull(FontCache.getFont(fontName));
    }

    @Test
    public void fontStringTtcCacheKeyTest() {
        String fontName = "Font.ttc";

        FontCacheKey ttc0 = FontCacheKey.create(fontName, 0);
        FontCacheKey ttc1 = FontCacheKey.create(fontName, 1);

        Assert.assertNull(FontCache.getFont(ttc0));
        Assert.assertNull(FontCache.getFont(ttc1));

        FontProgram fontProgram = new FontProgramMock();
        FontCache.saveFont(fontProgram, ttc1);

        Assert.assertNull(FontCache.getFont(ttc0));
        Assert.assertEquals(fontProgram, FontCache.getFont(ttc1));
    }

    @Test
    public void fontBytesTtcCacheKeyTest() {
        byte[] fontBytes = "SupposedTtcFontData".getBytes(StandardCharsets.UTF_8);
        byte[] otherFontBytes = "DifferentTtcFontBytes".getBytes(StandardCharsets.UTF_8);
        byte[] normalFontBytes = "NormalFontBytes".getBytes(StandardCharsets.UTF_8);

        FontCacheKey ttc0 = FontCacheKey.create(fontBytes, 1);
        FontCacheKey otherTtc0 = FontCacheKey.create(otherFontBytes, 1);
        FontCacheKey normal = FontCacheKey.create(normalFontBytes);

        Assert.assertNull(FontCache.getFont(ttc0));
        Assert.assertNull(FontCache.getFont(otherTtc0));
        Assert.assertNull(FontCache.getFont(normal));

        FontProgram otherTtc0MockFontProgram = new FontProgramMock();
        FontProgram normalMockFontProgram = new FontProgramMock();
        FontCache.saveFont(otherTtc0MockFontProgram, otherTtc0);
        FontCache.saveFont(normalMockFontProgram, normal);

        Assert.assertNull(FontCache.getFont(ttc0));
        Assert.assertEquals(otherTtc0MockFontProgram, FontCache.getFont(otherTtc0));
        Assert.assertEquals(normalMockFontProgram, FontCache.getFont(normal));
    }

    @Test
    public void getCompatibleCidFontNoFontAsian() {
        // Without font-asian module in the class path
        // any value passed into a method is expected to return null.
        Assert.assertNull(FontCache.getCompatibleCidFont("78-RKSJ-V"));
    }

    @Test
    public void isPredefinedCidFontNoFontAsian() {
        // Without font-asian module in the class path
        // any value passed into a method is expected to return false.
        Assert.assertFalse(FontCache.isPredefinedCidFont("78-RKSJ-V"));
    }

    @Test
    public void getCompatibleCmapsNoFontAsian() {
        // Without font-asian module in the class path
        // any value passed into a method is expected to return null.
        Assert.assertNull(FontCache.getCompatibleCmaps("HeiseiKakuGo-W5"));
    }

    @Test
    public void getRegistryNamesNoFontAsian() {
        // Without font-asian module in the class path
        // registry names collection is expected to be empty.
        Assert.assertTrue(FontCache.getRegistryNames().isEmpty());
    }

    @Test
    public void getCid2UniCMapNoFontAsian() {
        junitExpectedException.expect(IOException.class);

        // Without font-asian module in the class path
        // no CMap can be found.
        FontCache.getCid2UniCmap("UniJIS-UTF16-H");
    }

    @Test
    public void getUni2CidCMapNoFontAsian() {
        junitExpectedException.expect(IOException.class);

        // Without font-asian module in the class path
        // no CMap can be found.
        FontCache.getUni2CidCmap("UniJIS-UTF16-H");
    }

    @Test
    public void getByte2CidCMapNoFontAsian() {
        junitExpectedException.expect(IOException.class);

        // Without font-asian module in the class path
        // no CMap can be found.
        FontCache.getByte2CidCmap("78ms-RKSJ-H");
    }

    @Test
    public void getCid2ByteCMapNoFontAsian() {
        junitExpectedException.expect(IOException.class);

        // Without font-asian module in the class path
        // no CMap can be found.
        FontCache.getCid2Byte("78ms-RKSJ-H");
    }

    private static class FontProgramMock extends FontProgram {

        @Override
        public int getPdfFontFlags() {
            return 0;
        }

        @Override
        public int getKerning(Glyph first, Glyph second) {
            return 0;
        }
    }
}
