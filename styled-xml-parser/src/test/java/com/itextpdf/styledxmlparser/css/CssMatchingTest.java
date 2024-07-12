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
package com.itextpdf.styledxmlparser.css;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.styledxmlparser.IXmlParser;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.parse.CssStyleSheetParser;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupHtmlParser;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssMatchingTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/styledxmlparser/css/CssMatchingTest/";

    @BeforeClass
    public static void beforeClass() {
    }

    @Test
    public void test01() throws IOException {
        String htmlFileName = sourceFolder + "html01.html";
        String cssFileName = sourceFolder + "css01.css";
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode document = htmlParser.parse(FileUtil.getInputStreamForFile(htmlFileName), "UTF-8");
        CssStyleSheet css = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFileName));
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        IElementNode element = new JsoupElementNode(((JsoupDocumentNode)document).getDocument().getElementsByTag("p").first());
        List<CssDeclaration> declarations = css.getCssDeclarations(element, deviceDescription);
        Assert.assertEquals(1, declarations.size());
        Assert.assertEquals("font-weight: bold", declarations.get(0).toString());
    }

    @Test
    public void test02() throws IOException {
        String htmlFileName = sourceFolder + "html02.html";
        String cssFileName = sourceFolder + "css02.css";
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode document = htmlParser.parse(FileUtil.getInputStreamForFile(htmlFileName), "UTF-8");
        CssStyleSheet css = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFileName));
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        IElementNode element = new JsoupElementNode(((JsoupDocumentNode)document).getDocument().getElementsByTag("p").first());
        List<CssDeclaration> declarations = css.getCssDeclarations(element, deviceDescription);
        Assert.assertEquals(2, declarations.size());
        Assert.assertEquals("font-weight: bold", declarations.get(1).toString());
        Assert.assertEquals("color: red", declarations.get(0).toString());
    }

    @Test
    public void test03() throws IOException {
        String htmlFileName = sourceFolder + "html03.html";
        String cssFileName = sourceFolder + "css03.css";
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode document = htmlParser.parse(FileUtil.getInputStreamForFile(htmlFileName), "UTF-8");
        CssStyleSheet css = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFileName));
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        IElementNode element = new JsoupElementNode(((JsoupDocumentNode)document).getDocument().getElementsByTag("p").first());
        List<CssDeclaration> declarations = css.getCssDeclarations(element, deviceDescription);
        Assert.assertEquals(2, declarations.size());
        Assert.assertEquals("font-weight: bold", declarations.get(0).toString());
        Assert.assertEquals("color: black", declarations.get(1).toString());
    }

    @Test
    public void test04() throws IOException {
        String htmlFileName = sourceFolder + "html04.html";
        String cssFileName = sourceFolder + "css04.css";
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode document = htmlParser.parse(FileUtil.getInputStreamForFile(htmlFileName), "UTF-8");
        CssStyleSheet css = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFileName));
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        IElementNode element = new JsoupElementNode(((JsoupDocumentNode)document).getDocument().getElementsByTag("p").first());
        List<CssDeclaration> declarations = css.getCssDeclarations(element, deviceDescription);
        Assert.assertEquals(1, declarations.size());
        Assert.assertEquals("font-size: 100px", declarations.get(0).toString());
    }

    @Test
    public void test05() throws IOException {
        String htmlFileName = sourceFolder + "html05.html";
        String cssFileName = sourceFolder + "css05.css";
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode document = htmlParser.parse(FileUtil.getInputStreamForFile(htmlFileName), "UTF-8");
        CssStyleSheet css = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFileName));
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        IElementNode element = new JsoupElementNode(((JsoupDocumentNode)document).getDocument().getElementsByTag("p").first());
        List<CssDeclaration> declarations = css.getCssDeclarations(element, deviceDescription);
        Assert.assertEquals(1, declarations.size());
        Assert.assertEquals("color: red", declarations.get(0).toString());
    }

    @Test
    public void test06() throws IOException {
        String htmlFileName = sourceFolder + "html06.html";
        String cssFileName = sourceFolder + "css06.css";
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode document = htmlParser.parse(FileUtil.getInputStreamForFile(htmlFileName), "UTF-8");
        CssStyleSheet css = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(cssFileName));
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        IElementNode element = new JsoupElementNode(((JsoupDocumentNode)document).getDocument().getElementsByTag("p").first());
        List<CssDeclaration> declarations = css.getCssDeclarations(element, deviceDescription);
        Assert.assertEquals(1, declarations.size());
        Assert.assertEquals("color: blue", declarations.get(0).toString());
    }

}
