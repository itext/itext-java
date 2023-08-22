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
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.TextUtil;
import com.itextpdf.styledxmlparser.jsoup.internal.StringUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

/**
 Test TextNodes
*/
@Category(UnitTest.class)
public class TextNodeTest extends ExtendedITextTest {
    @Test public void testBlank() {
        TextNode one = new TextNode("");
        TextNode two = new TextNode("     ");
        TextNode three = new TextNode("  \n\n   ");
        TextNode four = new TextNode("Hello");
        TextNode five = new TextNode("  \nHello ");

        Assert.assertTrue(one.isBlank());
        Assert.assertTrue(two.isBlank());
        Assert.assertTrue(three.isBlank());
        Assert.assertFalse(four.isBlank());
        Assert.assertFalse(five.isBlank());
    }

    @Test public void testTextBean() {
        Document doc = Jsoup.parse("<p>One <span>two &amp;</span> three &amp;</p>");
        Element p = doc.select("p").first();

        Element span = doc.select("span").first();
        Assert.assertEquals("two &", span.text());
        TextNode spanText = (TextNode) span.childNode(0);
        Assert.assertEquals("two &", spanText.text());

        TextNode tn = (TextNode) p.childNode(2);
        Assert.assertEquals(" three &", tn.text());

        tn.text(" POW!");
        Assert.assertEquals("One <span>two &amp;</span> POW!", TextUtil.stripNewlines(p.html()));

        tn.attr(tn.nodeName(), "kablam &");
        Assert.assertEquals("kablam &", tn.text());
        Assert.assertEquals("One <span>two &amp;</span>kablam &amp;", TextUtil.stripNewlines(p.html()));
    }

    @Test public void testSplitText() {
        Document doc = Jsoup.parse("<div>Hello there</div>");
        Element div = doc.select("div").first();
        TextNode tn = (TextNode) div.childNode(0);
        TextNode tail = tn.splitText(6);
        Assert.assertEquals("Hello ", tn.getWholeText());
        Assert.assertEquals("there", tail.getWholeText());
        tail.text("there!");
        Assert.assertEquals("Hello there!", div.text());
        Assert.assertSame(tn.parent(), tail.parent());
    }

    @Test public void testSplitAnEmbolden() {
        Document doc = Jsoup.parse("<div>Hello there</div>");
        Element div = doc.select("div").first();
        TextNode tn = (TextNode) div.childNode(0);
        TextNode tail = tn.splitText(6);
        tail.wrap("<b></b>");

        Assert.assertEquals("Hello <b>there</b>", TextUtil.stripNewlines(div.html())); // not great that we get \n<b>there there... must correct
    }

    @Test public void testWithSupplementaryCharacter(){
        Document doc = Jsoup.parse(new String(Character.toChars(135361)));
        TextNode t = doc.body().textNodes().get(0);
        Assert.assertEquals(new String(Character.toChars(135361)), t.outerHtml().trim());
    }

    @Test public void testLeadNodesHaveNoChildren() {
        Document doc = Jsoup.parse("<div>Hello there</div>");
        Element div = doc.select("div").first();
        TextNode tn = (TextNode) div.childNode(0);
        List<Node> nodes = tn.childNodes();
        Assert.assertEquals(0, nodes.size());
    }

    @Test public void testSpaceNormalise() {
        // https://github.com/jhy/jsoup/issues/1309
        String whole = "Two  spaces";
        String norm = "Two spaces";
        TextNode tn = new TextNode(whole); // there are 2 spaces between the words
        Assert.assertEquals(whole, tn.getWholeText());
        Assert.assertEquals(norm, tn.text());
        Assert.assertEquals(norm, tn.outerHtml());
        Assert.assertEquals(norm, tn.toString());

        Element el = new Element("p");
        el.appendChild(tn); // this used to change the context
        //tn.setParentNode(el); // set any parent
        Assert.assertEquals(whole, tn.getWholeText());
        Assert.assertEquals(norm, tn.text());
        Assert.assertEquals(norm, tn.outerHtml());
        Assert.assertEquals(norm, tn.toString());

        Assert.assertEquals("<p>" + norm + "</p>", el.outerHtml());
        Assert.assertEquals(norm, el.html());
        Assert.assertEquals(whole, el.wholeText());
    }

    @Test
    public void testClone() {
        // https://github.com/jhy/jsoup/issues/1176
        TextNode x = new TextNode("zzz");
        TextNode y = (TextNode) x.clone();

        Assert.assertNotSame(x, y);
        Assert.assertEquals(x.outerHtml(), y.outerHtml());

        y.text("yyy");
        Assert.assertNotEquals(x.outerHtml(), y.outerHtml());
        Assert.assertEquals("zzz", x.text());

        x.attributes(); // already cloned so no impact
        y.text("xxx");
        Assert.assertEquals("zzz", x.text());
        Assert.assertEquals("xxx", y.text());
    }

    @Test
    public void testCloneAfterAttributesHit() {
        // https://github.com/jhy/jsoup/issues/1176
        TextNode x = new TextNode("zzz");
        x.attributes(); // moves content from leafnode value to attributes, which were missed in clone
        TextNode y = (TextNode) x.clone();
        y.text("xxx");
        Assert.assertEquals("zzz", x.text());
        Assert.assertEquals("xxx", y.text());
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
                    Assert.assertFalse(StringUtil.isBlank(textNode.text()));
                    if (!foundFirst) {
                        foundFirst = true;
                        Assert.assertEquals("One ", textNode.text());
                        Assert.assertEquals("One ", textNode.getWholeText());
                    }
                }
            }
        }
        Assert.assertTrue(foundFirst);
    }
}
