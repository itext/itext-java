/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.styledxmlparser.jsoup.select;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Tests that the selector selects correctly.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
@Category(UnitTest.class)
public class SelectorTest extends ExtendedITextTest {
    @Test public void testByTag() {
        // should be case insensitive
        Elements els = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><DIV id=3>").select("DIV");
        Assert.assertEquals(3, els.size());
        Assert.assertEquals("1", els.get(0).id());
        Assert.assertEquals("2", els.get(1).id());
        Assert.assertEquals("3", els.get(2).id());

        Elements none = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><div id=3>").select("span");
        Assert.assertEquals(0, none.size());
    }

    @Test public void testById() {
        Elements els = Jsoup.parse("<div><p id=foo>Hello</p><p id=foo>Foo two!</p></div>").select("#foo");
        Assert.assertEquals(2, els.size());
        Assert.assertEquals("Hello", els.get(0).text());
        Assert.assertEquals("Foo two!", els.get(1).text());

        Elements none = Jsoup.parse("<div id=1></div>").select("#foo");
        Assert.assertEquals(0, none.size());
    }

    @Test public void testByClass() {
        Elements els = Jsoup.parse("<p id=0 class='ONE two'><p id=1 class='one'><p id=2 class='two'>").select("P.One");
        Assert.assertEquals(2, els.size());
        Assert.assertEquals("0", els.get(0).id());
        Assert.assertEquals("1", els.get(1).id());

        Elements none = Jsoup.parse("<div class='one'></div>").select(".foo");
        Assert.assertEquals(0, none.size());

        Elements els2 = Jsoup.parse("<div class='One-Two'></div>").select(".one-two");
        Assert.assertEquals(1, els2.size());
    }

    @Test public void testByClassCaseInsensitive() {
        String html = "<p Class=foo>One <p Class=Foo>Two <p class=FOO>Three <p class=farp>Four";
        Elements elsFromClass = Jsoup.parse(html).select("P.Foo");
        Elements elsFromAttr = Jsoup.parse(html).select("p[class=foo]");

        Assert.assertEquals(elsFromAttr.size(), elsFromClass.size());
        Assert.assertEquals(3, elsFromClass.size());
        Assert.assertEquals("Two", elsFromClass.get(1).text());
    }

    @Test public void testNamespacedTag() {
        Document doc = Jsoup.parse("<div><abc:def id=1>Hello</abc:def></div> <abc:def class=bold id=2>There</abc:def>");
        Elements byTag = doc.select("abc|def");
        Assert.assertEquals(2, byTag.size());
        Assert.assertEquals("1", byTag.first().id());
        Assert.assertEquals("2", byTag.last().id());

        Elements byAttr = doc.select(".bold");
        Assert.assertEquals(1, byAttr.size());
        Assert.assertEquals("2", byAttr.last().id());

        Elements byTagAttr = doc.select("abc|def.bold");
        Assert.assertEquals(1, byTagAttr.size());
        Assert.assertEquals("2", byTagAttr.last().id());

        Elements byContains = doc.select("abc|def:contains(e)");
        Assert.assertEquals(2, byContains.size());
        Assert.assertEquals("1", byContains.first().id());
        Assert.assertEquals("2", byContains.last().id());
    }

    @Test public void testWildcardNamespacedTag() {
        Document doc = Jsoup.parse("<div><abc:def id=1>Hello</abc:def></div> <abc:def class=bold id=2>There</abc:def>");
        Elements byTag = doc.select("*|def");
        Assert.assertEquals(2, byTag.size());
        Assert.assertEquals("1", byTag.first().id());
        Assert.assertEquals("2", byTag.last().id());

        Elements byAttr = doc.select(".bold");
        Assert.assertEquals(1, byAttr.size());
        Assert.assertEquals("2", byAttr.last().id());

        Elements byTagAttr = doc.select("*|def.bold");
        Assert.assertEquals(1, byTagAttr.size());
        Assert.assertEquals("2", byTagAttr.last().id());

        Elements byContains = doc.select("*|def:contains(e)");
        Assert.assertEquals(2, byContains.size());
        Assert.assertEquals("1", byContains.first().id());
        Assert.assertEquals("2", byContains.last().id());
    }

