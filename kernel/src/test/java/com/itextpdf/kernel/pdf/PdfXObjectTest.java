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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Tag("IntegrationTest")
public class PdfXObjectTest extends ExtendedITextTest{
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfXObjectTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfXObjectTest/";

    public static final String[] images = new String[]{SOURCE_FOLDER + "WP_20140410_001.bmp",
            SOURCE_FOLDER + "WP_20140410_001.JPC",
            SOURCE_FOLDER + "WP_20140410_001.jpg",
            SOURCE_FOLDER + "WP_20140410_001.tif"};


    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void createDocumentFromImages1() throws IOException,  InterruptedException {
        final String destinationDocument = DESTINATION_FOLDER + "documentFromImages1.pdf";
        PdfWriter writer = new PdfWriter(destinationDocument);
        PdfDocument document = new PdfDocument(writer);
        PdfImageXObject[] images = new PdfImageXObject[4];
        for (int i = 0; i < 4; i++) {
            images[i] = new PdfImageXObject(ImageDataFactory.create(PdfXObjectTest.images[i]));
            images[i].setLayer(new PdfLayer("layer" + i, document));
            if (i % 2 == 0)
                images[i].flush();
        }
        for (int i = 0; i < 4; i++) {
            PdfPage page = document.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.addXObjectFittedIntoRectangle(images[i], PageSize.DEFAULT);
            page.flush();
        }
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectFittedIntoRectangle(images[0], new Rectangle(0, 0, 200, 112.35f));
        canvas.addXObjectFittedIntoRectangle(images[1], new Rectangle(300, 0, 200, 112.35f));
        canvas.addXObjectFittedIntoRectangle(images[2], new Rectangle(0, 300, 200, 112.35f));
        canvas.addXObjectFittedIntoRectangle(images[3], new Rectangle(300, 300, 200, 112.35f));
        canvas.release();
        page.flush();
        document.close();

        Assertions.assertTrue(new File(destinationDocument).length() < 20 * 1024 * 1024);
        Assertions.assertNull(new CompareTool().compareByContent(destinationDocument, SOURCE_FOLDER + "cmp_documentFromImages1.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.IMAGE_SIZE_CANNOT_BE_MORE_4KB)
    })
    public void createDocumentFromImages2() throws IOException,  InterruptedException {
        final String destinationDocument = DESTINATION_FOLDER + "documentFromImages2.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationDocument);
        PdfDocument document = new PdfDocument(writer);

        ImageData image = ImageDataFactory.create(SOURCE_FOLDER + "itext.jpg");
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addImageFittedIntoRectangle(image, new Rectangle(50, 500, 100, 14.16f), true);
        canvas.addImageFittedIntoRectangle(image, new Rectangle(200, 500, 100, 14.16f), false).flush();
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationDocument, SOURCE_FOLDER + "cmp_documentFromImages2.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void createDocumentWithForms() throws IOException,  InterruptedException {
        final String destinationDocument = DESTINATION_FOLDER + "documentWithForms1.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationDocument);
        PdfDocument document = new PdfDocument(writer);

        //Create form XObject and flush to document.
        PdfFormXObject form = new PdfFormXObject(new Rectangle(0, 0, 50, 50));
        PdfCanvas canvas = new PdfCanvas(form, document);
        canvas.rectangle(10, 10, 30, 30);
        canvas.fill();
        canvas.release();
        form.flush();

        //Create page1 and add forms to the page.
        PdfPage page1 = document.addNewPage();
        canvas = new PdfCanvas(page1);
        canvas.addXObjectAt(form, 0, 0).addXObjectAt(form, 50, 0).addXObjectAt(form, 0, 50).addXObjectAt(form, 50, 50);
        canvas.release();

        //Create form from the page1 and flush it.
        form = new PdfFormXObject(page1);
        form.flush();

        //Now page1 can be flushed. It's not needed anymore.
        page1.flush();

        //Create page2 and add forms to the page.
        PdfPage page2 = document.addNewPage();
        canvas = new PdfCanvas(page2);
        canvas.addXObjectAt(form, 0, 0);
        canvas.addXObjectAt(form, 0, 200);
        canvas.addXObjectAt(form, 200, 0);
        canvas.addXObjectAt(form, 200, 200);
        canvas.release();
        page2.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationDocument, SOURCE_FOLDER + "cmp_documentWithForms1.pdf",
                DESTINATION_FOLDER, "diff_"));

    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY))
    public void xObjectIterativeReference() throws IOException {

        // The input file contains circular references chain, see: 8 0 R -> 10 0 R -> 4 0 R -> 8 0 R.
        // Copying of such file even with smart mode is expected to be handled correctly.
        String src = SOURCE_FOLDER + "checkboxes_XObject_iterative_reference.pdf";
        String dest = DESTINATION_FOLDER + "checkboxes_XObject_iterative_reference_out.pdf";

        PdfDocument pdf = new PdfDocument(CompareTool.createTestPdfWriter(dest).setSmartMode(true));
        PdfReader pdfReader = new PdfReader(src);
        PdfDocument sourceDocumentPdf = new PdfDocument(pdfReader);
        sourceDocumentPdf.copyPagesTo(1, sourceDocumentPdf.getNumberOfPages(), pdf);

        //map <object pdf, count>
        HashMap<String, Integer> mapIn = new HashMap<>();
        HashMap<String, Integer> mapOut = new HashMap<>();

        //map <object pdf, list of object id referenceing that podf object>
        HashMap<String, List<Integer>> mapOutId = new HashMap<>();

        PdfObject obj;

        //create helpful data structures from pdf output
        for (int i = 1; i < pdf.getNumberOfPdfObjects(); i++) {
            obj = pdf.getPdfObject(i);
            String objString = obj.toString();
            Integer count = mapOut.get(objString);
            List<Integer> list;

            if (count == null) {
                count = 1;
                list = new ArrayList<Integer>();
                list.add(i);
            } else {
                count++;
                list = mapOutId.get(objString);
            }

            mapOut.put(objString, count);
            mapOutId.put(objString, list);
        }

        //create helpful data structures from pdf input
        for (int i = 1; i < sourceDocumentPdf.getNumberOfPdfObjects(); i++) {
            obj = sourceDocumentPdf.getPdfObject(i);
            String objString = obj.toString();
            Integer count = mapIn.get(objString);

            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            mapIn.put(objString, count);
        }

        pdf.close();

        //the following object is copied and reused. it appears 6 times in the original pdf file. just once in the output file
        String case1 = "<</BBox [0 0 20 20 ] /Filter /FlateDecode /FormType 1 /Length 12 /Matrix [1 0 0 1 0 0 ] /Resources <<>> /Subtype /Form /Type /XObject >>";
        Integer countOut1 = mapOut.get(case1);
        Integer countIn1 = mapIn.get(case1);
        Assertions.assertTrue(countOut1.equals(1) && countIn1.equals(6));

        //the following object appears 1 time in the original pdf file and just once in the output file
        String case2 = "<</BaseFont /ZapfDingbats /Subtype /Type1 /Type /Font >>";
        Integer countOut2 = mapOut.get(case2);
        Integer countIn2 = mapIn.get(case2);
        Assertions.assertTrue(countOut2.equals(countIn2) && countOut2.equals(1));

        //from the original pdf the object "<</BBox [0 0 20 20 ] /Filter /FlateDecode /FormType 1 /Length 70 /Matrix [1 0 0 1 0 0 ] /Resources <</Font <</ZaDb 2 0 R >> >> /Subtype /Form /Type /XObject >>";
        //is going to be found changed in the output pdf referencing the referenced object with another id which is retrieved through the hashmap
        String case3 = "<</BaseFont /ZapfDingbats /Subtype /Type1 /Type /Font >>";
        Integer countIdIn = mapOutId.get(case3).get(0);
        //EXPECTED to be as the original but with different referenced object and marked as modified
        String expected = "<</BBox [0 0 20 20 ] /Filter /FlateDecode /FormType 1 /Length 70 /Matrix [1 0 0 1 0 0 ] /Resources <</Font <</ZaDb " + countIdIn + " 0 R Modified; >> >> /Subtype /Form /Type /XObject >>";
        Assertions.assertTrue(mapOut.get(expected).equals(1));
    }

    @Test
    public void calculateProportionallyFitRectangleWithWidthTest() throws IOException,  InterruptedException {
        final String fileName = "calculateProportionallyFitRectangleWithWidthTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        formXObject.put(PdfName.Matrix, new PdfArray(new float[] {1, 0.57f, 0, 2, 20, 5}));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itext.png"));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle rect = PdfXObject.calculateProportionallyFitRectangleWithWidth(formXObject, 0, 0, 20);
        canvas.addXObjectFittedIntoRectangle(formXObject, rect);
        rect = PdfXObject.calculateProportionallyFitRectangleWithWidth(imageXObject, 20, 0, 20);
        canvas.addXObjectFittedIntoRectangle(imageXObject, rect);

        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @Tag("UnitTest")
    public void calculateProportionallyFitRectangleWithWidthForCustomXObjectTest() {
        PdfXObject pdfXObject = new CustomPdfXObject(new PdfStream());

        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> PdfXObject.calculateProportionallyFitRectangleWithWidth(pdfXObject, 0, 0, 20)
        );
        Assertions.assertEquals("PdfFormXObject or PdfImageXObject expected.", e.getMessage());
    }

    @Test
    public void calculateProportionallyFitRectangleWithHeightTest() throws IOException,  InterruptedException {
        final String fileName = "calculateProportionallyFitRectangleWithHeightTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        formXObject.put(PdfName.Matrix, new PdfArray(new float[] {1, 0.57f, 0, 2, 20, 5}));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itext.png"));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle rect = PdfXObject.calculateProportionallyFitRectangleWithHeight(formXObject, 0, 0, 20);
        canvas.addXObjectFittedIntoRectangle(formXObject, rect);
        rect = PdfXObject.calculateProportionallyFitRectangleWithHeight(imageXObject, 20, 0, 20);
        canvas.addXObjectFittedIntoRectangle(imageXObject, rect);

        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @Tag("UnitTest")
    public void calculateProportionallyFitRectangleWithHeightForCustomXObjectTest() {
        PdfXObject pdfXObject = new CustomPdfXObject(new PdfStream());

        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> PdfXObject.calculateProportionallyFitRectangleWithHeight(pdfXObject, 0, 0, 20)
        );
        Assertions.assertEquals("PdfFormXObject or PdfImageXObject expected.", e.getMessage());
    }

    private static class CustomPdfXObject extends PdfXObject {
        protected CustomPdfXObject(PdfStream pdfObject) {
            super(pdfObject);
        }
    }
}
