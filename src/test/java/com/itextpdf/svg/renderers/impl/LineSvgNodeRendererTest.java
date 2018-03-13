package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class LineSvgNodeRendererTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/RootSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/RootSvgNodeRendererTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }


    @Test
    public void lineRendererTest() throws IOException, InterruptedException {
        String filename = "lineRendererTest.pdf";
        PdfDocument doc = new PdfDocument( new PdfWriter( destinationFolder + "lineRendererTest.pdf" ) );
        doc.addNewPage();

        ISvgNodeRenderer root = new LineSvgNodeRenderer( 100, 800, 300, 800 );

        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas( doc, 1 );
        context.pushCanvas( cv );

        root.draw( context );
        doc.close();
        Assert.assertNull( new CompareTool().compareByContent( destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_" ) );

    }

}
