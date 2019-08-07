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


import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Category(IntegrationTest.class)
public class SvgStyleResolverIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/css/SvgStyleResolver/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/css/SvgStyleResolver/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }


    @Test
    public void RedCirleTest() {
        String svg = "<svg\n" +
                "   width=\"210mm\"\n" +
                "   height=\"297mm\"\n" +
                "   viewBox=\"0 0 210 297\"\n" +
                "   version=\"1.1\"\n" +
                "  <title id=\"title4508\">Red Circle</title>\n" +
                "    <ellipse\n" +
                "       id=\"path3699\"\n" +
                "       cx=\"96.005951\"\n" +
                "       cy=\"110.65774\"\n" +
                "       rx=\"53.672619\"\n" +
                "       ry=\"53.294643\"\n" +
                "       style=\"stroke-width:1.76388889;stroke:#da0000;stroke-opacity:1;fill:none;stroke-miterlimit:4;stroke-dasharray:none\" />\n" +
                "</svg>\n";
        JsoupXmlParser xmlParser = new JsoupXmlParser();
        IDocumentNode root = xmlParser.parse(svg);
        IBranchSvgNodeRenderer nodeRenderer = (IBranchSvgNodeRenderer) new DefaultSvgProcessor().process(root).getRootRenderer();

        Map<String, String> actual = new HashMap<>();
        //Traverse to ellipse
        ISvgNodeRenderer ellipse = nodeRenderer.getChildren().get(0);
        actual.put("stroke", ellipse.getAttribute("stroke"));
        actual.put("stroke-width", ellipse.getAttribute("stroke-width"));
        actual.put("stroke-opacity", ellipse.getAttribute("stroke-opacity"));

        Map<String, String> expected = new HashMap<>();
        expected.put("stroke-width", "1.76388889");
        expected.put("stroke", "#da0000");
        expected.put("stroke-opacity", "1");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void styleTagProcessingTest() {
        String svg = "<svg\n" +
                "   width=\"210mm\"\n" +
                "   height=\"297mm\"\n" +
                "   viewBox=\"0 0 210 297\"\n" +
                "   version=\"1.1\"\n" +
                "   id=\"svg8\"\n" +
                "   >\n" +
                "  <style>\n" +
                "\tellipse{\n" +
                "\t\tstroke-width:1.76388889;\n" +
                "\t\tstroke:#da0000;\n" +
                "\t\tstroke-opacity:1;\n" +
                "\t}\n" +
                "  </style>\n" +
                "    <ellipse\n" +
                "       id=\"path3699\"\n" +
                "       cx=\"96.005951\"\n" +
                "       cy=\"110.65774\"\n" +
                "       rx=\"53.672619\"\n" +
                "       ry=\"53.294643\"\n" +
                "       style=\"fill:none;stroke-miterlimit:4;stroke-dasharray:none\" />\n" +
                "</svg>\n";
        JsoupXmlParser xmlParser = new JsoupXmlParser();
        IDocumentNode root = xmlParser.parse(svg);
        IBranchSvgNodeRenderer nodeRenderer = (IBranchSvgNodeRenderer) new DefaultSvgProcessor().process(root).getRootRenderer();

        Map<String, String> actual = new HashMap<>();
        //Traverse to ellipse
        ISvgNodeRenderer ellipse = nodeRenderer.getChildren().get(0);
        actual.put("stroke", ellipse.getAttribute("stroke"));
        actual.put("stroke-width", ellipse.getAttribute("stroke-width"));
        actual.put("stroke-opacity", ellipse.getAttribute("stroke-opacity"));

        Map<String, String> expected = new HashMap<>();
        expected.put("stroke-width", "1.76388889");
        expected.put("stroke", "#da0000");
        expected.put("stroke-opacity", "1");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void stylesOfSvgTagProcessingTest() {
        String svg = "<?xml version=\"1.0\" standalone=\"no\"?>\n" +
                "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n" +
                "        \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
                "<svg width=\"400\" height=\"200\"\n" +
                "     viewBox=\"0 0 400 200\" version=\"1.1\"\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\"\n" +
                "     xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
                "     xmlns:v=\"http://schemas.microsoft.com/visio/2003/SVGExtensions/\"\n" +
                "     class=\"st11\">\n" +
                "    <style type=\"text/css\">\n" +
                "        .st11 {fill:none;stroke:black;stroke-width:6}\n" +
                "    </style>\n" +
                "    <g >\n" +
                "        <path d=\"M0 100 L0 50 L70 50\"/>\n" +
                "    </g>\n" +
                "</svg>";
        JsoupXmlParser xmlParser = new JsoupXmlParser();
        IDocumentNode root = xmlParser.parse(svg);
        IBranchSvgNodeRenderer nodeRenderer = (IBranchSvgNodeRenderer) new DefaultSvgProcessor().process(root).getRootRenderer();

        PathSvgNodeRenderer pathSvgNodeRenderer = (PathSvgNodeRenderer) ((IBranchSvgNodeRenderer) nodeRenderer.getChildren().get(0)).getChildren().get(0);

        Map<String, String> actual = new HashMap<>();
        actual.put("stroke", pathSvgNodeRenderer.getAttribute("stroke"));
        actual.put("fill", pathSvgNodeRenderer.getAttribute("fill"));
        actual.put("d", pathSvgNodeRenderer.getAttribute("d"));

        Map<String, String> expected = new HashMap<>();
        expected.put("stroke", "black");
        expected.put("fill", "none");
        expected.put("d", "M0 100 L0 50 L70 50");

        Assert.assertEquals(expected, actual);
    }

    @Test
    //TODO DEVSIX-2058
    public void fontResolverIntegrationTest() throws com.itextpdf.io.IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "fontssvg");
    }

    @Test
    public void validLocalFontTest() throws com.itextpdf.io.IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "validLocalFontTest");
    }

    @Test
    public void fontWeightTest() throws com.itextpdf.io.IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "fontWeightTest");
    }

    @Test
    //TODO DEVSIX-2264: that test shall fail after the fix.
    public void googleFontsTest() throws com.itextpdf.io.IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "googleFontsTest");
    }

    @Test
    // TODO: update cmp files when DEVSIX-2286 resolved
    public void svgWithExternalCSStoSingleDefaultPage() throws IOException,InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "externalCss");
    }

    @Test
    // TODO: update cmp files when DEVSIX-2286 resolved
    public void svgWithInternalCSStoSingleDefaultPage() throws IOException,InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "internalCss");
    }

    @Test
    // TODO: update cmp files when DEVSIX-2286 resolved
    public void svgWithExternalCSStoCustomPage() throws IOException,InterruptedException {
        // Take a note this method differs from the one used in Default Page test
        convertAndCompare(sourceFolder, destinationFolder, "internalCss");
    }

    @Test
    // TODO: update cmp files when DEVSIX-2286 resolved
    public void svgWithInternalCSStoCustomPage() throws IOException,InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "internalCss_custom");
    }

    @Test
    // TODO: update cmp files when DEVSIX-2286 resolved
    public void multipleSVGtagsWithDiffStylesFromExternalCSS() throws IOException,InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "externalCss_palette");
    }
}
