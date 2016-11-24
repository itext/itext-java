package com.itextpdf.io.font;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(UnitTest.class)
public class TrueTypeFontTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/io/font/";

    @Test
    public void openMono() throws IOException {
        TrueTypeFont font = new TrueTypeFont(sourceFolder + "DejaVuSansMono.ttf");
        Assert.assertNotNull(font.getGlyph('A'));
    }

    @Test
    public void openSans() throws IOException {
        TrueTypeFont font = new TrueTypeFont(sourceFolder + "DejaVuSans.ttf");
        Assert.assertNotNull(font.getGlyph('A'));
    }

    @Test
    public void openSerif() throws IOException {
        TrueTypeFont font = new TrueTypeFont(sourceFolder + "DejaVuSerif.ttf");
        Assert.assertNotNull(font.getGlyph('A'));
    }


}
