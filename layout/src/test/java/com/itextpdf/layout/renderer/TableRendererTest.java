/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class TableRendererTest extends ExtendedITextTest {

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count = 6)})
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
        Assert.assertEquals(minMaxWidth.getMaxWidth(), 332.46f, 0.001);
        Assert.assertEquals(minMaxWidth.getMinWidth(), 332.46f, 0.001);
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

        Assert.assertFalse(grandChildren[0].isOriginalNonSplitRenderer);
    }
}
