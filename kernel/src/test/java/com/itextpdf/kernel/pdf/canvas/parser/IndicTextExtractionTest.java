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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class IndicTextExtractionTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/IndicTextExtractionTest/";

    @Test
    public void test01() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "test01.pdf"));

        final String[] expectedText = new String[]{
                "\u0928\u093F\u0930\u094D\u0935\u093E\u091A\u0915",
                "\u0928\u0917\u0930\u0928\u093F\u0917\u092E / " +
                        "\u0928\u0917\u0930\u092A\u0930\u093F\u0937\u0926" +
                        " / \u0928\u0917\u0930\u092A\u093E\u0932\u093F\u0915\u093E \u0915\u093E \u0928\u093E\u092E",
                "\u0935 " + "\u0938\u0902\u0916\u094D\u092F\u093E",
                "\u0938\u0902\u0915\u094D\u0937\u093F\u092A\u094D\u0924 \u092A\u0941\u0928\u0930\u0940\u0915\u094D\u0937\u0923",
                "\u092E\u0924\u0926\u093E\u0928 " + "\u0915\u0947\u0928\u094D\u0926\u094D\u0930" +
                        "\u0915\u093E",
                "\u0906\u0930\u0902\u092D\u093F\u0915 " + "\u0915\u094D\u0930\u092E\u0938\u0902\u0916\u094D\u092F\u093E"
        };
        final Rectangle[] regions = new Rectangle[]{
                new Rectangle(30, 779, 45, 20),
                new Rectangle(30, 745, 210, 20),
                new Rectangle(30, 713, 42, 20),
                new Rectangle(30, 679, 80, 20),
                new Rectangle(30, 647, 73, 20),
                new Rectangle(30, 612, 93, 20)
        };

        final TextRegionEventFilter[] regionFilters = new TextRegionEventFilter[regions.length];
        for (int i = 0; i < regions.length; i++)
            regionFilters[i] = new TextRegionEventFilter(regions[i]);

        FilteredEventListener listener = new FilteredEventListener();
        LocationTextExtractionStrategy[] extractionStrategies = new LocationTextExtractionStrategy[regions.length];
        for (int i = 0; i < regions.length; i++)
            extractionStrategies[i] = listener.attachEventListener(new LocationTextExtractionStrategy().setUseActualText(true), regionFilters[i]);

        new PdfCanvasProcessor(listener).processPageContent(pdfDocument.getPage(1));

        for (int i = 0; i < regions.length; i++) {
            String actualText = extractionStrategies[i].getResultantText();
            Assertions.assertEquals(expectedText[i], actualText);
        }
    }

    @Test
    public void test02() throws IOException {
        String expectedText = "\u0926\u0947\u0935\u0928\u093E\u0917\u0930\u0940 \u090F\u0915 \u0932\u093F\u092A\u093F \u0939\u0948 \u091C\u093F\u0938\u092E\u0947\u0902 \u0905\u0928\u0947\u0915 \u092D\u093E\u0930\u0924\u0940\u092F \u092D\u093E\u0937\u093E\u090F\u0901 \u0924\u0925\u093E \u0915\u0941\u091B \u0935\u093F\u0926\u0947\u0936\u0940 \u092D\u093E\u0937\u093E\u090F\u0902 \u0932\u093F\u0916\u0940\u0902 \u091C\u093E\u0924\u0940 \u0939\u0948\u0902\u0964 \u0926\u0947\u0935\u0928\u093E\u0917\u0930\u0940 \u092C\u093E\u092F\u0947\u0902 \u0938\u0947 \u0926\u093E\u092F\u0947\u0902 \u0932\u093F\u0916\u0940" +
                "\n" +
                "\u091C\u093E\u0924\u0940 \u0939\u0948, \u0907\u0938\u0915\u0940 \u092A\u0939\u091A\u093E\u0928 \u090F\u0915 \u0915\u094D\u0937\u0948\u0924\u093F\u091C \u0930\u0947\u0916\u093E \u0938\u0947 \u0939\u0948 \u091C\u093F\u0938\u0947 \'\u0936\u093F\u0930\u093F\u0930\u0947\u0916\u093E\' \u0915\u0939\u0924\u0947 \u0939\u0948\u0902\u0964 \u0938\u0902\u0938\u094D\u0915\u0943\u0924, \u092A\u093E\u0932\u093F, \u0939\u093F\u0928\u094D\u0926\u0940, \u092E\u0930\u093E\u0920\u0940, \u0915\u094B\u0902\u0915\u0923\u0940, \u0938\u093F\u0928\u094D\u0927\u0940," +
                "\n" +
                "\u0915\u0936\u094D\u092E\u0940\u0930\u0940, \u0921\u094B\u0917\u0930\u0940, \u0928\u0947\u092A\u093E\u0932\u0940, \u0928\u0947\u092A\u093E\u0932 \u092D\u093E\u0937\u093E (\u0924\u0925\u093E \u0905\u0928\u094D\u092F \u0928\u0947\u092A\u093E\u0932\u0940 \u0909\u092A\u092D\u093E\u0937\u093E\u090F\u0901), \u0924\u093E\u092E\u093E\u0919 \u092D\u093E\u0937\u093E, \u0917\u0922\u093C\u0935\u093E\u0932\u0940, \u092C\u094B\u0921\u094B, \u0905\u0902\u0917\u093F\u0915\u093E, \u092E\u0917\u0939\u0940, \u092D\u094B\u091C\u092A\u0941\u0930\u0940," +
                "\n" +
                "\u092E\u0948\u0925\u093F\u0932\u0940, \u0938\u0902\u0925\u093E\u0932\u0940 \u0906\u0926\u093F \u092D\u093E\u0937\u093E\u090F\u0901 \u0926\u0947\u0935\u0928\u093E\u0917\u0930\u0940 \u092E\u0947\u0902 \u0932\u093F\u0916\u0940 \u091C\u093E\u0924\u0940 \u0939\u0948\u0902\u0964 \u0907\u0938\u0915\u0947 \u0905\u0924\u093F\u0930\u093F\u0915\u094D\u0924 \u0915\u0941\u091B \u0938\u094D\u0925\u093F\u0924\u093F\u092F\u094B\u0902 \u092E\u0947\u0902 \u0917\u0941\u091C\u0930\u093E\u0924\u0940, \u092A\u0902\u091C\u093E\u092C\u0940, \u092C\u093F\u0937\u094D\u0923\u0941\u092A\u0941\u0930\u093F\u092F\u093E" +
                "\n" +
                "\u092E\u0923\u093F\u092A\u0941\u0930\u0940, \u0930\u094B\u092E\u093E\u0928\u0940 \u0914\u0930 \u0909\u0930\u094D\u0926\u0942 \u092D\u093E\u0937\u093E\u090F\u0902 \u092D\u0940 \u0926\u0947\u0935\u0928\u093E\u0917\u0930\u0940 \u092E\u0947\u0902 \u0932\u093F\u0916\u0940 \u091C\u093E\u0924\u0940 \u0939\u0948\u0902\u0964";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "test02.pdf"));
        String extractedText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new LocationTextExtractionStrategy().setUseActualText(true));

        Assertions.assertEquals(expectedText, extractedText);
    }

}
