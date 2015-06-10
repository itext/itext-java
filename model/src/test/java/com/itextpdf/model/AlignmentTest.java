package com.itextpdf.model;

import com.itextpdf.canvas.color.DeviceRgb;
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

public class AlignmentTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/AlignmentTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/AlignmentTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }


    @Test
    public void justifyAlignmentTest01() throws IOException, DocumentException, InterruptedException {
        String outFileName = destinationFolder + "justifyAlignmentTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_justifyAlignmentTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph().setAlignment(Property.Alignment.JUSTIFIED);
        for (int i = 0; i < 21; i++) {
            paragraph.add(new Text ("Hello World! Hello People! " +
                    "Hello Sky! Hello Sun! Hello Moon! Hello Stars!").setBackgroundColor(DeviceRgb.Red));
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

        Paragraph paragraph = new Paragraph().setAlignment(Property.Alignment.JUSTIFIED);
        paragraph.add(new Text("Hello World!")).add(new Text(" ")).add(new Text("Hello People! ")).add("End");
        document.add(paragraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
