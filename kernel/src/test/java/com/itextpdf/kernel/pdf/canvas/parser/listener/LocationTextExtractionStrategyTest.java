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

    @Test
    // TODO DEVSIX-9940 - Text location extraction issue
    public void testLocationExtractionInTibetan() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "tibetan.pdf"));
        LocationTextExtractionStrategy locationTextExtractionStrategy =
                new LocationTextExtractionStrategy().setUseActualText(true);
        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), locationTextExtractionStrategy);
        pdfDocument.close();

        // Actual expected result
//                  "0\n"
//                + "དརཀྭྲོ - 1\n"
//                + "དརཀྭླི - 2\n"
//                + "དརཀྭླུ - 3\n"
//                + "དརཀྭླེ - 4\n"
//                + "དརཀྭློ - 5\n"
//                + "དརཀྭྭི - 6\n"
//                + "དརཀྭྭུ - 7\n"
//                + "དརཀྭྭེ - 8\n"
//                + "དརཀྭྭོ - 9\n"
//                + "དརཁིགས - 10\n"
//                + "དརཁིངས - 11\n"
//                + "དརཁིདས - 12\n"
//                + "དརཁིའས - 13\n"
//                + "དརཁུགས - 14\n"
//                + "དརཁུངས - 15\n"
//                + "དརཁུདས - 16\n"
//                + "དརཁུའས - 17\n"
//                + "དརཁེགས - 18\n"
//                + "དརཁེངས - 19"

        String expectedText = "0\n"
                + "- 1\n"
                + "- 2\n"
                + "- 3\n"
                + "- 4\n"
                + "- 5\n"
                + "- 6\n"
                + "- 7\n"
                + "- 8\n"
                + "- 9\n"
                + "དརཁགས - 10\n"
                + "དརཁིངས - 11\n"
                + "དརཁིདས - 12\n"
                + "དརཁིའས - 13\n"
                + "དརཁུགས - 14\n"
                + "དརཁུངས - 15\n"
                + "དརཁུདས - 16\n"
                + "དརཁུའས - 17\n"
                + "དརཁེགས - 18\n"
                + "དརཁེངས - 19\n"
                + "དརཀྭླི \n"
                + "དརཀྭླུ \n"
                + "དརཀྭླེ \n"
                + "དརཀྭློ \n"
                + "དརཀྭྭི \n"
                + "དརཀྭྭེ \n"
                + "དརཀྭྭོ \n"
                + "ི\n"
                + "དརཀྭྲོ \n"
                + "དརཀྭྭུ ";
        Assertions.assertEquals(expectedText, text);
    }
}
