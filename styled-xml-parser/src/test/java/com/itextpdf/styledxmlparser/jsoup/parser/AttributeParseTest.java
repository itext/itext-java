/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attribute;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

/**
 Test suite for attribute parser.
*/
@Category(UnitTest.class)
public class AttributeParseTest extends ExtendedITextTest {

    @Test public void parsesRoughAttributeString() {
        String html = "<a id=\"123\" class=\"baz = 'bar'\" style = 'border: 2px'qux zim foo = 12 mux=18 />";
        // should be: <id=123>, <class=baz = 'bar'>, <qux=>, <zim=>, <foo=12>, <mux.=18>

        Element el = Jsoup.parse(html).getElementsByTag("a").get(0);
        Attributes attr = el.attributes();
        Assert.assertEquals(7, attr.size());
        Assert.assertEquals("123", attr.get("id"));
        Assert.assertEquals("baz = 'bar'", attr.get("class"));
        Assert.assertEquals("border: 2px", attr.get("style"));
        Assert.assertEquals("", attr.get("qux"));
        Assert.assertEquals("", attr.get("zim"));
        Assert.assertEquals("12", attr.get("foo"));
        Assert.assertEquals("18", attr.get("mux"));
    }

    @Test public void handlesNewLinesAndReturns() {
        String html = "<a\r\nfoo='bar\r\nqux'\r\nbar\r\n=\r\ntwo>One</a>";
        Element el = Jsoup.parse(html).select("a").first();
        Assert.assertEquals(2, el.attributes().size());
        Assert.assertEquals("bar\r\nqux", el.attr("foo"));
        Assert.assertEquals("two", el.attr("bar"));
    }

    @Test public void parsesEmptyString() {
        String html = "<a />";
        Element el = Jsoup.parse(html).getElementsByTag("a").get(0);
        Attributes attr = el.attributes();
        Assert.assertEquals(0, attr.size());
    }

    @Test public void canStartWithEq() {
        String html = "<a =empty />";
        Element el = Jsoup.parse(html).getElementsByTag("a").get(0);
        Attributes attr = el.attributes();
        Assert.assertEquals(1, attr.size());
        Assert.assertTrue(attr.hasKey("=empty"));
        Assert.assertEquals("", attr.get("=empty"));
    }

    @Test public void strictAttributeUnescapes() {
        String html = "<a id=1 href='?foo=bar&mid&lt=true'>One</a> <a id=2 href='?foo=bar&lt;qux&lg=1'>Two</a>";
        Elements els = Jsoup.parse(html).select("a");
        Assert.assertEquals("?foo=bar&mid&lt=true", els.first().attr("href"));
        Assert.assertEquals("?foo=bar<qux&lg=1", els.last().attr("href"));
    }

    @Test public void moreAttributeUnescapes() {
        String html = "<a href='&wr_id=123&mid-size=true&ok=&wr'>Check</a>";
        Elements els = Jsoup.parse(html).select("a");
        Assert.assertEquals("&wr_id=123&mid-size=true&ok=&wr", els.first().attr("href"));
    }

    @Test public void parsesBooleanAttributes() {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Element el = Jsoup.parse(html).select("a").first();

        Assert.assertEquals("123", el.attr("normal"));
        Assert.assertEquals("", el.attr("boolean"));
        Assert.assertEquals("", el.attr("empty"));

        List<Attribute> attributes = el.attributes().asList();
        Assert.assertEquals(3, attributes.size());

        Assert.assertEquals(html, el.outerHtml()); // vets boolean syntax
    }

    @Test public void dropsSlashFromAttributeName() {
        String html = "<img /onerror='doMyJob'/>";
        Document doc = Jsoup.parse(html);
        Assert.assertFalse(doc.select("img[onerror]").isEmpty());
        Assert.assertEquals("<img onerror=\"doMyJob\">", doc.body().html());

        doc = Jsoup.parse(html, "", Parser.xmlParser());
        Assert.assertEquals("<img onerror=\"doMyJob\" />", doc.html());
    }
}
