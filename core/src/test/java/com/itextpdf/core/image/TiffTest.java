package com.itextpdf.core.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.image.ImageFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TiffTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/image/";

    @Test
    public void openTiff1() throws IOException, PdfException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001.tif");
        TiffImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff2() throws IOException, PdfException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_gray.tiff");
        TiffImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff3() throws IOException, PdfException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_monochrome.tiff");
        TiffImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff4() throws IOException, PdfException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_negate.tiff");
        TiffImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff5() throws IOException, PdfException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_year1900.tiff");
        TiffImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff6() throws IOException, PdfException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001_year1980.tiff");
        TiffImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }


}
