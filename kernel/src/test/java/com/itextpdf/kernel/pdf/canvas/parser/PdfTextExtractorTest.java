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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class PdfTextExtractorTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/PdfTextExtractorTest/";

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.PDF_REFERS_TO_NOT_EXISTING_PROPERTY_DICTIONARY))
    public void noSpecifiedDictionaryInPropertiesTest() throws IOException {
        String inFile = sourceFolder + "noSpecifiedDictionaryInProperties.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile))) {
            String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1));
            // Here we check that no NPE wasn't thrown. There is no text on the page so the extracted string should be empty.
            Assertions.assertEquals("", text);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.PDF_REFERS_TO_NOT_EXISTING_PROPERTY_DICTIONARY))
    public void noPropertiesInResourcesTest() throws IOException {
        String inFile = sourceFolder + "noPropertiesInResources.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile))) {
            String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1));
            // Here we check that no NPE wasn't thrown. There is no text on the page so the extracted string should be empty.
            Assertions.assertEquals("", text);
        }
    }

    @Test
    public void type3FontNoCMapTest() throws IOException {
        String inFile = sourceFolder + "type3NoCMap.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile))) {
            Assertions.assertEquals("*0*", PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1)));
        }
    }

    @Test
    public void noBaseEncodingTest() throws IOException {
        String inFile = sourceFolder + "noBaseEncoding.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile))) {
            Assertions.assertEquals("HELLO WORLD", PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1)));
        }
    }

    @Test
    public void simpleFontWithoutEncodingToUnicodeTest() throws IOException {
        String inFile = sourceFolder + "simpleFontWithoutEncodingToUnicode.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile))) {
            Assertions.assertEquals("MyriadPro-Bold font.", PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1)));
        }
    }

    @Test
    public void simpleFontWithPartialToUnicodeTest() throws IOException {
        String inFile = sourceFolder + "simpleFontWithPartialToUnicode.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile))) {
            Assertions.assertEquals("Registered", PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1)));
        }
    }

    @Test
    public void type0FontToUnicodeTest() throws IOException {
        String inFile = sourceFolder + "type0FontToUnicode.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile))) {
            Assertions.assertEquals("€ 390", PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1)));
        }
    }

    @Test
    public void parseTextDiacriticShiftedLessThanTwo() throws IOException {
        String inFile = sourceFolder + "diacriticShiftedLessThanTwo.pdf";

        // संस्कृत म्
        String expected = "\u0938\u0902\u0938\u094d\u0915\u0943\u0924 \u092e\u094d";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile))) {
            Assertions.assertEquals(expected, PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1)));
        }
    }

    @Test
    public void parseTextDiacriticShiftedMoreThanTwo() throws IOException {
        String inFile = sourceFolder + "diacriticShiftedMoreThanTwo.pdf";

        // ृ
        //संस्कृत म्
        String expected = "\u0943\n\u0938\u0902\u0938\u094d\u0915\u0943\u0924 \u092e\u094d";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile))) {
            Assertions.assertEquals(expected, PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1)));
        }
    }

    @Test
    public void shortOctalDataAsTextTest() throws IOException {
        String inFile = sourceFolder + "shortOctalDataAsText.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile))) {
            Assertions.assertEquals("EC", PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1)));
        }
    }

    @Test
    public void notDefaultCodespacesCyrillicTest() throws IOException {
        String inFile = sourceFolder + "notDefaultCodespacesCyrillic.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile))) {
            String extractedText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1));
            Assertions.assertTrue(extractedText.contains("бронирование"));
            Assertions.assertTrue(extractedText.contains("From"));
        }
    }

    @Test
    public void notDefaultCodespacesChineseTest() throws IOException {
        String inFile = sourceFolder + "notDefaultCodespacesChinese.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile))) {
            String extractedText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1));
            Assertions.assertTrue(extractedText.contains("L3B 廠： 新竹科學工業園區新竹市東區力行二路 1 號"));
        }
    }

    @Test
    public void mixedCharacterCodes() throws IOException {
        String inFile = sourceFolder + "SameCidForDifferentCodes.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFile))) {
            String extractedText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1));
            Assertions.assertTrue(extractedText.contains("18个月"));
            Assertions.assertFalse(extractedText.contains("18个⽉"));
        }
    }
}
