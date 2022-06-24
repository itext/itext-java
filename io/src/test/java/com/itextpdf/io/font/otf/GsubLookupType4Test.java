package com.itextpdf.io.font.otf;

import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class GsubLookupType4Test extends ExtendedITextTest {

    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/GsubLookupType4Test/";

    @Test
    public void testNoIndexOutOfBound() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "DejaVuSansMono.ttf");
        GlyphSubstitutionTableReader gsubTableReader = fontProgram.getGsubTable();

        List<Glyph> glyphs = Arrays.asList(new Glyph(1, 1, 1),
                                           new Glyph(1, 1, 1),
                                           new Glyph(1, 1, 1),
                                           new Glyph(1, 1, 1),
                                           new Glyph(1, 1, 1),
                                           new Glyph(1, 1, 1));

        GlyphLine gl = new GlyphLine(glyphs);
        gl.idx = gl.end;

        GsubLookupType4 lookup = (GsubLookupType4) gsubTableReader.getLookupTable(6);

        //Assert that no exception is thrown if gl.idx = gl.end
        Assert.assertFalse(lookup.transformOne(gl));
    }
}