    @Test public void testWildcardNamespacedXmlTag() {
        Document doc = Jsoup.parse(
            "<div><Abc:Def id=1>Hello</Abc:Def></div> <Abc:Def class=bold id=2>There</abc:def>",
            "", Parser.xmlParser()
        );

        Elements byTag = doc.select("*|Def");
        Assert.assertEquals(2, byTag.size());
        Assert.assertEquals("1", byTag.first().id());
        Assert.assertEquals("2", byTag.last().id());

        Elements byAttr = doc.select(".bold");
        Assert.assertEquals(1, byAttr.size());
        Assert.assertEquals("2", byAttr.last().id());

        Elements byTagAttr = doc.select("*|Def.bold");
        Assert.assertEquals(1, byTagAttr.size());
        Assert.assertEquals("2", byTagAttr.last().id());

        Elements byContains = doc.select("*|Def:contains(e)");
        Assert.assertEquals(2, byContains.size());
        Assert.assertEquals("1", byContains.first().id());
        Assert.assertEquals("2", byContains.last().id());
    }

    @Test public void testWildCardNamespacedCaseVariations() {
        Document doc = Jsoup.parse("<One:Two>One</One:Two><three:four>Two</three:four>", "", Parser.xmlParser());
        Elements els1 = doc.select("One|Two");
        Elements els2 = doc.select("one|two");
        Elements els3 = doc.select("Three|Four");
        Elements els4 = doc.select("three|Four");

        Assert.assertEquals(els1, els2);
        Assert.assertEquals(els3, els4);
        Assert.assertEquals("One", els1.text());
        Assert.assertEquals(1, els1.size());
        Assert.assertEquals("Two", els3.text());
        Assert.assertEquals(1, els2.size());
    }

    @Test public void testByAttributeRegex() {
        Document doc = Jsoup.parse("<p><img src=foo.png id=1><img src=bar.jpg id=2><img src=qux.JPEG id=3><img src=old.gif><img></p>");
        Elements imgs = doc.select("img[src~=(?i)\\.(png|jpe?g)]");
        Assert.assertEquals(3, imgs.size());
        Assert.assertEquals("1", imgs.get(0).id());
        Assert.assertEquals("2", imgs.get(1).id());
        Assert.assertEquals("3", imgs.get(2).id());
    }

    @Test public void testByAttributeRegexCharacterClass() {
        Document doc = Jsoup.parse("<p><img src=foo.png id=1><img src=bar.jpg id=2><img src=qux.JPEG id=3><img src=old.gif id=4></p>");
        Elements imgs = doc.select("img[src~=[o]]");
        Assert.assertEquals(2, imgs.size());
        Assert.assertEquals("1", imgs.get(0).id());
        Assert.assertEquals("4", imgs.get(1).id());
    }

    @Test public void testByAttributeRegexCombined() {
        Document doc = Jsoup.parse("<div><table class=x><td>Hello</td></table></div>");
        Elements els = doc.select("div table[class~=x|y]");
        Assert.assertEquals(1, els.size());
        Assert.assertEquals("Hello", els.text());
    }

    @Test public void testCombinedWithContains() {
        Document doc = Jsoup.parse("<p id=1>One</p><p>Two +</p><p>Three +</p>");
        Elements els = doc.select("p#1 + :contains(+)");
        Assert.assertEquals(1, els.size());
        Assert.assertEquals("Two +", els.text());
        Assert.assertEquals("p", els.first().tagName());
    }

    @Test public void testAllElements() {
        String h = "<div><p>Hello</p><p><b>there</b></p></div>";
        Document doc = Jsoup.parse(h);
        Elements allDoc = doc.select("*");
        Elements allUnderDiv = doc.select("div *");
        Assert.assertEquals(8, allDoc.size());
        Assert.assertEquals(3, allUnderDiv.size());
        Assert.assertEquals("p", allUnderDiv.first().tagName());
    }

    @Test public void testAllWithClass() {
        String h = "<p class=first>One<p class=first>Two<p>Three";
        Document doc = Jsoup.parse(h);
        Elements ps = doc.select("*.first");
        Assert.assertEquals(2, ps.size());
    }

    @Test public void testGroupOr() {
        String h = "<div title=foo /><div title=bar /><div /><p></p><img /><span title=qux>";
        Document doc = Jsoup.parse(h);
        Elements els = doc.select("p,div,[title]");

        Assert.assertEquals(5, els.size());
        Assert.assertEquals("div", els.get(0).tagName());
        Assert.assertEquals("foo", els.get(0).attr("title"));
        Assert.assertEquals("div", els.get(1).tagName());
        Assert.assertEquals("bar", els.get(1).attr("title"));
        Assert.assertEquals("div", els.get(2).tagName());
        Assert.assertEquals(0, els.get(2).attr("title").length()); // missing attributes come back as empty string
        Assert.assertFalse(els.get(2).hasAttr("title"));
        Assert.assertEquals("p", els.get(3).tagName());
        Assert.assertEquals("span", els.get(4).tagName());
    }

