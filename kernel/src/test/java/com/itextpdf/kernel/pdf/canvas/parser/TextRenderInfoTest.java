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

import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class TextRenderInfoTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/parser/TextRenderInfoTest/";

    public static final int FIRST_PAGE = 1;
    public static final int FIRST_ELEMENT_INDEX = 0;

    @Test
    public void testCharacterRenderInfos() throws Exception {
        PdfCanvasProcessor parser = new PdfCanvasProcessor(new CharacterPositionEventListener());
        parser.processPageContent(new PdfDocument(new PdfReader(SOURCE_FOLDER + "simple_text.pdf")).getPage(FIRST_PAGE));
    }

    /**
     * Test introduced to exclude a bug related to a Unicode quirk for
     * Japanese. TextRenderInfo threw an AIOOBE for some characters.
     */
    @Test
    public void testUnicodeEmptyString() throws Exception {
        StringBuilder sb = new StringBuilder();
        String inFile = "japanese_text.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFile));
        ITextExtractionStrategy start = new SimpleTextExtractionStrategy();

        sb.append(PdfTextExtractor.getTextFromPage(pdfDocument.getPage(FIRST_PAGE), start));

        String result = sb.substring(0, sb.toString().indexOf("\n"));
        String origText =
                "\u76f4\u8fd1\u306e\u0053\uff06\u0050\u0035\u0030\u0030"
                        + "\u914d\u5f53\u8cb4\u65cf\u6307\u6570\u306e\u30d1\u30d5"
                        + "\u30a9\u30fc\u30de\u30f3\u30b9\u306f\u0053\uff06\u0050"
                        + "\u0035\u0030\u0030\u6307\u6570\u3092\u4e0a\u56de\u308b";
        Assertions.assertEquals(origText, result);
    }

    @Test
    public void testType3FontWidth() throws Exception {
        String inFile = "type3font_text.pdf";
        LineSegment origLineSegment = new LineSegment(new Vector(20.3246f, 769.4974f, 1.0f), new Vector(151.22923f, 769.4974f, 1.0f));

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFile));
        TextPositionEventListener renderListener = new TextPositionEventListener();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(renderListener);

        processor.processPageContent(pdfDocument.getPage(FIRST_PAGE));

        Assertions.assertEquals(renderListener.getLineSegments().get(FIRST_ELEMENT_INDEX).getStartPoint().get(FIRST_ELEMENT_INDEX),
                origLineSegment.getStartPoint().get(FIRST_ELEMENT_INDEX), 1 / 2f);

        Assertions.assertEquals(renderListener.getLineSegments().get(FIRST_ELEMENT_INDEX).getEndPoint().get(FIRST_ELEMENT_INDEX),
                origLineSegment.getEndPoint().get(FIRST_ELEMENT_INDEX), 1 / 2f);
    }

    @Test
    public void testDoubleMappedCharacterExtraction() throws IOException {
        String inFile = "double_cmap_mapping.pdf";
        // TODO after fixing DEVSIX-6089 first hyphen should be 002D instead of 2011. The similar for the second line
        String expectedResult = "Regular hyphen [\u002D] and non-breaking hyphen [\u002D] (both CID 14)\n"
                + "Turtle kyuujitai [\u9f9c] and turtle radical [\u9f9c] (both CID 7472)";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFile));
        ITextExtractionStrategy strategy = new SimpleTextExtractionStrategy();

        String result = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(FIRST_PAGE), strategy).trim();
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testEmbeddedIdentityToUnicodeTest() throws IOException {
        String inFile = "embedded_identity_to_unicode.pdf";
        String expectedResult = "Regular hyphen [\u002d] and non-breaking hyphen [\u2011] (both CID 14)\n"
                + "Turtle kyuujitai [\u9f9c] and turtle radical [\u2fd4] (both CID 7472)";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFile));
        ITextExtractionStrategy start = new SimpleTextExtractionStrategy();

        String result = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(FIRST_PAGE), start).trim();
        Assertions.assertEquals(expectedResult, result);
    }

    private static class TextPositionEventListener implements IEventListener {
        List<LineSegment> lineSegments = new ArrayList<>();

        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type.equals(EventType.RENDER_TEXT)) {
                lineSegments.add(((TextRenderInfo) data).getBaseline());
            }
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_TEXT));
        }

        public List<LineSegment> getLineSegments() {
            return lineSegments;
        }
    }

    private static class CharacterPositionEventListener implements ITextExtractionStrategy {

        @Override
        public String getResultantText() {
            return null;
        }

        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type.equals(EventType.RENDER_TEXT)) {
                TextRenderInfo renderInfo = (TextRenderInfo) data;
                List<TextRenderInfo> subs = renderInfo.getCharacterRenderInfos();
                TextRenderInfo previousCharInfo = subs.get(0);

                for (int i = 1; i < subs.size(); i++) {
                    TextRenderInfo charInfo = subs.get(i);
                    Vector previousEndPoint = previousCharInfo.getBaseline().getEndPoint();
                    Vector currentStartPoint = charInfo.getBaseline().getStartPoint();
                    assertVectorsEqual(charInfo.getText(), previousEndPoint, currentStartPoint);
                    previousCharInfo = charInfo;
                }
            }
        }

        private void assertVectorsEqual(String message, Vector v1, Vector v2) {
            Assertions.assertEquals(v1.get(0), v2.get(0), 1 / 72f, message);
            Assertions.assertEquals(v1.get(1), v2.get(1), 1 / 72f, message);
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_TEXT));
        }
    }

}
