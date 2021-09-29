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
package com.itextpdf.styledxmlparser.jsoup.internal;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.Collections;

import static com.itextpdf.styledxmlparser.jsoup.internal.StringUtil.normaliseWhitespace;
import static com.itextpdf.styledxmlparser.jsoup.internal.StringUtil.resolve;

@Category(UnitTest.class)
public class StringUtilTest extends ExtendedITextTest {

    @Test
    public void join() {
        Assert.assertEquals("", StringUtil.join(Collections.singletonList(""), " "));
        Assert.assertEquals("one", StringUtil.join(Collections.singletonList("one"), " "));
        Assert.assertEquals("one two three", StringUtil.join(Arrays.asList("one", "two", "three"), " "));
    }

    @Test public void padding() {
        Assert.assertEquals("", StringUtil.padding(0));
        Assert.assertEquals(" ", StringUtil.padding(1));
        Assert.assertEquals("  ", StringUtil.padding(2));
        Assert.assertEquals("               ", StringUtil.padding(15));
        Assert.assertEquals("                              ", StringUtil.padding(45)); // we tap out at 30
    }

    @Test public void paddingInACan() {
        String[] padding = StringUtil.padding;
        Assert.assertEquals(21, padding.length);
        for (int i = 0; i < padding.length; i++) {
            Assert.assertEquals(i, padding[i].length());
        }
    }

    @Test public void isBlank() {
        Assert.assertTrue(StringUtil.isBlank(null));
        Assert.assertTrue(StringUtil.isBlank(""));
        Assert.assertTrue(StringUtil.isBlank("      "));
        Assert.assertTrue(StringUtil.isBlank("   \r\n  "));

        Assert.assertFalse(StringUtil.isBlank("hello"));
        Assert.assertFalse(StringUtil.isBlank("   hello   "));
    }

    @Test public void isNumeric() {
        Assert.assertFalse(StringUtil.isNumeric(null));
        Assert.assertFalse(StringUtil.isNumeric(" "));
        Assert.assertFalse(StringUtil.isNumeric("123 546"));
        Assert.assertFalse(StringUtil.isNumeric("hello"));
        Assert.assertFalse(StringUtil.isNumeric("123.334"));

        Assert.assertTrue(StringUtil.isNumeric("1"));
        Assert.assertTrue(StringUtil.isNumeric("1234"));
    }

    @Test public void isWhitespace() {
        Assert.assertTrue(StringUtil.isWhitespace('\t'));
        Assert.assertTrue(StringUtil.isWhitespace('\n'));
        Assert.assertTrue(StringUtil.isWhitespace('\r'));
        Assert.assertTrue(StringUtil.isWhitespace('\f'));
        Assert.assertTrue(StringUtil.isWhitespace(' '));

        Assert.assertFalse(StringUtil.isWhitespace('\u00a0'));
        Assert.assertFalse(StringUtil.isWhitespace('\u2000'));
        Assert.assertFalse(StringUtil.isWhitespace('\u3000'));
    }

    @Test public void normaliseWhiteSpace() {
        Assert.assertEquals(" ", normaliseWhitespace("    \r \n \r\n"));
        Assert.assertEquals(" hello there ", normaliseWhitespace("   hello   \r \n  there    \n"));
        Assert.assertEquals("hello", normaliseWhitespace("hello"));
        Assert.assertEquals("hello there", normaliseWhitespace("hello\nthere"));
    }

    @Test public void normaliseWhiteSpaceHandlesHighSurrogates() {
        String test71540chars = "\ud869\udeb2\u304b\u309a  1";
        String test71540charsExpectedSingleWhitespace = "\ud869\udeb2\u304b\u309a 1";

        Assert.assertEquals(test71540charsExpectedSingleWhitespace, normaliseWhitespace(test71540chars));
        String extractedText = Jsoup.parse(test71540chars).text();
        Assert.assertEquals(test71540charsExpectedSingleWhitespace, extractedText);
    }