    @Test public void testGroupOrAttribute() {
        String h = "<div id=1 /><div id=2 /><div title=foo /><div title=bar />";
        Elements els = Jsoup.parse(h).select("[id],[title=foo]");

        Assert.assertEquals(3, els.size());
        Assert.assertEquals("1", els.get(0).id());
        Assert.assertEquals("2", els.get(1).id());
        Assert.assertEquals("foo", els.get(2).attr("title"));
    }

    @Test public void descendant() {
        String h = "<div class=head><p class=first>Hello</p><p>There</p></div><p>None</p>";
        Document doc = Jsoup.parse(h);
        Element root = doc.getElementsByClass("HEAD").first();

        Elements els = root.select(".head p");
        Assert.assertEquals(2, els.size());
        Assert.assertEquals("Hello", els.get(0).text());
        Assert.assertEquals("There", els.get(1).text());

        Elements p = root.select("p.first");
        Assert.assertEquals(1, p.size());
        Assert.assertEquals("Hello", p.get(0).text());

        Elements empty = root.select("p .first"); // self, not descend, should not match
        Assert.assertEquals(0, empty.size());

        Elements aboveRoot = root.select("body div.head");
        Assert.assertEquals(0, aboveRoot.size());
    }

    @Test public void and() {
        String h = "<div id=1 class='foo bar' title=bar name=qux><p class=foo title=bar>Hello</p></div";
        Document doc = Jsoup.parse(h);

        Elements div = doc.select("div.foo");
        Assert.assertEquals(1, div.size());
        Assert.assertEquals("div", div.first().tagName());

        Elements p = doc.select("div .foo"); // space indicates like "div *.foo"
        Assert.assertEquals(1, p.size());
        Assert.assertEquals("p", p.first().tagName());

        Elements div2 = doc.select("div#1.foo.bar[title=bar][name=qux]"); // very specific!
        Assert.assertEquals(1, div2.size());
        Assert.assertEquals("div", div2.first().tagName());

        Elements p2 = doc.select("div *.foo"); // space indicates like "div *.foo"
        Assert.assertEquals(1, p2.size());
        Assert.assertEquals("p", p2.first().tagName());
    }

    @Test public void deeperDescendant() {
        String h = "<div class=head><p><span class=first>Hello</div><div class=head><p class=first><span>Another</span><p>Again</div>";
        Document doc = Jsoup.parse(h);
        Element root = doc.getElementsByClass("head").first();

        Elements els = root.select("div p .first");
        Assert.assertEquals(1, els.size());
        Assert.assertEquals("Hello", els.first().text());
        Assert.assertEquals("span", els.first().tagName());

        Elements aboveRoot = root.select("body p .first");
        Assert.assertEquals(0, aboveRoot.size());
    }

    @Test public void parentChildElement() {
        String h = "<div id=1><div id=2><div id = 3></div></div></div><div id=4></div>";
        Document doc = Jsoup.parse(h);

        Elements divs = doc.select("div > div");
        Assert.assertEquals(2, divs.size());
        Assert.assertEquals("2", divs.get(0).id()); // 2 is child of 1
        Assert.assertEquals("3", divs.get(1).id()); // 3 is child of 2

        Elements div2 = doc.select("div#1 > div");
        Assert.assertEquals(1, div2.size());
        Assert.assertEquals("2", div2.get(0).id());
    }

    @Test public void parentWithClassChild() {
        String h = "<h1 class=foo><a href=1 /></h1><h1 class=foo><a href=2 class=bar /></h1><h1><a href=3 /></h1>";
        Document doc = Jsoup.parse(h);

        Elements allAs = doc.select("h1 > a");
        Assert.assertEquals(3, allAs.size());
        Assert.assertEquals("a", allAs.first().tagName());

        Elements fooAs = doc.select("h1.foo > a");
        Assert.assertEquals(2, fooAs.size());
        Assert.assertEquals("a", fooAs.first().tagName());

        Elements barAs = doc.select("h1.foo > a.bar");
        Assert.assertEquals(1, barAs.size());
    }

    @Test public void parentChildStar() {
        String h = "<div id=1><p>Hello<p><b>there</b></p></div><div id=2><span>Hi</span></div>";
        Document doc = Jsoup.parse(h);
        Elements divChilds = doc.select("div > *");
        Assert.assertEquals(3, divChilds.size());
        Assert.assertEquals("p", divChilds.get(0).tagName());
        Assert.assertEquals("p", divChilds.get(1).tagName());
        Assert.assertEquals("span", divChilds.get(2).tagName());
    }

