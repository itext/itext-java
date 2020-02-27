/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CanvasUnitTest extends ExtendedITextTest {

    @Test
    public void canvasImmediateFlushConstructorTest() {
        PdfDocument pdf = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdf.addNewPage();
        PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf);
        Rectangle rectangle = new Rectangle(0, 0);
        Canvas canvas = new Canvas(pdfCanvas, rectangle, false);

        Assert.assertEquals(pdfCanvas.getDocument(), canvas.getPdfDocument());
        Assert.assertFalse(canvas.immediateFlush);
    }

    @Test
    //TODO remove test after deprecated constructor is removed
    public void canvasImmediateFlushDeprecatedConstructorTest() {
        PdfDocument pdf = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf);
        Rectangle rectangle = new Rectangle(0, 0);
        Canvas canvas = new Canvas(pdfCanvas, pdf, rectangle, false);

        Assert.assertEquals(pdfCanvas.getDocument(), canvas.getPdfDocument());
        Assert.assertFalse(canvas.immediateFlush);
    }
}
