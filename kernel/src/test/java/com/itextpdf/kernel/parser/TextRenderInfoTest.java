package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class TextRenderInfoTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/TextRenderInfoTest/";

    public static final int FIRST_PAGE = 1;
    public static final int FIRST_ELEMENT_INDEX = 0;

    @Test
    public void testCharacterRenderInfos() throws Exception {
        PdfContentStreamProcessor parser = new PdfContentStreamProcessor(new CharacterPositionEventListener());
        parser.processPageContent(new PdfDocument(new PdfReader(sourceFolder + "simple_text.pdf")).getPage(FIRST_PAGE));
    }

    /**
     * Test introduced to exclude a bug related to a Unicode quirk for
     * Japanese. TextRenderInfo threw an AIOOBE for some characters.
     *
     * @throws java.lang.Exception
     */
    @Test
//    @Ignore("Seems to be a problem with decode")
    public void testUnicodeEmptyString() throws Exception {
        StringBuilder sb = new StringBuilder();
        String inFile = "japanese_text.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + inFile));
        TextExtractionStrategy start = new SimpleTextExtractionStrategy();

        sb.append(PdfTextExtractor.getTextFromPage(pdfDocument.getPage(FIRST_PAGE), start));

        String result = sb.substring(0, sb.indexOf("\n"));
        String origText =
                "\u76f4\u8fd1\u306e\u0053\uff06\u0050\u0035\u0030\u0030"
                        + "\u914d\u5f53\u8cb4\u65cf\u6307\u6570\u306e\u30d1\u30d5"
                        + "\u30a9\u30fc\u30de\u30f3\u30b9\u306f\u0053\uff06\u0050"
                        + "\u0035\u0030\u0030\u6307\u6570\u3092\u4e0a\u56de\u308b";
        Assert.assertEquals(origText, result);
    }

    @Test
    public void testType3FontWidth() throws Exception {
        String inFile = "type3font_text.pdf";
        LineSegment origLineSegment = new LineSegment(new Vector(20.3246f, 769.4974f, 1.0f), new Vector(151.22923f, 769.4974f, 1.0f));

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + inFile));
        TextPositionEventListener renderListener = new TextPositionEventListener();
        PdfContentStreamProcessor processor = new PdfContentStreamProcessor(renderListener);

        processor.processPageContent(pdfDocument.getPage(FIRST_PAGE));

        Assert.assertEquals(renderListener.getLineSegments().get(FIRST_ELEMENT_INDEX).getStartPoint().get(FIRST_ELEMENT_INDEX),
                origLineSegment.getStartPoint().get(FIRST_ELEMENT_INDEX), 1 / 2f);

        Assert.assertEquals(renderListener.getLineSegments().get(FIRST_ELEMENT_INDEX).getEndPoint().get(FIRST_ELEMENT_INDEX),
                origLineSegment.getEndPoint().get(FIRST_ELEMENT_INDEX), 1 / 2f);
    }


    private static class TextPositionEventListener implements EventListener {
        List<LineSegment> lineSegments = new ArrayList<>();

        @Override
        public void eventOccurred(EventData data, EventType type) {
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

    private static class CharacterPositionEventListener implements TextExtractionStrategy {

        @Override
        public String getResultantText() {
            return null;
        }

        @Override
        public void eventOccurred(EventData data, EventType type) {
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
            Assert.assertEquals(message, v1.get(0), v2.get(0), 1 / 72f);
            Assert.assertEquals(message, v1.get(1), v2.get(1), 1 / 72f);
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_TEXT));
        }
    }

}
