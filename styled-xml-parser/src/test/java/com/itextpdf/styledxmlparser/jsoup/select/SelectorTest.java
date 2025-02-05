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
package com.itextpdf.styledxmlparser.jsoup.select;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 * Tests that the selector selects correctly.
 */
@Tag("UnitTest")
public class SelectorTest extends ExtendedITextTest {
    @Test public void testByTag() {
        // should be case insensitive
        Elements els = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><DIV id=3>").select("DIV");
        Assertions.assertEquals(3, els.size());
        Assertions.assertEquals("1", els.get(0).id());
        Assertions.assertEquals("2", els.get(1).id());
        Assertions.assertEquals("3", els.get(2).id());

        Elements none = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><div id=3>").select("span");
        Assertions.assertEquals(0, none.size());
    }

    @Test public void testById() {
        Elements els = Jsoup.parse("<div><p id=foo>Hello</p><p id=foo>Foo two!</p></div>").select("#foo");
        Assertions.assertEquals(2, els.size());
        Assertions.assertEquals("Hello", els.get(0).text());
        Assertions.assertEquals("Foo two!", els.get(1).text());

        Elements none = Jsoup.parse("<div id=1></div>").select("#foo");
        Assertions.assertEquals(0, none.size());
    }

    @Test public void testByClass() {
        Elements els = Jsoup.parse("<p id=0 class='ONE two'><p id=1 class='one'><p id=2 class='two'>").select("P.One");
        Assertions.assertEquals(2, els.size());
        Assertions.assertEquals("0", els.get(0).id());
        Assertions.assertEquals("1", els.get(1).id());

        Elements none = Jsoup.parse("<div class='one'></div>").select(".foo");
        Assertions.assertEquals(0, none.size());

        Elements els2 = Jsoup.parse("<div class='One-Two'></div>").select(".one-two");
        Assertions.assertEquals(1, els2.size());
    }

    @Test public void testByClassCaseInsensitive() {
        String html = "<p Class=foo>One <p Class=Foo>Two <p class=FOO>Three <p class=farp>Four";
        Elements elsFromClass = Jsoup.parse(html).select("P.Foo");
        Elements elsFromAttr = Jsoup.parse(html).select("p[class=foo]");

        Assertions.assertEquals(elsFromAttr.size(), elsFromClass.size());
        Assertions.assertEquals(3, elsFromClass.size());
        Assertions.assertEquals("Two", elsFromClass.get(1).text());
    }

    @Test public void testNamespacedTag() {
        Document doc = Jsoup.parse("<div><abc:def id=1>Hello</abc:def></div> <abc:def class=bold id=2>There</abc:def>");
        Elements byTag = doc.select("abc|def");
        Assertions.assertEquals(2, byTag.size());
        Assertions.assertEquals("1", byTag.first().id());
        Assertions.assertEquals("2", byTag.last().id());

        Elements byAttr = doc.select(".bold");
        Assertions.assertEquals(1, byAttr.size());
        Assertions.assertEquals("2", byAttr.last().id());

        Elements byTagAttr = doc.select("abc|def.bold");
        Assertions.assertEquals(1, byTagAttr.size());
        Assertions.assertEquals("2", byTagAttr.last().id());

        Elements byContains = doc.select("abc|def:contains(e)");
        Assertions.assertEquals(2, byContains.size());
        Assertions.assertEquals("1", byContains.first().id());
        Assertions.assertEquals("2", byContains.last().id());
    }

    @Test public void testWildcardNamespacedTag() {
        Document doc = Jsoup.parse("<div><abc:def id=1>Hello</abc:def></div> <abc:def class=bold id=2>There</abc:def>");
        Elements byTag = doc.select("*|def");
        Assertions.assertEquals(2, byTag.size());
        Assertions.assertEquals("1", byTag.first().id());
        Assertions.assertEquals("2", byTag.last().id());

        Elements byAttr = doc.select(".bold");
        Assertions.assertEquals(1, byAttr.size());
        Assertions.assertEquals("2", byAttr.last().id());

        Elements byTagAttr = doc.select("*|def.bold");
        Assertions.assertEquals(1, byTagAttr.size());
        Assertions.assertEquals("2", byTagAttr.last().id());

        Elements byContains = doc.select("*|def:contains(e)");
        Assertions.assertEquals(2, byContains.size());
        Assertions.assertEquals("1", byContains.first().id());
        Assertions.assertEquals("2", byContains.last().id());
    }

