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
package com.itextpdf.styledxmlparser.jsoup.integration;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 Tests fixes for issues raised by the OSS Fuzz project @ https://oss-fuzz.com/testcases?project=jsoup
 */
@Category(IntegrationTest.class)
public class FuzzFixesTest extends ExtendedITextTest {

    @Test
    public void blankAbsAttr() {
        // https://github.com/jhy/jsoup/issues/1541
        String html = "b<bodY abs: abs:abs: abs:abs:abs>";
        Document doc = Jsoup.parse(html);
        Assert.assertNotNull(doc);
    }

    @Test
    public void resetInsertionMode() throws IOException {
        // https://github.com/jhy/jsoup/issues/1538
        File in = ParseTest.getFile("/fuzztests/1538.html"); // lots of escape chars etc.
        Document doc = Jsoup.parse(in, "UTF-8");
        Assert.assertNotNull(doc);
    }

    @Test
    public void xmlDeclOverflow() throws IOException {
        // https://github.com/jhy/jsoup/issues/1539
        File in = ParseTest.getFile("/fuzztests/1539.html"); // lots of escape chars etc.
        Document doc = Jsoup.parse(in, "UTF-8");
        Assert.assertNotNull(doc);

        Document docXml = Jsoup.parse(FileUtil.getInputStreamForFile(in), "UTF-8", "https://example.com", Parser.xmlParser());
        Assert.assertNotNull(docXml);
    }

    @Test
    public void xmlDeclOverflowOOM() throws IOException {
        // https://github.com/jhy/jsoup/issues/1569
        File in = ParseTest.getFile("/fuzztests/1569.html");
        Document doc = Jsoup.parse(in, "UTF-8");
        Assert.assertNotNull(doc);

        Document docXml = Jsoup.parse(FileUtil.getInputStreamForFile(in), "UTF-8", "https://example.com", Parser.xmlParser());
        Assert.assertNotNull(docXml);
    }

    @Test
    public void stackOverflowState14() throws IOException {
        // https://github.com/jhy/jsoup/issues/1543
        File in = ParseTest.getFile("/fuzztests/1543.html");
        Document doc = Jsoup.parse(in, "UTF-8");
        Assert.assertNotNull(doc);
    }

    @Test
    public void parseTimeout() throws IOException {
        // https://github.com/jhy/jsoup/issues/1544
        File in = ParseTest.getFile("/fuzztests/1544.html");
        Document doc = Jsoup.parse(in, "UTF-8");
        Assert.assertNotNull(doc);
    }
}
