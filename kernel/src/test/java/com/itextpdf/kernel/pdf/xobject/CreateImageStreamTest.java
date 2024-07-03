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
package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class CreateImageStreamTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/xobject/CreateImageStreamTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/xobject/CreateImageStreamTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    public void compareColorspacesTest() throws IOException, InterruptedException {
        String[] imgFiles = new String[] {
                "adobe.png",
                "anon.gif",
                "anon.jpg",
                "anon.png",
                "gamma.png",
                "odd.png",
                "rec709.jpg",
                "srgb.jpg",
                "srgb.png",
        };

        String out = destinationFolder + "compareColorspacesTest.pdf";
        String cmp = sourceFolder + "cmp_compareColorspacesTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(out));
        PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
        canvas.beginText().moveText(40, 730).setFontAndSize(PdfFontFactory.createFont(), 12)
                .showText("The images below are in row and expected to form four continuous lines of constant colors.")
                .endText();
        for (int i = 0; i < imgFiles.length; i++) {
            String imgFile = imgFiles[i];
            PdfImageXObject imageXObject = new PdfImageXObject(
                    ImageDataFactory.create(sourceFolder + "compare_colorspaces/" + imgFile));
            canvas.addXObjectFittedIntoRectangle(imageXObject, new Rectangle(50 + i * 40, 550, 40, 160));
        }

        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder, "diff_"));
    }

    @Test
    public void createDictionaryFromMapIntArrayTest() throws IOException, InterruptedException {
        testSingleImage("createDictionaryFromMapIntArrayTest.png");
    }

    @Test
    public void imgCalrgb() throws IOException, InterruptedException {
        testSingleImage("img_calrgb.png");
    }

    @Test
    public void imgCmyk() throws IOException, InterruptedException {
        testSingleImage("img_cmyk.tif");
    }

    @Test
    public void imgCmykIcc() throws IOException, InterruptedException {
        testSingleImage("img_cmyk_icc.tif");
    }

    @Test
    public void imgIndexed() throws IOException, InterruptedException {
        testSingleImage("img_indexed.png");
    }

    @Test
    public void imgRgb() throws IOException, InterruptedException {
        testSingleImage("img_rgb.png");
    }

    @Test
    public void imgRgbIcc() throws IOException, InterruptedException {
        testSingleImage("img_rgb_icc.png");
    }

    @Test
    public void addPngImageIndexedColorspaceTest() throws IOException, InterruptedException {
        testSingleImage("pngImageIndexedColorspace.png");
    }

    private void testSingleImage(String imgName) throws IOException, InterruptedException {
        String out = destinationFolder + imgName.substring(0, imgName.length() - 4) + ".pdf";
        String cmp = sourceFolder + "cmp_" + imgName.substring(0, imgName.length() - 4) + ".pdf";

        ImageData img = ImageDataFactory.create(sourceFolder + imgName);
        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(out));
        PdfImageXObject imageXObject = new PdfImageXObject(img);
        new PdfCanvas(pdfDocument.addNewPage(new PageSize(img.getWidth(), img.getHeight())))
                .addXObjectFittedIntoRectangle(imageXObject, new Rectangle(0, 0, img.getWidth(), img.getHeight()));
        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder, "diff_"));
    }
}
