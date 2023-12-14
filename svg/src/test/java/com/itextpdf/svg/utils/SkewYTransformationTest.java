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
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SkewYTransformationTest extends ExtendedITextTest {

    @Test
    public void normalSkewYTest() {
        AffineTransform expected = new AffineTransform(1d, Math.tan(Math.toRadians(143)), 0d, 1d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("skewY(143)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void noSkewYValuesTest() {
        Exception e = Assert.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("skewY()")
        );
        Assert.assertEquals(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES, e.getMessage());
    }

    @Test
    public void twoSkewYValuesTest() {
        Exception e = Assert.assertThrows(SvgProcessingException.class,
                () -> TransformUtils.parseTransform("skewY(1 2)")
        );
        Assert.assertEquals(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES, e.getMessage());
    }

    @Test
    public void negativeSkewYTest() {
        AffineTransform expected = new AffineTransform(1d, Math.tan(Math.toRadians(-26)), 0d, 1d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("skewY(-26)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void ninetyDegreesTest() {
        AffineTransform expected = new AffineTransform(1d, Math.tan(Math.toRadians(90)), 0d, 1d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("skewY(90)");

        Assert.assertEquals(expected, actual);
    }
}
