package com.itextpdf.io.font;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class FontCacheTest extends ExtendedITextTest {

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
