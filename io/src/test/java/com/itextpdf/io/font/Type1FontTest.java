package com.itextpdf.io.font;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class Type1FontTest extends ExtendedITextTest {

    @Test
    public void fillUsingEncodingTest() throws IOException {
        FontEncoding fontEncoding = FontEncoding.createFontEncoding("WinAnsiEncoding");
        Type1Font type1StdFont = (Type1Font) FontProgramFactory.createFont("Helvetica", true);
        Assert.assertEquals(149, type1StdFont.codeToGlyph.size());
        type1StdFont.initializeGlyphs(fontEncoding);
        Assert.assertEquals(217, type1StdFont.codeToGlyph.size());
        Assert.assertEquals(0x2013, type1StdFont.codeToGlyph.get(150).getUnicode());
        Assert.assertArrayEquals(new char[]{(char)0x2013}, type1StdFont.codeToGlyph.get(150).getChars());
    }
}
