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
import com.itextpdf.styledxmlparser.css.CssImportAtRule;
import com.itextpdf.styledxmlparser.css.CssRuleSet;
import com.itextpdf.styledxmlparser.css.CssSemicolonAtRule;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class CssStyleSheetParserTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/styledxmlparser/css/parse/CssStyleSheetParserTest/";

    @Test
    public void test01() throws IOException {
        String cssFile = sourceFolder + "css01.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assertions.assertEquals(getCssFileContents(cssFile), styleSheet.toString());
    }

    @Test
    public void test02() throws IOException {
        String cssFile = sourceFolder + "css02.css";
        String cmpFile = sourceFolder + "cmp_css02.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assertions.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test03() throws IOException {
        String cssFile = sourceFolder + "css03.css";
        String cmpFile = sourceFolder + "cmp_css03.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assertions.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test04() throws IOException {
        String cssFile = sourceFolder + "css04.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assertions.assertEquals("", styleSheet.toString());
    }

    @Test
    public void test05() throws IOException {
        String cssFile = sourceFolder + "css05.css";
        String cmpFile = sourceFolder + "cmp_css05.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assertions.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test06() throws IOException {
        String cssFile = sourceFolder + "css06.css";
        String cmpFile = sourceFolder + "cmp_css06.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assertions.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, count = 6, logLevel = LogLevelConstants.ERROR)
    })
    public void test07() throws IOException {
        String cssFile = sourceFolder + "css07.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile), sourceFolder);
        Assertions.assertEquals(".myclass {\n    font-size: 10pt\n}", styleSheet.toString());
    }

    @Test
    public void test08() throws IOException {
        String cssFile = sourceFolder + "css08.css";
        String cmpFile = sourceFolder + "cmp_css08.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assertions.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test09() throws IOException {
        String cssFile = sourceFolder + "css09.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assertions.assertEquals(getCssFileContents(cssFile), styleSheet.toString());
    }

    @Test
    public void test10() throws IOException {
        String cssFile = sourceFolder + "css10.css";
        String cmpFile = sourceFolder + "cmp_css10.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assertions.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    // TODO DEVSIX-6364 Fix the body declarations duplication for each pageSelector part
    public void test11() throws IOException {
        String cssFile = sourceFolder + "css11.css";
        String cmpFile = sourceFolder + "cmp_css11.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assertions.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test12() throws IOException {
        String cssFile = sourceFolder + "css12.css";
        String cmpFile = sourceFolder + "cmp_css12.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFile));
        Assertions.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.RULE_IS_NOT_SUPPORTED, logLevel = LogLevelConstants.ERROR)
    })
    public void layerUsingTest1() throws IOException {
        String cssString = "@layer utilities {\n"
                + "           .padding-sm {\n"
                + "             padding: 0.5rem;\n"
                + "           }\n"
                + "         }";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new ByteArrayInputStream(cssString.getBytes()));
        Assertions.assertTrue(styleSheet.getStatements().isEmpty());
    }

    @Test
    public void layerUsingTest2() throws IOException {
        String cssString = "@layer utilities;";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new ByteArrayInputStream(cssString.getBytes()));
        Assertions.assertEquals(1, styleSheet.getStatements().size());
        Assertions.assertTrue(styleSheet.getStatements().get(0) instanceof CssSemicolonAtRule);
    }

    @Test
    public void charsetBeforeImportTest() throws IOException {
        String cssString = "@charset \"UTF-8\";\n"
                + "         @import url(\"css01.css\");";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new ByteArrayInputStream(cssString.getBytes()), sourceFolder);
        Assertions.assertEquals(2, styleSheet.getStatements().size());
        Assertions.assertTrue(styleSheet.getStatements().get(0) instanceof CssSemicolonAtRule);
        Assertions.assertTrue("charset".equals(((CssSemicolonAtRule) styleSheet.getStatements().get(0)).getRuleName()));
        Assertions.assertTrue(styleSheet.getStatements().get(1) instanceof CssRuleSet);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.IMPORT_MUST_COME_BEFORE, logLevel = LogLevelConstants.WARN)
    })
    public void styleBeforeImportTest() throws IOException {
        String cssString = "div {background-color: red;}\n"
                + "         @import url(\"test.css\");";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new ByteArrayInputStream(cssString.getBytes()));
        Assertions.assertEquals(1, styleSheet.getStatements().size());
        Assertions.assertFalse(styleSheet.getStatements().get(0) instanceof CssImportAtRule);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.RULE_IS_NOT_SUPPORTED, logLevel = LogLevelConstants.ERROR)
    })
    public void layerBeforeImportTest1() throws IOException {
        String cssString = "@layer utilities {\n"
                + "           .padding-sm {\n"
                + "             padding: 0.5rem;\n"
                + "           }\n"
                + "         }\n"
                + "         @import url(\"css01.css\");";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new ByteArrayInputStream(cssString.getBytes()), sourceFolder);
        Assertions.assertEquals(1, styleSheet.getStatements().size());
        Assertions.assertTrue(styleSheet.getStatements().get(0) instanceof CssRuleSet);
    }

    @Test
    public void layerBeforeImportTest2() throws IOException {
        String cssString = "@layer utilities;"
                + "         @import url(\"css01.css\");";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new ByteArrayInputStream(cssString.getBytes()), sourceFolder);
        Assertions.assertEquals(2, styleSheet.getStatements().size());
        Assertions.assertTrue(styleSheet.getStatements().get(0) instanceof CssSemicolonAtRule);
        Assertions.assertTrue("layer".equals(((CssSemicolonAtRule) styleSheet.getStatements().get(0)).getRuleName()));
        Assertions.assertTrue(styleSheet.getStatements().get(1) instanceof CssRuleSet);
    }

    @Test
    public void importBeforeImportTest() throws IOException {
        String cssString = "@import url(\"css01.css\");\n"
                + "         @import url(\"css09.css\");";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new ByteArrayInputStream(cssString.getBytes()), sourceFolder);
        Assertions.assertEquals(2, styleSheet.getStatements().size());
        Assertions.assertTrue(styleSheet.getStatements().get(0) instanceof CssRuleSet);
        Assertions.assertTrue(styleSheet.getStatements().get(1) instanceof CssRuleSet);
    }

    @Test
    public void importWithoutSemicolonTest() throws IOException {
        String cssString = "@import url(\"css01.css\")";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new ByteArrayInputStream(cssString.getBytes()), sourceFolder);
        Assertions.assertTrue(styleSheet.getStatements().isEmpty());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.IMPORT_RULE_URL_CAN_NOT_BE_RESOLVED, logLevel = LogLevelConstants.ERROR)
    })
    public void importWithoutBaseUrlTest() throws IOException {
        String cssString = "@import url(\"css01.css\");";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new ByteArrayInputStream(cssString.getBytes()));
        Assertions.assertTrue(styleSheet.getStatements().isEmpty());
    }

    @Test
    public void backgroundWithSvgDataTest() throws IOException {
        String declaration = "background: url('data:image/svg+xml;utf8,"
                + "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"300\" height=\"200\"> "
                +       "<polygon points=\"100,10 40,198 190,78 10,78 160,198\" style=\"fill:lime;stroke:purple;stroke-width:5;fill-rule:evenodd;\" />"
                + "</svg>');";

        String cssString = "dif {" + declaration + "}";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new ByteArrayInputStream(cssString.getBytes()), sourceFolder);
        Assertions.assertEquals(1, styleSheet.getStatements().size());
        // When we parse URL using CssDeclarationValueTokenizer, we lost `;` at the end of declaration
        Assertions.assertTrue(styleSheet.getStatements().get(0).toString().contains(declaration.substring(0, declaration.length() - 1)));
    }

    private String getCssFileContents(String filePath) throws IOException {
        byte[] bytes = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile(filePath));
        String content = new String(bytes);
        content = content.trim();
        content = content.replace("\r\n", "\n");
        return content;
    }
}
