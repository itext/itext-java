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

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.SvgIntegrationTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Tag("IntegrationTest")
public class PdfRootSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    @Test
    public void calculateOutermostViewportTest() {
        Rectangle expected = new Rectangle(0, 0, 600, 600);

        SvgDrawContext context = new SvgDrawContext(null, null);

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();
        PdfFormXObject pdfForm = new PdfFormXObject(expected);
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        context.pushCanvas(canvas);

        SvgTagSvgNodeRenderer renderer = new SvgTagSvgNodeRenderer();

        PdfRootSvgNodeRenderer root = new PdfRootSvgNodeRenderer(renderer);

        Rectangle actual = root.calculateViewPort(context);
        Assertions.assertTrue(expected.equalsWithEpsilon(actual));
    }

    @Test
    public void calculateOutermostViewportWithDifferentXYTest() {
        Rectangle expected = new Rectangle(10, 20, 600, 600);

        SvgDrawContext context = new SvgDrawContext(null, null);

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();
        PdfFormXObject pdfForm = new PdfFormXObject(expected);
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        context.pushCanvas(canvas);

        SvgTagSvgNodeRenderer renderer = new SvgTagSvgNodeRenderer();

        PdfRootSvgNodeRenderer root = new PdfRootSvgNodeRenderer(renderer);

        Rectangle actual = root.calculateViewPort(context);
        Assertions.assertTrue(expected.equalsWithEpsilon(actual));
    }


    @Test
    public void calculateNestedViewportDifferentFromParentTest() {
        Rectangle expected = new Rectangle(0, 0, 500, 500);

        SvgDrawContext context = new SvgDrawContext(null, null);

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();
        PdfFormXObject pdfForm = new PdfFormXObject(expected);
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        context.pushCanvas(canvas);
        context.addViewPort(expected);

        SvgTagSvgNodeRenderer parent = new SvgTagSvgNodeRenderer();
        SvgTagSvgNodeRenderer renderer = new SvgTagSvgNodeRenderer();

        PdfRootSvgNodeRenderer root = new PdfRootSvgNodeRenderer(parent);

        Map<String, String> styles = new HashMap<>();
        styles.put("width", "500");
        styles.put("height", "500");

        renderer.setAttributesAndStyles(styles);
        renderer.setParent(parent);

        Rectangle actual = root.calculateViewPort(context);
        Assertions.assertTrue(expected.equalsWithEpsilon(actual));
    }

    @Test
    public void noBoundingBoxOnXObjectTest() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();

        ISvgNodeRenderer processed = SvgConverter.process(SvgConverter.parse("<svg />"), null).getRootRenderer();
        PdfRootSvgNodeRenderer root = new PdfRootSvgNodeRenderer(processed);
        PdfFormXObject pdfForm = new PdfFormXObject(new PdfStream());
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        SvgDrawContext context = new SvgDrawContext(null, null);
        context.pushCanvas(canvas);

        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> root.draw(context)
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.ROOT_SVG_NO_BBOX, e.getMessage());
    }

    @Test
    public void calculateOutermostTransformation() {
        AffineTransform expected = new AffineTransform(1d, 0d, 0d, -1d, 0d, 600d);

        SvgDrawContext context = new SvgDrawContext(null, null);

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();
        PdfFormXObject pdfForm = new PdfFormXObject(new Rectangle(0, 0, 600, 600));
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        context.pushCanvas(canvas);

        SvgTagSvgNodeRenderer renderer = new SvgTagSvgNodeRenderer();
        PdfRootSvgNodeRenderer root = new PdfRootSvgNodeRenderer(renderer);
        context.addViewPort(root.calculateViewPort(context));

        AffineTransform actual = root.calculateTransformation(context);

        Assertions.assertEquals(expected, actual);
    }
}
