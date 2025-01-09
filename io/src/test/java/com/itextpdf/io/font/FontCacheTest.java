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

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.test.ExtendedITextTest;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class FontCacheTest extends ExtendedITextTest {

    @BeforeEach
    public void before() {
        FontCache.clearSavedFonts();
    }

    @Test
    public void clearFontCacheTest() {
        String fontName = "FreeSans.ttf";
        Assertions.assertNull(FontCache.getFont(fontName));

        FontProgram fontProgram = new FontProgramMock();
        FontCache.saveFont(fontProgram, fontName);
        Assertions.assertEquals(fontProgram, FontCache.getFont(fontName));

        FontCache.clearSavedFonts();
        Assertions.assertNull(FontCache.getFont(fontName));
    }

    @Test
    public void fontStringTtcCacheKeyTest() {
        String fontName = "Font.ttc";

        FontCacheKey ttc0 = FontCacheKey.create(fontName, 0);
        FontCacheKey ttc1 = FontCacheKey.create(fontName, 1);

        Assertions.assertNull(FontCache.getFont(ttc0));
        Assertions.assertNull(FontCache.getFont(ttc1));

        FontProgram fontProgram = new FontProgramMock();
        FontCache.saveFont(fontProgram, ttc1);

        Assertions.assertNull(FontCache.getFont(ttc0));
        Assertions.assertEquals(fontProgram, FontCache.getFont(ttc1));
    }

    @Test
    public void fontBytesTtcCacheKeyTest() {
        byte[] fontBytes = "SupposedTtcFontData".getBytes(StandardCharsets.UTF_8);
        byte[] otherFontBytes = "DifferentTtcFontBytes".getBytes(StandardCharsets.UTF_8);
        byte[] normalFontBytes = "NormalFontBytes".getBytes(StandardCharsets.UTF_8);

        FontCacheKey ttc0 = FontCacheKey.create(fontBytes, 1);
        FontCacheKey otherTtc0 = FontCacheKey.create(otherFontBytes, 1);
        FontCacheKey normal = FontCacheKey.create(normalFontBytes);

        Assertions.assertNull(FontCache.getFont(ttc0));
        Assertions.assertNull(FontCache.getFont(otherTtc0));
        Assertions.assertNull(FontCache.getFont(normal));

        FontProgram otherTtc0MockFontProgram = new FontProgramMock();
        FontProgram normalMockFontProgram = new FontProgramMock();
        FontCache.saveFont(otherTtc0MockFontProgram, otherTtc0);
        FontCache.saveFont(normalMockFontProgram, normal);

        Assertions.assertNull(FontCache.getFont(ttc0));
        Assertions.assertEquals(otherTtc0MockFontProgram, FontCache.getFont(otherTtc0));
        Assertions.assertEquals(normalMockFontProgram, FontCache.getFont(normal));
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