    @Test public void multiChildDescent() {
        String h = "<div id=foo><h1 class=bar><a href=http://example.com/>One</a></h1></div>";
        Document doc = Jsoup.parse(h);
        Elements els = doc.select("div#foo > h1.bar > a[href*=example]");
        Assert.assertEquals(1, els.size());
        Assert.assertEquals("a", els.first().tagName());
    }

    @Test public void caseInsensitive() {
        String h = "<dIv tItle=bAr><div>"; // mixed case so a simple toLowerCase() on value doesn't catch
        Document doc = Jsoup.parse(h);

        Assert.assertEquals(2, doc.select("DiV").size());
        Assert.assertEquals(1, doc.select("DiV[TiTLE]").size());
        Assert.assertEquals(1, doc.select("DiV[TiTLE=BAR]").size());
        Assert.assertEquals(0, doc.select("DiV[TiTLE=BARBARELLA]").size());
    }

    @Test public void adjacentSiblings() {
        String h = "<ol><li>One<li>Two<li>Three</ol>";
        Document doc = Jsoup.parse(h);
        Elements sibs = doc.select("li + li");
        Assert.assertEquals(2, sibs.size());
        Assert.assertEquals("Two", sibs.get(0).text());
        Assert.assertEquals("Three", sibs.get(1).text());
    }

    @Test public void adjacentSiblingsWithId() {
        String h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>";
        Document doc = Jsoup.parse(h);
        Elements sibs = doc.select("li#1 + li#2");
        Assert.assertEquals(1, sibs.size());
        Assert.assertEquals("Two", sibs.get(0).text());
    }

    @Test public void notAdjacent() {
        String h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>";
        Document doc = Jsoup.parse(h);
        Elements sibs = doc.select("li#1 + li#3");
        Assert.assertEquals(0, sibs.size());
    }

    @Test public void mixCombinator() {
        String h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>";
        Document doc = Jsoup.parse(h);
        Elements sibs = doc.select("body > div.foo li + li");

        Assert.assertEquals(2, sibs.size());
        Assert.assertEquals("Two", sibs.get(0).text());
        Assert.assertEquals("Three", sibs.get(1).text());
    }

    @Test public void mixCombinatorGroup() {
        String h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>";
        Document doc = Jsoup.parse(h);
        Elements els = doc.select(".foo > ol, ol > li + li");

        Assert.assertEquals(3, els.size());
        Assert.assertEquals("ol", els.get(0).tagName());
        Assert.assertEquals("Two", els.get(1).text());
        Assert.assertEquals("Three", els.get(2).text());
    }

    @Test public void generalSiblings() {
        String h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>";
        Document doc = Jsoup.parse(h);
        Elements els = doc.select("#1 ~ #3");
        Assert.assertEquals(1, els.size());
        Assert.assertEquals("Three", els.first().text());
    }

    // for http://github.com/jhy/jsoup/issues#issue/10
    @Test public void testCharactersInIdAndClass() {
        // using CSS spec for identifiers (id and class): a-z0-9, -, _. NOT . (which is OK in html spec, but not css)
        String h = "<div><p id='a1-foo_bar'>One</p><p class='b2-qux_bif'>Two</p></div>";
        Document doc = Jsoup.parse(h);

        Element el1 = doc.getElementById("a1-foo_bar");
        Assert.assertEquals("One", el1.text());
        Element el2 = doc.getElementsByClass("b2-qux_bif").first();
        Assert.assertEquals("Two", el2.text());

        Element el3 = doc.select("#a1-foo_bar").first();
        Assert.assertEquals("One", el3.text());
        Element el4 = doc.select(".b2-qux_bif").first();
        Assert.assertEquals("Two", el4.text());
    }

    // for http://github.com/jhy/jsoup/issues#issue/13
    @Test public void testSupportsLeadingCombinator() {
        String h = "<div><p><span>One</span><span>Two</span></p></div>";
        Document doc = Jsoup.parse(h);

        Element p = doc.select("div > p").first();
        Elements spans = p.select("> span");
        Assert.assertEquals(2, spans.size());
        Assert.assertEquals("One", spans.first().text());

        // make sure doesn't get nested
        h = "<div id=1><div id=2><div id=3></div></div></div>";
        doc = Jsoup.parse(h);
        Element div = doc.select("div").select(" > div").first();
        Assert.assertEquals("2", div.id());
    }

