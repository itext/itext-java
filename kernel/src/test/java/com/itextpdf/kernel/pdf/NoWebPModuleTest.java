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
}
