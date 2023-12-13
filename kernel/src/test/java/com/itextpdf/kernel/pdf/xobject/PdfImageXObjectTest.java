/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfImageXObjectTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/xobject/PdfImageXObjectTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/xobject/PdfImageXObjectTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void addFlushedImageXObjectToCanvas() throws IOException, InterruptedException {
        String filename = destinationFolder + "addFlushedImageXObjectToCanvas.pdf";
        String cmpfile = sourceFolder + "cmp_addFlushedImageXObjectToCanvas.pdf";
        String image = sourceFolder + "image.png";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(image));
        // flushing pdf object directly
        imageXObject.getPdfObject().makeIndirect(pdfDoc).flush();

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        canvas.addXObject(imageXObject, 50, 500, 200);
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpfile, destinationFolder));
    }

    @Test
    public void indexedColorPngImageXObjectTest() throws IOException {
        convertAndCompare(destinationFolder + "indexed.pdf",
                sourceFolder + "cmp_indexed.pdf",
                sourceFolder + "indexed.png");
    }

    @Test
    public void indexedColorSimpleTransparencyPngImageXObjectTest() throws IOException {
        convertAndCompare(destinationFolder + "indexedSimpleTransparency.pdf",
                sourceFolder + "cmp_indexedSimpleTransparency.pdf",
                sourceFolder + "indexedSimpleTransparency.png");
    }

    @Test
    public void grayPngImageXObjectTest() throws IOException {
        convertAndCompare(destinationFolder + "grayscale16Bpc.pdf",
                sourceFolder + "cmp_grayscale16Bpc.pdf",
                sourceFolder + "grayscale16Bpc.png");
    }

    @Test
    public void grayAlphaPngImageXObjectTest() throws IOException {
        convertAndCompare(destinationFolder + "graya8Bpc.pdf",
                sourceFolder + "cmp_graya8Bpc.pdf",
                sourceFolder + "graya8Bpc.png");
    }

    @Test
    public void grayAlphaPngWithoutEmbeddedProfileImageXObjectTest() throws IOException {
        convertAndCompare(destinationFolder + "graya8BpcWithoutProfile.pdf",
                sourceFolder + "cmp_graya8BpcWithoutProfile.pdf",
                sourceFolder + "graya8BpcWithoutProfile.png");
    }

    @Test
    public void graySimpleTransparencyPngImageXObjectTest() throws IOException {
        convertAndCompare(destinationFolder + "grayscaleSimpleTransparencyImage.pdf",
                sourceFolder + "cmp_grayscaleSimpleTransparencyImage.pdf",
                sourceFolder + "grayscaleSimpleTransparencyImage.png");
    }

    @Test
    public void rgbPngImageXObjectTest() throws IOException {
        convertAndCompare(destinationFolder + "rgb16Bpc.pdf",
                sourceFolder + "cmp_rgb16Bpc.pdf",
                sourceFolder + "rgb16Bpc.png");
    }

    @Test
    public void rgbAlphaPngImageXObjectTest() throws IOException {
        convertAndCompare(destinationFolder + "rgba16Bpc.pdf",
                sourceFolder + "cmp_rgba16Bpc.pdf",
                sourceFolder + "rgba16Bpc.png");
    }

    @Test
    public void rgbSimpleTransparencyPngImageXObjectTest() throws IOException {
        convertAndCompare(destinationFolder + "rgbSimpleTransparencyImage.pdf",
                sourceFolder + "cmp_rgbSimpleTransparencyImage.pdf",
                sourceFolder + "rgbSimpleTransparencyImage.png");
    }

    @Test
    public void sRgbImageTest() throws IOException {
        convertAndCompare(destinationFolder + "sRGBImage.pdf",
                sourceFolder + "cmp_sRGBImage.pdf",
                sourceFolder + "sRGBImage.png");
    }

    private void convertAndCompare(String outFilename, String cmpFilename, String imageFilename)
            throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFilename));
        PdfDocument cmpDoc = new PdfDocument(new PdfReader(cmpFilename));

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(imageFilename));

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.addXObject(imageXObject, 50, 500, 346);
        pdfDoc.close();

        PdfDocument outDoc = new PdfDocument(new PdfReader(outFilename));

        PdfStream outStream = outDoc.getFirstPage().getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));
        PdfStream cmpStream = cmpDoc.getFirstPage().getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));


        Assert.assertNull(new CompareTool().compareStreamsStructure(outStream, cmpStream));


        cmpDoc.close();
        outDoc.close();

    }
}
