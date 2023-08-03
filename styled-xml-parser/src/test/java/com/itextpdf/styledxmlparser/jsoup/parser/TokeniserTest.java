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
import com.itextpdf.styledxmlparser.jsoup.nodes.CDataNode;
import com.itextpdf.styledxmlparser.jsoup.nodes.Comment;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.Node;
import com.itextpdf.styledxmlparser.jsoup.nodes.TextNode;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@Category(UnitTest.class)
public class TokeniserTest extends ExtendedITextTest {
    @Test
    public void bufferUpInAttributeVal() {
        // https://github.com/jhy/jsoup/issues/967

        // check each double, singlem, unquoted impls
        String[] quotes = {"\"", "'", ""};
        for (String quote : quotes) {
            String preamble = "<img src=" + quote;
            String tail = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
            StringBuilder sb = new StringBuilder(preamble);

            final int charsToFillBuffer = CharacterReader.maxBufferLen - preamble.length();
            for (int i = 0; i < charsToFillBuffer; i++) {
                sb.append('a');
            }

            sb.append('X'); // First character to cross character buffer boundary
            sb.append(tail).append(quote).append(">\n");

            String html = sb.toString();
            Document doc = Jsoup.parse(html);
            String src = doc.select("img").attr("src");

            Assert.assertTrue(src.contains("X"));
            Assert.assertTrue(src.contains(tail));
        }
    }

    @Test public void handleSuperLargeTagNames() {
        // unlikely, but valid. so who knows.

        StringBuilder sb = new StringBuilder(CharacterReader.maxBufferLen);
        do {
            sb.append("LargeTagName");
        } while (sb.length() < CharacterReader.maxBufferLen);
        String tag = sb.toString();
        String html = "<" + tag + ">One</" + tag + ">";

        Document doc = Parser.htmlParser().settings(ParseSettings.preserveCase).parseInput(html, "");
        Elements els = doc.select(tag);
        Assert.assertEquals(1, els.size());
        Element el = els.first();
        Assert.assertNotNull(el);
        Assert.assertEquals("One", el.text());
        Assert.assertEquals(tag, el.tagName());
    }

    @Test public void handleSuperLargeAttributeName() {
        StringBuilder sb = new StringBuilder(CharacterReader.maxBufferLen);
        do {
            sb.append("LargAttributeName");
        } while (sb.length() < CharacterReader.maxBufferLen);
        String attrName = sb.toString();
        String html = "<p " + attrName + "=foo>One</p>";

        Document doc = Jsoup.parse(html);
        Elements els = doc.getElementsByAttribute(attrName);
        Assert.assertEquals(1, els.size());
        Element el = els.first();
        Assert.assertNotNull(el);
        Assert.assertEquals("One", el.text());
        Attribute attribute = el.attributes().asList().get(0);
        Assert.assertEquals(attrName.toLowerCase(), attribute.getKey());
        Assert.assertEquals("foo", attribute.getValue());
    }

    @Test public void handleLargeText() {
        StringBuilder sb = new StringBuilder(CharacterReader.maxBufferLen);
        do {
            sb.append("A Large Amount of Text");
        } while (sb.length() < CharacterReader.maxBufferLen);
        String text = sb.toString();
        String html = "<p>" + text + "</p>";

        Document doc = Jsoup.parse(html);
        Elements els = doc.select("p");
        Assert.assertEquals(1, els.size());
        Element el = els.first();

        Assert.assertNotNull(el);
        Assert.assertEquals(text, el.text());
    }

    @Test public void handleLargeComment() {
        StringBuilder sb = new StringBuilder(CharacterReader.maxBufferLen);
        do {
            sb.append("Quite a comment ");
        } while (sb.length() < CharacterReader.maxBufferLen);
        String comment = sb.toString();
        String html = "<p><!-- " + comment + " --></p>";

        Document doc = Jsoup.parse(html);
        Elements els = doc.select("p");
        Assert.assertEquals(1, els.size());
        Element el = els.first();

        Assert.assertNotNull(el);
        Comment child = (Comment) el.childNode(0);
        Assert.assertEquals(" " + comment + " ", child.getData());
    }

