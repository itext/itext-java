/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.layout.properties.BlendMode;
import com.itextpdf.styledxmlparser.CommonAttributeConstants;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.pseudo.CssPseudoElementNode;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@org.junit.jupiter.api.Tag("UnitTest")
public class CssUtilsTest extends ExtendedITextTest {
    private static float EPS = 0.0001f;

    @Test
    public void convertFloatMaximumToPdfTest() {
        float expected = Float.POSITIVE_INFINITY;
        float actual = CssUtils.convertPtsToPx(Float.MAX_VALUE);

        Assertions.assertEquals(expected, actual, 0f);
    }

    @Test
    public void convertFloatMinimumToPdfTest() {
        float expected = 1.4E-45f;
        float actual = CssUtils.convertPtsToPx(Float.MIN_VALUE);

        Assertions.assertEquals(expected, actual, 0f);
    }

    @Test
    public void extractShorthandPropertiesFromEmptyStringTest() {
        String sourceString = "";
        List<List<String>> expected = new ArrayList<>();
        expected.add(new ArrayList<String>());

        Assertions.assertEquals(expected, CssUtils.extractShorthandProperties(sourceString));
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

        Assertions.assertEquals(expected, CssUtils.extractShorthandProperties(sourceString));
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

        Assertions.assertEquals(expected, CssUtils.extractShorthandProperties(sourceString));
    }

    @Test
    public void normalConvertPtsToPxTest() {
        float[] input = new float[] {-1f, 0f, 1f};
        float[] expected = new float[] {-1.3333334f, 0f, 1.3333334f};

        for (int i = 0; i < input.length; i++) {
            float actual = CssUtils.convertPtsToPx(input[i]);
            Assertions.assertEquals(expected[i], actual, 0f);
        }
    }

    @Test
    public void normalizeProperty() {
        Assertions.assertEquals("part1 part2", CssUtils.normalizeCssProperty("   part1   part2  "));
        Assertions.assertEquals("\" the next quote is ESCAPED \\\\\\\" still  IN string \"", CssUtils.normalizeCssProperty("\" the next quote is ESCAPED \\\\\\\" still  IN string \""));
        Assertions.assertEquals("\" the next quote is NOT ESCAPED \\\\\" not in the string", CssUtils.normalizeCssProperty("\" the next quote is NOT ESCAPED \\\\\" NOT in   THE string"));
        Assertions.assertEquals("\" You CAN put 'Single  Quotes' in double quotes WITHOUT escaping\"", CssUtils.normalizeCssProperty("\" You CAN put 'Single  Quotes' in double quotes WITHOUT escaping\""));
        Assertions.assertEquals("' You CAN put \"DOUBLE  Quotes\" in double quotes WITHOUT escaping'", CssUtils.normalizeCssProperty("' You CAN put \"DOUBLE  Quotes\" in double quotes WITHOUT escaping'"));
        Assertions.assertEquals("\" ( BLA \" attr(href)\" BLA )  \"", CssUtils.normalizeCssProperty("\" ( BLA \"      AttR( Href  )\" BLA )  \""));
        Assertions.assertEquals("\" (  \"attr(href) \"  )  \"", CssUtils.normalizeCssProperty("\" (  \"aTTr( hREf  )   \"  )  \""));
        Assertions.assertEquals("rgba(255,255,255,0.2)", CssUtils.normalizeCssProperty("rgba(  255,  255 ,  255 ,0.2   )"));
    }

    @Test
    public void normalizeUrlTest() {
        Assertions.assertEquals("url(data:application/font-woff;base64,2CBPCRXmgywtV1t4oWwjBju0kqkvfhPs0cYdMgFtDSY5uL7MIGT5wiGs078HrvBHekp0Yf=)",
                CssUtils.normalizeCssProperty("url(data:application/font-woff;base64,2CBPCRXmgywtV1t4oWwjBju0kqkvfhPs0cYdMgFtDSY5uL7MIGT5wiGs078HrvBHekp0Yf=)"));
        Assertions.assertEquals("url(\"quoted  Url\")", CssUtils.normalizeCssProperty("  url(  \"quoted  Url\")"));
        Assertions.assertEquals("url('quoted  Url')", CssUtils.normalizeCssProperty("  url(  'quoted  Url')"));
        Assertions.assertEquals("url(haveEscapedEndBracket\\))", CssUtils.normalizeCssProperty("url(  haveEscapedEndBracket\\) )"));
    }

