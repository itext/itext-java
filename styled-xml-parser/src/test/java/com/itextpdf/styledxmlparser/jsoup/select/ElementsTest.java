/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.select;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.TextUtil;
import com.itextpdf.styledxmlparser.jsoup.nodes.Comment;
import com.itextpdf.styledxmlparser.jsoup.nodes.DataNode;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.FormElement;
import com.itextpdf.styledxmlparser.jsoup.nodes.Node;
import com.itextpdf.styledxmlparser.jsoup.nodes.TextNode;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.List;

/**
 Tests for ElementList.
*/
@Tag("UnitTest")
public class ElementsTest extends ExtendedITextTest {
    @Test public void filter() {
        String h = "<p>Excl</p><div class=headline><p>Hello</p><p>There</p></div><div class=headline><h1>Headline</h1></div>";
        Document doc = Jsoup.parse(h);
        Elements els = doc.select(".headline").select("p");
        Assertions.assertEquals(2, els.size());
        Assertions.assertEquals("Hello", els.get(0).text());
        Assertions.assertEquals("There", els.get(1).text());
    }

    @Test public void attributes() {
        String h = "<p title=foo><p title=bar><p class=foo><p class=bar>";
        Document doc = Jsoup.parse(h);
        Elements withTitle = doc.select("p[title]");
        Assertions.assertEquals(2, withTitle.size());
        Assertions.assertTrue(withTitle.hasAttr("title"));
        Assertions.assertFalse(withTitle.hasAttr("class"));
        Assertions.assertEquals("foo", withTitle.attr("title"));

        withTitle.removeAttr("title");
        Assertions.assertEquals(2, withTitle.size()); // existing Elements are not reevaluated
        Assertions.assertEquals(0, doc.select("p[title]").size());

        Elements ps = doc.select("p").attr("style", "classy");
        Assertions.assertEquals(4, ps.size());
        Assertions.assertEquals("classy", ps.last().attr("style"));
        Assertions.assertEquals("bar", ps.last().attr("class"));
    }

    @Test public void hasAttr() {
        Document doc = Jsoup.parse("<p title=foo><p title=bar><p class=foo><p class=bar>");
        Elements ps = doc.select("p");
        Assertions.assertTrue(ps.hasAttr("class"));
        Assertions.assertFalse(ps.hasAttr("style"));
    }

    @Test public void hasAbsAttr() {
        Document doc = Jsoup.parse("<a id=1 href='/foo'>One</a> <a id=2 href='https://jsoup.org'>Two</a>");
        Elements one = doc.select("#1");
        Elements two = doc.select("#2");
        Elements both = doc.select("a");
        Assertions.assertFalse(one.hasAttr("abs:href"));
        Assertions.assertTrue(two.hasAttr("abs:href"));
        Assertions.assertTrue(both.hasAttr("abs:href")); // hits on #2
    }

    @Test public void attr() {
        Document doc = Jsoup.parse("<p title=foo><p title=bar><p class=foo><p class=bar>");
        String classVal = doc.select("p").attr("class");
        Assertions.assertEquals("foo", classVal);
    }

    @Test public void absAttr() {
        Document doc = Jsoup.parse("<a id=1 href='/foo'>One</a> <a id=2 href='https://jsoup.org/'>Two</a>");
        Elements one = doc.select("#1");
        Elements two = doc.select("#2");
        Elements both = doc.select("a");

        Assertions.assertEquals("", one.attr("abs:href"));
        Assertions.assertEquals("https://jsoup.org/", two.attr("abs:href"));
        Assertions.assertEquals("https://jsoup.org/", both.attr("abs:href"));
    }

