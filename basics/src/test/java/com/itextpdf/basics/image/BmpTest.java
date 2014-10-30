package com.itextpdf.basics.image;

import com.itextpdf.basics.PdfException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class BmpTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/basics/image/";

    @Test
    public void openBmp1() throws IOException, PdfException {
        Image img = Image.getInstance(sourceFolder + "WP_20140410_001.bmp");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openBmp2() throws IOException, PdfException {
        Image img = Image.getInstance(sourceFolder + "WP_20140410_001_gray.bmp");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }

    @Test
    public void openBmp3() throws IOException, PdfException {
        Image img = Image.getInstance(sourceFolder + "WP_20140410_001_monochrome.bmp");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(1, img.getBpc());
    }



}
