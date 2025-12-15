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
package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link BrotliFilter}.
 */
@Tag("IntegrationTest")
public class BrotliFilterIntegrationTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/filters"
            + "/BrotliFilterIntegrationTest/";
    public static final String DESTINATION_FOLDER =
            TestUtil.getOutputPath() + "/kernel/filters/BrotliFilterIntegrationTest/";

    @Test
    public void decodeBrotliWithDecodeParmsTest() throws IOException, java.io.IOException {
        String sourcePdf = SOURCE_FOLDER + "brotli_correct_decode.pdf";
        byte[] originalBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "simple.bmp"));

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourcePdf))) {
            PdfPage page = pdfDoc.getFirstPage();
            PdfDictionary xObjects = page.getResources().getPdfObject().getAsDictionary(PdfName.XObject);
            PdfStream imgStream = xObjects.getAsStream(new PdfName("Im1"));

            Assertions.assertArrayEquals(originalBytes, imgStream.getBytes(), "Decoded image should match original");
        }
    }

    @Test
    @Disabled("DEVSIX-9595: We cannot create an integrationtest yet for this.")
    public void decodeBrotliContentStreamWithDDictionaryTest() throws java.io.IOException {
        String src = SOURCE_FOLDER + "brotli_with_D_dictionary.pdf";

        byte[] expected = "The quick brown fox jumps over the lazy dog.".getBytes(StandardCharsets.UTF_8);

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src))) {
            PdfPage page = pdfDoc.getFirstPage();
            PdfStream stream = page.getContentStream(0);
            Assertions.assertArrayEquals(expected, stream.getBytes());
        }
    }

    @Test
    public void decodeBrotliContentStreamWithWrongDecodeParamsTest() throws java.io.IOException {
        String src = SOURCE_FOLDER + "brotli_contentstream_wrong_DecodeParms.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src))) {
            PdfPage page = pdfDoc.getFirstPage();
            PdfStream stream = page.getContentStream(0);

            Exception exception = Assertions.assertThrows(PdfException.class, () ->
                    stream.getBytes());

            Assertions.assertEquals(KernelExceptionMessageConstant.PNG_FILTER_UNKNOWN,
                    exception.getMessage());
        }
    }
}