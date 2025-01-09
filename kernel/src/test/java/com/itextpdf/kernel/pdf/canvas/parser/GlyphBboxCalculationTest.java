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

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Tag("IntegrationTest")
public class GlyphBboxCalculationTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/parser/GlyphBboxCalculationTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/canvas/parser/GlyphBboxCalculationTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    public void checkBboxCalculationForType3FontsWithFontMatrix01() throws IOException {
        String inputPdf = sourceFolder + "checkBboxCalculationForType3FontsWithFontMatrix01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputPdf));
        CharacterPositionEventListener listener = new CharacterPositionEventListener();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(pdfDocument.getPage(1));
        // font size (36) * |fontMatrix| (0.001) * glyph width (600) = 21.6
        Assertions.assertEquals(21.6, listener.glyphWidth, 1e-5);
    }

    @Test
    public void checkBboxCalculationForType3FontsWithFontMatrix02() throws IOException {
        String inputPdf = sourceFolder + "checkBboxCalculationForType3FontsWithFontMatrix02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputPdf));
        CharacterPositionEventListener listener = new CharacterPositionEventListener();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(pdfDocument.getPage(1));
        // font size (36) * |fontMatrix| (1) * glyph width (0.6) = 21.6
        Assertions.assertEquals(21.6, listener.glyphWidth, 1e-5);
    }

    @Test
    public void checkAverageBboxCalculationForType3FontsWithFontMatrix01Test() throws IOException {
        String inputPdf = sourceFolder + "checkAverageBboxCalculationForType3FontsWithFontMatrix01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputPdf));
        CharacterPositionEventListener listener = new CharacterPositionEventListener();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(pdfDocument.getPage(1));
        Assertions.assertEquals(600, listener.firstTextRenderInfo.getFont().getFontProgram().getAvgWidth(), 0.01f);
    }

    @Test
    public void type3FontsWithIdentityFontMatrixAndMultiplier() throws IOException, InterruptedException {
        String inputPdf = sourceFolder + "type3FontsWithIdentityFontMatrixAndMultiplier.pdf";
        String outputPdf = destinationFolder +  "type3FontsWithIdentityFontMatrixAndMultiplier.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputPdf), CompareTool.createTestPdfWriter(outputPdf));
        CharacterPositionEventListener listener = new CharacterPositionEventListener();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(pdfDocument.getPage(1));

        PdfPage page = pdfDocument.getPage(1);
        Rectangle pageSize = page.getPageSize();
        PdfCanvas pdfCanvas = new PdfCanvas(page);

        pdfCanvas.beginText().setFontAndSize(processor.getGraphicsState().getFont(), processor.getGraphicsState().getFontSize())
                .moveText(pageSize.getWidth() / 2 - 24, pageSize.getHeight() / 2)
                .showText("A")
                .endText();

        pdfDocument.close();
        Assertions.assertNull(new CompareTool().compareByContent(outputPdf, sourceFolder + "cmp_type3FontsWithIdentityFontMatrixAndMultiplier.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void type3FontCustomFontMatrixAndFontBBoxTest() throws IOException {
        String inputPdf = sourceFolder + "type3FontCustomFontMatrixAndFontBBox.pdf";

        // Resultant rectangle is expected to be a bounding box over the text on the page.
        Rectangle expectedRectangle = new Rectangle(10f, 97.84f, 14.400002f, 8.880005f);
        List<Rectangle> actualRectangles;

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputPdf))) {
            TextBBoxEventListener eventListener = new TextBBoxEventListener();
            PdfCanvasProcessor canvasProcessor = new PdfCanvasProcessor(eventListener);

            PdfPage page = pdfDoc.getPage(1);
            canvasProcessor.processPageContent(page);

            actualRectangles = eventListener.getRectangles();
        }

        Assertions.assertEquals(1, actualRectangles.size());
        Assertions.assertTrue(expectedRectangle.equalsWithEpsilon(actualRectangles.get(0)));
    }

    private static class CharacterPositionEventListener implements ITextExtractionStrategy {
        float glyphWidth;
        TextRenderInfo firstTextRenderInfo;

        @Override
        public String getResultantText() {
            return null;
        }

        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type.equals(EventType.RENDER_TEXT)) {
                TextRenderInfo renderInfo = (TextRenderInfo) data;
                if (firstTextRenderInfo == null) {
                    firstTextRenderInfo = renderInfo;
                    firstTextRenderInfo.preserveGraphicsState();
                }
                List<TextRenderInfo> subs = renderInfo.getCharacterRenderInfos();
                for (int i = 0; i < subs.size(); i++) {
                    TextRenderInfo charInfo = subs.get(i);
                    glyphWidth = charInfo.getBaseline().getLength();
                }
            }
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_TEXT));
        }
    }

    private static class TextBBoxEventListener implements IEventListener {
        private final List<Rectangle> rectangles = new ArrayList<>();

        public List<Rectangle> getRectangles() {
            return rectangles;
        }

        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (EventType.RENDER_TEXT.equals(type)) {
                TextRenderInfo renderInfo = (TextRenderInfo) data;
                Vector startPoint = renderInfo.getDescentLine().getStartPoint();
                Vector endPoint = renderInfo.getAscentLine().getEndPoint();
                float x1 = Math.min(startPoint.get(0), endPoint.get(0));
                float x2 = Math.max(startPoint.get(0), endPoint.get(0));
                float y1 = Math.min(startPoint.get(1), endPoint.get(1));
                float y2 = Math.max(startPoint.get(1), endPoint.get(1));
                rectangles.add(new Rectangle(x1, y1, x2 - x1, y2 - y1));
            }
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return null;
        }
    }

}
