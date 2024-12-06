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
package com.itextpdf.svg.css;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class SvgStyleResolverIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/css/SvgStyleResolver/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/css/SvgStyleResolver/";

    @BeforeAll
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
        IBranchSvgNodeRenderer nodeRenderer = (IBranchSvgNodeRenderer) new DefaultSvgProcessor().process(root, null).getRootRenderer();

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

        Assertions.assertEquals(expected, actual);
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
        IBranchSvgNodeRenderer nodeRenderer = (IBranchSvgNodeRenderer) new DefaultSvgProcessor().process(root, null).getRootRenderer();

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

        Assertions.assertEquals(expected, actual);
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
        IBranchSvgNodeRenderer nodeRenderer = (IBranchSvgNodeRenderer) new DefaultSvgProcessor().process(root, null).getRootRenderer();

        PathSvgNodeRenderer pathSvgNodeRenderer = (PathSvgNodeRenderer) ((IBranchSvgNodeRenderer) nodeRenderer.getChildren().get(0)).getChildren().get(0);

        Map<String, String> actual = new HashMap<>();
        actual.put("stroke", pathSvgNodeRenderer.getAttribute("stroke"));
        actual.put("fill", pathSvgNodeRenderer.getAttribute("fill"));
        actual.put("d", pathSvgNodeRenderer.getAttribute("d"));

        Map<String, String> expected = new HashMap<>();
        expected.put("stroke", "black");
        expected.put("fill", "none");
        expected.put("d", "M0 100 L0 50 L70 50");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void fontResolverIntegrationTest() throws com.itextpdf.io.exceptions.IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "fontssvg");
    }

    @Test
    public void validLocalFontTest() throws com.itextpdf.io.exceptions.IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "validLocalFontTest");
    }

    @Test
    public void fontWeightTest() throws com.itextpdf.io.exceptions.IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "fontWeightTest");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG, logLevel = LogLevelConstants.WARN))
    public void externalStyleSheetWithFillStyleTest() throws com.itextpdf.io.exceptions.IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "externalStyleSheetWithFillStyleTest");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG, logLevel = LogLevelConstants.WARN))
    public void externalStyleSheetWithStrokeStyleTest() throws com.itextpdf.io.exceptions.IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "externalStyleSheetWithStrokeStyleTest");
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6459 Android: fix the SecurityException(Permission denied) from UrlUtil method)
    public void googleFontsTest() throws com.itextpdf.io.exceptions.IOException, InterruptedException, java.io.IOException {
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
    public void svgWithInternalCSSWithoutOverlapTest() throws IOException,InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "internalCssNoOverlap");
    }


    @Test
    // TODO: update cmp files when DEVSIX-2286 resolved
    public void svgWithExternalCSStoCustomPage() throws IOException,InterruptedException {
        // Take a note this method differs from the one used in Default Page test
        convertAndCompare(sourceFolder, destinationFolder, "externalCss_custom", PageSize.A3.rotate());
    }

    @Test
    // TODO: update cmp files when DEVSIX-2286 resolved
    public void svgWithInternalCSStoCustomPage() throws IOException,InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "internalCss_custom", PageSize.A3.rotate());
    }

    @Test
    // TODO: update cmp files when DEVSIX-2286 resolved
    public void multipleSVGtagsWithDiffStylesFromExternalCSS() throws IOException,InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "externalCss_palette", PageSize.A3.rotate());
    }

    @Test
    public void relativeStyleInheritanceTest() throws IOException,InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "relativeStyleInheritanceTest");
    }

    @Test
    public void textTagNoFontSizeTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "textTagNoFontSize");
    }

    @Test
    public void chartWithText1Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "chartWithText1");
    }

    @Test
    public void chartWithText2Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "chartWithText2");
    }

    @Test
    public void importStyleSheetWithStrokeStyleTest() throws com.itextpdf.io.exceptions.IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "importStyleSheetWithStrokeStyleTest");
    }

    @Test
    public void styleInCdataTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "styleInCDATA");
    }

    @Test
    public void styleInCdataWithNewLineBeforeTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "styleInCdataWithNewLineBefore");
    }

    @Test
    public void cssStylesResolverOrder1Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder1");
    }

    @Test
    public void cssStylesResolverOrder2Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder2");
    }

    @Test
    public void cssStylesResolverOrder3Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder3");
    }

    @Test
    public void cssStylesResolverOrder4Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder4");
    }

    @Test
    public void cssStylesResolverOrder5Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder5");
    }

    @Test
    public void cssStylesResolverOrder6Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder6");
    }

    @Test
    public void cssStylesResolverOrder7Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder7");
    }

    @Test
    public void cssStylesResolverOrder8Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder8");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG, logLevel = LogLevelConstants.WARN))
    public void cssStylesResolverOrder9Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder9");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG, logLevel = LogLevelConstants.WARN))
    public void cssStylesResolverOrder10Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder10");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG, logLevel = LogLevelConstants.WARN))
    public void cssStylesResolverOrder11Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder11");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG, logLevel = LogLevelConstants.WARN))
    public void cssStylesResolverOrder12Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder12");
    }

    @Test
    public void cssStylesResolverOrder13Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder13");
    }

    @Test
    public void cssStylesResolverOrder14Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder14");
    }

    @Test
    public void cssStylesResolverOrder15Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder15");
    }

    @Test
    public void cssStylesResolverOrder16Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder16");
    }

    @Test
    public void cssStylesResolverOrder17Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "cssStylesResolverOrder17");
    }

    @Test
    //TODO DEVSIX-8823: update after issue is fixed
    public void heightWidthSvgStyleTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "height-width-style");
    }
}
