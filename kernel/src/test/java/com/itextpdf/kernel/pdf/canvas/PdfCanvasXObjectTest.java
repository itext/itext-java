/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfCanvasXObjectTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/PdfCanvasXObjectTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/canvas/PdfCanvasXObjectTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    // addXObjectAt(PdfXObject, float, float) test block

    @Test
    public void addFormXObjectXYWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(10, 15, 10, 20));
        new PdfCanvas(formXObject, document).rectangle(10, 10, 10, 20).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectAt(formXObject, 5, 2.5f);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(10, 10, 10, 20));
        formXObject.put(PdfName.Matrix, new PdfArray(new float[] {1, 1, 0, 1.5f, 35, -10}));
        new PdfCanvas(formXObject, document).rectangle(10, 10, 10, 20).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectAt(formXObject, 5, 0);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addImageXObjectAtTest() throws IOException,  InterruptedException {
        final String fileName = "addImageXObjectAtTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "box.png"));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectAt(imageXObject, 30, 10);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @Tag("UnitTest")
    public void addCustomXObjectAtTest() {
        PdfXObject pdfXObject = new CustomPdfXObject(new PdfStream());
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfCanvas canvas = new PdfCanvas(document.addNewPage());
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> canvas.addXObjectAt(pdfXObject, 0, 0)
        );
        Assertions.assertEquals("PdfFormXObject or PdfImageXObject expected.", e.getMessage());
    }

    // addXObjectFittedIntoRectangle(PdfXObject, Rectangle) test block (use PdfXObject#calculateProportionallyFitRectangleWithWidth)

    @Test
    public void addFormXObjectXYWidthLessOneWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYWidthWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(10, 15, 10, 20));
        new PdfCanvas(formXObject, document).rectangle(10, 10, 10, 20).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle rect = PdfXObject.calculateProportionallyFitRectangleWithWidth(formXObject, 5, 2.5f, 5);
        canvas.addXObjectFittedIntoRectangle(formXObject, rect);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYWidthLargerOneWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYWidthLargerOneWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(10, 15, 10, 20));
        new PdfCanvas(formXObject, document).rectangle(10, 10, 10, 20).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle rect = PdfXObject.calculateProportionallyFitRectangleWithWidth(formXObject, 5, 5, 30);
        canvas.addXObjectFittedIntoRectangle(formXObject, rect);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYWidthLessOneWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYWidthLessOneWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(10, 15, 10, 20));
        formXObject.put(PdfName.Matrix, new PdfArray(new float[] {1, 0, 0.57f, 1, 20, 5}));
        new PdfCanvas(formXObject, document).rectangle(10, 10, 10, 20).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle rect = PdfXObject.calculateProportionallyFitRectangleWithWidth(formXObject, 2.5f, 2.5f, 5);
        canvas.addXObjectFittedIntoRectangle(formXObject, rect);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYWidthLargerOneWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYWidthLargerOneWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(10, 15, 10, 20));
        formXObject.put(PdfName.Matrix, new PdfArray(new float[] {1, 0.57f, 0.57f, 1, 20, 5}));
        new PdfCanvas(formXObject, document).rectangle(10, 10, 10, 20).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle rect = PdfXObject.calculateProportionallyFitRectangleWithWidth(formXObject, 2.5f, 0, 30);
        canvas.addXObjectFittedIntoRectangle(formXObject, rect);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    // addXObjectFittedIntoRectangle(PdfXObject, Rectangle) test block (use PdfXObject#calculateProportionallyFitRectangleWithHeight)

    @Test
    public void addFormXObjectXYHeightLessOneWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYHeightLessOneWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle rect = PdfXObject.calculateProportionallyFitRectangleWithHeight(formXObject, 5, 2.5f, 10);
        canvas.addXObjectFittedIntoRectangle(formXObject, rect);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYHeightLargerOneWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYHeightLargerOneWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle rect = PdfXObject.calculateProportionallyFitRectangleWithHeight(formXObject, 0, 0, 30);
        canvas.addXObjectFittedIntoRectangle(formXObject, rect);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYHeightLessOneWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYHeightLessOneWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        formXObject.put(PdfName.Matrix, new PdfArray(new float[] {2, 0.57f, 0.57f, 1, 20, 5}));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle rect = PdfXObject.calculateProportionallyFitRectangleWithHeight(formXObject, 2.5f, 2.5f, 10);
        canvas.addXObjectFittedIntoRectangle(formXObject, rect);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYHeightLargerOneWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYHeightLargerOneWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        formXObject.put(PdfName.Matrix, new PdfArray(new float[] {3, 0.2f, 0, 1, 20, 5}));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle rect = PdfXObject.calculateProportionallyFitRectangleWithHeight(formXObject, 2.5f, 0, 30);
        canvas.addXObjectFittedIntoRectangle(formXObject, rect);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    // addXObjectFittedIntoRectangle(PdfXObject, Rectangle) test block

    @Test
    public void addFormXObjectRectangleLessWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectRectangleLessWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectFittedIntoRectangle(formXObject, new Rectangle(0, 2.5f, 5, 10));
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectRectangleLargerWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectRectangleLargerWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectFittedIntoRectangle(formXObject, new Rectangle(10, 5, 40, 20));
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectRectangleLessWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectRectangleLessWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        formXObject.put(PdfName.Matrix, new PdfArray(new float[] {1, 0.57f, 0, 1, 20, 5}));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectFittedIntoRectangle(formXObject, new Rectangle(2.5f, 2.5f, 10, 5));
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectRectangleLargerWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectRectangleLargerWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        formXObject.put(PdfName.Matrix, new PdfArray(new float[] {2, 0, 0.3f, 3, 20, 5}));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectFittedIntoRectangle(formXObject, new Rectangle(5, 0, 30, 30));
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @Tag("UnitTest")
    public void addCustomXObjectFittedIntoRectangleTest() {
        PdfXObject pdfXObject = new CustomPdfXObject(new PdfStream());
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfCanvas pdfCanvas = new PdfCanvas(document.addNewPage());
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> pdfCanvas.addXObjectFittedIntoRectangle(pdfXObject, new Rectangle(0, 0, 0, 0))
        );
        Assertions.assertEquals("PdfFormXObject or PdfImageXObject expected.", e.getMessage());
    }

    // addXObject(PdfXObject) test block

    @Test
    public void addFormXObjectWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObject(formXObject);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        formXObject.put(PdfName.Matrix, new PdfArray(new float[] {1, 0.57f, 0, 2, 20, 5}));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObject(formXObject);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addImageXObjectTest() throws IOException,  InterruptedException {
        final String fileName = "addImageXObjectTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "box.png"));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObject(imageXObject);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    // addXObjectWithTransformationMatrix(PdfXObject, float, float, float, float, float, float) test block

    @Test
    public void addFormXObjectWithTransformationMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addFormXObjectWithTransformationMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectWithTransformationMatrix(formXObject, 8, 0, 0, 1, 0, 0);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addImageXObjectWithTransformationMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addImageXObjectWithTransformationMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "box.png"));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectWithTransformationMatrix(imageXObject, 20, 0 , 0, 40, 0, 0);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @Tag("UnitTest")
    public void addCustomXObjectTest() {
        PdfXObject pdfXObject = new CustomPdfXObject(new PdfStream());
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfCanvas canvas = new PdfCanvas(document.addNewPage());
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> canvas.addXObject(pdfXObject)
        );
        Assertions.assertEquals("PdfFormXObject or PdfImageXObject expected.", e.getMessage());
    }

    private static class CustomPdfXObject extends PdfXObject {
        protected CustomPdfXObject(PdfStream pdfObject) {
            super(pdfObject);
        }
    }

    // Adds PdfFormXObject with matrix close to the identity matrix tests block


    @Test
    public void addFormXObjectWithUserIdentityMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addFormXObjectWithUserIdentityMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(0, 0, 20, 20));
        new PdfCanvas(formXObject, document).circle(10, 10, 10).fill();

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        // It should be written because it is user matrix
        canvas.addXObjectWithTransformationMatrix(formXObject, 1.00011f, 0, 0, 1, 0, 0);
        canvas.release();
        page.flush();

        page = document.addNewPage();
        canvas = new PdfCanvas(page);
        // It should be written because it is user matrix
        canvas.addXObjectWithTransformationMatrix(formXObject, 1.00009f, 0, 0, 1, 0, 0);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectWithIdentityMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addFormXObjectWithIdentityMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(destPdf);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(0, 0, 20, 20));
        new PdfCanvas(formXObject, document).circle(10, 10, 10).fill();

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        // It should be written because it is larger then PdfCanvas#IDENTITY_MATRIX_EPS
        canvas.addXObjectAt(formXObject, 0.00011f, 0);
        canvas.release();
        page.flush();

        page = document.addNewPage();
        canvas = new PdfCanvas(page);
        // It shouldn't be written because it is less then PdfCanvas#IDENTITY_MATRIX_EPS
        canvas.addXObjectAt(formXObject, 0.00009f, 0);
        canvas.release();
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }
}
