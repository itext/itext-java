package com.itextpdf.svg.renderers.impl;

import com.itextpdf.io.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class CircleNodeRendererIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/CircleSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/CircleSvgNodeRendererTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void basicCircleTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "basicCircleTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<circle cx='100' cy='100' r='80' stroke='red' fill ='green'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleCxCyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleCxCyAbsentTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<circle r='80' stroke='green' fill ='cyan'/>\n" +
                "\t<circle r='80' transform='translate(100,100)' stroke='red' fill ='green'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleCxAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleCxAbsentTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<circle cy='100' r='80' stroke='green' fill ='cyan'/>\n" +
                "\t<circle cy='100' r='80' transform='translate(100,100)' stroke='red' fill ='green'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleCxNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleCxNegativeTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<circle cx='-100' cy='100' r='80' stroke='green' fill ='cyan'/>\n" +
                "\t<circle cx='-100' cy='100' r='80' transform='translate(100,100)' stroke='red' fill ='green'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleCyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleCyAbsentTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<circle cx='100' r='80' stroke='green' fill ='cyan'/>\n" +
                "\t<circle cx='100' r='80' transform='translate(100,100)' stroke='red' fill ='green'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleCyNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleCyNegativeTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<circle cx='100' cy='-100' r='80' stroke='green' fill ='cyan'/>\n" +
                "\t<circle cx='100' cy='-100' r='80' transform='translate(100,100)' stroke='red' fill ='green'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleRAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleRAbsentTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<circle cx='100' cy='100' stroke='green' fill ='cyan'/>\n" +
                "\t<circle cx='100' cy='100' transform='translate(100,100)' stroke='red' fill ='green'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleRNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleRNegativeTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<circle cx='100' cy='100' r='-80' stroke='green' fill ='cyan'/>\n" +
                "\t<circle cx='100' cy='100' r='-80' transform='translate(100,100)' stroke='red' fill ='green'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleTranslatedTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleTranslatedTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<circle r='80' stroke='green' fill ='cyan'/>\n" +
                "\t<circle r='80' transform='translate(100,100)' stroke='red' fill ='green'/>\n" +
                "\t<line x1='0' y1='0' x2='100' y2='0' transform='translate(100,100)' stroke='black' />\n" +
                "\t<line x1='0' y1='0' x2='0' y2='100' transform='translate(100,100)' stroke='black'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleRotatedTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleRotatedTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<circle r='80' stroke='green' fill ='cyan'/>\n" +
                "\t<circle r='80' transform='translate(100,100) rotate(25)' stroke='red' fill ='green'/>\n" +
                "\t<line x1='0' y1='0' x2='100' y2='0' transform='translate(100,100) rotate(25)' stroke='black' />\n" +
                "\t<line x1='0' y1='0' x2='0' y2='100' transform='translate(100,100) rotate(25)' stroke='black'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleScaledUpTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleScaledUpTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <circle r='80' transform='translate(100,100) scale(2)' stroke='red' fill ='blue'/>\n" +
                "    <circle r='80' transform='translate(100,100)' stroke='green' fill ='cyan'/>\n" +
                "\t<line x1='100' y1='100' x2='200' y2='100' stroke='black'/>\n" +
                "\t<line x1='100' y1='100' x2='100' y2='200' stroke='black'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleScaledDownTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleScaledDownTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <circle r='80' transform='translate(100,100)' stroke='red' fill ='blue'/>\n" +
                "    <circle r='80' transform='translate(100,100) scale(0.5)' stroke='green' fill ='cyan'/>\n" +
                "\t<line x1='100' y1='100' x2='200' y2='100' stroke='black'/>\n" +
                "\t<line x1='100' y1='100' x2='100' y2='200' stroke='black'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleScaledXYTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleScaledXYTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <circle r='80' transform='translate(100,100)' stroke='red' fill ='blue'/>\n" +
                "    <circle r='80' transform='translate(100,100) scale(0.5,1.1)' stroke='green' fill ='cyan'/>\n" +
                "\t<line x1='100' y1='100' x2='200' y2='100' stroke='black'/>\n" +
                "\t<line x1='100' y1='100' x2='100' y2='200' stroke='black'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleSkewXTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleSkewXTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<circle r='80' transform='translate(100,100)' stroke='green' fill ='cyan'/>\n" +
                "\t<circle r='80' transform='translate(100,100) skewX(40)' stroke='red' fill ='green'/>\n" +
                "\t<line x1='0' y1='0' x2='100' y2='0' transform='translate(100,100) skewX(40)' stroke='black' />\n" +
                "\t<line x1='0' y1='0' x2='0' y2='100' transform='translate(100,100) skewX(40)' stroke='black'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void circleSkewYTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "circleSkewYTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<circle r='80' transform='translate(100,100)' stroke='green' fill ='cyan'/>\n" +
                "\t<circle r='80' transform='translate(100,100) skewY(40)' stroke='red' fill ='green'/>\n" +
                "\t<line x1='0' y1='0' x2='100' y2='0' transform='translate(100,100) skewY(40)' stroke='black' />\n" +
                "\t<line x1='0' y1='0' x2='0' y2='100' transform='translate(100,100) skewY(40)' stroke='black' />\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }
}
