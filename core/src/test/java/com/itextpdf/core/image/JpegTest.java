package com.itextpdf.core.image;

import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.image.ImageFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class JpegTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/image/";

    @Test
    public void openJpeg1() throws IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001.jpg");
        JpegImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openJpeg2() throws IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_gray.jpg");
        JpegImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openJpeg3() throws IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_monochrome.jpg");
        JpegImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openJpeg4() throws IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_negate.jpg");
        JpegImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openJpeg5() throws IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_year1900.jpg");
        JpegImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openJpeg6() throws IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_year1980.jpg");
        JpegImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }





}