    @Test
    public void parseUnicodeRangeTest() {
        Assertions.assertEquals("[(0; 1048575)]", CssUtils.parseUnicodeRange("U+?????").toString());
        Assertions.assertEquals("[(38; 38)]", CssUtils.parseUnicodeRange("U+26").toString());
        Assertions.assertEquals("[(0; 127)]", CssUtils.parseUnicodeRange(" U+0-7F").toString());
        Assertions.assertEquals("[(37; 255)]", CssUtils.parseUnicodeRange("U+0025-00FF").toString());
        Assertions.assertEquals("[(1024; 1279)]", CssUtils.parseUnicodeRange("U+4??").toString());
        Assertions.assertEquals("[(262224; 327519)]", CssUtils.parseUnicodeRange("U+4??5?").toString());
        Assertions.assertEquals("[(37; 255), (1024; 1279)]", CssUtils.parseUnicodeRange("U+0025-00FF, U+4??").toString());

        Assertions.assertNull(CssUtils.parseUnicodeRange("U+??????")); // more than 5 question marks are not allowed
        Assertions.assertNull(CssUtils.parseUnicodeRange("UU+7-10")); // wrong syntax
        Assertions.assertNull(CssUtils.parseUnicodeRange("U+7?-9?")); // wrong syntax
        Assertions.assertNull(CssUtils.parseUnicodeRange("U+7-")); // wrong syntax
    }

    @Test
    public void elementNodeIsStyleSheetLink() {
        Element element = new Element(Tag.valueOf("link"), "");
        element.attr(CommonAttributeConstants.REL, CommonAttributeConstants.STYLESHEET);
        JsoupElementNode elementNode = new JsoupElementNode(element);

        Assertions.assertTrue(CssUtils.isStyleSheetLink(elementNode));
    }

    @Test
    public void elementNodeIsNotLink() {
        Element element = new Element(Tag.valueOf("p"), "");
        element.attr(CommonAttributeConstants.REL, CommonAttributeConstants.STYLESHEET);
        JsoupElementNode elementNode = new JsoupElementNode(element);

        Assertions.assertFalse(CssUtils.isStyleSheetLink(elementNode));
    }