    @Test public void testWildcardNamespacedXmlTag() {
        Document doc = Jsoup.parse(
            "<div><Abc:Def id=1>Hello</Abc:Def></div> <Abc:Def class=bold id=2>There</abc:def>",
            "", Parser.xmlParser()
        );

        Elements byTag = doc.select("*|Def");
        Assertions.assertEquals(2, byTag.size());
        Assertions.assertEquals("1", byTag.first().id());
        Assertions.assertEquals("2", byTag.last().id());

        Elements byAttr = doc.select(".bold");
        Assertions.assertEquals(1, byAttr.size());
        Assertions.assertEquals("2", byAttr.last().id());

        Elements byTagAttr = doc.select("*|Def.bold");
        Assertions.assertEquals(1, byTagAttr.size());
        Assertions.assertEquals("2", byTagAttr.last().id());

        Elements byContains = doc.select("*|Def:contains(e)");
        Assertions.assertEquals(2, byContains.size());
        Assertions.assertEquals("1", byContains.first().id());
        Assertions.assertEquals("2", byContains.last().id());
    }

    @Test public void testWildCardNamespacedCaseVariations() {
        Document doc = Jsoup.parse("<One:Two>One</One:Two><three:four>Two</three:four>", "", Parser.xmlParser());
        Elements els1 = doc.select("One|Two");
        Elements els2 = doc.select("one|two");
        Elements els3 = doc.select("Three|Four");
        Elements els4 = doc.select("three|Four");

        Assertions.assertEquals(els1, els2);
        Assertions.assertEquals(els3, els4);
        Assertions.assertEquals("One", els1.text());
        Assertions.assertEquals(1, els1.size());
        Assertions.assertEquals("Two", els3.text());
        Assertions.assertEquals(1, els2.size());
    }

    @Test public void testByAttributeRegex() {
        Document doc = Jsoup.parse("<p><img src=foo.png id=1><img src=bar.jpg id=2><img src=qux.JPEG id=3><img src=old.gif><img></p>");
        Elements imgs = doc.select("img[src~=(?i)\\.(png|jpe?g)]");
        Assertions.assertEquals(3, imgs.size());
        Assertions.assertEquals("1", imgs.get(0).id());
        Assertions.assertEquals("2", imgs.get(1).id());
        Assertions.assertEquals("3", imgs.get(2).id());
    }

    @Test public void testByAttributeRegexCharacterClass() {
        Document doc = Jsoup.parse("<p><img src=foo.png id=1><img src=bar.jpg id=2><img src=qux.JPEG id=3><img src=old.gif id=4></p>");
        Elements imgs = doc.select("img[src~=[o]]");
        Assertions.assertEquals(2, imgs.size());
        Assertions.assertEquals("1", imgs.get(0).id());
        Assertions.assertEquals("4", imgs.get(1).id());
    }

    @Test public void testByAttributeRegexCombined() {
        Document doc = Jsoup.parse("<div><table class=x><td>Hello</td></table></div>");
        Elements els = doc.select("div table[class~=x|y]");
        Assertions.assertEquals(1, els.size());
        Assertions.assertEquals("Hello", els.text());
    }

    @Test public void testCombinedWithContains() {
        Document doc = Jsoup.parse("<p id=1>One</p><p>Two +</p><p>Three +</p>");
        Elements els = doc.select("p#1 + :contains(+)");
        Assertions.assertEquals(1, els.size());
        Assertions.assertEquals("Two +", els.text());
        Assertions.assertEquals("p", els.first().tagName());
    }

    @Test public void testAllElements() {
        String h = "<div><p>Hello</p><p><b>there</b></p></div>";
        Document doc = Jsoup.parse(h);
        Elements allDoc = doc.select("*");
        Elements allUnderDiv = doc.select("div *");
        Assertions.assertEquals(8, allDoc.size());
        Assertions.assertEquals(3, allUnderDiv.size());
        Assertions.assertEquals("p", allUnderDiv.first().tagName());
    }

    @Test public void testAllWithClass() {
        String h = "<p class=first>One<p class=first>Two<p>Three";
        Document doc = Jsoup.parse(h);
        Elements ps = doc.select("*.first");
        Assertions.assertEquals(2, ps.size());
    }

    @Test public void testGroupOr() {
        String h = "<div title=foo /><div title=bar /><div /><p></p><img /><span title=qux>";
        Document doc = Jsoup.parse(h);
        Elements els = doc.select("p,div,[title]");

        Assertions.assertEquals(5, els.size());
        Assertions.assertEquals("div", els.get(0).tagName());
        Assertions.assertEquals("foo", els.get(0).attr("title"));
        Assertions.assertEquals("div", els.get(1).tagName());
        Assertions.assertEquals("bar", els.get(1).attr("title"));
        Assertions.assertEquals("div", els.get(2).tagName());
        Assertions.assertEquals(0, els.get(2).attr("title").length()); // missing attributes come back as empty string
        Assertions.assertFalse(els.get(2).hasAttr("title"));
        Assertions.assertEquals("p", els.get(3).tagName());
        Assertions.assertEquals("span", els.get(4).tagName());
    }

