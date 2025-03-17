/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.svg.css;

import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.styledxmlparser.css.CssFontFaceRule;
import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.styledxmlparser.css.resolve.AbstractCssContext;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attribute;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.TextNode;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupTextNode;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.css.impl.SvgStyleResolver;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgProcessorContext;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@org.junit.jupiter.api.Tag("UnitTest")
public class SvgStyleResolverTest extends ExtendedITextTest{
    private static final String baseUri = "./src/test/resources/com/itextpdf/svg/css/SvgStyleResolver/";

    //Single element test
    //Inherits values from parent?
    //Calculates values from parent
    @Test
    public void SvgCssResolverBasicAttributeTest() {

        Element jsoupCircle = new Element(Tag.valueOf("circle"), "");
        Attributes circleAttributes = jsoupCircle.attributes();
        circleAttributes.put(new Attribute("id", "circle1"));
        circleAttributes.put(new Attribute("cx", "95"));
        circleAttributes.put(new Attribute("cy", "95"));
        circleAttributes.put(new Attribute("rx", "53"));
        circleAttributes.put(new Attribute("ry", "53"));
        circleAttributes.put(new Attribute("style", "stroke-width:1.5;stroke:#da0000;"));

        AbstractCssContext cssContext = new SvgCssContext();

        INode circle = new JsoupElementNode(jsoupCircle);
        SvgProcessorContext context = new SvgProcessorContext(new SvgConverterProperties());
        ICssResolver resolver = new SvgStyleResolver(circle, context);
        Map<String, String> actual = resolver.resolveStyles(circle, cssContext);
        Map<String, String> expected = new HashMap<>();
        expected.put("id", "circle1");
        expected.put("cx", "95");
        expected.put("cy", "95");
        expected.put("rx", "53");
        expected.put("ry", "53");
        expected.put("stroke-width", "1.5");
        expected.put("stroke", "#da0000");
        expected.put("font-size", "12pt");


        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void svgCssResolverStylesheetTest() {
        Element jsoupLink = new Element(Tag.valueOf(SvgConstants.Tags.LINK), "");
        Attributes linkAttributes = jsoupLink.attributes();
        linkAttributes.put(new Attribute(SvgConstants.Attributes.XMLNS, "http://www.w3.org/1999/xhtml"));
        linkAttributes.put(new Attribute(SvgConstants.Attributes.REL, SvgConstants.Attributes.STYLESHEET));
        linkAttributes.put(new Attribute(SvgConstants.Attributes.HREF, "styleSheetWithLinkStyle.css"));
        linkAttributes.put(new Attribute("type", "text/css"));
        JsoupElementNode node = new JsoupElementNode(jsoupLink);

        SvgConverterProperties scp = new SvgConverterProperties();
        scp.setBaseUri(baseUri);

        SvgProcessorContext processorContext = new SvgProcessorContext(scp);
        SvgStyleResolver sr = new SvgStyleResolver(node, processorContext);
        Map<String, String> attr = sr.resolveStyles(node, new SvgCssContext());

        Map<String, String> expectedAttr = new HashMap<>();
        expectedAttr.put(SvgConstants.Attributes.XMLNS, "http://www.w3.org/1999/xhtml");
        expectedAttr.put(SvgConstants.Attributes.REL, SvgConstants.Attributes.STYLESHEET);
        expectedAttr.put(SvgConstants.Attributes.HREF, "styleSheetWithLinkStyle.css");
        expectedAttr.put(SvgConstants.Attributes.FONT_SIZE, "12pt");
        expectedAttr.put("type", "text/css");
        // Attribute from external stylesheet
        expectedAttr.put(SvgConstants.Attributes.FILL, "black");

        Assertions.assertEquals(expectedAttr, attr);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, logLevel = LogLevelConstants.ERROR),
    })
    public void svgCssResolverInvalidNameStylesheetTest() {
        Element jsoupLink = new Element(Tag.valueOf(SvgConstants.Tags.LINK), "");
        Attributes linkAttributes = jsoupLink.attributes();
        linkAttributes.put(new Attribute(SvgConstants.Attributes.XMLNS, "http://www.w3.org/1999/xhtml"));
        linkAttributes.put(new Attribute(SvgConstants.Attributes.REL, SvgConstants.Attributes.STYLESHEET));
        linkAttributes.put(new Attribute(SvgConstants.Attributes.HREF, "!invalid name!externalSheet.css"));
        linkAttributes.put(new Attribute("type", "text/css"));
        JsoupElementNode node = new JsoupElementNode(jsoupLink);

        SvgConverterProperties scp = new SvgConverterProperties();
        scp.setBaseUri(baseUri);

        SvgProcessorContext processorContext = new SvgProcessorContext(scp);
        SvgStyleResolver sr = new SvgStyleResolver(node, processorContext);
        Map<String, String> attr = sr.resolveStyles(node, new SvgCssContext());

        Map<String, String> expectedAttr = new HashMap<>();
        expectedAttr.put(SvgConstants.Attributes.XMLNS, "http://www.w3.org/1999/xhtml");
        expectedAttr.put(SvgConstants.Attributes.REL, SvgConstants.Attributes.STYLESHEET);
        expectedAttr.put(SvgConstants.Attributes.HREF,  "!invalid name!externalSheet.css");
        expectedAttr.put(SvgConstants.Attributes.FONT_SIZE,  "12pt");
        expectedAttr.put("type", "text/css");

        Assertions.assertEquals(expectedAttr, attr);
    }

    @Test
    public void svgCssResolverXlinkTest() {
        Element jsoupImage = new Element(Tag.valueOf("image"), "");
        Attributes imageAttributes = jsoupImage.attributes();
        imageAttributes.put(new Attribute("xlink:href", "itis.jpg"));
        JsoupElementNode node = new JsoupElementNode(jsoupImage);

        SvgConverterProperties scp = new SvgConverterProperties();
        scp.setBaseUri(baseUri);

        SvgProcessorContext processorContext = new SvgProcessorContext(scp);
        SvgStyleResolver sr = new SvgStyleResolver(node, processorContext);
        Map<String, String> attr = sr.resolveStyles(node, new SvgCssContext());

        String fileName = baseUri + "itis.jpg";
        final String expectedUrl = UrlUtil.toNormalizedURI(fileName).toString();
        String expectedUrlAnotherValidVersion;

        if (expectedUrl.startsWith("file:///")) {
            expectedUrlAnotherValidVersion = "file:/" + expectedUrl.substring("file:///".length());
        } else if (expectedUrl.startsWith("file:/")) {
            expectedUrlAnotherValidVersion = "file:///" + expectedUrl.substring("file:/".length());
        } else {
            expectedUrlAnotherValidVersion = expectedUrl;
        }

        final String url = attr.get("xlink:href");

        // Both variants(namely with triple and single slashes) are valid.
        Assertions.assertTrue(expectedUrl.equals(url) || expectedUrlAnotherValidVersion.equals(url));
    }

    @Test
    public void svgCssResolveHashXlinkTest() {
        Element jsoupImage = new Element(Tag.valueOf("image"), "");
        Attributes imageAttributes = jsoupImage.attributes();
        imageAttributes.put(new Attribute("xlink:href", "#testid"));
        JsoupElementNode node = new JsoupElementNode(jsoupImage);

        SvgConverterProperties scp = new SvgConverterProperties();
        scp.setBaseUri(baseUri);

        SvgProcessorContext processorContext = new SvgProcessorContext(scp);
        SvgStyleResolver sr = new SvgStyleResolver(node, processorContext);
        Map<String, String> attr = sr.resolveStyles(node, new SvgCssContext());

        Assertions.assertEquals("#testid", attr.get("xlink:href"));
    }

    @Test
    public void overrideDefaultStyleTest() {
        ICssResolver styleResolver = new SvgStyleResolver(new SvgProcessorContext(new SvgConverterProperties()));
        Element svg = new Element(Tag.valueOf("svg"), "");
        svg.attributes().put(SvgConstants.Attributes.STYLE, "stroke:white");
        INode svgNode = new JsoupElementNode(svg);
        Map<String, String> resolvedStyles = styleResolver.resolveStyles(svgNode, new SvgCssContext());

        Assertions.assertEquals("white", resolvedStyles.get(SvgConstants.Attributes.STROKE));
    }

    @Test
    public void SvgCssResolverStyleTagTest() {
        Element styleTag = new Element(Tag.valueOf("style"), "");
        TextNode styleContents = new TextNode("\n" +
                "\tellipse{\n" +
                "\t\tstroke-width:1.76388889;\n" +
                "\t\tstroke:#da0000;\n" +
                "\t\tstroke-opacity:1;\n" +
                "\t}\n" +
                "  ");
        JsoupElementNode jSoupStyle = new JsoupElementNode(styleTag);
        jSoupStyle.addChild(new JsoupTextNode(styleContents));
        Element ellipse = new Element(Tag.valueOf("ellipse"), "");
        JsoupElementNode jSoupEllipse = new JsoupElementNode(ellipse);
        SvgProcessorContext context = new SvgProcessorContext(new SvgConverterProperties());

        SvgStyleResolver resolver = new SvgStyleResolver(jSoupStyle, context);
        AbstractCssContext svgContext = new SvgCssContext();
        Map<String, String> actual = resolver.resolveStyles(jSoupEllipse, svgContext);

        Map<String, String> expected = new HashMap<>();
        expected.put("stroke-width", "1.76388889");
        expected.put("stroke", "#da0000");
        expected.put("stroke-opacity", "1");
        expected.put("font-size", "12pt");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void fontsResolverTagTest() {
        Element styleTag = new Element(Tag.valueOf("style"), "");
        TextNode styleContents = new TextNode("\n" +
                "\t@font-face{\n" +
                "\t\tfont-family:Courier;\n" +
                "\t\tsrc:url(#Super Sans);\n" +
                "\t}\n" +
                "  ");
        JsoupElementNode jSoupStyle = new JsoupElementNode(styleTag);
        jSoupStyle.addChild(new JsoupTextNode(styleContents));
        SvgProcessorContext context = new SvgProcessorContext(new SvgConverterProperties());
        SvgStyleResolver resolver = new SvgStyleResolver(jSoupStyle, context);
        List<CssFontFaceRule> fontFaceRuleList = resolver.getFonts();
        Assertions.assertEquals(1, fontFaceRuleList.size());
        Assertions.assertEquals(2, fontFaceRuleList.get(0).getProperties().size());
    }

    @Test
    public void nestedCssVariableTest() {
        Element styleTag = new Element(Tag.valueOf("style"), "");
        TextNode styleContents = new TextNode(
                "\tspan {\n" +
                        "\t\t--test-var: 30px;\n" +
                        "\t}\n" +
                        ".a {\n" +
                        "    --test-var2: 50px;\n" +
                        "}\n" +
                        "div {\n" +
                        "    margin: var(--test-var,var(--test-var2));\n" +
                        "}\n"
        );
        JsoupElementNode jSoupStyle = new JsoupElementNode(styleTag);
        jSoupStyle.addChild(new JsoupTextNode(styleContents));

        Element div = new Element(Tag.valueOf("div"), "");
        div.attributes().put("class", "a");
        JsoupElementNode jSoupDiv = new JsoupElementNode(div);

        SvgProcessorContext context = new SvgProcessorContext(new SvgConverterProperties());
        SvgStyleResolver resolver = new SvgStyleResolver(jSoupStyle, context);
        AbstractCssContext svgContext = new SvgCssContext();

        Map<String, String> actual = resolver.resolveStyles(jSoupDiv, svgContext);

        Map<String, String> expected = new HashMap<>();
        expected.put("class", "a");
        expected.put("--test-var2", "50px");
        expected.put("margin-top", "50px");
        expected.put("margin-right", "50px");
        expected.put("margin-bottom", "50px");
        expected.put("margin-left", "50px");
        expected.put("font-size", "12pt");

        Assertions.assertEquals(expected, actual);
    }

    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, count = 2))
    @Test
    public void invalidValueCssVariableTest() {
        Element styleTag = new Element(Tag.valueOf("style"), "");
        TextNode styleContents = new TextNode(
                "    p {\n" +
                        "        word-break: var(--test-var);\n" +
                        "    }\n" +
                        "    div {\n" +
                        "\t\t--test-var: incorrect;\n" +
                        "\t}");
        JsoupElementNode jSoupStyle = new JsoupElementNode(styleTag);
        jSoupStyle.addChild(new JsoupTextNode(styleContents));

        Element div = new Element(Tag.valueOf("div"), "");
        Element paragraph = new Element(Tag.valueOf("p"), "");
        Element nestedParagraph = new Element(Tag.valueOf("p"), "");
        JsoupElementNode jSoupParagraph = new JsoupElementNode(paragraph);
        JsoupElementNode jSoupNestedParagraph = new JsoupElementNode(nestedParagraph);
        JsoupElementNode jSoupDiv = new JsoupElementNode(div);
        jSoupDiv.addChild(jSoupNestedParagraph);

        SvgProcessorContext context = new SvgProcessorContext(new SvgConverterProperties());
        SvgStyleResolver resolver = new SvgStyleResolver(jSoupStyle, context);
        AbstractCssContext svgContext = new SvgCssContext();


        jSoupDiv.setStyles(resolver.resolveStyles(jSoupDiv, svgContext));
        Map<String, String> nestedStyles = resolver.resolveStyles(jSoupNestedParagraph, svgContext);
        Map<String, String> styles = resolver.resolveStyles(jSoupParagraph, svgContext);

        Map<String, String> expectedStyles = new HashMap<>();
        expectedStyles.put("font-size", "12pt");
        Assertions.assertEquals(expectedStyles, styles);

        Map<String, String> expectedNestedStyles = new HashMap<>();
        expectedNestedStyles.put("--test-var", "incorrect");
        expectedNestedStyles.put("font-size", "12pt");

        Assertions.assertEquals(expectedNestedStyles, nestedStyles);
    }

    public static Iterable<Object[]> divVariablesTestProvider() {
        return Arrays.asList(new Object[][]{
                {"a", "30px", "30px"},
                {"b", "50px", "30px"},
                {"c d", "35px", "35px"}
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("divVariablesTestProvider")
    public void cssVariableInTheSameScopeTest(String divClass, String expectedMargin, String expectedVarValue) {
        Element styleTag = new Element(Tag.valueOf("style"), "");
        TextNode styleContents = new TextNode(
                "\tdiv {\n" +
                        "\t\t--test-var: 30px;\n" +
                        "\t}\n" +
                        "    div.a {\n" +
                        "        margin: var(--test-var,40px);\n" +
                        "    }\n" +
                        "    div.b {\n" +
                        "        margin: var(--other-var,50px);\n" +
                        "    }\n" +
                        "    div.c {\n" +
                        "        --test-var: 35px;\n" +
                        "    }\n" +
                        "    div.c.d {\n" +
                        "        margin: var(--test-var,40px);\n" +
                        "    }"
        );
        JsoupElementNode jSoupStyle = new JsoupElementNode(styleTag);
        jSoupStyle.addChild(new JsoupTextNode(styleContents));

        Element div = new Element(Tag.valueOf("div"), "");
        div.attributes().put("class", divClass);
        JsoupElementNode jSoupDiv = new JsoupElementNode(div);

        SvgProcessorContext context = new SvgProcessorContext(new SvgConverterProperties());
        SvgStyleResolver resolver = new SvgStyleResolver(jSoupStyle, context);
        AbstractCssContext svgContext = new SvgCssContext();

        Map<String, String> actual = resolver.resolveStyles(jSoupDiv, svgContext);

        Map<String, String> expected = new HashMap<>();
        expected.put("class", divClass);
        expected.put("--test-var", expectedVarValue);
        expected.put("margin-top", expectedMargin);
        expected.put("margin-right", expectedMargin);
        expected.put("margin-bottom", expectedMargin);
        expected.put("margin-left", expectedMargin);
        expected.put("font-size", "12pt");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void cssVariableInheritanceTest() {
        Element styleTag = new Element(Tag.valueOf("style"), "");
        TextNode styleContents = new TextNode("\tol {\n" +
                "\t\t--test-var: circle;\n" +
                "\t}\n" +
                "    ul {\n" +
                "        list-style-type: var(--test-var, square);\n" +
                "    }\n" +
                "    ol {\n" +
                "        list-style-type: var(--test-var, square);\n" +
                "    }");
        JsoupElementNode jSoupStyle = new JsoupElementNode(styleTag);
        jSoupStyle.addChild(new JsoupTextNode(styleContents));

        Element unorderedList = new Element(Tag.valueOf("ul"), "");
        Element orderedList = new Element(Tag.valueOf("ol"), "");
        JsoupElementNode jSoupUnorderedList = new JsoupElementNode(unorderedList);
        JsoupElementNode jSoupOrderedList = new JsoupElementNode(orderedList);
        JsoupElementNode jSoupItemUnordered = new JsoupElementNode(new Element(Tag.valueOf("li"), ""));
        JsoupElementNode jSoupItemOrdered = new JsoupElementNode(new Element(Tag.valueOf("li"), ""));
        jSoupUnorderedList.addChild(jSoupItemUnordered);
        jSoupOrderedList.addChild(jSoupItemOrdered);

        SvgProcessorContext context = new SvgProcessorContext(new SvgConverterProperties());
        SvgStyleResolver resolver = new SvgStyleResolver(jSoupStyle, context);
        AbstractCssContext svgContext = new SvgCssContext();


        jSoupUnorderedList.setStyles(resolver.resolveStyles(jSoupUnorderedList, svgContext));
        jSoupOrderedList.setStyles(resolver.resolveStyles(jSoupOrderedList, svgContext));
        Map<String, String> unorderedStyles = resolver.resolveStyles(jSoupItemUnordered, svgContext);
        Map<String, String> orderedStyles = resolver.resolveStyles(jSoupItemOrdered, svgContext);

        Map<String, String> expectedUnorderedStyles = new HashMap<>();
        expectedUnorderedStyles.put("font-size", "12pt");
        expectedUnorderedStyles.put("list-style-type", "square");
        Assertions.assertEquals(expectedUnorderedStyles, unorderedStyles);

        Map<String, String> expectedOrderedStyles = new HashMap<>();
        expectedOrderedStyles.put("--test-var", "circle");
        expectedOrderedStyles.put("list-style-type", "circle");
        expectedOrderedStyles.put("font-size", "12pt");

        Assertions.assertEquals(expectedOrderedStyles, orderedStyles);
    }

    @Test
    public void fontFamilyResolvingTest1() {
        Map<String, String> expected = new HashMap<>();
        expected.put("font-family", "courier");
        expected.put("font-size", "12pt");
        fontFamilyResolving("font-family:Courier;", expected);
    }

    @Test
    public void fontFamilyResolvingTest2() {
        Map<String, String> expected = new HashMap<>();
        expected.put("font-family", "Courier");
        expected.put("font-size", "12pt");
        fontFamilyResolving("font-family:'Courier';", expected);
    }

    @Test
    public void fontFamilyResolvingTest3() {
        Map<String, String> expected = new HashMap<>();
        expected.put("font-family", "Courier");
        expected.put("font-size", "12pt");
        fontFamilyResolving("font-family:\"Courier\";", expected);
    }

    @Test
    public void fontFamilyResolvingTest4() {
        Map<String, String> expected = new HashMap<>();
        expected.put("font-family", " Courier");
        expected.put("font-size", "12pt");
        fontFamilyResolving("font-family:\" Courier\" ;", expected);
    }

    @Test
    public void fontFamilyResolvingTest5() {
        Map<String, String> expected = new HashMap<>();
        expected.put("font-family", "Courier");
        expected.put("font-size", "12pt");
        fontFamilyResolving("font-family:\"Courier\", serif, Times;", expected);
    }

    @Test
    public void fontFamilyResolvingTest6() {
        Map<String, String> expected = new HashMap<>();
        expected.put("font-family", "serif");
        expected.put("font-size", "12pt");
        fontFamilyResolving("font-family:serif, \"Courier\", Times;", expected);
    }

    private static void fontFamilyResolving(String styleAttr, Map<String, String> expected) {
        Element jsoupText = new Element(Tag.valueOf("text"), "");
        Attributes textAttributes = jsoupText.attributes();
        textAttributes.put(new Attribute("style", styleAttr));

        INode text = new JsoupElementNode(jsoupText);
        ICssResolver resolver = new SvgStyleResolver(text, new SvgProcessorContext(new SvgConverterProperties()));
        Map<String, String> actual = resolver.resolveStyles(text, new SvgCssContext());
        Assertions.assertEquals(expected, actual);
    }
}
