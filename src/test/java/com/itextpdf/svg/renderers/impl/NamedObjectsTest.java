package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.ISvgProcessor;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

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
        ISvgProcessor processor = new DefaultSvgProcessor();
        ISvgProcessorResult result = processor.process( parsedSvg );
        ISvgNodeRenderer process = result.getRootRenderer();
        SvgDrawContext drawContext = new SvgDrawContext();
        ISvgNodeRenderer root = new PdfRootSvgNodeRenderer( process );
        drawContext.pushCanvas(canvas);
        root.draw( drawContext );
        doc.close();

        Assert.assertTrue( result.getNamedObjects().get( "name_rect" ) instanceof RectangleSvgNodeRenderer );
    }
}