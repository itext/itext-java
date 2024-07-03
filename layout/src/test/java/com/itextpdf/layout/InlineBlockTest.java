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

import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;

import java.io.FileNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class InlineBlockTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/InlineBlockTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/InlineBlockTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void inlineTableTest01() throws IOException, InterruptedException {
        // TODO DEVSIX-1967
        String name = "inlineTableTest01.pdf";
        String outFileName = destinationFolder + name;
        String cmpFileName = sourceFolder + "cmp_" + name;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);


        Paragraph p = new Paragraph().setMultipliedLeading(0);
        p.add(new Paragraph("This is inline table: ").setBorder(new SolidBorder(1)).setMultipliedLeading(0));
        Table inlineTable = new Table(1);
        int commonPadding = 10;
        Cell cell = new Cell();
        Paragraph paragraph = new Paragraph("Cell 1");
        inlineTable.addCell(cell.add(paragraph.setMultipliedLeading(0)).setPadding(commonPadding).setWidth(33));
        Div div = new Div();
        p.add(div.add(inlineTable).setPadding(commonPadding))
                .add(new Paragraph(". Was it fun?").setBorder(new SolidBorder(1)).setMultipliedLeading(0));

        SolidBorder border = new SolidBorder(1);

        doc.add(p);

        Paragraph p1 = new Paragraph().add(p).setBorder(border);
        doc.add(p1);

        Paragraph p2 = new Paragraph().add(p1).setBorder(border);
        doc.add(p2);

        Paragraph p3 = new Paragraph().add(p2).setBorder(border);
        doc.add(p3);

        Paragraph p4 = new Paragraph().add(p3).setBorder(border);
        doc.add(p4);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void deepNestingInlineBlocksTest01() throws IOException, InterruptedException {
        // TODO DEVSIX-1963
        String name = "deepNestingInlineBlocksTest01.pdf";
        String outFileName = destinationFolder + name;
        String cmpFileName = sourceFolder + "cmp_" + name;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Color[] colors = new Color[]{ColorConstants.BLUE, ColorConstants.RED, ColorConstants.LIGHT_GRAY, ColorConstants.ORANGE};
        int w = 60;
        int n = 6;
        Paragraph p = new Paragraph("hello world").setWidth(w);
        for (int i = 0; i < n; ++i) {
            Paragraph currP = new Paragraph().setWidth(i == 0 ? w * 1.1f * 3 : 450 + 5 * i);
            currP.add(p).add(p).add(p).setBorder(new DashedBorder(colors[i % colors.length], 0.5f));
            p = currP;
        }
        long start = SystemUtil.getRelativeTimeMillis();
        doc.add(p);

        // 606 on local machine (including jvm warming up)
        System.out.println(SystemUtil.getRelativeTimeMillis() - start);

        p = new Paragraph("hello world");
        for (int i = 0; i < n; ++i) {
            Paragraph currP = new Paragraph();
            currP.add(p).add(p).add(p).setBorder(new DashedBorder(colors[i % colors.length], 0.5f));
            p = currP;
        }
        start = SystemUtil.getRelativeTimeMillis();
        doc.add(p);

        // 4656 on local machine
        System.out.println(SystemUtil.getRelativeTimeMillis() - start);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void wrappingAfter100PercentWidthFloatTest() throws IOException, InterruptedException {
        String name = "wrappingAfter100PercentWidthFloatTest.pdf";
        String output = destinationFolder + name;
        String cmp = sourceFolder + "cmp_" + name;

        try (Document doc = new Document(new PdfDocument(new PdfWriter(output)))) {
            Div floatingDiv = new Div()
                    .setWidth(UnitValue.createPercentValue(100))
                    .setHeight(10)
                    .setBorder(new SolidBorder(1))
                    .setBackgroundColor(ColorConstants.RED);
            floatingDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
            floatingDiv.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
            floatingDiv.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
            Div inlineDiv = new Div()
                    .setWidth(UnitValue.createPercentValue(100))
                    .setHeight(10)
                    .setBorder(new SolidBorder(1))
                    // gold color
                    .setBackgroundColor(new DeviceRgb(255, 215, 0));
            inlineDiv.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
            inlineDiv.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);

            doc.add(new Div()
                    .add(floatingDiv)
                    .add(new Paragraph().add(inlineDiv))
            );
        }

        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, destinationFolder));
    }
}
