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

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.xobject.SvgImageXObject;
import com.itextpdf.test.ExtendedITextTest;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class ImageSvgNodeRendererUnitTest extends ExtendedITextTest {

    private static final float EPSILON = 0.00001f;
    private static final String IMAGE_DATA = "data:image/png;base64,"
            + "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";

    @Test
    public void noObjectBoundingBoxTest() {
        ImageSvgNodeRenderer renderer = new ImageSvgNodeRenderer();
        Assertions.assertNull(renderer.getObjectBoundingBox(null));
    }

    @Test
    public void zeroSizedViewBoxDoesNotProduceExceptionTest() {
        PdfFormXObject zeroSizedXObject = new PdfFormXObject(new Rectangle(0, 0, 0, 0));

        ResourceResolver resourceResolver = new ResourceResolver("") {
            @Override
            public PdfXObject retrieveImage(String src) {
                return zeroSizedXObject;
            }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument document = new PdfDocument(new PdfWriter(baos))) {
            PdfCanvas canvas = new PdfCanvas(document.addNewPage());

            SvgDrawContext context = new SvgDrawContext(resourceResolver, null);
            context.addViewPort(new Rectangle(0, 0, 500, 500));
            context.pushCanvas(canvas);

            Map<String, String> attributes = new ConcurrentHashMap<>();
            attributes.put(SvgConstants.Attributes.HREF, "any.png");
            attributes.put(SvgConstants.Attributes.WIDTH, "100");
            attributes.put(SvgConstants.Attributes.HEIGHT, "50");

            ImageSvgNodeRenderer renderer = new ImageSvgNodeRenderer();
            renderer.setAttributesAndStyles(attributes);

            // Should not throw when a view box does not exist
            renderer.doDraw(context);

            String contentStream = new String(canvas.getContentStream().getBytes(), StandardCharsets.UTF_8);
            // 100px x 50px converted to points
            Assertions.assertTrue(contentStream.contains("75 0 0 -37.5 0 37.5 cm"));
        }
    }

    @Test
    public void objectBoundingBoxTest() {
        ImageSvgNodeRenderer renderer = new ImageSvgNodeRenderer();
        renderer.setAttribute(SvgConstants.Attributes.HREF, IMAGE_DATA);
        renderer.setAttribute(SvgConstants.Attributes.X, "10");
        renderer.setAttribute(SvgConstants.Attributes.Y, "20");
        renderer.setAttribute(SvgConstants.Attributes.WIDTH, "40");
        renderer.setAttribute(SvgConstants.Attributes.HEIGHT, "30");
        renderer.setAttribute(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO, SvgConstants.Values.NONE);

        SvgDrawContext context = new SvgDrawContext(null, null);
        context.addViewPort(new Rectangle(0, 0, 200, 200));
        Rectangle objectBoundingBox = renderer.getObjectBoundingBox(context);

        Assertions.assertNotNull(objectBoundingBox);
        Assertions.assertTrue(new Rectangle(7.5f, 15f, 30f, 22.5f).equalsWithEpsilon(objectBoundingBox, EPSILON));
    }

    @Test
    public void zeroSizedSvgImageXObjectUpdatesBBoxTest() {
        SvgImageXObject zeroSizedXObject = new SvgImageXObject(null, new TestSvgProcessorResult(),
                new ResourceResolver(""));

        ResourceResolver resourceResolver = new ResourceResolver("") {
            @Override
            public PdfXObject retrieveImage(String src) {
                return zeroSizedXObject;
            }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument document = new PdfDocument(new PdfWriter(baos))) {
            PdfCanvas canvas = new PdfCanvas(document.addNewPage());

            SvgDrawContext context = new SvgDrawContext(resourceResolver, null);
            context.addViewPort(new Rectangle(0, 0, 500, 500));
            context.pushCanvas(canvas);

            Map<String, String> attributes = new ConcurrentHashMap<>();
            attributes.put(SvgConstants.Attributes.HREF, "any.svg");
            attributes.put(SvgConstants.Attributes.WIDTH, "100");
            attributes.put(SvgConstants.Attributes.HEIGHT, "50");

            ImageSvgNodeRenderer renderer = new ImageSvgNodeRenderer();
            renderer.setAttributesAndStyles(attributes);

            renderer.doDraw(context);

            Assertions.assertNotNull(zeroSizedXObject.getBBox());
            // 100px x 50px converted to points
            Assertions.assertTrue(new Rectangle(0, 0, 75, 37.5f)
                    .equalsWithEpsilon(zeroSizedXObject.getBBox().toRectangle()));
        }
    }

    private static class TestSvgProcessorResult implements ISvgProcessorResult {

        @Override
        public Map<String, ISvgNodeRenderer> getNamedObjects() {
            return null;
        }

        @Override
        public ISvgNodeRenderer getRootRenderer() {
            return null;
        }

        @Override
        public FontProvider getFontProvider() {
            return null;
        }

        @Override
        public FontSet getTempFonts() {
            return null;
        }
    }
}
