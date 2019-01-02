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
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.GlyphEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.GlyphTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
    public class GlyphTextEventListenerTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/GlyphTextEventListenerTest/";

    @Test
    public void test01() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "test.pdf"));

        float x1, y1, x2, y2;
        x1 = 203;
        x2 = 21;
        y1 = 749;
        y2 = 49;
        String extractedText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1),
                new GlyphTextEventListener(new FilteredTextEventListener(new LocationTextExtractionStrategy(),
                        new TextRegionEventFilter(new Rectangle(x1, y1, x2, y2)))));
        Assert.assertEquals("1234\nt5678", extractedText);
    }

    @Test
    public void test02() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "Sample.pdf"));

        String extractedText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1),
                new GlyphTextEventListener(new FilteredTextEventListener(new LocationTextExtractionStrategy(),
                        new TextRegionEventFilter(new Rectangle(111, 855, 25, 12)))));
        Assert.assertEquals("Your ", extractedText);
    }

    @Test
    public void testWithMultiFilteredRenderListener() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "test.pdf"));

        float x1, y1, x2, y2;

        FilteredEventListener listener = new FilteredEventListener();
        x1 = 122;
        x2 = 22;
        y1 = 678.9f;
        y2 = 12;
        ITextExtractionStrategy region1Listener = listener.attachEventListener(new LocationTextExtractionStrategy(),
                new TextRegionEventFilter(new Rectangle(x1, y1, x2, y2)));

        x1 = 156;
        x2 = 13;
        y1 = 678.9f;
        y2 = 12;
        ITextExtractionStrategy region2Listener = listener.attachEventListener(new LocationTextExtractionStrategy(),
                new TextRegionEventFilter(new Rectangle(x1, y1, x2, y2)));

        PdfCanvasProcessor parser = new PdfCanvasProcessor(new GlyphEventListener(listener));
        parser.processPageContent(pdfDocument.getPage(1));

        Assert.assertEquals("Your", region1Listener.getResultantText());
        Assert.assertEquals("dju", region2Listener.getResultantText());
    }

}
