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
package com.itextpdf.styledxmlparser.jsoup.helper;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class StringUtilTest extends ExtendedITextTest {

    @Test public void join() {
        assertEquals("", StringUtil.join(Arrays.asList(""), " "));
        assertEquals("one", StringUtil.join(Arrays.asList("one"), " "));
        assertEquals("one two three", StringUtil.join(Arrays.asList("one", "two", "three"), " "));
    }

    @Test public void padding() {
        assertEquals("", StringUtil.padding(0));
        assertEquals(" ", StringUtil.padding(1));
        assertEquals("  ", StringUtil.padding(2));
        assertEquals("               ", StringUtil.padding(15));
    }

    @Test public void isBlank() {
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        assertTrue(StringUtil.isBlank("      "));
        assertTrue(StringUtil.isBlank("   \r\n  "));

        assertFalse(StringUtil.isBlank("hello"));
        assertFalse(StringUtil.isBlank("   hello   "));
    }

    @Test public void isNumeric() {
        assertFalse(StringUtil.isNumeric(null));
        assertFalse(StringUtil.isNumeric(" "));
        assertFalse(StringUtil.isNumeric("123 546"));
        assertFalse(StringUtil.isNumeric("hello"));
        assertFalse(StringUtil.isNumeric("123.334"));

        assertTrue(StringUtil.isNumeric("1"));
        assertTrue(StringUtil.isNumeric("1234"));
    }

    @Test public void isWhitespace() {
        assertTrue(StringUtil.isWhitespace('\t'));
        assertTrue(StringUtil.isWhitespace('\n'));
        assertTrue(StringUtil.isWhitespace('\r'));
        assertTrue(StringUtil.isWhitespace('\f'));
        assertTrue(StringUtil.isWhitespace(' '));
        
        assertFalse(StringUtil.isWhitespace('\u00a0'));
        assertFalse(StringUtil.isWhitespace('\u2000'));
        assertFalse(StringUtil.isWhitespace('\u3000'));
    }

    @Test public void normaliseWhiteSpace() {
        Assert.assertEquals(" ", StringUtil.normaliseWhitespace("    \r \n \r\n"));
        Assert.assertEquals(" hello there ", StringUtil.normaliseWhitespace("   hello   \r \n  there    \n"));
        Assert.assertEquals("hello", StringUtil.normaliseWhitespace("hello"));
        Assert.assertEquals("hello there", StringUtil.normaliseWhitespace("hello\nthere"));
    }

    @Test public void normaliseWhiteSpaceHandlesHighSurrogates() {
        String test71540chars = "\ud869\udeb2\u304b\u309a  1";
        String test71540charsExpectedSingleWhitespace = "\ud869\udeb2\u304b\u309a 1";

        Assert.assertEquals(test71540charsExpectedSingleWhitespace, StringUtil.normaliseWhitespace(test71540chars));
        String extractedText = Jsoup.parse(test71540chars).text();
        assertEquals(test71540charsExpectedSingleWhitespace, extractedText);
    }

    @Test public void resolvesRelativeUrls() {
        Assert.assertEquals("http://example.com/one/two?three", StringUtil.resolve("http://example.com", "./one/two?three"));
        Assert.assertEquals("http://example.com/one/two?three", StringUtil.resolve("http://example.com?one", "./one/two?three"));
        Assert.assertEquals("http://example.com/one/two?three#four", StringUtil.resolve("http://example.com", "./one/two?three#four"));
        Assert.assertEquals("https://example.com/one", StringUtil.resolve("http://example.com/", "https://example.com/one"));
        Assert.assertEquals("http://example.com/one/two.html", StringUtil.resolve("http://example.com/two/", "../one/two.html"));
        Assert.assertEquals("https://example2.com/one", StringUtil.resolve("https://example.com/", "//example2.com/one"));
        Assert.assertEquals("https://example.com:8080/one", StringUtil.resolve("https://example.com:8080", "./one"));
        Assert.assertEquals("https://example2.com/one", StringUtil.resolve("http://example.com/", "https://example2.com/one"));
        Assert.assertEquals("https://example.com/one", StringUtil.resolve("wrong", "https://example.com/one"));
        Assert.assertEquals("https://example.com/one", StringUtil.resolve("https://example.com/one", ""));
        Assert.assertEquals("", StringUtil.resolve("wrong", "also wrong"));
        Assert.assertEquals("ftp://example.com/one", StringUtil.resolve("ftp://example.com/two/", "../one"));
        Assert.assertEquals("ftp://example.com/one/two.c", StringUtil.resolve("ftp://example.com/one/", "./two.c"));
        Assert.assertEquals("ftp://example.com/one/two.c", StringUtil.resolve("ftp://example.com/one/", "two.c"));
    }
}
