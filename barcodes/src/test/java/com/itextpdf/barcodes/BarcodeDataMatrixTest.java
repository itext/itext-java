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
package com.itextpdf.barcodes;


import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.IOException;

@Category(IntegrationTest.class)
public class BarcodeDataMatrixTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/barcodes/";
    public static final String destinationFolder = "./target/test/com/itextpdf/barcodes/BarcodeDataMatrix/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();
    
    @Test
    public void barcode01Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeDataMatrix.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        BarcodeDataMatrix barcode = new BarcodeDataMatrix();
        barcode.setCode("AAAAAAAAAA;BBBBAAAA3;00028;BBBAA05;AAAA;AAAAAA;1234567;AQWXSZ;JEAN;;;;7894561;AQWXSZ;GEO;;;;1;1;1;1;0;0;1;0;1;0;0;0;1;0;1;0;0;0;0;0;0;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1");
        barcode.placeBarcode(canvas, ColorConstants.GREEN, 5);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode02Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeDataMatrix2.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);
        BarcodeDataMatrix barcode2 = new BarcodeDataMatrix("дима", "UTF-8");
        barcode2.placeBarcode(canvas, ColorConstants.GREEN, 10);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode03Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeDataMatrix3.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        BarcodeDataMatrix barcode3 = new BarcodeDataMatrix();
        barcode3.setWidth(36);
        barcode3.setHeight(12);
        barcode3.setCode("AbcdFFghijklmnopqrstuWXSQ");
        barcode3.placeBarcode(canvas, ColorConstants.BLACK, 10);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode04Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeDataMatrix4.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        BarcodeDataMatrix barcode3 = new BarcodeDataMatrix();
        barcode3.setWidth(36);
        barcode3.setHeight(12);
        barcode3.setCode("01AbcdefgAbcdefg123451231231234");
        barcode3.placeBarcode(canvas, ColorConstants.BLACK, 10);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode05Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeDataMatrix5.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        BarcodeDataMatrix barcode3 = new BarcodeDataMatrix();
        barcode3.setWidth(40);
        barcode3.setHeight(40);
        barcode3.setCode("aaabbbcccdddAAABBBAAABBaaabbbcccdddaaa");
        barcode3.placeBarcode(canvas, ColorConstants.BLACK, 10);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode06Test() throws IOException, PdfException, InterruptedException {
        String filename = "barcodeDataMatrix6.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page1 = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        BarcodeDataMatrix barcode3 = new BarcodeDataMatrix();
        barcode3.setWidth(36);
        barcode3.setHeight(12);
        barcode3.setCode(">>>\r>>>THIS VERY TEXT>>\r>");
        barcode3.placeBarcode(canvas, ColorConstants.BLACK, 10);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void barcode07Test() {
        BarcodeDataMatrix bc = new BarcodeDataMatrix();
        bc.setOptions(BarcodeDataMatrix.DM_AUTO);
        bc.setWidth(10);
        bc.setHeight(10);

        String aCode = "aBCdeFG12";

        int result = bc.setCode(aCode);
        Assert.assertEquals(result, BarcodeDataMatrix.DM_ERROR_TEXT_TOO_BIG);
    }

    @Test
    public void barcode08Test() {
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(18);
        barcodeDataMatrix.setHeight(18);
        int result = barcodeDataMatrix.setCode("AbcdFFghijklmnopqrstuWXSQ");
        Assert.assertEquals(BarcodeDataMatrix.DM_ERROR_TEXT_TOO_BIG, result);
    }

    @Test
    public void barcode09Test() {
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(17);
        barcodeDataMatrix.setHeight(17);
        int result = barcodeDataMatrix.setCode("AbcdFFghijklmnopqrstuWXSQ");
        Assert.assertEquals(BarcodeDataMatrix.DM_ERROR_INVALID_SQUARE, result);
    }

    @Test
    public void barcode10Test() {
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(26);
        barcodeDataMatrix.setHeight(12);
        int result = barcodeDataMatrix.setCode("AbcdFFghijklmnopqrstuWXSQ");
        Assert.assertEquals(BarcodeDataMatrix.DM_ERROR_TEXT_TOO_BIG, result);
    }

    @Test
    public void barcode11Test() {
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(18);
        barcodeDataMatrix.setHeight(18);
        byte[] str = "AbcdFFghijklmnop".getBytes();
        int result = barcodeDataMatrix.setCode(str, 0, str.length);
        Assert.assertEquals(BarcodeDataMatrix.DM_NO_ERROR, result);
    }

    @Test
    public void barcode12Test() {
        junitExpectedException.expect(IndexOutOfBoundsException.class);
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(18);
        barcodeDataMatrix.setHeight(18);
        byte[] str = "AbcdFFghijklmnop".getBytes();
        barcodeDataMatrix.setCode(str, -1, str.length);
    }

    @Test
    public void barcode13Test() {
        junitExpectedException.expect(IndexOutOfBoundsException.class);
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(18);
        barcodeDataMatrix.setHeight(18);
        byte[] str = "AbcdFFghijklmnop".getBytes();
        barcodeDataMatrix.setCode(str, 0, str.length + 1);
    }

    @Test
    public void barcode14Test() {
        junitExpectedException.expect(IndexOutOfBoundsException.class);
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(18);
        barcodeDataMatrix.setHeight(18);
        byte[] str = "AbcdFFghijklmnop".getBytes();
        barcodeDataMatrix.setCode(str, 0, -1);
    }

    @Test
    public void barcode15Test() {
        BarcodeDataMatrix barcodeDataMatrix = new BarcodeDataMatrix();
        barcodeDataMatrix.setWidth(18);
        barcodeDataMatrix.setHeight(18);
        byte[] str = "AbcdFFghijklmnop".getBytes();
        int result = barcodeDataMatrix.setCode(str, str.length, 0);
        Assert.assertEquals(BarcodeDataMatrix.DM_NO_ERROR, result);
    }
}
