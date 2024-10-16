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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class ImageMasksTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/ImageMasksTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/canvas/ImageMasksTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void imageResizedParentWithHardMaskTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageResizedParentWithHardMask.pdf";
        String cmpFileName = sourceFolder + "cmp_imageResizedParentWithHardMask.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));

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

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    // TODO: DEVSIX-4992
    public void diffMasksOnSameImageXObjectTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "diffMasksOnSameImageXObject.pdf";
        String cmpFileName = sourceFolder + "cmp_diffMasksOnSameImageXObject.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));

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

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void imageResizedParentWithSoftMaskTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageResizedParentWithSoftMask.pdf";
        String cmpFileName = sourceFolder + "cmp_imageResizedParentWithSoftMask.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));

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

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void imageWithSoftMaskMatteTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageWithSoftMaskMatte.pdf";
        String cmpFileName = sourceFolder + "cmp_imageWithSoftMaskMatte.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));

        ImageData mask = ImageDataFactory.create(sourceFolder + "matteMask.jpg");
        mask.makeMask();

        PdfImageXObject maskXObject = new PdfImageXObject(mask);
        maskXObject.put(new PdfName("Matte"), new PdfArray(new float[] {1, 1, 1}));

        ImageData img1 = ImageDataFactory.create(sourceFolder + "imageForMatteMask.jpg");

        PdfImageXObject xObject = new PdfImageXObject(img1, maskXObject);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage(PageSize.A4));
        canvas.addXObjectAt(xObject, 50, 500);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    // TODO: DEVSIX-4991
    public void sMaskMatteDifferentSizeOfImgTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "sMaskMatteDifferentSizeOfImg.pdf";
        String cmpFileName = sourceFolder + "cmp_sMaskMatteDifferentSizeOfImg.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));

        ImageData mask = ImageDataFactory.create(sourceFolder + "matteMask.jpg");
        mask.makeMask();

        PdfImageXObject maskXObject = new PdfImageXObject(mask);
        maskXObject.put(new PdfName("Matte"), new PdfArray(new float[] {1, 1, 1}));

        ImageData img1 = ImageDataFactory.create(sourceFolder + "resizedImageForMatteMask.jpg");

        PdfImageXObject xObject = new PdfImageXObject(img1, maskXObject);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage(PageSize.A4));
        canvas.addXObjectAt(xObject, 50, 500);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void imageWithInvalidMaskTest() throws IOException {
        ImageData mask = ImageDataFactory.create(sourceFolder + "mask.png");

        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> mask.makeMask()
        );
        Assertions.assertEquals(MessageFormatUtil.format(IoExceptionMessageConstant.THIS_IMAGE_CAN_NOT_BE_AN_IMAGE_MASK), e.getMessage());
    }
}