    @Test public void handleLargeCdata() {
        StringBuilder sb = new StringBuilder(CharacterReader.maxBufferLen);
        do {
            sb.append("Quite a lot of CDATA <><><><>");
        } while (sb.length() < CharacterReader.maxBufferLen);
        String cdata = sb.toString();
        String html = "<p><![CDATA[" + cdata + "]]></p>";

        Document doc = Jsoup.parse(html);
        Elements els = doc.select("p");
        Assert.assertEquals(1, els.size());
        Element el = els.first();

        Assert.assertNotNull(el);
        TextNode child = (TextNode) el.childNode(0);
        Assert.assertEquals(cdata, el.text());
        Assert.assertEquals(cdata, child.getWholeText());
    }

    @Test public void handleLargeTitle() {
        StringBuilder sb = new StringBuilder(CharacterReader.maxBufferLen);
        do {
            sb.append("Quite a long title");
        } while (sb.length() < CharacterReader.maxBufferLen);
        String title = sb.toString();
        String html = "<title>" + title + "</title>";

        Document doc = Jsoup.parse(html);
        Elements els = doc.select("title");
        Assert.assertEquals(1, els.size());
        Element el = els.first();

        Assert.assertNotNull(el);
        TextNode child = (TextNode) el.childNode(0);
        Assert.assertEquals(title, el.text());
        Assert.assertEquals(title, child.getWholeText());
        Assert.assertEquals(title, doc.title());
    }

    @Test public void cp1252Entities() {
        Assert.assertEquals("\u20ac", Jsoup.parse("&#0128;").text());
        Assert.assertEquals("\u201a", Jsoup.parse("&#0130;").text());
        Assert.assertEquals("\u20ac", Jsoup.parse("&#x80;").text());
    }

    @Test public void cp1252EntitiesProduceError() {
        Parser parser = new Parser(new HtmlTreeBuilder());
        parser.setTrackErrors(10);
        Assert.assertEquals("\u20ac", parser.parseInput("<html><body>&#0128;</body></html>", "").text());
        Assert.assertEquals(1, parser.getErrors().size());
    }

    @Test public void cp1252SubstitutionTable() throws UnsupportedEncodingException {
        for (int i = 0; i < Tokeniser.win1252Extensions.length; i++) {
            String s = new String(new byte[]{ (byte) (i + Tokeniser.win1252ExtensionsStart) }, "Windows-1252");
            Assert.assertEquals(1, s.length());

            // some of these characters are illegal
            if (s.charAt(0) == '\ufffd') { continue; }

            Assert.assertEquals(s.charAt(0), Tokeniser.win1252Extensions[i]);
        }
    }

    @Test public void canParseVeryLongBogusComment() {
        StringBuilder commentData = new StringBuilder(CharacterReader.maxBufferLen);
        do {
            commentData.append("blah blah blah blah ");
        } while (commentData.length() < CharacterReader.maxBufferLen);
        String expectedCommentData = commentData.toString();
        String testMarkup = "<html><body><!" + expectedCommentData + "></body></html>";
        Parser parser = new Parser(new HtmlTreeBuilder());

        Document doc = parser.parseInput(testMarkup, "");

        Node commentNode = doc.body().childNode(0);
        Assert.assertTrue(commentNode instanceof Comment);
        Assert.assertEquals(expectedCommentData, ((Comment)commentNode).getData());
    }

    @Test public void canParseCdataEndingAtEdgeOfBuffer() {
        String cdataStart = "<![CDATA[";
        String cdataEnd = "]]>";
        int bufLen = CharacterReader.maxBufferLen - cdataStart.length() - 1;    // also breaks with -2, but not with -3 or 0
        char[] cdataContentsArray = new char[bufLen];
        Arrays.fill(cdataContentsArray, 'x');
        String cdataContents = new String(cdataContentsArray);
        String testMarkup = cdataStart + cdataContents + cdataEnd;
        Parser parser = new Parser(new HtmlTreeBuilder());

        Document doc = parser.parseInput(testMarkup, "");

        Node cdataNode = doc.body().childNode(0);
        Assert.assertTrue(cdataNode instanceof CDataNode);
        Assert.assertEquals(cdataContents, ((CDataNode)cdataNode).text());
    }
}
