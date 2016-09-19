package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

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
public class HighlightItemsTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/HighlightItemsTest/";
    private static final String outputPath = "./target/test/com/itextpdf/kernel/parser/HighlightItemsTest/";

    @Before
    public void setUp() {
        createDestinationFolder(outputPath);
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

    @Test
    public void highlightReferencePage2Test() throws IOException, InterruptedException {
        String input = sourceFolder + "reference_page2.pdf";
        String output = outputPath + "reference_page2_characters.pdf";
        String cmp = sourceFolder + "cmp_reference_page2_characters.pdf";
        parseAndHighlight(input, output, true);
        Assert.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightReferencePage832Test() throws IOException, InterruptedException {
        String input = sourceFolder + "reference_page832.pdf";
        String output = outputPath + "reference_page832_characters.pdf";
        String cmp = sourceFolder + "cmp_reference_page832_characters.pdf";
        parseAndHighlight(input, output, true);
        Assert.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightReferencePage604Test() throws IOException, InterruptedException {
        String input = sourceFolder + "reference_page604.pdf";
        String output = outputPath + "reference_page604_characters.pdf";
        String cmp = sourceFolder + "cmp_reference_page604_characters.pdf";
        parseAndHighlight(input, output, true);
        Assert.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    @Test
    public void highlightNotDefTest() throws IOException, InterruptedException {
        String input = sourceFolder + "notdefWidth.pdf";
        String output = outputPath + "notdefWidth_highlighted.pdf";
        String cmp = sourceFolder + "cmp_notdefWidth.pdf";
        parseAndHighlight(input, output, false);
        Assert.assertEquals(null, new CompareTool().compareByContent(output, cmp, outputPath, "diff"));
    }

    private void parseAndHighlight(String input, String output, boolean singleCharacters) throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(input), new PdfWriter(output));

        MyEventListener myEventListener = singleCharacters ? new MyCharacterEventListener() : new MyEventListener();
        PdfDocumentContentParser parser = new PdfDocumentContentParser(pdfDocument);
        for (int pageNum = 1; pageNum <= pdfDocument.getNumberOfPages(); pageNum++) {
            parser.processContent(pageNum, myEventListener);
            List<Rectangle> rectangles = myEventListener.getRectangles();
            PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(pageNum));
            canvas.setLineWidth(0.5f);
            canvas.setStrokeColor(Color.RED);
            for (Rectangle rectangle : rectangles) {
                canvas.rectangle(rectangle);
                canvas.stroke();
            }
            myEventListener.clear();
        }

        pdfDocument.close();
    }

    static class MyEventListener implements IEventListener {
        private List<Rectangle> rectangles = new ArrayList<>();

        @Override
        public void eventOccurred(IEventData data, EventType type) {
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

        public void clear() {
            rectangles.clear();
        }
    }

    static class MyCharacterEventListener extends MyEventListener {
        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type == EventType.RENDER_TEXT) {
                TextRenderInfo renderInfo = (TextRenderInfo) data;
                for (TextRenderInfo tri : renderInfo.getCharacterRenderInfos()) {
                    super.eventOccurred(tri, type);
                }
            }
        }
    }

}
