/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Category(IntegrationTest.class)
public class GlyphBboxCalculationTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/parser/GlyphBboxCalculationTest/";

    @Test
    public void checkBboxCalculationForType3FontsWithFontMatrix01() throws IOException {
        String inputPdf = sourceFolder + "checkBboxCalculationForType3FontsWithFontMatrix01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputPdf));
        CharacterPositionEventListener listener = new CharacterPositionEventListener();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(pdfDocument.getPage(1));
        // font size (36) * |fontMatrix| (0.001) * glyph width (600) = 21.6
        Assert.assertEquals(21.6, listener.glyphWith, 1e-5);
    }

    @Test
    @Ignore("DEVSIX-3343")
    public void checkBboxCalculationForType3FontsWithFontMatrix02() throws IOException {
        String inputPdf = sourceFolder + "checkBboxCalculationForType3FontsWithFontMatrix02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputPdf));
        CharacterPositionEventListener listener = new CharacterPositionEventListener();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(pdfDocument.getPage(1));
        // font size (36) * |fontMatrix| (1) * glyph width (0.6) = 21.6
        Assert.assertEquals(21.6, listener.glyphWith, 1e-5);
    }

    private static class CharacterPositionEventListener implements ITextExtractionStrategy {
        float glyphWith;

        @Override
        public String getResultantText() {
            return null;
        }

        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type.equals(EventType.RENDER_TEXT)) {
                TextRenderInfo renderInfo = (TextRenderInfo) data;
                List<TextRenderInfo> subs = renderInfo.getCharacterRenderInfos();
                for (int i = 0; i < subs.size(); i++) {
                    TextRenderInfo charInfo = subs.get(i);
                    glyphWith = charInfo.getBaseline().getLength();
                }
            }
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_TEXT));
        }
    }

}
