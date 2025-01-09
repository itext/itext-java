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
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class RotateTransformationTest extends ExtendedITextTest {

    @Test
    public void normalRotateTest() {
        AffineTransform expected = AffineTransform.getRotateInstance(Math.toRadians(10), CssDimensionParsingUtils.parseAbsoluteLength("5"), CssDimensionParsingUtils
                .parseAbsoluteLength("10"));
        AffineTransform actual = TransformUtils.parseTransform("rotate(10, 5, 10)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void noRotateValuesTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("rotate()")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES, e.getMessage());
    }

    @Test
    public void oneRotateValuesTest() {
        AffineTransform expected = AffineTransform.getRotateInstance(Math.toRadians(10));
        AffineTransform actual = TransformUtils.parseTransform("rotate(10)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void twoRotateValuesTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("rotate(23,58)")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES, e.getMessage());
    }

    @Test
    public void threeRotateValuesTest() {
        AffineTransform expected = AffineTransform.getRotateInstance(Math.toRadians(23), CssDimensionParsingUtils.parseAbsoluteLength("58"), CssDimensionParsingUtils
                .parseAbsoluteLength("57"));
        AffineTransform actual = TransformUtils.parseTransform("rotate(23, 58, 57)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void tooManyRotateValuesTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("rotate(1 2 3 4)")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES, e.getMessage());
    }

    @Test
    public void negativeRotateValuesTest() {
        AffineTransform expected = AffineTransform.getRotateInstance(Math.toRadians(-23), CssDimensionParsingUtils.parseAbsoluteLength("-58"), CssDimensionParsingUtils
                .parseAbsoluteLength("-1"));
        AffineTransform actual = TransformUtils.parseTransform("rotate(-23,-58,-1)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void ninetyDegreesTest() {
        AffineTransform expected = AffineTransform.getRotateInstance(Math.toRadians(90));
        AffineTransform actual = TransformUtils.parseTransform("rotate(90)");

        Assertions.assertEquals(expected, actual);
    }
}
