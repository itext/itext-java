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
