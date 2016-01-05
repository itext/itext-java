package com.itextpdf.model;

import com.itextpdf.basics.LogMessageConstant;
import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.model.element.Div;
import com.itextpdf.model.element.Image;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Text;
import com.itextpdf.test.ExtendedITextTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class ImageTest extends ExtendedITextTest{

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/ImageTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/ImageTest/";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void imageTest01() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest01.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);

        PdfWriter writer = new PdfWriter(file);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageFactory.getImage(sourceFolder + "Desert.jpg"));
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

        PdfImageXObject xObject = new PdfImageXObject(ImageFactory.getJpegImage(new File(sourceFolder+"Desert.jpg").toURI().toURL()));
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

        PdfImageXObject xObject = new PdfImageXObject(ImageFactory.getImage(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100);

        doc.add(new Paragraph(new Text("First Line")));
        Paragraph p = new Paragraph();
        p.add(image);
        image.setRotationAngle(Math.PI / 6);
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

        PdfImageXObject xObject = new PdfImageXObject(ImageFactory.getImage(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100);

        Paragraph p = new Paragraph();
        p.add(new Text("before image"));
        p.add(image);
        image.setRotationAngle(Math.PI / 6);
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

        PdfImageXObject xObject = new PdfImageXObject(ImageFactory.getImage(sourceFolder + "Desert.jpg"));
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

        PdfImageXObject xObject = new PdfImageXObject(ImageFactory.getImage(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100);

        doc.add(new Paragraph(new Text("First Line")));
        Paragraph p = new Paragraph();
        p.add(image);
        image.setMarginLeft(100).setMarginTop(100);
        doc.add(p);
        doc.add(new Paragraph(new Text("Second Line")));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void imageTest07() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest07.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest07.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);


        Image image = new Image(ImageFactory.getImage(sourceFolder + "Desert.jpg"));

        Div div = new Div();
        div.add(image);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void imageTest08() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest08.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest08.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);


        Image image = new Image(ImageFactory.getImage(sourceFolder + "Desert.jpg"));

        Div div = new Div();
        div.add(image);
        div.add(image);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
