package com.itextpdf.model;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.model.element.Cell;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Table;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LargeElementTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/LargeElementTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/LargeElementTest/";

    @BeforeClass
    static public void beforeClass() {
        File dest = new File(destinationFolder);
        dest.mkdirs();
        File[] files = dest.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    @Test
    @Ignore
    public void largeTableTest01() throws IOException, InterruptedException {
        String testName = "largeTableTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{65, 65, 65, 65, 65, 65, 65, 65}, true);

        for (int i = 0; i < 20000; i++) {
            for (int j = 0; j < 8; j++) {
                table.addCell(new Cell().add(new Paragraph(String.format("Cell %s, %s", i + 1, j + 1))));
            }
            doc.add(table);
            table.flush();
        }

        table.complete();
        doc.add(table);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @Ignore
    public void largeTableTest02() throws IOException, InterruptedException {
        String testName = "largeTableTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{65, 65, 65, 65, 65, 65, 65, 65}, false);

        for (int i = 0; i < 20000; i++) {
            for (int j = 0; j < 8; j++) {
                table.addCell(new Cell().add(new Paragraph(String.format("Cell %s, %s", i + 1, j + 1))));
            }
            //doc.add(table);
           // table.flush();
        }

        //table.complete();
        doc.add(table);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @Ignore
    public void largeTableMultipleDocumentsTest01() throws IOException, InterruptedException {
        String testName = "largeTableMultipleDocumentsTest01.pdf";
        String outFileName1 = destinationFolder + "largeTableMultipleDocumentsTest01_1.pdf";
        String outFileName2 = destinationFolder + "largeTableMultipleDocumentsTest01_2.pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName;

        Document doc1 = new Document(new PdfDocument(new PdfWriter(new FileOutputStream(outFileName1))));
        Document doc2 = new Document(new PdfDocument(new PdfWriter(new FileOutputStream(outFileName2))));

        doc1.setProperty(Property.FONT_SIZE, 10);
        doc2.setProperty(Property.FONT_SIZE, 15);

        Table table = new Table(4, true);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 4; j++) {
                table.addCell(new Cell().add(new Paragraph(String.format("This is a very good cell with very nice coordinates: (%s, %s)", i + 1, j + 1))));
            }
            doc1.add(table);
            doc2.add(table);
            table.flush();
        }

        table.complete();
        doc1.add(table);
        doc2.add(table);

        doc1.close();
        doc2.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName1, cmpFileName, destinationFolder, testName + "_diff"));
        Assert.assertNull(new CompareTool().compareByContent(outFileName2, cmpFileName, destinationFolder, testName + "_diff"));
    }

}