    @Test public void testGroupOrAttribute() {
        String h = "<div id=1 /><div id=2 /><div title=foo /><div title=bar />";
        Elements els = Jsoup.parse(h).select("[id],[title=foo]");

        Assertions.assertEquals(3, els.size());
        Assertions.assertEquals("1", els.get(0).id());
        Assertions.assertEquals("2", els.get(1).id());
        Assertions.assertEquals("foo", els.get(2).attr("title"));
    }

    @Test public void descendant() {
        String h = "<div class=head><p class=first>Hello</p><p>There</p></div><p>None</p>";
        Document doc = Jsoup.parse(h);
        Element root = doc.getElementsByClass("HEAD").first();

        Elements els = root.select(".head p");
        Assertions.assertEquals(2, els.size());
        Assertions.assertEquals("Hello", els.get(0).text());
        Assertions.assertEquals("There", els.get(1).text());

        Elements p = root.select("p.first");
        Assertions.assertEquals(1, p.size());
        Assertions.assertEquals("Hello", p.get(0).text());

        Elements empty = root.select("p .first"); // self, not descend, should not match
        Assertions.assertEquals(0, empty.size());

        Elements aboveRoot = root.select("body div.head");
        Assertions.assertEquals(0, aboveRoot.size());
    }

    @Test public void and() {
        String h = "<div id=1 class='foo bar' title=bar name=qux><p class=foo title=bar>Hello</p></div";
        Document doc = Jsoup.parse(h);

        Elements div = doc.select("div.foo");
        Assertions.assertEquals(1, div.size());
        Assertions.assertEquals("div", div.first().tagName());

        Elements p = doc.select("div .foo"); // space indicates like "div *.foo"
        Assertions.assertEquals(1, p.size());
        Assertions.assertEquals("p", p.first().tagName());

        Elements div2 = doc.select("div#1.foo.bar[title=bar][name=qux]"); // very specific!
        Assertions.assertEquals(1, div2.size());
        Assertions.assertEquals("div", div2.first().tagName());

        Elements p2 = doc.select("div *.foo"); // space indicates like "div *.foo"
        Assertions.assertEquals(1, p2.size());
        Assertions.assertEquals("p", p2.first().tagName());
    }

    @Test public void deeperDescendant() {
        String h = "<div class=head><p><span class=first>Hello</div><div class=head><p class=first><span>Another</span><p>Again</div>";
        Document doc = Jsoup.parse(h);
        Element root = doc.getElementsByClass("head").first();

        Elements els = root.select("div p .first");
        Assertions.assertEquals(1, els.size());
        Assertions.assertEquals("Hello", els.first().text());
        Assertions.assertEquals("span", els.first().tagName());

        Elements aboveRoot = root.select("body p .first");
        Assertions.assertEquals(0, aboveRoot.size());
    }

    @Test public void parentChildElement() {
        String h = "<div id=1><div id=2><div id = 3></div></div></div><div id=4></div>";
        Document doc = Jsoup.parse(h);

        Elements divs = doc.select("div > div");
        Assertions.assertEquals(2, divs.size());
        Assertions.assertEquals("2", divs.get(0).id()); // 2 is child of 1
        Assertions.assertEquals("3", divs.get(1).id()); // 3 is child of 2

        Elements div2 = doc.select("div#1 > div");
        Assertions.assertEquals(1, div2.size());
        Assertions.assertEquals("2", div2.get(0).id());
    }

    @Test public void parentWithClassChild() {
        String h = "<h1 class=foo><a href=1 /></h1><h1 class=foo><a href=2 class=bar /></h1><h1><a href=3 /></h1>";
        Document doc = Jsoup.parse(h);

        Elements allAs = doc.select("h1 > a");
        Assertions.assertEquals(3, allAs.size());
        Assertions.assertEquals("a", allAs.first().tagName());

        Elements fooAs = doc.select("h1.foo > a");
        Assertions.assertEquals(2, fooAs.size());
        Assertions.assertEquals("a", fooAs.first().tagName());

        Elements barAs = doc.select("h1.foo > a.bar");
        Assertions.assertEquals(1, barAs.size());
    }