    @Test public void classes() {
        Document doc = Jsoup.parse("<div><p class='mellow yellow'></p><p class='red green'></p>");

        Elements els = doc.select("p");
        Assertions.assertTrue(els.hasClass("red"));
        Assertions.assertFalse(els.hasClass("blue"));
        els.addClass("blue");
        els.removeClass("yellow");
        els.toggleClass("mellow");

        Assertions.assertEquals("blue", els.get(0).className());
        Assertions.assertEquals("red green blue mellow", els.get(1).className());
    }

    @Test public void hasClassCaseInsensitive() {
        Elements els = Jsoup.parse("<p Class=One>One <p class=Two>Two <p CLASS=THREE>THREE").select("p");
        Element one = els.get(0);
        Element two = els.get(1);
        Element thr = els.get(2);

        Assertions.assertTrue(one.hasClass("One"));
        Assertions.assertTrue(one.hasClass("ONE"));

        Assertions.assertTrue(two.hasClass("TWO"));
        Assertions.assertTrue(two.hasClass("Two"));

        Assertions.assertTrue(thr.hasClass("ThreE"));
        Assertions.assertTrue(thr.hasClass("three"));
    }

    @Test public void text() {
        String h = "<div><p>Hello<p>there<p>world</div>";
        Document doc = Jsoup.parse(h);
        Assertions.assertEquals("Hello there world", doc.select("div > *").text());
    }

    @Test public void hasText() {
        Document doc = Jsoup.parse("<div><p>Hello</p></div><div><p></p></div>");
        Elements divs = doc.select("div");
        Assertions.assertTrue(divs.hasText());
        Assertions.assertFalse(doc.select("div + div").hasText());
    }

    @Test public void html() {
        Document doc = Jsoup.parse("<div><p>Hello</p></div><div><p>There</p></div>");
        Elements divs = doc.select("div");
        Assertions.assertEquals("<p>Hello</p>\n<p>There</p>", divs.html());
    }

    @Test public void outerHtml() {
        Document doc = Jsoup.parse("<div><p>Hello</p></div><div><p>There</p></div>");
        Elements divs = doc.select("div");
        Assertions.assertEquals("<div><p>Hello</p></div><div><p>There</p></div>", TextUtil.stripNewlines(divs.outerHtml()));
    }

    @Test public void setHtml() {
        Document doc = Jsoup.parse("<p>One</p><p>Two</p><p>Three</p>");
        Elements ps = doc.select("p");

        ps.prepend("<b>Bold</b>").append("<i>Ital</i>");
        Assertions.assertEquals("<p><b>Bold</b>Two<i>Ital</i></p>", TextUtil.stripNewlines(ps.get(1).outerHtml()));

        ps.html("<span>Gone</span>");
        Assertions.assertEquals("<p><span>Gone</span></p>", TextUtil.stripNewlines(ps.get(1).outerHtml()));
    }

    @Test public void val() {
        Document doc = Jsoup.parse("<input value='one' /><textarea>two</textarea>");
        Elements els = doc.select("input, textarea");
        Assertions.assertEquals(2, els.size());
        Assertions.assertEquals("one", els.val());
        Assertions.assertEquals("two", els.last().val());

        els.val("three");
        Assertions.assertEquals("three", els.first().val());
        Assertions.assertEquals("three", els.last().val());
        Assertions.assertEquals("<textarea>three</textarea>", els.last().outerHtml());
    }

