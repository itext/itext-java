/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
        Assert.assertEquals(0, ((PngImageData)img).getColorType());
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
        Assert.assertEquals(0, ((PngImageData)img).getColorType());
    }

    @Test
    public void graya8BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "graya8Bpc.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(1, img.getColorSpace());
        Assert.assertEquals(4, ((PngImageData)img).getColorType());
        Assert.assertNotNull(img.getImageMask());
        Assert.assertEquals(1, img.getImageMask().getColorSpace());
        Assert.assertEquals(8, img.getImageMask().getBpc());
    }

    @Test
    public void graya8BpcDepthWithoutEmbeddedProfileImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "graya8BpcWithoutProfile.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(1, img.getColorSpace());
        Assert.assertEquals(4, ((PngImageData) img).getColorType());
        Assert.assertNotNull(img.getImageMask());
        Assert.assertEquals(1, img.getImageMask().getColorSpace());
        Assert.assertEquals(8, img.getImageMask().getBpc());
        Assert.assertNull(img.getProfile());
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
        Assert.assertEquals(2, ((PngImageData)img).getColorType());
    }

    @Test
    public void rgbWithoutSaveColorProfileImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgbWithoutSaveColorProfile.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(3, img.getColorSpace());
        Assert.assertEquals(2, ((PngImageData)img).getColorType());
        Assert.assertNull(img.getProfile());
    }

    @Test
    public void rgba8BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgba8Bpc.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(100, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(3, img.getColorSpace());
        Assert.assertEquals(6, ((PngImageData)img).getColorType());
        Assert.assertNotNull(img.getImageMask());
        Assert.assertEquals(1, img.getImageMask().getColorSpace());
        Assert.assertEquals(8, img.getImageMask().getBpc());
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
        Assert.assertEquals(6, ((PngImageData)img).getColorType());
        Assert.assertNotNull(img.getImageMask());
        Assert.assertEquals(1, img.getImageMask().getColorSpace());
        Assert.assertEquals(8, img.getImageMask().getBpc());
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
        Assert.assertEquals(3, ((PngImageData)img).getColorType());
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
        Assert.assertEquals(3, ((PngImageData)img).getColorType());
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
        Assert.assertEquals(3, ((PngImageData)img).getColorType());
    }

    @Test
    public void grayscaleSimpleTransparencyImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "grayscaleSimpleTransparencyImage.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(200, img.getWidth(), 0);
        Assert.assertEquals(200, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(1, img.getColorSpace());
        Assert.assertEquals(0, ((PngImageData)img).getColorType());
        Assert.assertNotNull(img.getImageAttributes().entrySet());
        Assert.assertEquals("[0 0]", img.getImageAttributes()
                .get(PngImageHelperConstants.MASK));

    }

    @Test
    public void rgbSimpleTransparencyImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgbSimpleTransparencyImage.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(600, img.getWidth(), 0);
        Assert.assertEquals(100, img.getHeight(), 0);
        Assert.assertEquals(8, img.getBpc());
        Assert.assertEquals(3, img.getColorSpace());
        Assert.assertEquals(2, ((PngImageData)img).getColorType());
        Assert.assertNotNull(img.getImageAttributes().entrySet());
        Assert.assertEquals("[255 255 0 0 0 0]", img.getImageAttributes()
                .get(PngImageHelperConstants.MASK));
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
        Assert.assertEquals(3, ((PngImageData)img).getColorType());
        Assert.assertNotNull(img.getImageAttributes().entrySet());
        Assert.assertEquals(0, ((int[])img.getImageAttributes()
                .get(PngImageHelperConstants.MASK))[0]);
        Assert.assertEquals(0, ((int[])img.getImageAttributes()
                .get(PngImageHelperConstants.MASK))[1]);
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

    @Test
    public void sRGBImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "sRGBImage.png");
        Assert.assertEquals(ImageType.PNG, img.getOriginalType());
        Assert.assertEquals(50, img.getWidth(), 0);
        Assert.assertEquals(50, img.getHeight(), 0);
        Assert.assertEquals(96, img.getDpiX());
        Assert.assertEquals(96, img.getDpiY());
        Assert.assertEquals(2.2, ((PngImageData)img).getGamma(), 0.0001f);

        PngChromaticities pngChromaticities = ((PngImageData)img).getPngChromaticities();
        Assert.assertEquals(0.3127f, pngChromaticities.getXW(), 0.0001f);
        Assert.assertEquals(0.329f, pngChromaticities.getYW(), 0.0001f);
        Assert.assertEquals(0.64f, pngChromaticities.getXR(), 0.0001f);
        Assert.assertEquals(0.33f, pngChromaticities.getYR(), 0.0001f);
        Assert.assertEquals(0.3f, pngChromaticities.getXG(), 0.0001f);
        Assert.assertEquals(0.6f, pngChromaticities.getYG(), 0.0001f);
        Assert.assertEquals(0.15f, pngChromaticities.getXB(), 0.0001f);
        Assert.assertEquals(0.06f, pngChromaticities.getYB(), 0.0001f);
    }
}