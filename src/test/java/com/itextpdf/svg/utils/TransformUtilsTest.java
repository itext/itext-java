package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.AffineTransform;
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
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.FLOAT_PARSING_NAN);

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
        AffineTransform expected = new AffineTransform(10d,20d,30d,40d,50d, 60d);
        AffineTransform actual = TransformUtils.parseTransform("matrix(10, 20, 30, 40, 50, 60)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void commasTest() {
        AffineTransform expected = new AffineTransform(10d,20d,30d,40d,50d, 60d);
        AffineTransform actual = TransformUtils.parseTransform("matrix(10,20,30,40,50,60)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void combinedTransformTest() {
        AffineTransform actual = TransformUtils.parseTransform("translate(40,20) scale(3)");
        AffineTransform expected = new AffineTransform(3,0,0,3,40,20);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void combinedReverseTransformTest() {
        AffineTransform actual = TransformUtils.parseTransform("scale(3) translate(40,20)");
        AffineTransform expected = new AffineTransform(3,0,0,3,120,60);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void doubleTransformationTest() {
        double expectedScaleValue = Math.pow(43d, 2);
        AffineTransform expected = new AffineTransform(expectedScaleValue, 0d, 0d, expectedScaleValue, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("scale(43) scale(43)");

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
}
