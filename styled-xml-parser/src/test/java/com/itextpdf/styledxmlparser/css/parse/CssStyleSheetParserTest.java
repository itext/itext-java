/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;

@Category(UnitTest.class)
public class CssStyleSheetParserTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/styledxmlparser/css/parse/CssStyleSheetParserTest/";

    @Test
    public void test01() throws IOException {
        String cssFile = sourceFolder + "css01.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(cssFile));
        Assert.assertEquals(getCssFileContents(cssFile), styleSheet.toString());
    }

    @Test
    public void test02() throws IOException {
        String cssFile = sourceFolder + "css02.css";
        String cmpFile = sourceFolder + "cmp_css02.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test03() throws IOException {
        String cssFile = sourceFolder + "css03.css";
        String cmpFile = sourceFolder + "cmp_css03.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test04() throws IOException {
        String cssFile = sourceFolder + "css04.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(cssFile));
        Assert.assertEquals("", styleSheet.toString());
    }

    @Test
    public void test05() throws IOException {
        String cssFile = sourceFolder + "css05.css";
        String cmpFile = sourceFolder + "cmp_css05.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test06() throws IOException {
        String cssFile = sourceFolder + "css06.css";
        String cmpFile = sourceFolder + "cmp_css06.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test07() throws IOException {
        String cssFile = sourceFolder + "css07.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(cssFile));
        Assert.assertEquals(getCssFileContents(cssFile), styleSheet.toString());
    }

    @Test
    public void test08() throws IOException {
        String cssFile = sourceFolder + "css08.css";
        String cmpFile = sourceFolder + "cmp_css08.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test09() throws IOException {
        String cssFile = sourceFolder + "css09.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(cssFile));
        Assert.assertEquals(getCssFileContents(cssFile), styleSheet.toString());
    }

    @Test
    public void test10() throws IOException {
        String cssFile = sourceFolder + "css10.css";
        String cmpFile = sourceFolder + "cmp_css10.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test11() throws IOException {
        // TODO in this test declarations of the page at-rule with compound selector are duplicated.
        // See CssPageRule#addBodyCssDeclarations() method for the reason and possible solution if this becomes important.

        String cssFile = sourceFolder + "css11.css";
        String cmpFile = sourceFolder + "cmp_css11.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    @Test
    public void test12() throws IOException {
        String cssFile = sourceFolder + "css12.css";
        String cmpFile = sourceFolder + "cmp_css12.css";
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(cssFile));
        Assert.assertEquals(getCssFileContents(cmpFile), styleSheet.toString());
    }

    private String getCssFileContents(String filePath) throws IOException {
        byte[] bytes = StreamUtil.inputStreamToArray(new FileInputStream(filePath));
        String content = new String(bytes);
        content = content.trim();
        content = content.replace("\r\n", "\n");
        return content;
    }

}
