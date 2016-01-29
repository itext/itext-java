package com.itextpdf.model;

import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.model.element.Cell;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Table;
import com.itextpdf.test.ExtendedITextTest;

import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class LargeElementTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/LargeElementTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/LargeElementTest/";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
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

    @Test
    public void largeTableWithHeaderFooterTest01A() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest01A.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        FileOutputStream fos = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(5, true);
        doc.add(table);

        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)"));
        table.addHeaderCell(cell);
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page"));
        table.addFooterCell(cell);
        table.setSkipFirstHeader(true);
        table.setSkipLastFooter(true);

        for (int i = 0; i < 350; i++) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i+1))));
            table.flush();
        }

        table.complete();

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest01B() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest01B.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        FileOutputStream fos = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(5, true);
        doc.add(table);

        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)"));
        table.addHeaderCell(cell);
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page"));
        table.addFooterCell(cell);
        table.setSkipFirstHeader(true);
        table.setSkipLastFooter(true);

        for (int i = 0; i < 350; i++) {
            table.flush();
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i+1))));
        }

        // That's the trick. complete() is called when table has non-empty content, so the last row is better laid out.
        // Compare with #largeTableWithHeaderFooterTest01A. When we flush last row before calling complete(), we don't yet know
        // if there will be any more rows. Flushing last row implicitly by calling complete solves this problem.
        table.complete();

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest02() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        FileOutputStream fos = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(5, true);
        doc.add(table);

        for (int i = 0; i < 5; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header1 \n" + i)));
        }
        for (int i = 0; i < 5; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header2 \n" + i)));
        }

        for (int i = 0; i < 500; i++) {
            if (i % 5 == 0) {
                table.flush();
            }
            table.addCell(new Cell().add(new Paragraph("Test " + i)));
        }

        table.complete();

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest03() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest03.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        FileOutputStream fos = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(5, true);
        doc.add(table);

        for (int i = 0; i < 5; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header \n" + i)));
        }
        for (int i = 0; i < 5; i++) {
            table.addFooterCell(new Cell().add(new Paragraph("Footer \n" + i)));
        }

        for (int i = 0; i < 500; i++) {
            if (i % 5 == 0) {
                table.flush();
            }
            table.addCell(new Cell().add(new Paragraph("Test " + i)));
        }

        table.complete();

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest04() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest04.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        FileOutputStream fos = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(5, true);
        doc.add(table);

        for (int i = 0; i < 5; i++) {
            table.addFooterCell(new Cell().add(new Paragraph("Footer \n" + i)));
        }

        for (int i = 0; i < 500; i++) {
            if (i % 5 == 0) {
                table.flush();
            }
            table.addCell(new Cell().add(new Paragraph("Test " + i)));
        }

        table.complete();

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

}
