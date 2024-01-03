/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

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
