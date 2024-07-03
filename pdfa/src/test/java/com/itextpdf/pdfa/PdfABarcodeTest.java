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
package com.itextpdf.pdfa;

import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.barcodes.Barcode1D;
import com.itextpdf.barcodes.Barcode39;
import com.itextpdf.barcodes.BarcodeCodabar;
import com.itextpdf.barcodes.BarcodeEAN;
import com.itextpdf.barcodes.BarcodeInter25;
import com.itextpdf.barcodes.BarcodeMSI;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.fail;

@Tag("IntegrationTest")
public class PdfABarcodeTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfABarcodeTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfABarcodeTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void barcodeMSITest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "barcodeMSITest.pdf";
        String cmpPdf = cmpFolder + "cmp_barcodeMSITest.pdf";

        Document doc = createPdfATaggedDocument(outPdf);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        font.setSubset(true);

        BarcodeMSI codeMSI = new BarcodeMSI(doc.getPdfDocument(), font);
        fillBarcode1D(codeMSI, "1234567");

        PdfFormXObject barcode = codeMSI.createFormXObject(doc.getPdfDocument());
        Image img = new Image(barcode).setMargins(0, 0, 0, 0);
        img.getAccessibilityProperties().setAlternateDescription("hello world!");

        doc.add(img);

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void barcodeInter25Test() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "barcodeInter25Test.pdf";
        String cmpPdf = cmpFolder + "cmp_barcodeInter25Test.pdf";

        Document doc = createPdfATaggedDocument(outPdf);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        font.setSubset(true);

        BarcodeInter25 codeInter25 = new BarcodeInter25(doc.getPdfDocument(), font);
        fillBarcode1D(codeInter25, "1234567");

        PdfFormXObject barcode = codeInter25.createFormXObject(doc.getPdfDocument());
        Image img = new Image(barcode).setMargins(0, 0, 0, 0);
        img.getAccessibilityProperties().setAlternateDescription("hello world!");

        doc.add(img);

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void barcodeEANTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "barcodeEANTest.pdf";
        String cmpPdf = cmpFolder + "cmp_barcodeEANTest.pdf";

        Document doc = createPdfATaggedDocument(outPdf);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        font.setSubset(true);

        BarcodeEAN codeEAN = new BarcodeEAN(doc.getPdfDocument(), font);
        fillBarcode1D(codeEAN, "9781935182610");

        PdfFormXObject barcode = codeEAN.createFormXObject(doc.getPdfDocument());
        Image img = new Image(barcode).setMargins(0, 0, 0, 0);
        img.getAccessibilityProperties().setAlternateDescription("hello world!");

        doc.add(img);

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void barcodeCodabarTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "barcodeCodabarTest.pdf";
        String cmpPdf = cmpFolder + "cmp_barcodeCodabarTest.pdf";

        Document doc = createPdfATaggedDocument(outPdf);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        font.setSubset(true);

        BarcodeCodabar codeCodabar = new BarcodeCodabar(doc.getPdfDocument(), font);
        fillBarcode1D(codeCodabar, "A123A");

        PdfFormXObject barcode = codeCodabar.createFormXObject(doc.getPdfDocument());
        Image img = new Image(barcode).setMargins(0, 0, 0, 0);
        img.getAccessibilityProperties().setAlternateDescription("hello world!");

        doc.add(img);

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void barcode39Test() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "barcode39Test.pdf";
        String cmpPdf = cmpFolder + "cmp_barcode39Test.pdf";

        Document doc = createPdfATaggedDocument(outPdf);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        font.setSubset(true);

        Barcode39 code39 = new Barcode39(doc.getPdfDocument(), font);
        fillBarcode1D(code39, "1234567");

        PdfFormXObject barcode = code39.createFormXObject(doc.getPdfDocument());
        Image img = new Image(barcode).setMargins(0, 0, 0, 0);
        img.getAccessibilityProperties().setAlternateDescription("hello world!");

        doc.add(img);

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void barcode128Test() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "barcode128Test.pdf";
        String cmpPdf = cmpFolder + "cmp_barcode128Test.pdf";

        Document doc = createPdfATaggedDocument(outPdf);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        font.setSubset(true);

        Barcode128 code128 = new Barcode128(doc.getPdfDocument(), font);
        fillBarcode1D(code128, "1234567");

        PdfFormXObject barcode = code128.createFormXObject(doc.getPdfDocument());
        Image img = new Image(barcode).setMargins(0, 0, 0, 0);
        img.getAccessibilityProperties().setAlternateDescription("hello world!");

        doc.add(img);

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    private void fillBarcode1D(Barcode1D barcode1D, String code) {
        barcode1D.setCode(code);
        barcode1D.setCodeType(Barcode128.CODE128);
        barcode1D.setSize(10);
        barcode1D.setBaseline(barcode1D.getSize());
        barcode1D.setGenerateChecksum(true);
        barcode1D.setX(1);
        barcode1D.setN(5);
        barcode1D.setBarHeight(20);
        barcode1D.setChecksumText(false);
    }

    private Document createPdfATaggedDocument(String outPdf) throws IOException {
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        Document doc = new Document(pdfDocument);
        pdfDocument.setTagged();
        return doc;
    }

    private void compareResult(String outFile, String cmpFile) throws IOException, InterruptedException {
        String differences = new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_");
        if (differences != null) {
            fail(differences);
        }
    }
}
