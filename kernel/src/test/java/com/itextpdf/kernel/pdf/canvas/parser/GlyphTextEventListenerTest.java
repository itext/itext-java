package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.GlyphEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.GlyphTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.TextExtractionStrategy;
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
        TextExtractionStrategy region1Listener = listener.attachEventListener(new LocationTextExtractionStrategy(),
                new TextRegionEventFilter(new Rectangle(x1, y1, x2, y2)));

        x1 = 156;
        x2 = 13;
        y1 = 678.9f;
        y2 = 12;
        TextExtractionStrategy region2Listener = listener.attachEventListener(new LocationTextExtractionStrategy(),
                new TextRegionEventFilter(new Rectangle(x1, y1, x2, y2)));

        PdfCanvasProcessor parser = new PdfCanvasProcessor(new GlyphEventListener(listener));
        parser.processPageContent(pdfDocument.getPage(1));

        Assert.assertEquals("Your", region1Listener.getResultantText());
        Assert.assertEquals("dju", region2Listener.getResultantText());
    }

}
