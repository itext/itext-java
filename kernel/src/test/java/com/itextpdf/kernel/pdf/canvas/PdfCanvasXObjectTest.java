/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfCanvasXObjectTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/PdfCanvasXObjectTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/canvas/PdfCanvasXObjectTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    // addXObjectAt(PdfXObject, float, float) test block

    @Test
    public void addFormXObjectXYWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(10, 15, 10, 20));
        new PdfCanvas(formXObject, document).rectangle(10, 10, 10, 20).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectAt(formXObject, 5, 2.5f);
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addImageXObjectAtTest() throws IOException,  InterruptedException {
        final String fileName = "addImageXObjectAtTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "box.png"));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectAt(imageXObject, 30, 10);
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @Category(UnitTest.class)
    public void addCustomXObjectAtTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage("PdfFormXObject or PdfImageXObject expected.");
        PdfXObject pdfXObject = new CustomPdfXObject(new PdfStream());
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        new PdfCanvas(document.addNewPage()).addXObjectAt(pdfXObject, 0, 0);
    }

    // addXObjectFittedIntoRectangle(PdfXObject, Rectangle) test block (use PdfXObject#calculateProportionallyFitRectangleWithWidth)

    @Test
    public void addFormXObjectXYWidthLessOneWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYWidthWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYWidthLargerOneWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYWidthLargerOneWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYWidthLessOneWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYWidthLessOneWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYWidthLargerOneWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYWidthLargerOneWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    // addXObjectFittedIntoRectangle(PdfXObject, Rectangle) test block (use PdfXObject#calculateProportionallyFitRectangleWithHeight)

    @Test
    public void addFormXObjectXYHeightLessOneWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYHeightLessOneWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYHeightLargerOneWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYHeightLargerOneWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYHeightLessOneWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYHeightLessOneWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectXYHeightLargerOneWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectXYHeightLargerOneWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    // addXObjectFittedIntoRectangle(PdfXObject, Rectangle) test block

    @Test
    public void addFormXObjectRectangleLessWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectRectangleLessWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectFittedIntoRectangle(formXObject, new Rectangle(0, 2.5f, 5, 10));
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectRectangleLargerWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectRectangleLargerWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectFittedIntoRectangle(formXObject, new Rectangle(10, 5, 40, 20));
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectRectangleLessWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectRectangleLessWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectRectangleLargerWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectRectangleLargerWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @Category(UnitTest.class)
    public void addCustomXObjectFittedIntoRectangleTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage("PdfFormXObject or PdfImageXObject expected.");
        PdfXObject pdfXObject = new CustomPdfXObject(new PdfStream());
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        new PdfCanvas(document.addNewPage()).addXObjectFittedIntoRectangle(pdfXObject, new Rectangle(0, 0, 0, 0));
    }

    // addXObject(PdfXObject) test block

    @Test
    public void addFormXObjectWithoutMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectWithoutMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObject(formXObject);
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectWithMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addXObjectWithMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addImageXObjectTest() throws IOException,  InterruptedException {
        final String fileName = "addImageXObjectTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "box.png"));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObject(imageXObject);
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    // addXObjectWithTransformationMatrix(PdfXObject, float, float, float, float, float, float) test block

    @Test
    public void addFormXObjectWithTransformationMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addFormXObjectWithTransformationMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(5, 5, 15, 20));
        new PdfCanvas(formXObject, document).circle(10, 15, 10).fill();
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectWithTransformationMatrix(formXObject, 8, 0, 0, 1, 0, 0);
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addImageXObjectWithTransformationMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addImageXObjectWithTransformationMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "box.png"));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObjectWithTransformationMatrix(imageXObject, 20, 0 , 0, 40, 0, 0);
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @Category(UnitTest.class)
    public void addCustomXObjectTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage("PdfFormXObject or PdfImageXObject expected.");
        PdfXObject pdfXObject = new CustomPdfXObject(new PdfStream());
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        new PdfCanvas(document.addNewPage()).addXObject(pdfXObject);
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
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addFormXObjectWithIdentityMatrixTest() throws IOException,  InterruptedException {
        final String fileName = "addFormXObjectWithIdentityMatrixTest.pdf";
        final String destPdf = DESTINATION_FOLDER + fileName;
        final String cmpPdf = SOURCE_FOLDER + "cmp_" + fileName;
        FileOutputStream fos = new FileOutputStream(destPdf);
        PdfWriter writer = new PdfWriter(fos);
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

        Assert.assertNull(new CompareTool().compareByContent(destPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }
}
