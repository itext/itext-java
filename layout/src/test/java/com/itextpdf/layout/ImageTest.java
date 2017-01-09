package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class ImageTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/ImageTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/ImageTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void imageTest01() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest01.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
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

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.createJpeg(UrlUtil.toURL(sourceFolder + "Desert.jpg")));
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

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
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

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
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

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
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

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
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

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));

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

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);


        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));

        Div div = new Div();
        div.add(image);
        div.add(image);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void imageTest09() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest09.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest09.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, new PageSize(500, 300));

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image.setWidthPercent(100);
        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest10() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest10.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest10.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, new PageSize(500, 300));

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image.setAutoScale(true);
        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest11() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest11.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest11.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image.setAutoScale(true);
        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest12_HorizontalAlignment_CENTER() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest12.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest12.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);

        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest13_HorizontalAlignment_RIGHT() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest13.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest13.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        image.setHorizontalAlignment(HorizontalAlignment.RIGHT);

        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest14_HorizontalAlignment_LEFT() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest14.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest14.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        image.setHorizontalAlignment(HorizontalAlignment.LEFT);

        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void imageTest15() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest15.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest15.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));

        image.setBorder(new SolidBorder(Color.BLUE, 5));
        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test()
    public void imageTest16() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest16.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest16.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));

        image.setBorder(new SolidBorder(Color.BLUE, 5));
        image.setAutoScale(true);
        image.setRotationAngle(Math.PI / 2);
        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test()
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 50)
    })
    public void imageTest17() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest17.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest17.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image1 = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image1.setBorder(new SolidBorder(Color.BLUE, 5));
        Image image2 = new Image(ImageDataFactory.create(sourceFolder + "scarf.jpg"));
        image2.setBorder(new SolidBorder(Color.BLUE, 5));

        for (int i = 0; i <= 24; i++) {
            image1.setRotationAngle(i * Math.PI / 12);
            image2.setRotationAngle(i * Math.PI / 12);
            doc.add(image1);
            doc.add(image2);
        }

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    /**
     * Image can be reused in layout, so flushing it on the very first draw is a bad thing.
     */
    @Test
    public void flushOnDrawTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flushOnDrawTest.pdf";
        String cmpFileName = sourceFolder + "cmp_flushOnDrawTest.pdf";

        int rowCount = 60;
        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);
        Image img = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Table table = new Table(8);
        table.setWidthPercent(100);
        for (int k = 0; k < rowCount; k++) {
            for (int j = 0; j < 7; j++) {
                table.addCell("Hello");
            }
            Cell c = new Cell().add(img.setWidthPercent(50));
            table.addCell(c);
        }
        document.add(table);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    /**
     * If an image is flushed automatically on draw, we will later check it for circular references
     * as it is an XObject. This is a test for {@link NullPointerException} that was caused by getting
     * a value from flushed image.
     */
    @Test
    public void flushOnDrawCheckCircularReferencesTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flushOnDrawCheckCircularReferencesTest.pdf";
        String cmpFileName = sourceFolder + "cmp_flushOnDrawCheckCircularReferencesTest.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        //Initialize document
        Document document = new Document(pdf);

        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        img.setAutoScale(true);
        Table table = new Table(4);
        table.setWidthPercent(100);
        for (int k = 0; k < 5; k++) {
            table.addCell("Hello World from iText7");

            List list = new List().setListSymbol("-> ");
            list.add("list item").add("list item").add("list item").add("list item").add("list item");
            Cell cell = new Cell().add(list);
            table.addCell(cell);

            Cell c = new Cell().add(img);
            table.addCell(c);

            Table innerTable = new Table(3);
            int j = 0;
            while (j < 9) {
                innerTable.addCell("Hi");
                j++;
            }
            table.addCell(innerTable);
        }
        document.add(table);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageWithBordersSurroundedByTextTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageBordersTextTest.pdf";
        String cmpFileName = sourceFolder + "cmp_imageBordersTextTest.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100);

        Paragraph p = new Paragraph();
        p.setBorder(new SolidBorder(Color.GREEN, 5));
        p.add(new Text("before image"));
        p.add(image);
        image.setBorder(new SolidBorder(Color.BLUE, 5));
        p.add(new Text("after image"));
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageInParagraphBorderTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageParagraphBorderTest.pdf";
        String cmpFileName = sourceFolder + "cmp_imageParagraphBorderTest.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100);

        Paragraph p = new Paragraph();
        p.setBorder(new SolidBorder(Color.GREEN, 5));
        p.add(image);
        image.setBorder(new SolidBorder(Color.BLUE, 5));
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
    
    @Test
    @Ignore("DEVSIX-1022")
    public void imageRelativePositionTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageRelativePositionTest.pdf";
        String cmpFileName = sourceFolder + "cmp_imageRelativePositionTest.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100).setRelativePosition(30, 30, 0, 0);

        Paragraph p = new Paragraph();
        p.setBorder(new SolidBorder(Color.GREEN, 5));
        p.add(image);
        image.setBorder(new SolidBorder(Color.BLUE, 5));
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
