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
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.TextUtil;
import com.itextpdf.styledxmlparser.jsoup.integration.ParseTest;
import com.itextpdf.styledxmlparser.jsoup.nodes.CDataNode;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.Entities;
import com.itextpdf.styledxmlparser.jsoup.nodes.Node;
import com.itextpdf.styledxmlparser.jsoup.nodes.TextNode;
import com.itextpdf.styledxmlparser.jsoup.nodes.XmlDeclaration;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Tests XmlTreeBuilder.
 */
@Category(UnitTest.class)
public class XmlTreeBuilderTest extends ExtendedITextTest {
    @Test
    public void testSimpleXmlParse() {
        String xml = "<doc id=2 href='/bar'>Foo <br /><link>One</link><link>Two</link></doc>";
        XmlTreeBuilder tb = new XmlTreeBuilder();
        Document doc = tb.parse(xml, "http://foo.com/");
        Assert.assertEquals("<doc id=\"2\" href=\"/bar\">Foo <br /><link>One</link><link>Two</link></doc>",
                TextUtil.stripNewlines(doc.html()));
        Assert.assertEquals(doc.getElementById("2").absUrl("href"), "http://foo.com/bar");
    }

    @Test
    public void testPopToClose() {
        // test: </val> closes Two, </bar> ignored
        String xml = "<doc><val>One<val>Two</val></bar>Three</doc>";
        XmlTreeBuilder tb = new XmlTreeBuilder();
        Document doc = tb.parse(xml, "http://foo.com/");
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>",
                TextUtil.stripNewlines(doc.html()));
    }

    @Test
    public void testCommentAndDocType() {
        String xml = "<!DOCTYPE HTML><!-- a comment -->One <qux />Two";
        XmlTreeBuilder tb = new XmlTreeBuilder();
        Document doc = tb.parse(xml, "http://foo.com/");
        Assert.assertEquals("<!DOCTYPE HTML><!-- a comment -->One <qux />Two",
                TextUtil.stripNewlines(doc.html()));
    }

    @Test
    public void testSupplyParserToJsoupClass() {
        String xml = "<doc><val>One<val>Two</val></bar>Three</doc>";
        Document doc = Jsoup.parse(xml, "http://foo.com/", Parser.xmlParser());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>",
                TextUtil.stripNewlines(doc.html()));
    }

    @Test
    public void testSupplyParserToDataStream() throws IOException {
        File xmlFile = ParseTest.getFile("/htmltests/xml-test.xml");
        InputStream inStream = FileUtil.getInputStreamForFile(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>",
                TextUtil.stripNewlines(doc.html()));
    }

    @Test
    public void testDoesNotForceSelfClosingKnownTags() {
        // html will force "<br>one</br>" to logically "<br />One<br />". XML should be stay "<br>one</br> -- don't recognise tag.
        Document htmlDoc = Jsoup.parse("<br>one</br>");
        Assert.assertEquals("<br>one\n<br>", htmlDoc.body().html());

        Document xmlDoc = Jsoup.parse("<br>one</br>", "", Parser.xmlParser());
        Assert.assertEquals("<br>one</br>", xmlDoc.html());
    }

    @Test public void handlesXmlDeclarationAsDeclaration() {
        String html = "<?xml encoding='UTF-8' ?><body>One</body><!-- comment -->";
        Document doc = Jsoup.parse(html, "", Parser.xmlParser());
        Assert.assertEquals("<?xml encoding=\"UTF-8\"?><body>One</body><!-- comment -->",doc.outerHtml());
        Assert.assertEquals("#declaration", doc.childNode(0).nodeName());
        Assert.assertEquals("#comment", doc.childNode(2).nodeName());
    }

    @Test public void xmlFragment() {
        String xml = "<one src='/foo/' />Two<three><four /></three>";
        List<Node> nodes = Parser.parseXmlFragment(xml, "http://example.com/");
        Assert.assertEquals(3, nodes.size());

        Assert.assertEquals("http://example.com/foo/", nodes.get(0).absUrl("src"));
        Assert.assertEquals("one", nodes.get(0).nodeName());
        Assert.assertEquals("Two", ((TextNode)nodes.get(1)).text());
    }

    @Test public void xmlParseDefaultsToHtmlOutputSyntax() {
        Document doc = Jsoup.parse("x", "", Parser.xmlParser());
        Assert.assertEquals(Document.OutputSettings.Syntax.xml, doc.outputSettings().syntax());
    }

    @Test
    public void testDoesHandleEOFInTag() {
        String html = "<img src=asdf onerror=\"alert(1)\" x=";
        Document xmlDoc = Jsoup.parse(html, "", Parser.xmlParser());
        Assert.assertEquals("<img src=\"asdf\" onerror=\"alert(1)\" x=\"\" />", xmlDoc.html());
    }

    @Test
    public void testDetectCharsetEncodingDeclaration() throws IOException, URISyntaxException {
        File xmlFile = ParseTest.getFile("/htmltests/xml-charset.xml");
        InputStream inStream = FileUtil.getInputStreamForFile(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        Assert.assertEquals("ISO-8859-1", doc.charset().name());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><data>äöåéü</data>",
            TextUtil.stripNewlines(doc.html()));
    }

    @Test
    public void testParseDeclarationAttributes() {
        String xml = "<?xml version='1' encoding='UTF-8' something='else'?><val>One</val>";
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        XmlDeclaration decl = (XmlDeclaration) doc.childNode(0);
        Assert.assertEquals("1", decl.attr("version"));
        Assert.assertEquals("UTF-8", decl.attr("encoding"));
        Assert.assertEquals("else", decl.attr("something"));
        Assert.assertEquals("version=\"1\" encoding=\"UTF-8\" something=\"else\"", decl.getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"1\" encoding=\"UTF-8\" something=\"else\"?>", decl.outerHtml());
    }

    @Test
    public void caseSensitiveDeclaration() {
        String xml = "<?XML version='1' encoding='UTF-8' something='else'?>";
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        Assert.assertEquals("<?XML version=\"1\" encoding=\"UTF-8\" something=\"else\"?>", doc.outerHtml());
    }

    @Test
    public void testCreatesValidProlog() {
        Document document = Document.createShell("");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.charset(StandardCharsets.UTF_8);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<html>\n" +
            " <head></head>\n" +
            " <body></body>\n" +
            "</html>", document.outerHtml());
    }

    @Test
    public void preservesCaseByDefault() {
        String xml = "<CHECK>One</CHECK><TEST ID=1>Check</TEST>";
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        Assert.assertEquals("<CHECK>One</CHECK><TEST ID=\"1\">Check</TEST>", TextUtil.stripNewlines(doc.html()));
    }

    @Test
    public void appendPreservesCaseByDefault() {
        String xml = "<One>One</One>";
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        Elements one = doc.select("One");
        one.append("<Two ID=2>Two</Two>");
        Assert.assertEquals("<One>One<Two ID=\"2\">Two</Two></One>", TextUtil.stripNewlines(doc.html()));
    }

    @Test
    public void disablesPrettyPrintingByDefault() {
        String xml = "\n\n<div><one>One</one><one>\n Two</one>\n</div>\n ";
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        Assert.assertEquals(xml, doc.html());
    }

    @Test
    public void canNormalizeCase() {
        String xml = "<TEST ID=1>Check</TEST>";
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser().settings(ParseSettings.htmlDefault));
        Assert.assertEquals("<test id=\"1\">Check</test>", TextUtil.stripNewlines(doc.html()));
    }

    @Test public void normalizesDiscordantTags() {
        Parser parser = Parser.xmlParser().settings(ParseSettings.htmlDefault);
        Document document = Jsoup.parse("<div>test</DIV><p></p>", "", parser);
        Assert.assertEquals("<div>test</div><p></p>", document.html());
        // was failing -> toString() = "<div>\n test\n <p></p>\n</div>"
    }

    @Test public void roundTripsCdata() {
        String xml = "<div id=1><![CDATA[\n<html>\n <foo><&amp;]]></div>";
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());

        Element div = doc.getElementById("1");
        Assert.assertEquals("<html>\n <foo><&amp;", div.text());
        Assert.assertEquals(0, div.children().size());
        Assert.assertEquals(1, div.childNodeSize()); // no elements, one text node

        Assert.assertEquals("<div id=\"1\"><![CDATA[\n<html>\n <foo><&amp;]]></div>", div.outerHtml());

        CDataNode cdata = (CDataNode) div.textNodes().get(0);
        Assert.assertEquals("\n<html>\n <foo><&amp;", cdata.text());
    }

    @Test public void cdataPreservesWhiteSpace() {
        String xml = "<script type=\"text/javascript\">//<![CDATA[\n\n  foo();\n//]]></script>";
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        Assert.assertEquals(xml, doc.outerHtml());

        Assert.assertEquals("//\n\n  foo();\n//", doc.selectFirst("script").text());
    }

    @Test
    public void handlesDodgyXmlDecl() {
        String xml = "<?xml version='1.0'><val>One</val>";
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        Assert.assertEquals("One", doc.select("val").text());
    }

    @Test
    public void handlesLTinScript() {
        // https://github.com/jhy/jsoup/issues/1139
        String html = "<script> var a=\"<?\"; var b=\"?>\"; </script>";
        Document doc = Jsoup.parse(html, "", Parser.xmlParser());
        Assert.assertEquals("<script> var a=\"<!--?\"; var b=\"?-->\"; </script>", doc.html()); // converted from pseudo xmldecl to comment
    }

    @Test public void dropsDuplicateAttributes() {
        // case sensitive, so should drop Four and Five
        String html = "<p One=One ONE=Two one=Three One=Four ONE=Five two=Six two=Seven Two=Eight>Text</p>";
        Parser parser = Parser.xmlParser().setTrackErrors(10);
        Document doc = parser.parseInput(html, "");

        Assert.assertEquals("<p One=\"One\" ONE=\"Two\" one=\"Three\" two=\"Six\" Two=\"Eight\">Text</p>", doc.selectFirst("p").outerHtml());
    }

    @Test public void readerClosedAfterParse() {
        Document doc = Jsoup.parse("Hello", "", Parser.xmlParser());
        TreeBuilder treeBuilder = doc.parser().getTreeBuilder();
        Assert.assertNull(treeBuilder.reader);
        Assert.assertNull(treeBuilder.tokeniser);
    }

    @Test public void xmlParserEnablesXmlOutputAndEscapes() {
        // Test that when using the XML parser, the output mode and escape mode default to XHTML entities
        // https://github.com/jhy/jsoup/issues/1420
        Document doc = Jsoup.parse("<p one='&lt;two&gt;&copy'>Three</p>", "", Parser.xmlParser());
        Assert.assertEquals(doc.outputSettings().syntax(), Document.OutputSettings.Syntax.xml);
        Assert.assertEquals(doc.outputSettings().escapeMode(), Entities.EscapeMode.xhtml);
        Assert.assertEquals("<p one=\"&lt;two>©\">Three</p>", doc.html()); // only the < should be escaped
    }

    @Test public void xmlSyntaxEscapesLtInAttributes() {
        // Regardless of the entity escape mode, make sure < is escaped in attributes when in XML
        Document doc = Jsoup.parse("<p one='&lt;two&gt;&copy'>Three</p>", "", Parser.xmlParser());
        doc.outputSettings().escapeMode(Entities.EscapeMode.extended);
        doc.outputSettings().charset("ascii"); // to make sure &copy; is output
        Assert.assertEquals(doc.outputSettings().syntax(), Document.OutputSettings.Syntax.xml);
        Assert.assertEquals("<p one=\"&lt;two>&copy;\">Three</p>", doc.html());
    }

}
