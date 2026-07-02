/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.helper;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.integration.ParseTest;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Tag("UnitTest")
public class DataUtilTest extends ExtendedITextTest {
    @Test
    public void testCharset() {
        Assertions.assertEquals("utf-8", DataUtil.getCharsetFromContentType("text/html;charset=utf-8 "));
        Assertions.assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=UTF-8"));
        Assertions.assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html; charset=ISO-8859-1"));
        Assertions.assertNull(DataUtil.getCharsetFromContentType("text/html"));
        Assertions.assertNull(DataUtil.getCharsetFromContentType(null));
        Assertions.assertNull(DataUtil.getCharsetFromContentType("text/html;charset=Unknown"));
    }

    @Test
    public void testQuotedCharset() {
        Assertions.assertEquals("utf-8", DataUtil.getCharsetFromContentType("text/html; charset=\"utf-8\""));
        Assertions.assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html;charset=\"UTF-8\""));
        Assertions.assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html; charset=\"ISO-8859-1\""));
        Assertions.assertNull(DataUtil.getCharsetFromContentType("text/html; charset=\"Unsupported\""));
        Assertions.assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset='UTF-8'"));
    }

    private InputStream stream(String data) {
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }

    private InputStream stream(String data, String charset) {
        try {
            return new ByteArrayInputStream(data.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            Assertions.fail();
        }
        return null;
    }

    @Test
    public void discardsSpuriousByteOrderMark() throws IOException {
        String html = "\uFEFF<html><head><title>One</title></head><body>Two</body></html>";
        Document doc = DataUtil.parseInputStream(stream(html), "UTF-8", "http://foo.com/", Parser.htmlParser());
        Assertions.assertEquals("One", doc.head().text());
    }

    @Test
    public void discardsSpuriousByteOrderMarkWhenNoCharsetSet() throws IOException {
        String html = "\uFEFF<html><head><title>One</title></head><body>Two</body></html>";
        Document doc = DataUtil.parseInputStream(stream(html), null, "http://foo.com/", Parser.htmlParser());
        Assertions.assertEquals("One", doc.head().text());
        Assertions.assertEquals("UTF-8", doc.outputSettings().charset().displayName());
    }

    @Test
    public void shouldNotThrowExceptionOnEmptyCharset() {
        Assertions.assertNull(DataUtil.getCharsetFromContentType("text/html; charset="));
        Assertions.assertNull(DataUtil.getCharsetFromContentType("text/html; charset=;"));
    }

    @Test
    public void shouldSelectFirstCharsetOnWeirdMultileCharsetsInMetaTags() {
        Assertions.assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html; charset=ISO-8859-1, charset=1251"));
    }

    @Test
    public void shouldCorrectCharsetForDuplicateCharsetString() {
        Assertions.assertEquals("iso-8859-1", DataUtil.getCharsetFromContentType("text/html; charset=charset=iso-8859-1"));
    }

    @Test
    public void shouldReturnNullForIllegalCharsetNames() {
        Assertions.assertNull(DataUtil.getCharsetFromContentType("text/html; charset=$HJKDF§$/("));
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

        Assertions.assertEquals(expected, doc.toString());
    }

    @Test
    public void secondMetaElementWithContentTypeContainsCharsetParameter() throws Exception {
        String html = "<html><head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html\">" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" +
                "</head><body>한국어</body></html>";

        Document doc = DataUtil.parseInputStream(stream(html, "euc-kr"), null, "http://example.com", Parser.htmlParser());

        Assertions.assertEquals("한국어", doc.body().text());
    }

    @Test
    public void firstMetaElementWithCharsetShouldBeUsedForDecoding() throws Exception {
        String html = "<html><head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=koi8-u\">" +
                "</head><body>Übergrößenträger</body></html>";

        Document doc = DataUtil.parseInputStream(stream(html, "iso-8859-1"), null, "http://example.com", Parser.htmlParser());

        Assertions.assertEquals("Übergrößenträger", doc.body().text());
    }

    @Test
    public void supportsBOMinFiles() throws IOException {
        // test files from http://www.i18nl10n.com/korean/utftest/
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        Assertions.assertTrue(doc.title().contains("UTF-16BE"));
        Assertions.assertTrue(doc.text().contains("가각갂갃간갅"));

        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        Assertions.assertTrue(doc.title().contains("UTF-16LE"));
        Assertions.assertTrue(doc.text().contains("가각갂갃간갅"));

        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        Assertions.assertTrue(doc.title().contains("UTF-32BE"));
        Assertions.assertTrue(doc.text().contains("가각갂갃간갅"));

        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        Assertions.assertTrue(doc.title().contains("UTF-32LE"));
        Assertions.assertTrue(doc.text().contains("가각갂갃간갅"));
    }

    @Test
    public void supportsUTF8BOM() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf8.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        Assertions.assertEquals("OK", doc.head().select("title").text());
    }

    @Test
    public void noExtraNULLBytes() throws IOException {
    	final byte[] b = "<html><head><meta charset=\"UTF-8\"></head><body><div><u>ü</u>ü</div></body></html>".getBytes("UTF-8");
    	
    	Document doc = Jsoup.parse(new ByteArrayInputStream(b), null, "");
        Assertions.assertFalse( doc.outerHtml().contains("\u0000") );
    }

    @Test
    public void supportsZippedUTF8BOM() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf8.html.gz");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        Assertions.assertEquals("OK", doc.head().select("title").text());
        Assertions.assertEquals("There is a UTF8 BOM at the top (before the XML decl). If not read correctly, will look like a non-joining space.", doc.body().text());
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
        Assertions.assertEquals("Hellö Wörld!", doc.body().text());
    }


    @Test
    public void lLoadsGzipFile() throws IOException {
        File in = ParseTest.getFile("/htmltests/gzip.html.gz");
        Document doc = Jsoup.parse(in, null);
        Assertions.assertEquals("Gzip test", doc.title());
        Assertions.assertEquals("This is a gzipped HTML file.", doc.selectFirst("p").text());
    }

    @Test
    public void loadsZGzipFile() throws IOException {
        File in = ParseTest.getFile("/htmltests/gzip.html.z");
        Document doc = Jsoup.parse(in, null);
        Assertions.assertEquals("Gzip test", doc.title());
        Assertions.assertEquals("This is a gzipped HTML file.", doc.selectFirst("p").text());
    }

    @Test
    public void handlesFakeGzipFile() throws IOException {
        File in = ParseTest.getFile("/htmltests/fake-gzip.html.gz");
        Document doc = Jsoup.parse(in, null);
        Assertions.assertEquals("This is not gzipped", doc.title());
        Assertions.assertEquals("And should still be readable.", doc.selectFirst("p").text());
    }

    @Test
    public void loadWithParserOverloadUsesGivenParser() throws IOException {
        String html = "<html><head><title>One</title></head><body>Two</body></html>";
        Document doc = DataUtil.load(stream(html), "UTF-8", "http://foo.com/", Parser.htmlParser());
        Assertions.assertEquals("One", doc.head().text());
    }

    @Test
    public void parseInputStreamWithNullReturnsEmptyDocument() throws IOException {
        Document doc = DataUtil.parseInputStream(null, "UTF-8", "http://foo.com/", Parser.htmlParser());
        Assertions.assertEquals("http://foo.com/", doc.baseUri());
        Assertions.assertEquals("", doc.text());
    }

    @Test
    public void crossStreamsCopiesAllBytes() throws IOException {
        byte[] data = new byte[1024 * 64 + 7];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 251);
        }
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        DataUtil.crossStreams(in, out);

        Assertions.assertArrayEquals(data, out.toByteArray());
    }

    @Test
    public void shortStreamUnderBomLengthIsHandled() throws IOException {
        Document doc = DataUtil.parseInputStream(stream("ab"), null, "http://foo.com/", Parser.htmlParser());
        Assertions.assertEquals("ab", doc.text());
    }

    @Test
    public void largeCharsetlessStreamTriggersReread() throws IOException {
       StringBuilder sb = new StringBuilder(10000).append("<html><head><title>Big</title></head><body>");
        while (sb.length() < (1024 * 5) + 1024) {
            sb.append("<p>filler filler filler filler filler</p>");
        }
        sb.append("</body></html>");
        Document doc = DataUtil.parseInputStream(stream(sb.toString()), null, "http://foo.com/", Parser.htmlParser());
        Assertions.assertEquals("Big", doc.head().select("title").text());
        Assertions.assertEquals("UTF-8", doc.outputSettings().charset().displayName());
    }

    @Test
    public void xmlDeclarationFirstChildIsUsedForCharset() throws IOException {
       String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><data>Hellö</data>";
        Document doc = DataUtil.parseInputStream(stream(xml, "ISO-8859-1"), null, "http://foo.com/", Parser.xmlParser());
        Assertions.assertEquals("Hellö", doc.text());
    }

    @Test
    public void uncheckedIoExceptionDuringFirstParseIsRethrown() {
       Parser throwingParser = new ThrowingParser("boom-first");
        Exception ex = Assertions.assertThrows(Exception.class,
                () -> DataUtil.parseInputStream(stream("<html></html>"), null, "http://foo.com/", throwingParser));
        Assertions.assertEquals("boom-first", ex.getMessage());
    }

    @Test
    public void uncheckedIoExceptionDuringReaderParseIsRethrown() {
        Parser throwingParser = new ThrowingParser("boom-reader");
        Exception ex = Assertions.assertThrows(Exception.class,
                () -> DataUtil.parseInputStream(stream("<html></html>"), "UTF-8", "http://foo.com/", throwingParser));
        Assertions.assertEquals("boom-reader", ex.getMessage());
    }

    /**
     * A Parser whose parseInput always throws an UncheckedIOException wrapping an IOException,
     * used to exercise DataUtil's UncheckedIOException catch/rethrow paths.
     */
    private static final class ThrowingParser extends Parser {
        private final String message;

        ThrowingParser(String message) {
            super(Parser.htmlParser().getTreeBuilder());
            this.message = message;
        }

        @Override
        public Document parseInput(java.io.Reader inputHtml, String baseUri) {
            throw new com.itextpdf.styledxmlparser.jsoup.UncheckedIOException(new IOException(message));
        }

        @Override
        public Document parseInput(String html, String baseUri) {
            throw new com.itextpdf.styledxmlparser.jsoup.UncheckedIOException(new IOException(message));
        }
    }
}
