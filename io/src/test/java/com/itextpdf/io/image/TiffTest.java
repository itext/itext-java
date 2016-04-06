package com.itextpdf.io.image;

import java.io.IOException;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TiffTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/io/image/";

    @Test
    public void openTiff1() throws IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001.tif");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff2() throws IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_gray.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff3() throws IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_monochrome.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff4() throws IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_negate.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff5() throws IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_year1900.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff6() throws IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_year1980.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }
}
