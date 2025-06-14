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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.net.MalformedURLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class KeepTogetherDivAndTableTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/KeepTogetherDivAndTableTest/";
    public static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/KeepTogetherDivAndTableTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 4),
            @LogMessage(messageTemplate = IoLogMessageConstant.RENDERER_WAS_NOT_ABLE_TO_PROCESS_KEEP_WITH_NEXT)
    })
    public void combineKeepTogetherDivWithTableTest() throws IOException, InterruptedException {
        String cmpFile = SOURCE_FOLDER + "cmp_combineKeepTogetherDivWithTable.pdf";
        String destPdf = DESTINATION_FOLDER + "combineKeepTogetherDivWithTable.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destPdf))) {
            Document doc = new Document(pdfDoc);

            for (int i = 0; i < 10; i++) {
                doc.add(new Paragraph("").setFontSize(10).setMarginBottom(0)
                        .setBorder(new SolidBorder(ColorConstants.PINK, 1f)));
            }

            Div div = new Div().setKeepTogether(true).setKeepWithNext(true)
                    .setBorder(new SolidBorder(ColorConstants.BLUE, 1f));
            div.add(new Paragraph("Moved title").setFontSize(12).setFontColor(ColorConstants.BLUE)
                    .setMarginTop(10).setMarginBottom(0));
            doc.add(div);

            doc.add(createTableWithData(createBigCellTest(55)));
        }

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpFile, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void combineKeepTogetherDivWithTablePossibleLayout2pageTest() throws IOException, InterruptedException {
        String cmpFile = SOURCE_FOLDER + "cmp_combineKeepTogetherDivWithTablePossibleLayout2page.pdf";
        String destPdf = DESTINATION_FOLDER + "combineKeepTogetherDivWithTablePossibleLayout2page.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destPdf))) {
            Document doc = new Document(pdfDoc);

            for (int i = 0; i < 10; i++) {
                doc.add(new Paragraph("").setFontSize(10).setMarginBottom(0)
                        .setBorder(new SolidBorder(ColorConstants.PINK, 1f)));
            }

            Div div = new Div().setKeepTogether(true).setKeepWithNext(true)
                    .setBorder(new SolidBorder(ColorConstants.BLUE, 1f));
            div.add(new Paragraph("Moved title").setFontSize(12).setFontColor(ColorConstants.BLUE)
                    .setMarginTop(10).setMarginBottom(0));
            doc.add(div);

            doc.add(createTableWithData(createBigCellTest(45)));
        }

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpFile, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.RENDERER_WAS_NOT_ABLE_TO_PROCESS_KEEP_WITH_NEXT)
    })
    public void combineKeepTogetherDivWithTextTest() throws IOException, InterruptedException {
        String cmpFile = SOURCE_FOLDER + "cmp_combineKeepTogetherDivWithText.pdf";
        String destPdf = DESTINATION_FOLDER + "combineKeepTogetherDivWithText.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destPdf))) {
            Document doc = new Document(pdfDoc);

            for (int i = 0; i < 1; i++) {
                doc.add(new Paragraph("").setFontSize(10).setMarginBottom(0)
                        .setBorder(new SolidBorder(ColorConstants.PINK, 1f)));
            }

            Div div = new Div().setKeepTogether(true).setKeepWithNext(true)
                    .setBorder(new SolidBorder(ColorConstants.BLUE, 1f));
            StringBuilder divText = new StringBuilder();
            for (int i = 0; i < 369; i++) {
                divText.append("some text ");
            }
            div.add(new Paragraph(divText.toString()).setFontSize(12).setFontColor(ColorConstants.BLUE)
                    .setMarginTop(10).setMarginBottom(0));
            doc.add(div);

            doc.add(new Paragraph().add(new Text("BIG TEXT").setFontSize(72)).add(divText.toString()));

            doc.flush();
        }

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpFile, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.RENDERER_WAS_NOT_ABLE_TO_PROCESS_KEEP_WITH_NEXT)
    })
    public void combineKeepTogetherDivWithTableWithImageTest() throws IOException, InterruptedException {
        String cmpFile = SOURCE_FOLDER + "cmp_combineKeepTogetherDivWithTableWithImage.pdf";
        String destPdf = DESTINATION_FOLDER + "combineKeepTogetherDivWithTableWithImage.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destPdf))) {
            Document doc = new Document(pdfDoc);

            for (int i = 0; i < 1; i++) {
                doc.add(new Paragraph("").setFontSize(10).setMarginBottom(0)
                        .setBorder(new SolidBorder(ColorConstants.PINK, 1f)));
            }

            Div div = new Div().setKeepTogether(true).setKeepWithNext(true)
                    .setBorder(new SolidBorder(ColorConstants.BLUE, 1f));
            StringBuilder divText = new StringBuilder();
            for (int i = 0; i < 350; i++) {
                divText.append("some text ");
            }
            div.add(new Paragraph(divText.toString()).setFontSize(12).setFontColor(ColorConstants.BLUE)
                    .setMarginTop(10).setMarginBottom(0));
            doc.add(div);

            doc.add(createTableWithImages());
            doc.flush();
        }

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpFile, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void paragraphTableSameDivKeepNextTest() throws IOException, InterruptedException {
        String cmpFile = SOURCE_FOLDER + "cmp_paragraphTableSameDivKeepNext.pdf";
        String destPdf = DESTINATION_FOLDER + "paragraphTableSameDivKeepNext.pdf";
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(destPdf))) {
            Document document = new Document(pdf, pdf.getDefaultPageSize(), false);

            Div div = new Div().setKeepTogether(true).setKeepWithNext(true);
            div.add(createTableWithData(createBigCellTest(40)));
            document.add(div);
            document.close();
        }

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpFile, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void paragraphTableWithAnotherParagraphKeepNextTest() throws IOException, InterruptedException {
        String cmpFile = SOURCE_FOLDER + "cmp_paragraphTableWithAnotherParagraphKeepNext.pdf";
        String destPdf = DESTINATION_FOLDER + "paragraphTableWithAnotherParagraphKeepNext.pdf";
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(destPdf))) {
            Document document = new Document(pdf, pdf.getDefaultPageSize(), false);

            Div div = new Div().setKeepTogether(true).setKeepWithNext(true);
            div.add(createTableWithData(createBigCellTest(40)));
            document.add(div);
            document.add(new Paragraph("some text here"));
            document.close();
        }

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpFile, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.RENDERER_WAS_NOT_ABLE_TO_PROCESS_KEEP_WITH_NEXT)
    })
    public void combineKeepTogether2TablesTest() throws IOException, InterruptedException {
        String cmpFile = SOURCE_FOLDER + "cmp_combineKeepTogether2Tables.pdf";
        String destPdf = DESTINATION_FOLDER + "combineKeepTogether2Tables.pdf";
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(destPdf))) {
            Document document = new Document(pdf, pdf.getDefaultPageSize(), false);

            Div div1 = new Div().setKeepTogether(true).setKeepWithNext(true);
            div1.add(createTableWithData(createBigCellTest(20)));
            document.add(div1);

            Div div2 = new Div().setKeepTogether(true).setKeepWithNext(true);
            div2.add(createTableWithData(createBigCellTest(20)));
            document.add(div2);

            document.close();
        }

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpFile, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2),
            @LogMessage(messageTemplate = IoLogMessageConstant.RENDERER_WAS_NOT_ABLE_TO_PROCESS_KEEP_WITH_NEXT)
    })
    public void combineKeepTogether2LargeTablesTest() throws IOException, InterruptedException {
        String cmpFile = SOURCE_FOLDER + "cmp_combineKeepTogether2LargeTables.pdf";
        String destPdf = DESTINATION_FOLDER + "combineKeepTogether2LargeTables.pdf";
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(destPdf))) {
            Document document = new Document(pdf, pdf.getDefaultPageSize(), false);

            Div div1 = new Div().setKeepTogether(true).setKeepWithNext(true);
            div1.add(createTableWithData(createBigCellTest(40)));
            document.add(div1);

            Div div2 = new Div().setKeepTogether(true).setKeepWithNext(true);
            div2.add(createTableWithData(createBigCellTest(40)));
            document.add(div2);

            document.close();
        }

        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpFile, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 5)
    })
    public void divWithLargeTableTest() throws IOException, InterruptedException {
        String cmpFile = SOURCE_FOLDER + "cmp_divWithLargeTable.pdf";
        String destPdf = DESTINATION_FOLDER + "divWithLargeTable.pdf";
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(destPdf))) {
            Document document = new Document(pdf, pdf.getDefaultPageSize(), false);
            document.immediateFlush = false;

            Div div2 = new Div().setKeepTogether(true).setKeepWithNext(true);
            div2.add(createTableWithData(createBigCellTest(150)));
            document.add(div2);

            document.close();
        }
        Assertions.assertNull(new CompareTool().compareByContent(destPdf, cmpFile, DESTINATION_FOLDER, "diff"));
    }

    private String createBigCellTest(int numRepeats) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < numRepeats; ++i) {
            buf.append("Test ").append(i) .append(" ");
        }

        String bigText = buf.toString();
        return bigText;
    }

    private static Table createTableWithData(String mainCellText) {
        Table table = new Table(UnitValue.createPercentArray(new float[] { 5, 25, 25 }));
        table.setWidth(UnitValue.createPercentValue(100)).setMarginTop(0).setMarginBottom(5);
        table.setBorder(Border.NO_BORDER);
        table.setVerticalAlignment(VerticalAlignment.BOTTOM).setPadding(0);
        table.setFixedLayout();

        String[] headerTitles = { "Title1", "Title2", "Title3" };

        for (String headerTitle : headerTitles) {
            table.addHeaderCell(createCell(headerTitle, 8, Border.NO_BORDER));
        }

        Border grayBorder = new SolidBorder(ColorConstants.LIGHT_GRAY, 0.75f);
        for (int i = 0; i < 2; i++) {
            table.addCell(createCell(mainCellText, 10, grayBorder));
            table.addCell(createCell("Col2_" + i, 10, grayBorder));
            table.addCell(createCell("Col3_" + i, 10, grayBorder));
        }

        return table;
    }

    private static Table createTableWithImages() throws MalformedURLException {
        Table table = new Table(UnitValue.createPercentArray(new float[] { 25, 5, 5 }));
        table.setWidth(UnitValue.createPercentValue(100)).setMarginTop(0).setMarginBottom(5);
        table.setBorder(Border.NO_BORDER);
        table.setVerticalAlignment(VerticalAlignment.BOTTOM).setPadding(0);
        table.setFixedLayout();

        String[] headerTitles = { "Title1", "Title2", "Title3" };

        for (String headerTitle : headerTitles) {
            table.addHeaderCell(createCell(headerTitle, 8, Border.NO_BORDER));
        }

        Border grayBorder = new SolidBorder(ColorConstants.LIGHT_GRAY, 0.75f);
        for (int i = 0; i < 2; i++) {
            Cell cell = new Cell().add(new Image(ImageDataFactory.create("./src/test/resources/com/itextpdf/layout/ImageTest/itis.jpg")));
            cell.setBorder(grayBorder);
            table.addCell(cell);
            table.addCell(createCell("Col2_" + i, 10, grayBorder));
            table.addCell(createCell("Col3_" + i, 10, grayBorder));
        }

        return table;
    }

    private static Cell createCell(String cellText, float fontSize, Border border) {
        Paragraph p = new Paragraph(cellText);
        p.setFontSize(fontSize);
        p.setPaddingBottom(0);
        p.setKeepTogether(true);

        Cell c = new Cell();
        c.setBorder(border);
        c.setPaddingLeft(0);
        c.setPaddingBottom(0);
        c.setKeepTogether(true);
        c.add(p);
        return c;
    }

}
