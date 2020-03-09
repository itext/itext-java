/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
        // TODO DEVSIX-1313
        // Update cmp file after the specified ticket will be resolved
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