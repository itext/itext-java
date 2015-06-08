package com.itextpdf.model;

import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.model.element.Image;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Text;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

public class ImageTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/ImageTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/ImageTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void imageTest01() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest01.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);

        PdfWriter writer = new PdfWriter(file);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        FileInputStream is = new FileInputStream(sourceFolder+"Desert.jpg");

        nRead = is.read();
        while (nRead != -1){
            buffer.write(nRead);
            nRead = is.read();
        }

        PdfImageXObject xObject = new PdfImageXObject(pdfDoc, ImageFactory.getJpegImage(buffer.toByteArray()));
        Image image = new Image(xObject, 100);

        doc.add(new Paragraph(new Text("First Line")));
        Paragraph p = new Paragraph();
        p.add(image);
        doc.add(p);
        doc.add(new Paragraph(new Text("Second Line")));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest02() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest02.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);

        PdfWriter writer = new PdfWriter(file);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        FileInputStream is = new FileInputStream(sourceFolder+"Desert.jpg");

        nRead = is.read();
        while (nRead != -1){
            buffer.write(nRead);
            nRead = is.read();
        }

        PdfImageXObject xObject = new PdfImageXObject(pdfDoc, ImageFactory.getJpegImage(buffer.toByteArray()));
        Image image = new Image(xObject, 100);

        Paragraph p = new Paragraph();
        p.add(new Text("before image"));
        p.add(image);
        p.add(new Text("after image"));
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest03() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest03.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);

        PdfWriter writer = new PdfWriter(file);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        FileInputStream is = new FileInputStream(sourceFolder+"Desert.jpg");

        nRead = is.read();
        while (nRead != -1){
            buffer.write(nRead);
            nRead = is.read();
        }

        PdfImageXObject xObject = new PdfImageXObject(pdfDoc, ImageFactory.getJpegImage(buffer.toByteArray()));
        Image image = new Image(xObject, 100);

        doc.add(new Paragraph(new Text("First Line")));
        Paragraph p = new Paragraph();
        p.add(image);
        image.setRotateAngle(Math.PI/6);
        doc.add(p);
        doc.add(new Paragraph(new Text("Second Line")));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest04() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest04.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);

        PdfWriter writer = new PdfWriter(file);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        FileInputStream is = new FileInputStream(sourceFolder+"Desert.jpg");

        nRead = is.read();
        while (nRead != -1){
            buffer.write(nRead);
            nRead = is.read();
        }

        PdfImageXObject xObject = new PdfImageXObject(pdfDoc, ImageFactory.getJpegImage(buffer.toByteArray()));
        Image image = new Image(xObject, 100);

        Paragraph p = new Paragraph();
        p.add(new Text("before image"));
        p.add(image);
        image.setRotateAngle(Math.PI/6);
        p.add(new Text("after image"));
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest05() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest05.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest05.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);

        PdfWriter writer = new PdfWriter(file);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        FileInputStream is = new FileInputStream(sourceFolder+"Desert.jpg");

        nRead = is.read();
        while (nRead != -1){
            buffer.write(nRead);
            nRead = is.read();
        }

        PdfImageXObject xObject = new PdfImageXObject(pdfDoc, ImageFactory.getJpegImage(buffer.toByteArray()));
        Image image = new Image(xObject, 100);

        doc.add(new Paragraph(new Text("First Line")));
        Paragraph p = new Paragraph();
        p.add(image);
        image.scale(1, 0.5f);
        doc.add(p);
        doc.add(new Paragraph(new Text("Second Line")));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest06() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest06.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest06.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);

        PdfWriter writer = new PdfWriter(file);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        FileInputStream is = new FileInputStream(sourceFolder+"Desert.jpg");

        nRead = is.read();
        while (nRead != -1){
            buffer.write(nRead);
            nRead = is.read();
        }

        PdfImageXObject xObject = new PdfImageXObject(pdfDoc, ImageFactory.getJpegImage(buffer.toByteArray()));
        Image image = new Image(xObject, 100);

        doc.add(new Paragraph(new Text("First Line")));
        Paragraph p = new Paragraph();
        p.add(image);
        image.setTranslationDistance(100, -100);
        doc.add(p);
        doc.add(new Paragraph(new Text("Second Line")));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