    @Test public void testPseudoLessThan() {
        Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
        Elements ps = doc.select("div p:lt(2)");
        Assert.assertEquals(3, ps.size());
        Assert.assertEquals("One", ps.get(0).text());
        Assert.assertEquals("Two", ps.get(1).text());
        Assert.assertEquals("Four", ps.get(2).text());
    }

    @Test public void testPseudoGreaterThan() {
        Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</p></div><div><p>Four</p>");
        Elements ps = doc.select("div p:gt(0)");
        Assert.assertEquals(2, ps.size());
        Assert.assertEquals("Two", ps.get(0).text());
        Assert.assertEquals("Three", ps.get(1).text());
    }

    @Test public void testPseudoEquals() {
        Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
        Elements ps = doc.select("div p:eq(0)");
        Assert.assertEquals(2, ps.size());
        Assert.assertEquals("One", ps.get(0).text());
        Assert.assertEquals("Four", ps.get(1).text());

        Elements ps2 = doc.select("div:eq(0) p:eq(0)");
        Assert.assertEquals(1, ps2.size());
        Assert.assertEquals("One", ps2.get(0).text());
        Assert.assertEquals("p", ps2.get(0).tagName());
    }

    @Test public void testPseudoBetween() {
        Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
        Elements ps = doc.select("div p:gt(0):lt(2)");
        Assert.assertEquals(1, ps.size());
        Assert.assertEquals("Two", ps.get(0).text());
    }

    @Test public void testPseudoCombined() {
        Document doc = Jsoup.parse("<div class='foo'><p>One</p><p>Two</p></div><div><p>Three</p><p>Four</p></div>");
        Elements ps = doc.select("div.foo p:gt(0)");
        Assert.assertEquals(1, ps.size());
        Assert.assertEquals("Two", ps.get(0).text());
    }

    @Test public void testPseudoHas() {
        Document doc = Jsoup.parse("<div id=0><p><span>Hello</span></p></div> <div id=1><span class=foo>There</span></div> <div id=2><p>Not</p></div>");

        Elements divs1 = doc.select("div:has(span)");
        Assert.assertEquals(2, divs1.size());
        Assert.assertEquals("0", divs1.get(0).id());
        Assert.assertEquals("1", divs1.get(1).id());

        Elements divs2 = doc.select("div:has([class])");
        Assert.assertEquals(1, divs2.size());
        Assert.assertEquals("1", divs2.get(0).id());

        Elements divs3 = doc.select("div:has(span, p)");
        Assert.assertEquals(3, divs3.size());
        Assert.assertEquals("0", divs3.get(0).id());
        Assert.assertEquals("1", divs3.get(1).id());
        Assert.assertEquals("2", divs3.get(2).id());

        Elements els1 = doc.body().select(":has(p)");
        Assert.assertEquals(3, els1.size()); // body, div, dib
        Assert.assertEquals("body", els1.first().tagName());
        Assert.assertEquals("0", els1.get(1).id());
        Assert.assertEquals("2", els1.get(2).id());

        Elements els2 = doc.body().select(":has(> span)");
        Assert.assertEquals(2,els2.size()); // p, div
        Assert.assertEquals("p",els2.first().tagName());
        Assert.assertEquals("1", els2.get(1).id());
    }

    @Test public void testNestedHas() {
        Document doc = Jsoup.parse("<div><p><span>One</span></p></div> <div><p>Two</p></div>");
        Elements divs = doc.select("div:has(p:has(span))");
        Assert.assertEquals(1, divs.size());
        Assert.assertEquals("One", divs.first().text());

        // test matches in has
        divs = doc.select("div:has(p:matches((?i)two))");
        Assert.assertEquals(1, divs.size());
        Assert.assertEquals("div", divs.first().tagName());
        Assert.assertEquals("Two", divs.first().text());

        // test contains in has
        divs = doc.select("div:has(p:contains(two))");
        Assert.assertEquals(1, divs.size());
        Assert.assertEquals("div", divs.first().tagName());
        Assert.assertEquals("Two", divs.first().text());
    }

    @Test public void testPsuedoContainsWithParentheses() {
        Document doc = Jsoup.parse("<div><p id=1>This (is good)</p><p id=2>This is bad)</p>");

        Elements ps1 = doc.select("p:contains(this (is good))");
        Assert.assertEquals(1, ps1.size());
        Assert.assertEquals("1", ps1.first().id());

        Elements ps2 = doc.select("p:contains(this is bad\\))");
        Assert.assertEquals(1, ps2.size());
        Assert.assertEquals("2", ps2.first().id());
    }

