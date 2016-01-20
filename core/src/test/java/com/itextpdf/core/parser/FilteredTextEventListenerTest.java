package com.itextpdf.core.parser;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FilteredTextEventListenerTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/core/parser/FilteredTextEventListenerTest/";

    @Test
    public void testRegion() throws Exception {
        PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "in.pdf"));
        float pageHeight = doc.getPage(1).getPageSize().getHeight();
        Rectangle upperLeft = new Rectangle(0, (int) pageHeight - 30, 250, (int) pageHeight);

        Assert.assertTrue(textIsInRectangle(doc, "Upper Left", upperLeft));
        Assert.assertFalse(textIsInRectangle(doc, "Upper Right", upperLeft));
    }

    private boolean textIsInRectangle(PdfDocument doc, String text, Rectangle rect) throws Exception {
        FilteredTextEventListener filterListener = new FilteredTextEventListener(new LocationTextExtractionStrategy(), new TextRegionEventFilter(rect));
        String extractedText = TextExtractor.getTextFromPage(doc.getPage(1), filterListener);
        return extractedText.equals(text);
    }

}
