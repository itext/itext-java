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
package com.itextpdf.barcodes;

import com.itextpdf.barcodes.exceptions.BarcodesExceptionMessageConstant;
import com.itextpdf.io.codec.CCITTG4Encoder;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.image.RawImageData;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class BarcodePDF417Test extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/barcodes/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/barcodes/BarcodePDF417/";
    
    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void barcode01Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcode417_01.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
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

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void barcode02Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcode417_02.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "DocumentWithTrueTypeFont1.pdf");
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

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void macroPDF417Test01() throws IOException, InterruptedException {
        String filename = "barcode417Macro_01.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument pdfDocument = new PdfDocument(writer);

        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.addNewPage());

        pdfCanvas.addXObjectWithTransformationMatrix(createMacroBarcodePart(pdfDocument, "This is PDF417 segment 0", 1, 1, 0), 1, 0, 0, 1, 36, 791);
        pdfCanvas.addXObjectWithTransformationMatrix(createMacroBarcodePart(pdfDocument, "This is PDF417 segment 1", 1, 1, 1), 1, 0, 0, 1, 36, 676);

        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_"));
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

        Assertions.assertEquals(10, barcode.getAspectRatio(), 0);
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

        Assertions.assertEquals(0.5, barcode.getAspectRatio(), 0);
    }

    // Android-Conversion-Skip-Block-Start (java.awt library isn't available on Android)
    @Test
    public void barcode417CreateAWTImageTest() throws IOException, InterruptedException {
        String filename = "barcode417CreateAWTImageTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(text);

        java.awt.Image image = barcode.createAwtImage(java.awt.Color.MAGENTA, java.awt.Color.ORANGE);
        ImageData imageData = ImageDataFactory.create(image, java.awt.Color.BLACK);

        canvas.addImageAt(imageData, 10, 650, false);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename,
                SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER));
    }

    @Test
    public void barcode417XObjectTest() throws IOException, InterruptedException {
        String filename = "barcode417XObjectTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
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

        canvas.addXObjectAt(xObject, 10, 650);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename,
                SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER));
    }
    // Android-Conversion-Skip-Block-End

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

        Assertions.assertEquals(15, barcode.getYHeight(), 0);
    }

    @Test
    public void barcode417CodeReuseTest() throws IOException, InterruptedException {
        String filename = "barcode417CodeReuseTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
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

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename,
                SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER));
    }

    @Test
    public void barcode417NumbersTest() throws IOException, InterruptedException {
        String filename = "barcode417NumbersTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfCanvas canvas = new PdfCanvas(document.addNewPage());

        String numbers = "1234567890";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(numbers);
        barcode.placeBarcode(canvas, null);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename,
                SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER));
    }

    @Test
    public void barcode417ByteLessThanSixSizeNumbersTest() throws IOException, InterruptedException {
        String filename = "barcode417ByteLessThanSixSizeNumbersTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfCanvas canvas = new PdfCanvas(document.addNewPage());

        byte[] numbers = {0, 10};

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(numbers);
        barcode.placeBarcode(canvas, ColorConstants.BLUE);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename,
                SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER));
    }

    @Test
    public void barcode417ByteMoreThanSixSizeNumbersTest() throws IOException, InterruptedException {
        String filename = "barcode417ByteMoreThanSixSizeNumbersTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfCanvas canvas = new PdfCanvas(document.addNewPage());

        byte[] numbers = {0, 10, 11, 12, 13, 30, 50, 70};

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(numbers);
        barcode.placeBarcode(canvas, ColorConstants.BLUE);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename,
                SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER));
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

        Assertions.assertEquals(8, barcode.getCodeRows());
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

        Assertions.assertEquals(1, barcode.getCodeColumns());
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

        Assertions.assertEquals(8, barcode.getLenCodewords());
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

        Assertions.assertEquals(2, barcode.getErrorLevel());
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

        Assertions.assertEquals(63, barcode.getOptions());
    }

    @Test
    public void barcode417OptionsWithBarcodeGenerationInvalidSizeTest() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setOptions(64);

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> barcode.placeBarcode(canvas, null)
        );
        Assertions.assertEquals("Invalid codeword size.", e.getMessage());
        Assertions.assertEquals(64, barcode.getOptions());
    }

    @Test
    public void lenCodewordsIsNotEnoughTest() {
        BarcodePDF417 barcodePDF417 = new BarcodePDF417();
        barcodePDF417.setOptions(BarcodePDF417.PDF417_USE_RAW_CODEWORDS);
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> barcodePDF417.paintCode());
        Assertions.assertEquals(BarcodesExceptionMessageConstant.INVALID_CODEWORD_SIZE, exception.getMessage());
    }

    @Test
    public void lenCodewordsIsTooSmallTest() {
        BarcodePDF417 barcodePDF417 = new BarcodePDF417();
        barcodePDF417.setOptions(BarcodePDF417.PDF417_USE_RAW_CODEWORDS);
        // lenCodeWords should be bigger than 1
        barcodePDF417.setLenCodewords(0);
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> barcodePDF417.paintCode());
        Assertions.assertEquals(BarcodesExceptionMessageConstant.INVALID_CODEWORD_SIZE, exception.getMessage());
    }

    @Test
    public void lenCodewordsMoreThanMaxDataCodewordsTest() {
        BarcodePDF417 barcodePDF417 = new BarcodePDF417();
        barcodePDF417.setOptions(BarcodePDF417.PDF417_USE_RAW_CODEWORDS);
        // lenCodeWords should be smaller than MAX_DATA_CODEWORDS
        barcodePDF417.setLenCodewords(927);
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> barcodePDF417.paintCode());
        Assertions.assertEquals(BarcodesExceptionMessageConstant.INVALID_CODEWORD_SIZE, exception.getMessage());
    }

    @Test
    public void ccittImageFromBarcodeTest() throws IOException, InterruptedException {
        String filename = "ccittImage01.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";

        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(text);
        barcode.paintCode();

        byte g4[] = CCITTG4Encoder.compress(barcode.getOutBits(), barcode.getBitColumns(), barcode.getCodeRows());
        ImageData img = ImageDataFactory.create(barcode.getBitColumns(), barcode.getCodeRows(), false,
                RawImageData.CCITTG4, 0, g4, null);

        canvas.addImageAt(img, 100, 100, false);

        document.close();

        Assertions.assertNull(
                new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                        DESTINATION_FOLDER, "diff_"));
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
