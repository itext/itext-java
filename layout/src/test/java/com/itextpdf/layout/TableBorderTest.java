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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.BorderCollapsePropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileNotFoundException;
import java.io.IOException;

@Category(IntegrationTest.class)
public class TableBorderTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/TableBorderTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/TableBorderTest/";
    public static final String cmpPrefix = "cmp_";

    String fileName;
    String outFileName;
    String cmpFileName;

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE)
    })
    @Test
    @Ignore("DEVSIX-1124")
    public void incompleteTableTest01() throws IOException, InterruptedException {
        fileName = "incompleteTableTest01.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 5));
        Cell cell;
        // row 1, cell 1
        cell = new Cell().add(new Paragraph("One"));
        table.addCell(cell);
        // row 1 and 2, cell 2
        cell = new Cell(2, 1).add(new Paragraph("Two"));
        table.addCell(cell);
        // row 2, cell 1
        cell = new Cell().add(new Paragraph("Three"));
        table.addCell(cell);

        // row 3, cell 1
        cell = new Cell().add(new Paragraph("Four"));
        table.addCell(cell);


        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @Ignore("DEVSIX-1124")
    public void incompleteTableTest02() throws IOException, InterruptedException {
        fileName = "incompleteTableTest02.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 5));
        Cell cell;
        // row 1, cell 1
        cell = new Cell().add(new Paragraph("One"));
        table.addCell(cell);
        table.startNewRow();
        // row 2, cell 1
        cell = new Cell().add(new Paragraph("Two"));
        table.addCell(cell);
        // row 2, cell 2
        cell = new Cell().add(new Paragraph("Three"));
        table.addCell(cell);

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE)
    })
    public void incompleteTableTest03() throws IOException, InterruptedException {
        fileName = "incompleteTableTest03.pdf";
        Document doc = createDocument();

        Table innerTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        Cell cell = new Cell().add(new Paragraph("Inner"));
        innerTable.addCell(cell);
        innerTable.startNewRow();

        Table outerTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        outerTable.addCell(innerTable);

        doc.add(outerTable);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 2)
    })
    public void incompleteTableTest04() throws IOException, InterruptedException {
        fileName = "incompleteTableTest04.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addCell(new Cell().add(new Paragraph("Liberte")).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 10)).setHeight(40));
        table.startNewRow();

        table.addCell(new Cell().add(new Paragraph("Fraternite")).setBorderTop(new SolidBorder(ColorConstants.BLUE, 15)).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 15)).setHeight(40));
        table.startNewRow();

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void simpleBorderTest02() throws IOException, InterruptedException {
        fileName = "simpleBorderTest02.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        Cell cell;
        // row 1, cell 1
        cell = new Cell().add(new Paragraph("One"));
        cell.setBorderTop(new SolidBorder(20));
        cell.setBorderBottom(new SolidBorder(20));
        table.addCell(cell);
        // row 2, cell 1
        cell = new Cell().add(new Paragraph("Two"));
        cell.setBorderTop(new SolidBorder(30));
        cell.setBorderBottom(new SolidBorder(40));

        table.addCell(cell);
        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void simpleBorderTest03() throws IOException, InterruptedException {
        fileName = "simpleBorderTest03.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.addCell(new Cell().add(new Paragraph("1")));
        table.addCell(new Cell(2, 1).add(new Paragraph("2")));
        table.addCell(new Cell().add(new Paragraph("3")));
        doc.add(table);


        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void simpleBorderTest04() throws IOException, InterruptedException {
        fileName = "simpleBorderTest04.pdf";
        Document doc = createDocument();
        String textByron =
                "When a man hath no freedom to fight for at home,\n" +
                        "    Let him combat for that of his neighbours;\n" +
                        "Let him think of the glories of Greece and of Rome,\n" +
                        "    And get knocked on the head for his labours.\n" +
                        "\n" +
                        "To do good to Mankind is the chivalrous plan,\n" +
                        "    And is always as nobly requited;\n" +
                        "Then battle for Freedom wherever you can,\n" +
                        "    And, if not shot or hanged, you'll get knighted.";
        String textHelloWorld =
                "Hello World\n" +
                        "Hello World\n" +
                        "Hello World\n" +
                        "Hello World\n" +
                        "Hello World\n";

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(ColorConstants.RED, 2f));
        table.addCell(new Cell(2, 1).add(new Paragraph(textHelloWorld)));
        for (int i = 0; i < 2; i++) {
            table.addCell(new Cell().add(new Paragraph(textByron)));
        }
        table.addCell(new Cell(1, 2).add(new Paragraph(textByron)));
        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void noVerticalBorderTest() throws IOException, InterruptedException {
        fileName = "noVerticalBorderTest.pdf";
        Document doc = createDocument();

        Table mainTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        Cell cell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setBorderTop(new SolidBorder(ColorConstants.BLACK, 0.5f));
        cell.add(new Paragraph("TESCHTINK"));
        mainTable.addCell(cell);
        doc.add(mainTable);

        doc.add(new AreaBreak());
        mainTable.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(mainTable);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void wideBorderTest01() throws IOException, InterruptedException {
        fileName = "wideBorderTest01.pdf";
        Document doc = createDocument();

        doc.add(new Paragraph("ROWS SHOULD BE THE SAME"));

        Table table = new Table(new float[]{1, 3});
        table.setWidth(UnitValue.createPercentValue(50));
        Cell cell;
        // row 21, cell 1
        cell = new Cell().add(new Paragraph("BORDERS"));
        table.addCell(cell);
        // row 1, cell 2
        cell = new Cell().add(new Paragraph("ONE"));
        cell.setBorderLeft(new SolidBorder(ColorConstants.RED, 16f));
        table.addCell(cell);
        // row 2, cell 1
        cell = new Cell().add(new Paragraph("BORDERS"));
        table.addCell(cell);
        // row 2, cell 2
        cell = new Cell().add(new Paragraph("TWO"));
        cell.setBorderLeft(new SolidBorder(ColorConstants.RED, 16f));
        table.addCell(cell);

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void wideBorderTest02() throws IOException, InterruptedException {
        fileName = "wideBorderTest02.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument, new PageSize(902, 842));

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 91f));
        Cell cell;

        cell = new Cell(1, 2).add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 70f));
        table.addCell(cell);
        cell = new Cell(2, 1).add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 70f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 70f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.BLUE, 20f));
        table.addCell(cell);

        for (int i = 0; i < 6; i++) {
            cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
            cell.setBorder(new SolidBorder(ColorConstants.RED, 50f));
            table.addCell(cell);
        }

        cell = new Cell(1, 2).add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 50f));
        table.addCell(cell);
        cell = new Cell(2, 1).add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 50f));
        table.addCell(cell);
        for (int i = 0; i < 1 + 6; i++) {
            cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
            cell.setBorder(new SolidBorder(ColorConstants.RED, 50f));
            table.addCell(cell);
        }

        cell = new Cell(1, 2).add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 50f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 45f));
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 40f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 35f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.BLUE, 5f));
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 45f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 64f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 102f));
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 11f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 12f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 44f));
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 27f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 16f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 59));
        table.addCell(cell);

        for (int i = 0; i < 9; i++) {
            cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
            cell.setBorder(new SolidBorder(ColorConstants.RED, 50f));
            table.addCell(cell);
        }

        for (int i = 0; i < 3; i++) {
            cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
            cell.setBorder(new SolidBorder(ColorConstants.RED, 20f));
            table.addCell(cell);
        }

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void wideBorderTest03() throws IOException, InterruptedException {
        fileName = "wideBorderTest03.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument, new PageSize(842, 400));

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 90f));
        Cell cell;

        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.BLUE, 20f));
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 120f));
        table.addCell(cell);


        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 50f));
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("Borders shouldn't be layouted outside the layout area."));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 50f));
        table.addCell(cell);

        doc.add(table);

        doc.getPdfDocument().setDefaultPageSize(new PageSize(842, 520));
        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void wideBorderTest04() throws IOException, InterruptedException {
        fileName = "wideBorderTest04.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument, new PageSize(200, 150));

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(ColorConstants.RED, 5));
        for (int i = 0; i < 5; i++) {
            table.addCell(new Cell().add(new Paragraph("Cell " + i)));
        }
        table.addCell(new Cell().add(new Paragraph("Cell 5")).setBorderTop(new SolidBorder(ColorConstants.GREEN, 20)));

        doc.add(table);

        pdfDocument.setDefaultPageSize(new PageSize(250, 170));
        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);


        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void borderCollapseTest01() throws IOException, InterruptedException {
        fileName = "borderCollapseTest01.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(ColorConstants.RED, 5));

        Cell cell;
        table.addCell(new Cell(1, 2).add(new Paragraph("first")).setBorder(Border.NO_BORDER));

        cell = new Cell(1, 2).add(new Paragraph("second"));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void borderCollapseTest02() throws IOException, InterruptedException {
        fileName = "borderCollapseTest02.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Cell cell;
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        // first row
        // column 1
        cell = new Cell().add(new Paragraph("1"));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        // column 2
        cell = new Cell().add(new Paragraph("2"));
        table.addCell(cell);
        // second row
        // column 1
        cell = new Cell().add(new Paragraph("3"));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        // column 2
        cell = new Cell().add(new Paragraph("4"));
        table.addCell(cell);
        cell = new Cell(1, 2).add(new Paragraph("5"));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void borderCollapseTest03() throws IOException, InterruptedException {
        fileName = "borderCollapseTest03.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Cell cell;
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        // first row
        // column 1
        cell = new Cell().add(new Paragraph("1"));
        cell.setBorderBottom(new SolidBorder(ColorConstants.RED, 4));
        table.addCell(cell);
        // column 2
        cell = new Cell().add(new Paragraph("2"));
        cell.setBorderBottom(new SolidBorder(ColorConstants.YELLOW, 5));
        table.addCell(cell);
        // second row
        // column 1
        cell = new Cell().add(new Paragraph("3"));
        cell.setBorder(new SolidBorder(ColorConstants.GREEN, 3));
        table.addCell(cell);
        // column 2
        cell = new Cell().add(new Paragraph("4"));
        cell.setBorderBottom(new SolidBorder(ColorConstants.MAGENTA, 2));
        table.addCell(cell);
        cell = new Cell(1, 2).add(new Paragraph("5"));
        table.addCell(cell);

        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void separatedBorderTest01A() throws IOException, InterruptedException {
        fileName = "separatedBorderTest01A.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setProperty(Property.BORDER_COLLAPSE, BorderCollapsePropertyValue.SEPARATE);

        table.setBorder(new SolidBorder(ColorConstants.RED, 50));

        for (int i = 0; i < 10; i++) {
            table.addCell(new Cell().add(new Paragraph("Cell#" + i)).setBorder(new SolidBorder(new DeviceRgb(100 * i % 255, 60 * i % 255, 20 * i % 255), 10 * (i % 5) + 10)));
        }

        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void separatedBorderTest01B() throws IOException, InterruptedException {
        fileName = "separatedBorderTest01B.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setProperty(Property.BORDER_COLLAPSE, BorderCollapsePropertyValue.SEPARATE);

        // table.setBorder(new SolidBorder(ColorConstants.RED, 5));

        for (int i = 0; i < 10; i++) {
            table.addCell(new Cell().add(new Paragraph("Cell#" + i)).setBorder(new SolidBorder(new DeviceRgb(100 * i % 255, 60 * i % 255, 20 * i % 255), 10 * (i % 5) + 10)));
        }

        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void separatedBorderTest01C() throws IOException, InterruptedException {
        fileName = "separatedBorderTest01C.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setProperty(Property.BORDER_COLLAPSE, BorderCollapsePropertyValue.SEPARATE);

        for (int i = 0; i < 10; i++) {
            table.addCell(new Cell().add(new Paragraph("Cell#" + i)).setBorder(new SolidBorder(new DeviceRgb(100 * i % 255, 60 * i % 255, 20 * i % 255), 10)));
        }

        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void infiniteLoopTest01() throws IOException, InterruptedException {
        fileName = "infiniteLoopTest01.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3}));
        table.setWidth(UnitValue.createPercentValue(50)).setProperty(Property.TABLE_LAYOUT, "fixed");
        Cell cell;

        // row 1, cell 1
        cell = new Cell().add(new Paragraph("1ORD"));
        cell.setBorderLeft(new SolidBorder(ColorConstants.BLUE, 5));
        table.addCell(cell);
        // row 1, cell 2
        cell = new Cell().add(new Paragraph("ONE"));
        cell.setBorderLeft(new SolidBorder(ColorConstants.RED, 100f));
        table.addCell(cell);
        // row 2, cell 1
        cell = new Cell().add(new Paragraph("2ORD"));
        cell.setBorderTop(new SolidBorder(ColorConstants.YELLOW, 100f));
        table.addCell(cell);
        // row 2, cell 2
        cell = new Cell().add(new Paragraph("TWO"));
        cell.setBorderLeft(new SolidBorder(ColorConstants.RED, 0.5f));
        table.addCell(cell);

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void splitCellsTest01() throws IOException, InterruptedException {
        fileName = "splitCellsTest01.pdf";
        Document doc = createDocument();

        String longText = "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text." +
                "Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.Very very very very very very very very very very very very very very very very very very long text.";
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setBorderTop(new DottedBorder(ColorConstants.MAGENTA, 3f));
        table.setBorderRight(new DottedBorder(ColorConstants.RED, 3f));
        table.setBorderBottom(new DottedBorder(ColorConstants.BLUE, 3f));
        table.setBorderLeft(new DottedBorder(ColorConstants.GRAY, 3f));

        Cell cell;
        cell = new Cell().add(new Paragraph("Some text"));
        cell.setBorderRight(new SolidBorder(ColorConstants.RED, 2f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Some text"));
        cell.setBorderLeft(new SolidBorder(ColorConstants.GREEN, 4f));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph(longText));
        cell.setBorderBottom(new SolidBorder(ColorConstants.RED, 5f));
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("Hello"));
        cell.setBorderBottom(new SolidBorder(ColorConstants.BLUE, 5f));
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("Some text."));
        cell.setBorderTop(new SolidBorder(ColorConstants.GREEN, 6f));
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("World"));
        cell.setBorderTop(new SolidBorder(ColorConstants.YELLOW, 6f));
        table.addCell(cell);

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void splitCellsTest02() throws IOException, InterruptedException {
        fileName = "splitCellsTest02.pdf";
        Document doc = createDocument();

        String text = "When a man hath no freedom to fight for at home,\n" +
                "    Let him combat for that of his neighbours;\n" +
                "Let him think of the glories of Greece and of Rome,\n" +
                "    And get knocked on the head for his labours.\n";

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

        Cell cell;
        for (int i = 0; i < 38; i++) {
            cell = new Cell().add(new Paragraph(text));
            cell.setBorder(new SolidBorder(ColorConstants.RED, 2f));
            table.addCell(cell);
        }
        doc.add(table);
        doc.add(new AreaBreak());

        table.setBorder(new SolidBorder(ColorConstants.YELLOW, 3));
        doc.add(table);

        doc.add(new AreaBreak());
        table.deleteOwnProperty(Property.BORDER);
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorder(new SolidBorder(ColorConstants.YELLOW, 3));
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void splitCellsTest03() throws IOException, InterruptedException {
        fileName = "splitCellsTest03.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(100, 160));

        String textAlphabet = "ABCDEFGHIJKLMNOPQRSTUWXYZ";

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();

        table.addCell(new Cell().add(new Paragraph(textAlphabet)).setBorder(new SolidBorder(4)));
        table.addFooterCell(new Cell().add(new Paragraph("Footer")));
        doc.add(table);

        doc.getPdfDocument().setDefaultPageSize(new PageSize(140, 200));
        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    // TODO (DEVSIX-1734 Run commented snippet to produce a bug.)
    public void splitCellsTest04() throws IOException, InterruptedException {
        fileName = "splitCellsTest04.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(595, 100 + 72));

        String text = "When a man hath no freedom to fight for at home,\n" +
                "    Let him combat for that of his neighbours;\n" +
                "Let him think of the glories of Greece and of Rome,\n" +
                "    And get knocked on the head for his labours.\n" +
                "A\n" +
                "B\n" +
                "C\n" +
                "D";

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();

        Cell cell;
        cell = new Cell().add(new Paragraph(text));
        cell.setBorderBottom(new SolidBorder(ColorConstants.RED, 20));
        cell.setBorderTop(new SolidBorder(ColorConstants.GREEN, 20));
        table.addCell(cell);

        table.addFooterCell(new Cell().add(new Paragraph("Footer")).setBorderTop(new SolidBorder(ColorConstants.YELLOW, 20)));

        doc.add(table);

//        doc.add(new AreaBreak());
//        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
//        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @Ignore("DEVSIX-1734")
    public void splitCellsTest04A() throws IOException, InterruptedException {
        fileName = "splitCellsTest04A.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(595, 80 + 72));

        String text = "When a man hath no freedom to fight for at home,\n" +
                "    Let him combat for that of his neighbours;\n" +
                "Let him think of the glories of Greece and of Rome,\n" +
                "    And get knocked on the head for his labours.\n" +
                "A\n" +
                "B\n" +
                "C\n" +
                "D";

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();

        Cell cell;
        cell = new Cell().add(new Paragraph(text));
        cell.setBorderBottom(new SolidBorder(ColorConstants.RED, 20));
        cell.setBorderTop(new SolidBorder(ColorConstants.GREEN, 20));
        table.addCell(cell);

        table.addFooterCell(new Cell().add(new Paragraph("Footer")).setBorderTop(new SolidBorder(ColorConstants.YELLOW, 20)));

        doc.add(table);
        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void splitCellsTest05() throws IOException, InterruptedException {
        fileName = "splitCellsTest05.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(130, 150));

        String text = "Cell";

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();
        table.addCell(new Cell().add(new Paragraph(text)));
        table.addCell(new Cell(2, 1).add(new Paragraph(text)));
        table.addCell(new Cell().add(new Paragraph(text)));
        table.addCell(new Cell().add(new Paragraph(text)));
        table.addCell(new Cell().add(new Paragraph(text)));

        doc.add(table);

        // TODO DEVSIX-1735: set pagesize as 196x132 to produce a NPE
        doc.getPdfDocument().setDefaultPageSize(new PageSize(196, 192));
        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void splitCellsTest06() throws IOException, InterruptedException {
        fileName = "splitCellsTest06.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(300, 150));

        doc.add(new Paragraph("No more"));
        doc.add(new Paragraph("place"));
        doc.add(new Paragraph("here"));


        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        Cell cell = new Cell(3, 1);
        cell.add(new Paragraph("G"));
        cell.add(new Paragraph("R"));
        cell.add(new Paragraph("P"));
        table.addCell(cell);
        table.addCell("middle row 1");
        cell = new Cell(3, 1);
        cell.add(new Paragraph("A"));
        cell.add(new Paragraph("B"));
        cell.add(new Paragraph("C"));
        table.addCell(cell);
        table.addCell("middle row 2");
        table.addCell("middle row 3");
        doc.add(table);

        // TODO DEVSIX-1735: uncomment to produce a NPE
//        doc.add(new AreaBreak());
//
//        doc.add(new Paragraph("No more"));
//        doc.add(new Paragraph("place"));
//        doc.add(new Paragraph("here"));
//
//        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
//        table.setHorizontalBorderSpacing(20);
//        table.setVerticalBorderSpacing(20);
//        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void splitCellsTest07() throws IOException, InterruptedException {
        fileName = "splitCellsTest07.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(133, 180));

        String text = "Cell";

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();
        table.addCell(new Cell().add(new Paragraph(text + "1")));
        table.addCell(new Cell(2, 1).add(new Paragraph(text + "222")));
        table.addCell(new Cell().add(new Paragraph(text + "3")));
        table.addCell(new Cell().add(new Paragraph(text + "4")).setKeepTogether(true));
        table.addCell(new Cell().add(new Paragraph(text + "5")).setKeepTogether(true));

        table.setBorderBottom(new SolidBorder(ColorConstants.BLUE, 1));

        doc.add(table);

        doc.getPdfDocument().setDefaultPageSize(new PageSize(193, 240));
        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void splitCellsTest08() throws IOException, InterruptedException {
        fileName = "splitCellsTest08.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(134, 140));

        String text = "Cell";

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();
        table.addCell(new Cell().add(new Paragraph(text + "1")));
        table.addCell(new Cell(2, 1).add(new Paragraph(text + "2")).setBorder(new SolidBorder(ColorConstants.GREEN, 4)));
        table.addCell(new Cell().add(new Paragraph(text + "3")));
        table.addCell(new Cell().add(new Paragraph(text + "4")));
        table.addCell(new Cell().add(new Paragraph(text + "5")));

        doc.add(table);

        // TODO DEVSIX-1735: set pagesize as 204x160 to produce a bug: cell with a big rowspan appears only on the final page
        doc.getPdfDocument().setDefaultPageSize(new PageSize(224, 200));
        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    // TODO DEVSIX-1735: uncomment snippet with separated borders to produce a NPE
    public void splitCellsTest09() throws IOException, InterruptedException {
        fileName = "splitCellsTest09.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(595, 160));

        String text = "Cell";

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        table.addCell(new Cell().add(new Paragraph("Make Gretzky great again! Make Gretzky great again! Make Gretzky great again! Make Gretzky great again! Make Gretzky great again! Make Gretzky great again!")));
        table.addCell(new Cell(2, 1).add(new Paragraph(text + "3")));
        table.addCell(new Cell().add(new Paragraph(text + "4")).setBorder(new SolidBorder(ColorConstants.GREEN, 2)));
        table.addCell(new Cell().add(new Paragraph(text + "5")));
        table.addCell(new Cell().add(new Paragraph(text + "5")));

        doc.add(table);

//        doc.add(new AreaBreak());
//        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
//        table.setHorizontalBorderSpacing(20);
//        table.setVerticalBorderSpacing(20);
//        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void splitCellsTest10() throws IOException, InterruptedException {
        fileName = "splitCellsTest10.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(136, 142));

        String text = "Cell";

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();
        table.addCell(new Cell().add(new Paragraph(text + "1")).setBackgroundColor(ColorConstants.YELLOW));
        table.addCell(new Cell(2, 1).add(new Paragraph(text + "222222222")).setBackgroundColor(ColorConstants.YELLOW));
        table.addCell(new Cell().add(new Paragraph(text + "3")).setBackgroundColor(ColorConstants.YELLOW));
        table.addCell(new Cell().setBackgroundColor(ColorConstants.YELLOW).add(new Paragraph(text + "4")).setKeepTogether(true));
        table.addCell(new Cell().setBackgroundColor(ColorConstants.YELLOW).add(new Paragraph(text + "5")).setKeepTogether(true));


        table.setBorder(new SolidBorder(ColorConstants.BLUE, 1));

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    // TODO DEVSIX-1735
    public void splitCellsTest10A() throws IOException, InterruptedException {
        fileName = "splitCellsTest10A.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(130, 140));

        String textAlphabet = "Cell";

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();
        table.addCell(new Cell().add(new Paragraph(textAlphabet + "1")).setBackgroundColor(ColorConstants.YELLOW));
        table.addCell(new Cell(2, 1).add(new Paragraph(textAlphabet + "222222222")).setBackgroundColor(ColorConstants.YELLOW));
        table.addCell(new Cell().add(new Paragraph(textAlphabet + "3")).setBackgroundColor(ColorConstants.YELLOW));
        table.addCell(new Cell().setBackgroundColor(ColorConstants.YELLOW).add(new Paragraph(textAlphabet + "4")).setKeepTogether(true));
        table.addCell(new Cell().setBackgroundColor(ColorConstants.YELLOW).add(new Paragraph(textAlphabet + "5")).setKeepTogether(true));

        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @Ignore("DEVSIX-1736")
    public void splitCellsTest10B() throws IOException, InterruptedException {
        fileName = "splitCellsTest10B.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(130, 110));

        String textAlphabet = "Cell";

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();
        table.addCell(new Cell().add(new Paragraph(textAlphabet + "1")).setBackgroundColor(ColorConstants.YELLOW));
        table.addCell(new Cell(2, 1).add(new Paragraph(textAlphabet + "222222222")).setBackgroundColor(ColorConstants.YELLOW));
        table.addCell(new Cell().add(new Paragraph(textAlphabet + "3")).setBackgroundColor(ColorConstants.YELLOW));
        table.addCell(new Cell().setBackgroundColor(ColorConstants.YELLOW).add(new Paragraph(textAlphabet + "4")).setKeepTogether(true));
        table.addCell(new Cell().setBackgroundColor(ColorConstants.YELLOW).add(new Paragraph(textAlphabet + "5")).setKeepTogether(true));

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void splitCellsTest10C() throws IOException, InterruptedException {
        fileName = "splitCellsTest10C.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(136, 142));

        String text = "Cell";

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();
        table.addCell(new Cell().add(new Paragraph(text + "1")).setBackgroundColor(ColorConstants.YELLOW).setBorderBottom(new SolidBorder(ColorConstants.BLACK, 10)));
        table.addCell(new Cell(2, 1).add(new Paragraph(text + "222222222")).setBackgroundColor(ColorConstants.YELLOW).setBorderBottom(new SolidBorder(ColorConstants.BLACK, 10)));
        table.addCell(new Cell().add(new Paragraph(text + "3")).setBackgroundColor(ColorConstants.YELLOW).setBorderBottom(new SolidBorder(ColorConstants.BLACK, 10)));
        table.addCell(new Cell().setBackgroundColor(ColorConstants.YELLOW).add(new Paragraph(text + "4")).setKeepTogether(true).setBorderBottom(new SolidBorder(ColorConstants.BLACK, 10)));
        table.addCell(new Cell().setBackgroundColor(ColorConstants.YELLOW).add(new Paragraph(text + "5")).setKeepTogether(true).setBorderBottom(new SolidBorder(ColorConstants.BLACK, 10)));


        table.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        doc.add(table);

        // TODO DEVSIX-1736: Set pagesize as 236x162 to produce a NPE
        doc.getPdfDocument().setDefaultPageSize(new PageSize(236, 222));
        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest01() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest01.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(1000, 1000));
        String text = "Cell";

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        for (int i = 0; i < 2; i++) {
            table.addCell(new Cell().add(new Paragraph(text + "1")).setHeight(40).setBorderBottom(new SolidBorder(ColorConstants.MAGENTA, 100)));
            table.addCell(new Cell().add(new Paragraph(text + "4")).setHeight(40).setBorderBottom(new SolidBorder(ColorConstants.MAGENTA, 100)));
            table.addCell(new Cell().add(new Paragraph(text + "5")).setHeight(40).setBorderBottom(new SolidBorder(ColorConstants.MAGENTA, 100)));
        }
        for (int i = 0; i < 3; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header")).setHeight(40));
            table.addFooterCell(new Cell().add(new Paragraph("Footer")).setHeight(40));
        }

        table.setBorder(new SolidBorder(ColorConstants.GREEN, 100));

        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell(new Cell().add(new Paragraph("Hello"))).setBorder(new SolidBorder(ColorConstants.BLACK, 10)));

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell(new Cell().add(new Paragraph("Hello"))).setBorder(new SolidBorder(ColorConstants.BLACK, 10)));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest02() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest02.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(595, 1500));
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

        table.addHeaderCell(new Cell().setHeight(30).add(new Paragraph("Header1")).setBorderTop(new SolidBorder(ColorConstants.RED, 100)));
        table.addHeaderCell(new Cell().setHeight(30).add(new Paragraph("Header2")).setBorderTop(new SolidBorder(ColorConstants.RED, 200)));

        table.addFooterCell(new Cell().setHeight(30).add(new Paragraph("Footer1")).setBorderTop(new SolidBorder(ColorConstants.RED, 100)));
        table.addFooterCell(new Cell().setHeight(30).add(new Paragraph("Footer2")).setBorderTop(new SolidBorder(ColorConstants.RED, 200)));
        table.addFooterCell(new Cell().setHeight(30).add(new Paragraph("Footer3")));
        table.addFooterCell(new Cell().setHeight(30).add(new Paragraph("Footer4")));

        for (int i = 1; i < 43; i += 2) {
            table.addCell(new Cell().setHeight(30).add(new Paragraph("Cell" + i)).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 400)).setBorderRight(new SolidBorder(20)));
            table.addCell(new Cell().setHeight(30).add(new Paragraph("Cell" + (i + 1))).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 100)).setBorderLeft(new SolidBorder(20)));
        }

        table.setSkipLastFooter(true);
        table.setSkipFirstHeader(true);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Hello").setBorder(new SolidBorder(ColorConstants.ORANGE, 2)));

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Hello").setBorder(new SolidBorder(ColorConstants.ORANGE, 2)));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest03() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest03.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("Header")).setHeight(400).setBorder(new SolidBorder(ColorConstants.BLUE, 40)));
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 100));
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Hello").setBorder(new SolidBorder(ColorConstants.MAGENTA, 5)));
        doc.add(new AreaBreak());

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Hello").setBorder(new SolidBorder(ColorConstants.MAGENTA, 5)));
        doc.add(new AreaBreak());

        table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addFooterCell(new Cell().add(new Paragraph("Footer")).setHeight(400).setBorder(new SolidBorder(ColorConstants.BLUE, 40)));
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 100));
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Hello").setBorder(new SolidBorder(ColorConstants.MAGENTA, 5)));
        doc.add(new AreaBreak());

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Hello").setBorder(new SolidBorder(ColorConstants.MAGENTA, 5)));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest04() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest04.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("Header")).setBorder(new SolidBorder(ColorConstants.BLUE, 40)));
        table.addCell(new Cell().add(new Paragraph("Cell")).setBorder(new SolidBorder(ColorConstants.MAGENTA, 30)));
        table.addFooterCell(new Cell().add(new Paragraph("Footer")).setBorder(new SolidBorder(ColorConstants.BLUE, 20)));
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Hello").setBorder(new SolidBorder(ColorConstants.MAGENTA, 5)));

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Hello").setBorder(new SolidBorder(ColorConstants.MAGENTA, 5)));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest05() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest05.pdf";
        Document doc = createDocument();
        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addCell(new Cell().add(new Paragraph("Cell")).setBorder(new SolidBorder(ColorConstants.MAGENTA, 30)).setHeight(30));
        table.addFooterCell(new Cell().add(new Paragraph("Footer")).setBorder(new SolidBorder(ColorConstants.BLUE, 50)).setHeight(30));
        table.setBorder(new SolidBorder(100));
        table.setSkipLastFooter(true);

        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Hello").setBorder(new SolidBorder(ColorConstants.ORANGE, 5)));

        table.deleteOwnProperty(Property.BORDER);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Hello").setBorder(new SolidBorder(ColorConstants.ORANGE, 5)));

        doc.add(new AreaBreak());
        table.setBorder(new SolidBorder(100));
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Hello").setBorder(new SolidBorder(ColorConstants.ORANGE, 5)));

        table.deleteOwnProperty(Property.BORDER);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Hello").setBorder(new SolidBorder(ColorConstants.ORANGE, 5)));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 10)})
    public void tableWithHeaderFooterTest06() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest06.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(PageSize.A6.rotate());
        Table table = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)")).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.RED, 20));
        table.addHeaderCell(cell);
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page")).setHeight(30).setBorderTop(new SolidBorder(ColorConstants.MAGENTA, 20));
        table.addFooterCell(cell);
        for (int i = 0; i < 50; i++) {
            table.addCell(new Cell().setBorderLeft(new SolidBorder(ColorConstants.BLUE, 0.5f)).setBorderRight(new SolidBorder(ColorConstants.BLUE, 0.5f)).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 2 * i + 1 > 50 ? 50 : 2 * i + 1)).setBorderTop(new SolidBorder(ColorConstants.GREEN, (50 - 2 * i + 1 >= 0) ? 50 - 2 * i + 1 : 0)).add(new Paragraph(String.valueOf(i + 1))));
        }
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 10)})
    public void tableWithHeaderFooterTest06A() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest06A.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument, PageSize.A6.rotate());

        Table table = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)")).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.RED, 20));
        table.addHeaderCell(cell);
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page")).setHeight(30).setBorderTop(new SolidBorder(ColorConstants.MAGENTA, 20));
        table.addFooterCell(cell);
        for (int i = 0; i < 50; i++) {
            table.addCell(new Cell().setBorderLeft(new SolidBorder(ColorConstants.BLUE, 0.5f)).setBorderRight(new SolidBorder(ColorConstants.BLUE, 0.5f)).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 2 * i + 1 > 50 ? 50 : 2 * i + 1)).setBorderTop(new SolidBorder(ColorConstants.GREEN, (50 - 2 * i + 1 >= 0) ? 50 - 2 * i + 1 : 0)).add(new Paragraph(String.valueOf(i + 1))));
        }
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 10)})
    public void tableWithHeaderFooterTest06B() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest06B.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument, PageSize.A6.rotate());

        Table table = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)")).setHeight(30).setBorderBottom(new SolidBorder(ColorConstants.RED, 20));
        table.addHeaderCell(cell);
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page")).setHeight(30).setBorderTop(new SolidBorder(ColorConstants.MAGENTA, 20));
        table.addFooterCell(cell);
        for (int i = 0; i < 50; i++) {
            table.addCell(new Cell().setBorderLeft(new SolidBorder(ColorConstants.BLUE, 0.5f)).setBorderRight(new SolidBorder(ColorConstants.BLUE, 0.5f)).setHeight(30).setBorderTop(new SolidBorder(ColorConstants.BLUE, 2 * i + 1 > 50 ? 50 : 2 * i + 1)).setBorderBottom(new SolidBorder(ColorConstants.GREEN, (50 - 2 * i + 1 >= 0) ? 50 - 2 * i + 1 : 0)).add(new Paragraph(String.valueOf(i + 1))));
        }
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));


        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest07() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest07.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + "cmp_" + fileName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A7.rotate());

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();
        table.addFooterCell(new Cell(1, 2).setHeight(30).add(new Paragraph("Footer")));
        table.addCell(new Cell().add(new Paragraph("0abcdefghijklmnopqrstuvwxyz1abcdefghijklmnopqrstuvwxyz2abcdefghijklmnopq")));
        table.addCell(new Cell().add(new Paragraph("0bbbbbbbbbbbbbbbbbbbbbbbbbbbb")).setBorderBottom(new SolidBorder(50)));
        doc.add(table);

        pdfDoc.setDefaultPageSize(new PageSize(298, 250));
        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest08() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest08.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + "cmp_" + fileName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A7.rotate());

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.addFooterCell(new Cell(1, 2).setHeight(50).add(new Paragraph("Footer")));
        table.addCell(new Cell().add(new Paragraph("Cell1")).setHeight(50));
        table.addCell(new Cell().add(new Paragraph("Cell2")).setHeight(50));
        table.setSkipLastFooter(true);
        table.setBorderBottom(new SolidBorder(ColorConstants.RED, 30));

        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest09() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest09.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + "cmp_" + fileName;

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)), PageSize.A3.rotate());
        Cell headerCell1 = new Cell().add(new Paragraph("I am header"))
                .setBorder(new SolidBorder(ColorConstants.GREEN, 30))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER);
        Cell headerCell2 = new Cell().add(new Paragraph("I am header"))
                .setBorder(new SolidBorder(ColorConstants.GREEN, 30))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER);

        Cell tableCell1 = new Cell().add(new Paragraph("I am table"))
                .setBorder(new SolidBorder(ColorConstants.RED, 200))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER);
        Cell tableCell2 = new Cell().add(new Paragraph("I am table"))
                .setBorder(new SolidBorder(ColorConstants.RED, 200))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER);

        Cell footerCell1 = new Cell().add(new Paragraph("I am footer"))
                .setBorder(new SolidBorder(ColorConstants.GREEN, 30))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER);
        Cell footerCell2 = new Cell().add(new Paragraph("I am footer"))
                .setBorder(new SolidBorder(ColorConstants.GREEN, 30))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER);

        Table table = new Table(new float[]{350, 350}).setBorder(new SolidBorder(ColorConstants.BLUE, 20))
                .addHeaderCell(headerCell1).addHeaderCell(headerCell2)
                .addCell(tableCell1).addCell(tableCell2)
                .addFooterCell(footerCell1).addFooterCell(footerCell2);
        table.getHeader().setBorderLeft(new SolidBorder(ColorConstants.MAGENTA, 40));
        table.getFooter().setBorderRight(new SolidBorder(ColorConstants.MAGENTA, 40));

        doc.add(table);
        doc.add(new AreaBreak());

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new AreaBreak());

        headerCell1 = new Cell().add(new Paragraph("I am header"))
                .setBorder(new SolidBorder(ColorConstants.GREEN, 200))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER);
        headerCell2 = new Cell().add(new Paragraph("I am header"))
                .setBorder(new SolidBorder(ColorConstants.GREEN, 200))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER);

        tableCell1 = new Cell().add(new Paragraph("I am table"))
                .setBorder(new SolidBorder(ColorConstants.RED, 30))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER);
        tableCell2 = new Cell().add(new Paragraph("I am table"))
                .setBorder(new SolidBorder(ColorConstants.RED, 30))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER);

        table = new Table(new float[]{350, 350}).setBorder(new SolidBorder(ColorConstants.BLUE, 20))
                .addHeaderCell(headerCell1).addHeaderCell(headerCell2)
                .addCell(tableCell1).addCell(tableCell2);

        doc.add(table);
        doc.add(new AreaBreak());

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest10() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest10.pdf";
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + "cmp_" + fileName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(380, 300));

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        table.addFooterCell(new Cell(1, 3).setHeight(70).add(new Paragraph("Footer")));
        table.addHeaderCell(new Cell(1, 3).setHeight(30).add(new Paragraph("Header")));

        for (int i = 0; i < 2; i++) {
            table.addCell(new Cell().add(new Paragraph(i + ": Bazz :")).setBorder(new SolidBorder(ColorConstants.BLACK, 10)));
            table.addCell(new Cell().add(new Paragraph("To infinity")).setBorder(new SolidBorder(ColorConstants.YELLOW, 30)));
            table.addCell(new Cell().add(new Paragraph(" and beyond!")).setBorder(new SolidBorder(ColorConstants.RED, 20)));
        }

        table.setSkipLastFooter(true);
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 1));
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        pdfDoc.setDefaultPageSize(new PageSize(480, 350));
        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest11() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest11.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(90));
        table.addFooterCell(new Cell(1, 3).setHeight(150).add(new Paragraph("Footer")));
        table.addHeaderCell(new Cell(1, 3).setHeight(30).add(new Paragraph("Header")));

        for (int i = 0; i < 10; i++) {
            table.addCell(new Cell().add(new Paragraph(i + ": Bazz :")).setBorder(new SolidBorder(ColorConstants.BLACK, 10)));
            table.addCell(new Cell().add(new Paragraph("To infinity")).setBorder(new SolidBorder(ColorConstants.YELLOW, 30)));
            table.addCell(new Cell().add(new Paragraph(" and beyond!")).setBorder(new SolidBorder(ColorConstants.RED, 20)));
        }

        table.setSkipLastFooter(true);

        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.getPdfDocument().setDefaultPageSize(new PageSize(695, 842));
        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest11A() throws IOException, InterruptedException {
        String testName = "tableWithHeaderFooterTest11A.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(90));
        table.addFooterCell(new Cell(1, 3).setHeight(150).add(new Paragraph("Footer")));
        table.addHeaderCell(new Cell(1, 3).setHeight(30).add(new Paragraph("Header")));

        for (int i = 0; i < 11; i++) {
            table.addCell(new Cell().add(new Paragraph(i + ": Bazz :")).setBorder(new SolidBorder(ColorConstants.BLACK, 10)));
            table.addCell(new Cell().add(new Paragraph("To infinity")).setBorder(new SolidBorder(ColorConstants.YELLOW, 30)));
            table.addCell(new Cell().add(new Paragraph(" and beyond!")).setBorder(new SolidBorder(ColorConstants.RED, 20)));
        }

        table.setSkipLastFooter(true);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.getPdfDocument().setDefaultPageSize(new PageSize(695, 842));
        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableWithHeaderFooterTest12() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest12.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.addHeaderCell(new Cell().setHeight(30).add(new Paragraph("Header")).setBorder(new SolidBorder(ColorConstants.BLUE, 5)));
        table.addHeaderCell(new Cell().setHeight(30).add(new Paragraph("Header")).setBorder(new SolidBorder(ColorConstants.BLUE, 35)));

        table.addFooterCell(new Cell().setHeight(30).add(new Paragraph("Footer")).setBorder(new SolidBorder(ColorConstants.YELLOW, 20)));
        table.addFooterCell(new Cell().setHeight(30).add(new Paragraph("Footer")).setBorder(new SolidBorder(ColorConstants.YELLOW, 20)));

        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        closeDocumentAndCompareOutputs(doc);
    }

    @Ignore("DEVSIX-1219")
    @Test
    public void tableWithHeaderFooterTest13() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest13.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addHeaderCell(new Cell().setHeight(30).add(new Paragraph("Header")).setBorder(new SolidBorder(ColorConstants.BLUE, 5)));
        table.addCell(new Cell().setHeight(30).add(new Paragraph("Make Gretzky great again!")).setBorder(Border.NO_BORDER));
        table.addFooterCell(new Cell().setHeight(30).add(new Paragraph("Footer")).setBorder(new SolidBorder(ColorConstants.YELLOW, 5)));
        doc.add(table);

        doc.add(new AreaBreak());

        table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addCell(new Cell().setHeight(30).add(new Paragraph("Make Gretzky great again!"))
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER))
        ;
        table.addCell(new Cell().setHeight(30).add(new Paragraph("Make Gretzky great again!"))
                .setBorderLeft(new SolidBorder(ColorConstants.GREEN, 0.5f))
                .setBorderRight(new SolidBorder(ColorConstants.RED, 0.5f)))
        ;

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest14() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest14.pdf";
        Document doc = createDocument();

        Table table = new Table(new float[3]);
        for (int r = 0; r < 1; r++) {
            for (int c = 0; c < 3; c++) {
                table.addHeaderCell(new Cell().add(new Paragraph(MessageFormatUtil.format("header row {0} col {1}", r, c))).setBorder(Border.NO_BORDER));
            }
        }
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                table.addCell(new Cell().add(new Paragraph(MessageFormatUtil.format("row {0} col {1}", r, c))).setBorder(Border.NO_BORDER));
            }
        }
        for (int r = 0; r < 1; r++) {
            for (int c = 0; c < 3; c++) {
                table.addFooterCell(new Cell().add(new Paragraph(MessageFormatUtil.format("footer row {0} col {1}", r, c))).setBorder(Border.NO_BORDER));
            }
        }

        table.getHeader()
                .setBorderTop(new SolidBorder(2))
                .setBorderBottom(new SolidBorder(1));
        table.getFooter()
                .setBold()
                .setBorderTop(new SolidBorder(10))
                .setBorderBottom(new SolidBorder(1))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest15() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest15.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addHeaderCell(new Cell().setHeight(30).add(new Paragraph("Header")).setBorder(new DottedBorder(ColorConstants.RED, 20)));
        table.addCell(new Cell().setHeight(30).add(new Paragraph("Body")).setBorder(new DottedBorder(ColorConstants.GREEN, 20)));
        table.addFooterCell(new Cell().setHeight(30).add(new Paragraph("Footer")).setBorder(new DottedBorder(ColorConstants.BLUE, 20)));
        table.setBackgroundColor(ColorConstants.MAGENTA);
        table.getHeader().setBackgroundColor(ColorConstants.ORANGE);
        table.getFooter().setBackgroundColor(ColorConstants.ORANGE);

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest16() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest16.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();

        table.addHeaderCell(new Cell().add(new Paragraph("Header 1")).setBorderBottom(new SolidBorder(ColorConstants.RED, 25)).setBorderTop(new SolidBorder(ColorConstants.ORANGE, 27)));
        table.getHeader().addHeaderCell("Header 2");

        table.addCell(new Cell().add(new Paragraph("Body 1")).setBorderTop(new SolidBorder(ColorConstants.GREEN, 20)));

        table.addFooterCell(new Cell().add(new Paragraph("Footer 1")).setBorderTop(new SolidBorder(ColorConstants.RED, 25)).setBorderBottom(new SolidBorder(ColorConstants.ORANGE, 27)));
        table.getFooter().addFooterCell("Footer 2");


        table.setBorderTop(new SolidBorder(ColorConstants.BLUE, 30)).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 30));
        table.getFooter().setBorderBottom(new SolidBorder(ColorConstants.YELLOW, 50));
        table.getHeader().setBorderTop(new SolidBorder(ColorConstants.YELLOW, 50));

        table.setBackgroundColor(ColorConstants.MAGENTA);

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest17() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest17.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(300, 300));

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(40));
        table.addFooterCell(new Cell().setHeight(50).add(new Paragraph("Footer")));

        for (int i = 0; i < 3; i++) {
            table.addCell(new Cell().setHeight(50).add(new Paragraph("Cell" + i)));
        }
        table.setSkipLastFooter(true);

        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest18() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest18.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(300, 400));

        // only footer
        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(50));
        table.addFooterCell(new Cell(1, 1).setHeight(50).add(new Paragraph("Footer")));

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.add(new AreaBreak());

        // only header
        table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(10));
        table.addHeaderCell(new Cell(1, 1).setHeight(50).add(new Paragraph("Header")).setBackgroundColor(ColorConstants.YELLOW));

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.add(new AreaBreak());

        // only header and footer
        table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.setBorderTop(new SolidBorder(ColorConstants.BLUE, 40));
        table.setBorderBottom(new SolidBorder(ColorConstants.BLUE, 40));

        table.addHeaderCell(new Cell(1, 1).setHeight(50).add(new Paragraph("Header")));
        table.addFooterCell(new Cell(1, 1).setHeight(50).add(new Paragraph("Footer")));

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void tableWithHeaderFooterTest19() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest19.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(300, 400));

        // footer and body
        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addCell(new Cell(1, 1).setHeight(50).add(new Paragraph("Cell")));
        table.addFooterCell(new Cell(1, 1).setHeight(50).add(new Paragraph("Footer")));

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.add(new AreaBreak());

        // header and body
        table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addCell(new Cell(1, 1).setHeight(50).add(new Paragraph("Cell")));
        table.addHeaderCell(new Cell(1, 1).setHeight(50).add(new Paragraph("Header")));

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.add(new AreaBreak());

        // header, footer and body
        table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addCell(new Cell(1, 1).setHeight(50).add(new Paragraph("Cell")));
        table.addHeaderCell(new Cell(1, 1).setHeight(50).add(new Paragraph("Header")));
        table.addFooterCell(new Cell(1, 1).setHeight(50).add(new Paragraph("Footer")));

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void tableWithHeaderFooterTest20() throws IOException, InterruptedException {
        fileName = "tableWithHeaderFooterTest20.pdf";
        Document doc = createDocument();
        doc.getPdfDocument().setDefaultPageSize(new PageSize(300, 115 + 72));

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(10));
        table.addFooterCell(new Cell().setHeight(50).add(new Paragraph("Footer")));
        table.addCell(new Cell().add(new Paragraph("Cell")).setHeight(50));

        doc.add(table);
        doc.add(new AreaBreak());

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void splitRowspanKeepTogetherTest() throws IOException, InterruptedException {
        fileName = "splitRowspanKeepTogetherTest.pdf";
        Document doc = createDocument();

        String textByron =
                "When a man hath no freedom to fight for at home,\n" +
                        "    Let him combat for that of his neighbours;\n" +
                        "Let him think of the glories of Greece and of Rome,\n" +
                        "    And get knocked on the head for his labours.\n" +
                        "\n" +
                        "To do good to Mankind is the chivalrous plan,\n" +
                        "    And is always as nobly requited;\n" +
                        "Then battle for Freedom wherever you can,\n" +
                        "    And, if not shot or hanged, you'll get knighted.";


        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setKeepTogether(true);

        int bigRowspan = 8;
        table.addCell(new Cell(bigRowspan, 1).add(new Paragraph("Big cell")).setBorder(new SolidBorder(ColorConstants.GREEN, 20)));
        for (int i = 0; i < bigRowspan; i++) {
            table.addCell(i + " " + textByron);
        }


        doc.add(new Paragraph("Try to break me!"));
        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 4),
            @LogMessage(messageTemplate = LogMessageConstant.RECTANGLE_HAS_NEGATIVE_SIZE, count = 2),

    })
    public void forcedPlacementTest01() throws IOException, InterruptedException {
        fileName = "forcedPlacementTest01.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.setWidth(10).setProperty(Property.TABLE_LAYOUT, "fixed");
        Cell cell;
        // row 1, cell 1
        cell = new Cell().add(new Paragraph("1ORD"));
        table.addCell(cell);
        // row 2, cell 1
        cell = new Cell().add(new Paragraph("2ORD"));
        cell.setBorderTop(new SolidBorder(ColorConstants.YELLOW, 100f));
        table.addCell(cell);

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void noHorizontalBorderTest() throws IOException, InterruptedException {
        fileName = "noHorizontalBorderTest.pdf";
        Document doc = createDocument();

        Table mainTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        Cell cell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setBorderRight(new SolidBorder(ColorConstants.BLACK, 0.5f));
        cell.add(new Paragraph("TESCHTINK"));
        mainTable.addCell(cell);
        doc.add(mainTable);

        doc.add(new AreaBreak());
        mainTable.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        mainTable.setHorizontalBorderSpacing(20);
        mainTable.setVerticalBorderSpacing(20);
        doc.add(mainTable);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void bordersWithSpansTest01() throws IOException, InterruptedException {
        fileName = "bordersWithSpansTest01.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(10)).useAllAvailableWidth();
        table.setWidth(UnitValue.createPercentValue(100));
        table.addCell(new Cell(1, 3).add(new Paragraph(1 + "_" + 3 + "_")));
        table.addCell(new Cell(1, 7).add(new Paragraph(1 + "_" + 7 + "_")));
        table.addCell(new Cell(6, 1).add(new Paragraph(6 + "_" + 1 + "_")));
        table.addCell(new Cell(6, 9).add(new Paragraph(6 + "_" + 9 + "_")));
        table.flushContent();
        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void bordersWithSpansTest02() throws IOException, InterruptedException {
        fileName = "bordersWithSpansTest02.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.addCell(new Cell().add(new Paragraph("Liberte")).setBorder(new SolidBorder(ColorConstants.MAGENTA, 1)));
        table.addCell(new Cell().add(new Paragraph("Egalite")));
        table.addCell(new Cell(3, 1).add(new Paragraph("Fra")).setBorder(new SolidBorder(ColorConstants.GREEN, 2)));
        table.addCell(new Cell(2, 1).add(new Paragraph("ter")).setBorder(new SolidBorder(ColorConstants.YELLOW, 2)));
        table.addCell(new Cell().add(new Paragraph("nite")).setBorder(new SolidBorder(ColorConstants.CYAN, 5)));

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void bordersWithSpansTest03() throws IOException, InterruptedException {
        fileName = "bordersWithSpansTest03.pdf";
        Document doc = createDocument();

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        table.addCell(new Cell(6, 1).add(new Paragraph("Fra")).setBorder(new SolidBorder(ColorConstants.ORANGE, 10)));
        table.addCell(new Cell().add(new Paragraph("Liberte")).setBorder(new SolidBorder(ColorConstants.MAGENTA, 1)));
        table.addCell(new Cell().add(new Paragraph("Egalite")));
        table.addCell(new Cell(5, 1).add(new Paragraph("ter")).setBorder(new SolidBorder(ColorConstants.GREEN, 2)));
        table.addCell(new Cell(2, 1).add(new Paragraph("ni")).setBorder(new SolidBorder(ColorConstants.YELLOW, 2)));
        table.addCell(new Cell(3, 1).add(new Paragraph("te")).setBorder(new SolidBorder(ColorConstants.CYAN, 5)));

        doc.add(table);

        doc.add(new AreaBreak());
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        doc.add(table);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void headerTopBorderTest01() throws IOException, InterruptedException {
        fileName = "headerTopBorderTest01.pdf";
        Document doc = createDocument();

        for (int i = 0; i < 29; ++i) {
            doc.add(new Paragraph("aaaaaaaaaaaa"));
        }

        Table table = new Table(new float[]{50, 50}).setBorder(new SolidBorder(1));
        table.addHeaderCell(new Cell().add(new Paragraph("h")).setBorderTop(Border.NO_BORDER));
        table.addHeaderCell(new Cell().add(new Paragraph("h")).setBorderTop(Border.NO_BORDER));
        for (int i = 0; i < 4; ++i) {
            table.addCell(new Cell().add(new Paragraph("aa")).setBorder(Border.NO_BORDER));
        }

        doc.add(table);
        doc.add(new Paragraph("Correct result:"));
        doc.add(table);
        closeDocumentAndCompareOutputs(doc);
    }


    private Document createDocument() throws FileNotFoundException {
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        return new Document(pdfDocument);
    }

    private void closeDocumentAndCompareOutputs(Document document) throws IOException, InterruptedException {
        document.close();
        String compareResult = new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff");
        if (compareResult != null) {
            Assert.fail(compareResult);
        }
    }
}