    @Test public void parentChildStar() {
        String h = "<div id=1><p>Hello<p><b>there</b></p></div><div id=2><span>Hi</span></div>";
        Document doc = Jsoup.parse(h);
        Elements divChilds = doc.select("div > *");
        Assertions.assertEquals(3, divChilds.size());
        Assertions.assertEquals("p", divChilds.get(0).tagName());
        Assertions.assertEquals("p", divChilds.get(1).tagName());
        Assertions.assertEquals("span", divChilds.get(2).tagName());
    }

    @Test public void multiChildDescent() {
        String h = "<div id=foo><h1 class=bar><a href=http://example.com/>One</a></h1></div>";
        Document doc = Jsoup.parse(h);
        Elements els = doc.select("div#foo > h1.bar > a[href*=example]");
        Assertions.assertEquals(1, els.size());
        Assertions.assertEquals("a", els.first().tagName());
    }

    @Test public void caseInsensitive() {
        String h = "<dIv tItle=bAr><div>"; // mixed case so a simple toLowerCase() on value doesn't catch
        Document doc = Jsoup.parse(h);

        Assertions.assertEquals(2, doc.select("DiV").size());
        Assertions.assertEquals(1, doc.select("DiV[TiTLE]").size());
        Assertions.assertEquals(1, doc.select("DiV[TiTLE=BAR]").size());
        Assertions.assertEquals(0, doc.select("DiV[TiTLE=BARBARELLA]").size());
    }

    @Test public void adjacentSiblings() {
        String h = "<ol><li>One<li>Two<li>Three</ol>";
        Document doc = Jsoup.parse(h);
        Elements sibs = doc.select("li + li");
        Assertions.assertEquals(2, sibs.size());
        Assertions.assertEquals("Two", sibs.get(0).text());
        Assertions.assertEquals("Three", sibs.get(1).text());
    }

    @Test public void adjacentSiblingsWithId() {
        String h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>";
        Document doc = Jsoup.parse(h);
        Elements sibs = doc.select("li#1 + li#2");
        Assertions.assertEquals(1, sibs.size());
        Assertions.assertEquals("Two", sibs.get(0).text());
    }

    @Test public void notAdjacent() {
        String h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>";
        Document doc = Jsoup.parse(h);
        Elements sibs = doc.select("li#1 + li#3");
        Assertions.assertEquals(0, sibs.size());
    }

    @Test public void mixCombinator() {
        String h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>";
        Document doc = Jsoup.parse(h);
        Elements sibs = doc.select("body > div.foo li + li");

        Assertions.assertEquals(2, sibs.size());
        Assertions.assertEquals("Two", sibs.get(0).text());
        Assertions.assertEquals("Three", sibs.get(1).text());
    }

    @Test public void mixCombinatorGroup() {
        String h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>";
        Document doc = Jsoup.parse(h);
        Elements els = doc.select(".foo > ol, ol > li + li");

        Assertions.assertEquals(3, els.size());
        Assertions.assertEquals("ol", els.get(0).tagName());
        Assertions.assertEquals("Two", els.get(1).text());
        Assertions.assertEquals("Three", els.get(2).text());
    }

    @Test public void generalSiblings() {
        String h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>";
        Document doc = Jsoup.parse(h);
        Elements els = doc.select("#1 ~ #3");
        Assertions.assertEquals(1, els.size());
        Assertions.assertEquals("Three", els.first().text());
    }

    // for http://github.com/jhy/jsoup/issues#issue/10
    @Test public void testCharactersInIdAndClass() {
        // using CSS spec for identifiers (id and class): a-z0-9, -, _. NOT . (which is OK in html spec, but not css)
        String h = "<div><p id='a1-foo_bar'>One</p><p class='b2-qux_bif'>Two</p></div>";
        Document doc = Jsoup.parse(h);

        Element el1 = doc.getElementById("a1-foo_bar");
        Assertions.assertEquals("One", el1.text());
        Element el2 = doc.getElementsByClass("b2-qux_bif").first();
        Assertions.assertEquals("Two", el2.text());

        Element el3 = doc.select("#a1-foo_bar").first();
        Assertions.assertEquals("One", el3.text());
        Element el4 = doc.select(".b2-qux_bif").first();
        Assertions.assertEquals("Two", el4.text());
    }

    // for http://github.com/jhy/jsoup/issues#issue/13
    @Test public void testSupportsLeadingCombinator() {
        String h = "<div><p><span>One</span><span>Two</span></p></div>";
        Document doc = Jsoup.parse(h);

        Element p = doc.select("div > p").first();
        Elements spans = p.select("> span");
        Assertions.assertEquals(2, spans.size());
        Assertions.assertEquals("One", spans.first().text());

        // make sure doesn't get nested
        h = "<div id=1><div id=2><div id=3></div></div></div>";
        doc = Jsoup.parse(h);
        Element div = doc.select("div").select(" > div").first();
        Assertions.assertEquals("2", div.id());
    }

