package com.itextpdf.core.parser;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FilteredEventListenerTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/core/parser/FilteredEventListenerTest/";

    @Test
    public void test() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "test.pdf")));

        final String expectedText[] = new String[]{
                "PostScript Compatibility",
                "Because the PostScript language does not support the transparent imaging \n" +
                        "model, PDF 1.4 consumer applications must have some means for converting the \n" +
                        "appearance of a document that uses transparency to a purely opaque description \n" +
                        "for printing on PostScript output devices. Similar techniques can also be used to \n" +
                        "convert such documents to a form that can be correctly viewed by PDF 1.3 and \n" +
                        "earlier consumers. ",
                "Otherwise, flatten the colors to some assumed device color space with pre-\n" +
                        "determined calibration. In the generated PostScript output, paint the flattened \n" +
                        "colors in a CIE-based color space having that calibration. "};

        final Rectangle[] regions = new Rectangle[]{new Rectangle(90, 581, 130, 24),
                new Rectangle(80, 486, 370, 92), new Rectangle(103, 143, 357, 53)};

        final TextRegionEventFilter[] regionFilters = new TextRegionEventFilter[regions.length];
        for (int i = 0; i < regions.length; i++)
            regionFilters[i] = new TextRegionEventFilter(regions[i]);


        FilteredEventListener listener = new FilteredEventListener();
        LocationTextExtractionStrategy[] extractionStrategies = new LocationTextExtractionStrategy[regions.length];
        for (int i = 0; i < regions.length; i++)
            extractionStrategies[i] = listener.attachEventListener(new LocationTextExtractionStrategy(), regionFilters[i]);

        new PdfContentStreamProcessor(listener).processPageContent(pdfDocument.getPage(1));

        for (int i = 0; i < regions.length; i++) {
            String actualText = extractionStrategies[i].getResultantText();
            Assert.assertEquals(expectedText[i], actualText);
        }
    }

    @Test
    public void multipleFiltersForOneRegionTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new FileInputStream(sourceFolder + "test.pdf")));

        final Rectangle[] regions = new Rectangle[]{new Rectangle(0, 0, 500, 650),
                new Rectangle(0, 0, 400, 400), new Rectangle(200, 200, 300, 400), new Rectangle(100, 100, 350, 300)};

        final TextRegionEventFilter[] regionFilters = new TextRegionEventFilter[regions.length];
        for (int i = 0; i < regions.length; i++)
            regionFilters[i] = new TextRegionEventFilter(regions[i]);

        FilteredEventListener listener = new FilteredEventListener();
        LocationTextExtractionStrategy extractionStrategy = listener.attachEventListener(new LocationTextExtractionStrategy(), regionFilters);
        new PdfContentStreamProcessor(listener).processPageContent(pdfDocument.getPage(1));
        String actualText = extractionStrategy.getResultantText();

        String expectedText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new FilteredTextEventListener(new LocationTextExtractionStrategy(), regionFilters));

        Assert.assertEquals(expectedText, actualText);
    }

}
