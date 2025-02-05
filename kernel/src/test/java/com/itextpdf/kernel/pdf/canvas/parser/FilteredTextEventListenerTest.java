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
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class FilteredTextEventListenerTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/FilteredTextEventListenerTest/";

    @Test
    public void testRegion() throws Exception {
        PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "in.pdf"));
        float pageHeight = doc.getPage(1).getPageSize().getHeight();
        Rectangle upperLeft = new Rectangle(0, (int) pageHeight - 30, 250, (int) pageHeight);

        Assertions.assertTrue(textIsInRectangle(doc, "Upper Left", upperLeft));
        Assertions.assertFalse(textIsInRectangle(doc, "Upper Right", upperLeft));
    }

    private boolean textIsInRectangle(PdfDocument doc, String text, Rectangle rect) {
        FilteredTextEventListener filterListener = new FilteredTextEventListener(new LocationTextExtractionStrategy(), new TextRegionEventFilter(rect));
        String extractedText = PdfTextExtractor.getTextFromPage(doc.getPage(1), filterListener);
        return extractedText.equals(text);
    }

}
