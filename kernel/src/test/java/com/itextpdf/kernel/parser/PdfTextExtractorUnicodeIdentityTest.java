package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfTextExtractorUnicodeIdentityTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/PdfTextExtractorUnicodeIdentityTest/";

    @Test
    public void test() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "user10.pdf"));

        Rectangle rectangle = new Rectangle(71, 708, 154, 9);
        EventFilter filter = new TextRegionEventFilter(rectangle);
        String txt = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new FilteredTextEventListener(new LocationTextExtractionStrategy(), filter));
        Assert.assertEquals("Pname Dname Email Address", txt);
    }

}
