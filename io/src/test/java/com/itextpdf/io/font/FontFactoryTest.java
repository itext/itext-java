package com.itextpdf.io.font;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(UnitTest.class)
public class FontFactoryTest {

    @Test
    public void exceptionMessageTest() throws IOException {
        try {
            FontFactory.createFont("some-font.ttf");
        } catch (com.itextpdf.io.IOException ex) {
            Assert.assertEquals("font.file some-font.ttf not.found", ex.getMessage());
        }
    }
}
