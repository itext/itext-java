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
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Category(UnitTest.class)
public class TableRendererTest  extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count=13)})
    public void calculateColumnWidthsNotPointValue() throws FileNotFoundException {
        junitExpectedException.expect(NullPointerException.class);

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream("table_out.pdf")));
        Document doc = new Document(pdfDoc);
        LayoutArea area = new LayoutArea(1, new Rectangle(0,0,100,100));
        LayoutContext layoutContext = new LayoutContext(area);
        Rectangle layoutBox = area.getBBox().clone();


        Table table = new Table(UnitValue.createPercentArray(new float[]{10, 10, 80}));
        table.setProperty(Property.MARGIN_RIGHT, UnitValue.createPercentValue(7));
        table.setProperty(Property.MARGIN_LEFT, UnitValue.createPercentValue(7));
        table.setProperty(Property.PADDING_RIGHT, UnitValue.createPercentValue(7));
        table.setProperty(Property.PADDING_LEFT, UnitValue.createPercentValue(7));
        table.addCell("Col a");
        table.addCell("Col b");
        table.addCell("Col c");
        table.addCell("Value a");
        table.addCell("Value b");
        table.addCell("This is a long description for column c. " +
                "It needs much more space hence we made sure that the third column is wider.");
        doc.add(table);

        TableRenderer tableRenderer = (TableRenderer) table.getRenderer();

        tableRenderer.applyMarginsAndPaddingsAndCalculateColumnWidths(layoutBox);

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
