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
public class TransformUtilsTest extends ExtendedITextTest {

    @Test
    public void nullStringTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform(null)
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.TRANSFORM_NULL, e.getMessage());
    }

    @Test
    public void emptyTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.TRANSFORM_EMPTY, e.getMessage());
    }

    @Test
    public void noTransformationTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("Lorem ipsum")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.INVALID_TRANSFORM_DECLARATION, e.getMessage());
    }

    @Test
    public void wrongTypeOfValuesTest() {
        Assertions.assertThrows(NumberFormatException.class, () -> TransformUtils.parseTransform("matrix(a b c d e f)"));
    }

    @Test
    public void tooManyParenthesesTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("(((())))")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.INVALID_TRANSFORM_DECLARATION, e.getMessage());
    }

    @Test
    public void noClosingParenthesisTest() {
        AffineTransform expected = new AffineTransform(0d, 0d, 0d, 0d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("matrix(0 0 0 0 0 0");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void mixedCaseTest() {
        AffineTransform expected = new AffineTransform(0d, 0d, 0d, 0d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("maTRix(0 0 0 0 0 0)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void upperCaseTest() {
        AffineTransform expected = new AffineTransform(0d, 0d, 0d, 0d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("MATRIX(0 0 0 0 0 0)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void whitespaceTest() {
        AffineTransform expected = new AffineTransform(0d, 0d, 0d, 0d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("matrix(0 0 0 0 0 0)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void commasWithWhitespaceTest() {
        AffineTransform expected = new AffineTransform(10d,20d,30d,40d,37.5d, 45d);
        AffineTransform actual = TransformUtils.parseTransform("matrix(10, 20, 30, 40, 50, 60)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void commasTest() {
        AffineTransform expected = new AffineTransform(10d,20d,30d,40d,37.5d, 45d);
        AffineTransform actual = TransformUtils.parseTransform("matrix(10,20,30,40,50,60)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void combinedTransformTest() {
        AffineTransform actual = TransformUtils.parseTransform("translate(40,20) scale(3)");
        AffineTransform expected = new AffineTransform(3.0,0d,0d,3.0d,30d,15d);

        Assertions.assertEquals(actual, expected);
    }

    @Test
    public void combinedReverseTransformTest() {
        AffineTransform actual = TransformUtils.parseTransform("scale(3) translate(40,20)");
        AffineTransform expected = new AffineTransform(3d,0d,0d,3d,90d,45d);

        Assertions.assertEquals(actual, expected);
    }

    @Test
    public void combinedReverseTransformWithCommaTest() {
        AffineTransform actual = TransformUtils.parseTransform("scale(3),translate(40,20)");
        AffineTransform expected = new AffineTransform(3d,0d,0d,3d,90d,45d);

        Assertions.assertEquals(actual, expected);
    }

    @Test
    public void doubleTransformationTest() {
        AffineTransform expected = new AffineTransform(9d, 0d, 0d, 9d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("scale(3) scale(3)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void oppositeTransformationSequenceTest() {
        AffineTransform expected = new AffineTransform(1,0,0,1,0,0);
        AffineTransform actual = TransformUtils.parseTransform("translate(10 10) translate(-10 -10)");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void unknownTransformationTest() {
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("unknown(1 2 3)")
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.UNKNOWN_TRANSFORMATION_TYPE, e.getMessage());
    }

    @Test
    public void trailingWhiteSpace() {
        AffineTransform actual = TransformUtils.parseTransform("translate(1) translate(2) ");
        AffineTransform expected = AffineTransform.getTranslateInstance(2.25, 0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void leadingWhiteSpace() {
        AffineTransform actual = TransformUtils.parseTransform("   translate(1) translate(2)");
        AffineTransform expected = AffineTransform.getTranslateInstance(2.25, 0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void middleWhiteSpace() {
        AffineTransform actual = TransformUtils.parseTransform("translate(1)     translate(2)");
        AffineTransform expected = AffineTransform.getTranslateInstance(2.25, 0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void mixedWhiteSpace() {
        AffineTransform actual = TransformUtils.parseTransform("   translate(1)     translate(2)   ");
        AffineTransform expected = AffineTransform.getTranslateInstance(2.25, 0);

        Assertions.assertEquals(expected, actual);
    }
}