    @Test public void testMatches() {
        Document doc = Jsoup.parse("<p id=1>The <i>Rain</i></p> <p id=2>There are 99 bottles.</p> <p id=3>Harder (this)</p> <p id=4>Rain</p>");

        Elements p1 = doc.select("p:matches(The rain)"); // no match, case sensitive
        Assert.assertEquals(0, p1.size());

        Elements p2 = doc.select("p:matches((?i)the rain)"); // case insense. should include root, html, body
        Assert.assertEquals(1, p2.size());
        Assert.assertEquals("1", p2.first().id());

        Elements p4 = doc.select("p:matches((?i)^rain$)"); // bounding
        Assert.assertEquals(1, p4.size());
        Assert.assertEquals("4", p4.first().id());

        Elements p5 = doc.select("p:matches(\\d+)");
        Assert.assertEquals(1, p5.size());
        Assert.assertEquals("2", p5.first().id());

        Elements p6 = doc.select("p:matches(\\w+\\s+\\(\\w+\\))"); // test bracket matching
        Assert.assertEquals(1, p6.size());
        Assert.assertEquals("3", p6.first().id());

        Elements p7 = doc.select("p:matches((?i)the):has(i)"); // multi
        Assert.assertEquals(1, p7.size());
        Assert.assertEquals("1", p7.first().id());
    }

    @Test public void matchesOwn() {
        Document doc = Jsoup.parse("<p id=1>Hello <b>there</b> now</p>");

        Elements p1 = doc.select("p:matchesOwn((?i)hello now)");
        Assert.assertEquals(1, p1.size());
        Assert.assertEquals("1", p1.first().id());

        Assert.assertEquals(0, doc.select("p:matchesOwn(there)").size());
    }

    @Test public void testRelaxedTags() {
        Document doc = Jsoup.parse("<abc_def id=1>Hello</abc_def> <abc-def id=2>There</abc-def>");

        Elements el1 = doc.select("abc_def");
        Assert.assertEquals(1, el1.size());
        Assert.assertEquals("1", el1.first().id());

        Elements el2 = doc.select("abc-def");
        Assert.assertEquals(1, el2.size());
        Assert.assertEquals("2", el2.first().id());
    }

    @Test public void notParas() {
        Document doc = Jsoup.parse("<p id=1>One</p> <p>Two</p> <p><span>Three</span></p>");

        Elements el1 = doc.select("p:not([id=1])");
        Assert.assertEquals(2, el1.size());
        Assert.assertEquals("Two", el1.first().text());
        Assert.assertEquals("Three", el1.last().text());

        Elements el2 = doc.select("p:not(:has(span))");
        Assert.assertEquals(2, el2.size());
        Assert.assertEquals("One", el2.first().text());
        Assert.assertEquals("Two", el2.last().text());
    }

    @Test public void notAll() {
        Document doc = Jsoup.parse("<p>Two</p> <p><span>Three</span></p>");

        Elements el1 = doc.body().select(":not(p)"); // should just be the span
        Assert.assertEquals(2, el1.size());
        Assert.assertEquals("body", el1.first().tagName());
        Assert.assertEquals("span", el1.last().tagName());
    }

    @Test public void notClass() {
        Document doc = Jsoup.parse("<div class=left>One</div><div class=right id=1><p>Two</p></div>");

        Elements el1 = doc.select("div:not(.left)");
        Assert.assertEquals(1, el1.size());
        Assert.assertEquals("1", el1.first().id());
    }

    @Test public void handlesCommasInSelector() {
        Document doc = Jsoup.parse("<p name='1,2'>One</p><div>Two</div><ol><li>123</li><li>Text</li></ol>");

        Elements ps = doc.select("[name=1,2]");
        Assert.assertEquals(1, ps.size());

        Elements containers = doc.select("div, li:matches([0-9,]+)");
        Assert.assertEquals(2, containers.size());
        Assert.assertEquals("div", containers.get(0).tagName());
        Assert.assertEquals("li", containers.get(1).tagName());
        Assert.assertEquals("123", containers.get(1).text());
    }

    @Test public void selectSupplementaryCharacter() {
        String s = new String(Character.toChars(135361));
        Document doc = Jsoup.parse("<div k" + s + "='" + s + "'>^" + s +"$/div>");
        Assert.assertEquals("div", doc.select("div[k" + s + "]").first().tagName());
        Assert.assertEquals("div", doc.select("div:containsOwn(" + s + ")").first().tagName());
    }

