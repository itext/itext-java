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
package com.itextpdf.layout;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Tag("IntegrationTest")
public class PdfTextExtractorEncodingsTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/PdfTextExtractorEncodingsTest/";

    /**
     * Basic Latin characters, with Unicode values less than 128
     */
    private static final String TEXT1 = "AZaz09*!";
    /**
     * Latin-1 characters
     */
    private static final String TEXT2 = "\u0027\u0060\u00a4\u00a6";

    /**
     * Test parsing a document which uses a standard non-embedded font.
     *
     * @throws Exception any exception will cause the test to fail
     */
    @Test
    public void testStandardFont() throws Exception {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        byte[] pdfBytes = createPdf(font);
        checkPdf(pdfBytes);
    }

    /**
     * Test parsing a document which uses a font encoding which creates a /Differences
     * PdfArray in the PDF.
     *
     * @throws Exception any exception will cause the test to fail
     */
    @Test
    public void testEncodedFont() throws Exception {
        PdfFont font = getTTFont("ISO-8859-1", EmbeddingStrategy.PREFER_EMBEDDED);
        byte[] pdfBytes = createPdf(font);
        checkPdf(pdfBytes);
    }

    /**
     * Test parsing a document which uses a Unicode font encoding which creates a /ToUnicode
     * PdfArray.
     *
     * @throws Exception any exception will cause the test to fail
     */
    @Test
    public void testUnicodeFont() throws Exception {
        PdfFont font = getTTFont(PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
        byte[] pdfBytes = createPdf(font);
        checkPdf(pdfBytes);
    }

    private void checkPdf(byte[] pdfBytes) throws Exception {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdfBytes)));
        // Characters from http://unicode.org/charts/PDF/U0000.pdf
        Assertions.assertEquals(TEXT1, PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1)));
        // Characters from http://unicode.org/charts/PDF/U0080.pdf
        Assertions.assertEquals(TEXT2, PdfTextExtractor.getTextFromPage(pdfDocument.getPage(2)));
    }

    protected static PdfFont getTTFont(String encoding, EmbeddingStrategy embeddingStrategy) throws IOException {
        return PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", encoding, embeddingStrategy);
    }

    private static byte[] createPdf(PdfFont font) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        Document document = new Document(new PdfDocument(new PdfWriter(byteStream)));
        document.add(new Paragraph(TEXT1).setFont(font));
        document.add(new AreaBreak());
        document.add(new Paragraph(TEXT2).setFont(font));
        document.close();

        return byteStream.toByteArray();
    }
}
