/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
