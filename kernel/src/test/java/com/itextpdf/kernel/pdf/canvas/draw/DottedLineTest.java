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
package com.itextpdf.kernel.pdf.canvas.draw;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.CharacterRenderInfo;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Stack;

@Tag("UnitTest")
public class DottedLineTest extends ExtendedITextTest {

    @Test
    public void defaultDottedLineTest01() {
        DottedLine dottedLine = new DottedLine();

        Assertions.assertEquals(ColorConstants.BLACK, dottedLine.getColor());
        Assertions.assertEquals(1, dottedLine.getLineWidth(), 0.0001);
        Assertions.assertEquals(4, dottedLine.getGap(), 0.0001);
    }


    @Test
    public void dottedLineWithSetWidthTest01() {
        DottedLine dottedLine = new DottedLine(20);

        Assertions.assertEquals(ColorConstants.BLACK, dottedLine.getColor());
        Assertions.assertEquals(4, dottedLine.getGap(), 0.0001);
        Assertions.assertEquals(20, dottedLine.getLineWidth(), 0.0001);
    }

    @Test
    public void dottedLineWithSetWidthAndGapTest01() {
        DottedLine dottedLine = new DottedLine(10, 15);

        Assertions.assertEquals(ColorConstants.BLACK, dottedLine.getColor());
        Assertions.assertEquals(10, dottedLine.getLineWidth(), 0.0001);
        Assertions.assertEquals(15, dottedLine.getGap(), 0.0001);
    }



    @Test
    public void dottedLineSettersTest01() {
        DottedLine dottedLine = new DottedLine(15);
        Assertions.assertEquals(ColorConstants.BLACK, dottedLine.getColor());
        Assertions.assertEquals(15, dottedLine.getLineWidth(), 0.0001);
        Assertions.assertEquals(4, dottedLine.getGap(), 0.0001);


        dottedLine.setColor(ColorConstants.RED);
        Assertions.assertEquals(ColorConstants.RED, dottedLine.getColor());

        dottedLine.setLineWidth(10);
        Assertions.assertEquals(10, dottedLine.getLineWidth(), 0.0001);

        dottedLine.setGap(5);
        Assertions.assertEquals(5, dottedLine.getGap(), 0.0001);

    }

    @Test
    public void dottedLineDrawTest01() {
        String expectedContent = "q\n" +
                "15 w\n" +
                "0 0 0 RG\n" +
                "[0 5] 2.5 d\n" +
                "1 J\n" +
                "100 107.5 m\n" +
                "200 107.5 l\n" +
                "S\n" +
                "Q\n";

        PdfDocument tempDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfCanvas canvas = new PdfCanvas(tempDoc.addNewPage());

        DottedLine dottedLine = new DottedLine(15, 5);
        dottedLine.draw(canvas, new Rectangle(100, 100, 100, 100));

        byte[] writtenContentBytes = canvas.getContentStream().getBytes();

        Assertions.assertArrayEquals(expectedContent.getBytes(), writtenContentBytes);
    }
}
