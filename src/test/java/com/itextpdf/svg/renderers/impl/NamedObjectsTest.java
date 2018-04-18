package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.styledxmlparser.AttributeConstants;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
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
        doc.addNewPage();
        float width = 100;
        float height = 100;
        PdfFormXObject pdfForm = new PdfFormXObject(new Rectangle(0, 0, width, height));
        PdfCanvas canvas = new PdfCanvas(pdfForm, doc);

        INode parsedSvg = SvgConverter.parse(new FileInputStream("./src/test/resources/com/itextpdf/svg/renderers/impl/NamedObjectsTest/names.svg"));
        ISvgNodeRenderer process = SvgConverter.process(parsedSvg);
        SvgDrawContext drawContext = new SvgDrawContext();
        ISvgNodeRenderer root = new PdfRootSvgNodeRenderer(process);
        drawContext.pushCanvas(canvas);
        root.draw(drawContext);
        doc.close();

        Assert.assertTrue(drawContext.getNamedObject("name_svg") instanceof PdfFormXObject);
        Assert.assertTrue(drawContext.getNamedObject("name_rect") instanceof RectangleSvgNodeRenderer);
    }
}