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

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.image.ImageType;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Tag("IntegrationTest")
@DisabledInNativeImage
public class WebPIntegrationTest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/webpimagesupport/WebpIntegrationTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/webpimagesupport/image/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    public static Iterable<Object[]> getWebPImages() {
        return Arrays.asList(new Object[][]{
                {"5_webp_ll", false, false},
                {"lossless", false, false},
                {"lossyWebPImage", false, false},
                {"opaqueWebPImage", false, false},
                {"animatedWebPImage", false, false},
                {"displayP3Profile", true, true},
                {"linearRGBProfile", false, true},
                // TODO DEVSIX-10022 - Support image orientation set in exif metadata
                // when modern browsers start supporting it
                {"orientation", false, false}
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getWebPImages")
    public void webpSimpleImageTest(String imageName, boolean isImageBig, boolean isPlatformDependent)
            throws IOException, InterruptedException {
        String imageFileName = SOURCE_FOLDER + imageName + ".webp";
        String outFileName = DESTINATION_FOLDER + imageName + "Pdf.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + imageName + "Pdf.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName, new WriterProperties()));

        try (InputStream fis = FileUtil.getInputStreamForFile(imageFileName)) {
            byte[] imageBytes = StreamUtil.inputStreamToArray(fis);
            PdfPage page = pdfDocument.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            ImageData img = ImageDataFactory.create(imageBytes);
            Assertions.assertEquals(ImageType.WEBP, img.getOriginalType());
            if (isImageBig) {
                canvas.addImageFittedIntoRectangle(img, new Rectangle(50, 50, 500, 700), false);
            } else {
                canvas.addImageAt(img, 50, 50, false);
            }
            canvas.release();
        }

        pdfDocument.close();

        if (isPlatformDependent) {
            Assertions.assertNull(new CompareTool().compareVisually(outFileName, cmpFileName, DESTINATION_FOLDER, 1));
        } else {
            Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
        }
    }

    @Test
    public void webpPSimpleImageUrlTest() throws IOException, InterruptedException {
        String imageFileName = SOURCE_FOLDER + "lossless.webp";
        String outFileName = DESTINATION_FOLDER + "losslessUrlPdf.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_losslessPdf.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName, new WriterProperties()));

        PdfPage page = pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        ImageData img = ImageDataFactory.create(UrlUtil.toURL(imageFileName));
        Assertions.assertEquals(ImageType.WEBP, img.getOriginalType());
        canvas.addImageAt(img, 50, 50, false);
        canvas.release();

        pdfDocument.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }
}
