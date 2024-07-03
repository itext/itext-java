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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
// Android-Conversion-Skip-File (java.awt library isn't available on Android)
public class ImageFromLanguageStandardLibraryTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/xobject/ImageFromLanguageStandardLibraryTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/xobject/ImageFromLanguageStandardLibraryTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    // See http://stackoverflow.com/questions/39119776/itext-binary-transparency-bug
    public void imageBinaryTransparencySameColorTest() throws java.io.IOException {
        String outFile = destinationFolder + "imageBinaryTransparencySameColorTest.pdf";
        String cmpFile = sourceFolder + "cmp_imageBinaryTransparencySameColorTest.pdf";

        ImageData bkgnd = ImageDataFactory.create(sourceFolder + "itext.jpg");
        PdfImageXObject image = new PdfImageXObject(bkgnd);

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFile));

        PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
        PdfPage firstPage = pdfDocument.getFirstPage();
        canvas.addXObjectFittedIntoRectangle(image, firstPage.getPageSize());
        canvas
                .beginText()
                .setTextMatrix(36, 790)
                .setFontAndSize(PdfFontFactory.createFont(), 12)
                .showText("Invisible image (both opaque and non opaque pixels have the same color)")
                .endText();
        canvas.addXObjectAt(new PdfImageXObject(
                ImageDataFactory.create(createBinaryTransparentAWTImage(null), null)), 36, 580);

        PdfDocument cmpDoc = new PdfDocument(new PdfReader(cmpFile));

        // In general case this code will probably will fail, however in this particular case we know the structure of the pdf
        PdfStream outStream = firstPage.getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));
        PdfStream cmpStream = cmpDoc.getFirstPage().getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));

        Assertions.assertNull(new CompareTool().compareStreamsStructure(outStream, cmpStream));

        cmpDoc.close();
        pdfDocument.close();

        ExtendedITextTest.printOutputPdfNameAndDir(outFile);
    }

    @Test
    // See http://stackoverflow.com/questions/39119776/itext-binary-transparency-bug
    public void imageBinaryTransparencyDifferentColorsTest() throws java.io.IOException {
        String outFile = destinationFolder + "imageBinaryTransparencyDifferentColorsTest.pdf";
        String cmpFile = sourceFolder + "cmp_imageBinaryTransparencyDifferentColorsTest.pdf";

        ImageData bkgnd = ImageDataFactory.create(sourceFolder + "itext.jpg");
        PdfImageXObject image = new PdfImageXObject(bkgnd);

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFile));

        PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
        PdfPage firstPage = pdfDocument.getFirstPage();
        canvas.addXObjectFittedIntoRectangle(image, firstPage.getPageSize());
        canvas
                .beginText()
                .setTextMatrix(36, 790)
                .setFontAndSize(PdfFontFactory.createFont(), 12)
                .showText("Invisible image (both opaque and non opaque pixels have different colors)")
                .endText();
        canvas.addXObjectAt(new PdfImageXObject(
                ImageDataFactory.create(createBinaryTransparentAWTImage(java.awt.Color.red), null)), 36, 580);

        PdfDocument cmpDoc = new PdfDocument(new PdfReader(cmpFile));

        // In general case this code will probably will fail, however in this particular case we know the structure of the pdf
        PdfStream outStream = firstPage.getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));
        PdfStream cmpStream = cmpDoc.getFirstPage().getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));

        Assertions.assertNull(new CompareTool().compareStreamsStructure(outStream, cmpStream));

        cmpDoc.close();
        pdfDocument.close();

        ExtendedITextTest.printOutputPdfNameAndDir(outFile);
    }

    // Create an ARGB AWT Image that has only 100% transparent and 0% transparent pixels.
    // All transparent pixels have the Color "backgroundColor"
    private static java.awt.image.BufferedImage createBinaryTransparentAWTImage(java.awt.Color backgroundColor) {
        java.awt.Dimension size = new java.awt.Dimension(200, 200);
        java.awt.image.BufferedImage awtImg = new java.awt.image.BufferedImage(size.width, size.height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = awtImg.createGraphics();

        if (backgroundColor != null) {

            //Usually it doesn't make much sense to set the color of transparent pixels...
            //but in this case it changes the behavior of com.itextpdf.text.Image.getInstance fundamentally!
            g2d.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC, 0f));
            g2d.setColor(backgroundColor);
            g2d.fillRect(0, 0, size.width, size.height);
        }

        g2d.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));
        g2d.setColor(java.awt.Color.black);

        java.awt.BasicStroke bs = new java.awt.BasicStroke(2);
        g2d.setStroke(bs);

        for (int i = 0; i < 5; i++) {
            g2d.drawLine((size.width + 2) / 4 * i, 0, (size.width + 2) / 4 * i, size.height - 1);
            g2d.drawLine(0, (size.height + 2) / 4 * i, size.width - 1, (size.height + 2) / 4 * i);
        }

        return awtImg;
    }
}
