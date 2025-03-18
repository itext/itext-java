/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@Tag("UnitTest")
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

            Assertions.assertTrue(src.contains("X"));
            Assertions.assertTrue(src.contains(tail));
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
        Assertions.assertEquals(1, els.size());
        Element el = els.first();
        Assertions.assertNotNull(el);
        Assertions.assertEquals("One", el.text());
        Assertions.assertEquals(tag, el.tagName());
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
        Assertions.assertEquals(1, els.size());
        Element el = els.first();
        Assertions.assertNotNull(el);
        Assertions.assertEquals("One", el.text());
        Attribute attribute = el.attributes().asList().get(0);
        Assertions.assertEquals(attrName.toLowerCase(), attribute.getKey());
        Assertions.assertEquals("foo", attribute.getValue());
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
        Assertions.assertEquals(1, els.size());
        Element el = els.first();

        Assertions.assertNotNull(el);
        Assertions.assertEquals(text, el.text());
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
        Assertions.assertEquals(1, els.size());
        Element el = els.first();

        Assertions.assertNotNull(el);
        Comment child = (Comment) el.childNode(0);
        Assertions.assertEquals(" " + comment + " ", child.getData());
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
        Assertions.assertEquals(1, els.size());
        Element el = els.first();

        Assertions.assertNotNull(el);
        TextNode child = (TextNode) el.childNode(0);
        Assertions.assertEquals(cdata, el.text());
        Assertions.assertEquals(cdata, child.getWholeText());
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
        Assertions.assertEquals(1, els.size());
        Element el = els.first();

        Assertions.assertNotNull(el);
        TextNode child = (TextNode) el.childNode(0);
        Assertions.assertEquals(title, el.text());
        Assertions.assertEquals(title, child.getWholeText());
        Assertions.assertEquals(title, doc.title());
    }

    @Test public void cp1252Entities() {
        Assertions.assertEquals("\u20ac", Jsoup.parse("&#0128;").text());
        Assertions.assertEquals("\u201a", Jsoup.parse("&#0130;").text());
        Assertions.assertEquals("\u20ac", Jsoup.parse("&#x80;").text());
    }

    @Test public void cp1252EntitiesProduceError() {
        Parser parser = new Parser(new HtmlTreeBuilder());
        parser.setTrackErrors(10);
        Assertions.assertEquals("\u20ac", parser.parseInput("<html><body>&#0128;</body></html>", "").text());
        Assertions.assertEquals(1, parser.getErrors().size());
    }

    @Test public void cp1252SubstitutionTable() throws UnsupportedEncodingException {
        for (int i = 0; i < Tokeniser.win1252Extensions.length; i++) {
            String s = new String(new byte[]{ (byte) (i + Tokeniser.win1252ExtensionsStart) }, "Windows-1252");
            Assertions.assertEquals(1, s.length());

            // some of these characters are illegal
            if (s.charAt(0) == '\ufffd') { continue; }

            Assertions.assertEquals(s.charAt(0), Tokeniser.win1252Extensions[i]);
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
        Assertions.assertTrue(commentNode instanceof Comment);
        Assertions.assertEquals(expectedCommentData, ((Comment)commentNode).getData());
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
        Assertions.assertTrue(cdataNode instanceof CDataNode);
        Assertions.assertEquals(cdataContents, ((CDataNode)cdataNode).text());
    }
}
