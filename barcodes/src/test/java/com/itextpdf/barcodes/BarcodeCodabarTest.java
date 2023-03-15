/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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


import com.itextpdf.barcodes.exceptions.BarcodeExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class BarcodeCodabarTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/barcodes/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/barcodes/Codabar/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void barcode01Test() throws IOException, PdfException, InterruptedException {
        String filename = "codabar.pdf";
        PdfWriter writer = new PdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        BarcodeCodabar codabar = new BarcodeCodabar(document);
        codabar.setCode("A123A");
        codabar.setStartStopText(true);

        codabar.placeBarcode(canvas, null, null);

        document.close();

        Assert.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER,
                        "diff_"));
    }

    @Test
    public void barcodeHasNoAbcdAsStartCharacterTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        BarcodeCodabar codabar = new BarcodeCodabar(pdfDocument);
        Exception exception = Assert.assertThrows(IllegalArgumentException.class,
                () -> codabar.getBarsCodabar("qbcd"));
        Assert.assertEquals(BarcodeExceptionMessageConstant.CODABAR_MUST_HAVE_ONE_ABCD_AS_START_STOP_CHARACTER,
                exception.getMessage());
    }

    @Test
    public void barcodeHasNoAbcdAsStopCharacterTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        BarcodeCodabar codabar = new BarcodeCodabar(pdfDocument);
        Exception exception = Assert.assertThrows(IllegalArgumentException.class,
                () -> codabar.getBarsCodabar("abcf"));
        Assert.assertEquals(BarcodeExceptionMessageConstant.CODABAR_MUST_HAVE_ONE_ABCD_AS_START_STOP_CHARACTER,
                exception.getMessage());
    }

    @Test
    public void barcodeHasNoAbcdAsStartAndStopCharacterTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        BarcodeCodabar codabar = new BarcodeCodabar(pdfDocument);
        Exception exception = Assert.assertThrows(IllegalArgumentException.class,
                () -> codabar.getBarsCodabar("qbcq"));
        Assert.assertEquals(BarcodeExceptionMessageConstant.CODABAR_MUST_HAVE_ONE_ABCD_AS_START_STOP_CHARACTER,
                exception.getMessage());
    }

    @Test
    public void barcodeHasNoStartAndStopCharacterTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        BarcodeCodabar codabar = new BarcodeCodabar(pdfDocument);
        Exception exception = Assert.assertThrows(IllegalArgumentException.class,
                () -> codabar.getBarsCodabar(""));
        Assert.assertEquals(BarcodeExceptionMessageConstant.CODABAR_MUST_HAVE_AT_LEAST_START_AND_STOP_CHARACTER,
                exception.getMessage());
    }
}
