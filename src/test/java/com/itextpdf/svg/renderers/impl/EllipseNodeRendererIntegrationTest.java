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

import java.io.FileNotFoundException;

@Category(IntegrationTest.class)
public class EllipseNodeRendererIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/EllipseSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/EllipseSvgNodeRendererTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void basicEllipseTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "basicEllipseTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <rect x=\"20\" y=\"60\" width = \"160\" height=\"80\" stroke='red' />\n" +
                "    <ellipse cx='100' cy='100' rx='80' ry='40' stroke='green' fill ='cyan'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseCxCyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseCxCyAbsentTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <rect x='0' y='0' width = \"800\" height=\"800\" stroke='red' />\n" +
                "    <ellipse rx='80' ry='40' stroke='green' fill ='cyan'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseCxAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseCxAbsentTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <rect x='0' y='0' width = \"800\" height=\"800\" stroke='red' />\n" +
                "    <ellipse cy ='100' rx='80' ry='40' stroke='green' fill ='cyan'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseCxNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseCxNegativeTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <rect x='0' y='0' width = \"800\" height=\"800\" stroke='red' />\n" +
                "    <ellipse cx ='-40' cy='100' rx='80' ry='40' stroke='green' fill ='cyan'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseCyNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseCyNegativeTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <rect x='0' y='0' width = \"800\" height=\"800\" stroke='red' />\n" +
                "    <ellipse cx ='100' cy='-30' rx='80' ry='60' stroke='green' fill ='cyan'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseCyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseCyAbsentTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <rect x='0' y='0' width = \"800\" height=\"800\" stroke='red' />\n" +
                "    <ellipse cx ='100' rx='80' ry='40' stroke='green' fill ='cyan'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseRxAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseRxAbsentTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <rect x='0' y='0' width = \"800\" height=\"800\" stroke='red' />\n" +
                "    <ellipse cx ='100' ct='100' ry='40' stroke='green' fill ='cyan'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseRyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseRyAbsentTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <rect x='0' y='0' width = \"800\" height=\"800\" stroke='red' />\n" +
                "    <ellipse cx ='100' ct='100' rx='40' stroke='green' fill ='cyan'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseRxNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseRxNegativeTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <rect x='0' y='0' width = \"800\" height=\"800\" stroke='red' />\n" +
                "    <ellipse cx ='100' ct='100' rx='-50' ry='40' stroke='green' fill ='cyan'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseRyNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseRyNegativeTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <rect x='0' y='0' width = \"800\" height=\"800\" stroke='red' />\n" +
                "    <ellipse cx ='100' ct='100' rx='60' ry='-40' stroke='green' fill ='cyan'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseTranslatedTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseTranslatedTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<line x1='100' y1='100' x2='200' y2='100' stroke='black'/>\n" +
                "\t<line x1='100' y1='100' x2='100' y2='200' stroke='black'/>\n" +
                "    <ellipse cx='100' cy='100' rx='80' ry='40' transform='translate(100,50)' stroke='green' fill ='cyan'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseRotatedTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseRotatedTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <ellipse rx='80' ry='40' transform='translate(100,100)' stroke='red' fill ='blue'/>\n" +
                "    <ellipse rx='80' ry='40' transform='translate(100,100) rotate(-25)' stroke='green' fill ='cyan'/>\n" +
                "\t<line x1='100' y1='100' x2='200' y2='100' stroke='black'/>\n" +
                "\t<line x1='100' y1='100' x2='100' y2='200' stroke='black'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseScaledUpTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseScaledUpTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<line x1='100' y1='100' x2='200' y2='100' stroke='black'/>\n" +
                "\t<line x1='100' y1='100' x2='100' y2='200' stroke='black'/>\n" +
                "    <ellipse rx='80' ry='40' transform='translate(100,100) scale(2)' stroke='green' fill ='cyan'/>\n" +
                "    <ellipse rx='80' ry='40' transform='translate(100,100)' stroke='red' fill ='green'/> \n"+
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseScaledDownTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseScaledDownTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<line x1='100' y1='100' x2='200' y2='100' stroke='black'/>\n" +
                "\t<line x1='100' y1='100' x2='100' y2='200' stroke='black'/>\n" +
                "    <ellipse rx='80' ry='40' transform='translate(100,100)' stroke='green' fill ='cyan'/>\n" +
                "    <ellipse rx='80' ry='40' transform='translate(100,100) scale(0.5)' stroke='red' fill ='green'/> \n"+
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseScaledXYTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseScaledXYTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "\t<line x1='100' y1='100' x2='200' y2='100' stroke='black'/>\n" +
                "\t<line x1='100' y1='100' x2='100' y2='200' stroke='black'/>\n" +
                "    <ellipse rx='80' ry='40' transform='translate(100,100)' stroke='black' fill ='yellow'/>\n" +
                "    <ellipse rx='80' ry='40' transform='translate(100,100) scale(0.5,1.1)' stroke='red' fill ='orange'/> \n"+
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseSkewXTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseSkewXTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <ellipse rx='80' ry='40' transform='translate(100,100)' stroke='black' fill ='yellow'/>\n" +
                "    <ellipse rx='80' ry='40' transform='translate(100,100) skewX(40)' stroke='red' fill ='orange'/> \n"+
                "\t<line x1='100' y1='100' x2='200' y2='100' stroke='black'/>\n" +
                "\t<line x1='100' y1='100' x2='100' y2='200' stroke='black'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }

    @Test
    public void ellipseSkewYTest() throws IOException, InterruptedException, java.io.IOException {
        String filename = "ellipseSkewYTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(destinationFolder + filename,new WriterProperties().setCompressionLevel(0)));
        doc.addNewPage();

        String contents = "<svg width='800' height='800'\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" +
                "    <ellipse rx='80' ry='40' transform='translate(100,100)' stroke='black' fill ='yellow'/>\n" +
                "    <ellipse rx='80' ry='40' transform='translate(100,100) skewY(20)' stroke='red' fill ='orange'/> \n"+
                "\t<line x1='100' y1='100' x2='200' y2='100' stroke='black'/>\n" +
                "\t<line x1='100' y1='100' x2='100' y2='200' stroke='black'/>\n" +
                "</svg>";
        SvgConverter.drawOnDocument(contents, doc, 1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_"));
    }
}
