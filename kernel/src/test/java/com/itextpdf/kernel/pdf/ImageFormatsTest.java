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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class ImageFormatsTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/ImageFormatsTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/ImageFormatsTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void imagesWithDifferentDepth() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "transparencyTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_transparencyTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName, new WriterProperties()
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
        canvas.addImage(img, 100, 780, 200, false);


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
        canvas.addImage(img, 300, 780, 200, false);


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
        canvas.addImage(img, 500, 780, 200, false);

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
        canvas.addImage(img, 100, 300, 200, false);

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
        canvas.addImage(img, 300, 300, 200, false);

        canvas.release();
        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    public void png_imageTransparancy_8bitDepthImage() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "png_imageTransparancy_8bitDepthImage.pdf";
        String cmpFileName = sourceFolder + "cmp_png_imageTransparancy_8bitDepthImage.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName, new WriterProperties()
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
        canvas.addImage(img, 100, 450, 200, false);

        canvas.release();
        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    public void png_imageTransparancy_24bitDepthImage() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "png_imageTransparancy_24bitDepthImage.pdf";
        String cmpFileName = sourceFolder + "cmp_png_imageTransparancy_24bitDepthImage.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName, new WriterProperties()
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
        canvas.addImage(img, 100, 450, 200, false);

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
        canvas.addImage(img, 116, 100, 200, false);

        canvas.release();
        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff_"));
    }
}
