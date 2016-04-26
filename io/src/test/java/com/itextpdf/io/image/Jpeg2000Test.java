package com.itextpdf.io.image;

import com.itextpdf.io.IOException;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class Jpeg2000Test {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/io/image/";

    @Test
    public void openJpeg2000_1() throws java.io.IOException {
        try {
            ImageData img = ImageFactory.getImage(sourceFolder + "WP_20140410_001.JP2");
            Jpeg2000ImageHelper.processImage(img);
        } catch (IOException e) {
            Assert.assertEquals(IOException.UnsupportedBoxSizeEqEq0, e.getMessage());
        }
    }

    @Test
    public void openJpeg2000_2() throws java.io.IOException {
        ImageData img = ImageFactory.getImage(sourceFolder + "WP_20140410_001.JPC");
        Jpeg2000ImageHelper.processImage(img);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }
}
