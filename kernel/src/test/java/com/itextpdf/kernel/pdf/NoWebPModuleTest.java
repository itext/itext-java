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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.image.ImageType;
import com.itextpdf.io.image.WebPLogMessageConstant;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

@Tag("IntegrationTest")
public class NoWebPModuleTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/NoWebpModuleTest/";

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = WebPLogMessageConstant.WEBP_NOT_FOUND))
    public void isWebPSupportedTest() {
        Assertions.assertFalse(ImageDataFactory.isSupportedType(ImageType.WEBP));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = WebPLogMessageConstant.WEBP_NOT_FOUND))
    public void readWebPBytesTest() {
        byte[] webpImageDummy = new byte[]{(byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F',
                0x00, 0x00, 0x00, 0x00, (byte) 'W', (byte) 'E', (byte) 'B', (byte) 'P', 0, 0, 0};
        ImageData imageData = ImageDataFactory.createWebP(webpImageDummy);
        Assertions.assertNull(imageData);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = WebPLogMessageConstant.WEBP_NOT_FOUND))
    public void readWebPUrlTest() throws MalformedURLException {
        ImageData imageData = ImageDataFactory.createWebP(UrlUtil.toURL(SOURCE_FOLDER + "webpImage.webp"));
        Assertions.assertNull(imageData);
    }

    @Test
    public void webpNotFoundLogMessageConstantTest() {
        Assertions.assertFalse(WebPLogMessageConstant.WEBP_NOT_FOUND.isEmpty());
        Assertions.assertEquals("Processing WebP images is not supported on Android.", WebPLogMessageConstant.WEBP_NOT_FOUND);
    }
}
