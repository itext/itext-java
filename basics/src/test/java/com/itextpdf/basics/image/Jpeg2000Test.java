package com.itextpdf.basics.image;

import com.itextpdf.basics.PdfException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class Jpeg2000Test {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/basics/image/";

    @Test
    public void openJpeg2000_1() throws IOException, PdfException {
        try {
            Image.getInstance(sourceFolder + "WP_20140410_001.JP2");
        } catch (PdfException e) {
            Assert.assertEquals(PdfException.UnsupportedBoxSizeEqEq0, e.getMessage());
        }
    }

    @Test
    public void openJpeg2000_2() throws IOException, PdfException {
        Image img = Image.getInstance(sourceFolder + "WP_20140410_001.JPC");
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }


}
