/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfImageXObjectTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/xobject/PdfImageXObjectTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/xobject/PdfImageXObjectTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void addFlushedImageXObjectToCanvas() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "addFlushedImageXObjectToCanvas.pdf";
        String cmpfile = SOURCE_FOLDER + "cmp_addFlushedImageXObjectToCanvas.pdf";
        String image = SOURCE_FOLDER + "image.png";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(image));
        // flushing pdf object directly
        imageXObject.getPdfObject().makeIndirect(pdfDoc).flush();

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        canvas.addXObjectFittedIntoRectangle(imageXObject, new Rectangle(50, 500, 200, 200));
        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, cmpfile, DESTINATION_FOLDER));
    }

    @Test
    public void indexedColorPngImageXObjectTest() throws IOException {
        convertAndCompare(DESTINATION_FOLDER + "indexed.pdf",
                SOURCE_FOLDER + "cmp_indexed.pdf",
                SOURCE_FOLDER + "indexed.png");
    }

    @Test
    public void indexedColorSimpleTransparencyPngImageXObjectTest() throws IOException {
        convertAndCompare(DESTINATION_FOLDER + "indexedSimpleTransparency.pdf",
                SOURCE_FOLDER + "cmp_indexedSimpleTransparency.pdf",
                SOURCE_FOLDER + "indexedSimpleTransparency.png");
    }

    @Test
    public void grayPngImageXObjectTest() throws IOException {
        convertAndCompare(DESTINATION_FOLDER + "grayscale16Bpc.pdf",
                SOURCE_FOLDER + "cmp_grayscale16Bpc.pdf",
                SOURCE_FOLDER + "grayscale16Bpc.png");
    }

    @Test
    public void grayAlphaPngImageXObjectTest() throws IOException {
        convertAndCompare(DESTINATION_FOLDER + "graya8Bpc.pdf",
                SOURCE_FOLDER + "cmp_graya8Bpc.pdf",
                SOURCE_FOLDER + "graya8Bpc.png");
    }

    @Test
    public void grayAlphaPngWithoutEmbeddedProfileImageXObjectTest() throws IOException {
        convertAndCompare(DESTINATION_FOLDER + "graya8BpcWithoutProfile.pdf",
                SOURCE_FOLDER + "cmp_graya8BpcWithoutProfile.pdf",
                SOURCE_FOLDER + "graya8BpcWithoutProfile.png");
    }

    @Test
    public void graySimpleTransparencyPngImageXObjectTest() throws IOException {
        convertAndCompare(DESTINATION_FOLDER + "grayscaleSimpleTransparencyImage.pdf",
                SOURCE_FOLDER + "cmp_grayscaleSimpleTransparencyImage.pdf",
                SOURCE_FOLDER + "grayscaleSimpleTransparencyImage.png");
    }

    @Test
    public void rgbPngImageXObjectTest() throws IOException {
        convertAndCompare(DESTINATION_FOLDER + "rgb16Bpc.pdf",
                SOURCE_FOLDER + "cmp_rgb16Bpc.pdf",
                SOURCE_FOLDER + "rgb16Bpc.png");
    }

    @Test
    public void rgbAlphaPngImageXObjectTest() throws IOException {
        convertAndCompare(DESTINATION_FOLDER + "rgba16Bpc.pdf",
                SOURCE_FOLDER + "cmp_rgba16Bpc.pdf",
                SOURCE_FOLDER + "rgba16Bpc.png");
    }

    @Test
    public void rgbSimpleTransparencyPngImageXObjectTest() throws IOException {
        convertAndCompare(DESTINATION_FOLDER + "rgbSimpleTransparencyImage.pdf",
                SOURCE_FOLDER + "cmp_rgbSimpleTransparencyImage.pdf",
                SOURCE_FOLDER + "rgbSimpleTransparencyImage.png");
    }

    @Test
    public void sRgbImageTest() throws IOException {
        convertAndCompare(DESTINATION_FOLDER + "sRGBImage.pdf",
                SOURCE_FOLDER + "cmp_sRGBImage.pdf",
                SOURCE_FOLDER + "sRGBImage.png");
    }

    @Test
    public void group3CompressionTiffImageTest() throws IOException {
        String image = SOURCE_FOLDER + "group3CompressionImage.tif";
        convertAndCompare(DESTINATION_FOLDER + "group3CompressionTiffImage.pdf",
                SOURCE_FOLDER + "cmp_group3CompressionTiffImage.pdf",
                new PdfImageXObject(ImageDataFactory.create(UrlUtil.toURL(image))));
    }

    @Test
    public void group3CompTiffImgRecoverErrorAndDirectTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "group3CompTiffImgRecoverErrorAndDirect.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_group3CompTiffImgRecoverErrorAndDirect.pdf";
        String image = SOURCE_FOLDER + "group3CompressionImage.tif";

        try (PdfWriter writer = CompareTool.createTestPdfWriter(filename);
                PdfDocument pdfDoc = new PdfDocument(writer)) {

            PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.createTiff(UrlUtil.toURL(image),
                    true, 1, true));

            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

            canvas.addXObjectFittedIntoRectangle(imageXObject, new Rectangle(50, 500, 200, 200));
        }

        Assertions.assertNull(new CompareTool().compareByContent(filename, cmpFile, DESTINATION_FOLDER));
    }

    @Test
    public void group3CompTiffImgNoRecoverErrorAndNotDirectTest() throws IOException {
        String image = SOURCE_FOLDER + "group3CompressionImage.tif";

        convertAndCompare(DESTINATION_FOLDER + "group3CompTiffImgNoRecoverErrorAndNotDirect.pdf",
                SOURCE_FOLDER + "cmp_group3CompTiffImgNoRecoverErrorAndNotDirect.pdf",
                new PdfImageXObject(ImageDataFactory.createTiff(UrlUtil.toURL(image),
                        false, 1, false)));
    }

    @Test
    public void redundantDecodeParmsTest() throws IOException, InterruptedException {
        String srcFilename = SOURCE_FOLDER + "redundantDecodeParms.pdf";
        String destFilename = DESTINATION_FOLDER + "redundantDecodeParms.pdf";
        String cmpFilename = SOURCE_FOLDER + "cmp_redundantDecodeParms.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcFilename),
                CompareTool.createTestPdfWriter(destFilename),
                new StampingProperties())) {
        }

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, cmpFilename, DESTINATION_FOLDER));
    }

    private void convertAndCompare(String outFilename, String cmpFilename, String imageFilename)
            throws IOException {

        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFilename));
        System.out.println("Cmp pdf: " + UrlUtil.getNormalizedFileUriString(cmpFilename)+ "\n");

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFilename));

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(imageFilename));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.addXObjectFittedIntoRectangle(imageXObject, new Rectangle(50, 500, 346, imageXObject.getHeight()));
        pdfDoc.close();

        PdfDocument outDoc = new PdfDocument(CompareTool.createOutputReader(outFilename));

        PdfStream outStream = outDoc.getFirstPage().getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));

        PdfDocument cmpDoc = new PdfDocument(CompareTool.createOutputReader(cmpFilename));
        PdfStream cmpStream = cmpDoc.getFirstPage().getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));


        Assertions.assertNull(new CompareTool().compareStreamsStructure(outStream, cmpStream));

        cmpDoc.close();
        outDoc.close();
    }

    private void convertAndCompare(String outFilename, String cmpFilename,PdfImageXObject imageXObject )
            throws IOException {

        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFilename));
        System.out.println("Cmp pdf: " + UrlUtil.getNormalizedFileUriString(cmpFilename)+ "\n");

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFilename));


        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.addXObjectFittedIntoRectangle(imageXObject, new Rectangle(10, 20, 575 , 802));
        pdfDoc.close();

        PdfDocument outDoc = new PdfDocument(CompareTool.createOutputReader(outFilename));

        PdfStream outStream = outDoc.getFirstPage().getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));

        PdfDocument cmpDoc = new PdfDocument(CompareTool.createOutputReader(cmpFilename));
        PdfStream cmpStream = cmpDoc.getFirstPage().getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));


        Assertions.assertNull(new CompareTool().compareStreamsStructure(outStream, cmpStream));

        cmpDoc.close();
        outDoc.close();
    }
}
