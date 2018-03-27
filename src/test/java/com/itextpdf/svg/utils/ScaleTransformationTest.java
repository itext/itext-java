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
public class ScaleTransformationTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void normalScaleTest() {
        AffineTransform expected = AffineTransform.getScaleInstance(10d, 20d);
        AffineTransform actual = TransformUtils.parseTransform("scale(10, 20)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void noScaleValuesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);

        TransformUtils.parseTransform("scale()");
    }

    @Test
    public void oneScaleValuesTest() {
        AffineTransform expected = AffineTransform.getScaleInstance(10d, 10d);
        AffineTransform actual = TransformUtils.parseTransform("scale(10)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void twoScaleValuesTest() {
        AffineTransform expected = AffineTransform.getScaleInstance(2, 3);
        AffineTransform actual = TransformUtils.parseTransform("scale(2,3)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void negativeScaleValuesTest() {
        AffineTransform expected = AffineTransform.getScaleInstance(-2, -3);
        AffineTransform actual = TransformUtils.parseTransform("scale(-2, -3)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void tooManyScaleValuesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);

        TransformUtils.parseTransform("scale(1 2 3)");
    }

}