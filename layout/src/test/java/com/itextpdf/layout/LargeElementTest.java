/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.BorderCollapsePropertyValue;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class LargeElementTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/LargeElementTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/LargeElementTest/";

    @BeforeAll
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

        Table table = new Table(UnitValue.createPercentArray(5), true);

        doc.add(table);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 5; j++) {
                table.addCell(new Cell().add(new Paragraph(MessageFormatUtil.format("Cell {0}, {1}", i + 1, j + 1))));
            }

            if (i % 10 == 0) {
                table.flush();

                // This is a deliberate additional flush.
                table.flush();
            }
        }

        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableTest02() throws IOException, InterruptedException {
        String testName = "largeTableTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(5), true).
                setMargins(20, 20, 20, 20);

        doc.add(table);
        for (int i = 0; i < 100; i++) {
            table.addCell(new Cell().add(new Paragraph(MessageFormatUtil.format("Cell {0}", i + 1))));

            if (i % 7 == 0) {
                table.flush();
            }
        }

        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE)
    })
    public void largeTableWithEmptyLastRowTest() throws IOException, InterruptedException {
        String testName = "largeTableWithEmptyLastRowTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(5), true);

        doc.add(table);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 5; j++) {
                table.addCell(new Cell().add(new Paragraph(MessageFormatUtil.format("Cell {0}, {1}", i + 1, j + 1))));
            }
            if (i % 10 == 0) {
                table.flush();
            }
        }
        table.startNewRow();
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

   @Test
    //TODO DEVSIX-6025 Unexpected NPE, when trying to flush after starting new row
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 2)
    })
    public void flushingLargeTableAfterStartingNewRowTest() throws IOException, InterruptedException {
        String testName = "flushingLargeTableAfterStartingNewRowTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(5), true);

        doc.add(table);

        table.addCell(new Cell().add(new Paragraph("Hello")));
        table.addCell(new Cell().add(new Paragraph("World")));
        table.startNewRow();
        Assertions.assertThrows(NullPointerException.class, () -> table.flush());
        table.complete();
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE)
    })
    public void largeTableWithCollapsedFooterTest() throws IOException, InterruptedException {
        String testName = "largeTableWithCollapsedFooterTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(5), true);

        doc.add(table);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 5; j++) {
                table.addCell(new Cell().add(new Paragraph(MessageFormatUtil.format("Cell {0}, {1}", i + 1, j + 1))));
            }
            if (i % 10 == 0) {
                table.flush();
            }
        }
        table.startNewRow();
        Cell cell = new Cell(1, 5).add(new Paragraph("Collapsed footer"));
        table.addFooterCell(cell);
        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest01A() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest01A.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(UnitValue.createPercentArray(5), true);
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
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest01ASeparated() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest01ASeparated.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(UnitValue.createPercentArray(5), true);
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20f);
        table.setVerticalBorderSpacing(20f);
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
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }


    @Test
    public void largeTableWithHeaderFooterTest01B() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest01B.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(UnitValue.createPercentArray(5), true);
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
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    // TODO DEVSIX-5868 Look at page 2: large table's vertical borders are shorter in length than expected
    public void largeTableWithHeaderFooterTest01C() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest01C.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A6.rotate());
        Table table = new Table(UnitValue.createPercentArray(5), true);
        doc.add(table);
        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)")).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.MAGENTA, 20));
        table.addHeaderCell(cell);
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page")).setHeight(30).setBorderTop(new SolidBorder(ColorConstants.MAGENTA, 20));
        table.addFooterCell(cell);
        for (int i = 0; i < 50; i++) {
            table.addCell(new Cell().setBorderLeft(new SolidBorder(ColorConstants.BLUE, 0.5f)).setBorderRight(new SolidBorder(ColorConstants.BLUE, 0.5f)).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 2 * i + 1 > 50 ? 50 : 2 * i + 1)).setBorderTop(new SolidBorder(ColorConstants.GREEN, (50 - 2 * i + 1 >= 0) ? 50 - 2 * i + 1 : 0)).add(new Paragraph(String.valueOf(i + 1))));
            table.flush();
        }
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @Disabled("DEVSIX-1778")
    public void largeTableWithHeaderFooterTest01CForcedPlacement() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest01CForcedPlacement.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A6.rotate());

        // separate
        Table table = new Table(UnitValue.createPercentArray(5), true);
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20f);
        table.setVerticalBorderSpacing(20f);
        doc.add(table);
        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)")).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.MAGENTA, 20));
        table.addHeaderCell(cell);
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page")).setHeight(30).setBorderTop(new SolidBorder(ColorConstants.MAGENTA, 20));
        table.addFooterCell(cell);
        for (int i = 0; i < 50; i++) {
            table.addCell(new Cell().setBorderLeft(new SolidBorder(ColorConstants.BLUE, 0.5f)).setBorderRight(new SolidBorder(ColorConstants.BLUE, 0.5f)).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 2 * i + 1 > 50 ? 50 : 2 * i + 1)).setBorderTop(new SolidBorder(ColorConstants.GREEN, (50 - 2 * i + 1 >= 0) ? 50 - 2 * i + 1 : 0)).add(new Paragraph(String.valueOf(i + 1))));
            table.flush();
        }
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        pdfDoc.setDefaultPageSize(new PageSize(420, 208));
        doc.add(new AreaBreak());

        // collapse
        table = new Table(UnitValue.createPercentArray(5), true);
        doc.add(table);
        cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)")).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.MAGENTA, 20));
        table.addHeaderCell(cell);
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page")).setHeight(30).setBorderTop(new SolidBorder(ColorConstants.MAGENTA, 20));
        table.addFooterCell(cell);
        for (int i = 0; i < 50; i++) {
            table.addCell(new Cell().setBorderLeft(new SolidBorder(ColorConstants.BLUE, 0.5f)).setBorderRight(new SolidBorder(ColorConstants.BLUE, 0.5f)).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 2 * i + 1 > 50 ? 50 : 2 * i + 1)).setBorderTop(new SolidBorder(ColorConstants.GREEN, (50 - 2 * i + 1 >= 0) ? 50 - 2 * i + 1 : 0)).add(new Paragraph(String.valueOf(i + 1))));
            table.flush();
        }
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));


        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest01D() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest01D.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A6.rotate());
        Table table = new Table(UnitValue.createPercentArray(5), true);
        table.setSkipLastFooter(true);
        table.setSkipFirstHeader(true);
        doc.add(table);
        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)")).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.MAGENTA, 20));
        table.addHeaderCell(cell);
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page")).setHeight(30).setBorderTop(new SolidBorder(ColorConstants.MAGENTA, 20));
        table.addFooterCell(cell);
        for (int i = 0; i < 50; i++) {
            table.addCell(new Cell().setBorderLeft(new SolidBorder(ColorConstants.BLUE, 0.5f)).setBorderRight(new SolidBorder(ColorConstants.BLUE, 0.5f)).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 2 * i + 1 > 50 ? 50 : 2 * i + 1)).setBorderTop(new SolidBorder(ColorConstants.GREEN, (50 - 2 * i + 1 >= 0) ? 50 - 2 * i + 1 : 0)).add(new Paragraph(String.valueOf(i + 1))));
            table.flush();
        }
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest01DSeparated() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest01DSeparated.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A6.rotate());
        Table table = new Table(UnitValue.createPercentArray(5), true);
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setSkipLastFooter(true);
        table.setSkipFirstHeader(true);
        doc.add(table);
        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)")).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.MAGENTA, 20));
        table.addHeaderCell(cell);
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page")).setHeight(30).setBorderTop(new SolidBorder(ColorConstants.MAGENTA, 20));
        table.addFooterCell(cell);
        for (int i = 0; i < 50; i++) {
            table.addCell(new Cell().setBorderLeft(new SolidBorder(ColorConstants.BLUE, 0.5f)).setBorderRight(new SolidBorder(ColorConstants.BLUE, 0.5f)).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 2 * i + 1 > 50 ? 50 : 2 * i + 1)).setBorderTop(new SolidBorder(ColorConstants.GREEN, (50 - 2 * i + 1 >= 0) ? 50 - 2 * i + 1 : 0)).add(new Paragraph(String.valueOf(i + 1))));
            table.flush();
        }
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest01E() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest01E.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(UnitValue.createPercentArray(5), true);

        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)"));
        table.addHeaderCell(cell);
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page"));
        table.addFooterCell(cell);
        table.setSkipFirstHeader(true);
        table.setSkipLastFooter(true);

        for (int i = 0; i < 350; i++) {
            if (i % 10 == 0) {
                doc.add(table);
            }
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
        }

        // That's the trick. complete() is called when table has non-empty content, so the last row is better laid out.
        // Compare with #largeTableWithHeaderFooterTest01A. When we flush last row before calling complete(), we don't yet know
        // if there will be any more rows. Flushing last row implicitly by calling complete solves this problem.
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest01ESeparated() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest01ESeparated.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(UnitValue.createPercentArray(5), true);
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20f);
        table.setVerticalBorderSpacing(20f);

        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)"));
        table.addHeaderCell(cell);
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page"));
        table.addFooterCell(cell);
        table.setSkipFirstHeader(true);
        table.setSkipLastFooter(true);

        for (int i = 0; i < 350; i++) {
            if (i % 10 == 0) {
                doc.add(table);
            }
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
        }

        // That's the trick. complete() is called when table has non-empty content, so the last row is better laid out.
        // Compare with #largeTableWithHeaderFooterTest01A. When we flush last row before calling complete(), we don't yet know
        // if there will be any more rows. Flushing last row implicitly by calling complete solves this problem.
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest02() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(UnitValue.createPercentArray(5), true);
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
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest03() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest03.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(UnitValue.createPercentArray(5), true);
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
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithHeaderFooterTest04() throws IOException, InterruptedException {
        String testName = "largeTableWithHeaderFooterTest04.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(UnitValue.createPercentArray(5), true);
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
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithLayoutResultNothingTest01() throws IOException, InterruptedException {
        String testName = "largeTableWithLayoutResultNothingTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A1.rotate());

        float[] colWidths = new float[]{300, 150, 50, 100};

        // the second column has colspan value as 2
        int numOfColumns = colWidths.length - 1;
        int numOfRowsInARowGroup = 4;
        int[] widthsArray = {10, 50, 1, 100};

        // please also look at tableWithLayoutResultNothingTest01
        Table table = new Table(UnitValue.createPointArray(colWidths), true);
        doc.add(table);

        Cell cell;
        for (int k = 0; k < widthsArray.length; k++) {
            for (int j = 0; j < numOfRowsInARowGroup; j++) {
                for (int i = 0; i < numOfColumns; i++) {
                    cell = new Cell(1, 1 + i % 2).add(new Paragraph("Cell" + i));
                    cell.setBorder(new SolidBorder(new DeviceGray(i / (float) numOfColumns), widthsArray[k]));
                    table.addCell(cell);
                }
            }
            table.flush();
        }
        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableWithLayoutResultNothingTest01() throws IOException, InterruptedException {
        String testName = "tableWithLayoutResultNothingTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A1.rotate());

        float[] colWidths = new float[]{300, 150, 50, 100};
        int numOfColumns = colWidths.length - 1;
        int numOfRowsInARowGroup = 4;
        int[] widthsArray = {10, 50, 1, 100};

        // please also look at largeTableWithLayoutResultNothingTest01
        Table table = new Table(UnitValue.createPointArray(colWidths), false);
        table.setWidth(UnitValue.createPercentValue(100));
        table.setFixedLayout();

        Cell cell;
        for (int k = 0; k < widthsArray.length; k++) {
            for (int j = 0; j < numOfRowsInARowGroup; j++) {
                for (int i = 0; i < numOfColumns; i++) {
                    cell = new Cell(1, 1 + i % 2).add(new Paragraph("Cell" + i));
                    cell.setBorder(new SolidBorder(new DeviceGray(i / (float) numOfColumns), widthsArray[k]));
                    table.addCell(cell);
                }
            }
        }
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }


    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)})
    public void largeTableWithLayoutResultNothingTest02() throws IOException, InterruptedException {
        String testName = "largeTableWithLayoutResultNothingTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        float[] colWidths = new float[]{200, 1, 2, 4};

        Table table = new Table(UnitValue.createPointArray(colWidths), true);
        doc.add(table);

        Cell cell1 = new Cell().add(new Paragraph("Cell1"));
        Cell cell2 = new Cell().add(new Paragraph("Cell2"));
        Cell cell3 = new Cell().add(new Paragraph("Cell3"));
        Cell cell4 = new Cell().add(new Paragraph("Cell4"));

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.flush();

        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithLayoutResultNothingTest03() throws IOException, InterruptedException {
        String testName = "largeTableWithLayoutResultNothingTest03.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        float[] colWidths = new float[]{200, -1, 20, 40};

        Table table = new Table(UnitValue.createPointArray(colWidths), true);
        doc.add(table);

        Cell cell1 = new Cell().add(new Paragraph("Cell1"));
        Cell cell2 = new Cell().add(new Paragraph("Cell2"));
        Cell cell3 = new Cell().add(new Paragraph("Cell3"));
        Cell cell4 = new Cell().add(new Paragraph("Cell4"));

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.flush();

        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    private static class DifferentPagesDocumentRenderer extends DocumentRenderer {
        private int pageNum = 0;

        public DifferentPagesDocumentRenderer(Document document) {
            super(document);
        }

        @Override
        protected PageSize addNewPage(PageSize customPageSize) {
            PageSize newPageSize = null;
            switch (pageNum) {
                case 0:
                    newPageSize = PageSize.A4.rotate();
                    break;
                case 1:
                    newPageSize = PageSize.A3.rotate();
                    break;
                case 2:
                default:
                    newPageSize = PageSize.A5.rotate();
                    break;
            }
            return super.addNewPage(newPageSize);
        }

        @Override
        protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
            if (null != overflowResult && null != overflowResult.getOccupiedArea()) {
                pageNum = overflowResult.getOccupiedArea().getPageNumber();
            }
            return super.updateCurrentArea(overflowResult);
        }
    }

    @Test
    public void largeTableSplitTest01() throws IOException, InterruptedException {
        String testName = "largeTableSplitTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;
        largeTableSplitTest(outFileName, 100, 1, false, false);
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableSplitSeparateTest() throws IOException, InterruptedException {
        String testName = "largeTableSplitSeparateTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;
        largeTableSplitTest(outFileName, 100, 1, false, true);
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableSplitFooterTest() throws IOException, InterruptedException {
        String testName = "largeTableSplitFooterTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;
        largeTableSplitTest(outFileName, 280, 6, true, false);
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    private void largeTableSplitTest(String outFileName, float pageHeight, float rowsNumber, boolean addFooter, boolean separate) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(595, pageHeight));

        float[] colWidths = new float[]{200, -1, 20, 40};

        Table table = new Table(UnitValue.createPointArray(colWidths), true);

        if (addFooter) {
            Cell cell = new Cell(1, 4).add(new Paragraph("Table footer: continue on next page"));
            table.addFooterCell(cell);
        }

        if (separate) {
            table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        }

        doc.add(table);

        for (int i = 0; i < rowsNumber; i++) {
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 0))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 1))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 2))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 3))));

            table.flush();
        }

        table.complete();

        doc.close();
    }

    @Test
    // TODO DEVSIX-5865 Table last horizontal border is drawn twice: at final Table#flush and then at Table#complete
    public void largeTableWithTableBorderSplitTest() throws IOException, InterruptedException {
        String testName = "largeTableWithTableBorderSplitTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(595, 100));

        float[] colWidths = new float[]{200, -1, 20, 40};

        Table table = new Table(UnitValue.createPointArray(colWidths), true);
        doc.add(table);

        table.setBorder(new SolidBorder(ColorConstants.BLUE, 2));

        for (int i = 0; i < 1; i++) {
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 0))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 1))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 2))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 3))));

            table.flush();
        }

        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    // TODO DEVSIX-5865 Table last horizontal border is drawn twice: at final Table#flush and then at Table#complete
    public void largeTableWithTableBorderSplitTest02() throws IOException, InterruptedException {
        String testName = "largeTableWithTableBorderSplitTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(595, 100));

        Table table = new Table(2, true);
        doc.add(table);

        table.setBorder(new SolidBorder(ColorConstants.BLUE, 2));

        table.addCell(new Cell().setBackgroundColor(ColorConstants.RED).setHeight(50).setMargin(0).setPadding(0));
        table.addCell(new Cell().setBackgroundColor(ColorConstants.RED).setHeight(50).setMargin(0).setPadding(0));

        table.flush();
        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    // TODO DEVSIX-5866 at #complete left border is initialized as null
    public void largeTableWithCellBordersSplitTest1() throws IOException, InterruptedException {
        String testName = "largeTableWithCellBordersSplitTest1.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        float[] colWidths = new float[]{30, 30, 30};

        Table table = new Table(colWidths, true).setWidth(290);
        doc.add(table);

        table.addCell(new Cell().add(new Paragraph("Cell" + 0))
                .setPadding(0).setMargin(0)
                .setBorder(new SolidBorder(ColorConstants.MAGENTA, 50))
                .setBorderBottom(new SolidBorder(ColorConstants.BLUE, 50)));
        table.addCell(new Cell().add(new Paragraph("Cell" + 1))
                .setPadding(0).setMargin(0)
                .setBorder(new SolidBorder(ColorConstants.MAGENTA, 50))
                .setBorderBottom(new SolidBorder(ColorConstants.RED, 50)));
        table.addCell(new Cell().add(new Paragraph("Cell" + 3))
                .setPadding(0).setMargin(0)
                .setBorder(new SolidBorder(ColorConstants.MAGENTA, 50))
                .setBorderBottom(new SolidBorder(ColorConstants.BLUE, 50)));

        table.flush();
        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    // TODO DEVSIX-5866 at #complete left border is initialized as null
    public void largeTableWithCellBordersSplitTest() throws IOException, InterruptedException {
        String testName = "largeTableWithCellBordersSplitTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(595, 100));

        float[] colWidths = new float[]{200, -1, 20, 40};

        Table table = new Table(UnitValue.createPointArray(colWidths), true);
        doc.add(table);

        table.addCell(new Cell().add(new Paragraph("Cell" + 0))
                .setBorderBottom(new SolidBorder(ColorConstants.BLUE, 2)));
        table.addCell(new Cell().add(new Paragraph("Cell" + 1))
                .setBorderBottom(new SolidBorder(ColorConstants.RED, 5)));
        table.addCell(new Cell().add(new Paragraph("Cell" + 2))
                .setBorderBottom(new SolidBorder(ColorConstants.GREEN, 7)));
        table.addCell(new Cell().add(new Paragraph("Cell" + 3))
                .setBorderBottom(new SolidBorder(ColorConstants.BLUE, 10)));

        table.flush();
        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    // TODO DEVSIX-5866 at #complete left border is initialized as null
    public void largeTableWithCellBordersSplitTest02() throws IOException, InterruptedException {
        String testName = "largeTableWithCellBordersSplitTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(595, 100));

        float[] colWidths = new float[]{200, 40};

        Table table = new Table(UnitValue.createPointArray(colWidths), true);
        doc.add(table);

        table.addCell(new Cell().add(new Paragraph("Cell" + 0)).setBackgroundColor(ColorConstants.YELLOW)
                .setBorderBottom(new SolidBorder(ColorConstants.BLUE, 2)));
        table.addCell(new Cell().add(new Paragraph("Cell" + 3)).setBackgroundColor(ColorConstants.YELLOW)
                .setBorderBottom(new SolidBorder(ColorConstants.BLUE, 10)));

        table.flush();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    // TODO DEVSIX-5866 at #complete left border is initialized as null
    public void simpleLargeTableDifferentCellBottomBorderTest() throws IOException, InterruptedException {
        String testName = "simpleLargeTableDifferentCellBottomBorderTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(2, true);
        doc.add(table);

        table.addCell(new Cell().add(new Paragraph("Cell" + 0)).setHeight(30).setMargin(0).setPadding(0)
                .setBackgroundColor(ColorConstants.RED).setBorder(new SolidBorder(ColorConstants.BLUE, 10)));
        table.addCell(new Cell().add(new Paragraph("Cell" + 1)).setHeight(30).setMargin(0).setPadding(0)
                .setBackgroundColor(ColorConstants.RED).setBorder(new SolidBorder(10))
                .setBorderBottom(new SolidBorder(ColorConstants.BLUE, 100)));

        table.flush();
        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    // TODO DEVSIX-5867 footer's top / table body's bottom border gets drawn twice at different coordinates
    //  (Look at yellow border at page 2: it might not be tat obvious, however, there are two yelow borders
    //  there which overlap each other a bit)
    public void largeTableSplitFooter2Test() throws IOException, InterruptedException {
        String testName = "largeTableSplitFooter2Test.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(595, 400));

        float[] colWidths = new float[]{100};

        Table table = new Table(UnitValue.createPointArray(colWidths), true);
        doc.add(table);
        table.addFooterCell(new Cell().add(new Paragraph("Footer")).setBorderTop(new SolidBorder(ColorConstants.YELLOW, 15)).setBorderBottom(new SolidBorder(ColorConstants.GREEN, 35)));

        table.addCell(new Cell().add(new Paragraph("Cell1")).setHeight(400).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 20)));
        table.flush();
        table.addCell(new Cell().add(new Paragraph("Cell2")).setHeight(200).setBorderTop(new SolidBorder(ColorConstants.RED, 10)));

        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    // TODO DEVSIX-5867 footer's top / table body's bottom border gets drawn twice at different coordinates
    //  (Look at yellow border: it might not be tat obvious, however, there are two yelow borders
    //  there which overlap each other a bit)
    public void largeTableSplitFooter2ATest() throws IOException, InterruptedException {
        String testName = "largeTableSplitFooter2ATest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(1, true);
        doc.add(table);
        table.addFooterCell(new Cell().add(new Paragraph("Footer"))
                .setBorderTop(new SolidBorder(ColorConstants.YELLOW, 15))
        );

        table.addCell(new Cell().add(new Paragraph("Cell1")).setHeight(50)
                .setBorderBottom(new SolidBorder(ColorConstants.BLUE, 20)));
        table.flush();
        table.addCell(new Cell().add(new Paragraph("Cell2")).setHeight(50));

        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    // TODO DEVSIX-5869 large table's width should not change between flushes
    public void largeTableSplitFooter2BTest() throws IOException, InterruptedException {
        String testName = "largeTableSplitFooter2BTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(595, 900));

        addSpecificTableConsideringFlushes(doc, false, false);
        doc.add(new AreaBreak());

        addSpecificTableConsideringFlushes(doc, true, false);
        doc.add(new AreaBreak());

        addSpecificTableConsideringFlushes(doc, false, true);
        doc.add(new AreaBreak());

        addSpecificTableConsideringFlushes(doc, true, true);
        doc.add(new AreaBreak());

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void largeTableCollapsingSplitTest() throws IOException, InterruptedException {
        String testName = "largeTableCollapsingSplitTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(595, 400));

        float[] colWidths = new float[]{100};

        Table table = new Table(UnitValue.createPointArray(colWidths), true);
        doc.add(table);

        table.addCell(new Cell().add(new Paragraph("Cell1")).setHeight(1000).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 20)));
        table.flush();
        table.addCell(new Cell().add(new Paragraph("Cell2")).setHeight(1000).setBorderTop(new SolidBorder(ColorConstants.RED, 40)));

        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void largeTableOnDifferentPages01() throws IOException, InterruptedException {
        String testName = "largeTableOnDifferentPages01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        doc.setRenderer(new DifferentPagesDocumentRenderer(doc));

        float[] colWidths = new float[]{200, -1, 20, 40};

        // please also look at tableOnDifferentPages01
        Table table = new Table(UnitValue.createPointArray(colWidths), true);
        for (int i = 0; i < 28; i++) {
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 0))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 1))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 2))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 3))));

            if (0 == i) {
                doc.add(table);
            } else {
                table.flush();
            }
        }

        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableOnDifferentPages01() throws IOException, InterruptedException {
        String testName = "tableOnDifferentPages01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        doc.setRenderer(new DifferentPagesDocumentRenderer(doc));

        float[] colWidths = new float[]{200, -1, 20, 40};

        // please also look at largeTableOnDifferentPages01
        Table table = new Table(UnitValue.createPointArray(colWidths));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setFixedLayout();

        for (int i = 0; i < 28; i++) {
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 0))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 1))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 2))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 3))));
        }

        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableOnDifferentPages01A() throws IOException, InterruptedException {
        String testName = "largeTableOnDifferentPages01A.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        float[] colWidths = new float[]{200, -1, 20, 40};

        Table table = new Table(UnitValue.createPointArray(colWidths), true);
        doc.setRenderer(new DifferentPagesDocumentRenderer(doc));
        doc.add(table);

        table.addFooterCell(new Cell(1, 4).add(new Paragraph("Footer")));
        table.addHeaderCell(new Cell(1, 4).add(new Paragraph("Header")));

        for (int i = 0; i < 25; i++) {
            table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 0))));
            table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 1))));
            table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 2))));
            table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 3))));
            table.flush();
        }

        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableOnDifferentPages02() throws IOException, InterruptedException {
        String testName = "tableOnDifferentPages02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        doc.setRenderer(new DifferentPagesDocumentRenderer(doc));

        float[] colWidths = new float[]{200, -1, 20, 40};

        // please also look at largeTableOnDifferentPages01
        Table table = new Table(UnitValue.createPointArray(colWidths));
        table.setWidth(UnitValue.createPointValue(400));
        table.setFixedLayout();

        for (int i = 0; i < 28; i++) {
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 0))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 1))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 2))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 3))));
        }

        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 1)})
    public void reuseLargeTableTest01() throws IOException, InterruptedException {
        String testName = "reuseLargeTableTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        float[] colWidths = new float[]{200, -1, 20, 40};

        Table table = new Table(UnitValue.createPointArray(colWidths), true);
        table.setWidth(UnitValue.createPercentValue(60));
        doc.setRenderer(new DifferentPagesDocumentRenderer(doc));
        doc.add(table);

        table.addFooterCell(new Cell(1, 4).add(new Paragraph("Footer")));
        table.addHeaderCell(new Cell(1, 4).add(new Paragraph("Header")));

        for (int i = 0; i < 25; i++) {
            table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 0))));
            table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 1))));
            if (i != 24) {
                table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 2))));
                table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 3))));
                table.flush();
            }
        }

        table.complete();

        // One can relayout the table (it still has footer, f.i.)
        LayoutResult relayoutResult = table.createRendererSubTree().setParent(doc.getRenderer()).layout(new LayoutContext(new LayoutArea(0, new Rectangle(10000, 10000))));
        // But one cannot add content to the table anymore
        try {
            for (int i = 0; i < 25; i++) {
                table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 0))));
                Assertions.assertTrue(false, "The line above should have thrown an exception.");
                table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 1))));
                table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 2))));
                table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 3))));
            }
            doc.add(table);
        } catch (PdfException e) {
            if (!e.getMessage().equals(LayoutExceptionMessageConstant.CANNOT_ADD_CELL_TO_COMPLETED_LARGE_TABLE)) {
                throw e;
            }
        }

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeEmptyTableTest() throws IOException, InterruptedException {
        String testName = "largeEmptyTableTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        Table table = new Table(UnitValue.createPercentArray(1), true);
        doc.add(table);
        table.setBorderTop(new SolidBorder(ColorConstants.ORANGE, 100)).setBorderBottom(new SolidBorder(ColorConstants.MAGENTA, 150));
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 8)})
    public void largeEmptyTableTest02() throws IOException, InterruptedException {
        String testName = "largeEmptyTableTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(UnitValue.createPercentArray(3), true);
        doc.add(table);
        for (int i = 0; i < 3; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header" + i)));
        }
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.add(new AreaBreak());

        table = new Table(UnitValue.createPercentArray(3), true);
        doc.add(table);
        for (int i = 0; i < 3; i++) {
            table.addFooterCell(new Cell().add(new Paragraph("Footer" + i)));
        }
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.add(new AreaBreak());

        table = new Table(UnitValue.createPercentArray(3), true);
        doc.add(table);
        for (int i = 0; i < 3; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header" + i)));
            table.addFooterCell(new Cell().add(new Paragraph("Footer" + i)));
        }
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.add(new AreaBreak());

        table = new Table(UnitValue.createPercentArray(3), true);
        doc.add(table);
        for (int i = 0; i < 3; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header" + i)));
            table.addFooterCell(new Cell().add(new Paragraph("Footer" + i)));
        }
        table.addCell(new Cell().add(new Paragraph("Cell")));
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.add(new AreaBreak());

        table = new Table(UnitValue.createPercentArray(3), true);
        doc.add(table);
        for (int i = 0; i < 2; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header" + i)));
            table.addFooterCell(new Cell().add(new Paragraph("Footer" + i)));
        }
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.add(new AreaBreak());

        table = new Table(UnitValue.createPercentArray(3), true);
        doc.add(table);
        for (int i = 0; i < 2; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header" + i)));
            table.addFooterCell(new Cell().add(new Paragraph("Footer" + i)));
        }
        table.addCell(new Cell().add(new Paragraph("Cell")));
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 8)})
    public void largeEmptyTableTest02Separated() throws IOException, InterruptedException {
        String testName = "largeEmptyTableTest02Separated.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(UnitValue.createPercentArray(3), true);
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);
        for (int i = 0; i < 3; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header" + i)));
        }
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.add(new AreaBreak());

        table = new Table(UnitValue.createPercentArray(3), true);
        doc.add(table);
        for (int i = 0; i < 3; i++) {
            table.addFooterCell(new Cell().add(new Paragraph("Footer" + i)));
        }
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.add(new AreaBreak());

        table = new Table(UnitValue.createPercentArray(3), true);
        doc.add(table);
        for (int i = 0; i < 3; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header" + i)));
            table.addFooterCell(new Cell().add(new Paragraph("Footer" + i)));
        }
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.add(new AreaBreak());

        table = new Table(UnitValue.createPercentArray(3), true);
        doc.add(table);
        for (int i = 0; i < 3; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header" + i)));
            table.addFooterCell(new Cell().add(new Paragraph("Footer" + i)));
        }
        table.addCell(new Cell().add(new Paragraph("Cell")));
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.add(new AreaBreak());

        table = new Table(UnitValue.createPercentArray(3), true);
        doc.add(table);
        for (int i = 0; i < 2; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header" + i)));
            table.addFooterCell(new Cell().add(new Paragraph("Footer" + i)));
        }
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.add(new AreaBreak());

        table = new Table(UnitValue.createPercentArray(3), true);
        doc.add(table);
        for (int i = 0; i < 2; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header" + i)));
            table.addFooterCell(new Cell().add(new Paragraph("Footer" + i)));
        }
        table.addCell(new Cell().add(new Paragraph("Cell")));
        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    // TODO DEVSIX-3953 Footer is not placed on the first page in case of large table, but fits the page for a usual table
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void largeTableFooterNotFitTest() throws IOException, InterruptedException {
        String testName = "largeTableFooterNotFitTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(595, 100));

        Table table = new Table(1, true);
        Cell footerCell = new Cell().add(new Paragraph("Table footer: continue on next page"));
        table.addFooterCell(footerCell);
        doc.add(table);

        table.addCell(new Cell().add(new Paragraph("Cell")).setBackgroundColor(ColorConstants.RED));

        // If one comments flush, then the table fits the page
        table.flush();

        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    private static void addSpecificTableConsideringFlushes(Document doc, boolean flushFirst, boolean flushSecond) {
        Table table = new Table(UnitValue.createPercentArray(1), true);
        doc.add(table);

        table.addFooterCell(new Cell().add(new Paragraph("Footer"))
                .setBorderTop(new SolidBorder(ColorConstants.YELLOW, 15))
                .setHeight(100).setMargin(0).setPadding(0)
        );

        table.addCell(new Cell().add(new Paragraph("Cell1"))
                .setHeight(100).setMargin(0).setPadding(0)
                .setBackgroundColor(ColorConstants.RED)
        );

        if (flushFirst) {
            table.flush();
        }

        table.addCell(new Cell().add(new Paragraph("Cell2"))
                .setHeight(100).setMargin(0).setPadding(0)
                .setBackgroundColor(ColorConstants.RED)
                .setBorderLeft(new SolidBorder(ColorConstants.GREEN, 50))
                .setBorderRight(new SolidBorder(ColorConstants.GREEN, 50))

                .setBorderTop(new SolidBorder(ColorConstants.MAGENTA, 10)));

        if (flushSecond) {
            table.flush();
        }
        table.complete();
    }
}
