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
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.TextUtil;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document.OutputSettings;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document.OutputSettings.Syntax;
import com.itextpdf.styledxmlparser.jsoup.parser.ParseSettings;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 Tests for Document.
*/
@Tag("UnitTest")
public class DocumentTest extends ExtendedITextTest {
    private static final String charsetUtf8 = "UTF-8";
    private static final String charsetIso8859 = "ISO-8859-1";


    @Test public void setTextPreservesDocumentStructure() {
        Document doc = Jsoup.parse("<p>Hello</p>");
        doc.text("Replaced");
        Assertions.assertEquals("Replaced", doc.text());
        Assertions.assertEquals("Replaced", doc.body().text());
        Assertions.assertEquals(1, doc.select("head").size());
    }

    @Test public void testTitles() {
        Document noTitle = Jsoup.parse("<p>Hello</p>");
        Document withTitle = Jsoup.parse("<title>First</title><title>Ignore</title><p>Hello</p>");

        Assertions.assertEquals("", noTitle.title());
        noTitle.title("Hello");
        Assertions.assertEquals("Hello", noTitle.title());
        Assertions.assertEquals("Hello", noTitle.select("title").first().text());

        Assertions.assertEquals("First", withTitle.title());
        withTitle.title("Hello");
        Assertions.assertEquals("Hello", withTitle.title());
        Assertions.assertEquals("Hello", withTitle.select("title").first().text());

        Document normaliseTitle = Jsoup.parse("<title>   Hello\nthere   \n   now   \n");
        Assertions.assertEquals("Hello there now", normaliseTitle.title());
    }

    @Test public void testOutputEncoding() {
        Document doc = Jsoup.parse("<p title=π>π & < > </p>");
        // default is utf-8
        Assertions.assertEquals("<p title=\"π\">π &amp; &lt; &gt; </p>", doc.body().html());
        Assertions.assertEquals("UTF-8", doc.outputSettings().charset().name());

        doc.outputSettings().charset("ascii");
        Assertions.assertEquals(Entities.EscapeMode.base, doc.outputSettings().escapeMode());
        Assertions.assertEquals("<p title=\"&#x3c0;\">&#x3c0; &amp; &lt; &gt; </p>", doc.body().html());

        doc.outputSettings().escapeMode(Entities.EscapeMode.extended);
        Assertions.assertEquals("<p title=\"&pi;\">&pi; &amp; &lt; &gt; </p>", doc.body().html());
    }

    @Test public void testXhtmlReferences() {
        Document doc = Jsoup.parse("&lt; &gt; &amp; &quot; &apos; &times;");
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        Assertions.assertEquals("&lt; &gt; &amp; \" ' ×", doc.body().html());
    }

    @Test public void testNormalisesStructure() {
        Document doc = Jsoup.parse("<html><head><script>one</script><noscript><p>two</p></noscript></head><body><p>three</p></body><p>four</p></html>");
        Assertions.assertEquals("<html><head><script>one</script><noscript>&lt;p&gt;two</noscript></head><body><p>three</p><p>four</p></body></html>", TextUtil.stripNewlines(doc.html()));
    }

    @Test public void accessorsWillNormalizeStructure() {
        Document doc = new Document("");
        Assertions.assertEquals("", doc.html());

        Element body = doc.body();
        Assertions.assertEquals("body", body.tagName());
        Element head = doc.head();
        Assertions.assertEquals("head", head.tagName());
        Assertions.assertEquals("<html><head></head><body></body></html>", TextUtil.stripNewlines(doc.html()));
    }

    @Test public void accessorsAreCaseInsensitive() {
        Parser parser = Parser.htmlParser().settings(ParseSettings.preserveCase);
        Document doc = parser.parseInput("<!DOCTYPE html><HTML><HEAD><TITLE>SHOUTY</TITLE></HEAD><BODY>HELLO</BODY></HTML>", "");

        Element body = doc.body();
        Assertions.assertEquals("BODY", body.tagName());
        Assertions.assertEquals("body", body.normalName());
        Element head = doc.head();
        Assertions.assertEquals("HEAD", head.tagName());
        Assertions.assertEquals("body", body.normalName());

        Element root = doc.selectFirst("html");
        Assertions.assertEquals("HTML", root.tagName());
        Assertions.assertEquals("html", root.normalName());
        Assertions.assertEquals("SHOUTY", doc.title());
    }

