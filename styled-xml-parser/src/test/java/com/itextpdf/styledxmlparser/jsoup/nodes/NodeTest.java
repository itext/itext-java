/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.TextUtil;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.jsoup.select.NodeVisitor;
import com.itextpdf.test.ExtendedITextTest;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 Tests Nodes
*/
@org.junit.jupiter.api.Tag("UnitTest")
public class NodeTest extends ExtendedITextTest {
    @Test
    public void handlesBaseUri() {
        Tag tag = Tag.valueOf("a");
        Attributes attribs = new Attributes();
        attribs.put("relHref", "/foo");
        attribs.put("absHref", "http://bar/qux");

        Element noBase = new Element(tag, "", attribs);
        assertEquals("", noBase.absUrl("relHref")); // with no base, should NOT fallback to href attrib, whatever it is
        assertEquals("http://bar/qux", noBase.absUrl("absHref")); // no base but valid attrib, return attrib

        Element withBase = new Element(tag, "http://foo/", attribs);
        assertEquals("http://foo/foo", withBase.absUrl("relHref")); // construct abs from base + rel
        assertEquals("http://bar/qux", withBase.absUrl("absHref")); // href is abs, so returns that
        assertEquals("", withBase.absUrl("noval"));

        Element dodgyBase = new Element(tag, "fff://no-such-protocol/", attribs);
        assertEquals("http://bar/qux", dodgyBase.absUrl("absHref")); // base fails, but href good, so get that
    }

    @Test
    public void handlesBaseUriBaseFails() {
        Tag tag = Tag.valueOf("a");
        Attributes attribs = new Attributes();
        attribs.put("relHref", "/foo");
        attribs.put("absHref", "http://bar/qux");

        Element dodgyBase = new Element(tag, "fff://no-such-protocol/", attribs);
        assertEquals("", dodgyBase.absUrl("relHref")); // base fails, only rel href, so return nothing
    }

    @Test
    public void setBaseUriIsRecursive() {
        Document doc = Jsoup.parse("<div><p></p></div>");
        String baseUri = "https://jsoup.org";
        doc.setBaseUri(baseUri);

        assertEquals(baseUri, doc.baseUri());
        assertEquals(baseUri, doc.select("div").first().baseUri());
        assertEquals(baseUri, doc.select("p").first().baseUri());
    }

    @Test
    public void handlesAbsPrefix() {
        Document doc = Jsoup.parse("<a href=/foo>Hello</a>", "https://jsoup.org/");
        Element a = doc.select("a").first();
        assertEquals("/foo", a.attr("href"));
        assertEquals("https://jsoup.org/foo", a.attr("abs:href"));
        Assertions.assertTrue(a.hasAttr("abs:href"));
    }

    @Test
    public void handlesAbsOnImage() {
        Document doc = Jsoup.parse("<p><img src=\"/rez/osi_logo.png\" /></p>", "https://jsoup.org/");
        Element img = doc.select("img").first();
        assertEquals("https://jsoup.org/rez/osi_logo.png", img.attr("abs:src"));
        assertEquals(img.absUrl("src"), img.attr("abs:src"));
    }

    @Test
    public void handlesAbsPrefixOnHasAttr() {
        // 1: no abs url; 2: has abs url
        Document doc = Jsoup.parse("<a id=1 href='/foo'>One</a> <a id=2 href='https://jsoup.org/'>Two</a>");
        Element one = doc.select("#1").first();
        Element two = doc.select("#2").first();

        Assertions.assertFalse(one.hasAttr("abs:href"));
        Assertions.assertTrue(one.hasAttr("href"));
        assertEquals("", one.absUrl("href"));

        Assertions.assertTrue(two.hasAttr("abs:href"));
        Assertions.assertTrue(two.hasAttr("href"));
        assertEquals("https://jsoup.org/", two.absUrl("href"));
    }

    @Test
    public void literalAbsPrefix() {
        // if there is a literal attribute "abs:xxx", don't try and make absolute.
        Document doc = Jsoup.parse("<a abs:href='odd'>One</a>");
        Element el = doc.select("a").first();
        Assertions.assertTrue(el.hasAttr("abs:href"));
        assertEquals("odd", el.attr("abs:href"));
    }

    @Test
    public void handleAbsOnFileUris() {
        Document doc = Jsoup.parse("<a href='password'>One/a><a href='/var/log/messages'>Two</a>", "file:/etc/");
        String expectedUrl = "file:/etc/password";
        String expectedUrlAnotherValidVersion = createAnotherValidUrlVersion(expectedUrl);

        Element one = doc.select("a").first();
        final String firstUrl = one.absUrl("href");
        // Both variants(namely with triple and single slashes) are valid.

        Assertions.assertTrue(expectedUrl.equals(firstUrl) || expectedUrlAnotherValidVersion.equals(firstUrl));

        expectedUrl = "file:/var/log/messages";
        expectedUrlAnotherValidVersion = createAnotherValidUrlVersion(expectedUrl);

        Element two = doc.select("a").get(1);
        final String secondUrl = two.absUrl("href");
        // Both variants(namely with triple and single slashes) are valid.
        Assertions.assertTrue(expectedUrl.equals(secondUrl) || expectedUrlAnotherValidVersion.equals(secondUrl));
    }

