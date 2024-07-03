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


import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class BarcodeDataMatrixTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/barcodes/BarcodeDataMatrix/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/barcodes/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    public void barcode01Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeDataMatrix.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        BarcodeDataMatrix barcode = new BarcodeDataMatrix();
        barcode.setCode("AAAAAAAAAA;BBBBAAAA3;00028;BBBAA05;AAAA;AAAAAA;1234567;AQWXSZ;JEAN;;;;7894561;AQWXSZ;GEO;;;;1;1;1;1;0;0;1;0;1;0;0;0;1;0;1;0;0;0;0;0;0;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1");
        barcode.placeBarcode(canvas, ColorConstants.GREEN, 5);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode02Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeDataMatrix2.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        BarcodeDataMatrix barcode2 = new BarcodeDataMatrix("дима", "UTF-8");
        barcode2.placeBarcode(canvas, ColorConstants.GREEN, 10);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode03Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeDataMatrix3.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        BarcodeDataMatrix barcode3 = new BarcodeDataMatrix();
        barcode3.setWidth(36);
        barcode3.setHeight(12);
        barcode3.setCode("AbcdFFghijklmnopqrstuWXSQ");
        barcode3.placeBarcode(canvas, ColorConstants.BLACK, 10);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode04Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeDataMatrix4.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        BarcodeDataMatrix barcode3 = new BarcodeDataMatrix();
        barcode3.setWidth(36);
        barcode3.setHeight(12);
        barcode3.setCode("01AbcdefgAbcdefg123451231231234");
        barcode3.placeBarcode(canvas, ColorConstants.BLACK, 10);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode05Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeDataMatrix5.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        BarcodeDataMatrix barcode3 = new BarcodeDataMatrix();
        barcode3.setWidth(40);
        barcode3.setHeight(40);
        barcode3.setCode("aaabbbcccdddAAABBBAAABBaaabbbcccdddaaa");
        barcode3.placeBarcode(canvas, ColorConstants.BLACK, 10);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode06Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeDataMatrix6.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        BarcodeDataMatrix barcode3 = new BarcodeDataMatrix();
        barcode3.setWidth(36);
        barcode3.setHeight(12);
        barcode3.setCode(">>>\r>>>THIS VERY TEXT>>\r>");
        barcode3.placeBarcode(canvas, ColorConstants.BLACK, 10);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode07Test() {
        BarcodeDataMatrix bc = new BarcodeDataMatrix();
        bc.setOptions(BarcodeDataMatrix.DM_AUTO);
        bc.setWidth(10);
        bc.setHeight(10);

        String aCode = "aBCdeFG12";

        int result = bc.setCode(aCode);

        Assertions.assertEquals(result, BarcodeDataMatrix.DM_ERROR_TEXT_TOO_BIG);
    }

    @Test
    public void barcode08Test() {
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(18);
        barcodeDataMatrix.setHeight(18);
        int result = barcodeDataMatrix.setCode("AbcdFFghijklmnopqrstuWXSQ");

        Assertions.assertEquals(BarcodeDataMatrix.DM_ERROR_TEXT_TOO_BIG, result);
    }

    @Test
    public void barcode09Test() {
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(17);
        barcodeDataMatrix.setHeight(17);
        int result = barcodeDataMatrix.setCode("AbcdFFghijklmnopqrstuWXSQ");

        Assertions.assertEquals(BarcodeDataMatrix.DM_ERROR_INVALID_SQUARE, result);
    }

    @Test
    public void barcode10Test() {
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(26);
        barcodeDataMatrix.setHeight(12);
        int result = barcodeDataMatrix.setCode("AbcdFFghijklmnopqrstuWXSQ");

        Assertions.assertEquals(BarcodeDataMatrix.DM_ERROR_TEXT_TOO_BIG, result);
    }

    @Test
    public void barcode11Test() {
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(18);
        barcodeDataMatrix.setHeight(18);
        byte[] str = "AbcdFFghijklmnop".getBytes();
        int result = barcodeDataMatrix.setCode(str, 0, str.length);

        Assertions.assertEquals(BarcodeDataMatrix.DM_NO_ERROR, result);
    }

    @Test
    public void barcode12Test() {
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(18);
        barcodeDataMatrix.setHeight(18);
        byte[] str = "AbcdFFghijklmnop".getBytes();

        Exception e = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> barcodeDataMatrix.setCode(str, -1, str.length));
    }

    @Test
    public void barcode13Test() {
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(18);
        barcodeDataMatrix.setHeight(18);
        byte[] str = "AbcdFFghijklmnop".getBytes();

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> barcodeDataMatrix.setCode(str, 0, str.length + 1));
    }

    @Test
    public void barcode14Test() {
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(18);
        barcodeDataMatrix.setHeight(18);
        byte[] str = "AbcdFFghijklmnop".getBytes();

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> barcodeDataMatrix.setCode(str, 0, -1));
    }

    @Test
    public void barcode15Test() {
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(18);
        barcodeDataMatrix.setHeight(18);
        byte[] str = "AbcdFFghijklmnop".getBytes();
        int result = barcodeDataMatrix.setCode(str, str.length, 0);

        Assertions.assertEquals(BarcodeDataMatrix.DM_NO_ERROR, result);
    }

    @Test
    public void barcode16Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcode16Test.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));

        PdfCanvas canvas = new PdfCanvas(document.addNewPage());
        BarcodeDataMatrix barcode = new BarcodeDataMatrix();
        barcode.setCode("999999DILLERT XANG LIMITON 18               000");
        canvas.concatMatrix(1, 0, 0, 1, 100, 600);
        barcode.placeBarcode(canvas, ColorConstants.BLACK, 3);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder));
    }
}
