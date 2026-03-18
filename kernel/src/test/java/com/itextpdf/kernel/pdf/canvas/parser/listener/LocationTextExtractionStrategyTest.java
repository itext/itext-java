/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class LocationTextExtractionStrategyTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/parser/listener"
            + "/LocationTextExtractionStrategyTest/";

    @Test
    public void testSetOutputChunkSeparator() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "testSetOutputChunkSeparator.pdf"));
        LocationTextExtractionStrategy locationTextExtractionStrategy = new LocationTextExtractionStrategy();
        locationTextExtractionStrategy.setOutputChunkSeparator("|");
        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), locationTextExtractionStrategy);
        pdfDocument.close();
        String expectedText = "A|AA|B|BB|C|CC|D|DD";
        Assertions.assertEquals(expectedText, text);
    }

    @Test
    public void testSetOutputNewline() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "testSetOutputNewline.pdf"));
        LocationTextExtractionStrategy locationTextExtractionStrategy = new LocationTextExtractionStrategy();
        locationTextExtractionStrategy.setOutputNewline(";");
        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), locationTextExtractionStrategy);
        pdfDocument.close();
        String expectedText = "        We asked each candidate company to distribute to 225 ;" +
                "randomly selected employees the Great Place to Work ;" +
                "Trust Index. This employee survey was designed by the ;" +
                "Great Place to Work Institute of San Francisco to evaluate ;" +
                "trust in management, pride in work/company, and ;" +
                "camaraderie. Responses were returned directly to us. ";
        Assertions.assertEquals(expectedText, text);
    }
}