    @Test public void testPseudoLessThan() {
        Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
        Elements ps = doc.select("div p:lt(2)");
        Assertions.assertEquals(3, ps.size());
        Assertions.assertEquals("One", ps.get(0).text());
        Assertions.assertEquals("Two", ps.get(1).text());
        Assertions.assertEquals("Four", ps.get(2).text());
    }

    @Test public void testPseudoGreaterThan() {
        Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</p></div><div><p>Four</p>");
        Elements ps = doc.select("div p:gt(0)");
        Assertions.assertEquals(2, ps.size());
        Assertions.assertEquals("Two", ps.get(0).text());
        Assertions.assertEquals("Three", ps.get(1).text());
    }

    @Test public void testPseudoEquals() {
        Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
        Elements ps = doc.select("div p:eq(0)");
        Assertions.assertEquals(2, ps.size());
        Assertions.assertEquals("One", ps.get(0).text());
        Assertions.assertEquals("Four", ps.get(1).text());

        Elements ps2 = doc.select("div:eq(0) p:eq(0)");
        Assertions.assertEquals(1, ps2.size());
        Assertions.assertEquals("One", ps2.get(0).text());
        Assertions.assertEquals("p", ps2.get(0).tagName());
    }

    @Test public void testPseudoBetween() {
        Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
        Elements ps = doc.select("div p:gt(0):lt(2)");
        Assertions.assertEquals(1, ps.size());
        Assertions.assertEquals("Two", ps.get(0).text());
    }

    @Test public void testPseudoCombined() {
        Document doc = Jsoup.parse("<div class='foo'><p>One</p><p>Two</p></div><div><p>Three</p><p>Four</p></div>");
        Elements ps = doc.select("div.foo p:gt(0)");
        Assertions.assertEquals(1, ps.size());
        Assertions.assertEquals("Two", ps.get(0).text());
    }

    @Test public void testPseudoHas() {
        Document doc = Jsoup.parse("<div id=0><p><span>Hello</span></p></div> <div id=1><span class=foo>There</span></div> <div id=2><p>Not</p></div>");

        Elements divs1 = doc.select("div:has(span)");
        Assertions.assertEquals(2, divs1.size());
        Assertions.assertEquals("0", divs1.get(0).id());
        Assertions.assertEquals("1", divs1.get(1).id());

        Elements divs2 = doc.select("div:has([class])");
        Assertions.assertEquals(1, divs2.size());
        Assertions.assertEquals("1", divs2.get(0).id());

        Elements divs3 = doc.select("div:has(span, p)");
        Assertions.assertEquals(3, divs3.size());
        Assertions.assertEquals("0", divs3.get(0).id());
        Assertions.assertEquals("1", divs3.get(1).id());
        Assertions.assertEquals("2", divs3.get(2).id());

        Elements els1 = doc.body().select(":has(p)");
        Assertions.assertEquals(3, els1.size()); // body, div, dib
        Assertions.assertEquals("body", els1.first().tagName());
        Assertions.assertEquals("0", els1.get(1).id());
        Assertions.assertEquals("2", els1.get(2).id());

        Elements els2 = doc.body().select(":has(> span)");
        Assertions.assertEquals(2,els2.size()); // p, div
        Assertions.assertEquals("p",els2.first().tagName());
        Assertions.assertEquals("1", els2.get(1).id());
    }

    @Test public void testNestedHas() {
        Document doc = Jsoup.parse("<div><p><span>One</span></p></div> <div><p>Two</p></div>");
        Elements divs = doc.select("div:has(p:has(span))");
        Assertions.assertEquals(1, divs.size());
        Assertions.assertEquals("One", divs.first().text());

        // test matches in has
        divs = doc.select("div:has(p:matches((?i)two))");
        Assertions.assertEquals(1, divs.size());
        Assertions.assertEquals("div", divs.first().tagName());
        Assertions.assertEquals("Two", divs.first().text());

        // test contains in has
        divs = doc.select("div:has(p:contains(two))");
        Assertions.assertEquals(1, divs.size());
        Assertions.assertEquals("div", divs.first().tagName());
        Assertions.assertEquals("Two", divs.first().text());
    }

    @Test public void testPsuedoContainsWithParentheses() {
        Document doc = Jsoup.parse("<div><p id=1>This (is good)</p><p id=2>This is bad)</p>");

        Elements ps1 = doc.select("p:contains(this (is good))");
        Assertions.assertEquals(1, ps1.size());
        Assertions.assertEquals("1", ps1.first().id());

        Elements ps2 = doc.select("p:contains(this is bad\\))");
        Assertions.assertEquals(1, ps2.size());
        Assertions.assertEquals("2", ps2.first().id());
    }

