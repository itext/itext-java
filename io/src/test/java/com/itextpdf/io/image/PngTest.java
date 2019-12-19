package com.itextpdf.io.image;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.verapdf.metadata.fixer.entity.PDFDocument;

@Category(UnitTest.class)
public class PngTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/io/image/PngTest/";

    @Test
    public void grayscale8BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "grayscale8Bpc.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(1, img.getColorSpace());
    }

    @Test
    // iText explicitly processes 16bit images as 8bit
    public void grayscale16BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "grayscale16Bpc.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(1, img.getColorSpace());
    }

    @Test
    public void graya8BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "graya8Bpc.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(1, img.getColorSpace());
    }

    @Test
    public void graya8BpcAddColorToAlphaImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "graya8BpcAddColorToAlpha.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(1, img.getColorSpace());
    }

    @Test
    public void rgb8BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgb8Bpc.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(3, img.getColorSpace());
    }

    @Test
    // iText explicitly processes 16bit images as 8bit
    public void rgb16BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgb16Bpc.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(3, img.getColorSpace());
    }

    @Test
    public void rgbWithoutSaveColorProfileImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgbWithoutSaveColorProfile.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(3, img.getColorSpace());
    }

    @Test
    public void rgba8BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgba8Bpc.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(3, img.getColorSpace());
    }

    @Test
    // iText explicitly processes 16bit images as 8bit
    public void rgba16BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgba16Bpc.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(3, img.getColorSpace());
    }

    @Test
    public void rgbaAddColorToAlphaImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgbaAddColorToAlpha.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(3, img.getColorSpace());
    }

    @Test
    public void indexed2BpcImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "indexed2BpcImage.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(346, img.getWidth(), 0);
        Assert.assertEquals(49, img.getHeight(), 0);
        Assert.assertEquals(2, img.getBpc());
        //Indexed colorspace contains one component indeed
        Assert.assertEquals(1, img.getColorSpace());
    }

    @Test
    public void indexed1BpcImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "indexed1BpcImage.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(1, img.getBpc());
        //Indexed colorspace contains one component indeed
        Assert.assertEquals(1, img.getColorSpace());
    }

    @Test
    public void indexed2BpcWithAlphaChannelTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "indexed2BpcWithAlphaChannel.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(346, img.getWidth(), 0);
        Assert.assertEquals(49, img.getHeight(), 0);
        Assert.assertEquals(2, img.getBpc());
        //Indexed colorspace contains one component indeed
        Assert.assertEquals(1, img.getColorSpace());
    }

    @Test
    public void indexedAddColorToAlphaImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "indexedAddColorToAlpha.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(346, img.getWidth(), 0);
        Assert.assertEquals(49, img.getHeight(), 0);
        Assert.assertEquals(2, img.getBpc());
        //Indexed colorspace contains one component indeed
        Assert.assertEquals(1, img.getColorSpace());
    }

    @Test
    public void size50Px30DpiImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "size50Px30Dpi.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(50, img.getWidth(), 0);
        Assert.assertEquals(50, img.getHeight(), 0);
        Assert.assertEquals(30, img.getDpiX());
        Assert.assertEquals(30, img.getDpiY());
    }

    @Test
    public void size50Px300DpiImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "size50Px300Dpi.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(50, img.getWidth(), 0);
        Assert.assertEquals(50, img.getHeight(), 0);
        Assert.assertEquals(300, img.getDpiX());
        Assert.assertEquals(300, img.getDpiY());
    }

    @Test
    public void size150Px72DpiImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "size150Px72Dpi.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(150, img.getWidth(), 0);
        Assert.assertEquals(150, img.getHeight(), 0);
        Assert.assertEquals(72, img.getDpiX());
        Assert.assertEquals(72, img.getDpiY());
    }

    @Test
    public void size300Px72DpiImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "size300Px72Dpi.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(300, img.getWidth(), 0);
        Assert.assertEquals(300, img.getHeight(), 0);
        Assert.assertEquals(72, img.getDpiX());
        Assert.assertEquals(72, img.getDpiY());
    }

    @Test
    public void size300Px300DpiImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "size300Px300Dpi.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(300, img.getWidth(), 0);
        Assert.assertEquals(300, img.getHeight(), 0);
        Assert.assertEquals(300, img.getDpiX());
        Assert.assertEquals(300, img.getDpiY());
    }
}