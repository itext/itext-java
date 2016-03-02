package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class TextMarginFinderTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/TextMarginFinderTest/";

    @Test
    public void test() throws Exception {
        TextMarginFinder finder = new TextMarginFinder();
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "in.pdf"));
        new PdfCanvasProcessor(finder).processPageContent(pdfDocument.getPage(1));

        Rectangle textRect = finder.getTextRectangle();
        Assert.assertEquals(1.42f * 72f, textRect.getX(), 0.01f);
        Assert.assertEquals(7.42f * 72f, textRect.getX() + textRect.getWidth(), 0.01f);
        Assert.assertEquals(2.42f * 72f, textRect.getY(), 0.01f);
        Assert.assertEquals(10.42f * 72f, textRect.getY() + textRect.getHeight(), 0.01f);
    }
}
