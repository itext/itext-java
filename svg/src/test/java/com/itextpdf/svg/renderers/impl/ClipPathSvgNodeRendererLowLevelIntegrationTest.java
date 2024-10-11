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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.SvgIntegrationTest;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class ClipPathSvgNodeRendererLowLevelIntegrationTest extends SvgIntegrationTest {

    private PdfCanvas cv;
    private SvgDrawContext sdc;

    @BeforeEach
    public void setupDrawContextAndCanvas() {
        sdc = new SvgDrawContext(new ResourceResolver(""), new FontProvider());

        // set compression to none, in case you want to write to disk and inspect the created document
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0));
        PdfDocument doc = new PdfDocument(writer);

        cv = new PdfCanvas(doc.addNewPage());
        sdc.pushCanvas(cv);
    }

    @AfterEach
    public void close() {
        cv.getDocument().close();
    }

    @Test
    public void testEmptyClipPathRendererNotDrawn() {
        ClipPathSvgNodeRenderer clipRenderer = new ClipPathSvgNodeRenderer();
        clipRenderer.setAttributesAndStyles(new HashMap<String, String>());
        clipRenderer.draw(sdc);
        Assertions.assertEquals(0, cv.getContentStream().getBytes().length);
    }

    @Test
    public void testEmptyEoClipPathRendererNotDrawn() {
        ClipPathSvgNodeRenderer clipRenderer = new ClipPathSvgNodeRenderer();
        clipRenderer.setAttribute(SvgConstants.Attributes.CLIP_RULE, SvgConstants.Values.FILL_RULE_EVEN_ODD);
        clipRenderer.draw(sdc);
        Assertions.assertEquals(0, cv.getContentStream().getBytes().length);
    }

    @Test
    public void testRectClipPathRenderer() {
        ClipPathSvgNodeRenderer clipRenderer = new ClipPathSvgNodeRenderer();
        clipRenderer.setAttributesAndStyles(new HashMap<String, String>());
        RectangleSvgNodeRenderer rectRenderer = new RectangleSvgNodeRenderer();
        rectRenderer.setAttribute(SvgConstants.Attributes.WIDTH, "400");
        rectRenderer.setAttribute(SvgConstants.Attributes.HEIGHT, "400");
        clipRenderer.addChild(rectRenderer);
        clipRenderer.draw(sdc);

        Assertions.assertEquals("q\n% rect\n0 0 300 300 re\nW\nn\nQ\n", new String(cv.getContentStream().getBytes()));
    }

    @Test
    public void testRectClipPathEoRendererNoChange() {
        ClipPathSvgNodeRenderer clipRenderer = new ClipPathSvgNodeRenderer();
        // clip-rule can only be set on elements in a clipPath, and must not have any influence on drawing
        clipRenderer.setAttribute(SvgConstants.Attributes.CLIP_RULE, SvgConstants.Values.FILL_RULE_EVEN_ODD);
        RectangleSvgNodeRenderer rectRenderer = new RectangleSvgNodeRenderer();
        rectRenderer.setAttribute(SvgConstants.Attributes.WIDTH, "400");
        rectRenderer.setAttribute(SvgConstants.Attributes.HEIGHT, "400");
        clipRenderer.addChild(rectRenderer);
        clipRenderer.draw(sdc);

        Assertions.assertEquals("q\n% rect\n0 0 300 300 re\nW\nn\nQ\n", new String(cv.getContentStream().getBytes()));
    }

    @Test
    public void testRectEoClipPathRenderer() {
        ClipPathSvgNodeRenderer clipRenderer = new ClipPathSvgNodeRenderer();
        clipRenderer.setAttributesAndStyles(new HashMap<String, String>());
        RectangleSvgNodeRenderer rectRenderer = new RectangleSvgNodeRenderer();
        rectRenderer.setAttribute(SvgConstants.Attributes.WIDTH, "400");
        rectRenderer.setAttribute(SvgConstants.Attributes.HEIGHT, "400");
        rectRenderer.setAttribute(SvgConstants.Attributes.CLIP_RULE, SvgConstants.Values.FILL_RULE_EVEN_ODD);
        clipRenderer.addChild(rectRenderer);
        clipRenderer.draw(sdc);

        Assertions.assertEquals("q\n% rect\n0 0 300 300 re\nW*\nn\nQ\n", new String(cv.getContentStream().getBytes()));
    }

    @Test
    public void testAppliedClipPathRenderer() {
        AbstractBranchSvgNodeRenderer clipPathRenderer = new ClipPathSvgNodeRenderer();
        clipPathRenderer.setAttribute(SvgConstants.Attributes.ID, "randomString");

        ISvgNodeRenderer clippedRenderer = new RectangleSvgNodeRenderer();
        clippedRenderer.setAttribute(SvgConstants.Attributes.WIDTH, "80");
        clippedRenderer.setAttribute(SvgConstants.Attributes.HEIGHT, "80");
        clipPathRenderer.addChild(clippedRenderer);

        sdc.addNamedObject("randomString", clipPathRenderer);

        ISvgNodeRenderer drawnRenderer = new CircleSvgNodeRenderer();
        drawnRenderer.setAttribute(SvgConstants.Attributes.R, "84");
        drawnRenderer.setAttribute(SvgConstants.Attributes.CLIP_PATH, "url(#randomString)");

        drawnRenderer.draw(sdc);
        String expected =  "q\n" +
                "% rect\n" +
                "0 0 60 60 re\n" +
                "W\n" +
                "n\n" +
                "0 0 0 rg\n" +
                "% ellipse\n" +
                "63 0 m\n" +
                "63 34.79 34.79 63 0 63 c\n" +
                "-34.79 63 -63 34.79 -63 0 c\n" +
                "-63 -34.79 -34.79 -63 0 -63 c\n" +
                "34.79 -63 63 -34.79 63 0 c\n" +
                "f\n" +
                "Q\n";
        Assertions.assertEquals(expected, new String(cv.getContentStream().getBytes()));
    }

    @Test
    public void testAppliedGroupClipPathRenderer() {
        AbstractBranchSvgNodeRenderer clipPathRenderer = new ClipPathSvgNodeRenderer();
        clipPathRenderer.setAttribute(SvgConstants.Attributes.ID, "randomString");

        ISvgNodeRenderer clippedRenderer = new RectangleSvgNodeRenderer();
        clippedRenderer.setAttribute(SvgConstants.Attributes.WIDTH, "80");
        clippedRenderer.setAttribute(SvgConstants.Attributes.HEIGHT, "80");
        clipPathRenderer.addChild(clippedRenderer);

        sdc.addNamedObject("randomString", clipPathRenderer);

        AbstractBranchSvgNodeRenderer groupRenderer = new GroupSvgNodeRenderer();
        groupRenderer.setAttributesAndStyles(new HashMap<String, String>());
        ISvgNodeRenderer drawnRenderer = new CircleSvgNodeRenderer();
        drawnRenderer.setAttribute(SvgConstants.Attributes.R, "84");
        drawnRenderer.setAttribute(SvgConstants.Attributes.CLIP_PATH, "url(#randomString)");
        groupRenderer.addChild(drawnRenderer);

        groupRenderer.draw(sdc);
        String expected = "0 0 0 rg\n" +
                "q\n" +
                "q\n" +
                "% rect\n" +
                "0 0 60 60 re\n" +
                "W\n" +
                "n\n" +
                "% ellipse\n" +
                "63 0 m\n" +
                "63 34.79 34.79 63 0 63 c\n" +
                "-34.79 63 -63 34.79 -63 0 c\n" +
                "-63 -34.79 -34.79 -63 0 -63 c\n" +
                "34.79 -63 63 -34.79 63 0 c\n" +
                "f\n" +
                "Q\n" +
                "Q\n";
        Assertions.assertEquals(expected, new String(cv.getContentStream().getBytes()));
    }

    @Test
    public void testEoAppliedGroupClipPathRenderer() {
        AbstractBranchSvgNodeRenderer clipPathRenderer = new ClipPathSvgNodeRenderer();
        clipPathRenderer.setAttribute(SvgConstants.Attributes.ID, "randomString");

        ISvgNodeRenderer clippedRenderer = new RectangleSvgNodeRenderer();
        clippedRenderer.setAttribute(SvgConstants.Attributes.WIDTH, "80");
        clippedRenderer.setAttribute(SvgConstants.Attributes.HEIGHT, "80");
        clippedRenderer.setAttribute(SvgConstants.Attributes.CLIP_RULE, SvgConstants.Values.FILL_RULE_EVEN_ODD);

        ISvgNodeRenderer clippedRenderer2 = new RectangleSvgNodeRenderer();
        clippedRenderer2.setAttribute(SvgConstants.Attributes.WIDTH, "80");
        clippedRenderer2.setAttribute(SvgConstants.Attributes.HEIGHT, "80");

        clipPathRenderer.addChild(clippedRenderer);
        clipPathRenderer.addChild(clippedRenderer2);

        sdc.addNamedObject("randomString", clipPathRenderer);

        AbstractBranchSvgNodeRenderer groupRenderer = new GroupSvgNodeRenderer();
        groupRenderer.setAttributesAndStyles(new HashMap<String, String>());
        ISvgNodeRenderer drawnRenderer = new CircleSvgNodeRenderer();
        drawnRenderer.setAttribute(SvgConstants.Attributes.R, "84");
        drawnRenderer.setAttribute(SvgConstants.Attributes.CLIP_PATH, "url(#randomString)");
        groupRenderer.addChild(drawnRenderer);

        groupRenderer.draw(sdc);
        String expected = "0 0 0 rg\n" +
                "q\n" +
                "q\n" +
                "% rect\n" +
                "0 0 60 60 re\n" +
                "W*\n" +
                "n\n" +
                "% ellipse\n" +
                "63 0 m\n" +
                "63 34.79 34.79 63 0 63 c\n" +
                "-34.79 63 -63 34.79 -63 0 c\n" +
                "-63 -34.79 -34.79 -63 0 -63 c\n" +
                "34.79 -63 63 -34.79 63 0 c\n" +
                "f\n" +
                "Q\n" +
                "q\n" +
                "% rect\n" +
                "0 0 60 60 re\n" +
                "W\n" +
                "n\n" +
                "% ellipse\n" +
                "63 0 m\n" +
                "63 34.79 34.79 63 0 63 c\n" +
                "-34.79 63 -63 34.79 -63 0 c\n" +
                "-63 -34.79 -34.79 -63 0 -63 c\n" +
                "34.79 -63 63 -34.79 63 0 c\n" +
                "f\n" +
                "Q\n" +
                "Q\n";
        Assertions.assertEquals(expected, new String(cv.getContentStream().getBytes()));
    }
}
