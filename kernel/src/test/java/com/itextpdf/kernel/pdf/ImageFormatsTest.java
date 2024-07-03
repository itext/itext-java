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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class ImageFormatsTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/ImageFormatsTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/ImageFormatsTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void imagesWithDifferentDepth() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "transparencyTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_transparencyTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName, new WriterProperties()
                .setCompressionLevel(CompressionConstants.NO_COMPRESSION)));
        PdfPage page = pdfDocument.addNewPage(PageSize.A3);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(ColorConstants.LIGHT_GRAY).fill();
        canvas.rectangle(80, 0, 700, 1200).fill();

        canvas
                .saveState()
                .beginText()
                .moveText(116, 1150)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 14)
                .setFillColor(ColorConstants.MAGENTA)
                .showText("8 bit depth PNG")
                .endText()
                .restoreState();
        ImageData img = ImageDataFactory.create(sourceFolder + "manualTransparency_8bit.png");
        canvas.addImageFittedIntoRectangle(img, new Rectangle(100, 780, 200, 292.59f), false);


        canvas
                .saveState()
                .beginText()
                .moveText(316, 1150)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 14)
                .setFillColor(ColorConstants.MAGENTA)
                .showText("24 bit depth PNG")
                .endText()
                .restoreState();
        img = ImageDataFactory.create(sourceFolder + "manualTransparency_24bit.png");
        canvas.addImageFittedIntoRectangle(img, new Rectangle(300, 780, 200, 292.59f), false);


        canvas
                .saveState()
                .beginText()
                .moveText(516, 1150)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 14)
                .setFillColor(ColorConstants.MAGENTA)
                .showText("32 bit depth PNG")
                .endText()
                .restoreState();
        img = ImageDataFactory.create(sourceFolder + "manualTransparency_32bit.png");
        canvas.addImageFittedIntoRectangle(img, new Rectangle(500, 780, 200, 292.59f), false);

        canvas
                .saveState()
                .beginText()
                .moveText(116, 650)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .setFillColor(ColorConstants.MAGENTA)
                .showText("GIF image ")
                .endText()
                .restoreState();
        img = ImageDataFactory.create(sourceFolder + "manualTransparency_gif.gif");
        canvas.addImageFittedIntoRectangle(img, new Rectangle(100, 300, 200, 292.59f), false);

        canvas
                .saveState()
                .beginText()
                .moveText(316, 650)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .setFillColor(ColorConstants.MAGENTA)
                .showText("TIF image ")
                .endText()
                .restoreState();
        img = ImageDataFactory.create(sourceFolder + "manualTransparency_tif.tif");
        canvas.addImageFittedIntoRectangle(img, new Rectangle(300, 300, 200, 292.59f), false);

        canvas.release();
        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    public void png_imageTransparency_8bitDepthImage() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "png_imageTransparancy_8bitDepthImage.pdf";
        String cmpFileName = sourceFolder + "cmp_png_imageTransparancy_8bitDepthImage.pdf";
        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName, new WriterProperties()
                .setCompressionLevel(CompressionConstants.NO_COMPRESSION)));
        PdfPage page = pdfDocument.addNewPage(PageSize.A4);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(ColorConstants.LIGHT_GRAY).fill();
        canvas.rectangle(80, 0, PageSize.A4.getWidth()-80, PageSize.A4.getHeight()).fill();

        canvas
                .saveState()
                .beginText()
                .moveText(116, 800)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 14)
                .setFillColor(ColorConstants.MAGENTA)
                .showText("8 bit depth PNG")
                .moveText(0,-20)
                .showText("This image should not have a black rectangle as background")
                .endText()
                .restoreState();
        ImageData img = ImageDataFactory.create(sourceFolder + "manualTransparency_8bit.png");
        canvas.addImageFittedIntoRectangle(img, new Rectangle(100, 450, 200, 292.59f), false);

        canvas.release();
        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    public void png_imageTransparency_24bitDepthImage() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "png_imageTransparancy_24bitDepthImage.pdf";
        String cmpFileName = sourceFolder + "cmp_png_imageTransparancy_24bitDepthImage.pdf";
        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName, new WriterProperties()
                .setCompressionLevel(CompressionConstants.NO_COMPRESSION)));
        PdfPage page = pdfDocument.addNewPage(PageSize.A4);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(ColorConstants.LIGHT_GRAY).fill();
        canvas.rectangle(80, 0, PageSize.A4.getWidth()-80, PageSize.A4.getHeight()).fill();

        canvas
                .saveState()
                .beginText()
                .moveText(116, 800)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 14)
                .setFillColor(ColorConstants.MAGENTA)
                .showText("24 bit depth PNG")
                .moveText(0,-20)
                .showText("This image should not have a white rectangle as background")
                .endText()
                .restoreState();
        ImageData img = ImageDataFactory.create(sourceFolder + "manualTransparency_24bit.png");
        canvas.addImageFittedIntoRectangle(img, new Rectangle(100, 450, 200, 292.59f), false);

        canvas
                .saveState()
                .beginText()
                .moveText(116, 400)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 14)
                .setFillColor(ColorConstants.MAGENTA)
                .showText("32 bit depth PNG")
                .endText()
                .restoreState();
        img = ImageDataFactory.create(sourceFolder + "manualTransparency_32bit.png");
        canvas.addImageFittedIntoRectangle(img, new Rectangle(116, 100, 200, 292.59f), false);

        canvas.release();
        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff_"));
    }
}
