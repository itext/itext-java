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
package com.itextpdf.styledxmlparser.jsoup.internal;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.Arrays;
import java.util.Collections;

import static com.itextpdf.styledxmlparser.jsoup.internal.StringUtil.normaliseWhitespace;
import static com.itextpdf.styledxmlparser.jsoup.internal.StringUtil.resolve;

@Tag("UnitTest")
public class StringUtilTest extends ExtendedITextTest {

    @Test
    public void join() {
        Assertions.assertEquals("", StringUtil.join(Collections.singletonList(""), " "));
        Assertions.assertEquals("one", StringUtil.join(Collections.singletonList("one"), " "));
        Assertions.assertEquals("one two three", StringUtil.join(Arrays.asList("one", "two", "three"), " "));
    }

    @Test public void padding() {
        Assertions.assertEquals("", StringUtil.padding(0));
        Assertions.assertEquals(" ", StringUtil.padding(1));
        Assertions.assertEquals("  ", StringUtil.padding(2));
        Assertions.assertEquals("               ", StringUtil.padding(15));
        Assertions.assertEquals("                              ", StringUtil.padding(45)); // we tap out at 30
    }

    @Test public void paddingInACan() {
        String[] padding = StringUtil.padding;
        Assertions.assertEquals(21, padding.length);
        for (int i = 0; i < padding.length; i++) {
            Assertions.assertEquals(i, padding[i].length());
        }
    }

    @Test public void isBlank() {
        Assertions.assertTrue(StringUtil.isBlank(null));
        Assertions.assertTrue(StringUtil.isBlank(""));
        Assertions.assertTrue(StringUtil.isBlank("      "));
        Assertions.assertTrue(StringUtil.isBlank("   \r\n  "));

        Assertions.assertFalse(StringUtil.isBlank("hello"));
        Assertions.assertFalse(StringUtil.isBlank("   hello   "));
    }

    @Test public void isNumeric() {
        Assertions.assertFalse(StringUtil.isNumeric(null));
        Assertions.assertFalse(StringUtil.isNumeric(" "));
        Assertions.assertFalse(StringUtil.isNumeric("123 546"));
        Assertions.assertFalse(StringUtil.isNumeric("hello"));
        Assertions.assertFalse(StringUtil.isNumeric("123.334"));

        Assertions.assertTrue(StringUtil.isNumeric("1"));
        Assertions.assertTrue(StringUtil.isNumeric("1234"));
    }

    @Test public void isWhitespace() {
        Assertions.assertTrue(StringUtil.isWhitespace('\t'));
        Assertions.assertTrue(StringUtil.isWhitespace('\n'));
        Assertions.assertTrue(StringUtil.isWhitespace('\r'));
        Assertions.assertTrue(StringUtil.isWhitespace('\f'));
        Assertions.assertTrue(StringUtil.isWhitespace(' '));

        Assertions.assertFalse(StringUtil.isWhitespace('\u00a0'));
        Assertions.assertFalse(StringUtil.isWhitespace('\u2000'));
        Assertions.assertFalse(StringUtil.isWhitespace('\u3000'));
    }

    @Test public void normaliseWhiteSpace() {
        Assertions.assertEquals(" ", normaliseWhitespace("    \r \n \r\n"));
        Assertions.assertEquals(" hello there ", normaliseWhitespace("   hello   \r \n  there    \n"));
        Assertions.assertEquals("hello", normaliseWhitespace("hello"));
        Assertions.assertEquals("hello there", normaliseWhitespace("hello\nthere"));
    }

    @Test public void normaliseWhiteSpaceHandlesHighSurrogates() {
        String test71540chars = "\ud869\udeb2\u304b\u309a  1";
        String test71540charsExpectedSingleWhitespace = "\ud869\udeb2\u304b\u309a 1";

        Assertions.assertEquals(test71540charsExpectedSingleWhitespace, normaliseWhitespace(test71540chars));
        String extractedText = Jsoup.parse(test71540chars).text();
        Assertions.assertEquals(test71540charsExpectedSingleWhitespace, extractedText);
    }

