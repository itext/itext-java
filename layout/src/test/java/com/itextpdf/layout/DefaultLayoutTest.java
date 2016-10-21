package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class DefaultLayoutTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/DefaultLayoutTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/DefaultLayoutTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void multipleAdditionsOfSameModelElementTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "multipleAdditionsOfSameModelElementTest1.pdf";
        String cmpFileName = sourceFolder + "cmp_multipleAdditionsOfSameModelElementTest1.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph("Hello. I am a paragraph. I want you to process me correctly");
        document.add(p).add(p).add(new AreaBreak(PageSize.Default)).add(p);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void rendererTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "rendererTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_rendererTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        document.add(new Paragraph(new Text(str).setBackgroundColor(Color.RED)).setBackgroundColor(Color.GREEN)).
                add(new Paragraph(str)).
                add(new AreaBreak(PageSize.Default)).
                add(new Paragraph(str));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.RECTANGLE_HAS_NEGATIVE_OR_ZERO_SIZES, count = 2)
    })
    public void emptyParagraphsTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "emptyParagraphsTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_emptyParagraphsTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        // the next 3 lines should not cause any effect
        document.add(new Paragraph());
        document.add(new Paragraph().setBackgroundColor(Color.GREEN));
        document.add(new Paragraph().setBorder(new SolidBorder(Color.BLUE, 3)));

        document.add(new Paragraph("Hello! I'm the first paragraph added to the document. Am i right?").setBackgroundColor(Color.RED).setBorder(new SolidBorder(1)));
        document.add(new Paragraph().setHeight(50));
        document.add(new Paragraph("Hello! I'm the second paragraph added to the document. Am i right?"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void emptyParagraphsTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "emptyParagraphsTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_emptyParagraphsTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph("Hello, i'm the text of the first paragraph on the first line. Let's break me and meet on the next line!\nSee? I'm on the second line. Now let's create some empty lines,\n for example one\n\nor two\n\n\nor three\n\n\n\nNow let's do something else"));
        document.add(new Paragraph("\n\n\nLook, i'm the the text of the second paragraph. But before me and the first one there are three empty lines!"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void heightsTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "heightTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_heightTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Table table = new Table(1);
        table.addCell(new Cell().add("b").setHeight(900));

        table.setBorder(new SolidBorder(Color.RED, 3));
        doc.add(table);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
    public void heightTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "heightTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_heightTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        String textByron =
                "When a man hath no freedom to fight for at home,\n" +
                "    Let him combat for that of his neighbours;\n" +
                "Let him think of the glories of Greece and of Rome,\n" +
                "    And get knocked on the head for his labours.\n" +
                "\n" +
                "To do good to Mankind is the chivalrous plan,\n" +
                "    And is always as nobly requited;\n" +
                "Then battle for Freedom wherever you can,\n" +
                "    And, if not shot or hanged, you'll get knighted.";

        String textHelloWorld =
                "Hello World\n" +
                "Hello World\n" +
                "Hello World\n" +
                "Hello World\n" +
                "Hello World\n";


        Document doc = new Document(pdfDocument);

        List list = new List(ListNumberingType.DECIMAL);
        for(int i = 0; i < 10; i++) {
            list.add(new ListItem(""+i));
        }
        list.setHeight(60);
        list.setBorder(new SolidBorder(0.5f));
        //list.setPaddingTop(100); // TODO
        doc.add(list);
        doc.add(new AreaBreak());

        doc.add(list);
        doc.add(new AreaBreak());

        Paragraph p = new Paragraph(textByron);
        //for (int i = 0; i < 15; i++) {
            p.add(textByron);
        //}
        p.setBorder(new SolidBorder(0.5f));
        p.setHeight(1000);
        doc.add(p);
        doc.add(new AreaBreak());

        doc.add(p);
        doc.add(new AreaBreak());

        p.setBorder(Border.NO_BORDER);
        Div div = new Div();
        div.setBorder(new SolidBorder(0.5f));
        for (int i = 0; i < 5; i++) {
            div.add(p);
        }
        div.setHeight(1000);
        doc.add(div);
        doc.add(new AreaBreak());

        doc.add(div);
        doc.add(new AreaBreak());

        Table table = new Table(2);
        table.setBorder(new SolidBorder(Color.RED, 2f));
//        table.addCell(new Cell(2, 1).add(new Paragraph(textHelloWorld)));
        for (int i = 0; i < 4; i++) {
            table.addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(Color.YELLOW, 1)));
        }
//        table.addCell(new Cell(1, 2).add(textByron));

        table.setHeight(1700);
        doc.add(table);
        doc.add(new Paragraph("Hello"));
        doc.add(new AreaBreak());

        doc.add(table);
        doc.add(new Paragraph("Hello"));
        doc.add(new AreaBreak());


        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100);
        image.setMaxHeight(100);
        doc.add(image);
        doc.add(new AreaBreak());

        doc.add(image);
        doc.add(new AreaBreak());

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(count = 2, messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void addParagraphOnShortPage1() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "addParagraphOnShortPage1.pdf";
        String cmpFileName = sourceFolder + "cmp_addParagraphOnShortPage1.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(500, 70));

        Paragraph p = new Paragraph();
        p.add("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        p.add(new Text("BBB").setFontSize(30));
        p.add("CCC");
        p.add("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
        p.add("EEE");

        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void addParagraphOnShortPage2() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "addParagraphOnShortPage2.pdf";
        String cmpFileName = sourceFolder + "cmp_addParagraphOnShortPage2.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(300, 50));

        Paragraph p = new Paragraph();
        p.add("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        doc.add(p);


        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
