package com.itextpdf.io.image;

import com.itextpdf.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class Jpeg2000Test {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/io/image/";

    @Test
    public void openJpeg2000_1() throws java.io.IOException {
        try {
            Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001.JP2");
            Jpeg2000ImageHelper.processImage(img, null);
        } catch (IOException e) {
            Assert.assertEquals(IOException.UnsupportedBoxSizeEqEq0, e.getMessage());
        }
    }

    @Test
    public void openJpeg2000_2() throws java.io.IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001.JPC");
        Jpeg2000ImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }
}