    @Test
    public void selectClassWithSpace() {
        final String html = "<div class=\"value\">class without space</div>\n"
                          + "<div class=\"value \">class with space</div>";

        Document doc = Jsoup.parse(html);

        Elements found = doc.select("div[class=value ]");
        Assert.assertEquals(2, found.size());
        Assert.assertEquals("class without space", found.get(0).text());
        Assert.assertEquals("class with space", found.get(1).text());

        found = doc.select("div[class=\"value \"]");
        Assert.assertEquals(2, found.size());
        Assert.assertEquals("class without space", found.get(0).text());
        Assert.assertEquals("class with space", found.get(1).text());

        found = doc.select("div[class=\"value\\ \"]");
        Assert.assertEquals(0, found.size());
    }

    @Test public void selectSameElements() {
        final String html = "<div>one</div><div>one</div>";

        Document doc = Jsoup.parse(html);
        Elements els = doc.select("div");
        Assert.assertEquals(2, els.size());

        Elements subSelect = els.select(":contains(one)");
        Assert.assertEquals(2, subSelect.size());
    }

    @Test public void attributeWithBrackets() {
        String html = "<div data='End]'>One</div> <div data='[Another)]]'>Two</div>";
        Document doc = Jsoup.parse(html);
        Assert.assertEquals("One", doc.select("div[data='End]']").first().text());
        Assert.assertEquals("Two", doc.select("div[data='[Another)]]']").first().text());
        Assert.assertEquals("One", doc.select("div[data=\"End]\"]").first().text());
        Assert.assertEquals("Two", doc.select("div[data=\"[Another)]]\"]").first().text());
    }

    @Test public void containsWithQuote() {
        String html = "<p>One'One</p><p>One'Two</p>";
        Document doc = Jsoup.parse(html);
        Elements els = doc.select("p:contains(One\\'One)");
        Assert.assertEquals(1, els.size());
        Assert.assertEquals("One'One", els.text());
    }

    @Test public void selectFirst() {
        String html = "<p>One<p>Two<p>Three";
        Document doc = Jsoup.parse(html);
        Assert.assertEquals("One", doc.selectFirst("p").text());
    }

    @Test public void selectFirstWithAnd() {
        String html = "<p>One<p class=foo>Two<p>Three";
        Document doc = Jsoup.parse(html);
        Assert.assertEquals("Two", doc.selectFirst("p.foo").text());
    }

    @Test public void selectFirstWithOr() {
        String html = "<p>One<p>Two<p>Three<div>Four";
        Document doc = Jsoup.parse(html);
        Assert.assertEquals("One", doc.selectFirst("p, div").text());
    }

    @Test public void matchText() {
        String html = "<p>One<br>Two</p>";
        Document doc = Jsoup.parse(html);
        String origHtml = doc.html();

        Elements one = doc.select("p:matchText:first-child");
        Assert.assertEquals("One", one.first().text());

        Elements two = doc.select("p:matchText:last-child");
        Assert.assertEquals("Two", two.first().text());

        Assert.assertEquals(origHtml, doc.html());

        Assert.assertEquals("Two", doc.select("p:matchText + br + *").text());
    }

    @Test public void nthLastChildWithNoParent() {
        Element el = new Element("p").text("Orphan");
        Elements els = el.select("p:nth-last-child(1)");
        Assert.assertEquals(0, els.size());
    }

    @Test public void splitOnBr() {
        String html = "<div><p>One<br>Two<br>Three</p></div>";
        Document doc = Jsoup.parse(html);

        Elements els = doc.select("p:matchText");
        Assert.assertEquals(3, els.size());
        Assert.assertEquals("One", els.get(0).text());
        Assert.assertEquals("Two", els.get(1).text());
        Assert.assertEquals("Three", els.get(2).toString());
    }

    @Test public void matchTextAttributes() {
        Document doc = Jsoup.parse("<div><p class=one>One<br>Two<p class=two>Three<br>Four");
        Elements els = doc.select("p.two:matchText:last-child");

        Assert.assertEquals(1, els.size());
        Assert.assertEquals("Four", els.text());
    }

    @Test public void findBetweenSpan() {
        Document doc = Jsoup.parse("<p><span>One</span> Two <span>Three</span>");
        Elements els = doc.select("span ~ p:matchText"); // the Two becomes its own p, sibling of the span
        Assert.assertEquals(1, els.size());
        Assert.assertEquals("Two", els.text());
    }

    @Test public void startsWithBeginsWithSpace() {
        Document doc = Jsoup.parse("<small><a href=\" mailto:abc@def.net\">(abc@def.net)</a></small>");
        Elements els = doc.select("a[href^=' mailto']");

        Assert.assertEquals(1, els.size());
    }