    @Test public void resolvesRelativeUrls() {
        Assert.assertEquals("http://example.com/one/two?three", resolve("http://example.com", "./one/two?three"));
        Assert.assertEquals("http://example.com/one/two?three", resolve("http://example.com?one", "./one/two?three"));
        Assert.assertEquals("http://example.com/one/two?three#four", resolve("http://example.com", "./one/two?three#four"));
        Assert.assertEquals("https://example.com/one", resolve("http://example.com/", "https://example.com/one"));
        Assert.assertEquals("http://example.com/one/two.html", resolve("http://example.com/two/", "../one/two.html"));
        Assert.assertEquals("https://example2.com/one", resolve("https://example.com/", "//example2.com/one"));
        Assert.assertEquals("https://example.com:8080/one", resolve("https://example.com:8080", "./one"));
        Assert.assertEquals("https://example2.com/one", resolve("http://example.com/", "https://example2.com/one"));
        Assert.assertEquals("https://example.com/one", resolve("wrong", "https://example.com/one"));
        Assert.assertEquals("https://example.com/one", resolve("https://example.com/one", ""));
        Assert.assertEquals("", resolve("wrong", "also wrong"));
        Assert.assertEquals("ftp://example.com/one", resolve("ftp://example.com/two/", "../one"));
        Assert.assertEquals("ftp://example.com/one/two.c", resolve("ftp://example.com/one/", "./two.c"));
        Assert.assertEquals("ftp://example.com/one/two.c", resolve("ftp://example.com/one/", "two.c"));
        // examples taken from rfc3986 section 5.4.2
        Assert.assertEquals("http://example.com/g", resolve("http://example.com/b/c/d;p?q", "../../../g"));
        Assert.assertEquals("http://example.com/g", resolve("http://example.com/b/c/d;p?q", "../../../../g"));
        Assert.assertEquals("http://example.com/g", resolve("http://example.com/b/c/d;p?q", "/./g"));
        Assert.assertEquals("http://example.com/g", resolve("http://example.com/b/c/d;p?q", "/../g"));
        Assert.assertEquals("http://example.com/b/c/g.", resolve("http://example.com/b/c/d;p?q", "g."));
        Assert.assertEquals("http://example.com/b/c/.g", resolve("http://example.com/b/c/d;p?q", ".g"));
        Assert.assertEquals("http://example.com/b/c/g..", resolve("http://example.com/b/c/d;p?q", "g.."));
        Assert.assertEquals("http://example.com/b/c/..g", resolve("http://example.com/b/c/d;p?q", "..g"));
        Assert.assertEquals("http://example.com/b/g", resolve("http://example.com/b/c/d;p?q", "./../g"));
        Assert.assertEquals("http://example.com/b/c/g/", resolve("http://example.com/b/c/d;p?q", "./g/."));
        Assert.assertEquals("http://example.com/b/c/g/h", resolve("http://example.com/b/c/d;p?q", "g/./h"));
        Assert.assertEquals("http://example.com/b/c/h", resolve("http://example.com/b/c/d;p?q", "g/../h"));
        Assert.assertEquals("http://example.com/b/c/g;x=1/y", resolve("http://example.com/b/c/d;p?q", "g;x=1/./y"));
        Assert.assertEquals("http://example.com/b/c/y", resolve("http://example.com/b/c/d;p?q", "g;x=1/../y"));
        Assert.assertEquals("http://example.com/b/c/g?y/./x", resolve("http://example.com/b/c/d;p?q", "g?y/./x"));
        Assert.assertEquals("http://example.com/b/c/g?y/../x", resolve("http://example.com/b/c/d;p?q", "g?y/../x"));
        Assert.assertEquals("http://example.com/b/c/g#s/./x", resolve("http://example.com/b/c/d;p?q", "g#s/./x"));
        Assert.assertEquals("http://example.com/b/c/g#s/../x", resolve("http://example.com/b/c/d;p?q", "g#s/../x"));
    }

    @Test
    public void isAscii() {
        Assert.assertTrue(StringUtil.isAscii(""));
        Assert.assertTrue(StringUtil.isAscii("example.com"));
        Assert.assertTrue(StringUtil.isAscii("One Two"));
        Assert.assertFalse(StringUtil.isAscii("🧔"));
        Assert.assertFalse(StringUtil.isAscii("测试"));
        Assert.assertFalse(StringUtil.isAscii("测试.com"));
    }
}