    @Test public void before() {
        Document doc = Jsoup.parse("<p>This <a>is</a> <a>jsoup</a>.</p>");
        doc.select("a").before("<span>foo</span>");
        Assertions.assertEquals("<p>This <span>foo</span><a>is</a> <span>foo</span><a>jsoup</a>.</p>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test public void after() {
        Document doc = Jsoup.parse("<p>This <a>is</a> <a>jsoup</a>.</p>");
        doc.select("a").after("<span>foo</span>");
        Assertions.assertEquals("<p>This <a>is</a><span>foo</span> <a>jsoup</a><span>foo</span>.</p>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test public void wrap() {
        String h = "<p><b>This</b> is <b>jsoup</b></p>";
        Document doc = Jsoup.parse(h);
        doc.select("b").wrap("<i></i>");
        Assertions.assertEquals("<p><i><b>This</b></i> is <i><b>jsoup</b></i></p>", doc.body().html());
    }

    @Test public void wrapDiv() {
        String h = "<p><b>This</b> is <b>jsoup</b>.</p> <p>How do you like it?</p>";
        Document doc = Jsoup.parse(h);
        doc.select("p").wrap("<div></div>");
        Assertions.assertEquals("<div><p><b>This</b> is <b>jsoup</b>.</p></div> <div><p>How do you like it?</p></div>",
                TextUtil.stripNewlines(doc.body().html()));
    }

    @Test public void unwrap() {
        String h = "<div><font>One</font> <font><a href=\"/\">Two</a></font></div";
        Document doc = Jsoup.parse(h);
        doc.select("font").unwrap();
        Assertions.assertEquals("<div>One <a href=\"/\">Two</a></div>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test public void unwrapP() {
        String h = "<p><a>One</a> Two</p> Three <i>Four</i> <p>Fix <i>Six</i></p>";
        Document doc = Jsoup.parse(h);
        doc.select("p").unwrap();
        Assertions.assertEquals("<a>One</a> Two Three <i>Four</i> Fix <i>Six</i>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test public void unwrapKeepsSpace() {
        String h = "<p>One <span>two</span> <span>three</span> four</p>";
        Document doc = Jsoup.parse(h);
        doc.select("span").unwrap();
        Assertions.assertEquals("<p>One two three four</p>", doc.body().html());
    }

    @Test public void empty() {
        Document doc = Jsoup.parse("<div><p>Hello <b>there</b></p> <p>now!</p></div>");
        doc.outputSettings().prettyPrint(false);

        doc.select("p").empty();
        Assertions.assertEquals("<div><p></p> <p></p></div>", doc.body().html());
    }

    @Test public void remove() {
        Document doc = Jsoup.parse("<div><p>Hello <b>there</b></p> jsoup <p>now!</p></div>");
        doc.outputSettings().prettyPrint(false);

        doc.select("p").remove();
        Assertions.assertEquals("<div> jsoup </div>", doc.body().html());
    }

    @Test public void eq() {
        String h = "<p>Hello<p>there<p>world";
        Document doc = Jsoup.parse(h);
        Assertions.assertEquals("there", doc.select("p").eq(1).text());
        Assertions.assertEquals("there", doc.select("p").get(1).text());
    }

    @Test public void is() {
        String h = "<p>Hello<p title=foo>there<p>world";
        Document doc = Jsoup.parse(h);
        Elements ps = doc.select("p");
        Assertions.assertTrue(ps.is("[title=foo]"));
        Assertions.assertFalse(ps.is("[title=bar]"));
    }

    @Test public void parents() {
        Document doc = Jsoup.parse("<div><p>Hello</p></div><p>There</p>");
        Elements parents = doc.select("p").parents();

        Assertions.assertEquals(3, parents.size());
        Assertions.assertEquals("div", parents.get(0).tagName());
        Assertions.assertEquals("body", parents.get(1).tagName());
        Assertions.assertEquals("html", parents.get(2).tagName());
    }

    @Test public void not() {
        Document doc = Jsoup.parse("<div id=1><p>One</p></div> <div id=2><p><span>Two</span></p></div>");

        Elements div1 = doc.select("div").not(":has(p > span)");
        Assertions.assertEquals(1, div1.size());
        Assertions.assertEquals("1", div1.first().id());

        Elements div2 = doc.select("div").not("#1");
        Assertions.assertEquals(1, div2.size());
        Assertions.assertEquals("2", div2.first().id());
    }

    @Test public void tagNameSet() {
        Document doc = Jsoup.parse("<p>Hello <i>there</i> <i>now</i></p>");
        doc.select("i").tagName("em");

        Assertions.assertEquals("<p>Hello <em>there</em> <em>now</em></p>", doc.body().html());
    }

    @Test public void traverse() {
        Document doc = Jsoup.parse("<div><p>Hello</p></div><div>There</div>");
        final StringBuilder accum = new StringBuilder();
        doc.select("div").traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int depth) {
                accum.append("<").append(node.nodeName()).append(">");
            }

            @Override
            public void tail(Node node, int depth) {
                accum.append("</").append(node.nodeName()).append(">");
            }
        });
        Assertions.assertEquals("<div><p><#text></#text></p></div><div><#text></#text></div>", accum.toString());
    }

    @Test public void forms() {
        Document doc = Jsoup.parse("<form id=1><input name=q></form><div /><form id=2><input name=f></form>");
        Elements els = doc.select("form, div");
        Assertions.assertEquals(3, els.size());

        List<FormElement> forms = els.forms();
        Assertions.assertEquals(2, forms.size());
        Assertions.assertNotNull(forms.get(0));
        Assertions.assertNotNull(forms.get(1));
        Assertions.assertEquals("1", forms.get(0).id());
        Assertions.assertEquals("2", forms.get(1).id());
    }

    @Test public void comments() {
        Document doc = Jsoup.parse("<!-- comment1 --><p><!-- comment2 --><p class=two><!-- comment3 -->");
        List<Comment> comments = doc.select("p").comments();
        Assertions.assertEquals(2, comments.size());
        Assertions.assertEquals(" comment2 ", comments.get(0).getData());
        Assertions.assertEquals(" comment3 ", comments.get(1).getData());

        List<Comment> comments1 = doc.select("p.two").comments();
        Assertions.assertEquals(1, comments1.size());
        Assertions.assertEquals(" comment3 ", comments1.get(0).getData());
    }

    @Test public void textNodes() {
        Document doc = Jsoup.parse("One<p>Two<a>Three</a><p>Four</p>Five");
        List<TextNode> textNodes = doc.select("p").textNodes();
        Assertions.assertEquals(2, textNodes.size());
        Assertions.assertEquals("Two", textNodes.get(0).text());
        Assertions.assertEquals("Four", textNodes.get(1).text());
    }

    @Test public void dataNodes() {
        Document doc = Jsoup.parse("<p>One</p><script>Two</script><style>Three</style>");
        List<DataNode> dataNodes = doc.select("p, script, style").dataNodes();
        Assertions.assertEquals(2, dataNodes.size());
        Assertions.assertEquals("Two", dataNodes.get(0).getWholeData());
        Assertions.assertEquals("Three", dataNodes.get(1).getWholeData());

        doc = Jsoup.parse("<head><script type=application/json><crux></script><script src=foo>Blah</script>");
        Elements script = doc.select("script[type=application/json]");
        List<DataNode> scriptNode = script.dataNodes();
        Assertions.assertEquals(1, scriptNode.size());
        DataNode dataNode = scriptNode.get(0);
        Assertions.assertEquals("<crux>", dataNode.getWholeData());

        // check if they're live
        dataNode.setWholeData("<cromulent>");
        Assertions.assertEquals("<script type=\"application/json\"><cromulent></script>", script.outerHtml());
    }

    @Test public void nodesEmpty() {
        Document doc = Jsoup.parse("<p>");
        Assertions.assertEquals(0, doc.select("form").textNodes().size());
    }

    @Test public void classWithHyphen() {
        Document doc = Jsoup.parse("<p class='tab-nav'>Check</p>");
        Elements els = doc.getElementsByClass("tab-nav");
        Assertions.assertEquals(1, els.size());
        Assertions.assertEquals("Check", els.text());
    }

    @Test public void siblings() {
        Document doc = Jsoup.parse("<div><p>1<p>2<p>3<p>4<p>5<p>6</div><div><p>7<p>8<p>9<p>10<p>11<p>12</div>");

        Elements els = doc.select("p:eq(3)"); // gets p4 and p10
        Assertions.assertEquals(2, els.size());

        Elements next = els.next();
        Assertions.assertEquals(2, next.size());
        Assertions.assertEquals("5", next.first().text());
        Assertions.assertEquals("11", next.last().text());

        Assertions.assertEquals(0, els.next("p:contains(6)").size());
        final Elements nextF = els.next("p:contains(5)");
        Assertions.assertEquals(1, nextF.size());
        Assertions.assertEquals("5", nextF.first().text());

        Elements nextA = els.nextAll();
        Assertions.assertEquals(4, nextA.size());
        Assertions.assertEquals("5", nextA.first().text());
        Assertions.assertEquals("12", nextA.last().text());

        Elements nextAF = els.nextAll("p:contains(6)");
        Assertions.assertEquals(1, nextAF.size());
        Assertions.assertEquals("6", nextAF.first().text());

        Elements prev = els.prev();
        Assertions.assertEquals(2, prev.size());
        Assertions.assertEquals("3", prev.first().text());
        Assertions.assertEquals("9", prev.last().text());

        Assertions.assertEquals(0, els.prev("p:contains(1)").size());
        final Elements prevF = els.prev("p:contains(3)");
        Assertions.assertEquals(1, prevF.size());
        Assertions.assertEquals("3", prevF.first().text());

        Elements prevA = els.prevAll();
        Assertions.assertEquals(6, prevA.size());
        Assertions.assertEquals("3", prevA.first().text());
        Assertions.assertEquals("7", prevA.last().text());

        Elements prevAF = els.prevAll("p:contains(1)");
        Assertions.assertEquals(1, prevAF.size());
        Assertions.assertEquals("1", prevAF.first().text());
    }

    @Test public void eachText() {
        Document doc = Jsoup.parse("<div><p>1<p>2<p>3<p>4<p>5<p>6</div><div><p>7<p>8<p>9<p>10<p>11<p>12<p></p></div>");
        List<String> divText = doc.select("div").eachText();
        Assertions.assertEquals(2, divText.size());
        Assertions.assertEquals("1 2 3 4 5 6", divText.get(0));
        Assertions.assertEquals("7 8 9 10 11 12", divText.get(1));

        List<String> pText = doc.select("p").eachText();
        Elements ps = doc.select("p");
        Assertions.assertEquals(13, ps.size());
        Assertions.assertEquals(12, pText.size()); // not 13, as last doesn't have text
        Assertions.assertEquals("1", pText.get(0));
        Assertions.assertEquals("2", pText.get(1));
        Assertions.assertEquals("5", pText.get(4));
        Assertions.assertEquals("7", pText.get(6));
        Assertions.assertEquals("12", pText.get(11));
    }

    @Test public void eachAttr() {
        Document doc = Jsoup.parse(
            "<div><a href='/foo'>1</a><a href='http://example.com/bar'>2</a><a href=''>3</a><a>4</a>",
            "http://example.com");

        List<String> hrefAttrs = doc.select("a").eachAttr("href");
        Assertions.assertEquals(3, hrefAttrs.size());
        Assertions.assertEquals("/foo", hrefAttrs.get(0));
        Assertions.assertEquals("http://example.com/bar", hrefAttrs.get(1));
        Assertions.assertEquals("", hrefAttrs.get(2));
        Assertions.assertEquals(4, doc.select("a").size());

        List<String> absAttrs = doc.select("a").eachAttr("abs:href");
        Assertions.assertEquals(3, absAttrs.size());
        Assertions.assertEquals(3, absAttrs.size());
        Assertions.assertEquals("http://example.com/foo", absAttrs.get(0));
        Assertions.assertEquals("http://example.com/bar", absAttrs.get(1));
        Assertions.assertEquals("http://example.com", absAttrs.get(2));
    }
}
