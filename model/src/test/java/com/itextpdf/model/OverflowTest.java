package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Text;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class OverflowTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/OverflowTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/OverflowTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void textOverflowTest01() throws IOException, PdfException, InterruptedException, DocumentException {
        String outFileName = destinationFolder + "textOverflowTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            text.append("This is a waaaaay tooo long text...");
        }

        Paragraph p = new Paragraph(text.toString()).setFont(new PdfType1Font(pdfDocument, new Type1Font(FontConstants.HELVETICA, "")));
        document.add(p);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textOverflowTest02() throws IOException, PdfException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Text overflowText = new Text("This is a long-long and large text which will not overflow").
                setFontSize(19).setFontColor(Color.Red);
        Text followText = new Text("This is a text which follows overflowed text and will be wrapped");

        document.add(new Paragraph().add(overflowText).add(followText));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textOverflowTest03() throws IOException, PdfException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Text overflowText = new Text("This is a long-long and large text which will overflow").
                setFontSize(25).setFontColor(Color.Red);
        Text followText = new Text("This is a text which follows overflowed text and will not be wrapped");

        document.add(new Paragraph().add(overflowText).add(followText));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textOverflowTest04() throws IOException, PdfException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph("ThisIsALongTextWithNoSpacesSoSplittingShouldBeForcedInThisCase").setFontSize(20));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
