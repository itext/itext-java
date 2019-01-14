/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
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
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.BorderCollapsePropertyValue;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @Ignore("DEVSIX-1778")
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void largeTableWithLayoutResultNothingTest01() throws IOException, InterruptedException {
        String testName = "largeTableWithLayoutResultNothingTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A1.rotate());

        float[] colWidths = new float[]{300, 150, 50, 100};
        int numOfColumns = colWidths.length - 1; // the second column has colspan value as 2
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }


    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)})
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
    // TODO(DEVSIX-1664)
    public void largeTableSplitTest01() throws IOException, InterruptedException {
        String testName = "largeTableSplitTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(595, 100));

        float[] colWidths = new float[]{200, -1, 20, 40};

        // please also look at tableOnDifferentPages01
        Table table = new Table(UnitValue.createPointArray(colWidths), true);
        doc.add(table);

        for (int i = 0; i < 1; i++) {
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 0))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 1))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 2))));
            table.addCell(new Cell().add(new Paragraph("Cell" + (i * 4 + 3))));

            table.flush();
        }

        table.complete();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }


    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 1)})
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
                Assert.assertTrue("The line above should have thrown an exception.", false);
                table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 1))));
                table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 2))));
                table.addCell(new Cell().add(new Paragraph("Cell#" + (i * 4 + 3))));
            }
            doc.add(table);
        } catch (PdfException e) {
            if (!e.getMessage().equals(PdfException.CannotAddCellToCompletedLargeTable)) {
                throw e;
            }
        }

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 8)})
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 8)})
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }
}
