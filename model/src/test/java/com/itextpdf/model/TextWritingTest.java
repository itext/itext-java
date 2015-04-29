package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.font.PdfFont;
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

public class TextWritingTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/TextWritingTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/TextWritingTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void textRiseTest01() throws IOException, PdfException, DocumentException, InterruptedException {
        // CountryChunks example
        String outFileName = destinationFolder + "textRiseTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_textRiseTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        PdfFont font = new PdfType1Font(pdfDocument, new Type1Font(FontConstants.HELVETICA_BOLD, ""));
        for (int i = 0; i < 10; i++) {
            Paragraph p = new Paragraph().add("country").add(" ");
            Text id = new Text("id").
                    setTextRise(6).
                    setFont(font).
                    setFontSize(6).
                    setFontColor(Color.White).
                    setBackgroundColor(Color.Black, 0,0,0,0);
            p.add(id);
            document.add(p);
        }

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textRenderingModeTest01() throws IOException, PdfException, InterruptedException, DocumentException {
        String outFileName = destinationFolder + "textRenderingModeTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_textRenderingModeTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Text text1 = new Text("This is a fill and stroke text").
                setTextRenderingMode(Property.TextRenderingMode.TEXT_RENDERING_MODE_FILL_STROKE).
                setStrokeColor(Color.Red).
                setStrokeWidth(0.1f);
        document.add(new Paragraph().add(text1));

        Text text2 = new Text("This is a stroke-only text").
                setTextRenderingMode(Property.TextRenderingMode.TEXT_RENDERING_MODE_STROKE).
                setStrokeColor(Color.Green).
                setStrokeWidth(0.3f);
        document.add(new Paragraph(text2));

        Text text3 = new Text("This is a colorful text").
                setTextRenderingMode(Property.TextRenderingMode.TEXT_RENDERING_MODE_FILL_STROKE).
                setStrokeColor(Color.Blue).
                setStrokeWidth(0.3f).
                setFontColor(Color.Green).
                setFontSize(20);
        document.add(new Paragraph(text3));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void leadingTest01() throws IOException, PdfException, InterruptedException {
        String outFileName = destinationFolder + "leadingTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_leadingTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph p1 = new Paragraph("first, leading of 150")
                .setFixedLeading(150);
        document.add(p1);

        Paragraph p2 = new Paragraph("second, leading of 500")
                .setFixedLeading(500);
        document.add(p2);

        Paragraph p3 = new Paragraph();
        p3.add(new Text("third, leading of 20"))
                .setFixedLeading(20);
        document.add(p3);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
