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
package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs.CalGray;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs.CalRgb;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs.Lab;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfCieBasedCsUnitTest extends ExtendedITextTest {

    @Test
    public void whitePointOfCalGrayIsIncorrectEmptyTest() {
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> new CalGray(new float[] {}));
        Assert.assertEquals(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED,
                exception.getMessage());
    }

    @Test
    public void whitePointOfCalRgbIsIncorrectEmptyTest() {
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> new CalRgb(new float[] {}));
        Assert.assertEquals(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED,
                exception.getMessage());
    }

    @Test
    public void whitePointOfLabIsIncorrectEmptyTest() {
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> new Lab(new float[] {}));
        Assert.assertEquals(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED,
                exception.getMessage());
    }

    @Test
    public void whitePointOfCalGrayIsIncorrectTooLittlePointsTest() {
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> new CalGray(new float[] {1, 2}));
        Assert.assertEquals(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED,
                exception.getMessage());
    }

    @Test
    public void whitePointOfCalRgbIsIncorrectTooLittlePointsTest() {
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> new CalRgb(new float[] {1, 2}));
        Assert.assertEquals(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED,
                exception.getMessage());
    }

    @Test
    public void whitePointOfLabIsIncorrectTooLittlePointsTest() {
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> new Lab(new float[] {1, 2}));
        Assert.assertEquals(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED,
                exception.getMessage());
    }

    @Test
    public void whitePointOfCalGrayIsIncorrectTooMuchPointsTest() {
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> new CalGray(new float[] {1, 2, 3, 4}));
        Assert.assertEquals(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED,
                exception.getMessage());
    }

    @Test
    public void whitePointOfCalRgbIsIncorrectTooMuchPointsTest() {
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> new CalRgb(new float[] {1, 2, 3, 4}));
        Assert.assertEquals(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED,
                exception.getMessage());
    }

    @Test
    public void whitePointOfLabIsIncorrectTooMuchPointsTest() {
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> new Lab(new float[] {1, 2, 3, 4}));
        Assert.assertEquals(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED,
                exception.getMessage());
    }
}
