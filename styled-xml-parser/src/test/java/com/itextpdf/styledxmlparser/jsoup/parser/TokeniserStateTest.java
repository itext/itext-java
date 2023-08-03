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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.TextUtil;
import com.itextpdf.styledxmlparser.jsoup.nodes.Comment;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.TextNode;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;

@Category(UnitTest.class)
public class TokeniserStateTest extends ExtendedITextTest {

    final char[] whiteSpace = { '\t', '\n', '\r', '\f', ' ' };
    final char[] quote = { '\'', '"' };

    @Test
    public void ensureSearchArraysAreSorted() {
        char[][] arrays = {
            TokeniserState.attributeNameCharsSorted,
            TokeniserState.attributeValueUnquoted
        };

        for (char[] array : arrays) {
            char[] copy = Arrays.copyOf(array, array.length);
            Arrays.sort(array);
            Assert.assertArrayEquals(array, copy);
        }
    }

    @Test
    public void testCharacterReferenceInRcdata() {
        String body = "<textarea>You&I</textarea>";
        Document doc = Jsoup.parse(body);
        Elements els = doc.select("textarea");
        Assert.assertEquals("You&I", els.text());
    }

    @Test
    public void testBeforeTagName() {
        for (char c : whiteSpace) {
            String body = MessageFormatUtil.format("<div{0}>test</div>", c);
            Document doc = Jsoup.parse(body);
            Elements els = doc.select("div");
            Assert.assertEquals("test", els.text());
        }
    }

    @Test
    public void testEndTagOpen() {
        String body;
        Document doc;
        Elements els;

        body = "<div>hello world</";
        doc = Jsoup.parse(body);
        els = doc.select("div");
        Assert.assertEquals("hello world</", els.text());

        body = "<div>hello world</div>";
        doc = Jsoup.parse(body);
        els = doc.select("div");
        Assert.assertEquals("hello world", els.text());

        body = "<div>fake</></div>";
        doc = Jsoup.parse(body);
        els = doc.select("div");
        Assert.assertEquals("fake", els.text());

        body = "<div>fake</?</div>";
        doc = Jsoup.parse(body);
        els = doc.select("div");
        Assert.assertEquals("fake", els.text());
    }

    @Test
    public void testRcdataLessthanSign() {
        String body;
        Document doc;
        Elements els;

        body = "<textarea><fake></textarea>";
        doc = Jsoup.parse(body);
        els = doc.select("textarea");
        Assert.assertEquals("<fake>", els.text());

        body = "<textarea><open";
        doc = Jsoup.parse(body);
        els = doc.select("textarea");
        Assert.assertEquals("", els.text());

        body = "<textarea>hello world</?fake</textarea>";
        doc = Jsoup.parse(body);
        els = doc.select("textarea");
        Assert.assertEquals("hello world</?fake", els.text());
    }

    @Test
    public void testRCDATAEndTagName() {
        for (char c : whiteSpace) {
            String body = MessageFormatUtil.format("<textarea>data</textarea{0}>", c);
            Document doc = Jsoup.parse(body);
            Elements els = doc.select("textarea");
            Assert.assertEquals("data", els.text());
        }
    }

    @Test
    public void testCommentEndCoverage() {
        String html = "<html><head></head><body><img src=foo><!-- <table><tr><td></table> --! --- --><p>Hello</p></body></html>";
        Document doc = Jsoup.parse(html);

        Element body = doc.body();
        Comment comment = (Comment) body.childNode(1);
        Assert.assertEquals(" <table><tr><td></table> --! --- ", comment.getData());
        Element p = body.child(1);
        TextNode text = (TextNode) p.childNode(0);
        Assert.assertEquals("Hello", text.getWholeText());
    }

    @Test
    public void testCommentEndBangCoverage() {
        String html = "<html><head></head><body><img src=foo><!-- <table><tr><td></table> --!---!>--><p>Hello</p></body></html>";
        Document doc = Jsoup.parse(html);

        Element body = doc.body();
        Comment comment = (Comment) body.childNode(1);
        Assert.assertEquals(" <table><tr><td></table> --!-", comment.getData());
        Element p = body.child(1);
        TextNode text = (TextNode) p.childNode(0);
        Assert.assertEquals("Hello", text.getWholeText());
    }

