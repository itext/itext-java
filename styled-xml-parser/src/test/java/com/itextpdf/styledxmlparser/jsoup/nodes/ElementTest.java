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
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.TextUtil;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import com.itextpdf.styledxmlparser.jsoup.select.Evaluator;
import com.itextpdf.styledxmlparser.jsoup.select.NodeFilter;
import com.itextpdf.styledxmlparser.jsoup.select.NodeVisitor;
import com.itextpdf.styledxmlparser.jsoup.select.QueryParser;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 Tests for Element (DOM stuff mostly).
*/
@org.junit.jupiter.api.Tag("UnitTest")
public class ElementTest extends ExtendedITextTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    private static void validateScriptContents(String src, Element el) {
        Assertions.assertEquals("", el.text()); // it's not text
        Assertions.assertEquals("", el.ownText());
        Assertions.assertEquals("", el.wholeText());
        Assertions.assertEquals(src, el.html());
        Assertions.assertEquals(src, el.data());
    }

    private static void validateXmlScriptContents(Element el) {
        Assertions.assertEquals("var foo = 5 < 2; var bar = 1 && 2;", el.text());
        Assertions.assertEquals("var foo = 5 < 2; var bar = 1 && 2;", el.ownText());
        Assertions.assertEquals("var foo = 5 < 2;\nvar bar = 1 && 2;", el.wholeText());
        Assertions.assertEquals("var foo = 5 &lt; 2;\nvar bar = 1 &amp;&amp; 2;", el.html());
        Assertions.assertEquals("", el.data());
    }

    @Test
    public void testId() {
        Document doc = Jsoup.parse("<div id=Foo>");
        Element el = doc.selectFirst("div");
        Assertions.assertEquals("Foo", el.id());
    }

    @Test
    public void testSetId() {
        Document doc = Jsoup.parse("<div id=Boo>");
        Element el = doc.selectFirst("div");
        el.id("Foo");
        Assertions.assertEquals("Foo", el.id());
    }

    @Test
    public void getElementsByTagName() {
        Document doc = Jsoup.parse(reference);
        List<Element> divs = doc.getElementsByTag("div");
        Assertions.assertEquals(2, divs.size());
        Assertions.assertEquals("div1", divs.get(0).id());
        Assertions.assertEquals("div2", divs.get(1).id());

        List<Element> ps = doc.getElementsByTag("p");
        Assertions.assertEquals(2, ps.size());
        Assertions.assertEquals("Hello", ((TextNode) ps.get(0).childNode(0)).getWholeText());
        Assertions.assertEquals("Another ", ((TextNode) ps.get(1).childNode(0)).getWholeText());
        List<Element> ps2 = doc.getElementsByTag("P");
        Assertions.assertEquals(ps, ps2);

        List<Element> imgs = doc.getElementsByTag("img");
        Assertions.assertEquals("foo.png", imgs.get(0).attr("src"));

        List<Element> empty = doc.getElementsByTag("fff");
        Assertions.assertEquals(0, empty.size());
    }

    @Test
    public void getNamespacedElementsByTag() {
        Document doc = Jsoup.parse("<div><abc:def id=1>Hello</abc:def></div>");
        Elements els = doc.getElementsByTag("abc:def");
        Assertions.assertEquals(1, els.size());
        Assertions.assertEquals("1", els.first().id());
        Assertions.assertEquals("abc:def", els.first().tagName());
    }

    @Test
    public void testGetElementById() {
        Document doc = Jsoup.parse(reference);
        Element div = doc.getElementById("div1");
        Assertions.assertEquals("div1", div.id());
        Assertions.assertNull(doc.getElementById("none"));

        Document doc2 = Jsoup.parse("<div id=1><div id=2><p>Hello <span id=2>world!</span></p></div></div>");
        Element div2 = doc2.getElementById("2");
        Assertions.assertEquals("div", div2.tagName()); // not the span
        Element span = div2.child(0).getElementById("2"); // called from <p> context should be span
        Assertions.assertEquals("span", span.tagName());
    }

    @Test
    public void testGetText() {
        Document doc = Jsoup.parse(reference);
        Assertions.assertEquals("Hello Another element", doc.text());
        Assertions.assertEquals("Another element", doc.getElementsByTag("p").get(1).text());
    }

    @Test
    public void testGetChildText() {
        Document doc = Jsoup.parse("<p>Hello <b>there</b> now");
        Element p = doc.select("p").first();
        Assertions.assertEquals("Hello there now", p.text());
        Assertions.assertEquals("Hello now", p.ownText());
    }

    @Test
    public void testNormalisesText() {
        String h = "<p>Hello<p>There.</p> \n <p>Here <b>is</b> \n s<b>om</b>e text.";
        Document doc = Jsoup.parse(h);
        String text = doc.text();
        Assertions.assertEquals("Hello There. Here is some text.", text);
    }

    @Test
    public void testKeepsPreText() {
        String h = "<p>Hello \n \n there.</p> <div><pre>  What's \n\n  that?</pre>";
        Document doc = Jsoup.parse(h);
        Assertions.assertEquals("Hello there.   What's \n\n  that?", doc.text());
    }

    @Test
    public void testKeepsPreTextInCode() {
        String h = "<pre><code>code\n\ncode</code></pre>";
        Document doc = Jsoup.parse(h);
        Assertions.assertEquals("code\n\ncode", doc.text());
        Assertions.assertEquals("<pre><code>code\n\ncode</code></pre>", doc.body().html());
    }

    @Test
    public void testKeepsPreTextAtDepth() {
        String h = "<pre><code><span><b>code\n\ncode</b></span></code></pre>";
        Document doc = Jsoup.parse(h);
        Assertions.assertEquals("code\n\ncode", doc.text());
        Assertions.assertEquals("<pre><code><span><b>code\n\ncode</b></span></code></pre>", doc.body().html());
    }

    @Test
    public void testBrHasSpace() {
        Document doc = Jsoup.parse("<p>Hello<br>there</p>");
        Assertions.assertEquals("Hello there", doc.text());
        Assertions.assertEquals("Hello there", doc.select("p").first().ownText());

        doc = Jsoup.parse("<p>Hello <br> there</p>");
        Assertions.assertEquals("Hello there", doc.text());
    }

    @Test
    public void testWholeText() {
        Document doc = Jsoup.parse("<p> Hello\nthere &nbsp;  </p>");
        Assertions.assertEquals(" Hello\nthere    ", doc.wholeText());

        doc = Jsoup.parse("<p>Hello  \n  there</p>");
        Assertions.assertEquals("Hello  \n  there", doc.wholeText());

        doc = Jsoup.parse("<p>Hello  <div>\n  there</div></p>");
        Assertions.assertEquals("Hello  \n  there", doc.wholeText());
    }

    @Test
    public void testGetSiblings() {
        Document doc = Jsoup.parse("<div><p>Hello<p id=1>there<p>this<p>is<p>an<p id=last>element</div>");
        Element p = doc.getElementById("1");
        Assertions.assertEquals("there", p.text());
        Assertions.assertEquals("Hello", p.previousElementSibling().text());
        Assertions.assertEquals("this", p.nextElementSibling().text());
        Assertions.assertEquals("Hello", p.firstElementSibling().text());
        Assertions.assertEquals("element", p.lastElementSibling().text());
    }

    @Test
    public void testGetSiblingsWithDuplicateContent() {
        Document doc = Jsoup.parse("<div><p>Hello<p id=1>there<p>this<p>this<p>is<p>an<p id=last>element</div>");
        Element p = doc.getElementById("1");
        Assertions.assertEquals("there", p.text());
        Assertions.assertEquals("Hello", p.previousElementSibling().text());
        Assertions.assertEquals("this", p.nextElementSibling().text());
        Assertions.assertEquals("this", p.nextElementSibling().nextElementSibling().text());
        Assertions.assertEquals("is", p.nextElementSibling().nextElementSibling().nextElementSibling().text());
        Assertions.assertEquals("Hello", p.firstElementSibling().text());
        Assertions.assertEquals("element", p.lastElementSibling().text());
    }

    @Test
    public void testFirstElementSiblingOnOrphan() {
        Element p = new Element("p");
        Assertions.assertSame(p, p.firstElementSibling());
        Assertions.assertSame(p, p.lastElementSibling());
    }

    @Test
    public void testFirstAndLastSiblings() {
        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three");
        Element div = doc.selectFirst("div");
        Element one = div.child(0);
        Element two = div.child(1);
        Element three = div.child(2);

        Assertions.assertSame(one, one.firstElementSibling());
        Assertions.assertSame(one, two.firstElementSibling());
        Assertions.assertSame(three, three.lastElementSibling());
        Assertions.assertSame(three, two.lastElementSibling());
    }

    @Test
    public void testGetParents() {
        Document doc = Jsoup.parse("<div><p>Hello <span>there</span></div>");
        Element span = doc.select("span").first();
        Elements parents = span.parents();

        Assertions.assertEquals(4, parents.size());
        Assertions.assertEquals("p", parents.get(0).tagName());
        Assertions.assertEquals("div", parents.get(1).tagName());
        Assertions.assertEquals("body", parents.get(2).tagName());
        Assertions.assertEquals("html", parents.get(3).tagName());
    }

    @Test
    public void testElementSiblingIndex() {
        Document doc = Jsoup.parse("<div><p>One</p>...<p>Two</p>...<p>Three</p>");
        Elements ps = doc.select("p");
        Assertions.assertEquals(0, ps.get(0).elementSiblingIndex());
        Assertions.assertEquals(1, ps.get(1).elementSiblingIndex());
        Assertions.assertEquals(2, ps.get(2).elementSiblingIndex());
    }

    @Test
    public void testElementSiblingIndexSameContent() {
        Document doc = Jsoup.parse("<div><p>One</p>...<p>One</p>...<p>One</p>");
        Elements ps = doc.select("p");
        Assertions.assertEquals(0, ps.get(0).elementSiblingIndex());
        Assertions.assertEquals(1, ps.get(1).elementSiblingIndex());
        Assertions.assertEquals(2, ps.get(2).elementSiblingIndex());
    }

    @Test
    public void testGetElementsWithClass() {
        Document doc = Jsoup.parse("<div class='mellow yellow'><span class=mellow>Hello <b class='yellow'>Yellow!</b></span><p>Empty</p></div>");

        List<Element> els = doc.getElementsByClass("mellow");
        Assertions.assertEquals(2, els.size());
        Assertions.assertEquals("div", els.get(0).tagName());
        Assertions.assertEquals("span", els.get(1).tagName());

        List<Element> els2 = doc.getElementsByClass("yellow");
        Assertions.assertEquals(2, els2.size());
        Assertions.assertEquals("div", els2.get(0).tagName());
        Assertions.assertEquals("b", els2.get(1).tagName());

        List<Element> none = doc.getElementsByClass("solo");
        Assertions.assertEquals(0, none.size());
    }

    @Test
    public void testGetElementsWithAttribute() {
        Document doc = Jsoup.parse("<div style='bold'><p title=qux><p><b style></b></p></div>");
        List<Element> els = doc.getElementsByAttribute("style");
        Assertions.assertEquals(2, els.size());
        Assertions.assertEquals("div", els.get(0).tagName());
        Assertions.assertEquals("b", els.get(1).tagName());

        List<Element> none = doc.getElementsByAttribute("class");
        Assertions.assertEquals(0, none.size());
    }

    @Test
    public void testGetElementsWithAttributeDash() {
        Document doc = Jsoup.parse("<meta http-equiv=content-type value=utf8 id=1> <meta name=foo content=bar id=2> <div http-equiv=content-type value=utf8 id=3>");
        Elements meta = doc.select("meta[http-equiv=content-type], meta[charset]");
        Assertions.assertEquals(1, meta.size());
        Assertions.assertEquals("1", meta.first().id());
    }

    @Test
    public void testGetElementsWithAttributeValue() {
        Document doc = Jsoup.parse("<div style='bold'><p><p><b style></b></p></div>");
        List<Element> els = doc.getElementsByAttributeValue("style", "bold");
        Assertions.assertEquals(1, els.size());
        Assertions.assertEquals("div", els.get(0).tagName());

        List<Element> none = doc.getElementsByAttributeValue("style", "none");
        Assertions.assertEquals(0, none.size());
    }

    @Test
    public void testClassDomMethods() {
        Document doc = Jsoup.parse("<div><span class=' mellow yellow '>Hello <b>Yellow</b></span></div>");
        List<Element> els = doc.getElementsByAttribute("class");
        Element span = els.get(0);
        Assertions.assertEquals("mellow yellow", span.className());
        Assertions.assertTrue(span.hasClass("mellow"));
        Assertions.assertTrue(span.hasClass("yellow"));
        Set<String> classes = span.classNames();
        Assertions.assertEquals(2, classes.size());
        Assertions.assertTrue(classes.contains("mellow"));
        Assertions.assertTrue(classes.contains("yellow"));

        Assertions.assertEquals("", doc.className());
        classes = doc.classNames();
        Assertions.assertEquals(0, classes.size());
        Assertions.assertFalse(doc.hasClass("mellow"));
    }

    @Test
    public void testHasClassDomMethods() {
        Tag tag = Tag.valueOf("a");
        Attributes attribs = new Attributes();
        Element el = new Element(tag, "", attribs);

        attribs.put("class", "toto");
        boolean hasClass = el.hasClass("toto");
        Assertions.assertTrue(hasClass);

        attribs.put("class", " toto");
        hasClass = el.hasClass("toto");
        Assertions.assertTrue(hasClass);

        attribs.put("class", "toto ");
        hasClass = el.hasClass("toto");
        Assertions.assertTrue(hasClass);

        attribs.put("class", "\ttoto ");
        hasClass = el.hasClass("toto");
        Assertions.assertTrue(hasClass);

        attribs.put("class", "  toto ");
        hasClass = el.hasClass("toto");
        Assertions.assertTrue(hasClass);

        attribs.put("class", "ab");
        hasClass = el.hasClass("toto");
        Assertions.assertFalse(hasClass);

        attribs.put("class", "     ");
        hasClass = el.hasClass("toto");
        Assertions.assertFalse(hasClass);

        attribs.put("class", "tototo");
        hasClass = el.hasClass("toto");
        Assertions.assertFalse(hasClass);

        attribs.put("class", "raulpismuth  ");
        hasClass = el.hasClass("raulpismuth");
        Assertions.assertTrue(hasClass);

        attribs.put("class", " abcd  raulpismuth efgh ");
        hasClass = el.hasClass("raulpismuth");
        Assertions.assertTrue(hasClass);

        attribs.put("class", " abcd efgh raulpismuth");
        hasClass = el.hasClass("raulpismuth");
        Assertions.assertTrue(hasClass);

        attribs.put("class", " abcd efgh raulpismuth ");
        hasClass = el.hasClass("raulpismuth");
        Assertions.assertTrue(hasClass);
    }

    @Test
    public void testClassUpdates() {
        Document doc = Jsoup.parse("<div class='mellow yellow'></div>");
        Element div = doc.select("div").first();

        div.addClass("green");
        Assertions.assertEquals("mellow yellow green", div.className());
        div.removeClass("red"); // noop
        div.removeClass("yellow");
        Assertions.assertEquals("mellow green", div.className());
        div.toggleClass("green").toggleClass("red");
        Assertions.assertEquals("mellow red", div.className());
    }

    @Test
    public void testOuterHtml() {
        Document doc = Jsoup.parse("<div title='Tags &amp;c.'><img src=foo.png><p><!-- comment -->Hello<p>there");
        Assertions.assertEquals("<html><head></head><body><div title=\"Tags &amp;c.\"><img src=\"foo.png\"><p><!-- comment -->Hello</p><p>there</p></div></body></html>",
            TextUtil.stripNewlines(doc.outerHtml()));
    }

    @Test
    public void testInnerHtml() {
        Document doc = Jsoup.parse("<div>\n <p>Hello</p> </div>");
        Assertions.assertEquals("<p>Hello</p>", doc.getElementsByTag("div").get(0).html());
    }

    @Test
    public void testFormatHtml() {
        Document doc = Jsoup.parse("<title>Format test</title><div><p>Hello <span>jsoup <span>users</span></span></p><p>Good.</p></div>");
        Assertions.assertEquals("<html>\n <head>\n  <title>Format test</title>\n </head>\n <body>\n  <div>\n   <p>Hello <span>jsoup <span>users</span></span></p>\n   <p>Good.</p>\n  </div>\n </body>\n</html>", doc.html());
    }

    @Test
    public void testFormatOutline() {
        Document doc = Jsoup.parse("<title>Format test</title><div><p>Hello <span>jsoup <span>users</span></span></p><p>Good.</p></div>");
        doc.outputSettings().outline(true);
        Assertions.assertEquals("<html>\n <head>\n  <title>Format test</title>\n </head>\n <body>\n  <div>\n   <p>\n    Hello \n    <span>\n     jsoup \n     <span>users</span>\n    </span>\n   </p>\n   <p>Good.</p>\n  </div>\n </body>\n</html>", doc.html());
    }

    @Test
    public void testSetIndent() {
        Document doc = Jsoup.parse("<div><p>Hello\nthere</p></div>");
        doc.outputSettings().indentAmount(0);
        Assertions.assertEquals("<html>\n<head></head>\n<body>\n<div>\n<p>Hello there</p>\n</div>\n</body>\n</html>", doc.html());
    }

    @Test
    public void testNotPretty() {
        Document doc = Jsoup.parse("<div>   \n<p>Hello\n there\n</p></div>");
        doc.outputSettings().prettyPrint(false);
        Assertions.assertEquals("<html><head></head><body><div>   \n<p>Hello\n there\n</p></div></body></html>", doc.html());

        Element div = doc.select("div").first();
        Assertions.assertEquals("   \n<p>Hello\n there\n</p>", div.html());
    }

    @Test
    public void testNotPrettyWithEnDashBody() {
        String html = "<div><span>1:15</span>&ndash;<span>2:15</span>&nbsp;p.m.</div>";
        Document document = Jsoup.parse(html);
        document.outputSettings().prettyPrint(false);

        Assertions.assertEquals("<div><span>1:15</span>–<span>2:15</span>&nbsp;p.m.</div>", document.body().html());
    }

    @Test
    public void testPrettyWithEnDashBody() {
        String html = "<div><span>1:15</span>&ndash;<span>2:15</span>&nbsp;p.m.</div>";
        Document document = Jsoup.parse(html);

        Assertions.assertEquals("<div>\n <span>1:15</span>–<span>2:15</span>&nbsp;p.m.\n</div>", document.body().html());
    }

    @Test
    public void testPrettyAndOutlineWithEnDashBody() {
        String html = "<div><span>1:15</span>&ndash;<span>2:15</span>&nbsp;p.m.</div>";
        Document document = Jsoup.parse(html);
        document.outputSettings().outline(true);

        Assertions.assertEquals("<div>\n <span>1:15</span>\n –\n <span>2:15</span>\n &nbsp;p.m.\n</div>", document.body().html());
    }

    @Test
    public void testBasicFormats() {
        String html = "<span>0</span>.<div><span>1</span>-<span>2</span><p><span>3</span>-<span>4</span><div>5</div>";
        Document doc = Jsoup.parse(html);
        Assertions.assertEquals(
            "<span>0</span>.\n" +
                "<div>\n" +
                " <span>1</span>-<span>2</span>\n" +
                " <p><span>3</span>-<span>4</span></p>\n" +
                " <div>\n" +
                "  5\n" +
                " </div>\n" +
                "</div>", doc.body().html());
    }

    @Test
    public void testEmptyElementFormatHtml() {
        // don't put newlines into empty blocks
        Document doc = Jsoup.parse("<section><div></div></section>");
        Assertions.assertEquals("<section>\n <div></div>\n</section>", doc.select("section").first().outerHtml());
    }

    @Test
    public void testNoIndentOnScriptAndStyle() {
        // don't newline+indent closing </script> and </style> tags
        Document doc = Jsoup.parse("<script>one\ntwo</script>\n<style>three\nfour</style>");
        Assertions.assertEquals("<script>one\ntwo</script> \n<style>three\nfour</style>", doc.head().html());
    }

    @Test
    public void testContainerOutput() {
        Document doc = Jsoup.parse("<title>Hello there</title> <div><p>Hello</p><p>there</p></div> <div>Another</div>");
        Assertions.assertEquals("<title>Hello there</title>", doc.select("title").first().outerHtml());
        Assertions.assertEquals("<div>\n <p>Hello</p>\n <p>there</p>\n</div>", doc.select("div").first().outerHtml());
        Assertions.assertEquals("<div>\n <p>Hello</p>\n <p>there</p>\n</div> \n<div>\n Another\n</div>", doc.select("body").first().html());
    }

    @Test
    public void testSetText() {
        String h = "<div id=1>Hello <p>there <b>now</b></p></div>";
        Document doc = Jsoup.parse(h);
        Assertions.assertEquals("Hello there now", doc.text()); // need to sort out node whitespace
        Assertions.assertEquals("there now", doc.select("p").get(0).text());

        Element div = doc.getElementById("1").text("Gone");
        Assertions.assertEquals("Gone", div.text());
        Assertions.assertEquals(0, doc.select("p").size());
    }

    @Test
    public void testAddNewElement() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.appendElement("p").text("there");
        ((Element) div.appendElement("P").attr("CLASS", "second")).text("now");
        // manually specifying tag and attributes should maintain case based on parser settings
        Assertions.assertEquals("<html><head></head><body><div id=\"1\"><p>Hello</p><p>there</p><p class=\"second\">now</p></div></body></html>",
            TextUtil.stripNewlines(doc.html()));

        // check sibling index (with short circuit on reindexChildren):
        Elements ps = doc.select("p");
        for (int i = 0; i < ps.size(); i++) {
            Assertions.assertEquals(i, ps.get(i).siblingIndex);
        }
    }

    @Test
    public void testAddBooleanAttribute() {
        Element div = new Element(Tag.valueOf("div"), "");

        div.attr("true", true);

        div.attr("false", "value");
        div.attr("false", false);

        Assertions.assertTrue(div.hasAttr("true"));
        Assertions.assertEquals("", div.attr("true"));

        List<Attribute> attributes = div.attributes().asList();
        Assertions.assertEquals(1, attributes.size());
        Assertions.assertFalse(div.hasAttr("false"));

        Assertions.assertEquals("<div true></div>", div.outerHtml());
    }

    @Test
    public void testAppendRowToTable() {
        Document doc = Jsoup.parse("<table><tr><td>1</td></tr></table>");
        Element table = doc.select("tbody").first();
        table.append("<tr><td>2</td></tr>");

        Assertions.assertEquals("<table><tbody><tr><td>1</td></tr><tr><td>2</td></tr></tbody></table>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void testPrependRowToTable() {
        Document doc = Jsoup.parse("<table><tr><td>1</td></tr></table>");
        Element table = doc.select("tbody").first();
        table.prepend("<tr><td>2</td></tr>");

        Assertions.assertEquals("<table><tbody><tr><td>2</td></tr><tr><td>1</td></tr></tbody></table>", TextUtil.stripNewlines(doc.body().html()));

        // check sibling index (reindexChildren):
        Elements ps = doc.select("tr");
        for (int i = 0; i < ps.size(); i++) {
            Assertions.assertEquals(i, ps.get(i).siblingIndex);
        }
    }

    @Test
    public void testPrependElement() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.prependElement("p").text("Before");
        Assertions.assertEquals("Before", div.child(0).text());
        Assertions.assertEquals("Hello", div.child(1).text());
    }

    @Test
    public void testAddNewText() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.appendText(" there & now >");
        Assertions.assertEquals("<p>Hello</p> there &amp; now &gt;", TextUtil.stripNewlines(div.html()));
    }

    @Test
    public void testPrependText() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.prependText("there & now > ");
        Assertions.assertEquals("there & now > Hello", div.text());
        Assertions.assertEquals("there &amp; now &gt; <p>Hello</p>", TextUtil.stripNewlines(div.html()));
    }

    @Test
    public void testThrowsOnAddNullText() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
            Element div = doc.getElementById("1");
            div.appendText(null);
        });
    }

    @Test
    public void testThrowsOnPrependNullText() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
            Element div = doc.getElementById("1");
            div.prependText(null);
        });
    }

    @Test
    public void testAddNewHtml() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.append("<p>there</p><p>now</p>");
        Assertions.assertEquals("<p>Hello</p><p>there</p><p>now</p>", TextUtil.stripNewlines(div.html()));

        // check sibling index (no reindexChildren):
        Elements ps = doc.select("p");
        for (int i = 0; i < ps.size(); i++) {
            Assertions.assertEquals(i, ps.get(i).siblingIndex);
        }
    }

    @Test
    public void testPrependNewHtml() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.prepend("<p>there</p><p>now</p>");
        Assertions.assertEquals("<p>there</p><p>now</p><p>Hello</p>", TextUtil.stripNewlines(div.html()));

        // check sibling index (reindexChildren):
        Elements ps = doc.select("p");
        for (int i = 0; i < ps.size(); i++) {
            Assertions.assertEquals(i, ps.get(i).siblingIndex);
        }
    }

    @Test
    public void testSetHtml() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.html("<p>there</p><p>now</p>");
        Assertions.assertEquals("<p>there</p><p>now</p>", TextUtil.stripNewlines(div.html()));
    }

    @Test
    public void testSetHtmlTitle() {
        Document doc = Jsoup.parse("<html><head id=2><title id=1></title></head></html>");

        Element title = doc.getElementById("1");
        title.html("good");
        Assertions.assertEquals("good", title.html());
        title.html("<i>bad</i>");
        Assertions.assertEquals("&lt;i&gt;bad&lt;/i&gt;", title.html());

        Element head = doc.getElementById("2");
        head.html("<title><i>bad</i></title>");
        Assertions.assertEquals("<title>&lt;i&gt;bad&lt;/i&gt;</title>", head.html());
    }

    @Test
    public void testWrap() {
        Document doc = Jsoup.parse("<div><p>Hello</p><p>There</p></div>");
        Element p = doc.select("p").first();
        p.wrap("<div class='head'></div>");
        Assertions.assertEquals("<div><div class=\"head\"><p>Hello</p></div><p>There</p></div>", TextUtil.stripNewlines(doc.body().html()));

        Element ret = (Element) p.wrap("<div><div class=foo></div><p>What?</p></div>");
        Assertions.assertEquals("<div><div class=\"head\"><div><div class=\"foo\"><p>Hello</p></div><p>What?</p></div></div><p>There</p></div>",
            TextUtil.stripNewlines(doc.body().html()));

        Assertions.assertEquals(ret, p);
    }

    @Test
    public void testWrapNoop() {
        Document doc = Jsoup.parse("<div><p>Hello</p></div>");
        Node p = doc.select("p").first();
        Node wrapped = p.wrap("Some junk");
        Assertions.assertSame(p, wrapped);
        Assertions.assertEquals("<div><p>Hello</p></div>", TextUtil.stripNewlines(doc.body().html()));
        // should be a NOOP
    }

    @Test
    public void testWrapOnOrphan() {
        Element orphan = new Element("span").text("Hello!");
        Assertions.assertFalse(orphan.hasParent());
        Element wrapped = (Element) orphan.wrap("<div></div> There!");
        Assertions.assertSame(orphan, wrapped);
        Assertions.assertTrue(orphan.hasParent()); // should now be in the DIV
        Element parent = (Element) orphan.parent();
        Assertions.assertNotNull(parent);
        Assertions.assertEquals("div", ((Element) parent).tagName());
        Assertions.assertEquals("<div>\n <span>Hello!</span>\n</div>", parent.outerHtml());
    }

    @Test
    public void testWrapArtificialStructure() {
        // div normally couldn't get into a p, but explicitly want to wrap
        Document doc = Jsoup.parse("<p>Hello <i>there</i> now.");
        Element i = doc.selectFirst("i");
        i.wrap("<div id=id1></div> quite");
        Assertions.assertEquals("div", ((Element) i.parent()).tagName());
        Assertions.assertEquals("<p>Hello <div id=\"id1\"><i>there</i></div> quite now.</p>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void before() {
        Document doc = Jsoup.parse("<div><p>Hello</p><p>There</p></div>");
        Element p1 = doc.select("p").first();
        p1.before("<div>one</div><div>two</div>");
        Assertions.assertEquals("<div><div>one</div><div>two</div><p>Hello</p><p>There</p></div>", TextUtil.stripNewlines(doc.body().html()));

        doc.select("p").last().before("<p>Three</p><!-- four -->");
        Assertions.assertEquals("<div><div>one</div><div>two</div><p>Hello</p><p>Three</p><!-- four --><p>There</p></div>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void after() {
        Document doc = Jsoup.parse("<div><p>Hello</p><p>There</p></div>");
        Element p1 = doc.select("p").first();
        p1.after("<div>one</div><div>two</div>");
        Assertions.assertEquals("<div><p>Hello</p><div>one</div><div>two</div><p>There</p></div>", TextUtil.stripNewlines(doc.body().html()));

        doc.select("p").last().after("<p>Three</p><!-- four -->");
        Assertions.assertEquals("<div><p>Hello</p><div>one</div><div>two</div><p>There</p><p>Three</p><!-- four --></div>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void testWrapWithRemainder() {
        Document doc = Jsoup.parse("<div><p>Hello</p></div>");
        Element p = doc.select("p").first();
        p.wrap("<div class='head'></div><p>There!</p>");
        Assertions.assertEquals("<div><div class=\"head\"><p>Hello</p></div><p>There!</p></div>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void testWrapWithSimpleRemainder() {
        Document doc = Jsoup.parse("<p>Hello");
        Element p = doc.selectFirst("p");
        Element body = (Element) p.parent();
        Assertions.assertNotNull(body);
        Assertions.assertEquals("body", body.tagName());

        p.wrap("<div></div> There");
        Element div = (Element) p.parent();
        Assertions.assertNotNull(div);
        Assertions.assertEquals("div", div.tagName());
        Assertions.assertSame(div, p.parent());
        Assertions.assertSame(body, div.parent());

        Assertions.assertEquals("<div><p>Hello</p></div> There", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void testHasText() {
        Document doc = Jsoup.parse("<div><p>Hello</p><p></p></div>");
        Element div = doc.select("div").first();
        Elements ps = doc.select("p");

        Assertions.assertTrue(div.hasText());
        Assertions.assertTrue(ps.first().hasText());
        Assertions.assertFalse(ps.last().hasText());
    }

    @Test
    public void dataset() {
        Document doc = Jsoup.parse("<div id=1 data-name=jsoup class=new data-package=jar>Hello</div><p id=2>Hello</p>");
        Element div = doc.select("div").first();
        Map<String, String> dataset = div.dataset();
        Attributes attributes = div.attributes();

        // size, get, set, add, remove
        Assertions.assertEquals(2, dataset.size());
        Assertions.assertEquals("jsoup", dataset.get("name"));
        Assertions.assertEquals("jar", dataset.get("package"));

        dataset.put("name", "jsoup updated");
        dataset.put("language", "java");
        dataset.remove("package");

        Assertions.assertEquals(2, dataset.size());
        Assertions.assertEquals(4, attributes.size());
        Assertions.assertEquals("jsoup updated", attributes.get("data-name"));
        Assertions.assertEquals("jsoup updated", dataset.get("name"));
        Assertions.assertEquals("java", attributes.get("data-language"));
        Assertions.assertEquals("java", dataset.get("language"));

        attributes.put("data-food", "bacon");
        Assertions.assertEquals(3, dataset.size());
        Assertions.assertEquals("bacon", dataset.get("food"));

        attributes.put("data-", "empty");
        Assertions.assertNull(dataset.get("")); // data- is not a data attribute

        Element p = doc.select("p").first();
        Assertions.assertEquals(0, p.dataset().size());

    }

    @Test
    public void parentlessToString() {
        Document doc = Jsoup.parse("<img src='foo'>");
        Element img = doc.select("img").first();
        Assertions.assertEquals("<img src=\"foo\">", img.toString());

        img.remove(); // lost its parent
        Assertions.assertEquals("<img src=\"foo\">", img.toString());
    }

    @Test
    public void orphanDivToString() {
        Element orphan = new Element("div").id("foo").text("Hello");
        Assertions.assertEquals("<div id=\"foo\">\n Hello\n</div>", orphan.toString());
    }

    @Test
    public void testClone() {
        Document doc = Jsoup.parse("<div><p>One<p><span>Two</div>");

        Element p = doc.select("p").get(1);
        Element clone = (Element) p.clone();

        Assertions.assertNull(clone.parent()); // should be orphaned
        Assertions.assertEquals(0, clone.siblingIndex);
        Assertions.assertEquals(1, p.siblingIndex);
        Assertions.assertNotNull(p.parent());

        clone.append("<span>Three");
        Assertions.assertEquals("<p><span>Two</span><span>Three</span></p>", TextUtil.stripNewlines(clone.outerHtml()));
        Assertions.assertEquals("<div><p>One</p><p><span>Two</span></p></div>", TextUtil.stripNewlines(doc.body().html())); // not modified

        doc.body().appendChild(clone); // adopt
        Assertions.assertNotNull(clone.parent());
        Assertions.assertEquals("<div><p>One</p><p><span>Two</span></p></div><p><span>Two</span><span>Three</span></p>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void testClonesClassnames() {
        Document doc = Jsoup.parse("<div class='one two'></div>");
        Element div = doc.select("div").first();
        Set<String> classes = div.classNames();
        Assertions.assertEquals(2, classes.size());
        Assertions.assertTrue(classes.contains("one"));
        Assertions.assertTrue(classes.contains("two"));

        Element copy = (Element) div.clone();
        Set<String> copyClasses = copy.classNames();
        Assertions.assertEquals(2, copyClasses.size());
        Assertions.assertTrue(copyClasses.contains("one"));
        Assertions.assertTrue(copyClasses.contains("two"));
        copyClasses.add("three");
        copyClasses.remove("one");

        Assertions.assertTrue(classes.contains("one"));
        Assertions.assertFalse(classes.contains("three"));
        Assertions.assertFalse(copyClasses.contains("one"));
        Assertions.assertTrue(copyClasses.contains("three"));

        Assertions.assertEquals("", div.html());
        Assertions.assertEquals("", copy.html());
    }

    @Test
    public void testShallowClone() {
        String base = "http://example.com/";
        Document doc = Jsoup.parse("<div id=1 class=one><p id=2 class=two>One", base);
        Element d = doc.selectFirst("div");
        Element p = doc.selectFirst("p");
        TextNode t = p.textNodes().get(0);

        Element d2 = (Element) d.shallowClone();
        Element p2 = (Element) p.shallowClone();
        TextNode t2 = (TextNode) t.shallowClone();

        Assertions.assertEquals(1, d.childNodeSize());
        Assertions.assertEquals(0, d2.childNodeSize());

        Assertions.assertEquals(1, p.childNodeSize());
        Assertions.assertEquals(0, p2.childNodeSize());

        Assertions.assertEquals("", p2.text());
        Assertions.assertEquals("One", t2.text());

        Assertions.assertEquals("two", p2.className());
        p2.removeClass("two");
        Assertions.assertEquals("two", p.className());

        d2.append("<p id=3>Three");
        Assertions.assertEquals(1, d2.childNodeSize());
        Assertions.assertEquals("Three", d2.text());
        Assertions.assertEquals("One", d.text());
        Assertions.assertEquals(base, d2.baseUri());
    }

    @Test
    public void testTagNameSet() {
        Document doc = Jsoup.parse("<div><i>Hello</i>");
        doc.select("i").first().tagName("em");
        Assertions.assertEquals(0, doc.select("i").size());
        Assertions.assertEquals(1, doc.select("em").size());
        Assertions.assertEquals("<em>Hello</em>", doc.select("div").first().html());
    }

    @Test
    public void testHtmlContainsOuter() {
        Document doc = Jsoup.parse("<title>Check</title> <div>Hello there</div>");
        doc.outputSettings().indentAmount(0);
        Assertions.assertTrue(doc.html().contains(doc.select("title").outerHtml()));
        Assertions.assertTrue(doc.html().contains(doc.select("div").outerHtml()));
    }

    @Test
    public void testGetTextNodes() {
        Document doc = Jsoup.parse("<p>One <span>Two</span> Three <br> Four</p>");
        List<TextNode> textNodes = doc.select("p").first().textNodes();

        Assertions.assertEquals(3, textNodes.size());
        Assertions.assertEquals("One ", textNodes.get(0).text());
        Assertions.assertEquals(" Three ", textNodes.get(1).text());
        Assertions.assertEquals(" Four", textNodes.get(2).text());

        Assertions.assertEquals(0, doc.select("br").first().textNodes().size());
    }

    @Test
    public void testManipulateTextNodes() {
        Document doc = Jsoup.parse("<p>One <span>Two</span> Three <br> Four</p>");
        Element p = doc.select("p").first();
        List<TextNode> textNodes = p.textNodes();

        textNodes.get(1).text(" three-more ");
        textNodes.get(2).splitText(3).text("-ur");

        Assertions.assertEquals("One Two three-more Fo-ur", p.text());
        Assertions.assertEquals("One three-more Fo-ur", p.ownText());
        Assertions.assertEquals(4, p.textNodes().size()); // grew because of split
    }

    @Test
    public void testGetDataNodes() {
        Document doc = Jsoup.parse("<script>One Two</script> <style>Three Four</style> <p>Fix Six</p>");
        Element script = doc.select("script").first();
        Element style = doc.select("style").first();
        Element p = doc.select("p").first();

        List<DataNode> scriptData = script.dataNodes();
        Assertions.assertEquals(1, scriptData.size());
        Assertions.assertEquals("One Two", scriptData.get(0).getWholeData());

        List<DataNode> styleData = style.dataNodes();
        Assertions.assertEquals(1, styleData.size());
        Assertions.assertEquals("Three Four", styleData.get(0).getWholeData());

        List<DataNode> pData = p.dataNodes();
        Assertions.assertEquals(0, pData.size());
    }

    @Test
    public void elementIsNotASiblingOfItself() {
        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
        Element p2 = doc.select("p").get(1);

        Assertions.assertEquals("Two", p2.text());
        Elements els = p2.siblingElements();
        Assertions.assertEquals(2, els.size());
        Assertions.assertEquals("<p>One</p>", els.get(0).outerHtml());
        Assertions.assertEquals("<p>Three</p>", els.get(1).outerHtml());
    }

    @Test
    public void testChildThrowsIndexOutOfBoundsOnMissing() {
        Document doc = Jsoup.parse("<div><p>One</p><p>Two</p></div>");
        Element div = doc.select("div").first();

        Assertions.assertEquals(2, div.children().size());
        Assertions.assertEquals("One", div.child(0).text());

        try {
            div.child(3);
            Assertions.fail("Should throw index out of bounds");
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void moveByAppend() {
        // test for https://github.com/jhy/jsoup/issues/239
        // can empty an element and append its children to another element
        Document doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>");
        Element div1 = doc.select("div").get(0);
        Element div2 = doc.select("div").get(1);

        Assertions.assertEquals(4, div1.childNodeSize());
        List<Node> children = div1.childNodes();
        Assertions.assertEquals(4, children.size());

        div2.insertChildren(0, children);

        Assertions.assertEquals(4, children.size()); // children is NOT backed by div1.childNodes but a wrapper, so should still be 4 (but re-parented)
        Assertions.assertEquals(0, div1.childNodeSize());
        Assertions.assertEquals(4, div2.childNodeSize());
        Assertions.assertEquals("<div id=\"1\"></div>\n<div id=\"2\">\n Text \n <p>One</p> Text \n <p>Two</p>\n</div>",
            doc.body().html());
    }

    @Test
    public void insertChildrenArgumentValidation() {
        Document doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>");
        Element div1 = doc.select("div").get(0);
        Element div2 = doc.select("div").get(1);
        List<Node> children = div1.childNodes();

        try {
            div2.insertChildren(6, children);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            div2.insertChildren(-5, children);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            div2.insertChildren(0, (Collection<? extends Node>) null);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void insertChildrenAtPosition() {
        Document doc = Jsoup.parse("<div id=1>Text1 <p>One</p> Text2 <p>Two</p></div><div id=2>Text3 <p>Three</p></div>");
        Element div1 = doc.select("div").get(0);
        Elements p1s = div1.select("p");
        Element div2 = doc.select("div").get(1);

        Assertions.assertEquals(2, div2.childNodeSize());
        div2.insertChildren(-1, p1s);
        Assertions.assertEquals(2, div1.childNodeSize()); // moved two out
        Assertions.assertEquals(4, div2.childNodeSize());
        Assertions.assertEquals(3, p1s.get(1).siblingIndex()); // should be last

        List<Node> els = new ArrayList<>();
        Element el1 = new Element(Tag.valueOf("span"), "").text("Span1");
        Element el2 = new Element(Tag.valueOf("span"), "").text("Span2");
        TextNode tn1 = new TextNode("Text4");
        els.add(el1);
        els.add(el2);
        els.add(tn1);

        Assertions.assertNull(el1.parent());
        div2.insertChildren(-2, els);
        Assertions.assertEquals(div2, el1.parent());
        Assertions.assertEquals(7, div2.childNodeSize());
        Assertions.assertEquals(3, el1.siblingIndex());
        Assertions.assertEquals(4, el2.siblingIndex());
        Assertions.assertEquals(5, tn1.siblingIndex());
    }

    @Test
    public void insertChildrenAsCopy() {
        Document doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>");
        Element div1 = doc.select("div").get(0);
        Element div2 = doc.select("div").get(1);
        Elements ps = (Elements) doc.select("p").clone();
        ps.first().text("One cloned");
        div2.insertChildren(-1, ps);

        Assertions.assertEquals(4, div1.childNodeSize()); // not moved -- cloned
        Assertions.assertEquals(2, div2.childNodeSize());
        Assertions.assertEquals("<div id=\"1\">Text <p>One</p> Text <p>Two</p></div><div id=\"2\"><p>One cloned</p><p>Two</p></div>",
            TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void testCssPath() {
        Document doc = Jsoup.parse("<div id=\"id1\">A</div><div>B</div><div class=\"c1 c2\">C</div>");
        Element divA = doc.select("div").get(0);
        Element divB = doc.select("div").get(1);
        Element divC = doc.select("div").get(2);
        Assertions.assertEquals(divA.cssSelector(), "#id1");
        Assertions.assertEquals(divB.cssSelector(), "html > body > div:nth-child(2)");
        Assertions.assertEquals(divC.cssSelector(), "html > body > div.c1.c2");

        Assertions.assertSame(divA, doc.select(divA.cssSelector()).first());
        Assertions.assertSame(divB, doc.select(divB.cssSelector()).first());
        Assertions.assertSame(divC, doc.select(divC.cssSelector()).first());
    }

    @Test
    public void testCssPathDuplicateIds() {
        // https://github.com/jhy/jsoup/issues/1147 - multiple elements with same ID, use the non-ID form
        Document doc = Jsoup.parse("<article><div id=dupe>A</div><div id=dupe>B</div><div id=dupe class=c1>");
        Element divA = doc.select("div").get(0);
        Element divB = doc.select("div").get(1);
        Element divC = doc.select("div").get(2);

        Assertions.assertEquals(divA.cssSelector(), "html > body > article > div:nth-child(1)");
        Assertions.assertEquals(divB.cssSelector(), "html > body > article > div:nth-child(2)");
        Assertions.assertEquals(divC.cssSelector(), "html > body > article > div.c1");

        Assertions.assertSame(divA, doc.select(divA.cssSelector()).first());
        Assertions.assertSame(divB, doc.select(divB.cssSelector()).first());
        Assertions.assertSame(divC, doc.select(divC.cssSelector()).first());
    }

    @Test
    public void testClassNames() {
        Document doc = Jsoup.parse("<div class=\"c1 c2\">C</div>");
        Element div = doc.select("div").get(0);

        Assertions.assertEquals("c1 c2", div.className());

        final Set<String> set1 = div.classNames();
        final Object[] arr1 = set1.toArray();
        Assertions.assertEquals(2, arr1.length);
        Assertions.assertEquals("c1", arr1[0]);
        Assertions.assertEquals("c2", arr1[1]);

        // Changes to the set should not be reflected in the Elements getters
        set1.add("c3");
        Assertions.assertEquals(2, div.classNames().size());
        Assertions.assertEquals("c1 c2", div.className());

        // Update the class names to a fresh set
        final Set<String> newSet = new LinkedHashSet<>(set1);
        newSet.add("c3");

        div.classNames(newSet);

        Assertions.assertEquals("c1 c2 c3", div.className());

        final Set<String> set2 = div.classNames();
        final Object[] arr2 = set2.toArray();
        Assertions.assertEquals(3, arr2.length);
        Assertions.assertEquals("c1", arr2[0]);
        Assertions.assertEquals("c2", arr2[1]);
        Assertions.assertEquals("c3", arr2[2]);
    }

    @Test
    public void testHashAndEqualsAndValue() {
        // .equals and hashcode are identity. value is content.

        String doc1 = "<div id=1><p class=one>One</p><p class=one>One</p><p class=one>Two</p><p class=two>One</p></div>" +
            "<div id=2><p class=one>One</p><p class=one>One</p><p class=one>Two</p><p class=two>One</p></div>";

        Document doc = Jsoup.parse(doc1);
        Elements els = doc.select("p");

        /*
        for (Element el : els) {
            System.out.println(el.hashCode() + " - " + el.outerHtml());
        }

        0 1534787905 - <p class="one">One</p>
        1 1534787905 - <p class="one">One</p>
        2 1539683239 - <p class="one">Two</p>
        3 1535455211 - <p class="two">One</p>
        4 1534787905 - <p class="one">One</p>
        5 1534787905 - <p class="one">One</p>
        6 1539683239 - <p class="one">Two</p>
        7 1535455211 - <p class="two">One</p>
        */
        Assertions.assertEquals(8, els.size());
        Element e0 = els.get(0);
        Element e1 = els.get(1);
        Element e2 = els.get(2);
        Element e3 = els.get(3);
        Element e4 = els.get(4);
        Element e5 = els.get(5);
        Element e6 = els.get(6);
        Element e7 = els.get(7);

        Assertions.assertEquals(e0, e0);
        Assertions.assertTrue(e0.hasSameValue(e1));
        Assertions.assertTrue(e0.hasSameValue(e4));
        Assertions.assertTrue(e0.hasSameValue(e5));
        Assertions.assertNotEquals(e0, e2);
        Assertions.assertFalse(e0.hasSameValue(e2));
        Assertions.assertFalse(e0.hasSameValue(e3));
        Assertions.assertFalse(e0.hasSameValue(e6));
        Assertions.assertFalse(e0.hasSameValue(e7));

        Assertions.assertEquals(e0.hashCode(), e0.hashCode());
        Assertions.assertNotEquals(e0.hashCode(), (e2.hashCode()));
        Assertions.assertNotEquals(e0.hashCode(), (e3).hashCode());
        Assertions.assertNotEquals(e0.hashCode(), (e6).hashCode());
        Assertions.assertNotEquals(e0.hashCode(), (e7).hashCode());
    }

    @Test
    public void testRelativeUrls() {
        String html = "<body><a href='./one.html'>One</a> <a href='two.html'>two</a> <a href='../three.html'>Three</a> <a href='//example2.com/four/'>Four</a> <a href='https://example2.com/five/'>Five</a> <a>Six</a> <a href=''>Seven</a>";
        Document doc = Jsoup.parse(html, "http://example.com/bar/");
        Elements els = doc.select("a");

        Assertions.assertEquals("http://example.com/bar/one.html", els.get(0).absUrl("href"));
        Assertions.assertEquals("http://example.com/bar/two.html", els.get(1).absUrl("href"));
        Assertions.assertEquals("http://example.com/three.html", els.get(2).absUrl("href"));
        Assertions.assertEquals("http://example2.com/four/", els.get(3).absUrl("href"));
        Assertions.assertEquals("https://example2.com/five/", els.get(4).absUrl("href"));
        Assertions.assertEquals("", els.get(5).absUrl("href"));
        Assertions.assertEquals("http://example.com/bar/", els.get(6).absUrl("href"));
    }

    @Test
    public void testRelativeIdnUrls() {
        String idn = "https://www.测试.测试/";
        String idnFoo = idn + "foo.html?bar";

        Document doc = Jsoup.parse("<a href=''>One</a><a href='/bar.html?qux'>Two</a>", idnFoo);
        Elements els = doc.select("a");
        Element one = els.get(0);
        Element two = els.get(1);
        String hrefOne = one.absUrl("href");
        String hrefTwo = two.absUrl("href");
        Assertions.assertEquals(idnFoo, hrefOne);
        Assertions.assertEquals("https://www.测试.测试/bar.html?qux", hrefTwo);
    }

    @Test
    public void appendMustCorrectlyMoveChildrenInsideOneParentElement() {
        Document doc = new Document("");
        Element body = doc.appendElement("body");
        body.appendElement("div1");
        body.appendElement("div2");
        final Element div3 = body.appendElement("div3");
        div3.text("Check");
        final Element div4 = body.appendElement("div4");

        ArrayList<Element> toMove = new ArrayList<>();
        toMove.add(div3);
        toMove.add(div4);

        body.insertChildren(0, toMove);

        String result = doc.toString().replaceAll("\\s+", "");
        Assertions.assertEquals("<body><div3>Check</div3><div4></div4><div1></div1><div2></div2></body>", result);
    }

    @Test
    public void testHashcodeIsStableWithContentChanges() {
        Element root = new Element(Tag.valueOf("root"), "");

        HashSet<Element> set = new HashSet<>();
        // Add root node:
        set.add(root);

        root.appendChild(new Element(Tag.valueOf("a"), ""));
        Assertions.assertTrue(set.contains(root));
    }

    @Test
    public void testNamespacedElements() {
        // Namespaces with ns:tag in HTML must be translated to ns|tag in CSS.
        String html = "<html><body><fb:comments /></body></html>";
        Document doc = Jsoup.parse(html, "http://example.com/bar/");
        Elements els = doc.select("fb|comments");
        Assertions.assertEquals(1, els.size());
        Assertions.assertEquals("html > body > fb|comments", els.get(0).cssSelector());
    }

    @Test
    public void testChainedRemoveAttributes() {
        String html = "<a one two three four>Text</a>";
        Document doc = Jsoup.parse(html);
        Element a = doc.select("a").first();
        a
            .removeAttr("zero")
            .removeAttr("one")
            .removeAttr("two")
            .removeAttr("three")
            .removeAttr("four")
            .removeAttr("five");
        Assertions.assertEquals("<a>Text</a>", a.outerHtml());
    }

    @Test
    public void testLoopedRemoveAttributes() {
        String html = "<a one two three four>Text</a><p foo>Two</p>";
        Document doc = Jsoup.parse(html);
        for (Element el : doc.getAllElements()) {
            el.clearAttributes();
        }

        Assertions.assertEquals("<a>Text</a>\n<p>Two</p>", doc.body().html());
    }

    @Test
    public void testIs() {
        String html = "<div><p>One <a class=big>Two</a> Three</p><p>Another</p>";
        Document doc = Jsoup.parse(html);
        Element p = doc.select("p").first();

        Assertions.assertTrue(p.is("p"));
        Assertions.assertFalse(p.is("div"));
        Assertions.assertTrue(p.is("p:has(a)"));
        Assertions.assertFalse(p.is("a")); // does not descend
        Assertions.assertTrue(p.is("p:first-child"));
        Assertions.assertFalse(p.is("p:last-child"));
        Assertions.assertTrue(p.is("*"));
        Assertions.assertTrue(p.is("div p"));

        Element q = doc.select("p").last();
        Assertions.assertTrue(q.is("p"));
        Assertions.assertTrue(q.is("p ~ p"));
        Assertions.assertTrue(q.is("p + p"));
        Assertions.assertTrue(q.is("p:last-child"));
        Assertions.assertFalse(q.is("p a"));
        Assertions.assertFalse(q.is("a"));
    }

    @Test
    public void testEvalMethods() {
        Document doc = Jsoup.parse("<div><p>One <a class=big>Two</a> Three</p><p>Another</p>");
        Element p = doc.selectFirst(QueryParser.parse(("p")));
        Assertions.assertEquals("One Three", p.ownText());

        Assertions.assertTrue(p.is(QueryParser.parse("p")));
        Evaluator aEval = QueryParser.parse("a");
        Assertions.assertFalse(p.is(aEval));

        Element a = p.selectFirst(aEval);
        Assertions.assertEquals("div", a.closest(QueryParser.parse("div:has( > p)")).tagName());
        Element body = p.closest(QueryParser.parse("body"));
        Assertions.assertEquals("body", body.nodeName());
    }

    @Test
    public void testClosest() {
        String html = "<article>\n" +
            "  <div id=div-01>Here is div-01\n" +
            "    <div id=div-02>Here is div-02\n" +
            "      <div id=div-03>Here is div-03</div>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "</article>";

        Document doc = Jsoup.parse(html);
        Element el = doc.selectFirst("#div-03");
        Assertions.assertEquals("Here is div-03", el.text());
        Assertions.assertEquals("div-03", el.id());

        Assertions.assertEquals("div-02", el.closest("#div-02").id());
        Assertions.assertEquals(el, el.closest("div div")); // closest div in a div is itself
        Assertions.assertEquals("div-01", el.closest("article > div").id());
        Assertions.assertEquals("article", el.closest(":not(div)").tagName());
        Assertions.assertNull(el.closest("p"));
    }

    @Test
    public void elementByTagName() {
        Element a = new Element("P");
        Assertions.assertEquals("P", a.tagName());
    }

    @Test
    public void testChildrenElements() {
        String html = "<div><p><a>One</a></p><p><a>Two</a></p>Three</div><span>Four</span><foo></foo><img>";
        Document doc = Jsoup.parse(html);
        Element div = doc.select("div").first();
        Element p = doc.select("p").first();
        Element span = doc.select("span").first();
        Element foo = doc.select("foo").first();
        Element img = doc.select("img").first();

        Elements docChildren = div.children();
        Assertions.assertEquals(2, docChildren.size());
        Assertions.assertEquals("<p><a>One</a></p>", docChildren.get(0).outerHtml());
        Assertions.assertEquals("<p><a>Two</a></p>", docChildren.get(1).outerHtml());
        Assertions.assertEquals(3, div.childNodes().size());
        Assertions.assertEquals("Three", div.childNodes().get(2).outerHtml());

        Assertions.assertEquals(1, p.children().size());
        Assertions.assertEquals("One", p.children().text());

        Assertions.assertEquals(0, span.children().size());
        Assertions.assertEquals(1, span.childNodes().size());
        Assertions.assertEquals("Four", span.childNodes().get(0).outerHtml());

        Assertions.assertEquals(0, foo.children().size());
        Assertions.assertEquals(0, foo.childNodes().size());
        Assertions.assertEquals(0, img.children().size());
        Assertions.assertEquals(0, img.childNodes().size());
    }

    @Test
    public void testShadowElementsAreUpdated() {
        String html = "<div><p><a>One</a></p><p><a>Two</a></p>Three</div><span>Four</span><foo></foo><img>";
        Document doc = Jsoup.parse(html);
        Element div = doc.select("div").first();
        Elements els = div.children();
        List<Node> nodes = div.childNodes();

        Assertions.assertEquals(2, els.size()); // the two Ps
        Assertions.assertEquals(3, nodes.size()); // the "Three" textnode

        Element p3 = new Element("p").text("P3");
        Element p4 = new Element("p").text("P4");
        div.insertChildren(1, p3);
        div.insertChildren(3, p4);
        Elements els2 = div.children();

        // first els should not have changed
        Assertions.assertEquals(2, els.size());
        Assertions.assertEquals(4, els2.size());

        Assertions.assertEquals("<p><a>One</a></p>\n" +
            "<p>P3</p>\n" +
            "<p><a>Two</a></p>\n" +
            "<p>P4</p>Three", div.html());
        Assertions.assertEquals("P3", els2.get(1).text());
        Assertions.assertEquals("P4", els2.get(3).text());

        p3.after("<span>Another</span");

        Elements els3 = div.children();
        Assertions.assertEquals(5, els3.size());
        Assertions.assertEquals("span", els3.get(2).tagName());
        Assertions.assertEquals("Another", els3.get(2).text());

        Assertions.assertEquals("<p><a>One</a></p>\n" +
            "<p>P3</p><span>Another</span>\n" +
            "<p><a>Two</a></p>\n" +
            "<p>P4</p>Three", div.html());
    }

    @Test
    public void classNamesAndAttributeNameIsCaseInsensitive() {
        String html = "<p Class='SomeText AnotherText'>One</p>";
        Document doc = Jsoup.parse(html);
        Element p = doc.select("p").first();
        Assertions.assertEquals("SomeText AnotherText", p.className());
        Assertions.assertTrue(p.classNames().contains("SomeText"));
        Assertions.assertTrue(p.classNames().contains("AnotherText"));
        Assertions.assertTrue(p.hasClass("SomeText"));
        Assertions.assertTrue(p.hasClass("sometext"));
        Assertions.assertTrue(p.hasClass("AnotherText"));
        Assertions.assertTrue(p.hasClass("anothertext"));

        Element p1 = doc.select(".SomeText").first();
        Element p2 = doc.select(".sometext").first();
        Element p3 = doc.select("[class=SomeText AnotherText]").first();
        Element p4 = doc.select("[Class=SomeText AnotherText]").first();
        Element p5 = doc.select("[class=sometext anothertext]").first();
        Element p6 = doc.select("[class=SomeText AnotherText]").first();
        Element p7 = doc.select("[class^=sometext]").first();
        Element p8 = doc.select("[class$=nothertext]").first();
        Element p9 = doc.select("[class^=sometext]").first();
        Element p10 = doc.select("[class$=AnotherText]").first();

        Assertions.assertEquals("One", p1.text());
        Assertions.assertEquals(p1, p2);
        Assertions.assertEquals(p1, p3);
        Assertions.assertEquals(p1, p4);
        Assertions.assertEquals(p1, p5);
        Assertions.assertEquals(p1, p6);
        Assertions.assertEquals(p1, p7);
        Assertions.assertEquals(p1, p8);
        Assertions.assertEquals(p1, p9);
        Assertions.assertEquals(p1, p10);
    }

    @Test
    public void testAppendTo() {
        String parentHtml = "<div class='a'></div>";
        String childHtml = "<div class='b'></div><p>Two</p>";

        Document parentDoc = Jsoup.parse(parentHtml);
        Element parent = parentDoc.body();
        Document childDoc = Jsoup.parse(childHtml);

        Element div = childDoc.select("div").first();
        Element p = childDoc.select("p").first();
        Element appendTo1 = div.appendTo(parent);
        Assertions.assertEquals(div, appendTo1);

        Element appendTo2 = p.appendTo(div);
        Assertions.assertEquals(p, appendTo2);

        Assertions.assertEquals("<div class=\"a\"></div>\n<div class=\"b\">\n <p>Two</p>\n</div>", parentDoc.body().html());
        Assertions.assertEquals("", childDoc.body().html()); // got moved out
    }

    @Test
    public void testNormalizesNbspInText() {
        String escaped = "You can't always get what you&nbsp;want.";
        String withNbsp = "You can't always get what you want."; // there is an nbsp char in there
        Document doc = Jsoup.parse("<p>" + escaped);
        Element p = doc.select("p").first();
        Assertions.assertEquals("You can't always get what you want.", p.text()); // text is normalized

        Assertions.assertEquals("<p>" + escaped + "</p>", p.outerHtml()); // html / whole text keeps &nbsp;
        Assertions.assertEquals(withNbsp, p.textNodes().get(0).getWholeText());
        Assertions.assertEquals(160, withNbsp.charAt(29));

        Element matched = doc.select("p:contains(get what you want)").first();
        Assertions.assertEquals("p", matched.nodeName());
        Assertions.assertTrue(matched.is(":containsOwn(get what you want)"));
    }

    @Test
    public void testNormalizesInvisiblesInText() {
        String escaped = "This&shy;is&#x200b;one&shy;long&shy;word";
        String decoded = "This\u00ADis\u200Bone\u00ADlong\u00ADword"; // browser would not display those soft hyphens / other chars, so we don't want them in the text

        Document doc = Jsoup.parse("<p>" + escaped);
        Element p = doc.select("p").first();
        doc.outputSettings().charset("ascii"); // so that the outer html is easier to see with escaped invisibles
        Assertions.assertEquals("Thisisonelongword", p.text()); // text is normalized
        Assertions.assertEquals("<p>" + escaped + "</p>", p.outerHtml()); // html / whole text keeps &shy etc;
        Assertions.assertEquals(decoded, p.textNodes().get(0).getWholeText());

        Element matched = doc.select("p:contains(Thisisonelongword)").first(); // really just oneloneword, no invisibles
        Assertions.assertEquals("p", matched.nodeName());
        Assertions.assertTrue(matched.is(":containsOwn(Thisisonelongword)"));

    }

    @Test
    public void testRemoveBeforeIndex() {
        Document doc = Jsoup.parse(
            "<html><body><div><p>before1</p><p>before2</p><p>XXX</p><p>after1</p><p>after2</p></div></body></html>",
            "");
        Element body = doc.select("body").first();
        Elements elems = body.select("p:matchesOwn(XXX)");
        Element xElem = elems.first();
        Elements beforeX = ((Element) xElem.parent()).getElementsByIndexLessThan(xElem.elementSiblingIndex());

        for (Element p : beforeX) {
            p.remove();
        }

        Assertions.assertEquals("<body><div><p>XXX</p><p>after1</p><p>after2</p></div></body>", TextUtil.stripNewlines(body.outerHtml()));
    }

    @Test
    public void testRemoveAfterIndex() {
        Document doc2 = Jsoup.parse(
            "<html><body><div><p>before1</p><p>before2</p><p>XXX</p><p>after1</p><p>after2</p></div></body></html>",
            "");
        Element body = doc2.select("body").first();
        Elements elems = body.select("p:matchesOwn(XXX)");
        Element xElem = elems.first();
        Elements afterX = ((Element) xElem.parent()).getElementsByIndexGreaterThan(xElem.elementSiblingIndex());

        for (Element p : afterX) {
            p.remove();
        }

        Assertions.assertEquals("<body><div><p>before1</p><p>before2</p><p>XXX</p></div></body>", TextUtil.stripNewlines(body.outerHtml()));
    }

    @Test
    public void whiteSpaceClassElement() {
        Tag tag = Tag.valueOf("a");
        Attributes attribs = new Attributes();
        Element el = new Element(tag, "", attribs);

        attribs.put("class", "abc ");
        boolean hasClass = el.hasClass("ab");
        Assertions.assertFalse(hasClass);
    }

    @Test
    public void testNextElementSiblingAfterClone() {
        // via https://github.com/jhy/jsoup/issues/951
        String html = "<!DOCTYPE html><html lang=\"en\"><head></head><body><div>Initial element</div></body></html>";
        String expectedText = "New element";
        String cloneExpect = "New element in clone";

        Document original = Jsoup.parse(html);
        Document clone = (Document) original.clone();

        Element originalElement = original.body().child(0);
        originalElement.after("<div>" + expectedText + "</div>");
        Element originalNextElementSibling = originalElement.nextElementSibling();
        Element originalNextSibling = (Element) originalElement.nextSibling();
        Assertions.assertEquals(expectedText, originalNextElementSibling.text());
        Assertions.assertEquals(expectedText, originalNextSibling.text());

        Element cloneElement = clone.body().child(0);
        cloneElement.after("<div>" + cloneExpect + "</div>");
        Element cloneNextElementSibling = cloneElement.nextElementSibling();
        Element cloneNextSibling = (Element) cloneElement.nextSibling();
        Assertions.assertEquals(cloneExpect, cloneNextElementSibling.text());
        Assertions.assertEquals(cloneExpect, cloneNextSibling.text());
    }

    @Test
    public void testRemovingEmptyClassAttributeWhenLastClassRemoved() {
        // https://github.com/jhy/jsoup/issues/947
        Document doc = Jsoup.parse("<img class=\"one two\" />");
        Element img = doc.select("img").first();
        img.removeClass("one");
        img.removeClass("two");
        Assertions.assertFalse(doc.body().html().contains("class=\"\""));
    }

    @Test
    public void booleanAttributeOutput() {
        Document doc = Jsoup.parse("<img src=foo noshade='' nohref async=async autofocus=false>");
        Element img = doc.selectFirst("img");

        Assertions.assertEquals("<img src=\"foo\" noshade nohref async autofocus=\"false\">", img.outerHtml());
    }

    @Test
    public void textHasSpaceAfterBlockTags() {
        Document doc = Jsoup.parse("<div>One</div>Two");
        Assertions.assertEquals("One Two", doc.text());
    }

    @Test
    public void textHasSpaceBetweenDivAndCenterTags() {
        Document doc = Jsoup.parse("<div>One</div><div>Two</div><center>Three</center><center>Four</center>");
        Assertions.assertEquals("One Two Three Four", doc.text());
    }

    @Test
    public void testNextElementSiblings() {
        Document doc = Jsoup.parse("<ul id='ul'>" +
            "<li id='a'>a</li>" +
            "<li id='b'>b</li>" +
            "<li id='c'>c</li>" +
            "</ul> Not An Element but a node" +
            "<div id='div'>" +
            "<li id='d'>d</li>" +
            "</div>");

        Element element = doc.getElementById("a");
        Elements elementSiblings = element.nextElementSiblings();
        Assertions.assertNotNull(elementSiblings);
        Assertions.assertEquals(2, elementSiblings.size());
        Assertions.assertEquals("b", elementSiblings.get(0).id());
        Assertions.assertEquals("c", elementSiblings.get(1).id());

        Element element1 = doc.getElementById("b");
        List<Element> elementSiblings1 = element1.nextElementSiblings();
        Assertions.assertNotNull(elementSiblings1);
        Assertions.assertEquals(1, elementSiblings1.size());
        Assertions.assertEquals("c", elementSiblings1.get(0).id());

        Element element2 = doc.getElementById("c");
        List<Element> elementSiblings2 = element2.nextElementSiblings();
        Assertions.assertEquals(0, elementSiblings2.size());

        Element ul = doc.getElementById("ul");
        List<Element> elementSiblings3 = ul.nextElementSiblings();
        Assertions.assertNotNull(elementSiblings3);
        Assertions.assertEquals(1, elementSiblings3.size());
        Assertions.assertEquals("div", elementSiblings3.get(0).id());

        Element div = doc.getElementById("div");
        List<Element> elementSiblings4 = div.nextElementSiblings();
        Assertions.assertEquals(0, elementSiblings4.size());
    }

    @Test
    public void testPreviousElementSiblings() {
        Document doc = Jsoup.parse("<ul id='ul'>" +
            "<li id='a'>a</li>" +
            "<li id='b'>b</li>" +
            "<li id='c'>c</li>" +
            "</ul>" +
            "<div id='div'>" +
            "<li id='d'>d</li>" +
            "</div>");

        Element element = doc.getElementById("b");
        Elements elementSiblings = element.previousElementSiblings();
        Assertions.assertNotNull(elementSiblings);
        Assertions.assertEquals(1, elementSiblings.size());
        Assertions.assertEquals("a", elementSiblings.get(0).id());

        Element element1 = doc.getElementById("a");
        List<Element> elementSiblings1 = element1.previousElementSiblings();
        Assertions.assertEquals(0, elementSiblings1.size());

        Element element2 = doc.getElementById("c");
        List<Element> elementSiblings2 = element2.previousElementSiblings();
        Assertions.assertNotNull(elementSiblings2);
        Assertions.assertEquals(2, elementSiblings2.size());
        Assertions.assertEquals("b", elementSiblings2.get(0).id());
        Assertions.assertEquals("a", elementSiblings2.get(1).id());

        Element ul = doc.getElementById("ul");
        List<Element> elementSiblings3 = ul.previousElementSiblings();
        Assertions.assertEquals(0, elementSiblings3.size());
    }

    @Test
    public void testClearAttributes() {
        Element el = ((Element) new Element("a").attr("href", "http://example.com")).text("Hello");
        Assertions.assertEquals("<a href=\"http://example.com\">Hello</a>", el.outerHtml());
        Element el2 = (Element) el.clearAttributes(); // really just force testing the return type is Element
        Assertions.assertSame(el, el2);
        Assertions.assertEquals("<a>Hello</a>", el2.outerHtml());
    }

    @Test
    public void testRemoveAttr() {
        Element el = ((Element) new Element("a")
                .attr("href", "http://example.com")
                .attr("id", "1"))
                .text("Hello");
        Assertions.assertEquals("<a href=\"http://example.com\" id=\"1\">Hello</a>", el.outerHtml());
        Element el2 = (Element) el.removeAttr("href"); // really just force testing the return type is Element
        Assertions.assertSame(el, el2);
        Assertions.assertEquals("<a id=\"1\">Hello</a>", el2.outerHtml());
    }

    @Test
    public void testRoot() {
        Element el = new Element("a");
        el.append("<span>Hello</span>");
        Assertions.assertEquals("<a><span>Hello</span></a>", el.outerHtml());
        Element span = el.selectFirst("span");
        Assertions.assertNotNull(span);
        Element el2 = (Element) span.root();
        Assertions.assertSame(el, el2);

        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three");
        Element div = doc.selectFirst("div");
        Assertions.assertSame(doc, div.root());
        Assertions.assertSame(doc, div.ownerDocument());
    }

    @Test
    public void testTraverse() {
        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three");
        Element div = doc.selectFirst("div");
        final AtomicLong counter = new AtomicLong(0);

        Element div2 = (Element) div.traverse(new NodeVisitor() {

            @Override
            public void head(Node node, int depth) {
                counter.incrementAndGet();
            }

            @Override
            public void tail(Node node, int depth) {

            }
        });

        Assertions.assertEquals(7, counter.get());
        Assertions.assertEquals(div2, div);
    }

    @Test
    public void voidTestFilterCallReturnsElement() {
        // doesn't actually test the filter so much as the return type for Element. See node.nodeFilter for an acutal test
        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three");
        Element div = doc.selectFirst("div");
        Element div2 = (Element) div.filter(new NodeFilter() {
            @Override
            public FilterResult head(Node node, int depth) {
                return FilterResult.CONTINUE;
            }

            @Override
            public FilterResult tail(Node node, int depth) {
                return FilterResult.CONTINUE;
            }
        });

        Assertions.assertSame(div, div2);
    }

    @Test
    public void doesntDeleteZWJWhenNormalizingText() {
        String text = "\uD83D\uDC69\u200D\uD83D\uDCBB\uD83E\uDD26\uD83C\uDFFB\u200D\u2642\uFE0F";

        Document doc = Jsoup.parse("<p>" + text + "</p><div>One&zwj;Two</div>");
        Element p = doc.selectFirst("p");
        Element d = doc.selectFirst("div");

        Assertions.assertEquals(12, p.text().length());
        Assertions.assertEquals(text, p.text());
        Assertions.assertEquals(7, d.text().length());
        Assertions.assertEquals("One\u200DTwo", d.text());
        Element found = doc.selectFirst("div:contains(One\u200DTwo)");
        Assertions.assertTrue(found.hasSameValue(d));
    }

    @Test
    public void testReparentSeperateNodes() {
        String html = "<div><p>One<p>Two";
        Document doc = Jsoup.parse(html);
        Element new1 = new Element("p").text("Three");
        Element new2 = new Element("p").text("Four");

        doc.body().insertChildren(-1, new1, new2);
        Assertions.assertEquals("<div><p>One</p><p>Two</p></div><p>Three</p><p>Four</p>", TextUtil.stripNewlines(doc.body().html()));

        // note that these get moved from the above - as not copied
        doc.body().insertChildren(0, new1, new2);
        Assertions.assertEquals("<p>Three</p><p>Four</p><div><p>One</p><p>Two</p></div>", TextUtil.stripNewlines(doc.body().html()));

        doc.body().insertChildren(0, (Node) new2.clone(), (Node) new1.clone());
        Assertions.assertEquals("<p>Four</p><p>Three</p><p>Three</p><p>Four</p><div><p>One</p><p>Two</p></div>", TextUtil.stripNewlines(doc.body().html()));

        // shifted to end
        doc.body().appendChild(new1);
        Assertions.assertEquals("<p>Four</p><p>Three</p><p>Four</p><div><p>One</p><p>Two</p></div><p>Three</p>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void testNotActuallyAReparent() {
        // prep
        String html = "<div>";
        Document doc = Jsoup.parse(html);
        Element div = doc.selectFirst("div");
        Element new1 = new Element("p").text("One");
        Element new2 = new Element("p").text("Two");
        div.addChildren(new1, new2);

        Assertions.assertEquals("<div><p>One</p><p>Two</p></div>", TextUtil.stripNewlines(div.outerHtml()));

        // and the issue setup:
        Element new3 = new Element("p").text("Three");
        Element wrap = new Element("nav");
        wrap.addChildren(0, new1, new3);

        Assertions.assertEquals("<nav><p>One</p><p>Three</p></nav>", TextUtil.stripNewlines(wrap.outerHtml()));
        div.addChildren(wrap);
        // now should be that One moved into wrap, leaving Two in div.

        Assertions.assertEquals("<div><p>Two</p><nav><p>One</p><p>Three</p></nav></div>", TextUtil.stripNewlines(div.outerHtml()));
        Assertions.assertEquals("<div><p>Two</p><nav><p>One</p><p>Three</p></nav></div>", TextUtil.stripNewlines(div.outerHtml()));
    }

    @Test
    public void testChildSizeWithMixedContent() {
        Document doc = Jsoup.parse("<table><tbody>\n<tr>\n<td>15:00</td>\n<td>sport</td>\n</tr>\n</tbody></table>");
        Element row = doc.selectFirst("table tbody tr");
        Assertions.assertEquals(2, row.childrenSize());
        Assertions.assertEquals(5, row.childNodeSize());
    }

    @Test
    public void isBlock() {
        String html = "<div><p><span>Hello</span>";
        Document doc = Jsoup.parse(html);
        Assertions.assertTrue(doc.selectFirst("div").isBlock());
        Assertions.assertTrue(doc.selectFirst("p").isBlock());
        Assertions.assertFalse(doc.selectFirst("span").isBlock());
    }

    @Test
    public void testScriptTextHtmlSetAsData() {
        String src = "var foo = 5 < 2;\nvar bar = 1 && 2;";
        String html = "<script>" + src + "</script>";
        Document doc = Jsoup.parse(html);
        Element el = doc.selectFirst("script");
        Assertions.assertNotNull(el);

        validateScriptContents(src, el);

        src = "var foo = 4 < 2;\nvar bar > 1 && 2;";
        el.html(src);
        validateScriptContents(src, el);

        // special case for .text (in HTML; in XML will just be regular text)
        el.text(src);
        validateScriptContents(src, el);

        // XML, no special treatment, get escaped correctly
        Document xml = Parser.xmlParser().parseInput(html, "");
        Element xEl = xml.selectFirst("script");
        Assertions.assertNotNull(xEl);
        src = "var foo = 5 < 2;\nvar bar = 1 && 2;";
        String escaped = "var foo = 5 &lt; 2;\nvar bar = 1 &amp;&amp; 2;";
        validateXmlScriptContents(xEl);
        xEl.text(src);
        validateXmlScriptContents(xEl);
        xEl.html(src);
        validateXmlScriptContents(xEl);

        Assertions.assertEquals("<script>var foo = 4 < 2;\nvar bar > 1 && 2;</script>", el.outerHtml());
        Assertions.assertEquals("<script>" + escaped + "</script>", xEl.outerHtml()); // escaped in xml as no special treatment

    }

    @Test
    public void testShallowCloneToString() {
        // https://github.com/jhy/jsoup/issues/1410
        Document doc = Jsoup.parse("<p><i>Hello</i></p>");
        Element p = doc.selectFirst("p");
        Element i = doc.selectFirst("i");
        String pH = p.shallowClone().toString();
        String iH = i.shallowClone().toString();

        Assertions.assertEquals("<p></p>", pH); // shallow, so no I
        Assertions.assertEquals("<i></i>", iH);

        Assertions.assertEquals(p.outerHtml(), p.toString());
        Assertions.assertEquals(i.outerHtml(), i.toString());
    }

    @Test
    public void styleHtmlRoundTrips() {
        String styleContents = "foo < bar > qux {color:white;}";
        String html = "<head><style>" + styleContents + "</style></head>";
        Document doc = Jsoup.parse(html);

        Element head = doc.head();
        Element style = head.selectFirst("style");
        Assertions.assertNotNull(style);
        Assertions.assertEquals(styleContents, style.html());
        style.html(styleContents);
        Assertions.assertEquals(styleContents, style.html());
        Assertions.assertEquals("", style.text());
        style.text(styleContents); // pushes the HTML, not the Text
        Assertions.assertEquals("", style.text());
        Assertions.assertEquals(styleContents, style.html());
    }

    @Test
    public void moveChildren() {
        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div><div></div>");
        Elements divs = doc.select("div");
        Element a = divs.get(0);
        Element b = divs.get(1);

        b.insertChildren(-1, a.childNodes());

        Assertions.assertEquals("<div></div>\n<div>\n <p>One</p>\n <p>Two</p>\n <p>Three</p>\n</div>",
            doc.body().html());
    }

    @Test
    public void moveChildrenToOuter() {
        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div><div></div>");
        Elements divs = doc.select("div");
        Element a = divs.get(0);
        Element b = doc.body();

        b.insertChildren(-1, a.childNodes());

        Assertions.assertEquals("<div></div>\n<div></div>\n<p>One</p>\n<p>Two</p>\n<p>Three</p>",
            doc.body().html());
    }

    @Test
    public void appendChildren() {
        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div><div><p>Four</div>");
        Elements divs = doc.select("div");
        Element a = divs.get(0);
        Element b = divs.get(1);

        b.appendChildren(a.childNodes());

        Assertions.assertEquals("<div></div>\n<div>\n <p>Four</p>\n <p>One</p>\n <p>Two</p>\n <p>Three</p>\n</div>",
            doc.body().html());
    }

    @Test
    public void prependChildren() {
        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div><div><p>Four</div>");
        Elements divs = doc.select("div");
        Element a = divs.get(0);
        Element b = divs.get(1);

        b.prependChildren(a.childNodes());

        Assertions.assertEquals("<div></div>\n<div>\n <p>One</p>\n <p>Two</p>\n <p>Three</p>\n <p>Four</p>\n</div>",
            doc.body().html());
    }

    @Test
    public void loopMoveChildren() {
        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div><div><p>Four</div>");
        Elements divs = doc.select("div");
        Element a = divs.get(0);
        Element b = divs.get(1);

        Element outer = (Element) b.parent();
        Assertions.assertNotNull(outer);
        for (Node node : a.childNodes()) {
            outer.appendChild(node);
        }

        Assertions.assertEquals("<div></div>\n<div>\n <p>Four</p>\n</div>\n<p>One</p>\n<p>Two</p>\n<p>Three</p>",
            doc.body().html());
    }

    @Test
    public void accessorsDoNotVivifyAttributes() throws NoSuchFieldException, IllegalAccessException {
        // internally, we don't want to create empty Attribute objects unless actually used for something
        Document doc = Jsoup.parse("<div><p><a href=foo>One</a>");
        Element div = doc.selectFirst("div");
        Element p = doc.selectFirst("p");
        Element a = doc.selectFirst("a");

        // should not create attributes
        Assertions.assertEquals("", div.attr("href"));
        p.removeAttr("href");

        Elements hrefs = doc.select("[href]");
        Assertions.assertEquals(1, hrefs.size());

        Assertions.assertFalse(div.hasAttributes());
        Assertions.assertFalse(p.hasAttributes());
        Assertions.assertTrue(a.hasAttributes());
    }

    @Test
    public void childNodesAccessorDoesNotVivify() {
        Document doc = Jsoup.parse("<p></p>");
        Element p = doc.selectFirst("p");
        Assertions.assertFalse(p.hasChildNodes());

        Assertions.assertEquals(0, p.childNodeSize());
        Assertions.assertEquals(0, p.childrenSize());

        List<Node> childNodes = p.childNodes();
        Assertions.assertEquals(0, childNodes.size());

        Elements children = p.children();
        Assertions.assertEquals(0, children.size());

        Assertions.assertFalse(p.hasChildNodes());
    }

    @Test public void emptyChildrenElementsIsModifiable() {
        // using unmodifiable empty in childElementList as short circuit, but people may be modifying Elements.
        Element p = new Element("p");
        Elements els = p.children();
        Assertions.assertEquals(0, els.size());
        els.add(new Element("a"));
        Assertions.assertEquals(1, els.size());
    }
}
