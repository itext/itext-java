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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfArrayUnitTest extends ExtendedITextTest {

    @Test
    public void cannotConvertArrayOfPdfStringsToArrayOfBooleansTest() {
        PdfArray pdfArray = new PdfArray(new PdfString(""));
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfArray.toBooleanArray());
        Assertions.assertEquals(KernelExceptionMessageConstant.CANNOT_CONVERT_PDF_ARRAY_TO_AN_ARRAY_OF_BOOLEANS,
                exception.getMessage());
    }

    @Test
    public void cannotConvertArrayOfPdfStringsToDoubleArrayTest() {
        PdfArray pdfArray = new PdfArray(new PdfString(""));
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfArray.toDoubleArray());
        Assertions.assertEquals(KernelExceptionMessageConstant.CANNOT_CONVERT_PDF_ARRAY_TO_DOUBLE_ARRAY,
                exception.getMessage());
    }

    @Test
    public void cannotConvertArrayOfPdfStringsToIntArrayTest() {
        PdfArray pdfArray = new PdfArray(new PdfString(""));
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfArray.toIntArray());
        Assertions.assertEquals(KernelExceptionMessageConstant.CANNOT_CONVERT_PDF_ARRAY_TO_INT_ARRAY,
                exception.getMessage());
    }

    @Test
    public void cannotConvertArrayOfPdfStringsToFloatArrayTest() {
        PdfArray pdfArray = new PdfArray(new PdfString(""));
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfArray.toFloatArray());
        Assertions.assertEquals(KernelExceptionMessageConstant.CANNOT_CONVERT_PDF_ARRAY_TO_FLOAT_ARRAY,
                exception.getMessage());
    }

    @Test
    public void cannotConvertArrayOfPdfStringsToLongArrayTest() {
        PdfArray pdfArray = new PdfArray(new PdfString(""));
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfArray.toLongArray());
        Assertions.assertEquals(KernelExceptionMessageConstant.CANNOT_CONVERT_PDF_ARRAY_TO_LONG_ARRAY,
                exception.getMessage());
    }

    @Test
    public void cannotConvertArrayOfPdfStringsToRectangleTest() {
        PdfArray pdfArray = new PdfArray(new PdfString(""));
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfArray.toRectangle());
        Assertions.assertEquals(KernelExceptionMessageConstant.CANNOT_CONVERT_PDF_ARRAY_TO_RECTANGLE,
                exception.getMessage());
    }
}
