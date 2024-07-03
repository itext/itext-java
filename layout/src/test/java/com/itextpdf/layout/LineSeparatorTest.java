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

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class LineSeparatorTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/LineSeparatorTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/LineSeparatorTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void lineSeparatorWidthPercentageTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "lineSeparatorWidthPercentageTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_lineSeparatorWidthPercentageTest01.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        ILineDrawer line1 = new SolidLine();
        line1.setColor(ColorConstants.RED);
        ILineDrawer line2 = new SolidLine();
        document.add(new LineSeparator(line1).setWidth(50).setMarginBottom(10));
        document.add(new LineSeparator(line2).setWidth(UnitValue.createPercentValue(50)));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void lineSeparatorBackgroundTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "lineSeparatorBackgroundTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_lineSeparatorBackgroundTest01.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        Style style = new Style();
        style.setBackgroundColor(ColorConstants.YELLOW);
        style.setMargin(10);
        document.add(new LineSeparator(new SolidLine()).addStyle(style));

        document.add(new LineSeparator(new DashedLine()).setBackgroundColor(ColorConstants.RED));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
    public void rotatedLineSeparatorTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "rotatedLineSeparatorTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_rotatedLineSeparatorTest01.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        document.add(new LineSeparator(new DashedLine()).setBackgroundColor(ColorConstants.RED).setRotationAngle(Math.PI / 2));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void rotatedLineSeparatorTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "rotatedLineSeparatorTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_rotatedLineSeparatorTest02.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        document.add(new Paragraph("Hello"));
        document.add(new LineSeparator(new DashedLine()).setWidth(100).setHorizontalAlignment(HorizontalAlignment.CENTER).
                setBackgroundColor(ColorConstants.GREEN).setRotationAngle(Math.PI / 4));
        document.add(new Paragraph("World"));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
