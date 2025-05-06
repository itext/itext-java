/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attribute;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.List;

/**
 Test suite for attribute parser.
*/
@Tag("UnitTest")
public class AttributeParseTest extends ExtendedITextTest {

    @Test public void parsesRoughAttributeString() {
        String html = "<a id=\"123\" class=\"baz = 'bar'\" style = 'border: 2px'qux zim foo = 12 mux=18 />";
        // should be: <id=123>, <class=baz = 'bar'>, <qux=>, <zim=>, <foo=12>, <mux.=18>

        Element el = Jsoup.parse(html).getElementsByTag("a").get(0);
        Attributes attr = el.attributes();
        Assertions.assertEquals(7, attr.size());
        Assertions.assertEquals("123", attr.get("id"));
        Assertions.assertEquals("baz = 'bar'", attr.get("class"));
        Assertions.assertEquals("border: 2px", attr.get("style"));
        Assertions.assertEquals("", attr.get("qux"));
        Assertions.assertEquals("", attr.get("zim"));
        Assertions.assertEquals("12", attr.get("foo"));
        Assertions.assertEquals("18", attr.get("mux"));
    }

    @Test public void handlesNewLinesAndReturns() {
        String html = "<a\r\nfoo='bar\r\nqux'\r\nbar\r\n=\r\ntwo>One</a>";
        Element el = Jsoup.parse(html).select("a").first();
        Assertions.assertEquals(2, el.attributes().size());
        Assertions.assertEquals("bar\r\nqux", el.attr("foo"));
        Assertions.assertEquals("two", el.attr("bar"));
    }

    @Test public void parsesEmptyString() {
        String html = "<a />";
        Element el = Jsoup.parse(html).getElementsByTag("a").get(0);
        Attributes attr = el.attributes();
        Assertions.assertEquals(0, attr.size());
    }

    @Test public void canStartWithEq() {
        String html = "<a =empty />";
        Element el = Jsoup.parse(html).getElementsByTag("a").get(0);
        Attributes attr = el.attributes();
        Assertions.assertEquals(1, attr.size());
        Assertions.assertTrue(attr.hasKey("=empty"));
        Assertions.assertEquals("", attr.get("=empty"));
    }

    @Test public void strictAttributeUnescapes() {
        String html = "<a id=1 href='?foo=bar&mid&lt=true'>One</a> <a id=2 href='?foo=bar&lt;qux&lg=1'>Two</a>";
        Elements els = Jsoup.parse(html).select("a");
        Assertions.assertEquals("?foo=bar&mid&lt=true", els.first().attr("href"));
        Assertions.assertEquals("?foo=bar<qux&lg=1", els.last().attr("href"));
    }

    @Test public void moreAttributeUnescapes() {
        String html = "<a href='&wr_id=123&mid-size=true&ok=&wr'>Check</a>";
        Elements els = Jsoup.parse(html).select("a");
        Assertions.assertEquals("&wr_id=123&mid-size=true&ok=&wr", els.first().attr("href"));
    }

    @Test public void parsesBooleanAttributes() {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Element el = Jsoup.parse(html).select("a").first();

        Assertions.assertEquals("123", el.attr("normal"));
        Assertions.assertEquals("", el.attr("boolean"));
        Assertions.assertEquals("", el.attr("empty"));

        List<Attribute> attributes = el.attributes().asList();
        Assertions.assertEquals(3, attributes.size());

        Assertions.assertEquals(html, el.outerHtml()); // vets boolean syntax
    }

    @Test public void dropsSlashFromAttributeName() {
        String html = "<img /onerror='doMyJob'/>";
        Document doc = Jsoup.parse(html);
        Assertions.assertFalse(doc.select("img[onerror]").isEmpty());
        Assertions.assertEquals("<img onerror=\"doMyJob\">", doc.body().html());

        doc = Jsoup.parse(html, "", Parser.xmlParser());
        Assertions.assertEquals("<img onerror=\"doMyJob\" />", doc.html());
    }
}
