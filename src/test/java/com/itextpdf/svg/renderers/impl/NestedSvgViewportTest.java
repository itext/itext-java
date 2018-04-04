package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

public class NestedSvgViewportTest {

    public void nestedSvgViewPortTest() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        document.addNewPage();

        ISvgNodeRenderer rootRenderer = SvgConverter.process(SvgConverter.parse("<svg version=\"1.1\" baseProfile=\"full\" width=\"250\" height=\"250\" xmlns=\"http://www.w3.org/2000/svg\"><svg x=\"10\" y=\"10\" width=\"100\" height=\"100\"><line x1=\"900\" y1=\"300\" x2=\"1100\" y2=\"100\"stroke-width=\"25\"  /></svg></svg>"));
        PdfFormXObject pdfForm = new PdfFormXObject(new PdfStream());
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        SvgDrawContext context = new SvgDrawContext();
        context.pushCanvas(canvas);
        context.addViewPort(new Rectangle(0,0, 1000, 1000));

        SvgSvgNodeRenderer nestedSvgRenderer = (SvgSvgNodeRenderer) rootRenderer.getChildren().get(0);
        nestedSvgRenderer.doDraw(context);

        PdfResources resources = canvas.getResources();
        PdfFormXObject xObject = resources.getForm(new PdfName("Fm1"));
        PdfArray bBox = xObject.getBBox();
        float[] actual = bBox.toFloatArray();
        float[] expected = new float[] {7.5f, 7.5f, 82.5f, 82.5f};

        for ( int i = 0; i < expected.length; i++ ) {
            Assert.assertEquals(expected[i], actual[i]);
        }
    }
}
