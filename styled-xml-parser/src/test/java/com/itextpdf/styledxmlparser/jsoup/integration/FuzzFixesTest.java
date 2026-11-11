/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.integration;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 Tests fixes for issues raised by the OSS Fuzz project @ https://oss-fuzz.com/testcases?project=jsoup
 */
@Tag("IntegrationTest")
public class FuzzFixesTest extends ExtendedITextTest {

    @Test
    public void blankAbsAttr() {
        // https://github.com/jhy/jsoup/issues/1541
        String html = "b<bodY abs: abs:abs: abs:abs:abs>";
        Document doc = Jsoup.parse(html);
        Assertions.assertNotNull(doc);
    }

    @Test
    public void resetInsertionMode() throws IOException {
        // https://github.com/jhy/jsoup/issues/1538
        File in = ParseTest.getFile("/fuzztests/1538.html"); // lots of escape chars etc.
        Document doc = Jsoup.parse(in, "UTF-8");
        Assertions.assertNotNull(doc);
    }

    @Test
    public void xmlDeclOverflow() throws IOException {
        // https://github.com/jhy/jsoup/issues/1539
        File in = ParseTest.getFile("/fuzztests/1539.html"); // lots of escape chars etc.
        Document doc = Jsoup.parse(in, "UTF-8");
        Assertions.assertNotNull(doc);

        Document docXml = Jsoup.parse(FileUtil.getInputStreamForFile(in), "UTF-8", "https://example.com", Parser.xmlParser());
        Assertions.assertNotNull(docXml);
    }

    @Test
    public void xmlDeclOverflowOOM() throws IOException {
        // https://github.com/jhy/jsoup/issues/1569
        File in = ParseTest.getFile("/fuzztests/1569.html");
        Document doc = Jsoup.parse(in, "UTF-8");
        Assertions.assertNotNull(doc);

        Document docXml = Jsoup.parse(FileUtil.getInputStreamForFile(in), "UTF-8", "https://example.com", Parser.xmlParser());
        Assertions.assertNotNull(docXml);
    }

    @Test
    public void stackOverflowState14() throws IOException {
        // https://github.com/jhy/jsoup/issues/1543
        File in = ParseTest.getFile("/fuzztests/1543.html");
        Document doc = Jsoup.parse(in, "UTF-8");
        Assertions.assertNotNull(doc);
    }

    @Test
    public void parseTimeout() throws IOException {
        // https://github.com/jhy/jsoup/issues/1544
        File in = ParseTest.getFile("/fuzztests/1544.html");
        Document doc = Jsoup.parse(in, "UTF-8");
        Assertions.assertNotNull(doc);
    }
}
