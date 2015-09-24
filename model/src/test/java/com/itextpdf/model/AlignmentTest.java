package com.itextpdf.model;

import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.canvas.color.DeviceRgb;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.model.border.SolidBorder;
import com.itextpdf.model.element.*;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Category(IntegrationTest.class)
public class AlignmentTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/AlignmentTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/AlignmentTest/";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
    }


    @Test
    public void justifyAlignmentTest01() throws IOException, DocumentException, InterruptedException {
        String outFileName = destinationFolder + "justifyAlignmentTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_justifyAlignmentTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph().setHorizontalAlignment(Property.HorizontalAlignment.JUSTIFIED);
        for (int i = 0; i < 21; i++) {
            paragraph.add(new Text ("Hello World! Hello People! " +
                    "Hello Sky! Hello Sun! Hello Moon! Hello Stars!").setBackgroundColor(DeviceRgb.RED));
        }
        document.add(paragraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void justifyAlignmentTest02() throws IOException, DocumentException, InterruptedException {
        String outFileName = destinationFolder + "justifyAlignmentTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_justifyAlignmentTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph().setHorizontalAlignment(Property.HorizontalAlignment.JUSTIFIED);
        paragraph.add(new Text("Hello World!")).add(new Text(" ")).add(new Text("Hello People! ")).add("End");
        document.add(paragraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void justifyAlignmentTest03() throws IOException, DocumentException, InterruptedException {
        String outFileName = destinationFolder + "justifyAlignmentTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_justifyAlignmentTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph().setHorizontalAlignment(Property.HorizontalAlignment.JUSTIFIED);
        for (int i = 0; i < 21; i++) {
            paragraph.add(new Text("Hello World! Hello People! " +
                    "Hello Sky! Hello Sun! Hello Moon! Hello Stars!").setBorder(new SolidBorder(Color.GREEN, 0.1f))).setMultipliedLeading(1);
        }
        document.add(paragraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void justifyAlignmentTest04() throws IOException, DocumentException, InterruptedException {
        String outFileName = destinationFolder + "justifyAlignmentTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_justifyAlignmentTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph().setHorizontalAlignment(Property.HorizontalAlignment.JUSTIFIED);
        for (int i = 0; i < 21; i++) {
            paragraph.add(new Text("Hello World! Hello People! " +
                    "Hello Sky! Hello Sun! Hello Moon! Hello Stars!")).setFixedLeading(24);
        }
        document.add(paragraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void justifyAlignmentForcedNewlinesTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "justifyAlignmentForcedNewlinesTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_justifyAlignmentForcedNewlinesTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph().setHorizontalAlignment(Property.HorizontalAlignment.JUSTIFIED);
        paragraph.add("Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document.\n" +
                "To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries.\n" +
                "Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme.\n" +
                "Save time in Word with new buttons that show up where you need them. To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign.\n" +
                "Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device.\n");
        document.add(paragraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void justifyAllTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "justifyAllTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_justifyAllTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph().setHorizontalAlignment(Property.HorizontalAlignment.JUSTIFIED_ALL);
        paragraph.add("Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document.\n" +
                "To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries.\n" +
                "Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme.\n" +
                "Save time in Word with new buttons that show up where you need them. To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign.\n" +
                "Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device.\n");
        document.add(paragraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void blockAlignmentTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "blockAlignmentTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_blockAlignmentTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        List list = new List(Property.ListNumberingType.GREEK_LOWER);
        for (int i = 0; i < 10; i++) {
            list.add("Item # " + (i + 1));
        }
        list.setWidth(250);
        list.setHorizontalAlignment(Property.HorizontalAlignment.CENTER);
        list.setBackgroundColor(Color.GREEN);

        document.add(list);
        list.setHorizontalAlignment(Property.HorizontalAlignment.RIGHT).setBackgroundColor(Color.RED);
        document.add(list);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void blockAlignmentTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "blockAlignmentTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_blockAlignmentTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Div div = new Div();
        PdfImageXObject xObject = new PdfImageXObject(ImageFactory.getJpegImage(new File(sourceFolder + "Desert.jpg").toURI().toURL()));
        Image image1 = new Image(xObject, 100).setHorizontalAlignment(Property.HorizontalAlignment.RIGHT);
        Image image2 = new Image(xObject, 100).setHorizontalAlignment(Property.HorizontalAlignment.CENTER);
        Image image3 = new Image(xObject, 100).setHorizontalAlignment(Property.HorizontalAlignment.LEFT);

        div.add(image1).add(image2).add(image3);

        document.add(div);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageAlignmentTest01() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageAlignmentTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_imageAlignmentTest01.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);

        PdfWriter writer = new PdfWriter(file);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageFactory.getJpegImage(new File(sourceFolder + "Desert.jpg").toURI().toURL()));
        Image image = new Image(xObject, 100).setHorizontalAlignment(Property.HorizontalAlignment.RIGHT);

        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
