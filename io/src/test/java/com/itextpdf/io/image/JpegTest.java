package com.itextpdf.io.image;

import java.io.IOException;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class JpegTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/io/image/";

    @Test
    public void openJpeg1() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001.jpg");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openJpeg2() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001_gray.jpg");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openJpeg3() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001_monochrome.jpg");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openJpeg4() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001_negate.jpg");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openJpeg5() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001_year1900.jpg");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openJpeg6() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "WP_20140410_001_year1980.jpg");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }
}
