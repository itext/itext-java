package com.itextpdf.svg.renderers.impl;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class UseUnitTest {

    @Test
    public void isRendererDrawnTest() {
        DummySvgNodeRenderer renderer = new DummySvgNodeRenderer();
        SvgDrawContext context = new SvgDrawContext();
        context.addNamedObject("dummy", renderer);
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDocument.addNewPage();
        context.pushCanvas(new PdfCanvas(page));

        ISvgNodeRenderer use = new UseSvgNodeRenderer();
        use.setAttribute(SvgConstants.Attributes.HREF, "#dummy");

        use.draw(context);

        pdfDocument.close();

        Assert.assertTrue(renderer.isDrawn());
        }

    @Test
    public void referenceNotFoundTest() {
        DummySvgNodeRenderer renderer = new DummySvgNodeRenderer();
        SvgDrawContext context = new SvgDrawContext();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDocument.addNewPage();
        context.pushCanvas(new PdfCanvas(page));

        ISvgNodeRenderer use = new UseSvgNodeRenderer();
        use.setAttribute(SvgConstants.Attributes.HREF, "dummy");

        use.draw(context);

        pdfDocument.close();

        Assert.assertFalse(renderer.isDrawn());
    }
}