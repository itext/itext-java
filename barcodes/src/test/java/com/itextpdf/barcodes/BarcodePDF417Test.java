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
package com.itextpdf.barcodes;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.awt.Color;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class BarcodePDF417Test extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/barcodes/";
    public static final String destinationFolder = "./target/test/com/itextpdf/barcodes/BarcodePDF417/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void barcode01Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcode417_01.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(text);
        barcode.placeBarcode(canvas, null);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode02Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcode417_02.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfReader reader = new PdfReader(sourceFolder + "DocumentWithTrueTypeFont1.pdf");
        PdfDocument document = new PdfDocument(reader, writer);

        PdfCanvas canvas = new PdfCanvas(document.getLastPage());

        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(text);
        barcode.placeBarcode(canvas, null);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void macroPDF417Test01() throws IOException, InterruptedException {
        String filename = "barcode417Macro_01.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument pdfDocument = new PdfDocument(writer);

        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.addNewPage());

        pdfCanvas.addXObject(createMacroBarcodePart(pdfDocument, "This is PDF417 segment 0", 1, 1, 0), 1, 0, 0, 1, 36, 791);
        pdfCanvas.addXObject(createMacroBarcodePart(pdfDocument, "This is PDF417 segment 1", 1, 1, 1), 1, 0, 0, 1, 36, 676);

        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode417AspectRatioTest() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(text);
        barcode.setAspectRatio(10);
        barcode.placeBarcode(canvas, null);

        document.close();

        Assert.assertEquals(10, barcode.getAspectRatio(), 0);
    }

    @Test
    public void barcode417DefaultParamsTest() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setAspectRatio(10);
        barcode.setCode(text);
        barcode.setDefaultParameters();
        barcode.placeBarcode(canvas, null);

        document.close();

        Assert.assertEquals(0.5, barcode.getAspectRatio(), 0);
    }

    @Test
    public void barcode417CreateAWTImageTest() throws IOException, InterruptedException {
        String filename = "barcode417CreateAWTImageTest.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(text);

        Image image = barcode.createAwtImage(Color.MAGENTA, Color.ORANGE);
        ImageData imageData = ImageDataFactory.create(image, Color.BLACK);

        canvas.addImage(imageData, 10, 650, false);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename,
                sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    public void barcode417XObjectTest() throws IOException, InterruptedException {
        String filename = "barcode417XObjectTest.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(text);
        PdfFormXObject xObject = barcode.createFormXObject(document);

        canvas.addXObject(xObject, 10, 650);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename,
                sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    public void barcode417YHeightTest() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(text);
        barcode.setYHeight(15);
        barcode.placeBarcode(canvas, null);

        document.close();

        Assert.assertEquals(15, barcode.getYHeight(), 0);
    }

    @Test
    public void barcode417CodeReuseTest() throws IOException, InterruptedException {
        String filename = "barcode417CodeReuseTest.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfCanvas canvas = new PdfCanvas(document.addNewPage());

        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(text);
        barcode.placeBarcode(canvas, ColorConstants.BLUE);
        byte[] baos = barcode.getCode();

        BarcodePDF417 barcode2 = new BarcodePDF417();
        barcode2.setCode(baos);
        canvas = new PdfCanvas(document.addNewPage());
        barcode2.placeBarcode(canvas, ColorConstants.CYAN);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename,
                sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    public void barcode417NumbersTest() throws IOException, InterruptedException {
        String filename = "barcode417NumbersTest.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfCanvas canvas = new PdfCanvas(document.addNewPage());

        String numbers = "1234567890";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(numbers);
        barcode.placeBarcode(canvas, null);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename,
                sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    public void barcode417ByteLessThanSixSizeNumbersTest() throws IOException, InterruptedException {
        String filename = "barcode417ByteLessThanSixSizeNumbersTest.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfCanvas canvas = new PdfCanvas(document.addNewPage());

        byte[] numbers = {0, 10};

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(numbers);
        barcode.placeBarcode(canvas, ColorConstants.BLUE);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename,
                sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    public void barcode417ByteMoreThanSixSizeNumbersTest() throws IOException, InterruptedException {
        String filename = "barcode417ByteMoreThanSixSizeNumbersTest.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfCanvas canvas = new PdfCanvas(document.addNewPage());

        byte[] numbers = {0, 10, 11, 12, 13, 30, 50, 70};

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(numbers);
        barcode.placeBarcode(canvas, ColorConstants.BLUE);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename,
                sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    public void barcode417CodeRowsWithBarcodeGenerationTest() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCodeRows(150);
        barcode.placeBarcode(canvas, null);

        Assert.assertEquals(8, barcode.getCodeRows());
    }

    @Test
    public void barcode417CodeColumnsWithBarcodeGenerationTest() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCodeColumns(150);
        barcode.placeBarcode(canvas, null);

        Assert.assertEquals(1, barcode.getCodeColumns());
    }

    @Test
    public void barcode417CodeWordsWithBarcodeGenerationTest() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setLenCodewords(150);
        barcode.placeBarcode(canvas, null);

        Assert.assertEquals(8, barcode.getLenCodewords());
    }

    @Test
    public void barcode417ErrorLevelWithBarcodeGenerationTest() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setErrorLevel(3);
        barcode.placeBarcode(canvas, null);

        Assert.assertEquals(2, barcode.getErrorLevel());
    }

    @Test
    public void barcode417OptionsWithBarcodeGenerationTest() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setOptions(63);
        barcode.placeBarcode(canvas, null);

        Assert.assertEquals(63, barcode.getOptions());
    }

    @Test
    public void barcode417OptionsWithBarcodeGenerationInvalidSizeTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage("Invalid codeword size.");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setOptions(64);
        barcode.placeBarcode(canvas, null);

        Assert.assertEquals(64, barcode.getOptions());
    }

    private PdfFormXObject createMacroBarcodePart(PdfDocument document, String text, float mh, float mw, int segmentId) {
        BarcodePDF417 pf = new BarcodePDF417();

        // MacroPDF417 setup
        pf.setOptions(BarcodePDF417.PDF417_USE_MACRO);
        pf.setMacroFileId("12");
        pf.setMacroSegmentCount(2);
        pf.setMacroSegmentId(segmentId);

        pf.setCode(text);

        return pf.createFormXObject(ColorConstants.BLACK, mw, mh, document);
    }
}
