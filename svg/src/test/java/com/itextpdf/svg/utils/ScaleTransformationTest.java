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
public class ScaleTransformationTest extends ExtendedITextTest {

    @Test
    public void normalScaleTest() {
        AffineTransform expected = AffineTransform.getScaleInstance(10d, 20d);
        AffineTransform actual = TransformUtils.parseTransform("scale(10, 20)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void noScaleValuesTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("scale()")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES, e.getMessage());
    }

    @Test
    public void oneScaleValuesTest() {
        AffineTransform expected = AffineTransform.getScaleInstance(10d, 10d);
        AffineTransform actual = TransformUtils.parseTransform("scale(10)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void twoScaleValuesTest() {
        AffineTransform expected = AffineTransform.getScaleInstance(2, 3);
        AffineTransform actual = TransformUtils.parseTransform("scale(2,3)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void negativeScaleValuesTest() {
        AffineTransform expected = AffineTransform.getScaleInstance(-2, -3);
        AffineTransform actual = TransformUtils.parseTransform("scale(-2, -3)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void tooManyScaleValuesTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("scale(1 2 3)")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES, e.getMessage());
    }

}
