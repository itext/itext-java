/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Tag("UnitTest")
public class TableWidthsTest  extends ExtendedITextTest {

    @Test
    public void testprocessCellRemainWidth01() {
        TableRenderer tableRenderer = createTableRendererWithDiffColspan(150);

        TableWidths tableWidths = new TableWidths(tableRenderer, 150, true, 15, 15);
        List<TableWidths.CellInfo> cells = tableWidths.autoLayoutCustom();

        for(TableWidths.CellInfo cell : cells) {
            tableWidths.processCell(cell);
        }
    }

    @Test
    public void testProcessCellsRemainWidth02() {
        TableRenderer tableRenderer = createTableRendererWithDiffColspan(320);

        TableWidths tableWidths = new TableWidths(tableRenderer, 150, true, 15, 15);
        List<TableWidths.CellInfo> cells = tableWidths.autoLayoutCustom();

        for(TableWidths.CellInfo cell : cells) {
            tableWidths.processCell(cell);
        }

    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.SUM_OF_TABLE_COLUMNS_IS_GREATER_THAN_100, count=2)})
    public void testSumOfColumnsIsGreaterThan100(){
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document doc = new Document(pdfDoc);
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}));
        table.setWidth(UnitValue.createPercentValue(80));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        Cell c1 = new Cell(1, 3);
        c1.setProperty(Property.WIDTH, UnitValue.createPercentValue(200));
        c1.add(new Paragraph("Cell with colspan 3"));
        table.addCell(c1);
        table.addCell(new Cell(2, 1).add(new Paragraph("Cell with rowspan 2")));
        table.addCell(new Cell().add(new Paragraph("row 1; cell 1")).setMinWidth(200));
        table.addCell(new Cell().add(new Paragraph("row 1; cell 2")).setMaxWidth(50));
        table.addCell("row 2; cell 1");
        table.addCell("row 2; cell 2");

        TableRenderer tableRenderer = new TableRenderer(table);
        CellRenderer[] row1 = {new CellRenderer(table.getCell(0,0)), null, null};
        CellRenderer[] row2 = {null, new CellRenderer(table.getCell(1,1)), new CellRenderer(table.getCell(1,2))};
        CellRenderer[] row3 = {new CellRenderer(table.getCell(1,0)), new CellRenderer(table.getCell(2,1)),  new CellRenderer(table.getCell(2,2))};
        tableRenderer.rows.set(0, row1);
        tableRenderer.rows.set(1, row2);
        tableRenderer.rows.set(2, row3);

        table.setBorder(new SolidBorder(ColorConstants.GREEN, 5));
        tableRenderer.bordersHandler = new SeparatedTableBorders(tableRenderer.rows, table.getNumberOfColumns(), tableRenderer.getBorders());

        TableWidths tableWidths = new TableWidths(tableRenderer, 150, true, 15, 15);
        List<TableWidths.CellInfo> cells = tableWidths.autoLayoutCustom();

        for(TableWidths.CellInfo cell : cells) {
            tableWidths.processCell(cell);
        }

        doc.add(table);
        doc.close();
    }

    @Test
    public void testProcessCellPointWidthValue() {
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setMarginTop(5);
        for(int i = 0; i < 4; i++) {
            Cell cell = new Cell().add(new Paragraph("smth" + i));
            cell.setProperty(Property.WIDTH, UnitValue.createPointValue(250));
            table.addCell(cell);
        }

        TableRenderer tableRenderer = new TableRenderer(table);
        CellRenderer[] row1 = {new CellRenderer(table.getCell(0,0)), new CellRenderer(table.getCell(0,1))};
        CellRenderer[] row2 = {new CellRenderer(table.getCell(1,0)), new CellRenderer(table.getCell(1,1))};
        tableRenderer.rows.set(0, row1);
        tableRenderer.rows.set(1, row2);

        table.setBorder(new SolidBorder(ColorConstants.GREEN, 5));
        tableRenderer.bordersHandler = new SeparatedTableBorders(tableRenderer.rows, table.getNumberOfColumns(), tableRenderer.getBorders());

        TableWidths tableWidths = new TableWidths(tableRenderer, 150, true, 15, 15);
        List<TableWidths.CellInfo> cells = tableWidths.autoLayoutCustom();

        for(TableWidths.CellInfo cell : cells) {
            tableWidths.processCell(cell);
        }
    }

    @Test
    public void testProcessCellsWithPercentWidth01() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document doc = new Document(pdfDoc);
        doc.add(new Paragraph("With 2 columns:"));
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setMarginTop(5);
        for(int i = 0; i < 4; i++) {
            Cell cell = new Cell().add(new Paragraph("smth" + i));
            cell.setProperty(Property.WIDTH, UnitValue.createPercentValue(50));
            table.addCell(cell);
        }

        TableRenderer tableRenderer = new TableRenderer(table);
        CellRenderer[] row1 = {new CellRenderer(table.getCell(0,0)), new CellRenderer(table.getCell(0,1))};
        CellRenderer[] row2 = {new CellRenderer(table.getCell(1,0)), new CellRenderer(table.getCell(1,1))};
        tableRenderer.rows.set(0, row1);
        tableRenderer.rows.set(1, row2);

        table.setBorder(new SolidBorder(ColorConstants.GREEN, 5));
        tableRenderer.bordersHandler = new SeparatedTableBorders(tableRenderer.rows, table.getNumberOfColumns(), tableRenderer.getBorders());

        TableWidths tableWidths = new TableWidths(tableRenderer, 150, true, 15, 15);
        List<TableWidths.CellInfo> cells = tableWidths.autoLayoutCustom();

        for(TableWidths.CellInfo cell : cells) {
            tableWidths.processCell(cell);
        }

        tableWidths.recalculate(25);

        doc.close();
    }

    @Test
    public void testProcessCellsWithPercentWidth02() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document doc = new Document(pdfDoc);
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}));
        table.setWidth(UnitValue.createPercentValue(80));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        Cell c1 = new Cell(1, 3);
        c1.setProperty(Property.WIDTH, UnitValue.createPercentValue(90));
        c1.add(new Paragraph("Cell with colspan 3"));
        table.addCell(c1);
        Cell c2 = new Cell(2, 1);
        c2.add(new Paragraph("Cell with rowspan 2"));
        c2.setProperty(Property.WIDTH, UnitValue.createPercentValue(50));
        table.addCell(c2);
        table.addCell(new Cell().add(new Paragraph("row 1; cell 1")).setMinWidth(200));
        table.addCell(new Cell().add(new Paragraph("row 1; cell 2")).setMaxWidth(50));
        table.addCell("row 2; cell 1");
        table.addCell("row 2; cell 2");

        TableRenderer tableRenderer = new TableRenderer(table);
        CellRenderer[] row1 = {new CellRenderer(table.getCell(0,0)), null, null};
        CellRenderer[] row2 = {null, new CellRenderer(table.getCell(1,1)), new CellRenderer(table.getCell(1,2))};
        CellRenderer[] row3 = {new CellRenderer(table.getCell(1,0)), new CellRenderer(table.getCell(2,1)),  new CellRenderer(table.getCell(2,2))};
        tableRenderer.rows.set(0, row1);
        tableRenderer.rows.set(1, row2);
        tableRenderer.rows.set(2, row3);

        table.setBorder(new SolidBorder(ColorConstants.GREEN, 5));
        tableRenderer.bordersHandler = new SeparatedTableBorders(tableRenderer.rows, table.getNumberOfColumns(), tableRenderer.getBorders());

        TableWidths tableWidths = new TableWidths(tableRenderer, 150, true, 15, 15);
        List<TableWidths.CellInfo> cells = tableWidths.autoLayoutCustom();

        for(TableWidths.CellInfo cell : cells) {
            tableWidths.processCell(cell);
        }

        tableWidths.recalculate(200);

        doc.add(table);
        doc.close();
    }

    private Table createTableWithDiffColspan(int maxWidth) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}));
        table.setWidth(UnitValue.createPercentValue(80));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        Cell c1 = new Cell(1, 3);
        c1.setProperty(Property.WIDTH, UnitValue.createPointValue(maxWidth));
        c1.add(new Paragraph("Cell with colspan 3"));
        table.addCell(c1);
        table.addCell(new Cell(2, 1).add(new Paragraph("Cell with rowspan 2")));
        table.addCell(new Cell().add(new Paragraph("row 1; cell 1")).setMinWidth(200));
        table.addCell(new Cell().add(new Paragraph("row 1; cell 2")).setMaxWidth(50));
        table.addCell("row 2; cell 1");
        table.addCell("row 2; cell 2");

        table.setBorder(new SolidBorder(ColorConstants.GREEN, 5));

        return table;
    }

    private TableRenderer createTableRendererWithDiffColspan(int maxWidth) {
        Table table = createTableWithDiffColspan(maxWidth);
        TableRenderer tableRenderer = new TableRenderer(table);
        CellRenderer[] row1 = {new CellRenderer(table.getCell(0,0)), null, null};
        CellRenderer[] row2 = {null, new CellRenderer(table.getCell(1,1)), new CellRenderer(table.getCell(1,2))};
        CellRenderer[] row3 = {new CellRenderer(table.getCell(1,0)), new CellRenderer(table.getCell(2,1)),  new CellRenderer(table.getCell(2,2))};
        tableRenderer.rows.set(0, row1);
        tableRenderer.rows.set(1, row2);
        tableRenderer.rows.set(2, row3);

        tableRenderer.bordersHandler = new SeparatedTableBorders(tableRenderer.rows, table.getNumberOfColumns(), tableRenderer.getBorders());

        return tableRenderer;
    }

}
