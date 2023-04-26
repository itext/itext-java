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
package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TransformUtilsTest extends ExtendedITextTest {

    @Test
    public void nullStringTest() {
        Exception e = Assert.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform(null)
        );
        Assert.assertEquals(SvgExceptionMessageConstant.TRANSFORM_NULL, e.getMessage());
    }

    @Test
    public void emptyTest() {
        Exception e = Assert.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("")
        );
        Assert.assertEquals(SvgExceptionMessageConstant.TRANSFORM_EMPTY, e.getMessage());
    }

    @Test
    public void noTransformationTest() {
        Exception e = Assert.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("Lorem ipsum")
        );
        Assert.assertEquals(SvgExceptionMessageConstant.INVALID_TRANSFORM_DECLARATION, e.getMessage());
    }

    @Test
    public void wrongTypeOfValuesTest() {
        Assert.assertThrows(NumberFormatException.class, () -> TransformUtils.parseTransform("matrix(a b c d e f)"));
    }

    @Test
    public void tooManyParenthesesTest() {
        Exception e = Assert.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("(((())))")
        );
        Assert.assertEquals(SvgExceptionMessageConstant.INVALID_TRANSFORM_DECLARATION, e.getMessage());
    }

    @Test
    public void noClosingParenthesisTest() {
        AffineTransform expected = new AffineTransform(0d, 0d, 0d, 0d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("matrix(0 0 0 0 0 0");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void mixedCaseTest() {
        AffineTransform expected = new AffineTransform(0d, 0d, 0d, 0d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("maTRix(0 0 0 0 0 0)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void upperCaseTest() {
        AffineTransform expected = new AffineTransform(0d, 0d, 0d, 0d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("MATRIX(0 0 0 0 0 0)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void whitespaceTest() {
        AffineTransform expected = new AffineTransform(0d, 0d, 0d, 0d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("matrix(0 0 0 0 0 0)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void commasWithWhitespaceTest() {
        AffineTransform expected = new AffineTransform(10d,20d,30d,40d,37.5d, 45d);
        AffineTransform actual = TransformUtils.parseTransform("matrix(10, 20, 30, 40, 50, 60)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void commasTest() {
        AffineTransform expected = new AffineTransform(10d,20d,30d,40d,37.5d, 45d);
        AffineTransform actual = TransformUtils.parseTransform("matrix(10,20,30,40,50,60)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void combinedTransformTest() {
        AffineTransform actual = TransformUtils.parseTransform("translate(40,20) scale(3)");
        AffineTransform expected = new AffineTransform(3.0,0d,0d,3.0d,30d,15d);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void combinedReverseTransformTest() {
        AffineTransform actual = TransformUtils.parseTransform("scale(3) translate(40,20)");
        AffineTransform expected = new AffineTransform(3d,0d,0d,3d,90d,45d);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void doubleTransformationTest() {
        AffineTransform expected = new AffineTransform(9d, 0d, 0d, 9d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("scale(3) scale(3)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void oppositeTransformationSequenceTest() {
        AffineTransform expected = new AffineTransform(1,0,0,1,0,0);
        AffineTransform actual = TransformUtils.parseTransform("translate(10 10) translate(-10 -10)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void unknownTransformationTest() {
        Exception e = Assert.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("unknown(1 2 3)")
        );
        Assert.assertEquals(SvgExceptionMessageConstant.UNKNOWN_TRANSFORMATION_TYPE, e.getMessage());
    }

    @Test
    public void trailingWhiteSpace() {
        AffineTransform actual = TransformUtils.parseTransform("translate(1) translate(2) ");
        AffineTransform expected = AffineTransform.getTranslateInstance(2.25, 0);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void leadingWhiteSpace() {
        AffineTransform actual = TransformUtils.parseTransform("   translate(1) translate(2)");
        AffineTransform expected = AffineTransform.getTranslateInstance(2.25, 0);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void middleWhiteSpace() {
        AffineTransform actual = TransformUtils.parseTransform("translate(1)     translate(2)");
        AffineTransform expected = AffineTransform.getTranslateInstance(2.25, 0);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void mixedWhiteSpace() {
        AffineTransform actual = TransformUtils.parseTransform("   translate(1)     translate(2)   ");
        AffineTransform expected = AffineTransform.getTranslateInstance(2.25, 0);

        Assert.assertEquals(expected, actual);
    }
}
