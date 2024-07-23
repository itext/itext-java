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
package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssStyleSheetParserTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/styledxmlparser/css/parse/CssStyleSheetParserTest/";

    @Test
    public void test01() throws IOException {
        String cssFile = sourceFolder + "css01.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assert.assertEquals(getCssFileContents(cssFile), styleSheet.toString());
    }

    @Test
    public void test02() throws IOException {
        String cssFile = sourceFolder + "css02.css";
        String cmpFile = sourceFolder + "cmp_css02.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test03() throws IOException {
        String cssFile = sourceFolder + "css03.css";
        String cmpFile = sourceFolder + "cmp_css03.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test04() throws IOException {
        String cssFile = sourceFolder + "css04.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assert.assertEquals("", styleSheet.toString());
    }

    @Test
    public void test05() throws IOException {
        String cssFile = sourceFolder + "css05.css";
        String cmpFile = sourceFolder + "cmp_css05.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test06() throws IOException {
        String cssFile = sourceFolder + "css06.css";
        String cmpFile = sourceFolder + "cmp_css06.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test07() throws IOException {
        String cssFile = sourceFolder + "css07.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assert.assertEquals(getCssFileContents(cssFile), styleSheet.toString());
    }

    @Test
    public void test08() throws IOException {
        String cssFile = sourceFolder + "css08.css";
        String cmpFile = sourceFolder + "cmp_css08.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test09() throws IOException {
        String cssFile = sourceFolder + "css09.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assert.assertEquals(getCssFileContents(cssFile), styleSheet.toString());
    }

    @Test
    public void test10() throws IOException {
        String cssFile = sourceFolder + "css10.css";
        String cmpFile = sourceFolder + "cmp_css10.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    // TODO DEVSIX-6364 Fix the body declarations duplication for each pageSelector part
    public void test11() throws IOException {
        String cssFile = sourceFolder + "css11.css";
        String cmpFile = sourceFolder + "cmp_css11.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test12() throws IOException {
        String cssFile = sourceFolder + "css12.css";
        String cmpFile = sourceFolder + "cmp_css12.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    private String getCssFileContents(String filePath) throws IOException {
        byte[] bytes = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile(filePath));
        String content = new String(bytes);
        content = content.trim();
        content = content.replace("\r\n", "\n");
        return content;
    }

}
