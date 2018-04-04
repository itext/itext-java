package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Category(IntegrationTest.class)
public class PathSvgNodeRendererTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/PathSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/PathSvgNodeRendererTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void pathLineRendererMoveToTest() throws IOException, InterruptedException {
        String filename = "pathNodeRendererMoveToTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        Map<String, String> pathShapes = new HashMap<String, String>();
        pathShapes.put( "d", "M 100,100, L300,100,L200,300,z" );


        ISvgNodeRenderer pathRenderer = new PathSvgNodeRenderer();
        pathRenderer.setAttributesAndStyles( pathShapes );

        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        pathRenderer.draw( context );
        doc.close();
        Assert.assertNull( new CompareTool().compareByContent( destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_" ) );

    }

    @Test
    public void pathLineRendererMoveToTest1() throws IOException, InterruptedException {
        String filename = "pathNodeRendererMoveToTest1.pdf";
        PdfDocument doc = new PdfDocument( new PdfWriter( destinationFolder + filename ) );
        doc.addNewPage();

        Map<String, String> pathShapes = new HashMap<String, String>();
        pathShapes.put( "d", "M 100 100 l300 100 L200 300 z" );

        ISvgNodeRenderer pathRenderer = new PathSvgNodeRenderer();
        pathRenderer.setAttributesAndStyles( pathShapes );

        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas( doc, 1 );
        context.pushCanvas( cv );
        pathRenderer.draw( context );
        doc.close();
        Assert.assertNull( new CompareTool().compareByContent( destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_" ) );

    }

    @Test
    @Ignore("RND-900")
    public void pathLineRendererCurveToTest() throws IOException, InterruptedException {
        String filename = "pathNodeRendererCurveToTest.pdf";
        PdfDocument doc = new PdfDocument( new PdfWriter( destinationFolder + filename ) );
        doc.addNewPage();

        Map<String, String> pathShapes = new HashMap<String, String>();
        pathShapes.put( "d", "M100,200 C100,100 250,100 250,200 S400,300 400,200,z" );


        ISvgNodeRenderer pathRenderer = new PathSvgNodeRenderer();
        pathRenderer.setAttributesAndStyles( pathShapes );

        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas( doc, 1 );
        context.pushCanvas( cv );
        pathRenderer.draw( context );
        doc.close();
        Assert.assertNull( new CompareTool().compareByContent( destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_" ) );

    }

    @Test
    @Ignore("RND-900")
    public void pathLineRendererCurveToTest1() throws IOException, InterruptedException {
        String filename = "pathNodeRendererCurveToTest1.pdf";
        PdfDocument doc = new PdfDocument( new PdfWriter( destinationFolder + filename ) );
        doc.addNewPage();

        Map<String, String> pathShapes = new HashMap<String, String>();
        pathShapes.put( "d", "M100 200 C100 100 250 100 250 200 S400 300 400 200 z" );

        ISvgNodeRenderer pathRenderer = new PathSvgNodeRenderer();
        pathRenderer.setAttributesAndStyles( pathShapes );

        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas( doc, 1 );
        context.pushCanvas( cv );
        pathRenderer.draw( context );
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));

    }

    @Test
    public void  pathNodeRendererQCurveToCurveToTest()throws IOException, InterruptedException{
        String filename = "pathNodeRendererQCurveToCurveToTest.pdf";
        PdfDocument doc = new PdfDocument( new PdfWriter( destinationFolder + filename ) );
        doc.addNewPage();

        Map<String, String> pathShapes = new HashMap<String, String>();
        pathShapes.put( "d", "M200,300 Q400,50 600,300,z" );

        ISvgNodeRenderer pathRenderer = new PathSvgNodeRenderer();
        pathRenderer.setAttributesAndStyles( pathShapes );

        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas( doc, 1 );
        context.pushCanvas( cv );
        pathRenderer.draw( context );
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void  pathNodeRendererQCurveToCurveToTest1()throws IOException, InterruptedException{
        String filename = "pathNodeRendererQCurveToCurveToTest1.pdf";
        PdfDocument doc = new PdfDocument( new PdfWriter( destinationFolder + filename ) );
        doc.addNewPage();

        Map<String, String> pathShapes = new HashMap<String, String>();
        pathShapes.put( "d", "M200 300 Q400 50 600 300 z" );

        ISvgNodeRenderer pathRenderer = new PathSvgNodeRenderer();
        pathRenderer.setAttributesAndStyles( pathShapes );

        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas( doc, 1 );
        context.pushCanvas( cv );
        pathRenderer.draw( context );
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }
    @Test
    public void pathNodeRederarIntegrationTest() throws IOException, InterruptedException{
        String filename = "pathNodeRederarIntegrationTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename));
        doc.addNewPage();

        String svgFilename = "pathRendererTest.svg";
        InputStream xmlStream = new FileInputStream( sourceFolder + svgFilename );
        IElementNode rootTag = new JsoupXmlParser().parse( xmlStream, "ISO-8859-1" );

        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgNodeRenderer root = processor.process( rootTag );

        SvgDrawContext context = new SvgDrawContext();
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
       Assert.assertTrue(  root.getChildren().get( 0 )instanceof PathSvgNodeRenderer );
        root.getChildren().get( 0 ).draw( context );
       // root.getChildren().get( 0 ).draw( context );
        doc.close();
    }
}
