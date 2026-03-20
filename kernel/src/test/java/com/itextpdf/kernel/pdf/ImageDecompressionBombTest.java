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
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class ImageDecompressionBombTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/ImageDecompressionBombTest/";
    public static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/kernel/pdf/ImageDecompressionBombTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static Collection<Object[]> bombImagesSource() {
        return Arrays.asList(new Object[][]{
                {"10K.png"},
                {"10K.jpeg"},
                {"10K.j2k"},
                {"10K.tiff"},
                {"10K.gif"}
        });
    }

    public static Collection<Object[]> largeHeaderSmallDataSource() {
        return Arrays.asList(new Object[][]{
                {"largeHeaderSmallData.jp2"},
                {"largeHeaderSmallData.jpeg"},
                {"largeHeaderSmallData.png"}
        });
    }

    public static Collection<Object[]> smallHeaderLargeDataSource() {
        return Arrays.asList(new Object[][]{
                {"smallHeaderLargeData.png"},
                {"smallHeaderLargeData.jpeg"},
                {"smallHeaderLargeData.j2k"},
                {"smallHeaderLargeData.tiff"},
                {"smallHeaderLargeData.gif"}
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("bombImagesSource")
    public void bombImagesTest(String fileName) {
        Assertions.assertThrows(IOException.class, () -> processImage(fileName));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("largeHeaderSmallDataSource")
    public void largeHeaderSmallDataImagesTest(String fileName) {
        // This is done as a separate test to showcase that we don't have another simple way to catch
        // decompression bombs rather than the one shown in processImage.
        // Images tested here can be read without OOM, but we still throw and can't distinguish them from the ones
        // tested in bombImagesTest.
        Assertions.assertThrows(IOException.class, () -> processImage(fileName));
    }

    @Disabled("DEVSIX-9835: OutOfMemoryError when processing PNG images with small reported dimensions but large actual data")
    @ParameterizedTest(name = "{0}")
    @MethodSource("smallHeaderLargeDataSource")
    public void smallHeaderLargeDataImagesTest(String fileName) {
        AssertUtil.doesNotThrow(() -> processImage(fileName));
    }

    @Disabled("DEVSIX-9835: OutOfMemoryError when processing PNG images with small reported dimensions but large actual data")
    @ParameterizedTest(name = "{0}")
    @MethodSource("bombImagesSource")
    public void embeddedBombImageBytesFromPdfTest(String fileName) {
        AssertUtil.doesNotThrow(() -> {
            String pdfPath = createPdfWithImage(fileName);
            byte[] bytes = readEmbeddedImageBytes(pdfPath);

            Assertions.assertNotNull(bytes);
            Assertions.assertTrue(bytes.length > 0);
        });
    }

    private void processImage(String fileName) throws IOException {
        ImageData imageData = ImageDataFactory.create(SOURCE_FOLDER + fileName);
        PdfImageXObject xObject = new PdfImageXObject(imageData);

        long width = (long) imageData.getWidth();
        long height = (long) imageData.getHeight();
        long pixels = width * height;

        if (pixels > 2_000_000L) {
            throw new IOException("Image is too large to be processed safely: " + pixels + " pixels");
        }

        // It really fails only for png and tiff
        xObject.getImageBytes();
    }

    private String createPdfWithImage(String fileName) throws IOException {
        String pdfPath = DESTINATION_FOLDER + fileName + ".pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pdfPath))) {
            PdfPage page = pdfDocument.addNewPage();

            ImageData imageData = ImageDataFactory.create(SOURCE_FOLDER + fileName);
            PdfImageXObject imageXObject = new PdfImageXObject(imageData);

            new PdfCanvas(page).addXObject(imageXObject);
        }

        return pdfPath;
    }

    private byte[] readEmbeddedImageBytes(String pdfPath) throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(pdfPath))) {
            PdfDictionary xObjects = pdfDocument.getFirstPage().getResources().getResource(PdfName.XObject);
            Assertions.assertNotNull(xObjects, "No XObject resources found in PDF");

            for (PdfObject xObject : xObjects.values()) {
                if (xObject instanceof PdfStream) {
                    PdfStream stream = (PdfStream) xObject;
                    if (PdfName.Image.equals(stream.getAsName(PdfName.Subtype))) {
                        return new PdfImageXObject(stream).getImageBytes();
                    }
                }
            }
        }

        Assertions.fail("No image XObject found in PDF");
        return null;
    }
}