    @Test
    public void handleAbsOnLocalhostFileUris() {
        Document doc = Jsoup.parse("<a href='password'>One/a><a href='/var/log/messages'>Two</a>", "file://localhost/etc/");
        Element one = doc.select("a").first();
        assertEquals("file://localhost/etc/password", one.absUrl("href"));
    }

    @Test
    public void handlesAbsOnProtocolessAbsoluteUris() {
        Document doc1 = Jsoup.parse("<a href='//example.net/foo'>One</a>", "http://example.com/");
        Document doc2 = Jsoup.parse("<a href='//example.net/foo'>One</a>", "https://example.com/");

        Element one = doc1.select("a").first();
        Element two = doc2.select("a").first();

        assertEquals("http://example.net/foo", one.absUrl("href"));
        assertEquals("https://example.net/foo", two.absUrl("href"));

        Document doc3 = Jsoup.parse("<img src=//www.google.com/images/errors/logo_sm.gif alt=Google>", "https://google.com");
        assertEquals("https://www.google.com/images/errors/logo_sm.gif", doc3.select("img").attr("abs:src"));
    }

    /*
    Test for an issue with Java's abs URL handler.
     */
    @Test
    public void absHandlesRelativeQuery() {
        Document doc = Jsoup.parse("<a href='?foo'>One</a> <a href='bar.html?foo'>Two</a>", "https://jsoup.org/path/file?bar");

        Element a1 = doc.select("a").first();
        assertEquals("https://jsoup.org/path/file?foo", a1.absUrl("href"));

        Element a2 = doc.select("a").get(1);
        assertEquals("https://jsoup.org/path/bar.html?foo", a2.absUrl("href"));
    }

    @Test
    public void absHandlesDotFromIndex() {
        Document doc = Jsoup.parse("<a href='./one/two.html'>One</a>", "http://example.com");
        Element a1 = doc.select("a").first();
        assertEquals("http://example.com/one/two.html", a1.absUrl("href"));
    }

    @Test
    public void testRemove() {
        Document doc = Jsoup.parse("<p>One <span>two</span> three</p>");
        Element p = doc.select("p").first();
        p.childNode(0).remove();

        assertEquals("two three", p.text());
        assertEquals("<span>two</span> three", TextUtil.stripNewlines(p.html()));
    }

    @Test
    public void testReplace() {
        Document doc = Jsoup.parse("<p>One <span>two</span> three</p>");
        Element p = doc.select("p").first();
        Element insert = doc.createElement("em").text("foo");
        p.childNode(1).replaceWith(insert);

        assertEquals("One <em>foo</em> three", p.html());
    }

    @Test
    public void ownerDocument() {
        Document doc = Jsoup.parse("<p>Hello");
        Element p = doc.select("p").first();
        Assertions.assertSame(p.ownerDocument(), doc);
        Assertions.assertSame(doc.ownerDocument(), doc);
        Assertions.assertNull(doc.parent());
    }

    @Test
    public void root() {
        Document doc = Jsoup.parse("<div><p>Hello");
        Element p = doc.select("p").first();
        Node root = p.root();
        Assertions.assertSame(doc, root);
        Assertions.assertNull(root.parent());
        Assertions.assertSame(doc.root(), doc);
        Assertions.assertSame(doc.root(), doc.ownerDocument());

        Element standAlone = new Element(Tag.valueOf("p"), "");
        Assertions.assertNull(standAlone.parent());
        Assertions.assertSame(standAlone.root(), standAlone);
        Assertions.assertNull(standAlone.ownerDocument());
    }

    @Test
    public void before() {
        Document doc = Jsoup.parse("<p>One <b>two</b> three</p>");
        Element newNode = new Element(Tag.valueOf("em"), "");
        newNode.appendText("four");

        doc.select("b").first().before(newNode);
        assertEquals("<p>One <em>four</em><b>two</b> three</p>", doc.body().html());

        doc.select("b").first().before("<i>five</i>");
        assertEquals("<p>One <em>four</em><i>five</i><b>two</b> three</p>", doc.body().html());
    }

    @Test
    public void after() {
        Document doc = Jsoup.parse("<p>One <b>two</b> three</p>");
        Element newNode = new Element(Tag.valueOf("em"), "");
        newNode.appendText("four");

        doc.select("b").first().after(newNode);
        assertEquals("<p>One <b>two</b><em>four</em> three</p>", doc.body().html());

        doc.select("b").first().after("<i>five</i>");
        assertEquals("<p>One <b>two</b><i>five</i><em>four</em> three</p>", doc.body().html());
    }