    @Test public void resolvesRelativeUrls() {
        Assertions.assertEquals("http://example.com/one/two?three", resolve("http://example.com", "./one/two?three"));
        Assertions.assertEquals("http://example.com/one/two?three", resolve("http://example.com?one", "./one/two?three"));
        Assertions.assertEquals("http://example.com/one/two?three#four", resolve("http://example.com", "./one/two?three#four"));
        Assertions.assertEquals("https://example.com/one", resolve("http://example.com/", "https://example.com/one"));
        Assertions.assertEquals("http://example.com/one/two.html", resolve("http://example.com/two/", "../one/two.html"));
        Assertions.assertEquals("https://example2.com/one", resolve("https://example.com/", "//example2.com/one"));
        Assertions.assertEquals("https://example.com:8080/one", resolve("https://example.com:8080", "./one"));
        Assertions.assertEquals("https://example2.com/one", resolve("http://example.com/", "https://example2.com/one"));
        Assertions.assertEquals("https://example.com/one", resolve("wrong", "https://example.com/one"));
        Assertions.assertEquals("https://example.com/one", resolve("https://example.com/one", ""));
        Assertions.assertEquals("", resolve("wrong", "also wrong"));
        Assertions.assertEquals("ftp://example.com/one", resolve("ftp://example.com/two/", "../one"));
        Assertions.assertEquals("ftp://example.com/one/two.c", resolve("ftp://example.com/one/", "./two.c"));
        Assertions.assertEquals("ftp://example.com/one/two.c", resolve("ftp://example.com/one/", "two.c"));
        // examples taken from rfc3986 section 5.4.2
        Assertions.assertEquals("http://example.com/g", resolve("http://example.com/b/c/d;p?q", "../../../g"));
        Assertions.assertEquals("http://example.com/g", resolve("http://example.com/b/c/d;p?q", "../../../../g"));
        Assertions.assertEquals("http://example.com/g", resolve("http://example.com/b/c/d;p?q", "/./g"));
        Assertions.assertEquals("http://example.com/g", resolve("http://example.com/b/c/d;p?q", "/../g"));
        Assertions.assertEquals("http://example.com/b/c/g.", resolve("http://example.com/b/c/d;p?q", "g."));
        Assertions.assertEquals("http://example.com/b/c/.g", resolve("http://example.com/b/c/d;p?q", ".g"));
        Assertions.assertEquals("http://example.com/b/c/g..", resolve("http://example.com/b/c/d;p?q", "g.."));
        Assertions.assertEquals("http://example.com/b/c/..g", resolve("http://example.com/b/c/d;p?q", "..g"));
        Assertions.assertEquals("http://example.com/b/g", resolve("http://example.com/b/c/d;p?q", "./../g"));
        Assertions.assertEquals("http://example.com/b/c/g/", resolve("http://example.com/b/c/d;p?q", "./g/."));
        Assertions.assertEquals("http://example.com/b/c/g/h", resolve("http://example.com/b/c/d;p?q", "g/./h"));
        Assertions.assertEquals("http://example.com/b/c/h", resolve("http://example.com/b/c/d;p?q", "g/../h"));
        Assertions.assertEquals("http://example.com/b/c/g;x=1/y", resolve("http://example.com/b/c/d;p?q", "g;x=1/./y"));
        Assertions.assertEquals("http://example.com/b/c/y", resolve("http://example.com/b/c/d;p?q", "g;x=1/../y"));
        Assertions.assertEquals("http://example.com/b/c/g?y/./x", resolve("http://example.com/b/c/d;p?q", "g?y/./x"));
        Assertions.assertEquals("http://example.com/b/c/g?y/../x", resolve("http://example.com/b/c/d;p?q", "g?y/../x"));
        Assertions.assertEquals("http://example.com/b/c/g#s/./x", resolve("http://example.com/b/c/d;p?q", "g#s/./x"));
        Assertions.assertEquals("http://example.com/b/c/g#s/../x", resolve("http://example.com/b/c/d;p?q", "g#s/../x"));
    }

    @Test
    public void isAscii() {
        Assertions.assertTrue(StringUtil.isAscii(""));
        Assertions.assertTrue(StringUtil.isAscii("example.com"));
        Assertions.assertTrue(StringUtil.isAscii("One Two"));
        Assertions.assertFalse(StringUtil.isAscii("🧔"));
        Assertions.assertFalse(StringUtil.isAscii("测试"));
        Assertions.assertFalse(StringUtil.isAscii("测试.com"));
    }
}