    @Test public void endsWithEndsWithSpaces() {
        Document doc = Jsoup.parse("<small><a href=\" mailto:abc@def.net \">(abc@def.net)</a></small>");
        Elements els = doc.select("a[href$='.net ']");

        Assert.assertEquals(1, els.size());
    }

    // https://github.com/jhy/jsoup/issues/1257
    private final String mixedCase =
        "<html xmlns:n=\"urn:ns\"><n:mixedCase>text</n:mixedCase></html>";
    private final String lowercase =
        "<html xmlns:n=\"urn:ns\"><n:lowercase>text</n:lowercase></html>";

    @Test
    public void html_mixed_case_simple_name() {
        Document doc = Jsoup.parse(mixedCase, "", Parser.htmlParser());
        Assert.assertEquals(0, doc.select("mixedCase").size());
    }

    @Test
    public void html_mixed_case_wildcard_name() {
        Document doc = Jsoup.parse(mixedCase, "", Parser.htmlParser());
        Assert.assertEquals(1, doc.select("*|mixedCase").size());
    }

    @Test
    public void html_lowercase_simple_name() {
        Document doc = Jsoup.parse(lowercase, "", Parser.htmlParser());
        Assert.assertEquals(0, doc.select("lowercase").size());
    }

    @Test
    public void html_lowercase_wildcard_name() {
        Document doc = Jsoup.parse(lowercase, "", Parser.htmlParser());
        Assert.assertEquals(1, doc.select("*|lowercase").size());
    }

    @Test
    public void xml_mixed_case_simple_name() {
        Document doc = Jsoup.parse(mixedCase, "", Parser.xmlParser());
        Assert.assertEquals(0, doc.select("mixedCase").size());
    }

    @Test
    public void xml_mixed_case_wildcard_name() {
        Document doc = Jsoup.parse(mixedCase, "", Parser.xmlParser());
        Assert.assertEquals(1, doc.select("*|mixedCase").size());
    }

    @Test
    public void xml_lowercase_simple_name() {
        Document doc = Jsoup.parse(lowercase, "", Parser.xmlParser());
        Assert.assertEquals(0, doc.select("lowercase").size());
    }

    @Test
    public void xml_lowercase_wildcard_name() {
        Document doc = Jsoup.parse(lowercase, "", Parser.xmlParser());
        Assert.assertEquals(1, doc.select("*|lowercase").size());
    }

    @Test
    public void trimSelector() {
        // https://github.com/jhy/jsoup/issues/1274
        Document doc = Jsoup.parse("<p><span>Hello");
        Elements els = doc.select(" p span ");
        Assert.assertEquals(1, els.size());
        Assert.assertEquals("Hello", els.first().text());
    }

    @Test
    public void xmlWildcardNamespaceTest() {
        // https://github.com/jhy/jsoup/issues/1208
        Document doc = Jsoup.parse("<ns1:MyXmlTag>1111</ns1:MyXmlTag><ns2:MyXmlTag>2222</ns2:MyXmlTag>", "", Parser.xmlParser());
        Elements select = doc.select("*|MyXmlTag");
        Assert.assertEquals(2, select.size());
        Assert.assertEquals("1111", select.get(0).text());
        Assert.assertEquals("2222", select.get(1).text());
    }

    @Test
    public void childElements() {
        // https://github.com/jhy/jsoup/issues/1292
        String html = "<body><span id=1>One <span id=2>Two</span></span></body>";
        Document doc = Jsoup.parse(html);

        Element outer = doc.selectFirst("span");
        Element span = outer.selectFirst("span");
        Element inner = outer.selectFirst("* span");

        Assert.assertEquals("1", outer.id());
        Assert.assertEquals("1", span.id());
        Assert.assertEquals("2", inner.id());
        Assert.assertEquals(outer, span);
        Assert.assertNotEquals(outer, inner);
    }

    @Test
    public void selectFirstLevelChildrenOnly() {
        // testcase for https://github.com/jhy/jsoup/issues/984
        String html = "<div><span>One <span>Two</span></span> <span>Three <span>Four</span></span>";
        Document doc = Jsoup.parse(html);

        Element div = doc.selectFirst("div");
        Assert.assertNotNull(div);

        // want to select One and Three only - the first level children
        Elements spans = div.select(":root > span");
        Assert.assertEquals(2, spans.size());
        Assert.assertEquals("One Two", spans.get(0).text());
        Assert.assertEquals("Three Four", spans.get(1).text());
    }
}
