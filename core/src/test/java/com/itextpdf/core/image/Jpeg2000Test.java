package com.itextpdf.core.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.basics.image.Jpeg2000ImageHelper;
import com.itextpdf.core.testutils.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(UnitTest.class)
public class Jpeg2000Test {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/image/";

    @Test
    public void openJpeg2000_1() throws IOException {
        try {
            Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001.JP2");
            Jpeg2000ImageHelper.processImage(img, null);
        } catch (PdfException e) {
            Assert.assertEquals(PdfException.UnsupportedBoxSizeEqEq0, e.getMessage());
        }
    }

    @Test
    public void openJpeg2000_2() throws IOException {
        Image img = ImageFactory.getImage(sourceFolder + "WP_20140410_001.JPC");
        Jpeg2000ImageHelper.processImage(img, null);
        Assert.assertEquals(2592, img.getWidth(), 0);
        Assert.assertEquals(1456, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
    }


}
