package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
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
            @LogMessage(messageTemplate = LogMessageConstant.RECTANGLE_HAS_NEGATIVE_OR_ZERO_SIZES, count = 1)
    })
    public void emptyParagraphsTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "emptyParagraphsTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_emptyParagraphsTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph());
        // this line should not cause any effect
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
