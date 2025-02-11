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
package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class MatrixTransformationTest extends ExtendedITextTest {

    @Test
    public void normalMatrixTest() {
        AffineTransform expected = new AffineTransform(10d, 20d, 30d, 40d, 37.5d, 45d);
        AffineTransform actual = TransformUtils.parseTransform("matrix(10 20 30 40 50 60)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void noMatrixValuesTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("matrix()")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES, e.getMessage());
    }

    @Test
    public void notEnoughMatrixValuesTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("matrix(0)")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES, e.getMessage());
    }

    @Test
    public void tooManyMatrixValuesTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("matrix(1 2 3 4 5 6 7 8)")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES, e.getMessage());
    }
}