    @Test
    public void testPublicIdentifiersWithWhitespace() {
        String expectedOutput = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0//EN\">";
        for (char q : quote) {
            for (char ws : whiteSpace) {
                String[] htmls = {
                        MessageFormatUtil.format("<!DOCTYPE html{0}PUBLIC {1}-//W3C//DTD HTML 4.0//EN{2}>", ws, q, q),
                        MessageFormatUtil.format("<!DOCTYPE html {0}PUBLIC {1}-//W3C//DTD HTML 4.0//EN{2}>", ws, q, q),
                        MessageFormatUtil.format("<!DOCTYPE html PUBLIC{0}{1}-//W3C//DTD HTML 4.0//EN{2}>", ws, q, q),
                        MessageFormatUtil.format("<!DOCTYPE html PUBLIC {0}{1}-//W3C//DTD HTML 4.0//EN{2}>", ws, q, q),
                        MessageFormatUtil.format("<!DOCTYPE html PUBLIC {0}-//W3C//DTD HTML 4.0//EN{1}{2}>", q, q, ws),
                        MessageFormatUtil.format("<!DOCTYPE html PUBLIC{0}-//W3C//DTD HTML 4.0//EN{1}{2}>", q, q, ws)
                    };
                for (String html : htmls) {
                    Document doc = Jsoup.parse(html);
                    Assert.assertEquals(expectedOutput, doc.childNode(0).outerHtml());
                }
            }
        }
    }

    @Test
    public void testSystemIdentifiersWithWhitespace() {
        String expectedOutput = "<!DOCTYPE html SYSTEM \"http://www.w3.org/TR/REC-html40/strict.dtd\">";
        for (char q : quote) {
            for (char ws : whiteSpace) {
                String[] htmls = {
                        MessageFormatUtil.format("<!DOCTYPE html{0}SYSTEM {1}http://www.w3.org/TR/REC-html40/strict.dtd{2}>", ws, q, q),
                        MessageFormatUtil.format("<!DOCTYPE html {0}SYSTEM {1}http://www.w3.org/TR/REC-html40/strict.dtd{2}>", ws, q, q),
                        MessageFormatUtil.format("<!DOCTYPE html SYSTEM{0}{1}http://www.w3.org/TR/REC-html40/strict.dtd{2}>", ws, q, q),
                        MessageFormatUtil.format("<!DOCTYPE html SYSTEM {0}{1}http://www.w3.org/TR/REC-html40/strict.dtd{2}>", ws, q, q),
                        MessageFormatUtil.format("<!DOCTYPE html SYSTEM {0}http://www.w3.org/TR/REC-html40/strict.dtd{1}{2}>", q, q, ws),
                        MessageFormatUtil.format("<!DOCTYPE html SYSTEM{0}http://www.w3.org/TR/REC-html40/strict.dtd{1}{2}>", q, q, ws)
                    };
                for (String html : htmls) {
                    Document doc = Jsoup.parse(html);
                    Assert.assertEquals(expectedOutput, doc.childNode(0).outerHtml());
                }
            }
        }
    }

    @Test
    public void testPublicAndSystemIdentifiersWithWhitespace() {
        String expectedOutput = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0//EN\""
                + " \"http://www.w3.org/TR/REC-html40/strict.dtd\">";
    	for (char q : quote) {
            for (char ws : whiteSpace) {
                String[] htmls = {
                        MessageFormatUtil.format("<!DOCTYPE html PUBLIC {0}-//W3C//DTD HTML 4.0//EN{1}"
                                + "{2}{3}http://www.w3.org/TR/REC-html40/strict.dtd{4}>", q, q, ws, q, q),
                        MessageFormatUtil.format("<!DOCTYPE html PUBLIC {0}-//W3C//DTD HTML 4.0//EN{1}"
                                + "{2}http://www.w3.org/TR/REC-html40/strict.dtd{3}>", q, q, q, q)
                    };
                for (String html : htmls) {
                    Document doc = Jsoup.parse(html);
                    Assert.assertEquals(expectedOutput, doc.childNode(0).outerHtml());
                }
            }
        }
    }

