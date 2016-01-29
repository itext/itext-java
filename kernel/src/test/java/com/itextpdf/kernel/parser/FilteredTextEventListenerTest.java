package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FilteredTextEventListenerTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/FilteredTextEventListenerTest/";

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
        String extractedText = PdfTextExtractor.getTextFromPage(doc.getPage(1), filterListener);
        return extractedText.equals(text);
    }

}
