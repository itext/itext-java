package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Category(IntegrationTest.class)
public class PolygonSvgNodeRendererTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/PolygonSvgNoderendererTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/PolygonSvgNoderendererTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void polygonLineRendererTest() throws IOException, InterruptedException {
        String filename = "polygonLineRendererTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        ISvgNodeRenderer root = new PolygonSvgNodeRenderer();
        Map<String, String> polyLineAttributes = new HashMap<>();
        polyLineAttributes.put(SvgTagConstants.POINTS, "60,20 100,40 100,80 60,100 20,80 20,40");
        root.setAttributesAndStyles(polyLineAttributes);
        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);

        root.draw(context);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void polygonLinkedPointCheckerImplicit() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();
        ISvgNodeRenderer root = new PolygonSvgNodeRenderer();
        Map<String, String> polyLineAttributes = new HashMap<>();
        polyLineAttributes.put(SvgTagConstants.POINTS, "0,0 100,100 200,200 300,300");
        root.setAttributesAndStyles(polyLineAttributes);
        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        root.draw(context);

        List<Point> expectedPoints = new ArrayList<>();
        expectedPoints.add(new Point(0, 0));
        expectedPoints.add(new Point(100, 100));
        expectedPoints.add(new Point(200, 200));
        expectedPoints.add(new Point(300, 300));
        expectedPoints.add(new Point(0, 0));
        List<Point> attributePoints = ((PolygonSvgNodeRenderer) root).getPoints();

        Assert.assertEquals(expectedPoints.size(), attributePoints.size());
        for (int x = 0; x < attributePoints.size(); x++) {
            Assert.assertEquals(expectedPoints.get(x), attributePoints.get(x));
        }

    }

    @Test
    public void polygonLinkedPointCheckerExplicit() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();
        ISvgNodeRenderer root = new PolygonSvgNodeRenderer();
        Map<String, String> polyLineAttributes = new HashMap<>();
        polyLineAttributes.put(SvgTagConstants.POINTS, "0,0 100,100 200,200 300,300 0,0");
        root.setAttributesAndStyles(polyLineAttributes);
        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        root.draw(context);

        List<Point> expectedPoints = new ArrayList<>();
        expectedPoints.add(new Point(0, 0));
        expectedPoints.add(new Point(100, 100));
        expectedPoints.add(new Point(200, 200));
        expectedPoints.add(new Point(300, 300));
        expectedPoints.add(new Point(0, 0));
        List<Point> attributePoints = ((PolygonSvgNodeRenderer) root).getPoints();

        Assert.assertEquals(expectedPoints.size(), attributePoints.size());
        for (int x = 0; x < attributePoints.size(); x++) {
            Assert.assertEquals(expectedPoints.get(x), attributePoints.get(x));
        }

    }

    @Test
    public void polygonEmptyPointCheckerTest() throws IOException, InterruptedException {
        String filename = "polygonEmptyPointCheckerTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        ISvgNodeRenderer root = new PolygonSvgNodeRenderer();
        Map<String, String> polyLineAttributes = new HashMap<>();
        root.setAttributesAndStyles(polyLineAttributes);
        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);

        root.draw(context);
        doc.close();

        int numPoints = ((PolygonSvgNodeRenderer) root).getPoints().size();
        Assert.assertEquals(numPoints, 0);
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }
}
