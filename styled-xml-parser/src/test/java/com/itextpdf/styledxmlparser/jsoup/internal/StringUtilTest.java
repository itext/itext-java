/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
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