    @Test
    public void elementNodeAttributeIsNotStylesheet() {
        Element element = new Element(Tag.valueOf("link"), "");
        element.attr(CommonAttributeConstants.REL, "");
        JsoupElementNode elementNode = new JsoupElementNode(element);

        Assertions.assertFalse(CssUtils.isStyleSheetLink(elementNode));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INCORRECT_CHARACTER_SEQUENCE))
    public void splitStringWithCommaTest() {
        Assertions.assertEquals(new ArrayList<String>(), CssUtils.splitStringWithComma(null));
        Assertions.assertEquals(Arrays.asList("value1", "value2", "value3"),
                CssUtils.splitStringWithComma("value1,value2,value3"));
        Assertions.assertEquals(Arrays.asList("value1", " value2", " value3"),
                CssUtils.splitStringWithComma("value1, value2, value3"));
        Assertions.assertEquals(Arrays.asList("value1", "(value,with,comma)", "value3"),
                CssUtils.splitStringWithComma("value1,(value,with,comma),value3"));
        Assertions.assertEquals(Arrays.asList("value1", "(val(ue,with,comma),value3"),
                CssUtils.splitStringWithComma("value1,(val(ue,with,comma),value3"));
        Assertions.assertEquals(Arrays.asList("value1", "(value,with)", "comma)", "value3"),
                CssUtils.splitStringWithComma("value1,(value,with),comma),value3"));
        Assertions.assertEquals(Arrays.asList("value1", "( v2,v3)", "(v4, v5)", "value3"),
                CssUtils.splitStringWithComma("value1,( v2,v3),(v4, v5),value3"));
        Assertions.assertEquals(Arrays.asList("v.al*ue1\"", "( v2,v3)", "\"(v4,v5;);", "value3"),
                CssUtils.splitStringWithComma("v.al*ue1\",( v2,v3),\"(v4,v5;);,value3"));
    }

    @Test
    public void splitStringTest() {
        Assertions.assertEquals(new ArrayList<String>(), CssUtils.splitString(null, ','));
        Assertions.assertEquals(Arrays.asList("value1", "(value,with,comma)", "value3"),
                CssUtils.splitString("value1,(value,with,comma),value3", ',', new EscapeGroup('(', ')')));
        Assertions.assertEquals(Arrays.asList("value1 ", " (val(ue,with,comma),value3"),
                CssUtils.splitString("value1 , (val(ue,with,comma),value3", ',', new EscapeGroup('(', ')')));
        Assertions.assertEquals(Arrays.asList("some text", " (some", " text in", " brackets)", " \"some, text, in quotes,\""),
                CssUtils.splitString("some text, (some, text in, brackets), \"some, text, in quotes,\"", ',',
                        new EscapeGroup('\"')));
        Assertions.assertEquals(Arrays.asList("some text", " (some. text in. brackets)", " \"some. text. in quotes.\""),
                CssUtils.splitString("some text. (some. text in. brackets). \"some. text. in quotes.\"", '.',
                        new EscapeGroup('\"'), new EscapeGroup('(', ')')));
        Assertions.assertEquals(Arrays.asList("value1", "(value", "with" ,"comma)", "value3"),
                CssUtils.splitString("value1,(value,with,comma),value3", ','));
        Assertions.assertEquals(Arrays.asList("value1", "value", "with" ,"comma", "value3"),
                CssUtils.splitString("value1,value,with,comma,value3", ',', new EscapeGroup(',')));
    }

    @Test
    public void parseBlendModeTest() {
        Assertions.assertEquals(BlendMode.NORMAL, CssUtils.parseBlendMode(null));
        Assertions.assertEquals(BlendMode.NORMAL, CssUtils.parseBlendMode(CommonCssConstants.NORMAL));
        Assertions.assertEquals(BlendMode.MULTIPLY, CssUtils.parseBlendMode(CommonCssConstants.MULTIPLY));
        Assertions.assertEquals(BlendMode.SCREEN, CssUtils.parseBlendMode(CommonCssConstants.SCREEN));
        Assertions.assertEquals(BlendMode.OVERLAY, CssUtils.parseBlendMode(CommonCssConstants.OVERLAY));
        Assertions.assertEquals(BlendMode.DARKEN, CssUtils.parseBlendMode(CommonCssConstants.DARKEN));
        Assertions.assertEquals(BlendMode.LIGHTEN, CssUtils.parseBlendMode(CommonCssConstants.LIGHTEN));
        Assertions.assertEquals(BlendMode.COLOR_DODGE, CssUtils.parseBlendMode(CommonCssConstants.COLOR_DODGE));
        Assertions.assertEquals(BlendMode.COLOR_BURN, CssUtils.parseBlendMode(CommonCssConstants.COLOR_BURN));
        Assertions.assertEquals(BlendMode.HARD_LIGHT, CssUtils.parseBlendMode(CommonCssConstants.HARD_LIGHT));
        Assertions.assertEquals(BlendMode.SOFT_LIGHT, CssUtils.parseBlendMode(CommonCssConstants.SOFT_LIGHT));
        Assertions.assertEquals(BlendMode.DIFFERENCE, CssUtils.parseBlendMode(CommonCssConstants.DIFFERENCE));
        Assertions.assertEquals(BlendMode.EXCLUSION, CssUtils.parseBlendMode(CommonCssConstants.EXCLUSION));
        Assertions.assertEquals(BlendMode.HUE, CssUtils.parseBlendMode(CommonCssConstants.HUE));
        Assertions.assertEquals(BlendMode.SATURATION, CssUtils.parseBlendMode(CommonCssConstants.SATURATION));
        Assertions.assertEquals(BlendMode.COLOR, CssUtils.parseBlendMode(CommonCssConstants.COLOR));
        Assertions.assertEquals(BlendMode.LUMINOSITY, CssUtils.parseBlendMode(CommonCssConstants.LUMINOSITY));
        Assertions.assertEquals(BlendMode.NORMAL, CssUtils.parseBlendMode("invalid"));
        Assertions.assertEquals(BlendMode.NORMAL, CssUtils.parseBlendMode("SCREEN"));
    }

    @Test
    public void testWrongAttrTest01() {
        String strToParse = "attr((href))";
        String result = CssUtils.extractAttributeValue(strToParse, null);
        Assertions.assertNull(result);
    }

    @Test
    public void testWrongAttrTest02() {
        String strToParse = "attr('href')";
        String result = CssUtils.extractAttributeValue(strToParse, null);
        Assertions.assertNull(result);
    }

    @Test
    public void testWrongAttrTest03() {
        String strToParse = "attrrname)";
        String result = CssUtils.extractAttributeValue(strToParse, null);
        Assertions.assertNull(result);
    }

    @Test
    public void testExtractingAttrTest01() {
        IElementNode iNode = new CssPseudoElementNode(null, "url");
        String strToParse = "attr(url)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assertions.assertEquals("", result);
    }

    @Test
    public void testExtractingAttrTest02() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(url url)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assertions.assertNull(result);
    }

    @Test
    public void testExtractingAttrTest03() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(url url,#one)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assertions.assertEquals("#one", result);
    }

    @Test
    public void testExtractingAttrTest04() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr()";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assertions.assertNull(result);
    }

    @Test
    public void testExtractingAttrTest05() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr('\')";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assertions.assertNull(result);
    }

    @Test
    public void testExtractingAttrTest06() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str,\"hey\")";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assertions.assertEquals("hey", result);
    }

    @Test
    public void testExtractingAttrTest07() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str string)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assertions.assertEquals("", result);
    }

    @Test
    public void testExtractingAttrTest08() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str string,\"value\")";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assertions.assertEquals("value", result);
    }

    @Test
    public void testExtractingAttrTest09() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str string,\"val,ue\")";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assertions.assertEquals("val,ue", result);
    }

    @Test
    public void testExtractingAttrTest10() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str string,'val,ue')";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assertions.assertEquals("val,ue", result);
    }

    @Test
    public void testExtractingAttrTest11() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(name, \"value\", \"value\", \"value\")";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assertions.assertNull(result);
    }

    @Test
    public void wrongAttributeTypeTest() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str mem)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assertions.assertNull(result);
    }

    @Test
    public void wrongParamsInAttrFunctionTest() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str mem lol)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assertions.assertNull(result);
    }
}
