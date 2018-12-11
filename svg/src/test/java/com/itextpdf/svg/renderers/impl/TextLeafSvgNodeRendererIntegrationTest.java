package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class TextLeafSvgNodeRendererIntegrationTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void getContentLengthBaseTest() throws Exception {
        TextLeafSvgNodeRenderer toTest = new TextLeafSvgNodeRenderer();
        toTest.setAttribute(SvgConstants.Attributes.TEXT_CONTENT, "Hello");
        toTest.setAttribute(SvgConstants.Attributes.FONT_SIZE, "10");
        PdfFont font = PdfFontFactory.createFont();
        float actual = toTest.getTextContentLength(12, font);
        float expected = 22.78f;
        Assert.assertEquals(expected, actual, 1e-6f);
    }

    @Test
    public void getContentLengthNoValueTest() throws Exception {
        TextLeafSvgNodeRenderer toTest = new TextLeafSvgNodeRenderer();
        toTest.setAttribute(SvgConstants.Attributes.TEXT_CONTENT, "Hello");
        PdfFont font = PdfFontFactory.createFont();
        float actual = toTest.getTextContentLength(12, font);
        float expected = 27.336f;
        Assert.assertEquals(expected, actual,1e-6f);
    }

    @Test
    public void getContentLengthNaNTest() throws Exception {
        junitExpectedException.expect(StyledXMLParserException.class);
        TextLeafSvgNodeRenderer toTest = new TextLeafSvgNodeRenderer();
        toTest.setAttribute(SvgConstants.Attributes.TEXT_CONTENT, "Hello");
        toTest.setAttribute(SvgConstants.Attributes.FONT_SIZE, "spice");
        PdfFont font = PdfFontFactory.createFont();
        float actual = toTest.getTextContentLength(12, font);
        float expected = 27.336f;
        Assert.assertEquals(expected, actual, 1e-6f);
    }

    @Test
    public void getContentLengthNegativeTest() throws Exception {
        TextLeafSvgNodeRenderer toTest = new TextLeafSvgNodeRenderer();
        toTest.setAttribute(SvgConstants.Attributes.TEXT_CONTENT, "Hello");
        toTest.setAttribute(SvgConstants.Attributes.FONT_SIZE, "-10");
        PdfFont font = PdfFontFactory.createFont();
        float actual = toTest.getTextContentLength(12, font);
        float expected = 27.336f;
        Assert.assertEquals(expected, actual,1e-6f);
    }
}
