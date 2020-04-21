package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TextPreprocessingUtilTest extends ExtendedITextTest {
    private static PdfFont pdfFont;

    @BeforeClass
    public static void initializeFont() throws IOException {
        pdfFont = PdfFontFactory.createFont();
    }

    @Test
    public void enSpaceTest() {
        specialWhitespaceGlyphTest('\u2002');
    }

    @Test
    public void emSpaceTest() {
        specialWhitespaceGlyphTest('\u2003');
    }

    @Test
    public void thinSpaceTest() {
        specialWhitespaceGlyphTest('\u2009');
    }

    @Test
    public void horizontalTabulationTest() {
        specialWhitespaceGlyphTest('\t');
    }

    @Test
    public void regularSymbolTest() {
        GlyphLine glyphLine = new GlyphLine();
        Glyph regularGlyph = pdfFont.getGlyph('a');
        glyphLine.add(0, regularGlyph);

        TextPreprocessingUtil.replaceSpecialWhitespaceGlyphs(glyphLine, pdfFont);

        Glyph glyph = glyphLine.get(0);
        Assert.assertEquals(regularGlyph, glyph);
    }

    private void specialWhitespaceGlyphTest(int unicode) {
        GlyphLine glyphLine = new GlyphLine();
        // Create a new glyph, because it is a special glyph, and it is not contained in the regular font
        glyphLine.add(0, new Glyph(0, unicode));

        TextPreprocessingUtil.replaceSpecialWhitespaceGlyphs(glyphLine, pdfFont);

        Glyph glyph = glyphLine.get(0);
        Glyph space = pdfFont.getGlyph('\u0020');
        Assert.assertTrue(space.getCode() == glyph.getCode() && space.getWidth() == glyph.getWidth());
    }
}
