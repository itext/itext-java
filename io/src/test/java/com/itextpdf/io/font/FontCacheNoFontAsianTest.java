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

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
// Android-Conversion-Skip-File (TODO DEVSIX-7376 investigate why FontCacheNoFontAsianTest is skipped on Android)
public class FontCacheNoFontAsianTest extends ExtendedITextTest {

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
        // Without font-asian module in the class path
        // no CMap can be found.
        Assert.assertThrows(IOException.class, () -> FontCache.getCid2UniCmap("UniJIS-UTF16-H"));
    }

    @Test
    public void getUni2CidCMapNoFontAsian() {
        // Without font-asian module in the class path
        // no CMap can be found.
        Assert.assertThrows(IOException.class, () -> FontCache.getUni2CidCmap("UniJIS-UTF16-H"));
    }

    @Test
    public void getByte2CidCMapNoFontAsian() {
        // Without font-asian module in the class path
        // no CMap can be found.
        Assert.assertThrows(IOException.class, () -> FontCache.getByte2CidCmap("78ms-RKSJ-H"));
    }

    @Test
    public void getCid2ByteCMapNoFontAsian() {
        // Without font-asian module in the class path
        // no CMap can be found.
        Assert.assertThrows(IOException.class, () -> FontCache.getCidToCodepointCmap("78ms-RKSJ-H"));
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
