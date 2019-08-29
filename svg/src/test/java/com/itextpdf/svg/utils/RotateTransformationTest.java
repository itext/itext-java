/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class RotateTransformationTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void normalRotateTest() {
        AffineTransform expected = AffineTransform.getRotateInstance(Math.toRadians(10), CssUtils.parseAbsoluteLength("5"), CssUtils.parseAbsoluteLength("10"));
        AffineTransform actual = TransformUtils.parseTransform("rotate(10, 5, 10)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void noRotateValuesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);

        TransformUtils.parseTransform("rotate()");
    }

    @Test
    public void oneRotateValuesTest() {
        AffineTransform expected = AffineTransform.getRotateInstance(Math.toRadians(10));
        AffineTransform actual = TransformUtils.parseTransform("rotate(10)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void twoRotateValuesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);

        TransformUtils.parseTransform("rotate(23,58)");
    }

    @Test
    public void threeRotateValuesTest() {
        AffineTransform expected = AffineTransform.getRotateInstance(Math.toRadians(23), CssUtils.parseAbsoluteLength("58"), CssUtils.parseAbsoluteLength("57"));
        AffineTransform actual = TransformUtils.parseTransform("rotate(23, 58, 57)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void tooManyRotateValuesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);

        TransformUtils.parseTransform("rotate(1 2 3 4)");
    }

    @Test
    public void negativeRotateValuesTest() {
        AffineTransform expected = AffineTransform.getRotateInstance(Math.toRadians(-23), CssUtils.parseAbsoluteLength("-58"), CssUtils.parseAbsoluteLength("-1"));
        AffineTransform actual = TransformUtils.parseTransform("rotate(-23,-58,-1)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void ninetyDegreesTest() {
        AffineTransform expected = AffineTransform.getRotateInstance(Math.toRadians(90));
        AffineTransform actual = TransformUtils.parseTransform("rotate(90)");

        Assert.assertEquals(expected, actual);
    }
}
