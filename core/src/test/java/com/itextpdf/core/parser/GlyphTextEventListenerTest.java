package com.itextpdf.core.parser;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class GlyphTextEventListenerTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/core/parser/GlyphTextEventListenerTest/";

    @Test
    public void test01() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "test.pdf"));

        float x1, y1, x2, y2;
        x1 = 203;
        x2 = 224;
        y1 = 842 - 44;
        y2 = 842 - 93;
        String extractedText = TextExtractor.getTextFromPage(pdfDocument.getPage(1),
                new GlyphTextEventListener(new FilteredTextEventListener(new LocationTextExtractionStrategy(),
                        new TextRegionEventFilter(new Rectangle(x1, y1, x2, y2)))));
        Assert.assertEquals("1234\nt5678", extractedText);
    }

    @Test
    public void test02() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "Sample.pdf"));

        String extractedText = TextExtractor.getTextFromPage(pdfDocument.getPage(1),
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
        x2 = 144;
        y1 = 841.9f - 151;
        y2 = 841.9f - 163;
        TextExtractionStrategy region1Listener = listener.attachEventListener(new LocationTextExtractionStrategy(),
                new TextRegionEventFilter(new Rectangle(x1, y1, x2, y2)));

        x1 = 156;
        x2 = 169;
        y1 = 841.9f - 151;
        y2 = 841.9f - 163;
        TextExtractionStrategy region2Listener = listener.attachEventListener(new LocationTextExtractionStrategy(),
                new TextRegionEventFilter(new Rectangle(x1, y1, x2, y2)));

        PdfContentStreamProcessor parser = new PdfContentStreamProcessor(new GlyphEventListener(listener));
        parser.processPageContent(pdfDocument.getPage(1));

        Assert.assertEquals("Your", region1Listener.getResultantText());
        Assert.assertEquals("dju", region2Listener.getResultantText());
    }

}
