/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class SvgSvgNodeRendererUnitTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void calculateOutermostViewportTest() {
        Rectangle expected = new Rectangle(0, 0, 600, 600);

        SvgDrawContext context = new SvgDrawContext();

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();
        PdfFormXObject pdfForm = new PdfFormXObject(expected);
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        context.pushCanvas(canvas);

        SvgSvgNodeRenderer renderer = new SvgSvgNodeRenderer();
        Rectangle actual = renderer.calculateViewPort(context);
        Assert.assertTrue(expected.equalsWithEpsilon(actual));
    }

    @Test
    public void calculateOutermostViewportWithDifferentXYTest() {
        Rectangle expected = new Rectangle(10, 20, 600, 600);

        SvgDrawContext context = new SvgDrawContext();

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();
        PdfFormXObject pdfForm = new PdfFormXObject(expected);
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        context.pushCanvas(canvas);

        SvgSvgNodeRenderer renderer = new SvgSvgNodeRenderer();
        Rectangle actual = renderer.calculateViewPort(context);
        Assert.assertTrue(expected.equalsWithEpsilon(actual));
    }

    @Test
    public void calculateNestedViewportSameAsParentTest() {
        Rectangle expected = new Rectangle(0, 0, 600, 600);

        SvgDrawContext context = new SvgDrawContext();

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();
        PdfFormXObject pdfForm = new PdfFormXObject(expected);
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        context.pushCanvas(canvas);
        context.addViewPort(expected);

        SvgSvgNodeRenderer parent = new SvgSvgNodeRenderer();
        SvgSvgNodeRenderer renderer = new SvgSvgNodeRenderer();
        renderer.setParent(parent);

        Rectangle actual = renderer.calculateViewPort(context);
        Assert.assertTrue(expected.equalsWithEpsilon(actual));
    }

    @Test
    public void calculateNestedViewportDifferentFromParentTest() {
        Rectangle expected = new Rectangle(0, 0, 500, 500);

        SvgDrawContext context = new SvgDrawContext();

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();
        PdfFormXObject pdfForm = new PdfFormXObject(expected);
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        context.pushCanvas(canvas);
        context.addViewPort(expected);

        SvgSvgNodeRenderer parent = new SvgSvgNodeRenderer();
        SvgSvgNodeRenderer renderer = new SvgSvgNodeRenderer();

        Map<String, String> styles = new HashMap<>();
        styles.put("width", "500");
        styles.put("height", "500");

        renderer.setAttributesAndStyles(styles);
        renderer.setParent(parent);

        Rectangle actual = renderer.calculateViewPort(context);
        Assert.assertTrue(expected.equalsWithEpsilon(actual));
    }

    @Test
    public void noBoundingBoxOnXObjectTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.ROOT_SVG_NO_BBOX);

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();

        ISvgNodeRenderer rootRenderer = SvgConverter.process(SvgConverter.parse("<svg />"));
        PdfFormXObject pdfForm = new PdfFormXObject(new PdfStream());
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        SvgDrawContext context = new SvgDrawContext();
        context.pushCanvas(canvas);

        rootRenderer.draw(context);
    }

    @Test
    public void calculateOutermostTransformation() {
        AffineTransform expected = new AffineTransform(0.75d, 0d, 0d, -0.75d, 0d, 450d);

        SvgDrawContext context = new SvgDrawContext();

        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();
        PdfFormXObject pdfForm = new PdfFormXObject(new Rectangle(0, 0, 600, 600));
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        context.pushCanvas(canvas);

        SvgSvgNodeRenderer renderer = new SvgSvgNodeRenderer();
        context.addViewPort(renderer.calculateViewPort(context));

        AffineTransform actual = renderer.calculateTransformation(context);

        Assert.assertEquals(expected, actual);
    }
}
