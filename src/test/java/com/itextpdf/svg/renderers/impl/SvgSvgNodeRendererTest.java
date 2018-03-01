package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SvgSvgNodeRendererTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/RootSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/RootSvgNodeRendererTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    /*
     * It is expected that this compare file will have to change when
     * SvgSvgNodeRenderer implementation will get more sophisticated.
     */
    @Test
    public void basicRootRendererTest() throws IOException, InterruptedException {
        String filename = "basicRootRendererTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + "basicRootRendererTest.pdf"));
        doc.addNewPage();

        ISvgNodeRenderer root = new SvgSvgNodeRenderer();
        ISvgNodeRenderer subRoot = new SvgSvgNodeRenderer();

        root.addChild(subRoot);

        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);

        root.draw(context);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }
}
