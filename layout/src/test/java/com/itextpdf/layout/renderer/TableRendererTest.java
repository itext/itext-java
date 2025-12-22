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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.BorderCollapsePropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class TableRendererTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/TableRendererTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/TableRendererTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 6)})
    public void calculateColumnWidthsNotPointValue() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document doc = new Document(pdfDoc);

        Rectangle layoutBox = new Rectangle(0, 0, 1000, 100);

        Table table = new Table(UnitValue.createPercentArray(new float[] {10, 10, 80}));

        // Set margins and paddings in percents, which is not expected
        table.setProperty(Property.MARGIN_RIGHT, UnitValue.createPercentValue(7));
        table.setProperty(Property.MARGIN_LEFT, UnitValue.createPercentValue(7));
        table.setProperty(Property.PADDING_RIGHT, UnitValue.createPercentValue(7));
        table.setProperty(Property.PADDING_LEFT, UnitValue.createPercentValue(7));

        // Fill the table somehow. The layout area is wide enough to calculate the widths as expected
        for (int i = 0; i < 3; i++) {
            table.addCell("Hello");
        }

        // Create a TableRenderer, the instance of which will be used to test the application of margins and paddings
        TableRenderer tableRenderer = (TableRenderer) table.createRendererSubTree().setParent(doc.getRenderer());
        tableRenderer.bordersHandler = (TableBorders) new SeparatedTableBorders(tableRenderer.rows, 3,
                tableRenderer.getBorders(), 0);

        tableRenderer.applyMarginsAndPaddingsAndCalculateColumnWidths(layoutBox);

        // Specify that the render is not original in order not to recalculate the column widths
        tableRenderer.isOriginalNonSplitRenderer = false;

        MinMaxWidth minMaxWidth = tableRenderer.getMinMaxWidth();
        // TODO DEVSIX-3676: currently margins and paddings are still applied as if they are in points. After the mentioned ticket is fixed, the expected values should be updated.
        Assertions.assertEquals(327.46f, minMaxWidth.getMaxWidth(), 0.001);
        Assertions.assertEquals(327.46f, minMaxWidth.getMinWidth(), 0.001);
    }

    @Test
    public void testIsOriginalNonSplitRenderer() {
        Table table = new Table(1);
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());

        TableRenderer original = (TableRenderer) table.createRendererSubTree();
        TableRenderer[] children = original.split(1);

        TableRenderer[] grandChildren = children[1].split(1);

        Assertions.assertFalse(grandChildren[0].isOriginalNonSplitRenderer);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH),
    })
    public void nestedTableWithSpecifiedWidthTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "nestedTableWithSpecifiedWidth.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_nestedTableWithSpecifiedWidth.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(2);

        Cell cell1 = new Cell(1, 1);
        cell1.setBorder(new SolidBorder(ColorConstants.GRAY, 1.5f));

        Table nestedTable = new Table(1);
        nestedTable.setWidth(422.25f);
        nestedTable.setHeight(52.5f);
        Paragraph paragraph = new Paragraph("Hello");
        paragraph.setBorder(new SolidBorder(ColorConstants.GREEN, 1.5f));
        nestedTable.addCell(paragraph);
        cell1.add(nestedTable);
        table.addCell(cell1);

        Cell cell2 = new Cell(2, 1);
        cell2.setBorder(new SolidBorder(ColorConstants.YELLOW, 1.5f));
        Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        image.setWidth(406.5f);
        image.setHeight(7.5f);
        cell2.add(image);
        table.addCell(cell2);

        doc.add(table);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void collapsedBorderRowspanOnPageSplitTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "collapsedBorderRowspanOnPageSplit.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_collapsedBorderRowspanOnPageSplit.pdf";

        try (Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)))) {

            Div dummyDiv = new Div();
            dummyDiv.setBorder(new SolidBorder(ColorConstants.BLACK, 1.5f));
            dummyDiv.setWidth(400);
            dummyDiv.setHeight(720);
            doc.add(dummyDiv);

            Table table = new Table(2);
            table.setBorderCollapse(BorderCollapsePropertyValue.COLLAPSE);
            table.setBorder(new SolidBorder(ColorConstants.BLACK, 1.5f));

            Cell cell1 = new Cell(4, 1);
            cell1.setBackgroundColor(ColorConstants.GRAY);
            cell1.add(new Paragraph("Text 0"));
            table.addCell(cell1);

            Cell cell2 = new Cell(4, 1);
            cell2.add(new Paragraph("Text 1"));
            cell2.add(new Paragraph("Text 2"));
            cell2.add(new Paragraph("Text 3"));
            cell2.add(new Paragraph("Text 4"));
            table.addCell(cell2);

            doc.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }
}