    @Test public void testMatches() {
        Document doc = Jsoup.parse("<p id=1>The <i>Rain</i></p> <p id=2>There are 99 bottles.</p> <p id=3>Harder (this)</p> <p id=4>Rain</p>");

        Elements p1 = doc.select("p:matches(The rain)"); // no match, case sensitive
        Assertions.assertEquals(0, p1.size());

        Elements p2 = doc.select("p:matches((?i)the rain)"); // case insense. should include root, html, body
        Assertions.assertEquals(1, p2.size());
        Assertions.assertEquals("1", p2.first().id());

        Elements p4 = doc.select("p:matches((?i)^rain$)"); // bounding
        Assertions.assertEquals(1, p4.size());
        Assertions.assertEquals("4", p4.first().id());

        Elements p5 = doc.select("p:matches(\\d+)");
        Assertions.assertEquals(1, p5.size());
        Assertions.assertEquals("2", p5.first().id());

        Elements p6 = doc.select("p:matches(\\w+\\s+\\(\\w+\\))"); // test bracket matching
        Assertions.assertEquals(1, p6.size());
        Assertions.assertEquals("3", p6.first().id());

        Elements p7 = doc.select("p:matches((?i)the):has(i)"); // multi
        Assertions.assertEquals(1, p7.size());
        Assertions.assertEquals("1", p7.first().id());
    }

    @Test public void matchesOwn() {
        Document doc = Jsoup.parse("<p id=1>Hello <b>there</b> now</p>");

        Elements p1 = doc.select("p:matchesOwn((?i)hello now)");
        Assertions.assertEquals(1, p1.size());
        Assertions.assertEquals("1", p1.first().id());

        Assertions.assertEquals(0, doc.select("p:matchesOwn(there)").size());
    }

    @Test public void testRelaxedTags() {
        Document doc = Jsoup.parse("<abc_def id=1>Hello</abc_def> <abc-def id=2>There</abc-def>");

        Elements el1 = doc.select("abc_def");
        Assertions.assertEquals(1, el1.size());
        Assertions.assertEquals("1", el1.first().id());

        Elements el2 = doc.select("abc-def");
        Assertions.assertEquals(1, el2.size());
        Assertions.assertEquals("2", el2.first().id());
    }

    @Test public void notParas() {
        Document doc = Jsoup.parse("<p id=1>One</p> <p>Two</p> <p><span>Three</span></p>");

        Elements el1 = doc.select("p:not([id=1])");
        Assertions.assertEquals(2, el1.size());
        Assertions.assertEquals("Two", el1.first().text());
        Assertions.assertEquals("Three", el1.last().text());

        Elements el2 = doc.select("p:not(:has(span))");
        Assertions.assertEquals(2, el2.size());
        Assertions.assertEquals("One", el2.first().text());
        Assertions.assertEquals("Two", el2.last().text());
    }

    @Test public void notAll() {
        Document doc = Jsoup.parse("<p>Two</p> <p><span>Three</span></p>");

        Elements el1 = doc.body().select(":not(p)"); // should just be the span
        Assertions.assertEquals(2, el1.size());
        Assertions.assertEquals("body", el1.first().tagName());
        Assertions.assertEquals("span", el1.last().tagName());
    }

    @Test public void notClass() {
        Document doc = Jsoup.parse("<div class=left>One</div><div class=right id=1><p>Two</p></div>");

        Elements el1 = doc.select("div:not(.left)");
        Assertions.assertEquals(1, el1.size());
        Assertions.assertEquals("1", el1.first().id());
    }

    @Test public void handlesCommasInSelector() {
        Document doc = Jsoup.parse("<p name='1,2'>One</p><div>Two</div><ol><li>123</li><li>Text</li></ol>");

        Elements ps = doc.select("[name=1,2]");
        Assertions.assertEquals(1, ps.size());

        Elements containers = doc.select("div, li:matches([0-9,]+)");
        Assertions.assertEquals(2, containers.size());
        Assertions.assertEquals("div", containers.get(0).tagName());
        Assertions.assertEquals("li", containers.get(1).tagName());
        Assertions.assertEquals("123", containers.get(1).text());
    }

    @Test public void selectSupplementaryCharacter() {
        String s = new String(Character.toChars(135361));
        Document doc = Jsoup.parse("<div k" + s + "='" + s + "'>^" + s +"$/div>");
        Assertions.assertEquals("div", doc.select("div[k" + s + "]").first().tagName());
        Assertions.assertEquals("div", doc.select("div:containsOwn(" + s + ")").first().tagName());
    }

