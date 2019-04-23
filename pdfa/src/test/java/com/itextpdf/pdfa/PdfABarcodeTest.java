/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.pdfa;

import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.barcodes.Barcode1D;
import com.itextpdf.barcodes.Barcode39;
import com.itextpdf.barcodes.BarcodeCodabar;
import com.itextpdf.barcodes.BarcodeEAN;
import com.itextpdf.barcodes.BarcodeInter25;
import com.itextpdf.barcodes.BarcodeMSI;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfABarcodeTest extends ITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfABarcodeTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfABarcodeTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void barcodeMSITest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "barcodeMSITest.pdf";
        String cmpPdf = cmpFolder + "cmp_barcodeMSITest.pdf";

        Document doc = createPdfATaggedDocument(outPdf);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
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

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
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

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
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

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
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

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
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

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
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
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
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
