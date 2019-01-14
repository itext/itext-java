/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
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

        Assert.assertEquals(expected, actualText);
    }

    @Test
    // Again not completely correct. see test04()
    public void test02() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "in02.pdf"));

        final String[] expectedText = new String[]{
                "1879 ",
                "\u05D4\u05D0\u05D5\u05E4\u05E0\u05D5\u05E2",
                ")\u05D2\u05D5\u05D8\u05DC\u05D9\u05D1 \u05D3\u05D9\u05D9\u05DE\u05DC\u05E8 \u05D5\u05D5\u05D9\u05DC\u05D4\u05DC\u05DD \u05DE\u05D9\u05D9\u05D1\u05D0\u05DA, 1885 (,"
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
            Assert.assertEquals(expectedText[i], actualText);
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

        Assert.assertEquals(expectedText, actualText);
    }

    @Test
    // Not completely correct. The brackets are different. They are extracted like in visual representation, not in logical one
    // as Acrobat does. We are working as Foxit or Chrome here. Acrobat seems to try to apply reverse of bidi algorithm.
    public void test04() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "in04.pdf"));

        String expectedText = ")\u05E2\u05DC \u05E9\u05DD \u05DE\u05E4\u05EA\u05D7\u05D9\u05D5(";

        String actualText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new LocationTextExtractionStrategy().setRightToLeftRunDirection(true));

        Assert.assertEquals(expectedText, actualText);
    }

    @Test
    @Ignore("see DEVSIX-854")
    // Not correct since iText cannot detect reordering automatically when no /ReversedChars is present.
    public void test05() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "in05.pdf"));

        String expectedText = "\u0642\u0633\u0651\u0645 \u0639\u0644\u0645\u0627\u0621 \u0627\u0644\u0622\u062B\u0627\u0631";

        String actualText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new LocationTextExtractionStrategy().setRightToLeftRunDirection(true));

        Assert.assertEquals(expectedText, actualText);
    }

}