    @Test
    public void selectClassWithSpace() {
        final String html = "<div class=\"value\">class without space</div>\n"
                          + "<div class=\"value \">class with space</div>";

        Document doc = Jsoup.parse(html);

        Elements found = doc.select("div[class=value ]");
        Assertions.assertEquals(2, found.size());
        Assertions.assertEquals("class without space", found.get(0).text());
        Assertions.assertEquals("class with space", found.get(1).text());

        found = doc.select("div[class=\"value \"]");
        Assertions.assertEquals(2, found.size());
        Assertions.assertEquals("class without space", found.get(0).text());
        Assertions.assertEquals("class with space", found.get(1).text());

        found = doc.select("div[class=\"value\\ \"]");
        Assertions.assertEquals(0, found.size());
    }

    @Test public void selectSameElements() {
        final String html = "<div>one</div><div>one</div>";

        Document doc = Jsoup.parse(html);
        Elements els = doc.select("div");
        Assertions.assertEquals(2, els.size());

        Elements subSelect = els.select(":contains(one)");
        Assertions.assertEquals(2, subSelect.size());
    }

    @Test public void attributeWithBrackets() {
        String html = "<div data='End]'>One</div> <div data='[Another)]]'>Two</div>";
        Document doc = Jsoup.parse(html);
        Assertions.assertEquals("One", doc.select("div[data='End]']").first().text());
        Assertions.assertEquals("Two", doc.select("div[data='[Another)]]']").first().text());
        Assertions.assertEquals("One", doc.select("div[data=\"End]\"]").first().text());
        Assertions.assertEquals("Two", doc.select("div[data=\"[Another)]]\"]").first().text());
    }

    @Test public void containsWithQuote() {
        String html = "<p>One'One</p><p>One'Two</p>";
        Document doc = Jsoup.parse(html);
        Elements els = doc.select("p:contains(One\\'One)");
        Assertions.assertEquals(1, els.size());
        Assertions.assertEquals("One'One", els.text());
    }

    @Test public void selectFirst() {
        String html = "<p>One<p>Two<p>Three";
        Document doc = Jsoup.parse(html);
        Assertions.assertEquals("One", doc.selectFirst("p").text());
    }

    @Test public void selectFirstWithAnd() {
        String html = "<p>One<p class=foo>Two<p>Three";
        Document doc = Jsoup.parse(html);
        Assertions.assertEquals("Two", doc.selectFirst("p.foo").text());
    }

    @Test public void selectFirstWithOr() {
        String html = "<p>One<p>Two<p>Three<div>Four";
        Document doc = Jsoup.parse(html);
        Assertions.assertEquals("One", doc.selectFirst("p, div").text());
    }

    @Test public void matchText() {
        String html = "<p>One<br>Two</p>";
        Document doc = Jsoup.parse(html);
        String origHtml = doc.html();

        Elements one = doc.select("p:matchText:first-child");
        Assertions.assertEquals("One", one.first().text());

        Elements two = doc.select("p:matchText:last-child");
        Assertions.assertEquals("Two", two.first().text());

        Assertions.assertEquals(origHtml, doc.html());

        Assertions.assertEquals("Two", doc.select("p:matchText + br + *").text());
    }

    @Test public void nthLastChildWithNoParent() {
        Element el = new Element("p").text("Orphan");
        Elements els = el.select("p:nth-last-child(1)");
        Assertions.assertEquals(0, els.size());
    }

    @Test public void splitOnBr() {
        String html = "<div><p>One<br>Two<br>Three</p></div>";
        Document doc = Jsoup.parse(html);

        Elements els = doc.select("p:matchText");
        Assertions.assertEquals(3, els.size());
        Assertions.assertEquals("One", els.get(0).text());
        Assertions.assertEquals("Two", els.get(1).text());
        Assertions.assertEquals("Three", els.get(2).toString());
    }

    @Test public void matchTextAttributes() {
        Document doc = Jsoup.parse("<div><p class=one>One<br>Two<p class=two>Three<br>Four");
        Elements els = doc.select("p.two:matchText:last-child");

        Assertions.assertEquals(1, els.size());
        Assertions.assertEquals("Four", els.text());
    }

    @Test public void findBetweenSpan() {
        Document doc = Jsoup.parse("<p><span>One</span> Two <span>Three</span>");
        Elements els = doc.select("span ~ p:matchText"); // the Two becomes its own p, sibling of the span
        Assertions.assertEquals(1, els.size());
        Assertions.assertEquals("Two", els.text());
    }

    @Test public void startsWithBeginsWithSpace() {
        Document doc = Jsoup.parse("<small><a href=\" mailto:abc@def.net\">(abc@def.net)</a></small>");
        Elements els = doc.select("a[href^=' mailto']");

        Assertions.assertEquals(1, els.size());
    }

