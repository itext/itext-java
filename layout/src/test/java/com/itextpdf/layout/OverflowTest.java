package com.itextpdf.layout;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.test.ExtendedITextTest;


import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class OverflowTest extends ExtendedITextTest{

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/layout/OverflowTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/layout/OverflowTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void textOverflowTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            text.append("This is a waaaaay tooo long text...");
        }

        Paragraph p = new Paragraph(text.toString()).setFont(PdfFontFactory.createFont(FontConstants.HELVETICA));
        document.add(p);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textOverflowTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Text overflowText = new Text("This is a long-long and large text which will not overflow").
                setFontSize(19).setFontColor(Color.RED);
        Text followText = new Text("This is a text which follows overflowed text and will be wrapped");

        document.add(new Paragraph().add(overflowText).add(followText));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textOverflowTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Text overflowText = new Text("This is a long-long and large text which will overflow").
                setFontSize(25).setFontColor(Color.RED);
        Text followText = new Text("This is a text which follows overflowed text and will not be wrapped");

        document.add(new Paragraph().add(overflowText).add(followText));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textOverflowTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph("ThisIsALongTextWithNoSpacesSoSplittingShouldBeForcedInThisCase").setFontSize(20));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
