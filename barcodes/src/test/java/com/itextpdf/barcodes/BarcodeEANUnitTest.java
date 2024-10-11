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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class BarcodeEANUnitTest extends ExtendedITextTest {

    public static final float EPS = 0.0001f;

    @Test
    public void calculateEANParityTest() throws PdfException {
        int expectedParity = BarcodeEAN.calculateEANParity("1234567890");

        Assertions.assertEquals(5, expectedParity);
    }

    @Test
    public void convertUPCAtoUPCEIncorrectTextTest() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("HelloWorld");

        Assertions.assertNull(expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCE12DigitsStartNotWith0Or1Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("025272730706");

        Assertions.assertNull(expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom3Position00000Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012000005706");

        Assertions.assertEquals("01257006", expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom3Position10000Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012100005706");

        Assertions.assertEquals("01257016", expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom3Position20000Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012200005706");

        Assertions.assertEquals("01257026", expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom3Position000NullTest() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012000111706");

        Assertions.assertNull(expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom4Position00NullTest() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012300111706");

        Assertions.assertNull(expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom4Position00000Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012300000706");

        Assertions.assertEquals("01237036", expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom5Position0NullTest() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012340111706");

        Assertions.assertNull(expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCEFrom5Position00000Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("012340000006");

        Assertions.assertEquals("01234046", expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCE10PositionBiggerThan5NullTest() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("011111111711");

        Assertions.assertNull(expectedUpce);
    }

    @Test
    public void convertUPCAtoUPCE10PositionBiggerThan5Test() throws PdfException {
        String expectedUpce = BarcodeEAN.convertUPCAtoUPCE("011111000090");

        Assertions.assertEquals("01111190", expectedUpce);
    }

    @Test
    public void getBarsUPCETest() throws PdfException {
        String expectedBytes = "111212211411132132141111312111111";

        byte[] bytes = BarcodeEAN.getBarsUPCE("12345678");
        Assertions.assertEquals(33, bytes.length);
        for (int i = 0; i < expectedBytes.length(); i++) {
            Assertions.assertEquals(expectedBytes.charAt(i) - '0', bytes[i]);
        }
    }

    @Test
    public void getBarsSupplemental2Test() throws PdfException {
        String expectedBytes = "1121222113211";

        byte[] bytes = BarcodeEAN.getBarsSupplemental2("10");
        Assertions.assertEquals(13, bytes.length);
        for (int i = 0; i < expectedBytes.length(); i++) {
            Assertions.assertEquals(expectedBytes.charAt(i) - '0', bytes[i]);
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

        Assertions.assertEquals(expectedRectangle.getWidth(), barcodeSize.getWidth(), EPS);
        Assertions.assertEquals(expectedRectangle.getHeight(), barcodeSize.getHeight(), EPS);
    }

    @Test
    public void getBarcodeSizeUPCETest() throws PdfException {
        Rectangle expectedRectangle = new Rectangle(49.696f, 33.656f);

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        Barcode1D barcode = new BarcodeEAN(document);
        barcode.setCodeType(BarcodeEAN.UPCE);
        barcode.setCode("9781935182610");

        Rectangle barcodeSize = barcode.getBarcodeSize();

        Assertions.assertEquals(expectedRectangle.getWidth(), barcodeSize.getWidth(), EPS);
        Assertions.assertEquals(expectedRectangle.getHeight(), barcodeSize.getHeight(), EPS);
    }

    @Test
    public void getBarcodeSizeSUPP2Test() throws PdfException {
        Rectangle expectedRectangle = new Rectangle(16, 33.656f);

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        Barcode1D barcode = new BarcodeEAN(document);
        barcode.setCodeType(BarcodeEAN.SUPP2);
        barcode.setCode("03456781");

        Rectangle barcodeSize = barcode.getBarcodeSize();

        Assertions.assertEquals(expectedRectangle.getWidth(), barcodeSize.getWidth(), EPS);
        Assertions.assertEquals(expectedRectangle.getHeight(), barcodeSize.getHeight(), EPS);
    }

    @Test
    public void getBarcodeSizeIncorrectTypeTest() throws PdfException {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        Barcode1D barcode = new BarcodeEAN(document);
        barcode.setCode("9781935182610");

        // Set incorrect type
        barcode.setCodeType(1234);

        // We do expect an exception here
        Exception e = Assertions.assertThrows(PdfException.class, () -> barcode.getBarcodeSize());
        Assertions.assertEquals("Invalid code type", e.getMessage());
    }
}
