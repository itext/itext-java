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
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class TableRendererTest extends ExtendedITextTest {

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
}
