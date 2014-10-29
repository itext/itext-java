package com.itextpdf.basics.image;

import com.itextpdf.basics.PdfException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by chin on 10/29/2014.
 */
public class TiffTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/basics/image/";

    @Test
    public void openTiff1() throws IOException, PdfException {
        Image img = Image.getInstance(sourceFolder + "WP_20140410_001.tif");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff2() throws IOException, PdfException {
        Image img = Image.getInstance(sourceFolder + "WP_20140410_001_gray.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff3() throws IOException, PdfException {
        Image img = Image.getInstance(sourceFolder + "WP_20140410_001_monochrome.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff4() throws IOException, PdfException {
        Image img = Image.getInstance(sourceFolder + "WP_20140410_001_negate.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff5() throws IOException, PdfException {
        Image img = Image.getInstance(sourceFolder + "WP_20140410_001_year1900.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openTiff6() throws IOException, PdfException {
        Image img = Image.getInstance(sourceFolder + "WP_20140410_001_year1980.tiff");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }


}
