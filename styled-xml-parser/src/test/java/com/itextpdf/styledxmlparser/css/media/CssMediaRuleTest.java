/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.styledxmlparser.css.media;

import com.itextpdf.styledxmlparser.IXmlParser;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.CssRuleSet;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
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

public class CssMediaRuleTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/styledxmlparser/css/media/MediaRuleTest/";

    @BeforeClass
    public static void beforeClass() {
    }


    @Test
    public void test01() throws IOException {
        String htmlFileName = sourceFolder + "html01.html";
        String cssFileName = sourceFolder + "css01.css";
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode document = htmlParser.parse(new FileInputStream(htmlFileName), "UTF-8");
        CssStyleSheet css = CssStyleSheetParser.parse(new FileInputStream(cssFileName));
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription(MediaType.PRINT);
        IElementNode element = new JsoupElementNode(((JsoupDocumentNode) document).getDocument().getElementsByTag("p").first());
        List<CssDeclaration> declarations = css.getCssDeclarations(element, deviceDescription);
        Assert.assertEquals(3, declarations.size());
        Assert.assertEquals("font-weight: bold", declarations.get(0).toString());
        Assert.assertEquals("color: red", declarations.get(1).toString());
        Assert.assertEquals("font-size: 20pt", declarations.get(2).toString());
    }

    @Test
    public void test02() throws IOException {
        String htmlFileName = sourceFolder + "html02.html";
        String cssFileName = sourceFolder + "css02.css";
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode document = htmlParser.parse(new FileInputStream(htmlFileName), "UTF-8");
        CssStyleSheet css = CssStyleSheetParser.parse(new FileInputStream(cssFileName));
        IElementNode element = new JsoupElementNode(((JsoupDocumentNode) document).getDocument().getElementsByTag("p").first());

        MediaDeviceDescription deviceDescription1 = new MediaDeviceDescription(MediaType.PRINT);
        deviceDescription1.setWidth(525);

        MediaDeviceDescription deviceDescription2 = new MediaDeviceDescription(MediaType.HANDHELD);
        deviceDescription2.setOrientation("landscape");

        List<CssDeclaration> declarations1 = css.getCssDeclarations(element, deviceDescription1);
        List<CssDeclaration> declarations2 = css.getCssDeclarations(element, deviceDescription2);

        Assert.assertTrue(declarations1.equals(declarations2));

        Assert.assertEquals(1, declarations1.size());
        Assert.assertEquals("font-weight: bold", declarations1.get(0).toString());
    }

    @Test
    public void test03() throws IOException {
        String htmlFileName = sourceFolder + "html03.html";
        String cssFileName = sourceFolder + "css03.css";
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode document = htmlParser.parse(new FileInputStream(htmlFileName), "UTF-8");
        CssStyleSheet css = CssStyleSheetParser.parse(new FileInputStream(cssFileName));
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription(MediaType.PRINT);
        deviceDescription.setResolution(300);
        IElementNode element = new JsoupElementNode(((JsoupDocumentNode) document).getDocument().getElementsByTag("p").first());
        List<CssDeclaration> declarations = css.getCssDeclarations(element, deviceDescription);
        Assert.assertEquals(1, declarations.size());
        Assert.assertEquals("color: black", declarations.get(0).toString());
    }

    @Test
    public void test04() throws IOException {
        String htmlFileName = sourceFolder + "html04.html";
        String cssFileName = sourceFolder + "css04.css";
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode document = htmlParser.parse(new FileInputStream(htmlFileName), "UTF-8");
        CssStyleSheet css = CssStyleSheetParser.parse(new FileInputStream(cssFileName));

        MediaDeviceDescription deviceDescription = new MediaDeviceDescription(MediaType.PRINT).setColorIndex(256);

        IElementNode element = new JsoupElementNode(((JsoupDocumentNode) document).getDocument().getElementsByTag("p").first());
        List<CssDeclaration> declarations = css.getCssDeclarations(element, deviceDescription);
        Assert.assertEquals(2, declarations.size());
        Assert.assertEquals("color: red", declarations.get(0).toString());
        Assert.assertEquals("font-size: 20em", declarations.get(1).toString());
    }

    @Test
    public void test05() throws IOException {
        String htmlFileName = sourceFolder + "html05.html";
        String cssFileName = sourceFolder + "css05.css";
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode document = htmlParser.parse(new FileInputStream(htmlFileName), "UTF-8");
        CssStyleSheet css = CssStyleSheetParser.parse(new FileInputStream(cssFileName));
        IElementNode element = new JsoupElementNode(((JsoupDocumentNode) document).getDocument().getElementsByTag("p").first());

        MediaDeviceDescription deviceDescription1 = new MediaDeviceDescription(MediaType.PRINT).
                setWidth(300).setHeight(301);

        MediaDeviceDescription deviceDescription2 = new MediaDeviceDescription(MediaType.SCREEN).
                setWidth(400).setHeight(400);

        List<CssDeclaration> declarations1 = css.getCssDeclarations(element, deviceDescription1);
        List<CssDeclaration> declarations2 = css.getCssDeclarations(element, deviceDescription2);

        Assert.assertEquals(0, declarations1.size());

        Assert.assertEquals(1, declarations2.size());
        Assert.assertEquals("color: red", declarations2.get(0).toString());
    }

    @Test
    public void test06() throws IOException {
        String htmlFileName = sourceFolder + "html06.html";
        String cssFileName = sourceFolder + "css06.css";
        IXmlParser htmlParser = new JsoupHtmlParser();
        IDocumentNode document = htmlParser.parse(new FileInputStream(htmlFileName), "UTF-8");
        CssStyleSheet css = CssStyleSheetParser.parse(new FileInputStream(cssFileName));
        IElementNode element = new JsoupElementNode(((JsoupDocumentNode) document).getDocument().getElementsByTag("p").first());

        MediaDeviceDescription deviceDescription1 = new MediaDeviceDescription(MediaType.PRINT).
                setBitsPerComponent(2);

        MediaDeviceDescription deviceDescription2 = new MediaDeviceDescription(MediaType.HANDHELD).
                setBitsPerComponent(2);

        MediaDeviceDescription deviceDescription3 = new MediaDeviceDescription(MediaType.SCREEN).
                setBitsPerComponent(1);

        List<CssDeclaration> declarations1 = css.getCssDeclarations(element, deviceDescription1);
        List<CssDeclaration> declarations2 = css.getCssDeclarations(element, deviceDescription2);
        List<CssDeclaration> declarations3 = css.getCssDeclarations(element, deviceDescription3);

        Assert.assertTrue(declarations1.equals(declarations2));
        Assert.assertEquals(0, declarations3.size());

        Assert.assertEquals(1, declarations1.size());
        Assert.assertEquals("color: red", declarations1.get(0).toString());
    }


    @Test
    public void matchMediaDeviceTest() {
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        deviceDescription.setHeight(450);
        deviceDescription.setWidth(600);
        CssMediaRule rule = new CssMediaRule("@media all and (min-width: 600px) and (min-height: 600px)");
        Assert.assertTrue(rule.matchMediaDevice(deviceDescription));
    }

    @Test
    public void getCssRuleSetsTest() {
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        String html = "<a id=\"123\" class=\"baz = 'bar'\" style = media= all and (min-width: 600px) />";
        IDocumentNode node = new JsoupHtmlParser().parse(html);
        List<CssRuleSet> ruleSets = new CssMediaRule("only all and (min-width: 600px) and (min-height: 600px)").getCssRuleSets(node, deviceDescription);
        Assert.assertNotNull(ruleSets);
    }
}
