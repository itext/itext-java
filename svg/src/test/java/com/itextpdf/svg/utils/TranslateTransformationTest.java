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
public class TranslateTransformationTest extends ExtendedITextTest {

    @Test
    public void normalTranslateTest() {
        AffineTransform expected = new AffineTransform(1d, 0d, 0d, 1d, 15d, 37.5d);
        AffineTransform actual = TransformUtils.parseTransform("translate(20, 50)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void noTranslateValuesTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("translate()")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES, e.getMessage());
    }

    @Test
    public void oneTranslateValuesTest() {
        AffineTransform expected = new AffineTransform(1d, 0d, 0d, 1d, 7.5d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("translate(10)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void twoTranslateValuesTest() {
        AffineTransform expected = new AffineTransform(1d, 0d, 0d, 1d, 17.25d, 43.5d);
        AffineTransform actual = TransformUtils.parseTransform("translate(23,58)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void negativeTranslateValuesTest() {
        AffineTransform expected = new AffineTransform(1d, 0d, 0d, 1d, -17.25d, -43.5d);
        AffineTransform actual = TransformUtils.parseTransform("translate(-23,-58)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void tooManyTranslateValuesTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("translate(1 2 3)")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES, e.getMessage());
    }
}
