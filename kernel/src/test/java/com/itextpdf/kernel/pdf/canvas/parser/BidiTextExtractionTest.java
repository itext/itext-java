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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class BidiTextExtractionTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/BidiTextExtractionTest/";

    @Test
    public void test01() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "in01.pdf"));
        String expected = "\u0642\u0633\u0651\u0645 \u0639\u0644\u0645\u0627\u0621 \u0627\u0644\u0622\u062B\u0627\u0631 \u0627\u0644\u0644\u063A\u0627\u062A \u0627\u0644\u0639\u0631\u0628\u064A\u0629 \u0625\u0644\u0649 \u0642\u0633\u0645\u064A\u0646 \u0639\u0631\u0628\u064A\u0629 \u062C\u0646\u0648\u0628\u064A\u0629 \u0642\u062F\u064A\u0645\u0629 \u0648\u062A\u0634\u0645\u0644 \u0644\u063A\u0629 \u0633\u0628\u0626\u064A\u0629 \u0648\u0642\u062A\u0628\u0627\u0646\u064A\u0629 \u0648\u062D\u0636\u0631\u0645\u064A\u0629 \u0648\u0645\u0639\u064A\u0646\u064A\u0629 \u0648\u0627\u0644\u0642\u0633\u0645 \u0627\u0644\u0622\u062E\u0631 \n" +
                "\u0647\u0648 \u0639\u0631\u0628\u064A\u0629 \u0634\u0645\u0627\u0644\u064A\u0629 \u0642\u062F\u064A\u0645\u0629 \u0648\u062A\u0634\u0645\u0644 \u0627\u0644\u062D\u0633\u0627\u0626\u064A\u0629 \u0648\u0627\u0644\u0635\u0641\u0627\u0626\u064A\u0629 \u0648\u0644\u063A\u0629 \u0644\u062D\u064A\u0627\u0646\u064A\u0629\ufffd\u062F\u064A\u062F\u0627\u0646\u064A\u0629 \u0648\u062B\u0645\u0648\u062F\u064A\u0629 \ufffd\u0644\u0627 \u0639\u0644\u0627\u0642\u0629 \u0644\u0647\u0627 \u0628\u062B\u0645\u0648\u062F \u0625\u0646\u0645\u0627 \u0647\u064A \u062A\u0633\u0645\u064A\u0629 \n" +
                "\u0627\u0635\u0637\u0644\u0627\u062D\u064A\u0629\ufffd \u0648\u0627\u0644\u062A\u064A\u0645\u0627\u0626\u064A\u0629 \u0643\u0627\u0646 \u0627\u0644\u0639\u0631\u0628 \u0627\u0644\u062C\u0646\u0648\u0628\u064A\u0648\u0646 \u064A\u0633\u062A\u0639\u0645\u0644\u0648\u0646 \u0627\u0644\u062D\u0631\u0641 \u0646\u0648\u0646 \u0643\u0623\u062F\u0627\u0629 \u0644\u0644\u062A\u0639\u0631\u064A\u0641 \u0648\u064A\u0636\u0639\u0648\u0646\u0647 \u0622\u062E\u0631 \u0627\u0644\u0643\u0644\u0645\u0629 \u0628\u064A\u0646\u0645\u0627 \u0627\u0644\u0639\u0631\u0628 \u0627\u0644\u0634\u0645\u0627\u0644\u064A\u0648\u0646 \n" +
                "\u0627\u0633\u062A\u0639\u0645\u0644\u0648\u0627 \u0627\u0644\u062D\u0631\u0641 \u0647\u0627\u0621 \u0643\u0623\u062F\u0627\u0629 \u0644\u0644\u062A\u0639\u0631\u064A\u0641 \u0648\u0645\u0627\u064A\u064F\u0645\u064A\u0651\u0632 \u0627\u0644\u0639\u0631\u0628\u064A\u0629 \ufffd\u0627\u0644\u0641\u0635\u062D\u0649\ufffd \u0639\u0646 \u0647\u0630\u0647 \u0627\u0644\u0644\u063A\u0627\u062A \u0647\u0648 \u0627\u0633\u062A\u0639\u0645\u0627\u0644\u0647\u0627 \u0644\u0623\u062F\u0627\u0629 \u0627\u0644\u062A\u0639\u0631\u064A\u0641";

        String actualText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new LocationTextExtractionStrategy().setRightToLeftRunDirection(true));

        Assertions.assertEquals(expected, actualText);
    }

    @Test
    // Again not completely correct. see test04()
    //TODO DEVSIX-2648
    public void test02() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "in02.pdf"));

        final String[] expectedText = new String[]{
                "1879 ",
                "\u05D4\u05D0\u05D5\u05E4\u05E0\u05D5\u05E2",
                ")\u05D2\u05D5\u05D8\u05DC\u05D9\u05D1 \u05D3\u05D9\u05D9\u05DE\u05DC\u05E8 \u05D5\u05D5\u05D9\u05DC\u05D4\u05DC\u05DD \u05DE\u05D9\u05D9\u05D1\u05D0\u05DA,1885 (,"
        };
        final Rectangle[] regions = new Rectangle[]{
                new Rectangle(493, 765, 23, 11),
                new Rectangle(522, 784, 38, 12),
                new Rectangle(332, 784, 185, 12)
        };

        final TextRegionEventFilter[] regionFilters = new TextRegionEventFilter[regions.length];
        for (int i = 0; i < regions.length; i++)
            regionFilters[i] = new TextRegionEventFilter(regions[i]);

        FilteredEventListener listener = new FilteredEventListener();
        LocationTextExtractionStrategy[] extractionStrategies = new LocationTextExtractionStrategy[regions.length];
        for (int i = 0; i < regions.length; i++)
            extractionStrategies[i] = listener.attachEventListener(new LocationTextExtractionStrategy().setRightToLeftRunDirection(true), regionFilters[i]);

        new PdfCanvasProcessor(listener).processPageContent(pdfDocument.getPage(1));

        for (int i = 0; i < regions.length; i++) {
            String actualText = extractionStrategies[i].getResultantText();
            Assertions.assertEquals(expectedText[i], actualText);
        }
    }

    @Test
    // This is an example from the spec section 14.8.2.5.3. Not actually bidi, just reversedChars
    public void test03() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "in03.pdf"));

        // Not completely correct according to the spec explanation, but in this case LocationTextExtractionStrategy is used meaning
        // that the text chunks will be sorted according to their location. At least /ReversedChars is respected, which
        // is not the case when you try to copy this text from Acrobat.
        String expectedText = " world .  Hello ";

        String actualText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new LocationTextExtractionStrategy());

        Assertions.assertEquals(expectedText, actualText);
    }

    @Test
    // Not completely correct. The brackets are different. They are extracted like in visual representation, not in logical one
    // as Acrobat does. We are working as Foxit or Chrome here. Acrobat seems to try to apply reverse of bidi algorithm.
    public void test04() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "in04.pdf"));

        String expectedText = ")\u05E2\u05DC \u05E9\u05DD \u05DE\u05E4\u05EA\u05D7\u05D9\u05D5(";

        String actualText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new LocationTextExtractionStrategy().setRightToLeftRunDirection(true));

        Assertions.assertEquals(expectedText, actualText);
    }

    @Test
    @Disabled("see DEVSIX-854")
    // Not correct since iText cannot detect reordering automatically when no /ReversedChars is present.
    public void test05() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "in05.pdf"));

        String expectedText = "\u0642\u0633\u0651\u0645 \u0639\u0644\u0645\u0627\u0621 \u0627\u0644\u0622\u062B\u0627\u0631";

        String actualText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new LocationTextExtractionStrategy().setRightToLeftRunDirection(true));

        Assertions.assertEquals(expectedText, actualText);
    }

}
