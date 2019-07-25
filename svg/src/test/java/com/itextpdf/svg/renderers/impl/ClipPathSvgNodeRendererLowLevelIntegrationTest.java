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
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

@Category(IntegrationTest.class)
public class ClipPathSvgNodeRendererLowLevelIntegrationTest extends SvgIntegrationTest {

    private PdfCanvas cv;
    private SvgDrawContext sdc;

    @Before
    public void setupDrawContextAndCanvas() throws FileNotFoundException {
        sdc = new SvgDrawContext(new ResourceResolver(""), new FontProvider());

        // set compression to none, in case you want to write to disk and inspect the created document
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0));
        PdfDocument doc = new PdfDocument(writer);

        cv = new PdfCanvas(doc.addNewPage());
        sdc.pushCanvas(cv);
    }

    @After
    public void close() {
        cv.getDocument().close();
    }

    @Test
    public void testEmptyClipPathRendererNotDrawn() {
        ClipPathSvgNodeRenderer clipRenderer = new ClipPathSvgNodeRenderer();
        clipRenderer.setAttributesAndStyles(new HashMap<String, String>());
        clipRenderer.draw(sdc);
        Assert.assertEquals(0, cv.getContentStream().getBytes().length);
    }

    @Test
    public void testEmptyEoClipPathRendererNotDrawn() {
        ClipPathSvgNodeRenderer clipRenderer = new ClipPathSvgNodeRenderer();
        clipRenderer.setAttribute(SvgConstants.Attributes.CLIP_RULE, SvgConstants.Values.FILL_RULE_EVEN_ODD);
        clipRenderer.draw(sdc);
        Assert.assertEquals(0, cv.getContentStream().getBytes().length);
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

        Assert.assertEquals("q\n% rect\n0 0 300 300 re\nW\nn\nQ\n", new String(cv.getContentStream().getBytes()));
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

        Assert.assertEquals("q\n% rect\n0 0 300 300 re\nW\nn\nQ\n", new String(cv.getContentStream().getBytes()));
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

        Assert.assertEquals("q\n% rect\n0 0 300 300 re\nW*\nn\nQ\n", new String(cv.getContentStream().getBytes()));
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
        Assert.assertEquals(expected, new String(cv.getContentStream().getBytes()));
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
        Assert.assertEquals(expected, new String(cv.getContentStream().getBytes()));
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
        Assert.assertEquals(expected, new String(cv.getContentStream().getBytes()));
    }
}
