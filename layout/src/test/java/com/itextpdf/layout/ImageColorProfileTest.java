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
package com.itextpdf.layout;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.colors.IccProfile;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Image;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Tag("IntegrationTest")
public class ImageColorProfileTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/ImageColorProfileTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/ImageColorProfileTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.PNG_IMAGE_HAS_ICC_PROFILE_WITH_INCOMPATIBLE_NUMBER_OF_COLOR_COMPONENTS)})
    public void extractIncompatibleColorProfileTest() throws IOException {
        ImageData imageData = ImageDataFactory.create(sourceFolder + "png-incorrect-embedded-color-profile.png");
        Assertions.assertNotNull(imageData.getProfile());
    }

    @Test
    public void pngEmbeddedColorProfileTest() throws IOException, InterruptedException {
        runTest("pngEmbeddedColorProfile.pdf", "png-embedded-color-profile.png");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.PNG_IMAGE_HAS_ICC_PROFILE_WITH_INCOMPATIBLE_NUMBER_OF_COLOR_COMPONENTS),
            @LogMessage(messageTemplate = IoLogMessageConstant.IMAGE_HAS_ICC_PROFILE_WITH_INCOMPATIBLE_NUMBER_OF_COLOR_COMPONENTS_COMPARED_TO_BASE_COLOR_SPACE_IN_INDEXED_COLOR_SPACE)
    })
    public void pngIncorrectEmbeddedColorProfileTest() throws IOException, InterruptedException {
        runTest("pngIncorrectEmbeddedColorProfile.pdf", "png-incorrect-embedded-color-profile.png");
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.PNG_IMAGE_HAS_ICC_PROFILE_WITH_INCOMPATIBLE_NUMBER_OF_COLOR_COMPONENTS)})
    public void pngReplaceIncorrectEmbeddedColorProfileTest() throws IOException, InterruptedException {
        runTest("pngReplaceIncorrectColorProfile.pdf", "png-incorrect-embedded-color-profile.png", "sRGB_v4_ICC_preference.icc");
    }

    @Test
    public void pngIndexedEmbeddedColorProfileTest() throws IOException, InterruptedException {
        runTest("pngIndexedEmbeddedColorProfile.pdf", "png-indexed-embedded-color-profile.png");
    }

    @Test
    public void pngGreyscaleEmbeddedColorProfileTest() throws IOException, InterruptedException {
        runTest("pngGreyscaleEmbeddedColorProfile.pdf", "png-greyscale-embedded-color-profile.png");
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.IMAGE_HAS_ICC_PROFILE_WITH_INCOMPATIBLE_NUMBER_OF_COLOR_COMPONENTS_COMPARED_TO_COLOR_SPACE)})
    public void pngGreyscaleIncorrectColorProfileTest() throws IOException, InterruptedException {
        runTest("pngGreyscaleIncorrectColorProfile.pdf", "png-greyscale.png", "sRGB_v4_ICC_preference.icc");
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.IMAGE_HAS_INCORRECT_OR_UNSUPPORTED_COLOR_SPACE_OVERRIDDEN_BY_ICC_PROFILE)})
    public void pngUnsupportedColorSpaceTest() throws IOException, InterruptedException {
        Map<String, Object> fakeColorSpaceAttributes = new HashMap<>();
        fakeColorSpaceAttributes.put("ColorSpace", "/FakeColorSpace");
        runTest("pngUnsupportedColorSpace.pdf", "png-embedded-color-profile.png", null, fakeColorSpaceAttributes);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.IMAGE_HAS_INCORRECT_OR_UNSUPPORTED_BASE_COLOR_SPACE_IN_INDEXED_COLOR_SPACE_OVERRIDDEN_BY_ICC_PROFILE)})
    public void PngUnsupportedBaseColorSpace() throws IOException, InterruptedException {
        Map<String, Object> fakeColorSpaceAttributes = new HashMap<>();
        String lookup = PdfEncodings.convertToString(new byte[]{0, 0, 0, (byte) 0xff, (byte) 0xff, (byte) 0xff}, null);
        fakeColorSpaceAttributes.put("ColorSpace", new Object[]{"/Indexed", "/FakeColorSpace", 1, lookup});
        runTest("pngUnsupportedBaseColorSpace.pdf", "png-indexed-embedded-color-profile.png", "sRGB_v4_ICC_preference.icc", fakeColorSpaceAttributes);
    }

    @Test
    public void pngNoColorProfileTest() throws IOException, InterruptedException {
        runTest("pngNoColorProfile.pdf", "png-greyscale.png");
    }

    private void runTest(String pdfName, String imageName) throws IOException, InterruptedException {
        runTest(pdfName, imageName, null, null);
    }

    private void runTest(String pdfName, String imageName, String colorProfileName) throws IOException, InterruptedException {
        runTest(pdfName, imageName, colorProfileName, null);
    }

    private void runTest(String pdfName, String imageName, String colorProfileName, Map<String, Object> customImageAttribute) throws IOException, InterruptedException {
        String outFileName = destinationFolder + pdfName;
        String cmpFileName = sourceFolder + "cmp_" + pdfName;
        String diff = "diff_" + pdfName + "_";

        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);
        ImageData imageData = ImageDataFactory.create(sourceFolder + imageName);
        if (customImageAttribute != null) {
            imageData.getImageAttributes().putAll(customImageAttribute);
        }
        if (colorProfileName != null) {
            imageData.setProfile(IccProfile.getInstance(sourceFolder + colorProfileName));
        }
        Image png = new Image(imageData);
        png.setAutoScale(true);

        document.add(png);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, diff));
    }
}
