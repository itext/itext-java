/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.layout.properties.BlendMode;
import com.itextpdf.styledxmlparser.CommonAttributeConstants;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.pseudo.CssPseudoElementNode;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssUtilsTest extends ExtendedITextTest {
    private static float EPS = 0.0001f;

    @Test
    public void convertFloatMaximumToPdfTest() {
        float expected = Float.POSITIVE_INFINITY;
        float actual = CssUtils.convertPtsToPx(Float.MAX_VALUE);

        Assert.assertEquals(expected, actual, 0f);
    }

    @Test
    public void convertFloatMinimumToPdfTest() {
        float expected = 1.4E-45f;
        float actual = CssUtils.convertPtsToPx(Float.MIN_VALUE);

        Assert.assertEquals(expected, actual, 0f);
    }

    @Test
    public void extractShorthandPropertiesFromEmptyStringTest() {
        String sourceString = "";
        List<List<String>> expected = new ArrayList<>();
        expected.add(new ArrayList<String>());

        Assert.assertEquals(expected, CssUtils.extractShorthandProperties(sourceString));
    }

    @Test
    public void extractShorthandPropertiesFromStringWithOnePropertyTest() {
        String sourceString = "square inside url('sqpurple.gif')";
        List<List<String>> expected = new ArrayList<>();
        List<String> layer = new ArrayList<>();
        layer.add("square");
        layer.add("inside");
        layer.add("url('sqpurple.gif')");
        expected.add(layer);

        Assert.assertEquals(expected, CssUtils.extractShorthandProperties(sourceString));
    }

    @Test
    public void extractShorthandPropertiesFromStringWithMultiplyPropertiesTest() {
        String sourceString = "center no-repeat url('sqpurple.gif'), #eee 35% url('sqpurple.gif')";
        List<List<String>> expected = new ArrayList<>();
        List<String> layer = new ArrayList<>();
        layer.add("center");
        layer.add("no-repeat");
        layer.add("url('sqpurple.gif')");
        expected.add(layer);

        layer = new ArrayList<>();
        layer.add("#eee");
        layer.add("35%");
        layer.add("url('sqpurple.gif')");
        expected.add(layer);

        Assert.assertEquals(expected, CssUtils.extractShorthandProperties(sourceString));
    }

    @Test
    public void normalConvertPtsToPxTest() {
        float[] input = new float[] {-1f, 0f, 1f};
        float[] expected = new float[] {-1.3333334f, 0f, 1.3333334f};

        for (int i = 0; i < input.length; i++) {
            float actual = CssUtils.convertPtsToPx(input[i]);
            Assert.assertEquals(expected[i], actual, 0f);
        }
    }

    @Test
    public void normalizeProperty() {
        Assert.assertEquals("part1 part2", CssUtils.normalizeCssProperty("   part1   part2  "));
        Assert.assertEquals("\" the next quote is ESCAPED \\\\\\\" still  IN string \"", CssUtils.normalizeCssProperty("\" the next quote is ESCAPED \\\\\\\" still  IN string \""));
        Assert.assertEquals("\" the next quote is NOT ESCAPED \\\\\" not in the string", CssUtils.normalizeCssProperty("\" the next quote is NOT ESCAPED \\\\\" NOT in   THE string"));
        Assert.assertEquals("\" You CAN put 'Single  Quotes' in double quotes WITHOUT escaping\"", CssUtils.normalizeCssProperty("\" You CAN put 'Single  Quotes' in double quotes WITHOUT escaping\""));
        Assert.assertEquals("' You CAN put \"DOUBLE  Quotes\" in double quotes WITHOUT escaping'", CssUtils.normalizeCssProperty("' You CAN put \"DOUBLE  Quotes\" in double quotes WITHOUT escaping'"));
        Assert.assertEquals("\" ( BLA \" attr(href)\" BLA )  \"", CssUtils.normalizeCssProperty("\" ( BLA \"      AttR( Href  )\" BLA )  \""));
        Assert.assertEquals("\" (  \"attr(href) \"  )  \"", CssUtils.normalizeCssProperty("\" (  \"aTTr( hREf  )   \"  )  \""));
        Assert.assertEquals("rgba(255,255,255,0.2)", CssUtils.normalizeCssProperty("rgba(  255,  255 ,  255 ,0.2   )"));
    }

    @Test
    public void normalizeUrlTest() {
        Assert.assertEquals("url(data:application/font-woff;base64,2CBPCRXmgywtV1t4oWwjBju0kqkvfhPs0cYdMgFtDSY5uL7MIGT5wiGs078HrvBHekp0Yf=)",
                CssUtils.normalizeCssProperty("url(data:application/font-woff;base64,2CBPCRXmgywtV1t4oWwjBju0kqkvfhPs0cYdMgFtDSY5uL7MIGT5wiGs078HrvBHekp0Yf=)"));
        Assert.assertEquals("url(\"quoted  Url\")", CssUtils.normalizeCssProperty("  url(  \"quoted  Url\")"));
        Assert.assertEquals("url('quoted  Url')", CssUtils.normalizeCssProperty("  url(  'quoted  Url')"));
        Assert.assertEquals("url(haveEscapedEndBracket\\))", CssUtils.normalizeCssProperty("url(  haveEscapedEndBracket\\) )"));
    }

    @Test
    public void parseUnicodeRangeTest() {
        Assert.assertEquals("[(0; 1048575)]", CssUtils.parseUnicodeRange("U+?????").toString());
        Assert.assertEquals("[(38; 38)]", CssUtils.parseUnicodeRange("U+26").toString());
        Assert.assertEquals("[(0; 127)]", CssUtils.parseUnicodeRange(" U+0-7F").toString());
        Assert.assertEquals("[(37; 255)]", CssUtils.parseUnicodeRange("U+0025-00FF").toString());
        Assert.assertEquals("[(1024; 1279)]", CssUtils.parseUnicodeRange("U+4??").toString());
        Assert.assertEquals("[(262224; 327519)]", CssUtils.parseUnicodeRange("U+4??5?").toString());
        Assert.assertEquals("[(37; 255), (1024; 1279)]", CssUtils.parseUnicodeRange("U+0025-00FF, U+4??").toString());

        Assert.assertNull(CssUtils.parseUnicodeRange("U+??????")); // more than 5 question marks are not allowed
        Assert.assertNull(CssUtils.parseUnicodeRange("UU+7-10")); // wrong syntax
        Assert.assertNull(CssUtils.parseUnicodeRange("U+7?-9?")); // wrong syntax
        Assert.assertNull(CssUtils.parseUnicodeRange("U+7-")); // wrong syntax
    }

    @Test
    public void elementNodeIsStyleSheetLink() {
        Element element = new Element(Tag.valueOf("link"), "");
        element.attr(CommonAttributeConstants.REL, CommonAttributeConstants.STYLESHEET);
        JsoupElementNode elementNode = new JsoupElementNode(element);

        Assert.assertTrue(CssUtils.isStyleSheetLink(elementNode));
    }

    @Test
    public void elementNodeIsNotLink() {
        Element element = new Element(Tag.valueOf("p"), "");
        element.attr(CommonAttributeConstants.REL, CommonAttributeConstants.STYLESHEET);
        JsoupElementNode elementNode = new JsoupElementNode(element);

        Assert.assertFalse(CssUtils.isStyleSheetLink(elementNode));
    }

    @Test
    public void elementNodeAttributeIsNotStylesheet() {
        Element element = new Element(Tag.valueOf("link"), "");
        element.attr(CommonAttributeConstants.REL, "");
        JsoupElementNode elementNode = new JsoupElementNode(element);

        Assert.assertFalse(CssUtils.isStyleSheetLink(elementNode));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INCORRECT_CHARACTER_SEQUENCE))
    public void splitStringWithCommaTest() {
        Assert.assertEquals(new ArrayList<String>(), CssUtils.splitStringWithComma(null));
        Assert.assertEquals(Arrays.asList("value1", "value2", "value3"),
                CssUtils.splitStringWithComma("value1,value2,value3"));
        Assert.assertEquals(Arrays.asList("value1", " value2", " value3"),
                CssUtils.splitStringWithComma("value1, value2, value3"));
        Assert.assertEquals(Arrays.asList("value1", "(value,with,comma)", "value3"),
                CssUtils.splitStringWithComma("value1,(value,with,comma),value3"));
        Assert.assertEquals(Arrays.asList("value1", "(val(ue,with,comma),value3"),
                CssUtils.splitStringWithComma("value1,(val(ue,with,comma),value3"));
        Assert.assertEquals(Arrays.asList("value1", "(value,with)", "comma)", "value3"),
                CssUtils.splitStringWithComma("value1,(value,with),comma),value3"));
        Assert.assertEquals(Arrays.asList("value1", "( v2,v3)", "(v4, v5)", "value3"),
                CssUtils.splitStringWithComma("value1,( v2,v3),(v4, v5),value3"));
        Assert.assertEquals(Arrays.asList("v.al*ue1\"", "( v2,v3)", "\"(v4,v5;);", "value3"),
                CssUtils.splitStringWithComma("v.al*ue1\",( v2,v3),\"(v4,v5;);,value3"));
    }

    @Test
    public void splitStringTest() {
        Assert.assertEquals(new ArrayList<String>(), CssUtils.splitString(null, ','));
        Assert.assertEquals(Arrays.asList("value1", "(value,with,comma)", "value3"),
                CssUtils.splitString("value1,(value,with,comma),value3", ',', new EscapeGroup('(', ')')));
        Assert.assertEquals(Arrays.asList("value1 ", " (val(ue,with,comma),value3"),
                CssUtils.splitString("value1 , (val(ue,with,comma),value3", ',', new EscapeGroup('(', ')')));
        Assert.assertEquals(Arrays.asList("some text", " (some", " text in", " brackets)", " \"some, text, in quotes,\""),
                CssUtils.splitString("some text, (some, text in, brackets), \"some, text, in quotes,\"", ',',
                        new EscapeGroup('\"')));
        Assert.assertEquals(Arrays.asList("some text", " (some. text in. brackets)", " \"some. text. in quotes.\""),
                CssUtils.splitString("some text. (some. text in. brackets). \"some. text. in quotes.\"", '.',
                        new EscapeGroup('\"'), new EscapeGroup('(', ')')));
        Assert.assertEquals(Arrays.asList("value1", "(value", "with" ,"comma)", "value3"),
                CssUtils.splitString("value1,(value,with,comma),value3", ','));
        Assert.assertEquals(Arrays.asList("value1", "value", "with" ,"comma", "value3"),
                CssUtils.splitString("value1,value,with,comma,value3", ',', new EscapeGroup(',')));
    }

    @Test
    public void parseBlendModeTest() {
        Assert.assertEquals(BlendMode.NORMAL, CssUtils.parseBlendMode(null));
        Assert.assertEquals(BlendMode.NORMAL, CssUtils.parseBlendMode(CommonCssConstants.NORMAL));
        Assert.assertEquals(BlendMode.MULTIPLY, CssUtils.parseBlendMode(CommonCssConstants.MULTIPLY));
        Assert.assertEquals(BlendMode.SCREEN, CssUtils.parseBlendMode(CommonCssConstants.SCREEN));
        Assert.assertEquals(BlendMode.OVERLAY, CssUtils.parseBlendMode(CommonCssConstants.OVERLAY));
        Assert.assertEquals(BlendMode.DARKEN, CssUtils.parseBlendMode(CommonCssConstants.DARKEN));
        Assert.assertEquals(BlendMode.LIGHTEN, CssUtils.parseBlendMode(CommonCssConstants.LIGHTEN));
        Assert.assertEquals(BlendMode.COLOR_DODGE, CssUtils.parseBlendMode(CommonCssConstants.COLOR_DODGE));
        Assert.assertEquals(BlendMode.COLOR_BURN, CssUtils.parseBlendMode(CommonCssConstants.COLOR_BURN));
        Assert.assertEquals(BlendMode.HARD_LIGHT, CssUtils.parseBlendMode(CommonCssConstants.HARD_LIGHT));
        Assert.assertEquals(BlendMode.SOFT_LIGHT, CssUtils.parseBlendMode(CommonCssConstants.SOFT_LIGHT));
        Assert.assertEquals(BlendMode.DIFFERENCE, CssUtils.parseBlendMode(CommonCssConstants.DIFFERENCE));
        Assert.assertEquals(BlendMode.EXCLUSION, CssUtils.parseBlendMode(CommonCssConstants.EXCLUSION));
        Assert.assertEquals(BlendMode.HUE, CssUtils.parseBlendMode(CommonCssConstants.HUE));
        Assert.assertEquals(BlendMode.SATURATION, CssUtils.parseBlendMode(CommonCssConstants.SATURATION));
        Assert.assertEquals(BlendMode.COLOR, CssUtils.parseBlendMode(CommonCssConstants.COLOR));
        Assert.assertEquals(BlendMode.LUMINOSITY, CssUtils.parseBlendMode(CommonCssConstants.LUMINOSITY));
        Assert.assertEquals(BlendMode.NORMAL, CssUtils.parseBlendMode("invalid"));
        Assert.assertEquals(BlendMode.NORMAL, CssUtils.parseBlendMode("SCREEN"));
    }

    @Test
    public void testWrongAttrTest01() {
        String strToParse = "attr((href))";
        String result = CssUtils.extractAttributeValue(strToParse, null);
        Assert.assertNull(result);
    }

    @Test
    public void testWrongAttrTest02() {
        String strToParse = "attr('href')";
        String result = CssUtils.extractAttributeValue(strToParse, null);
        Assert.assertNull(result);
    }

    @Test
    public void testWrongAttrTest03() {
        String strToParse = "attrrname)";
        String result = CssUtils.extractAttributeValue(strToParse, null);
        Assert.assertNull(result);
    }

    @Test
    public void testExtractingAttrTest01() {
        IElementNode iNode = new CssPseudoElementNode(null, "url");
        String strToParse = "attr(url)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("", result);
    }

    @Test
    public void testExtractingAttrTest02() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(url url)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertNull(result);
    }

    @Test
    public void testExtractingAttrTest03() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(url url,#one)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("#one", result);
    }

    @Test
    public void testExtractingAttrTest04() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr()";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertNull(result);
    }

    @Test
    public void testExtractingAttrTest05() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr('\')";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertNull(result);
    }

    @Test
    public void testExtractingAttrTest06() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str,\"hey\")";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("hey", result);
    }

    @Test
    public void testExtractingAttrTest07() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str string)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("", result);
    }

    @Test
    public void testExtractingAttrTest08() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str string,\"value\")";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("value", result);
    }

    @Test
    public void testExtractingAttrTest09() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str string,\"val,ue\")";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("val,ue", result);
    }

    @Test
    public void testExtractingAttrTest10() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str string,'val,ue')";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("val,ue", result);
    }

    @Test
    public void testExtractingAttrTest11() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(name, \"value\", \"value\", \"value\")";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertNull(result);
    }

    @Test
    public void wrongAttributeTypeTest() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str mem)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertNull(result);
    }

    @Test
    public void wrongParamsInAttrFunctionTest() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str mem lol)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertNull(result);
    }
}
