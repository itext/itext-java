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
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
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
            Assert.assertEquals(expectedText[i], actualText);
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

        Assert.assertEquals(expectedText, extractedText);
    }

}
