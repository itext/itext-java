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
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.GlyphEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.GlyphTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
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
        Assertions.assertEquals("1234\nt5678", extractedText);
    }

    @Test
    public void test02() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "Sample.pdf"));

        String extractedText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1),
                new GlyphTextEventListener(new FilteredTextEventListener(new LocationTextExtractionStrategy(),
                        new TextRegionEventFilter(new Rectangle(111, 855, 25, 12)))));
        Assertions.assertEquals("Your ", extractedText);
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

        Assertions.assertEquals("Your", region1Listener.getResultantText());
        Assertions.assertEquals("dju", region2Listener.getResultantText());
    }

}
