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

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class TransformUtilsTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void nullStringTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_NULL);

        TransformUtils.parseTransform(null);
    }

    @Test
    public void emptyTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_EMPTY);

        TransformUtils.parseTransform("");
    }

    @Test
    public void noTransformationTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.INVALID_TRANSFORM_DECLARATION);

        TransformUtils.parseTransform("Lorem ipsum");
    }

    @Test
    public void wrongTypeOfValuesTest() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(LogMessageConstant.NAN, "a"));

        TransformUtils.parseTransform("matrix(a b c d e f)");
    }

    @Test
    public void tooManyParenthesesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.INVALID_TRANSFORM_DECLARATION);

        TransformUtils.parseTransform("(((())))");
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
        AffineTransform expected = new AffineTransform(7.5d,15d,22.5d,30d,37.5d, 45d);
        AffineTransform actual = TransformUtils.parseTransform("matrix(10, 20, 30, 40, 50, 60)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void commasTest() {
        AffineTransform expected = new AffineTransform(7.5d,15d,22.5d,30d,37.5d, 45d);
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
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.UNKNOWN_TRANSFORMATION_TYPE);

        TransformUtils.parseTransform("unknown(1 2 3)");
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
