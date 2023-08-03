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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfObjectStreamUnitTest extends ExtendedITextTest {

    @Test
    public void cannotAddMoreObjectsThanMaxStreamSizeTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfObjectStream pdfObjectStream = new PdfObjectStream(pdfDocument);
        PdfNumber number = new PdfNumber(1);
        number.makeIndirect(pdfDocument);
        //Here we add the maximum number of objects, so that adding the next one will cause an exception
        for (int i = 0; i < PdfObjectStream.MAX_OBJ_STREAM_SIZE; i++) {
            pdfObjectStream.addObject(number);
        }
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> pdfObjectStream.addObject(number));
        Assert.assertEquals(KernelExceptionMessageConstant.PDF_OBJECT_STREAM_REACH_MAX_SIZE,
                exception.getMessage());
    }

    @Test
    public void objectCanBeAddedToObjectStreamWithSizeLessThenMaxStreamSizeTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfObjectStream pdfObjectStream = new PdfObjectStream(pdfDocument);
        PdfNumber number = new PdfNumber(1);
        number.makeIndirect(pdfDocument);
        for (int i = 0; i <= PdfObjectStream.MAX_OBJ_STREAM_SIZE - 1; i++) {
            pdfObjectStream.addObject(number);
        }
        Assert.assertTrue("We don't expect to reach this line, since no exception should have been thrown", true);
    }
}
