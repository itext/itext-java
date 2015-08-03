package com.itextpdf.model;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.model.element.Cell;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Table;
import org.junit.Assert;
import org.junit.BeforeClass;
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
    public void largeTableTest01() throws IOException, InterruptedException {
        String testName = "largeTableTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Table table = new Table(5, true);

        doc.add(table);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 5; j++) {
                table.addCell(new Cell().add(new Paragraph(String.format("Cell %s, %s", i + 1, j + 1))));
            }

            if (i % 10 == 0) {
                table.flush();

                // This is a deliberate additional flush.
                table.flush();
            }
        }

        table.complete();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableTest02() throws IOException, InterruptedException {
        String testName = "largeTableTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Table table = new Table(5, true).
                setMargins(20, 20, 20, 20);

        doc.add(table);
        for (int i = 0; i < 100; i++) {
            table.addCell(new Cell().add(new Paragraph(String.format("Cell %s", i + 1))));

            if (i % 7 == 0) {
                table.flush();
            }
        }

        table.complete();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

}
