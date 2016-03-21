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

import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class DefaultLayoutTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/layout/DefaultLayoutTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/layout/DefaultLayoutTest/";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void multipleAdditionsOfSameModelElementTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "multipleAdditionsOfSameModelElementTest1.pdf";
        String cmpFileName = sourceFolder + "cmp_multipleAdditionsOfSameModelElementTest1.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph("Hello. I am a paragraph. I want you to process me correctly");
        document.add(p).add(p).add(new AreaBreak(PageSize.Default)).add(p);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void rendererTest01() throws IOException,  InterruptedException {
        String outFileName = destinationFolder + "rendererTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_rendererTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

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
            @LogMessage(messageTemplate = LogMessageConstant.RECTANGLE_HAS_NEGATIVE_OR_ZERO_SIZES)
    })
    public void emptyParagraphsTest01() throws IOException,  InterruptedException {
        String outFileName = destinationFolder + "emptyParagraphsTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_emptyParagraphsTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        // the next 3 lines should not cause any effect
        document.add(new Paragraph());
        document.add(new Paragraph().setBackgroundColor(Color.GREEN));
        document.add(new Paragraph().setBorder(new SolidBorder(Color.BLUE, 3)));

        document.add(new Paragraph("Hello! I'm the first paragraph added to the document. Am i right?"));
        document.add(new Paragraph().setHeight(50));
        document.add(new Paragraph("Hello! I'm the second paragraph added to the document. Am i right?"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @Ignore
    public void emptyParagraphsTest02() throws IOException,  InterruptedException {
        String outFileName = destinationFolder + "emptyParagraphsTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_emptyParagraphsTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph("\n\n\n"));
        document.add(new Paragraph("a\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nb\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nc\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nd\n\n\n\n\n\n\n\n\n\n\n\n\n\n\ne"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @Ignore
    public void emptyParagraphsTest03() throws IOException,  InterruptedException {
        String outFileName = destinationFolder + "emptyParagraphsTest03.pdf";
//         String cmpFileName = sourceFolder + "cmp_emptyParagraphsTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph("c\nb"));
//        document.add(new Paragraph("a a\nb"));

        document.close();

//        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
