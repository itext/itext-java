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
public class TranslateTransformationTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void normalTranslateTest() {
        AffineTransform expected = new AffineTransform(1d, 0d, 0d, 1d, 20d, 50d);
        AffineTransform actual = TransformUtils.parseTransform("translate(20, 50)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void noTranslateValuesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);

        TransformUtils.parseTransform("translate()");
    }

    @Test
    public void oneTranslateValuesTest() {
        AffineTransform expected = new AffineTransform(1d, 0d, 0d, 1d, 10d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("translate(10)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void twoTranslateValuesTest() {
        AffineTransform expected = new AffineTransform(1d, 0d, 0d, 1d, 23d, 58d);
        AffineTransform actual = TransformUtils.parseTransform("translate(23,58)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void negativeTranslateValuesTest() {
        AffineTransform expected = new AffineTransform(1d, 0d, 0d, 1d, -23d, -58d);
        AffineTransform actual = TransformUtils.parseTransform("translate(-23,-58)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void tooManyTranslateValuesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);

        TransformUtils.parseTransform("translate(1 2 3)");
    }

}