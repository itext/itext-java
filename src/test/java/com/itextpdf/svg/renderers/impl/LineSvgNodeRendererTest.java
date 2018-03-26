package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Category(IntegrationTest.class)
public class LineSvgNodeRendererTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/LineSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/LineSvgNodeRendererTest/";
    public static final String expectedExceptionMessage = SvgLogMessageConstant.FLOAT_PARSING_NAN;


    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void lineRendererTest() throws IOException, InterruptedException {
        String filename = "lineSvgRendererTest.pdf";
        PdfDocument doc = new PdfDocument( new PdfWriter( destinationFolder + filename ) );
        doc.addNewPage();

        Map<String, String> lineProperties = new HashMap<String, String>();

        lineProperties.put( "x1", "100" );
        lineProperties.put( "y1", "800" );
        lineProperties.put( "x2", "300" );
        lineProperties.put( "y2", "800" );
        lineProperties.put( "stroke-width", "25" );

        LineSvgNodeRenderer root = new LineSvgNodeRenderer();
        root.setAttributesAndStyles( lineProperties );

        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas( doc, 1 );
        context.pushCanvas( cv );

        root.draw( context );
        doc.close();
        Assert.assertNull( new CompareTool().compareByContent( destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_" ) );
    }

    @Test
    public void lineWithEmpyAttributesTest() throws IOException, InterruptedException {
        String filename = "lineWithEmpyAttributesTest.pdf";
        PdfDocument doc = new PdfDocument( new PdfWriter( destinationFolder + filename ) );
        doc.addNewPage();

        Map<String, String> lineProperties = new HashMap<String, String>();

        LineSvgNodeRenderer root = new LineSvgNodeRenderer();
        root.setAttributesAndStyles( lineProperties );

        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas( doc, 1 );
        context.pushCanvas( cv );

        root.draw( context );
        doc.close();
        Assert.assertNull( new CompareTool().compareByContent( destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_" ) );
    }

    @Test
    public void lnvalidAttributeTest01() {
        boolean isThrown = false;

        PdfDocument doc = new PdfDocument( new PdfWriter( new ByteArrayOutputStream() ) );
        doc.addNewPage();
        ISvgNodeRenderer root = new LineSvgNodeRenderer();
        Map<String, String> lineProperties = new HashMap<>();
        lineProperties.put( "x1", "1" );
        lineProperties.put( "y1", "800" );
        lineProperties.put( "x2", "notAnum" );
        lineProperties.put( "y2", "alsoNotANum" );
        root.setAttributesAndStyles( lineProperties );
        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas( doc, 1 );
        context.pushCanvas( cv );

        try {
            root.draw( context );
        } catch (SvgProcessingException e) {
            isThrown = true;
            Assert.assertEquals("Exception wasn't thrown",expectedExceptionMessage.trim(), e.getMessage() );

        } finally {
            doc.close();
        }

        Assert.assertTrue( "Exception wasn't thrown", isThrown );

    }


    @Test
    public void lnvalidAttributeTest02() {
        boolean isThrown = false;
        PdfDocument doc = new PdfDocument( new PdfWriter( new ByteArrayOutputStream() ) );
        doc.addNewPage();
        ISvgNodeRenderer root = new LineSvgNodeRenderer();
        Map<String, String> lineProperties = new HashMap<>();
        lineProperties.put( "x1", "100" );
        lineProperties.put( "y1", "800" );
        lineProperties.put( "x2", "1 0" );
        lineProperties.put( "y2", "0 2 0" );
        root.setAttributesAndStyles( lineProperties );

        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas( doc, 1 );
        context.pushCanvas( cv );
        try {
            root.draw( context );

        } catch (SvgProcessingException e) {
            isThrown = true;

            Assert.assertEquals("Exception wasn't thrown", expectedExceptionMessage.trim(), e.getMessage() );

        } finally {
            doc.close();
        }

        Assert.assertTrue( "Exception wasn't thrown", isThrown );
    }

    @Test
    public void emptyPointsListTest() throws IOException, InterruptedException {
        String filename = "lineEmptyPointsListTest.pdf";
        PdfDocument doc = new PdfDocument( new PdfWriter( destinationFolder + filename ) );
        doc.addNewPage();

        ISvgNodeRenderer root = new LineSvgNodeRenderer();
        Map<String, String> lineProperties = new HashMap<>();
        root.setAttributesAndStyles( lineProperties );
        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas( doc, 1 );
        context.pushCanvas( cv );

        root.draw( context );
        doc.close();

        int numPoints = ((LineSvgNodeRenderer) root).attributesAndStyles.size();
        Assert.assertEquals( numPoints, 0 );
        Assert.assertNull( new CompareTool().compareByContent( destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_" ) );

    }

    //TODO(RND-823) We'll need an integration test with the entire (not yet created) pipeline as well

}
