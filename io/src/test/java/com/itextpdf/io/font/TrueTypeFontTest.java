package com.itextpdf.io.font;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TrueTypeFontTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/io/font/TrueTypeFontTest/";

    @Test
    public void notoSansJpCmapTest() throws IOException, InterruptedException {
        // 信
        char jpChar = '\u4FE1';

        FontProgram fontProgram = FontProgramFactory.createFont(sourceFolder + "NotoSansJP-Regular.otf");
        Glyph glyph = fontProgram.getGlyph(jpChar);

        Assert.assertArrayEquals(new char[] {jpChar}, glyph.getUnicodeChars());
        Assert.assertEquals(20449, glyph.getUnicode());

        // TODO DEVSIX-5767 actual expected value is 0x27d3
        Assert.assertEquals(0x0a72, glyph.getCode());
    }
}
