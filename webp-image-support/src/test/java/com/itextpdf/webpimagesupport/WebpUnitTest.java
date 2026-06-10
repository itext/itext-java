/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.webpimagesupport;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.image.ImageType;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;

import java.net.MalformedURLException;
import java.net.URL;

@Tag("UnitTest")
@DisabledInNativeImage
public class WebpUnitTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/webpimagesupport/image/";

    @Test
    public void webpExceptionTest() {
        byte[] rawWebPBytes = new byte[]{(byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F',
                0x00, 0x00, 0x00, 0x00, (byte) 'W', (byte) 'E', (byte) 'B', (byte) 'P', 55};

        Exception e = Assertions.assertThrows(IOException.class,
                () -> new WebPImageData(rawWebPBytes));
        Assertions.assertEquals(MessageFormatUtil.format(
                IoExceptionMessageConstant.WEBP_IMAGE_EXCEPTION), e.getMessage());
    }

    @Test
    public void zeroBytesWebpExceptionTest() {
        byte[] rawWebPBytes = new byte[]{};

        Exception e = Assertions.assertThrows(IOException.class,
                () -> new WebPImageData(rawWebPBytes));
        Assertions.assertEquals(MessageFormatUtil.format(
                IoExceptionMessageConstant.WEBP_IMAGE_EXCEPTION), e.getMessage());
    }

    @Test
    public void urlWebpExceptionTest() throws MalformedURLException {
        URL url = new URL("https://someNonsense");

        Exception e = Assertions.assertThrows(IOException.class,
                () -> new WebPImageData(url));
        Assertions.assertEquals(MessageFormatUtil.format(
                IoExceptionMessageConstant.WEBP_IMAGE_EXCEPTION), e.getMessage());
    }

    @Test
    public void notWebpExceptionTest() {
        byte[] rawNotWebpBytes = new byte[]{(byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F',
                0x00, 0x00, 0x00, 0x00, (byte) 'W', (byte) 'E', (byte) 'B'};

        Exception e = Assertions.assertThrows(IOException.class,
                () -> new WebPImageData(rawNotWebpBytes));
        Assertions.assertEquals(MessageFormatUtil.format(
                IoExceptionMessageConstant.WEBP_IMAGE_EXCEPTION), e.getMessage());
    }

    @Test
    public void webpTest() throws MalformedURLException {
        String imageFileName = SOURCE_FOLDER + "lossyWebPImage.webp";
        WebPImageData webpImage = new WebPImageData(UrlUtil.toURL(imageFileName));

        Assertions.assertNotNull(webpImage);
        Assertions.assertEquals(512, webpImage.getHeight());
        Assertions.assertEquals(512, webpImage.getWidth());
        Assertions.assertEquals(8, webpImage.getBpc());
        Assertions.assertEquals(786432, webpImage.getData().length);
        Assertions.assertNotNull(webpImage.getImageMask());
    }

    @Test
    public void webpIsSupportedTest() {
        Assertions.assertTrue(ImageDataFactory.isSupportedType(ImageType.WEBP));
    }
}