    @Test public void endsWithEndsWithSpaces() {
        Document doc = Jsoup.parse("<small><a href=\" mailto:abc@def.net \">(abc@def.net)</a></small>");
        Elements els = doc.select("a[href$='.net ']");

        Assertions.assertEquals(1, els.size());
    }

    // https://github.com/jhy/jsoup/issues/1257
    private final String mixedCase =
        "<html xmlns:n=\"urn:ns\"><n:mixedCase>text</n:mixedCase></html>";
    private final String lowercase =
        "<html xmlns:n=\"urn:ns\"><n:lowercase>text</n:lowercase></html>";

    @Test
    public void html_mixed_case_simple_name() {
        Document doc = Jsoup.parse(mixedCase, "", Parser.htmlParser());
        Assertions.assertEquals(0, doc.select("mixedCase").size());
    }

    @Test
    public void html_mixed_case_wildcard_name() {
        Document doc = Jsoup.parse(mixedCase, "", Parser.htmlParser());
        Assertions.assertEquals(1, doc.select("*|mixedCase").size());
    }

    @Test
    public void html_lowercase_simple_name() {
        Document doc = Jsoup.parse(lowercase, "", Parser.htmlParser());
        Assertions.assertEquals(0, doc.select("lowercase").size());
    }

    @Test
    public void html_lowercase_wildcard_name() {
        Document doc = Jsoup.parse(lowercase, "", Parser.htmlParser());
        Assertions.assertEquals(1, doc.select("*|lowercase").size());
    }

    @Test
    public void xml_mixed_case_simple_name() {
        Document doc = Jsoup.parse(mixedCase, "", Parser.xmlParser());
        Assertions.assertEquals(0, doc.select("mixedCase").size());
    }

    @Test
    public void xml_mixed_case_wildcard_name() {
        Document doc = Jsoup.parse(mixedCase, "", Parser.xmlParser());
        Assertions.assertEquals(1, doc.select("*|mixedCase").size());
    }

    @Test
    public void xml_lowercase_simple_name() {
        Document doc = Jsoup.parse(lowercase, "", Parser.xmlParser());
        Assertions.assertEquals(0, doc.select("lowercase").size());
    }

    @Test
    public void xml_lowercase_wildcard_name() {
        Document doc = Jsoup.parse(lowercase, "", Parser.xmlParser());
        Assertions.assertEquals(1, doc.select("*|lowercase").size());
    }

    @Test
    public void trimSelector() {
        // https://github.com/jhy/jsoup/issues/1274
        Document doc = Jsoup.parse("<p><span>Hello");
        Elements els = doc.select(" p span ");
        Assertions.assertEquals(1, els.size());
        Assertions.assertEquals("Hello", els.first().text());
    }

    @Test
    public void xmlWildcardNamespaceTest() {
        // https://github.com/jhy/jsoup/issues/1208
        Document doc = Jsoup.parse("<ns1:MyXmlTag>1111</ns1:MyXmlTag><ns2:MyXmlTag>2222</ns2:MyXmlTag>", "", Parser.xmlParser());
        Elements select = doc.select("*|MyXmlTag");
        Assertions.assertEquals(2, select.size());
        Assertions.assertEquals("1111", select.get(0).text());
        Assertions.assertEquals("2222", select.get(1).text());
    }

    @Test
    public void childElements() {
        // https://github.com/jhy/jsoup/issues/1292
        String html = "<body><span id=1>One <span id=2>Two</span></span></body>";
        Document doc = Jsoup.parse(html);

        Element outer = doc.selectFirst("span");
        Element span = outer.selectFirst("span");
        Element inner = outer.selectFirst("* span");

        Assertions.assertEquals("1", outer.id());
        Assertions.assertEquals("1", span.id());
        Assertions.assertEquals("2", inner.id());
        Assertions.assertEquals(outer, span);
        Assertions.assertNotEquals(outer, inner);
    }

    @Test
    public void selectFirstLevelChildrenOnly() {
        // testcase for https://github.com/jhy/jsoup/issues/984
        String html = "<div><span>One <span>Two</span></span> <span>Three <span>Four</span></span>";
        Document doc = Jsoup.parse(html);

        Element div = doc.selectFirst("div");
        Assertions.assertNotNull(div);

        // want to select One and Three only - the first level children
        Elements spans = div.select(":root > span");
        Assertions.assertEquals(2, spans.size());
        Assertions.assertEquals("One Two", spans.get(0).text());
        Assertions.assertEquals("Three Four", spans.get(1).text());
    }
}
