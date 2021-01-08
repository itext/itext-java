/*
This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class ImageFromLanguageStandardLibraryTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/xobject/ImageFromLanguageStandardLibraryTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/xobject/ImageFromLanguageStandardLibraryTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    // See http://stackoverflow.com/questions/39119776/itext-binary-transparency-bug
    public void imageBinaryTransparencySameColorTest() throws java.io.IOException {
        String outFile = destinationFolder + "imageBinaryTransparencySameColorTest.pdf";
        String cmpFile = sourceFolder + "cmp_imageBinaryTransparencySameColorTest.pdf";

        ImageData bkgnd = ImageDataFactory.create(sourceFolder + "itext.jpg");
        PdfImageXObject image = new PdfImageXObject(bkgnd);

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile));

        PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
        PdfPage firstPage = pdfDocument.getFirstPage();
        canvas.addXObject(image, firstPage.getPageSize());
        canvas
                .beginText()
                .setTextMatrix(36, 790)
                .setFontAndSize(PdfFontFactory.createFont(), 12)
                .showText("Invisible image (both opaque and non opaque pixels have the same color)")
                .endText();
        canvas.addXObject(new PdfImageXObject(
                ImageDataFactory.create(createBinaryTransparentAWTImage(null), null)), 36, 580);

        PdfDocument cmpDoc = new PdfDocument(new PdfReader(cmpFile));

        // In general case this code will probably will fail, however in this particular case we know the structure of the pdf
        PdfStream outStream = firstPage.getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));
        PdfStream cmpStream = cmpDoc.getFirstPage().getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));

        Assert.assertNull(new CompareTool().compareStreamsStructure(outStream, cmpStream));

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

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile));

        PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
        PdfPage firstPage = pdfDocument.getFirstPage();
        canvas.addXObject(image, firstPage.getPageSize());
        canvas
                .beginText()
                .setTextMatrix(36, 790)
                .setFontAndSize(PdfFontFactory.createFont(), 12)
                .showText("Invisible image (both opaque and non opaque pixels have different colors)")
                .endText();
        canvas.addXObject(new PdfImageXObject(
                ImageDataFactory.create(createBinaryTransparentAWTImage(Color.red), null)), 36, 580);

        PdfDocument cmpDoc = new PdfDocument(new PdfReader(cmpFile));

        // In general case this code will probably will fail, however in this particular case we know the structure of the pdf
        PdfStream outStream = firstPage.getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));
        PdfStream cmpStream = cmpDoc.getFirstPage().getResources().getResource(PdfName.XObject).getAsStream(new PdfName("Im1"));

        Assert.assertNull(new CompareTool().compareStreamsStructure(outStream, cmpStream));

        cmpDoc.close();
        pdfDocument.close();

        ExtendedITextTest.printOutputPdfNameAndDir(outFile);
    }

    // Create an ARGB AWT Image that has only 100% transparent and 0% transparent pixels.
    // All transparent pixels have the Color "backgroundColor"
    private static BufferedImage createBinaryTransparentAWTImage(Color backgroundColor) {
        Dimension size = new Dimension(200, 200);
        BufferedImage awtImg = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = awtImg.createGraphics();

        if (backgroundColor != null) {

            //Usually it doesn't make much sense to set the color of transparent pixels...
            //but in this case it changes the behavior of com.itextpdf.text.Image.getInstance fundamentally!
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0f));
            g2d.setColor(backgroundColor);
            g2d.fillRect(0, 0, size.width, size.height);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setColor(Color.black);

        BasicStroke bs = new BasicStroke(2);
        g2d.setStroke(bs);

        for (int i = 0; i < 5; i++) {
            g2d.drawLine((size.width + 2) / 4 * i, 0, (size.width + 2) / 4 * i, size.height - 1);
            g2d.drawLine(0, (size.height + 2) / 4 * i, size.width - 1, (size.height + 2) / 4 * i);
        }

        return awtImg;
    }
}