    @Test public void handlesLessInTagThanAsNewTag() {
        // out of spec, but clear author intent
        String html = "<p\n<p<div id=one <span>Two";
        Document doc = Jsoup.parse(html);
        Assert.assertEquals("<p></p><p></p><div id=\"one\"><span>Two</span></div>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void testUnconsumeAtBufferBoundary() {
        String triggeringSnippet = "<a href=\"\"foo";
        char[] padding = new char[CharacterReader.readAheadLimit - triggeringSnippet.length() + 2]; // The "foo" part must be just at the limit.
        Arrays.fill(padding, ' ');
        String paddedSnippet = String.valueOf(padding) + triggeringSnippet;
        ParseErrorList errorList = ParseErrorList.tracking(1);

        Parser.parseFragment(paddedSnippet, null, "", errorList);

        Assert.assertEquals(CharacterReader.readAheadLimit - 1, errorList.get(0).getPosition());
    }

    @Test
    public void testUnconsumeAfterBufferUp() {
        // test for after consume() a bufferUp occurs (look-forward) but then attempts to unconsume. Would throw a "No buffer left to unconsume"
        String triggeringSnippet = "<title>One <span>Two";
        char[] padding = new char[CharacterReader.readAheadLimit - triggeringSnippet.length() + 8]; // The "<span" part must be just at the limit. The "containsIgnoreCase" scan does a bufferUp, losing the unconsume
        Arrays.fill(padding, ' ');
        String paddedSnippet = String.valueOf(padding) + triggeringSnippet;
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(paddedSnippet, null, "", errorList);
    }

    @Test
    public void testOpeningAngleBracketInsteadOfAttribute() {
        String triggeringSnippet = "<html <";
        ParseErrorList errorList = ParseErrorList.tracking(1);

        Parser.parseFragment(triggeringSnippet, null, "", errorList);

        Assert.assertEquals(6, errorList.get(0).getPosition());
    }

    @Test
    public void testMalformedSelfClosingTag() {
        String triggeringSnippet = "<html /ouch";
        ParseErrorList errorList = ParseErrorList.tracking(1);

        Parser.parseFragment(triggeringSnippet, null, "", errorList);

        Assert.assertEquals(7, errorList.get(0).getPosition());
    }

    @Test
    public void testOpeningAngleBracketInTagName() {
        String triggeringSnippet = "<html<";
        ParseErrorList errorList = ParseErrorList.tracking(1);

        Parser.parseFragment(triggeringSnippet, null, "", errorList);

        Assert.assertEquals(5, errorList.get(0).getPosition());
    }

    @Test
    public void rcData() {
        Document doc = Jsoup.parse("<title>One \0Two</title>");
        Assert.assertEquals("One �Two", doc.title());
    }

    @Test
    public void plaintext() {
        Document doc = Jsoup.parse("<div>One<plaintext><div>Two</plaintext>\0no < Return");
        Assert.assertEquals("<html><head></head><body><div>One<plaintext>&lt;div&gt;Two&lt;/plaintext&gt;�no &lt; Return</plaintext></div></body></html>", TextUtil.stripNewlines(doc.html()));
    }

    @Test
    public void nullInTag() {
        Document doc = Jsoup.parse("<di\0v>One</di\0v>Two");
        Assert.assertEquals("<di�v>\n One\n</di�v>Two", doc.body().html());
    }

    @Test
    public void attributeValUnquoted() {
        Document doc = Jsoup.parse("<p name=foo&lt;bar>");
        Element p = doc.selectFirst("p");
        Assert.assertEquals("foo<bar", p.attr("name"));

        doc = Jsoup.parse("<p foo=");
        Assert.assertEquals("<p foo></p>", doc.body().html());
    }

    @Test
    public void testRCDATAEndTagNameDiffTag() {
        String body = "<textarea>data</textare >";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected token"));
    }

    @Test
    public void testRCDATAEndTagNameValidSlash() {
        String body = "<textarea>data</textarea/>";
        Document doc = Jsoup.parse(body);
        Elements els = doc.select("textarea");
        Assert.assertEquals("data", els.text());
    }

    @Test
    public void testRCDATAEndTagNameInvalidSlash() {
        String body = "<textarea>data</textare/>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected token"));
    }

    @Test
    public void scriptDataEscapeStartDashValid() {
        String body = "<script><!-- text --></script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void scriptDataEscapeStartDashInvalid() {
        String body = "<script><!- text --></script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void scriptDataEscapedEmpty() {
        String body = "<script><!-- ";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpectedly reached end of file (EOF) in input state"));
    }

    @Test
    public void scriptDataEscapedStartTag() {
        String body = "<script><!--<</script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void scriptDataEscapedNullChar() {
        String body = "<script><!--a\0";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void scriptDataEscapedDashEmpty() {
        String body = "<script><!-- -";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpectedly reached end of file (EOF) in input state"));
    }

    @Test
    public void scriptDataEscapedDashStTag() {
        String body = "<script><!-- -<</script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void scriptDataEscapedDashNullChar() {
        String body = "<script><!-- -\0";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void scriptDataEscapedDashDashEmpty() {
        String body = "<script><!-- --";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpectedly reached end of file (EOF) in input state"));
    }

    @Test
    public void scriptDataEscapedDashDashStTag() {
        String body = "<script><!-- --<</script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void scriptDataEscapedDashDashNullChar() {
        String body = "<script><!-- --\0";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void scriptDataEscapedEndTagOpen() {
        String body = "<script><!-- --</---></script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void scriptDataDoubleEscapedNullChar() {
        String body = "<script><!--<script><\0!-";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void scriptDataDoubleEscapedEof() {
        String body = "<script><!--<script><!-";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpectedly reached end of file (EOF) in input state"));
    }

    @Test
    public void scriptDataDoubleEscapedDash() {
        String body = "<script><!--<script><!-- --></script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void scriptDataDoubleEscapedDashStTag() {
        String body = "<script><!--<script><!-< --></script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void scriptDataDoubleEscapedDashNull() {
        String body = "<script><!--<script><!-\0 --></script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void scriptDataDoubleEscapedDashEof() {
        String body = "<script><!--<script><!-";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpectedly reached end of file (EOF) in input state"));
    }

    @Test
    public void scriptDataDoubleEscapedDashDefault() {
        String body = "<script><!--<script><!-aaa --></script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void scriptDataDoubleEscapedDashDash() {
        String body = "<script><!--<script><!--- --></script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void scriptDataDoubleEscapedDashDashStTag() {
        String body = "<script><!--<script><!--< --></script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void scriptDataDoubleEscapedDashDashNull() {
        String body = "<script><!--<script><!--\0 --></script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void scriptDataDoubleEscapedDashDashEof() {
        String body = "<script><!--<script><!--";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpectedly reached end of file (EOF) in input state"));
    }

    @Test
    public void scriptDataDoubleEscapedDashDashDefault() {
        String body = "<script><!--<script><!--aaa --></script>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void attributeNameStTag() {
        String body = "<p name< />";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void afterAttributeNameEndTag() {
        String body = "<p name > />";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void afterAttributeNameNull() {
        String body = "<p name \0 />";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void afterAttributeNameEof() {
        String body = "<p name ";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpectedly reached end of file (EOF) in input state"));
    }

    @Test
    public void afterAttributeNameStTag() {
        String body = "<p name <";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void beforeAttributeNameClTag() {
        String body = "<p name=></p>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void beforeAttributeNameStTag() {
        String body = "<p name=<</p>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void attributeValueDoubleQuotedNull() {
        String body = "<p name=\"\0\"></p>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void attributeValueSingleQuotedNull() {
        String body = "<p name='\0'></p>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void attributeValueSingleUnquotedAmp() {
        String body = "<p name=&a></p>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(0, errorList.size());
    }

    @Test
    public void attributeValueSingleUnquotedNull() {
        String body = "<p name=a\0></p>";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpected character"));
    }

    @Test
    public void attributeValueSingleUnquotedEof() {
        String body = "<p name=a";
        ParseErrorList errorList = ParseErrorList.tracking(1);
        Parser.parseFragment(body, null, "", errorList);
        Assert.assertEquals(1, errorList.size());
        Assert.assertTrue(errorList.get(0).getErrorMessage()
                .contains("Unexpectedly reached end of file (EOF) in input state"));
    }

    @Test
    public void tokeniserStateToStringTest() {
        Assert.assertEquals("Data", TokeniserState.Data.toString());
        Assert.assertEquals("CharacterReferenceInData", TokeniserState.CharacterReferenceInData.toString());
        Assert.assertEquals("Rcdata", TokeniserState.Rcdata.toString());
        Assert.assertEquals("CharacterReferenceInRcdata", TokeniserState.CharacterReferenceInRcdata.toString());
        Assert.assertEquals("Rawtext", TokeniserState.Rawtext.toString());
        Assert.assertEquals("ScriptData", TokeniserState.ScriptData.toString());
        Assert.assertEquals("PLAINTEXT", TokeniserState.PLAINTEXT.toString());
        Assert.assertEquals("TagOpen", TokeniserState.TagOpen.toString());
        Assert.assertEquals("EndTagOpen", TokeniserState.EndTagOpen.toString());
        Assert.assertEquals("TagName", TokeniserState.TagName.toString());
        Assert.assertEquals("RcdataLessthanSign", TokeniserState.RcdataLessthanSign.toString());
        Assert.assertEquals("RCDATAEndTagOpen", TokeniserState.RCDATAEndTagOpen.toString());
        Assert.assertEquals("RCDATAEndTagName", TokeniserState.RCDATAEndTagName.toString());
        Assert.assertEquals("RawtextLessthanSign", TokeniserState.RawtextLessthanSign.toString());
        Assert.assertEquals("RawtextEndTagOpen", TokeniserState.RawtextEndTagOpen.toString());
        Assert.assertEquals("RawtextEndTagName", TokeniserState.RawtextEndTagName.toString());
        Assert.assertEquals("ScriptDataLessthanSign", TokeniserState.ScriptDataLessthanSign.toString());
        Assert.assertEquals("ScriptDataEndTagOpen", TokeniserState.ScriptDataEndTagOpen.toString());
        Assert.assertEquals("ScriptDataEndTagName", TokeniserState.ScriptDataEndTagName.toString());
        Assert.assertEquals("ScriptDataEscapeStart", TokeniserState.ScriptDataEscapeStart.toString());
        Assert.assertEquals("ScriptDataEscapeStartDash", TokeniserState.ScriptDataEscapeStartDash.toString());
        Assert.assertEquals("ScriptDataEscaped", TokeniserState.ScriptDataEscaped.toString());
        Assert.assertEquals("ScriptDataEscapedDash", TokeniserState.ScriptDataEscapedDash.toString());
        Assert.assertEquals("ScriptDataEscapedDashDash", TokeniserState.ScriptDataEscapedDashDash.toString());
        Assert.assertEquals("ScriptDataEscapedLessthanSign", TokeniserState.ScriptDataEscapedLessthanSign.toString());
        Assert.assertEquals("ScriptDataEscapedEndTagOpen", TokeniserState.ScriptDataEscapedEndTagOpen.toString());
        Assert.assertEquals("ScriptDataEscapedEndTagName", TokeniserState.ScriptDataEscapedEndTagName.toString());
        Assert.assertEquals("ScriptDataDoubleEscapeStart", TokeniserState.ScriptDataDoubleEscapeStart.toString());
        Assert.assertEquals("ScriptDataDoubleEscaped", TokeniserState.ScriptDataDoubleEscaped.toString());
        Assert.assertEquals("ScriptDataDoubleEscapedDash", TokeniserState.ScriptDataDoubleEscapedDash.toString());
        Assert.assertEquals("ScriptDataDoubleEscapedDashDash", TokeniserState.ScriptDataDoubleEscapedDashDash.toString());
        Assert.assertEquals("ScriptDataDoubleEscapedLessthanSign", TokeniserState.ScriptDataDoubleEscapedLessthanSign.toString());
        Assert.assertEquals("ScriptDataDoubleEscapeEnd", TokeniserState.ScriptDataDoubleEscapeEnd.toString());
        Assert.assertEquals("BeforeAttributeName", TokeniserState.BeforeAttributeName.toString());
        Assert.assertEquals("AttributeName", TokeniserState.AttributeName.toString());
        Assert.assertEquals("AfterAttributeName", TokeniserState.AfterAttributeName.toString());
        Assert.assertEquals("BeforeAttributeValue", TokeniserState.BeforeAttributeValue.toString());
        Assert.assertEquals("AttributeValue_doubleQuoted", TokeniserState.AttributeValue_doubleQuoted.toString());
        Assert.assertEquals("AttributeValue_singleQuoted", TokeniserState.AttributeValue_singleQuoted.toString());
        Assert.assertEquals("AttributeValue_unquoted", TokeniserState.AttributeValue_unquoted.toString());
        Assert.assertEquals("AfterAttributeValue_quoted", TokeniserState.AfterAttributeValue_quoted.toString());
        Assert.assertEquals("SelfClosingStartTag", TokeniserState.SelfClosingStartTag.toString());
        Assert.assertEquals("BogusComment", TokeniserState.BogusComment.toString());
        Assert.assertEquals("MarkupDeclarationOpen", TokeniserState.MarkupDeclarationOpen.toString());
        Assert.assertEquals("CommentStart", TokeniserState.CommentStart.toString());
        Assert.assertEquals("CommentStartDash", TokeniserState.CommentStartDash.toString());
        Assert.assertEquals("Comment", TokeniserState.Comment.toString());
        Assert.assertEquals("CommentEndDash", TokeniserState.CommentEndDash.toString());
        Assert.assertEquals("CommentEnd", TokeniserState.CommentEnd.toString());
        Assert.assertEquals("CommentEndBang", TokeniserState.CommentEndBang.toString());
        Assert.assertEquals("Doctype", TokeniserState.Doctype.toString());
        Assert.assertEquals("BeforeDoctypeName", TokeniserState.BeforeDoctypeName.toString());
        Assert.assertEquals("DoctypeName", TokeniserState.DoctypeName.toString());
        Assert.assertEquals("AfterDoctypeName", TokeniserState.AfterDoctypeName.toString());
        Assert.assertEquals("AfterDoctypePublicKeyword", TokeniserState.AfterDoctypePublicKeyword.toString());
        Assert.assertEquals("BeforeDoctypePublicIdentifier", TokeniserState.BeforeDoctypePublicIdentifier.toString());
        Assert.assertEquals("DoctypePublicIdentifier_doubleQuoted", TokeniserState.DoctypePublicIdentifier_doubleQuoted.toString());
        Assert.assertEquals("DoctypePublicIdentifier_singleQuoted", TokeniserState.DoctypePublicIdentifier_singleQuoted.toString());
        Assert.assertEquals("AfterDoctypePublicIdentifier", TokeniserState.AfterDoctypePublicIdentifier.toString());
        Assert.assertEquals("BetweenDoctypePublicAndSystemIdentifiers", TokeniserState.BetweenDoctypePublicAndSystemIdentifiers.toString());
        Assert.assertEquals("AfterDoctypeSystemKeyword", TokeniserState.AfterDoctypeSystemKeyword.toString());
        Assert.assertEquals("BeforeDoctypeSystemIdentifier", TokeniserState.BeforeDoctypeSystemIdentifier.toString());
        Assert.assertEquals("DoctypeSystemIdentifier_doubleQuoted", TokeniserState.DoctypeSystemIdentifier_doubleQuoted.toString());
        Assert.assertEquals("DoctypeSystemIdentifier_singleQuoted", TokeniserState.DoctypeSystemIdentifier_singleQuoted.toString());
        Assert.assertEquals("AfterDoctypeSystemIdentifier", TokeniserState.AfterDoctypeSystemIdentifier.toString());
        Assert.assertEquals("BogusDoctype", TokeniserState.BogusDoctype.toString());
        Assert.assertEquals("CdataSection", TokeniserState.CdataSection.toString());
    }
}
