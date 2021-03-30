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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class BarcodeEANUnitTest extends ExtendedITextTest {

    public static final float EPS = 0.0001f;

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void calculateEANParityTest() throws PdfException {
        int expectedParity = BarcodeEAN.calculateEANParity("1234567890");

        Assert.assertEquals(5, expectedParity);
    }

    @Test
    public void convertUPCAtoUPCEIncorrectTextTest() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("HelloWorld");

        Assert.assertNull(expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCE12DigitsStartNotWith0Or1Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("025272730706");

        Assert.assertNull(expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom3Position00000Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012000005706");

        Assert.assertEquals("01257006", expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom3Position10000Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012100005706");

        Assert.assertEquals("01257016", expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom3Position20000Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012200005706");

        Assert.assertEquals("01257026", expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom3Position000NullTest() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012000111706");

        Assert.assertNull(expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom4Position00NullTest() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012300111706");

        Assert.assertNull(expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom4Position00000Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012300000706");

        Assert.assertEquals("01237036", expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom5Position0NullTest() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012340111706");

        Assert.assertNull(expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom5Position00000Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012340000006");

        Assert.assertEquals("01234046", expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCE10PositionBiggerThan5NullTest() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("011111111711");

        Assert.assertNull(expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCE10PositionBiggerThan5Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("011111000090");

        Assert.assertEquals("01111190", expectedUpce);
    }

    @Test
    public void getBarsUPCETest() throws PdfException {
        String expectedBytes = "111212211411132132141111312111111";

        byte[] bytes = BarcodeEAN.getBarsUPCE("12345678");
        Assert.assertEquals(33, bytes.length);
        for (int i = 0; i < expectedBytes.length(); i++) {
            Assert.assertEquals(expectedBytes.charAt(i) - '0', bytes[i]);
        }
    }

    @Test
    public void getBarsSupplemental2Test() throws PdfException {
        String expectedBytes = "1121222113211";

        byte[] bytes = BarcodeEAN.getBarsSupplemental2("10");
        Assert.assertEquals(13, bytes.length);
        for (int i = 0; i < expectedBytes.length(); i++) {
            Assert.assertEquals(expectedBytes.charAt(i) - '0', bytes[i]);
        }
    }

    @Test
    public void getBarcodeSizeUPCATest() throws PdfException {
        Rectangle expectedRectangle = new Rectangle(84.895996f, 33.656f);

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        Barcode1D barcode = new BarcodeEAN(document);
        barcode.setCodeType(BarcodeEAN.UPCA);
        barcode.setCode("9781935182610");

        Rectangle barcodeSize = barcode.getBarcodeSize();

        Assert.assertEquals(expectedRectangle.getWidth(), barcodeSize.getWidth(), EPS);
        Assert.assertEquals(expectedRectangle.getHeight(), barcodeSize.getHeight(), EPS);
    }

    @Test
    public void getBarcodeSizeUPCETest() throws PdfException {
        Rectangle expectedRectangle = new Rectangle(49.696f, 33.656f);

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        Barcode1D barcode = new BarcodeEAN(document);
        barcode.setCodeType(BarcodeEAN.UPCE);
        barcode.setCode("9781935182610");

        Rectangle barcodeSize = barcode.getBarcodeSize();

        Assert.assertEquals(expectedRectangle.getWidth(), barcodeSize.getWidth(), EPS);
        Assert.assertEquals(expectedRectangle.getHeight(), barcodeSize.getHeight(), EPS);
    }

    @Test
    public void getBarcodeSizeSUPP2Test() throws PdfException {
        Rectangle expectedRectangle = new Rectangle(16, 33.656f);

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        Barcode1D barcode = new BarcodeEAN(document);
        barcode.setCodeType(BarcodeEAN.SUPP2);
        barcode.setCode("03456781");

        Rectangle barcodeSize = barcode.getBarcodeSize();

        Assert.assertEquals(expectedRectangle.getWidth(), barcodeSize.getWidth(), EPS);
        Assert.assertEquals(expectedRectangle.getHeight(), barcodeSize.getHeight(), EPS);
    }

    @Test
    public void getBarcodeSizeIncorrectTypeTest() throws PdfException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage("Invalid code type");

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        Barcode1D barcode = new BarcodeEAN(document);
        barcode.setCode("9781935182610");

        // Set incorrect type
        barcode.setCodeType(1234);

        // We do expect an exception here
        barcode.getBarcodeSize();
    }
}
