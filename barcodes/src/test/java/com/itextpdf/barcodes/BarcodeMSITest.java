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
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;


import java.io.IOException;

@Tag("IntegrationTest")
public class BarcodeMSITest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/barcodes/";
    public static final String destinationFolder = "./target/test/com/itextpdf/barcodes/BarcodeMSI/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    public void barcode01Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeMSI_01.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Barcode1D barcode = new BarcodeMSI(document);
        barcode.setCode("123456789");
        barcode.setGenerateChecksum(true);
        barcode.setTextAlignment(Barcode1D.ALIGN_LEFT);
        barcode.placeBarcode(canvas, ColorConstants.BLACK, ColorConstants.WHITE);
        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff01_"));
    }


    @Test
    public void barcode02Test() throws IOException, InterruptedException {
        String filename = "barcodeMSI_02.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfReader reader = new PdfReader(sourceFolder + "DocumentWithTrueTypeFont1.pdf");
        PdfDocument document = new PdfDocument(reader, writer);
        PdfCanvas canvas = new PdfCanvas(document.getLastPage());
        Barcode1D barcode = new BarcodeMSI(document);
        barcode.setCode("9781935182610");
        barcode.setTextAlignment(Barcode1D.ALIGN_LEFT);
        barcode.placeBarcode(canvas, ColorConstants.BLACK, ColorConstants.WHITE);
        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff02_"));
    }

    @Test
    public void barcodeAlignRightTest() throws IOException, InterruptedException {
        final String filename = "barcodeMSI_AlignRight.pdf";
        final PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        final PdfDocument document = new PdfDocument(writer);
        final PdfPage page = document.addNewPage();
        final PdfCanvas canvas = new PdfCanvas(page);
        final Barcode1D barcode = new BarcodeMSI(document);
        barcode.setCode("123456789");
        barcode.setGenerateChecksum(true);
        barcode.setTextAlignment(Barcode1D.ALIGN_RIGHT);
        barcode.placeBarcode(canvas, ColorConstants.BLACK, ColorConstants.RED);
        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff01_"));
    }

    @Test
    public void barcodeAlignCenterTest() throws IOException, InterruptedException {
        final String filename = "barcodeMSI_AlignCenter.pdf";
        final PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        final PdfDocument document = new PdfDocument(writer);
        final PdfPage page = document.addNewPage();
        final PdfCanvas canvas = new PdfCanvas(page);
        final Barcode1D barcode = new BarcodeMSI(document);
        barcode.setCode("123456789");
        barcode.setGenerateChecksum(true);
        barcode.setTextAlignment(Barcode1D.ALIGN_CENTER);
        barcode.placeBarcode(canvas, ColorConstants.BLACK, ColorConstants.RED);
        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff01_"));
    }

    @Test
    public void barcode03Test() {
        byte[] expected = {1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1};
        byte[] barcodeBytes = BarcodeMSI.getBarsMSI("1234");
        boolean isEqual = java.util.Arrays.equals(expected, barcodeBytes);
        Assertions.assertTrue(isEqual);
    }

    @Test
    public void barcode04Test() {
        String code = "0987654321";
        int expectedChecksum = 7;
        int checksum = BarcodeMSI.getChecksum(code);
        Assertions.assertEquals(checksum, expectedChecksum);
    }
}