    @Test public void testClone() {
        Document doc = Jsoup.parse("<title>Hello</title> <p>One<p>Two");
        Document clone = (Document) doc.clone();

        Assertions.assertEquals("<html><head><title>Hello</title> </head><body><p>One</p><p>Two</p></body></html>", TextUtil.stripNewlines(clone.html()));
        clone.title("Hello there");
        clone.select("p").first().text("One more").attr("id", "1");
        Assertions.assertEquals("<html><head><title>Hello there</title> </head><body><p id=\"1\">One more</p><p>Two</p></body></html>", TextUtil.stripNewlines(clone.html()));
        Assertions.assertEquals("<html><head><title>Hello</title> </head><body><p>One</p><p>Two</p></body></html>", TextUtil.stripNewlines(doc.html()));
    }

    @Test public void testClonesDeclarations() {
        Document doc = Jsoup.parse("<!DOCTYPE html><html><head><title>Doctype test");
        Document clone = (Document) doc.clone();

        Assertions.assertEquals(doc.html(), clone.html());
        Assertions.assertEquals("<!doctype html><html><head><title>Doctype test</title></head><body></body></html>",
                TextUtil.stripNewlines(clone.html()));
    }

    @Test public void testLocationFromString() {
        Document doc = Jsoup.parse("<p>Hello");
        Assertions.assertEquals("", doc.location());
    }