    @Test
    public void unwrap() {
        Document doc = Jsoup.parse("<div>One <span>Two <b>Three</b></span> Four</div>");
        Element span = doc.select("span").first();
        Node twoText = span.childNode(0);
        Node node = span.unwrap();

        assertEquals("<div>One Two <b>Three</b> Four</div>", TextUtil.stripNewlines(doc.body().html()));
        Assertions.assertTrue(node instanceof TextNode);
        assertEquals("Two ", ((TextNode) node).text());
        assertEquals(node, twoText);
        assertEquals(node.parent(), doc.select("div").first());
    }

    @Test
    public void unwrapNoChildren() {
        Document doc = Jsoup.parse("<div>One <span></span> Two</div>");
        Element span = doc.select("span").first();
        Node node = span.unwrap();
        assertEquals("<div>One  Two</div>", TextUtil.stripNewlines(doc.body().html()));
        Assertions.assertNull(node);
    }

    @Test
    public void traverse() {
        Document doc = Jsoup.parse("<div><p>Hello</p></div><div>There</div>");
        final StringBuilder accum = new StringBuilder();
        doc.select("div").first().traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int depth) {
                accum.append("<").append(node.nodeName()).append(">");
            }

            @Override
            public void tail(Node node, int depth) {
                accum.append("</").append(node.nodeName()).append(">");
            }
        });
        assertEquals("<div><p><#text></#text></p></div>", accum.toString());
    }

    @Test
    public void orphanNodeReturnsNullForSiblingElements() {
        Node node = new Element(Tag.valueOf("p"), "");
        Element el = new Element(Tag.valueOf("p"), "");

        assertEquals(0, node.siblingIndex());
        assertEquals(0, node.siblingNodes().size());

        Assertions.assertNull(node.previousSibling());
        Assertions.assertNull(node.nextSibling());

        assertEquals(0, el.siblingElements().size());
        Assertions.assertNull(el.previousElementSibling());
        Assertions.assertNull(el.nextElementSibling());
    }

    @Test
    public void nodeIsNotASiblingOfItself() {
        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
        Element p2 = doc.select("p").get(1);

        assertEquals("Two", p2.text());
        List<Node> nodes = p2.siblingNodes();
        assertEquals(2, nodes.size());
        assertEquals("<p>One</p>", nodes.get(0).outerHtml());
        assertEquals("<p>Three</p>", nodes.get(1).outerHtml());
    }

    @Test
    public void childNodesCopy() {
        Document doc = Jsoup.parse("<div id=1>Text 1 <p>One</p> Text 2 <p>Two<p>Three</div><div id=2>");
        Element div1 = doc.select("#1").first();
        Element div2 = doc.select("#2").first();
        List<Node> divChildren = div1.childNodesCopy();
        assertEquals(5, divChildren.size());
        TextNode tn1 = (TextNode) div1.childNode(0);
        TextNode tn2 = (TextNode) divChildren.get(0);
        tn2.text("Text 1 updated");
        assertEquals("Text 1 ", tn1.text());
        div2.insertChildren(-1, divChildren);
        assertEquals("<div id=\"1\">Text 1 <p>One</p> Text 2 <p>Two</p><p>Three</p></div><div id=\"2\">Text 1 updated"
            +"<p>One</p> Text 2 <p>Two</p><p>Three</p></div>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void supportsClone() {
        Document doc = com.itextpdf.styledxmlparser.jsoup.Jsoup.parse("<div class=foo>Text</div>");
        Element el = doc.select("div").first();
        Assertions.assertTrue(el.hasClass("foo"));

        Element elClone = ((Document) doc.clone()).select("div").first();
        Assertions.assertTrue(elClone.hasClass("foo"));
        assertEquals("Text", elClone.text());

        el.removeClass("foo");
        el.text("None");
        Assertions.assertFalse(el.hasClass("foo"));
        Assertions.assertTrue(elClone.hasClass("foo"));
        assertEquals("None", el.text());
        assertEquals("Text", elClone.text());
    }

    @Test
    public void changingAttributeValueShouldReplaceExistingAttributeCaseInsensitive() {
        Document document = Jsoup.parse("<INPUT id=\"foo\" NAME=\"foo\" VALUE=\"\">");
        Element inputElement = document.select("#foo").first();

        inputElement.attr("value","bar");

        assertEquals(singletonAttributes(), getAttributesCaseInsensitive(inputElement));
    }

    private Attributes getAttributesCaseInsensitive(Element element) {
        Attributes matches = new Attributes();
        for (Attribute attribute : element.attributes()) {
            if (attribute.getKey().equalsIgnoreCase("value")) {
                matches.put(attribute);
            }
        }
        return matches;
    }

    private Attributes singletonAttributes() {
        Attributes attributes = new Attributes();
        attributes.put("value", "bar");
        return attributes;
    }

    private static String createAnotherValidUrlVersion(String url) {
        if (url.startsWith("file:///")) {
            return "file:/" + url.substring("file:///".length());
        } else {
            if (url.startsWith("file:/")) {
                return "file:///" + url.substring("file:/".length());
            } else {
                return url;
            }
        }
    }
}
