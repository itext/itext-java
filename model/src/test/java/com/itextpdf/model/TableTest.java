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

public class TableTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/TableTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/TableTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void simpleTableTest1() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tableTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_tableTest01.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[] {50, 50})
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("cell 1, 2")));
        doc.add(table);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void simpleTableTest2() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tableTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_tableTest02.pdf";

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[] {50, 50})
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("cell 1, 2")))
                .addCell(new Cell().add(new Paragraph("cell 2, 1")))
                .addCell(new Cell().add(new Paragraph("cell 2, 2")));
        doc.add(table);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