    @Test public void testHtmlAndXmlSyntax() {
        String h = "<!DOCTYPE html><body><img async checked='checked' src='&<>\"'>&lt;&gt;&amp;&quot;<foo />bar";
        Document doc = Jsoup.parse(h);

        doc.outputSettings().syntax(Syntax.html);
        Assertions.assertEquals("<!doctype html>\n" +
                "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  <img async checked src=\"&amp;<>&quot;\">&lt;&gt;&amp;\"<foo />bar\n" +
                " </body>\n" +
                "</html>", doc.html());

        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        Assertions.assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  <img async=\"\" checked=\"checked\" src=\"&amp;&lt;>&quot;\" />&lt;&gt;&amp;\"<foo />bar\n" +
                " </body>\n" +
                "</html>", doc.html());
    }

    @Test public void htmlParseDefaultsToHtmlOutputSyntax() {
        Document doc = Jsoup.parse("x");
        Assertions.assertEquals(Syntax.html, doc.outputSettings().syntax());
    }

    @Test public void testHtmlAppendable() {
    	String htmlContent = "<html><head><title>Hello</title></head><body><p>One</p><p>Two</p></body></html>";
    	Document document = Jsoup.parse(htmlContent);
    	OutputSettings outputSettings = new OutputSettings();

    	outputSettings.prettyPrint(false);
    	document.outputSettings(outputSettings);
    	Assertions.assertEquals(htmlContent, document.html(new StringBuffer()).toString());
    }

    @Test public void testOverflowClone() {
        StringBuilder sb = new StringBuilder();
        sb.append("<head><base href='https://jsoup.org/'>");
        for (int i = 0; i < 100000; i++) {
            sb.append("<div>");
        }
        sb.append("<p>Hello <a href='/example.html'>there</a>");

        Document doc = Jsoup.parse(sb.toString());

        String expectedLink = "https://jsoup.org/example.html";
        Assertions.assertEquals(expectedLink, doc.selectFirst("a").attr("abs:href"));
        Document clone = (Document) doc.clone();
        doc.hasSameValue(clone);
        Assertions.assertEquals(expectedLink, clone.selectFirst("a").attr("abs:href"));
    }

    @Test public void DocumentsWithSameContentAreEqual() {
        Document docA = Jsoup.parse("<div/>One");
        Document docB = Jsoup.parse("<div/>One");
        Document docC = Jsoup.parse("<div/>Two");

        Assertions.assertNotEquals(docA, docB);
        Assertions.assertEquals(docA, docA);
        Assertions.assertEquals(docA.hashCode(), docA.hashCode());
        Assertions.assertNotEquals(docA.hashCode(), docC.hashCode());
    }

    @Test public void DocumentsWithSameContentAreVerifiable() {
        Document docA = Jsoup.parse("<div/>One");
        Document docB = Jsoup.parse("<div/>One");
        Document docC = Jsoup.parse("<div/>Two");

        Assertions.assertTrue(docA.hasSameValue(docB));
        Assertions.assertFalse(docA.hasSameValue(docC));
    }

    @Test
    public void testMetaCharsetUpdateUtf8() {
        final Document doc = createHtmlDocument("changeThis");
        doc.updateMetaCharsetElement(true);
        doc.charset(Charset.forName(charsetUtf8));

        final String htmlCharsetUTF8 = "<html>\n" +
                                        " <head>\n" +
                                        "  <meta charset=\"" + charsetUtf8 + "\">\n" +
                                        " </head>\n" +
                                        " <body></body>\n" +
                                        "</html>";
        Assertions.assertEquals(htmlCharsetUTF8, doc.toString());

        Element selectedElement = doc.select("meta[charset]").first();
        Assertions.assertEquals(charsetUtf8, doc.charset().name());
        Assertions.assertEquals(charsetUtf8, selectedElement.attr("charset"));
        Assertions.assertEquals(doc.charset(), doc.outputSettings().charset());
    }

    @Test
    public void testMetaCharsetUpdateIso8859() {
        final Document doc = createHtmlDocument("changeThis");
        doc.updateMetaCharsetElement(true);
        doc.charset(Charset.forName(charsetIso8859));

        final String htmlCharsetISO = "<html>\n" +
                                        " <head>\n" +
                                        "  <meta charset=\"" + charsetIso8859 + "\">\n" +
                                        " </head>\n" +
                                        " <body></body>\n" +
                                        "</html>";
        Assertions.assertEquals(htmlCharsetISO, doc.toString());

        Element selectedElement = doc.select("meta[charset]").first();
        Assertions.assertEquals(charsetIso8859, doc.charset().name());
        Assertions.assertEquals(charsetIso8859, selectedElement.attr("charset"));
        Assertions.assertEquals(doc.charset(), doc.outputSettings().charset());
    }

    @Test
    public void testMetaCharsetUpdateNoCharset() {
        final Document docNoCharset = Document.createShell("");
        docNoCharset.updateMetaCharsetElement(true);
        docNoCharset.charset(Charset.forName(charsetUtf8));

        Assertions.assertEquals(charsetUtf8, docNoCharset.select("meta[charset]").first().attr("charset"));

        final String htmlCharsetUTF8 = "<html>\n" +
                                        " <head>\n" +
                                        "  <meta charset=\"" + charsetUtf8 + "\">\n" +
                                        " </head>\n" +
                                        " <body></body>\n" +
                                        "</html>";
        Assertions.assertEquals(htmlCharsetUTF8, docNoCharset.toString());
    }

    @Test
    public void testMetaCharsetUpdateDisabled() {
        final Document docDisabled = Document.createShell("");

        final String htmlNoCharset = "<html>\n" +
                                        " <head></head>\n" +
                                        " <body></body>\n" +
                                        "</html>";
        Assertions.assertEquals(htmlNoCharset, docDisabled.toString());
        Assertions.assertNull(docDisabled.select("meta[charset]").first());
    }

    @Test
    public void testMetaCharsetUpdateDisabledNoChanges() {
        final Document doc = createHtmlDocument("dontTouch");

        final String htmlCharset = "<html>\n" +
                                    " <head>\n" +
                                    "  <meta charset=\"dontTouch\">\n" +
                                    "  <meta name=\"charset\" content=\"dontTouch\">\n" +
                                    " </head>\n" +
                                    " <body></body>\n" +
                                    "</html>";
        Assertions.assertEquals(htmlCharset, doc.toString());

        Element selectedElement = doc.select("meta[charset]").first();
        Assertions.assertNotNull(selectedElement);
        Assertions.assertEquals("dontTouch", selectedElement.attr("charset"));

        selectedElement = doc.select("meta[name=charset]").first();
        Assertions.assertNotNull(selectedElement);
        Assertions.assertEquals("dontTouch", selectedElement.attr("content"));
    }

    @Test
    public void testMetaCharsetUpdateEnabledAfterCharsetChange() {
        final Document doc = createHtmlDocument("dontTouch");
        doc.charset(Charset.forName(charsetUtf8));

        Element selectedElement = doc.select("meta[charset]").first();
        Assertions.assertEquals(charsetUtf8, selectedElement.attr("charset"));
        Assertions.assertTrue(doc.select("meta[name=charset]").isEmpty());
    }

    @Test
    public void testMetaCharsetUpdateCleanup() {
        final Document doc = createHtmlDocument("dontTouch");
        doc.updateMetaCharsetElement(true);
        doc.charset(Charset.forName(charsetUtf8));

        final String htmlCharsetUTF8 = "<html>\n" +
                                        " <head>\n" +
                                        "  <meta charset=\"" + charsetUtf8 + "\">\n" +
                                        " </head>\n" +
                                        " <body></body>\n" +
                                        "</html>";

        Assertions.assertEquals(htmlCharsetUTF8, doc.toString());
    }

    @Test
    public void testMetaCharsetUpdateXmlUtf8() {
        final Document doc = createXmlDocument("1.0", "changeThis", true);
        doc.updateMetaCharsetElement(true);
        doc.charset(Charset.forName(charsetUtf8));

        final String xmlCharsetUTF8 = "<?xml version=\"1.0\" encoding=\"" + charsetUtf8 + "\"?>\n" +
                                        "<root>\n" +
                                        " node\n" +
                                        "</root>";
        Assertions.assertEquals(xmlCharsetUTF8, doc.toString());

        XmlDeclaration selectedNode = (XmlDeclaration) doc.childNode(0);
        Assertions.assertEquals(charsetUtf8, doc.charset().name());
        Assertions.assertEquals(charsetUtf8, selectedNode.attr("encoding"));
        Assertions.assertEquals(doc.charset(), doc.outputSettings().charset());
    }

    @Test
    public void testMetaCharsetUpdateXmlIso8859() {
        final Document doc = createXmlDocument("1.0", "changeThis", true);
        doc.updateMetaCharsetElement(true);
        doc.charset(Charset.forName(charsetIso8859));

        final String xmlCharsetISO = "<?xml version=\"1.0\" encoding=\"" + charsetIso8859 + "\"?>\n" +
                                        "<root>\n" +
                                        " node\n" +
                                        "</root>";
        Assertions.assertEquals(xmlCharsetISO, doc.toString());

        XmlDeclaration selectedNode = (XmlDeclaration) doc.childNode(0);
        Assertions.assertEquals(charsetIso8859, doc.charset().name());
        Assertions.assertEquals(charsetIso8859, selectedNode.attr("encoding"));
        Assertions.assertEquals(doc.charset(), doc.outputSettings().charset());
    }

    @Test
    public void testMetaCharsetUpdateXmlNoCharset() {
        final Document doc = createXmlDocument("1.0", "none", false);
        doc.updateMetaCharsetElement(true);
        doc.charset(Charset.forName(charsetUtf8));

        final String xmlCharsetUTF8 = "<?xml version=\"1.0\" encoding=\"" + charsetUtf8 + "\"?>\n" +
                                        "<root>\n" +
                                        " node\n" +
                                        "</root>";
        Assertions.assertEquals(xmlCharsetUTF8, doc.toString());

        XmlDeclaration selectedNode = (XmlDeclaration) doc.childNode(0);
        Assertions.assertEquals(charsetUtf8, selectedNode.attr("encoding"));
    }

    @Test
    public void testMetaCharsetUpdateXmlDisabled() {
        final Document doc = createXmlDocument("none", "none", false);

        final String xmlNoCharset = "<root>\n" +
                                    " node\n" +
                                    "</root>";
        Assertions.assertEquals(xmlNoCharset, doc.toString());
    }

    @Test
    public void testMetaCharsetUpdateXmlDisabledNoChanges() {
        final Document doc = createXmlDocument("dontTouch", "dontTouch", true);

        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" +
                                    "<root>\n" +
                                    " node\n" +
                                    "</root>";
        Assertions.assertEquals(xmlCharset, doc.toString());

        XmlDeclaration selectedNode = (XmlDeclaration) doc.childNode(0);
        Assertions.assertEquals("dontTouch", selectedNode.attr("encoding"));
        Assertions.assertEquals("dontTouch", selectedNode.attr("version"));
    }

    @Test
    public void testMetaCharsetUpdatedDisabledPerDefault() {
        final Document doc = createHtmlDocument("none");
        Assertions.assertFalse(doc.updateMetaCharsetElement());
    }

    private Document createHtmlDocument(String charset) {
        final Document doc = Document.createShell("");
        doc.head().appendElement("meta").attr("charset", charset);
        doc.head().appendElement("meta").attr("name", "charset").attr("content", charset);

        return doc;
    }

    private Document createXmlDocument(String version, String charset, boolean addDecl) {
        final Document doc = new Document("");
        doc.appendElement("root").text("node");
        doc.outputSettings().syntax(Syntax.xml);

        if(addDecl) {
            XmlDeclaration decl = new XmlDeclaration("xml", false);
            decl.attr("version", version);
            decl.attr("encoding", charset);
            doc.prependChild(decl);
        }

        return doc;
    }

    @Test
    public void testShiftJisRoundtrip() throws Exception {
        String input =
                "<html>"
                        +   "<head>"
                        +     "<meta http-equiv=\"content-type\" content=\"text/html; charset=Shift_JIS\" />"
                        +   "</head>"
                        +   "<body>"
                        +     "before&nbsp;after"
                        +   "</body>"
                        + "</html>";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII));

        Document doc = Jsoup.parse(is, null, "http://example.com");
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);

        String output = new String(doc.html().getBytes(doc.outputSettings().charset()), doc.outputSettings().charset());

        Assertions.assertFalse(output.contains("?"));
        Assertions.assertTrue(output.contains("&#xa0;") || output.contains("&nbsp;"));
    }

    @Test public void parseAndHtmlOnDifferentThreads() throws InterruptedException {
        String html = "<p>Alrighty then it's not \uD83D\uDCA9. <span>Next</span></p>"; // 💩
        String asci = "<p>Alrighty then it's not &#x1f4a9;. <span>Next</span></p>";

        final Document doc = Jsoup.parse(html);
        final String[] out = new String[1];
        final Elements p = doc.select("p");
        Assertions.assertEquals(html, p.outerHtml());

        Thread thread = new Thread(() -> {
            out[0] = p.outerHtml();
            doc.outputSettings().charset(StandardCharsets.US_ASCII);
        });
        thread.start();
        thread.join();

        Assertions.assertEquals(html, out[0]);
        Assertions.assertEquals(StandardCharsets.US_ASCII, doc.outputSettings().charset());
        Assertions.assertEquals(asci, p.outerHtml());
    }

    @Test public void testDocumentTypeGet() {
        String html = "\n\n<!-- comment -->  <!doctype html><p>One</p>";
        Document doc = Jsoup.parse(html);
        DocumentType documentType = doc.documentType();
        Assertions.assertNotNull(documentType);
        Assertions.assertEquals("html", documentType.name());
    }

    @Test public void framesetSupportsBodyMethod() {
        String html = "<html><head><title>Frame Test</title></head><frameset id=id><frame src=foo.html></frameset>";
        Document doc = Jsoup.parse(html);
        Element head = doc.head();
        Assertions.assertNotNull(head);
        Assertions.assertEquals("Frame Test", doc.title());

        // Frameset docs per html5 spec have no body element - but instead a frameset elelemt
        Assertions.assertNull(doc.selectFirst("body"));
        Element frameset = doc.selectFirst("frameset");
        Assertions.assertNotNull(frameset);

        // the body() method returns body or frameset and does not otherwise modify the document
        // doing it in body() vs parse keeps the html close to original for round-trip option
        Element body = doc.body();
        Assertions.assertNotNull(body);
        Assertions.assertSame(frameset, body);
        Assertions.assertEquals("frame", body.child(0).tagName());

        Assertions.assertNull(doc.selectFirst("body")); // did not vivify a body element

        String expected = "<html>\n" +
            " <head>\n" +
            "  <title>Frame Test</title>\n" +
            " </head>\n" +
            " <frameset id=\"id\">\n" +
            "  <frame src=\"foo.html\">\n" +
            " </frameset>\n" +
            "</html>";
        Assertions.assertEquals(expected, doc.html());
    }
}
