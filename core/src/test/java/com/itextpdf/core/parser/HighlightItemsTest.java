package com.itextpdf.core.parser;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.color.Color;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class HighlightItemsTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/core/parser/HighlightItemsTest/";
    private static final String outputPath = "./target/com/itextpdf/core/parser/HighlightItemsTest/";

    @Before
    public void setUp() {
        new File(outputPath).mkdirs();
    }

    @Test
    public void highlightPage229() throws IOException, InterruptedException {
        String input = sourceFolder + "page229.pdf";
        String output = outputPath + "page229.pdf";
        String cmp = sourceFolder + "cmp_page229.pdf";
        parseAndHighlight(input, output, false);
        Assert.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightCharactersPage229() throws IOException, InterruptedException {
        String input = sourceFolder + "page229.pdf";
        String output = outputPath + "page229_characters.pdf";
        String cmp = sourceFolder + "cmp_page229_characters.pdf";
        parseAndHighlight(input, output, true);
        Assert.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightIsoTc171() throws IOException, InterruptedException {
        String input = sourceFolder + "ISO-TC171-SC2_N0896_SC2WG5_Edinburgh_Agenda.pdf";
        String output = outputPath + "SC2_N0896_SC2WG5_Edinburgh_Agenda.pdf";
        String cmp = sourceFolder + "cmp_ISO-TC171-SC2_N0896_SC2WG5_Edinburgh_Agenda.pdf";
        parseAndHighlight(input, output, false);
        Assert.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightCharactersIsoTc171() throws IOException, InterruptedException {
        String input = sourceFolder + "ISO-TC171-SC2_N0896_SC2WG5_Edinburgh_Agenda.pdf";
        String output = outputPath + "ISO-TC171-SC2_N0896_SC2WG5_Edinburgh_Agenda_characters.pdf";
        String cmp = sourceFolder + "cmp_ISO-TC171-SC2_N0896_SC2WG5_Edinburgh_Agenda_characters.pdf";
        parseAndHighlight(input, output, true);
        Assert.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightHeaderFooter() throws IOException, InterruptedException {
        String input = sourceFolder + "HeaderFooter.pdf";
        String output = outputPath + "HeaderFooter.pdf";
        String cmp = sourceFolder + "cmp_HeaderFooter.pdf";
        parseAndHighlight(input, output, false);
        Assert.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightCharactersHeaderFooter() throws IOException, InterruptedException {
        String input = sourceFolder + "HeaderFooter.pdf";
        String output = outputPath + "HeaderFooter_characters.pdf";
        String cmp = sourceFolder + "cmp_HeaderFooter_characters.pdf";
        parseAndHighlight(input, output, true);
        Assert.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    private void parseAndHighlight(String input, String output, boolean singleCharacters) throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(input), new PdfWriter(output));

        MyEventListener myEventListener = singleCharacters ? new MyCharacterEventListener() : new MyEventListener();
        PdfContentStreamProcessor parser = new PdfContentStreamProcessor(myEventListener);
        for (int pageNum = 1; pageNum <= pdfDocument.getNumberOfPages(); pageNum++) {
            parser.processPageContent(pdfDocument.getPage(pageNum));
            List<Rectangle> rectangles = myEventListener.getRectangles();
            PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(pageNum));
            canvas.setLineWidth(0.5f);
            canvas.setStrokeColor(Color.RED);
            for (Rectangle rectangle : rectangles) {
                canvas.rectangle(rectangle.getLeft(), rectangle.getBottom(), rectangle.getWidth(), rectangle.getHeight());
                canvas.stroke();
            }
        }

        pdfDocument.close();
    }

    static class MyEventListener implements EventListener {
        private List<Rectangle> rectangles = new ArrayList<>();

        @Override
        public void eventOccurred(EventData data, EventType type) {
            if (type == EventType.RENDER_TEXT) {
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
            return new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_TEXT));
        }

        public List<Rectangle> getRectangles() {
            return rectangles;
        }
    }

    static class MyCharacterEventListener extends MyEventListener {
        @Override
        public void eventOccurred(EventData data, EventType type) {
            if (type == EventType.RENDER_TEXT) {
                TextRenderInfo renderInfo = (TextRenderInfo) data;
                for (TextRenderInfo tri : renderInfo.getCharacterRenderInfos()) {
                    super.eventOccurred(tri, type);
                }
            }
        }
    }

}
