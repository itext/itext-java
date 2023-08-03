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
import com.itextpdf.styledxmlparser.jsoup.integration.ParseTest;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Category(UnitTest.class)
public class DataUtilTest extends ExtendedITextTest {
    @Test
    public void testCharset() {
        Assert.assertEquals("utf-8", DataUtil.getCharsetFromContentType("text/html;charset=utf-8 "));
        Assert.assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=UTF-8"));
        Assert.assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html; charset=ISO-8859-1"));
        Assert.assertNull(DataUtil.getCharsetFromContentType("text/html"));
        Assert.assertNull(DataUtil.getCharsetFromContentType(null));
        Assert.assertNull(DataUtil.getCharsetFromContentType("text/html;charset=Unknown"));
    }

    @Test
    public void testQuotedCharset() {
        Assert.assertEquals("utf-8", DataUtil.getCharsetFromContentType("text/html; charset=\"utf-8\""));
        Assert.assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html;charset=\"UTF-8\""));
        Assert.assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html; charset=\"ISO-8859-1\""));
        Assert.assertNull(DataUtil.getCharsetFromContentType("text/html; charset=\"Unsupported\""));
        Assert.assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset='UTF-8'"));
    }

    private InputStream stream(String data) {
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }

    private InputStream stream(String data, String charset) {
        try {
            return new ByteArrayInputStream(data.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            Assert.fail();
        }
        return null;
    }

    @Test
    public void discardsSpuriousByteOrderMark() throws IOException {
        String html = "\uFEFF<html><head><title>One</title></head><body>Two</body></html>";
        Document doc = DataUtil.parseInputStream(stream(html), "UTF-8", "http://foo.com/", Parser.htmlParser());
        Assert.assertEquals("One", doc.head().text());
    }

    @Test
    public void discardsSpuriousByteOrderMarkWhenNoCharsetSet() throws IOException {
        String html = "\uFEFF<html><head><title>One</title></head><body>Two</body></html>";
        Document doc = DataUtil.parseInputStream(stream(html), null, "http://foo.com/", Parser.htmlParser());
        Assert.assertEquals("One", doc.head().text());
        Assert.assertEquals("UTF-8", doc.outputSettings().charset().displayName());
    }

    @Test
    public void shouldNotThrowExceptionOnEmptyCharset() {
        Assert.assertNull(DataUtil.getCharsetFromContentType("text/html; charset="));
        Assert.assertNull(DataUtil.getCharsetFromContentType("text/html; charset=;"));
    }

    @Test
    public void shouldSelectFirstCharsetOnWeirdMultileCharsetsInMetaTags() {
        Assert.assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html; charset=ISO-8859-1, charset=1251"));
    }

    @Test
    public void shouldCorrectCharsetForDuplicateCharsetString() {
        Assert.assertEquals("iso-8859-1", DataUtil.getCharsetFromContentType("text/html; charset=charset=iso-8859-1"));
    }

    @Test
    public void shouldReturnNullForIllegalCharsetNames() {
        Assert.assertNull(DataUtil.getCharsetFromContentType("text/html; charset=$HJKDF§$/("));
    }

    @Test
    public void wrongMetaCharsetFallback() throws IOException {
        String html = "<html><head><meta charset=iso-8></head><body></body></html>";

        Document doc = DataUtil.parseInputStream(stream(html), null, "http://example.com", Parser.htmlParser());

        final String expected = "<html>\n" +
                " <head>\n" +
                "  <meta charset=\"iso-8\">\n" +
                " </head>\n" +
                " <body></body>\n" +
                "</html>";

        Assert.assertEquals(expected, doc.toString());
    }

    @Test
    public void secondMetaElementWithContentTypeContainsCharsetParameter() throws Exception {
        String html = "<html><head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html\">" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" +
                "</head><body>한국어</body></html>";

        Document doc = DataUtil.parseInputStream(stream(html, "euc-kr"), null, "http://example.com", Parser.htmlParser());

        Assert.assertEquals("한국어", doc.body().text());
    }

    @Test
    public void firstMetaElementWithCharsetShouldBeUsedForDecoding() throws Exception {
        String html = "<html><head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=koi8-u\">" +
                "</head><body>Übergrößenträger</body></html>";

        Document doc = DataUtil.parseInputStream(stream(html, "iso-8859-1"), null, "http://example.com", Parser.htmlParser());

        Assert.assertEquals("Übergrößenträger", doc.body().text());
    }

    @Test
    public void supportsBOMinFiles() throws IOException {
        // test files from http://www.i18nl10n.com/korean/utftest/
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        Assert.assertTrue(doc.title().contains("UTF-16BE"));
        Assert.assertTrue(doc.text().contains("가각갂갃간갅"));

        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        Assert.assertTrue(doc.title().contains("UTF-16LE"));
        Assert.assertTrue(doc.text().contains("가각갂갃간갅"));

        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        Assert.assertTrue(doc.title().contains("UTF-32BE"));
        Assert.assertTrue(doc.text().contains("가각갂갃간갅"));

        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        Assert.assertTrue(doc.title().contains("UTF-32LE"));
        Assert.assertTrue(doc.text().contains("가각갂갃간갅"));
    }

    @Test
    public void supportsUTF8BOM() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf8.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        Assert.assertEquals("OK", doc.head().select("title").text());
    }

    @Test
    public void noExtraNULLBytes() throws IOException {
    	final byte[] b = "<html><head><meta charset=\"UTF-8\"></head><body><div><u>ü</u>ü</div></body></html>".getBytes("UTF-8");
    	
    	Document doc = Jsoup.parse(new ByteArrayInputStream(b), null, "");
        Assert.assertFalse( doc.outerHtml().contains("\u0000") );
    }

    @Test
    public void supportsZippedUTF8BOM() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf8.html.gz");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        Assert.assertEquals("OK", doc.head().select("title").text());
        Assert.assertEquals("There is a UTF8 BOM at the top (before the XML decl). If not read correctly, will look like a non-joining space.", doc.body().text());
    }

    @Test
    public void supportsXmlCharsetDeclaration() throws IOException {
        String encoding = "iso-8859-1";
        InputStream soup = new ByteArrayInputStream((
                "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>" +
                        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
                        "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">Hellö Wörld!</html>"
        ).getBytes(encoding));

        Document doc = Jsoup.parse(soup, null, "");
        Assert.assertEquals("Hellö Wörld!", doc.body().text());
    }


    @Test
    public void lLoadsGzipFile() throws IOException {
        File in = ParseTest.getFile("/htmltests/gzip.html.gz");
        Document doc = Jsoup.parse(in, null);
        Assert.assertEquals("Gzip test", doc.title());
        Assert.assertEquals("This is a gzipped HTML file.", doc.selectFirst("p").text());
    }

    @Test
    public void loadsZGzipFile() throws IOException {
        // compressed on win, with z suffix
        File in = ParseTest.getFile("/htmltests/gzip.html.z");
        Document doc = Jsoup.parse(in, null);
        Assert.assertEquals("Gzip test", doc.title());
        Assert.assertEquals("This is a gzipped HTML file.", doc.selectFirst("p").text());
    }

    @Test
    public void handlesFakeGzipFile() throws IOException {
        File in = ParseTest.getFile("/htmltests/fake-gzip.html.gz");
        Document doc = Jsoup.parse(in, null);
        Assert.assertEquals("This is not gzipped", doc.title());
        Assert.assertEquals("And should still be readable.", doc.selectFirst("p").text());
    }
}
