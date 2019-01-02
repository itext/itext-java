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
package com.itextpdf.svg.css;

import com.itextpdf.styledxmlparser.css.CssFontFaceRule;
import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.styledxmlparser.css.resolve.AbstractCssContext;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attribute;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.TextNode;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupTextNode;
import com.itextpdf.svg.css.impl.SvgStyleResolver;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgProcessorContext;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SvgStyleResolverTest extends SvgIntegrationTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/css/SvgStyleResolver/";
    private static final String destinationFolder = "./target/test/com/itextpdf/svg/css/SvgStyleResolver/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    //Single element test
    //Inherits values from parent?
    //Calculates values from parent
    @Test
    public void SvgCssResolverBasicAttributeTest() {

        Element jsoupCircle = new Element(Tag.valueOf("circle"), "");
        Attributes circleAttributes = jsoupCircle.attributes();
        circleAttributes.put(new Attribute("id", "circle1"));
        circleAttributes.put(new Attribute("cx", "95"));
        circleAttributes.put(new Attribute("cy", "95"));
        circleAttributes.put(new Attribute("rx", "53"));
        circleAttributes.put(new Attribute("ry", "53"));
        circleAttributes.put(new Attribute("style", "stroke-width:1.5;stroke:#da0000;"));

        AbstractCssContext cssContext = new SvgCssContext();

        INode circle = new JsoupElementNode(jsoupCircle);
        SvgProcessorContext context = new SvgProcessorContext(new SvgConverterProperties());
        ICssResolver resolver = new SvgStyleResolver(circle, context);
        Map<String, String> actual = resolver.resolveStyles(circle, cssContext);
        Map<String, String> expected = new HashMap<>();
        expected.put("id", "circle1");
        expected.put("cx", "95");
        expected.put("cy", "95");
        expected.put("rx", "53");
        expected.put("ry", "53");
        expected.put("stroke-width", "1.5");
        expected.put("stroke", "#da0000");


        Assert.assertEquals(expected, actual);
    }

    @Test
    public void SvgCssResolverStyleTagTest() {
        Element styleTag = new Element(Tag.valueOf("style"), "");
        TextNode styleContents = new TextNode("\n" +
                "\tellipse{\n" +
                "\t\tstroke-width:1.76388889;\n" +
                "\t\tstroke:#da0000;\n" +
                "\t\tstroke-opacity:1;\n" +
                "\t}\n" +
                "  ", "");
        JsoupElementNode jSoupStyle = new JsoupElementNode(styleTag);
        jSoupStyle.addChild(new JsoupTextNode(styleContents));
        Element ellipse = new Element(Tag.valueOf("ellipse"), "");
        JsoupElementNode jSoupEllipse = new JsoupElementNode(ellipse);
        SvgProcessorContext context = new SvgProcessorContext(new SvgConverterProperties());

        SvgStyleResolver resolver = new SvgStyleResolver(jSoupStyle, context);
        AbstractCssContext svgContext = new SvgCssContext();
        Map<String, String> actual = resolver.resolveStyles(jSoupEllipse, svgContext);

        Map<String, String> expected = new HashMap<>();
        expected.put("stroke-width", "1.76388889");
        expected.put("stroke", "#da0000");
        expected.put("stroke-opacity", "1");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void fontsResolverTagTest() {
        Element styleTag = new Element(Tag.valueOf("style"), "");
        TextNode styleContents = new TextNode("\n" +
                "\t@font-face{\n" +
                "\t\tfont-family:Courier;\n" +
                "\t\tsrc:url(#Super Sans);\n" +
                "\t}\n" +
                "  ", "");
        JsoupElementNode jSoupStyle = new JsoupElementNode(styleTag);
        jSoupStyle.addChild(new JsoupTextNode(styleContents));
        SvgProcessorContext context = new SvgProcessorContext(new SvgConverterProperties());
        SvgStyleResolver resolver = new SvgStyleResolver(jSoupStyle, context);
        List<CssFontFaceRule> fontFaceRuleList = resolver.getFonts();
        Assert.assertEquals(1, fontFaceRuleList.size());
        Assert.assertEquals(2, fontFaceRuleList.get(0).getProperties().size());
    }

    @Test
    //TODO DEVSIX-2058
    public void fontResolverIntegrationTest() throws com.itextpdf.io.IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "fontssvg");
    }

    @Test
    public void validLocalFontTest() throws com.itextpdf.io.IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "validLocalFontTest");
    }

    @Test
    public void fontWeightTest() throws com.itextpdf.io.IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "fontWeightTest");
    }

    /**
     * The following test should fail when RND-1042 is resolved
     */
    @Test
    public void googleFontsTest() throws com.itextpdf.io.IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "googleFontsTest");
    }

}
