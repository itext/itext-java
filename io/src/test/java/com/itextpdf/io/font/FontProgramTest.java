package com.itextpdf.io.font;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.text.MessageFormat;

@Category(UnitTest.class)
public class FontProgramTest {

    @Test
    public void exceptionMessageTest() throws IOException {
        String font = "some-font.ttf";
        try {
            FontProgramFactory.createFont(font);
        } catch (com.itextpdf.io.IOException ex) {
            Assert.assertEquals(MessageFormat.format(com.itextpdf.io.IOException.FontFile1NotFound, font), ex.getMessage());
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
