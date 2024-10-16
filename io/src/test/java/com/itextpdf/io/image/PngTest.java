/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

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

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PngTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/io/image/PngTest/";

    @Test
    public void grayscale8BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "grayscale8Bpc.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(100, img.getWidth(), 0);
        Assertions.assertEquals(100, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
        Assertions.assertEquals(1, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(0, ((PngImageData)img).getColorType());
    }

    @Test
    // iText explicitly processes 16bit images as 8bit
    public void grayscale16BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "grayscale16Bpc.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(100, img.getWidth(), 0);
        Assertions.assertEquals(100, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
        Assertions.assertEquals(1, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(0, ((PngImageData)img).getColorType());
    }

    @Test
    public void graya8BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "graya8Bpc.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(100, img.getWidth(), 0);
        Assertions.assertEquals(100, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
        Assertions.assertEquals(1, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(4, ((PngImageData)img).getColorType());
        Assertions.assertNotNull(img.getImageMask());
        Assertions.assertEquals(1, img.getImageMask().getColorEncodingComponentsNumber());
        Assertions.assertEquals(8, img.getImageMask().getBpc());
    }

    @Test
    public void graya8BpcDepthWithoutEmbeddedProfileImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "graya8BpcWithoutProfile.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(100, img.getWidth(), 0);
        Assertions.assertEquals(100, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
        Assertions.assertEquals(1, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(4, ((PngImageData) img).getColorType());
        Assertions.assertNotNull(img.getImageMask());
        Assertions.assertEquals(1, img.getImageMask().getColorEncodingComponentsNumber());
        Assertions.assertEquals(8, img.getImageMask().getBpc());
        Assertions.assertNull(img.getProfile());
    }

    @Test
    public void graya8BpcAddColorToAlphaImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "graya8BpcAddColorToAlpha.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(100, img.getWidth(), 0);
        Assertions.assertEquals(100, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
        Assertions.assertEquals(1, img.getColorEncodingComponentsNumber());
    }

    @Test
    public void rgb8BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgb8Bpc.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(100, img.getWidth(), 0);
        Assertions.assertEquals(100, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
        Assertions.assertEquals(3, img.getColorEncodingComponentsNumber());
    }

    @Test
    // iText explicitly processes 16bit images as 8bit
    public void rgb16BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgb16Bpc.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(100, img.getWidth(), 0);
        Assertions.assertEquals(100, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
        Assertions.assertEquals(3, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(2, ((PngImageData)img).getColorType());
    }

    @Test
    public void rgbWithoutSaveColorProfileImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgbWithoutSaveColorProfile.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(100, img.getWidth(), 0);
        Assertions.assertEquals(100, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
        Assertions.assertEquals(3, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(2, ((PngImageData)img).getColorType());
        Assertions.assertNull(img.getProfile());
    }

    @Test
    public void rgba8BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgba8Bpc.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(100, img.getWidth(), 0);
        Assertions.assertEquals(100, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
        Assertions.assertEquals(3, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(6, ((PngImageData)img).getColorType());
        Assertions.assertNotNull(img.getImageMask());
        Assertions.assertEquals(1, img.getImageMask().getColorEncodingComponentsNumber());
        Assertions.assertEquals(8, img.getImageMask().getBpc());
    }

    @Test
    // iText explicitly processes 16bit images as 8bit
    public void rgba16BpcDepthImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgba16Bpc.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(100, img.getWidth(), 0);
        Assertions.assertEquals(100, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
        Assertions.assertEquals(3, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(6, ((PngImageData)img).getColorType());
        Assertions.assertNotNull(img.getImageMask());
        Assertions.assertEquals(1, img.getImageMask().getColorEncodingComponentsNumber());
        Assertions.assertEquals(8, img.getImageMask().getBpc());
    }

    @Test
    public void indexed2BpcImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "indexed2BpcImage.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(346, img.getWidth(), 0);
        Assertions.assertEquals(49, img.getHeight(), 0);
        Assertions.assertEquals(2, img.getBpc());
        //Indexed colorspace contains one component indeed
        Assertions.assertEquals(1, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(3, ((PngImageData)img).getColorType());
    }

    @Test
    public void indexed1BpcImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "indexed1BpcImage.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(100, img.getWidth(), 0);
        Assertions.assertEquals(100, img.getHeight(), 0);
        Assertions.assertEquals(1, img.getBpc());
        //Indexed colorspace contains one component indeed
        Assertions.assertEquals(1, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(3, ((PngImageData)img).getColorType());
    }

    @Test
    public void indexed2BpcWithAlphaChannelTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "indexed2BpcWithAlphaChannel.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(346, img.getWidth(), 0);
        Assertions.assertEquals(49, img.getHeight(), 0);
        Assertions.assertEquals(2, img.getBpc());
        //Indexed colorspace contains one component indeed
        Assertions.assertEquals(1, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(3, ((PngImageData)img).getColorType());
    }

    @Test
    public void grayscaleSimpleTransparencyImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "grayscaleSimpleTransparencyImage.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(200, img.getWidth(), 0);
        Assertions.assertEquals(200, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
        Assertions.assertEquals(1, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(0, ((PngImageData)img).getColorType());
        Assertions.assertNotNull(img.getImageAttributes().entrySet());
        Assertions.assertEquals("[0 0]", img.getImageAttributes()
                .get(PngImageHelperConstants.MASK));

    }

    @Test
    public void rgbSimpleTransparencyImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "rgbSimpleTransparencyImage.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(600, img.getWidth(), 0);
        Assertions.assertEquals(100, img.getHeight(), 0);
        Assertions.assertEquals(8, img.getBpc());
        Assertions.assertEquals(3, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(2, ((PngImageData)img).getColorType());
        Assertions.assertNotNull(img.getImageAttributes().entrySet());
        Assertions.assertEquals("[255 255 0 0 0 0]", img.getImageAttributes()
                .get(PngImageHelperConstants.MASK));
    }

    @Test
    public void indexedAddColorToAlphaImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "indexedAddColorToAlpha.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(346, img.getWidth(), 0);
        Assertions.assertEquals(49, img.getHeight(), 0);
        Assertions.assertEquals(2, img.getBpc());
        //Indexed colorspace contains one component indeed
        Assertions.assertEquals(1, img.getColorEncodingComponentsNumber());
        Assertions.assertEquals(3, ((PngImageData)img).getColorType());
        Assertions.assertNotNull(img.getImageAttributes().entrySet());
        Assertions.assertEquals(0, ((int[])img.getImageAttributes()
                .get(PngImageHelperConstants.MASK))[0]);
        Assertions.assertEquals(0, ((int[])img.getImageAttributes()
                .get(PngImageHelperConstants.MASK))[1]);
    }

    @Test
    public void size50Px30DpiImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "size50Px30Dpi.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(50, img.getWidth(), 0);
        Assertions.assertEquals(50, img.getHeight(), 0);
        Assertions.assertEquals(30, img.getDpiX());
        Assertions.assertEquals(30, img.getDpiY());
    }

    @Test
    public void size50Px300DpiImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "size50Px300Dpi.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(50, img.getWidth(), 0);
        Assertions.assertEquals(50, img.getHeight(), 0);
        Assertions.assertEquals(300, img.getDpiX());
        Assertions.assertEquals(300, img.getDpiY());
    }

    @Test
    public void size150Px72DpiImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "size150Px72Dpi.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(150, img.getWidth(), 0);
        Assertions.assertEquals(150, img.getHeight(), 0);
        Assertions.assertEquals(72, img.getDpiX());
        Assertions.assertEquals(72, img.getDpiY());
    }

    @Test
    public void size300Px72DpiImageTest() throws IOException {
        ImageData img = ImageDataFactory.create(sourceFolder + "size300Px72Dpi.png");
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(300, img.getWidth(), 0);
        Assertions.assertEquals(300, img.getHeight(), 0);
        Assertions.assertEquals(72, img.getDpiX());
        Assertions.assertEquals(72, img.getDpiY());
    }

    @Test
    public void size300Px300DpiImageTest() throws IOException {
        // Test a more specific entry point
        ImageData img = ImageDataFactory.createPng(UrlUtil.toURL(sourceFolder + "size300Px300Dpi.png"));
        Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
        Assertions.assertEquals(300, img.getWidth(), 0);
        Assertions.assertEquals(300, img.getHeight(), 0);
        Assertions.assertEquals(300, img.getDpiX());
        Assertions.assertEquals(300, img.getDpiY());
    }

    @Test
    public void sRGBImageTest() throws IOException {
        try (InputStream fis = FileUtil.getInputStreamForFile(sourceFolder + "sRGBImage.png")) {
            byte[] imageBytes = StreamUtil.inputStreamToArray(fis);
            // Test a more specific entry point
            ImageData img = ImageDataFactory.createPng(imageBytes);
            Assertions.assertEquals(ImageType.PNG, img.getOriginalType());
            Assertions.assertEquals(50, img.getWidth(), 0);
            Assertions.assertEquals(50, img.getHeight(), 0);
            Assertions.assertEquals(96, img.getDpiX());
            Assertions.assertEquals(96, img.getDpiY());
            Assertions.assertEquals(2.2, ((PngImageData) img).getGamma(), 0.0001f);

            PngChromaticities pngChromaticities = ((PngImageData) img).getPngChromaticities();
            Assertions.assertEquals(0.3127f, pngChromaticities.getXW(), 0.0001f);
            Assertions.assertEquals(0.329f, pngChromaticities.getYW(), 0.0001f);
            Assertions.assertEquals(0.64f, pngChromaticities.getXR(), 0.0001f);
            Assertions.assertEquals(0.33f, pngChromaticities.getYR(), 0.0001f);
            Assertions.assertEquals(0.3f, pngChromaticities.getXG(), 0.0001f);
            Assertions.assertEquals(0.6f, pngChromaticities.getYG(), 0.0001f);
            Assertions.assertEquals(0.15f, pngChromaticities.getXB(), 0.0001f);
            Assertions.assertEquals(0.06f, pngChromaticities.getYB(), 0.0001f);
        }
    }
}
