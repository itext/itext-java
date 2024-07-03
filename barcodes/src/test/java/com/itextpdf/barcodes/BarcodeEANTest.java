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

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class BarcodeEANTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/barcodes/";
    public static final String destinationFolder = "./target/test/com/itextpdf/barcodes/BarcodeEAN/";

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
        String filename = "barcodeEAN_01.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        Barcode1D barcode = new BarcodeEAN(document);
        barcode.setCodeType(BarcodeEAN.EAN13);
        barcode.setCode("9781935182610");

        barcode.setTextAlignment(Barcode1D.ALIGN_LEFT);
        barcode.placeBarcode(canvas, ColorConstants.BLACK, ColorConstants.BLACK);

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder,
                        "diff_"));
    }

    @Test
    public void barcode02Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeEAN_02.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfReader reader = new PdfReader(sourceFolder + "DocumentWithTrueTypeFont1.pdf");
        PdfDocument document = new PdfDocument(reader, writer);

        PdfCanvas canvas = new PdfCanvas(document.getLastPage());

        Barcode1D barcode = new BarcodeEAN(document);
        barcode.setCodeType(BarcodeEAN.EAN8);
        barcode.setCode("97819351");

        barcode.setTextAlignment(Barcode1D.ALIGN_LEFT);
        barcode.placeBarcode(canvas, ColorConstants.BLACK, ColorConstants.BLACK);

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder,
                        "diff_"));
    }

    @Test
    public void barcode03Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeEANSUP.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        BarcodeEAN codeEAN = new BarcodeEAN(document);
        codeEAN.setCodeType(BarcodeEAN.EAN13);
        codeEAN.setCode("9781935182610");
        BarcodeEAN codeSUPP = new BarcodeEAN(document);
        codeSUPP.setCodeType(BarcodeEAN.SUPP5);
        codeSUPP.setCode("55999");
        codeSUPP.setBaseline(-2);
        BarcodeEANSUPP eanSupp = new BarcodeEANSUPP(codeEAN, codeSUPP);
        eanSupp.placeBarcode(canvas, null, ColorConstants.BLUE);

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder,
                        "diff_"));
    }

    @Test
    public void placeBarcodeUPCATest() throws IOException, PdfException, InterruptedException {
        String filename = "placeBarcodeUPCATest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        Barcode1D barcode = new BarcodeEAN(document);
        barcode.setCodeType(BarcodeEAN.UPCA);
        barcode.setCode("012340000006");

        barcode.placeBarcode(canvas, ColorConstants.BLACK, ColorConstants.BLACK);

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder,
                        "diff_"));
    }

    @Test
    public void placeBarcodeUPCETest() throws IOException, PdfException, InterruptedException {
        String filename = "placeBarcodeUPCETest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        Barcode1D barcode = new BarcodeEAN(document);
        barcode.setCodeType(BarcodeEAN.UPCE);
        barcode.setCode("03456781");

        barcode.placeBarcode(canvas, ColorConstants.BLACK, ColorConstants.BLACK);

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder,
                        "diff_"));
    }

    @Test
    public void placeBarcodeSUPP2Test() throws IOException, PdfException, InterruptedException {
        String filename = "placeBarcodeSUPP2Test.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        Barcode1D barcode = new BarcodeEAN(document);
        barcode.setCodeType(BarcodeEAN.SUPP2);
        barcode.setCode("03456781");

        barcode.placeBarcode(canvas, ColorConstants.BLACK, ColorConstants.BLACK);

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder,
                        "diff_"));
    }

    @Test
    public void placeBarcodeSUPP5Test() throws IOException, PdfException, InterruptedException {
        String filename = "placeBarcodeSUPP5Test.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        Barcode1D barcode = new BarcodeEAN(document);
        barcode.setCodeType(BarcodeEAN.SUPP5);
        barcode.setCode("55999");

        barcode.placeBarcode(canvas, ColorConstants.BLACK, ColorConstants.BLACK);

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder,
                        "diff_"));
    }
}
