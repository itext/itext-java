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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class SimpleFontToUnicodeExtractionTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/SimpleFontToUnicodeExtractionTest/";

    @Test
    public void test01() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "simpleFontToUnicode01.pdf"));
        String expected = "Information plays a central role in soci-\n" +
                "ety today, and it is becoming more and \n" +
                "more common for that information to \n" +
                "be offered in digital form alone. The re-\n" +
                "liable, user-friendly Portable Document \n" +
                "Format (PDF) has become the world’s \n" +
                "file type of choice for providing infor-\n" +
                "mation as a digital document. \n" +
                "Tags can be added to a PDF in order \n" +
                "to structure the content of a document. \n" +
                "These tags are a critical requirement if \n" +
                "any form of assistive technology (such \n" +
                "as screen readers, specialist mice, and \n" +
                "speech recognition and text-to-speech \n" +
                "software) is to gain access to this con-\n" +
                "tent. To date, PDF documents have rare-\n" +
                "ly been tagged, and not all software can \n" +
                "make use of PDF tags. In practical terms, \n" +
                "this particularly reduces information‘s \n" +
                "accessibility for people with disabilities \n" +
                "who rely on assistive technology.";

        String actualText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1),
                new LocationTextExtractionStrategy());

        Assertions.assertEquals(expected, actualText);
    }

    @Test
    public void test02() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "simpleFontToUnicode02.pdf"));
        String expected = "ffaast";

        String actualText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1),
                new LocationTextExtractionStrategy());

        Assertions.assertEquals(expected, actualText);
    }

}
