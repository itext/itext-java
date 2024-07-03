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
package com.itextpdf.layout;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class LeadingHeightTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/LeadingHeightTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/LeadingHeightTest/";

    private static final int HEIGHT_LESS_THAN_REQUIRED = -2;
    private static final int HEIGHT_IS_NOT_SET = -1;
    private static final int HEIGHT_EXACT_THAT_REQUIRED = 0;
    private static final int HEIGHT_MORE_THAN_REQUIRED = 100;

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 2)
    })
    @Test
    public void clippedHeightParagraphTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "leadingTestHeight.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_leadingTestHeight.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        Document doc = new Document(pdfDoc, new PageSize(700, 700));
        addDescription(doc, 600, "This is how table looks like if no height property is set");
        addTable(doc, 504, "RETIREMENT PLANNING: BECAUSE YOU CAN’T BE A FINANCIAL PLANNER FOREVER.", HEIGHT_IS_NOT_SET);
        addDescription(doc, 456, "Here we set value from pre layout as height. We expect that this table shall be equal to the previous one");
        addTable(doc, 360, "RETIREMENT PLANNING: BECAUSE YOU CAN’T BE A FINANCIAL PLANNER FOREVER.", HEIGHT_EXACT_THAT_REQUIRED);
        addDescription(doc, 312, "Here we set 100 as height. We expect that this will be enough to place 3 lines");
        addTable(doc, 216, "RETIREMENT PLANNING: BECAUSE ***SOME TEST TEXT IS PLACED*** YOU CAN’T BE A FINANCIAL PLANNER FOREVER.", HEIGHT_MORE_THAN_REQUIRED);
        addDescription(doc, 146, "Here we set value from pre layout minus 0.5f as height. We expect that this table shall not be equal to the previous one");
        addTable(doc, 50, "RETIREMENT PLANNING: BECAUSE YOU CAN’T BE A FINANCIAL PLANNER FOREVER.", HEIGHT_LESS_THAN_REQUIRED);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void paragraphTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pageHeightParagraphTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_pageHeightParagraphTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        //Document height = 176 = 104 + 36 + 36, where 104 - is exact size of paragraph after layout and 34 + 34 - page margins
        Document doc = new Document(pdfDoc, new PageSize(700, 176));
        Paragraph ph = new Paragraph();
        Text txt = new Text("RETIREMENT PLANNING: BECAUSE YOU CAN’T BE A FINANCIAL PLANNER FOREVER.");
        txt.setFontSize(32f);
        ph.add(txt);
        ph.setFixedLeading(32f);
        ph.setPaddingTop(0f);
        ph.setPaddingBottom(0f);
        ph.setWidth(585f);

        doc.add(ph);
        doc.close();
        //Partial text expected to be present in the document
        //There should be only "RETIREMENT PLANNING: BECAUSE YOU CAN’T BE A FINANCIAL"
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void pageHeightDivWithNestedParagraphTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pageHeightParagraphWorkAroundTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_pageHeightParagraphWorkAroundTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        //Document height = 176 = 104 + 36 + 36, where 104 - is exact size of paragraph after layout and 34 + 34 - page margins
        Document doc = new Document(pdfDoc, new PageSize(700, 176));
        Paragraph ph = new Paragraph();
        Text txt = new Text("RETIREMENT PLANNING: BECAUSE YOU CAN’T BE A FINANCIAL PLANNER FOREVER.");
        txt.setFontSize(32f);
        ph.add(txt);
        ph.setFixedLeading(32f);
        ph.setPaddingTop(0f);
        ph.setPaddingBottom(0f);
        ph.setWidth(585f);


        Div ph2 = new Div();
        ph2.setHeight(104);
        ph2.setMargin(0);
        ph2.setPadding(0);
        ph2.add(ph);
        ph2.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);;
        doc.add(ph2);
        doc.close();
        //Full text expected to be present on the first page
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    private void addTable(Document doc, int y, String text, int heightParam)
    {
        float width = 585f;

        Table table = new Table(1);
        table.setWidth(width);
        table.setFixedLayout();

        Cell cell = addCell(table, text);

        // find out how tall the cell is we just added
        LayoutResult result = table.createRendererSubTree()
                .setParent(doc.getRenderer())
                .layout(
                        new LayoutContext(
                                new LayoutArea(
                                        1,
                                        new Rectangle(0, 0, width, 10000.0F)
                                )
                        )
                );

        String heightStr = "Natural";
        if (heightParam == HEIGHT_LESS_THAN_REQUIRED) {
            float rowHeight = result.getOccupiedArea().getBBox().getHeight();
            cell.setHeight(rowHeight - 0.5f);
            heightStr = "Calculated " + (rowHeight - 0.5f);
        }
        if (heightParam == HEIGHT_EXACT_THAT_REQUIRED)
        {
            float rowHeight = result.getOccupiedArea().getBBox().getHeight();
            cell.setHeight(rowHeight);
            heightStr = "Calculated " + rowHeight;
            if (heightStr.endsWith(".0")) {
                heightStr = heightStr.substring(0, heightStr.length() - 2);
            }
        }
        else if (heightParam > 0)
        {
            cell.setHeight(heightParam);
            heightStr = "Explicit " + heightParam;
        }

        table.setFixedPosition((float) 36, (float) y, width);

        doc.add(table);
        addCellFooter(doc, y, width, heightStr);
    }

    private Cell addCell(Table table, String text) {
        Paragraph ph = new Paragraph();
        Text txt = new Text(text);
        txt.setFontSize(32f);
        ph.add(txt);
        ph.setFixedLeading(32f);

        Cell cell = new Cell();
        cell.setPaddingTop(0f);
        cell.setPaddingBottom(0f);
        cell.add(ph);
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        cell.setBorder(null);

        table.addCell(cell);
        return cell;
    }

    private void addCellFooter(Document doc, float y, float width, String heightStr) {
        Table t2 = new Table(1);
        t2.setWidth(width);
        t2.setFixedLayout();
        Cell c2 = new Cell();
        c2.setTextAlignment(TextAlignment.CENTER);
        c2.setWidth(width);
        c2.setBorder(Border.NO_BORDER);
        c2.add(new Paragraph("Row Height: " + heightStr));
        t2.addCell(c2);
        t2.setFixedPosition((float) 36, (float) y-18, width);
        doc.add(t2);
    }

    private void addDescription(Document doc, float y, String text) {
        Paragraph ph = new Paragraph();
        Text txt = new Text(text);
        txt.setFontSize(12f);
        ph.add(txt);
        ph.setFontColor(ColorConstants.RED);
        ph.setFixedPosition(1, 40, y, 585f);
        doc.add(ph);
    }
}
