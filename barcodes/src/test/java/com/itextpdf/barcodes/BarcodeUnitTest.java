/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class BarcodeUnitTest extends ExtendedITextTest {

    private static final double EPS = 0.0001;

    @Test
    public void BarcodeMSIGetBarcodeSizeWithChecksumTest() {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        Barcode1D barcode = new BarcodeMSI(document);
        document.close();
        barcode.setCode("123456789");
        barcode.setGenerateChecksum(true);
        Rectangle barcodeSize = barcode.getBarcodeSize();
        Assertions.assertEquals(33.656, barcodeSize.getHeight(), EPS);
        Assertions.assertEquals(101.6, barcodeSize.getWidth(), EPS);
    }

    @Test
    public void BarcodeMSIGetBarcodeSizeWithoutChecksumTest() {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        Barcode1D barcode = new BarcodeMSI(document);
        document.close();
        barcode.setCode("123456789");
        barcode.setGenerateChecksum(false);
        Rectangle barcodeSize = barcode.getBarcodeSize();
        Assertions.assertEquals(33.656, barcodeSize.getHeight(), EPS);
        Assertions.assertEquals(92.0, barcodeSize.getWidth(), EPS);
    }
}
