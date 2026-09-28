/*
	This file is part of the iText (R) project.
	Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class ForeignObjectNodeRendererTest extends ExtendedITextTest {

    private static final String SAMPLE = "<foreignObject xmlns='http://www.w3.org/2000/svg' width='230' height='35'"
            + " style='font-size:14px;color:rgb(67,39,135);font-family:Arial;font-weight:700;"
            + "text-align:left;letter-spacing:0em;line-height:1.5' x='64' y='24'>\n"
            + "<div xmlns='http://www.w3.org/1999/xhtml'>#1 Repository Of The Day</div>\n</foreignObject>";

    @Test
    public void sampleBoundsAndStylesTest() {
        ForeignObjectNodeRenderer renderer = processForeignObject(SAMPLE);
        SvgDrawContext context = new SvgDrawContext(null, null);
        Assertions.assertTrue(new Rectangle(48, 18, 172.5f, 26.25f)
                .equalsWithEpsilon(renderer.getObjectBoundingBox(context)));

        Paragraph paragraph = renderer.createParagraph(context);
        Assertions.assertEquals("#1 Repository Of The Day", paragraphText(paragraph));
        Assertions.assertEquals(10.5f, ((UnitValue) paragraph.<UnitValue>getProperty(Property.FONT_SIZE)).getValue());
        Assertions.assertArrayEquals(new String[] {"arial"}, paragraph.<String[]>getProperty(Property.FONT));
        Assertions.assertEquals("700", paragraph.<String>getProperty(Property.FONT_WEIGHT));
        Assertions.assertEquals(new DeviceRgb(67, 39, 135),
                paragraph.<TransparentColor>getProperty(Property.FONT_COLOR).getColor());
        Assertions.assertEquals(15.75f, paragraph.<Leading>getProperty(Property.LEADING).getValue());
    }


    @Test
    public void samplePdfTest() throws IOException {
        Assertions.assertEquals("#1 Repository Of The Day", convertAndExtract(SAMPLE));
    }

    @Test
    public void wrappingTest() throws IOException {
        String text = convertAndExtract("<foreignObject width='65' height='120' style='font-size:12px'>"
                + "<div xmlns='http://www.w3.org/1999/xhtml'>one two three four five six seven</div></foreignObject>");
        Assertions.assertTrue(text.contains("\n"));
        Assertions.assertEquals("one two three four five six seven", text.replaceAll("\\s+", " "));
    }

    @Test
    public void inheritedXhtmlNamespaceAndWhitespaceTest() {
        ForeignObjectNodeRenderer renderer = processForeignObject("<foreignObject width='200' height='50'>"
                + "<div xmlns='http://www.w3.org/1999/xhtml'>one <span>two</span> <b>three</b> &amp; four</div>"
                + "</foreignObject>");
        Assertions.assertEquals("one two three & four",
                paragraphText(renderer.createParagraph(new SvgDrawContext(null, null))));
    }

    @Test
    public void prefixedXhtmlNamespaceTest() {
        ForeignObjectNodeRenderer renderer = processForeignObject("<foreignObject width='200' height='50'"
                + " xmlns:h='http://www.w3.org/1999/xhtml'>"
                + "<h:div>prefixed <h:span>text</h:span><span>ignored</span></h:div></foreignObject>");
        Assertions.assertEquals("prefixed text",
                paragraphText(renderer.createParagraph(new SvgDrawContext(null, null))));
    }

    @Test
    public void namespaceOverridesTest() {
        ForeignObjectNodeRenderer renderer = processForeignObject("<foreignObject width='200' height='50'"
                + " xmlns:h='http://www.w3.org/1999/xhtml'>"
                + "<div xmlns='http://www.w3.org/1999/xhtml'>before "
                + "<span xmlns=''>no namespace</span>"
                + "<text xmlns='http://www.w3.org/2000/svg'>SVG text</text>"
                + "<h:span xmlns:h='urn:other'>other namespace</h:span>"
                + "<h:span xmlns:h=''>empty prefix binding</h:span>"
                + "<h:span>after</h:span></div></foreignObject>");
        Assertions.assertEquals("before after",
                paragraphText(renderer.createParagraph(new SvgDrawContext(null, null))));
    }

    @Test
    public void xhtmlScriptAndStyleContentTest() {
        ForeignObjectNodeRenderer renderer = processForeignObject("<foreignObject width='200' height='50'"
                + " xmlns:h='http://www.w3.org/1999/xhtml'>"
                + "<div xmlns='http://www.w3.org/1999/xhtml'>visible"
                + "<script>hidden</script><style>/* hidden */</style>"
                + "<h:script>hidden</h:script><h:style>hidden</h:style>"
                + "<h:SCRIPT><h:span>hidden</h:span></h:SCRIPT></div></foreignObject>");
        Assertions.assertEquals("visible", paragraphText(renderer.createParagraph(new SvgDrawContext(null, null))));
    }

    @Test
    public void emptyAndNonPositiveBoundsTest() throws IOException {
        Assertions.assertEquals("", convertAndExtract("<foreignObject width='100' height='50'/>"));
        for (String dimensions : new String[] {"", "width='100'", "height='50'", "width='0' height='50'",
                "width='100' height='0'", "width='-1' height='50'", "width='100' height='-1'"}) {
            Assertions.assertEquals("", convertAndExtract("<foreignObject " + dimensions
                    + "><div xmlns='http://www.w3.org/1999/xhtml'>Hidden</div></foreignObject>"));
        }
    }


    private static ForeignObjectNodeRenderer processForeignObject(String content) {
        ISvgProcessorResult result = SvgConverter.process(SvgConverter.parse(svg(content)), null);
        return (ForeignObjectNodeRenderer) ((IBranchSvgNodeRenderer) result.getRootRenderer()).getChildren().get(0);
    }

    private static String paragraphText(Paragraph paragraph) {
        return ((Text) paragraph.getChildren().get(0)).getText();
    }

    private static String svg(String content) {
        return "<svg xmlns='http://www.w3.org/2000/svg' width='400' height='200'>" + content + "</svg>";
    }

    private static String convertAndExtract(String content) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        SvgConverter.createPdf(new ByteArrayInputStream(svg(content).getBytes(StandardCharsets.UTF_8)), output);
        try (PdfDocument document = new PdfDocument(new PdfReader(new ByteArrayInputStream(output.toByteArray())))) {
            return PdfTextExtractor.getTextFromPage(document.getPage(1));
        }
    }
}

