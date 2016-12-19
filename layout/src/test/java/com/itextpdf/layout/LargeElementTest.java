package com.itextpdf.layout;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.text.MessageFormat;

@Category(IntegrationTest.class)
public class LargeElementTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/LargeElementTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/LargeElementTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void largeTableTest01() throws IOException, InterruptedException {
        String testName = "largeTableTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(5, true);

        doc.add(table);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 5; j++) {
                table.addCell(new Cell().add(new Paragraph(MessageFormat.format("Cell {0}, {1}", i + 1, j + 1))));
            }

            if (i % 10 == 0) {
                table.flush();

                // This is a deliberate additional flush.
                table.flush();
            }
        }

        table.complete();
        doc.add(new Table(1).setBorder(new SolidBorder(Color.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableTest02() throws IOException, InterruptedException {
        String testName = "largeTableTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(5, true).
                setMargins(20, 20, 20, 20);

        doc.add(table);
        for (int i = 0; i < 100; i++) {
            table.addCell(new Cell().add(new Paragraph(MessageFormat.format("Cell {0}", i + 1))));

            if (i % 7 == 0) {
                table.flush();
            }
        }

        table.complete();
        doc.add(new Table(1).setBorder(new SolidBorder(Color.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest01A() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest01A.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
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
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
            table.flush();
        }

        table.complete();
        doc.add(new Table(1).setBorder(new SolidBorder(Color.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest01B() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest01B.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
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
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
        }

        // That's the trick. complete() is called when table has non-empty content, so the last row is better laid out.
        // Compare with #largeTableWithHeaderFooterTest01A. When we flush last row before calling complete(), we don't yet know
        // if there will be any more rows. Flushing last row implicitly by calling complete solves this problem.
        table.complete();
        doc.add(new Table(1).setBorder(new SolidBorder(Color.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest02() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
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
        doc.add(new Table(1).setBorder(new SolidBorder(Color.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest03() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest03.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
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
        doc.add(new Table(1).setBorder(new SolidBorder(Color.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest04() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest04.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
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
        doc.add(new Table(1).setBorder(new SolidBorder(Color.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

}
