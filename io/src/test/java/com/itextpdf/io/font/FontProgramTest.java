package com.itextpdf.io.font;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(UnitTest.class)
public class FontProgramTest {

    @Test
    public void exceptionMessageTest() throws IOException {
        try {
            FontProgramFactory.createFont("some-font.ttf");
        } catch (com.itextpdf.io.IOException ex) {
            Assert.assertEquals("font.file some-font.ttf not.found", ex.getMessage());
        }
    }

    @Test
    public void boldTest() throws IOException {
        FontProgram fp = FontProgramFactory.createFont(FontConstants.HELVETICA);
        fp.setBold(true);
        Assert.assertTrue("Bold expected", (fp.getPdfFontFlags() & (1 << 18)) != 0);
        fp.setBold(false);
        Assert.assertTrue("Not Bold expected", (fp.getPdfFontFlags() & (1 << 18)) == 0);
    }
}
