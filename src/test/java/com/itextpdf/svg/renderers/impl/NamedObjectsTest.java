package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class NamedObjectsTest {

    @Test
    public void addNamedObject() throws IOException {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setCompressionLevel(0)));
        PdfPage pdfPage = doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc, 1);

        INode parsedSvg = SvgConverter.parse(new FileInputStream("./src/test/resources/com/itextpdf/svg/renderers/impl/NamedObjectsTest/names.svg"));
        ISvgNodeRenderer process = SvgConverter.process(parsedSvg);
        SvgDrawContext drawContext = new SvgDrawContext();
        drawContext.pushCanvas(canvas);
        process.draw(drawContext);
        doc.close();

        Assert.assertTrue(drawContext.getNamedObject("name_svg") instanceof PdfFormXObject);
        Assert.assertTrue(drawContext.getNamedObject("name_rect") instanceof RectangleSvgNodeRenderer);
    }
}