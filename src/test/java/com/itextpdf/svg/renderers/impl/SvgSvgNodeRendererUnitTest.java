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
