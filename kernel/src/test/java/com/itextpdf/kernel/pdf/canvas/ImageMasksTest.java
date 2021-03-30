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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class ImageMasksTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/ImageMasksTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/canvas/ImageMasksTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void imageResizedParentWithHardMaskTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageResizedParentWithHardMask.pdf";
        String cmpFileName = sourceFolder + "cmp_imageResizedParentWithHardMask.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));

        ImageData mask = ImageDataFactory.create(sourceFolder + "hardMask.png");
        mask.makeMask();

        ImageData img1 = ImageDataFactory.create(sourceFolder + "sRGBImageBig.png");
        img1.setImageMask(mask);

        ImageData img2 = ImageDataFactory.create(sourceFolder + "sRGBImage.png");
        img2.setImageMask(mask);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.addImageAt(img1, 30, 500, false);
        canvas.addImageAt(img2, 430, 500, false);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    // TODO: DEVSIX-4992
    public void diffMasksOnSameImageXObjectTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "diffMasksOnSameImageXObject.pdf";
        String cmpFileName = sourceFolder + "cmp_diffMasksOnSameImageXObject.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));

        ImageData sMask = ImageDataFactory.create(sourceFolder + "SMask3px.jpg");
        sMask.makeMask();

        ImageData hardMask = ImageDataFactory.create(sourceFolder + "hardMask.png");
        hardMask.makeMask();

        PdfImageXObject hardMaskXObject = new PdfImageXObject(hardMask);

        ImageData img = ImageDataFactory.create(sourceFolder + "sRGBImageBig.png");
        img.setImageMask(sMask);

        PdfImageXObject hardXObject = new PdfImageXObject(img, hardMaskXObject);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.addXObjectAt(hardXObject, 300, 500);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void imageResizedParentWithSoftMaskTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageResizedParentWithSoftMask.pdf";
        String cmpFileName = sourceFolder + "cmp_imageResizedParentWithSoftMask.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));

        ImageData mask = ImageDataFactory.create(sourceFolder + "SMask3px.jpg");
        mask.makeMask();

        ImageData img1 = ImageDataFactory.create(sourceFolder + "sRGBImageBig.png");
        img1.setImageMask(mask);

        ImageData img2 = ImageDataFactory.create(sourceFolder + "sRGBImage.png");
        img2.setImageMask(mask);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.addImageAt(img1, 30, 500, false);
        canvas.addImageAt(img2, 430, 500, false);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void imageWithSoftMaskMatteTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageWithSoftMaskMatte.pdf";
        String cmpFileName = sourceFolder + "cmp_imageWithSoftMaskMatte.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));

        ImageData mask = ImageDataFactory.create(sourceFolder + "matteMask.jpg");
        mask.makeMask();

        PdfImageXObject maskXObject = new PdfImageXObject(mask);
        maskXObject.put(new PdfName("Matte"), new PdfArray(new float[] {1, 1, 1}));

        ImageData img1 = ImageDataFactory.create(sourceFolder + "imageForMatteMask.jpg");

        PdfImageXObject xObject = new PdfImageXObject(img1, maskXObject);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage(PageSize.A4));
        canvas.addXObjectAt(xObject, 50, 500);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    // TODO: DEVSIX-4991
    public void sMaskMatteDifferentSizeOfImgTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "sMaskMatteDifferentSizeOfImg.pdf";
        String cmpFileName = sourceFolder + "cmp_sMaskMatteDifferentSizeOfImg.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));

        ImageData mask = ImageDataFactory.create(sourceFolder + "matteMask.jpg");
        mask.makeMask();

        PdfImageXObject maskXObject = new PdfImageXObject(mask);
        maskXObject.put(new PdfName("Matte"), new PdfArray(new float[] {1, 1, 1}));

        ImageData img1 = ImageDataFactory.create(sourceFolder + "resizedImageForMatteMask.jpg");

        PdfImageXObject xObject = new PdfImageXObject(img1, maskXObject);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage(PageSize.A4));
        canvas.addXObjectAt(xObject, 50, 500);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void imageWithInvalidMaskTest() throws IOException {
        ImageData mask = ImageDataFactory.create(sourceFolder + "mask.png");
        junitExpectedException.expect(com.itextpdf.io.IOException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format
                (com.itextpdf.io.IOException.ThisImageCanNotBeAnImageMask));
        mask.makeMask();
    }
}
