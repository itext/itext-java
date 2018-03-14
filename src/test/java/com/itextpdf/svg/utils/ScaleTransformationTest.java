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
        AffineTransform expected = AffineTransform.getScaleInstance(7.5d, 15d);
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
        AffineTransform expected = AffineTransform.getScaleInstance(7.5d, 7.5d);
        AffineTransform actual = TransformUtils.parseTransform("scale(10)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void twoScaleValuesTest() {
        AffineTransform expected = AffineTransform.getScaleInstance(17.25d, 43.5d);
        AffineTransform actual = TransformUtils.parseTransform("scale(23,58)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void negativeScaleValuesTest() {
        AffineTransform expected = AffineTransform.getScaleInstance(-7.5d, -37.5d);
        AffineTransform actual = TransformUtils.parseTransform("scale(-10, -50)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void tooManyScaleValuesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);

        TransformUtils.parseTransform("scale(1 2 3)");
    }

}