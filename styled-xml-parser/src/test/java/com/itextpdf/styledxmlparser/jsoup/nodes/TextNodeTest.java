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
import com.itextpdf.styledxmlparser.jsoup.internal.StringUtil;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.List;

/**
 Test TextNodes
*/
@Tag("UnitTest")
public class TextNodeTest extends ExtendedITextTest {
    @Test public void testBlank() {
        TextNode one = new TextNode("");
        TextNode two = new TextNode("     ");
        TextNode three = new TextNode("  \n\n   ");
        TextNode four = new TextNode("Hello");
        TextNode five = new TextNode("  \nHello ");

        Assertions.assertTrue(one.isBlank());
        Assertions.assertTrue(two.isBlank());
        Assertions.assertTrue(three.isBlank());
        Assertions.assertFalse(four.isBlank());
        Assertions.assertFalse(five.isBlank());
    }

    @Test public void testTextBean() {
        Document doc = Jsoup.parse("<p>One <span>two &amp;</span> three &amp;</p>");
        Element p = doc.select("p").first();

        Element span = doc.select("span").first();
        Assertions.assertEquals("two &", span.text());
        TextNode spanText = (TextNode) span.childNode(0);
        Assertions.assertEquals("two &", spanText.text());

        TextNode tn = (TextNode) p.childNode(2);
        Assertions.assertEquals(" three &", tn.text());

        tn.text(" POW!");
        Assertions.assertEquals("One <span>two &amp;</span> POW!", TextUtil.stripNewlines(p.html()));

        tn.attr(tn.nodeName(), "kablam &");
        Assertions.assertEquals("kablam &", tn.text());
        Assertions.assertEquals("One <span>two &amp;</span>kablam &amp;", TextUtil.stripNewlines(p.html()));
    }

    @Test public void testSplitText() {
        Document doc = Jsoup.parse("<div>Hello there</div>");
        Element div = doc.select("div").first();
        TextNode tn = (TextNode) div.childNode(0);
        TextNode tail = tn.splitText(6);
        Assertions.assertEquals("Hello ", tn.getWholeText());
        Assertions.assertEquals("there", tail.getWholeText());
        tail.text("there!");
        Assertions.assertEquals("Hello there!", div.text());
        Assertions.assertSame(tn.parent(), tail.parent());
    }

    @Test public void testSplitAnEmbolden() {
        Document doc = Jsoup.parse("<div>Hello there</div>");
        Element div = doc.select("div").first();
        TextNode tn = (TextNode) div.childNode(0);
        TextNode tail = tn.splitText(6);
        tail.wrap("<b></b>");

        Assertions.assertEquals("Hello <b>there</b>", TextUtil.stripNewlines(div.html())); // not great that we get \n<b>there there... must correct
    }

    @Test public void testWithSupplementaryCharacter(){
        Document doc = Jsoup.parse(new String(Character.toChars(135361)));
        TextNode t = doc.body().textNodes().get(0);
        Assertions.assertEquals(new String(Character.toChars(135361)), t.outerHtml().trim());
    }

    @Test public void testLeadNodesHaveNoChildren() {
        Document doc = Jsoup.parse("<div>Hello there</div>");
        Element div = doc.select("div").first();
        TextNode tn = (TextNode) div.childNode(0);
        List<Node> nodes = tn.childNodes();
        Assertions.assertEquals(0, nodes.size());
    }

    @Test public void testSpaceNormalise() {
        // https://github.com/jhy/jsoup/issues/1309
        String whole = "Two  spaces";
        String norm = "Two spaces";
        TextNode tn = new TextNode(whole); // there are 2 spaces between the words
        Assertions.assertEquals(whole, tn.getWholeText());
        Assertions.assertEquals(norm, tn.text());
        Assertions.assertEquals(norm, tn.outerHtml());
        Assertions.assertEquals(norm, tn.toString());

        Element el = new Element("p");
        el.appendChild(tn); // this used to change the context
        //tn.setParentNode(el); // set any parent
        Assertions.assertEquals(whole, tn.getWholeText());
        Assertions.assertEquals(norm, tn.text());
        Assertions.assertEquals(norm, tn.outerHtml());
        Assertions.assertEquals(norm, tn.toString());

        Assertions.assertEquals("<p>" + norm + "</p>", el.outerHtml());
        Assertions.assertEquals(norm, el.html());
        Assertions.assertEquals(whole, el.wholeText());
    }

    @Test
    public void testClone() {
        // https://github.com/jhy/jsoup/issues/1176
        TextNode x = new TextNode("zzz");
        TextNode y = (TextNode) x.clone();

        Assertions.assertNotSame(x, y);
        Assertions.assertEquals(x.outerHtml(), y.outerHtml());

        y.text("yyy");
        Assertions.assertNotEquals(x.outerHtml(), y.outerHtml());
        Assertions.assertEquals("zzz", x.text());

        x.attributes(); // already cloned so no impact
        y.text("xxx");
        Assertions.assertEquals("zzz", x.text());
        Assertions.assertEquals("xxx", y.text());
    }

    @Test
    public void testCloneAfterAttributesHit() {
        // https://github.com/jhy/jsoup/issues/1176
        TextNode x = new TextNode("zzz");
        x.attributes(); // moves content from leafnode value to attributes, which were missed in clone
        TextNode y = (TextNode) x.clone();
        y.text("xxx");
        Assertions.assertEquals("zzz", x.text());
        Assertions.assertEquals("xxx", y.text());
    }

    @Test
    public void testHasTextWhenIterating() {
        // https://github.com/jhy/jsoup/issues/1170
        Document doc = Jsoup.parse("<div>One <p>Two <p>Three");
        boolean foundFirst = false;
        for (Element el : doc.getAllElements()) {
            for (Node node : el.childNodes()) {
                if (node instanceof TextNode) {
                    TextNode textNode = (TextNode) node;
                    Assertions.assertFalse(StringUtil.isBlank(textNode.text()));
                    if (!foundFirst) {
                        foundFirst = true;
                        Assertions.assertEquals("One ", textNode.text());
                        Assertions.assertEquals("One ", textNode.getWholeText());
                    }
                }
            }
        }
        Assertions.assertTrue(foundFirst);
    }
}
