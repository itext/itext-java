/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
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
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Category(IntegrationTest.class)
public class ImageColorProfileTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/ImageColorProfileTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/ImageColorProfileTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.PNG_IMAGE_HAS_ICC_PROFILE_WITH_INCOMPATIBLE_NUMBER_OF_COLOR_COMPONENTS)})
    public void extractIncompatibleColorProfileTest() throws IOException {
        ImageData imageData = ImageDataFactory.create(sourceFolder + "png-incorrect-embedded-color-profile.png");
        Assert.assertNotNull(imageData.getProfile());
    }

    @Test
    public void pngEmbeddedColorProfileTest() throws IOException, InterruptedException {
        runTest("pngEmbeddedColorProfile.pdf", "png-embedded-color-profile.png");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.PNG_IMAGE_HAS_ICC_PROFILE_WITH_INCOMPATIBLE_NUMBER_OF_COLOR_COMPONENTS),
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_ICC_PROFILE_WITH_INCOMPATIBLE_NUMBER_OF_COLOR_COMPONENTS_COMPARED_TO_BASE_COLOR_SPACE_IN_INDEXED_COLOR_SPACE)
    })
    public void pngIncorrectEmbeddedColorProfileTest() throws IOException, InterruptedException {
        runTest("pngIncorrectEmbeddedColorProfile.pdf", "png-incorrect-embedded-color-profile.png");
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.PNG_IMAGE_HAS_ICC_PROFILE_WITH_INCOMPATIBLE_NUMBER_OF_COLOR_COMPONENTS)})
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
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_ICC_PROFILE_WITH_INCOMPATIBLE_NUMBER_OF_COLOR_COMPONENTS_COMPARED_TO_COLOR_SPACE)})
    public void pngGreyscaleIncorrectColorProfileTest() throws IOException, InterruptedException {
        runTest("pngGreyscaleIncorrectColorProfile.pdf", "png-greyscale.png", "sRGB_v4_ICC_preference.icc");
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_INCORRECT_OR_UNSUPPORTED_COLOR_SPACE_OVERRIDDEN_BY_ICC_PROFILE)})
    public void pngUnsupportedColorSpaceTest() throws IOException, InterruptedException {
        Map<String, Object> fakeColorSpaceAttributes = new HashMap<>();
        fakeColorSpaceAttributes.put("ColorSpace", "/FakeColorSpace");
        runTest("pngUnsupportedColorSpace.pdf", "png-embedded-color-profile.png", null, fakeColorSpaceAttributes);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_INCORRECT_OR_UNSUPPORTED_BASE_COLOR_SPACE_IN_INDEXED_COLOR_SPACE_OVERRIDDEN_BY_ICC_PROFILE)})
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, diff));
    }
}
