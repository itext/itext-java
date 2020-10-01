package com.itextpdf.io.font.otf;

import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class OpenTypeGdefTableReaderTest extends ExtendedITextTest {
    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/OpenTypeGdefTableReaderTest/";

    @Test
    public void testLookupFlagWithMarkAttachmentTypeAndMarkGlyphWithoutMarkAttachmentClass() throws IOException {
        String fontName = "Padauk-Regular.ttf";
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + fontName);
        OpenTypeGdefTableReader gdef = fontProgram.getGdefTable();
        int glyphCode = 207;
        Assert.assertEquals(OtfClass.GLYPH_MARK, gdef.getGlyphClassTable().getOtfClass(glyphCode));
        Assert.assertTrue(gdef.isSkip(glyphCode, (1 << 8) | OpenTypeGdefTableReader.FLAG_IGNORE_BASE));
    }

    @Test
    public void testLookupFlagWithMarkAttachmentTypeAndMarkGlyphWithSameMarkAttachmentClass() throws IOException {
        String fontName = "Padauk-Regular.ttf";
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + fontName);
        OpenTypeGdefTableReader gdef = fontProgram.getGdefTable();
        int glyphCode = 151;
        Assert.assertEquals(OtfClass.GLYPH_MARK, gdef.getGlyphClassTable().getOtfClass(glyphCode));
        Assert.assertFalse(gdef.isSkip(glyphCode, (1 << 8) | OpenTypeGdefTableReader.FLAG_IGNORE_BASE));
    }

    @Test
    public void testLookupFlagWithMarkAttachmentTypeAndBaseGlyph() throws IOException {
        String fontName = "Padauk-Regular.ttf";
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + fontName);
        OpenTypeGdefTableReader gdef = fontProgram.getGdefTable();
        int glyphCode = 165;
        Assert.assertEquals(OtfClass.GLYPH_BASE, gdef.getGlyphClassTable().getOtfClass(glyphCode));
        Assert.assertFalse(gdef.isSkip(glyphCode, (1 << 8)));
    }

}
