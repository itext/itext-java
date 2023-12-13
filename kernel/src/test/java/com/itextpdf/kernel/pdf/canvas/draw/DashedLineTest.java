/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Stack;

@Category(UnitTest.class)
public class DashedLineTest extends ExtendedITextTest {

    @Test
    public void defaultDashedLineTest01() {
        DashedLine dashedLine = new DashedLine();

        Assert.assertEquals(ColorConstants.BLACK, dashedLine.getColor());
        Assert.assertEquals(1, dashedLine.getLineWidth(), 0.0001);
    }


    @Test
    public void dashedLineWithSetWidthTest01() {
        DashedLine dashedLine = new DashedLine(20);

        Assert.assertEquals(ColorConstants.BLACK, dashedLine.getColor());
        Assert.assertEquals(20, dashedLine.getLineWidth(), 0.0001);
    }

    @Test
    public void dashedLineSettersTest01() {
        DashedLine dashedLine = new DashedLine(15);
        Assert.assertEquals(ColorConstants.BLACK, dashedLine.getColor());
        Assert.assertEquals(15, dashedLine.getLineWidth(), 0.0001);

        dashedLine.setColor(ColorConstants.RED);
        Assert.assertEquals(ColorConstants.RED, dashedLine.getColor());

        dashedLine.setLineWidth(10);
        Assert.assertEquals(10, dashedLine.getLineWidth(), 0.0001);
    }

    @Test
    public void dashedLineDrawTest01() {
        String expectedContent = "q\n" +
                "15 w\n" +
                "0 0 0 RG\n" +
                "[2] 2 d\n" +
                "100 107.5 m\n" +
                "200 107.5 l\n" +
                "S\n" +
                "Q\n";

        PdfDocument tempDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfCanvas canvas = new PdfCanvas(tempDoc.addNewPage());

        DashedLine dashedLine = new DashedLine(15);
        dashedLine.draw(canvas, new Rectangle(100, 100, 100, 100));

        byte[] writtenContentBytes = canvas.getContentStream().getBytes();

        Assert.assertArrayEquals(expectedContent.getBytes(), writtenContentBytes);
    }
}
