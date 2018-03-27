package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class SkewYTransformationTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void normalSkewYTest() {
        AffineTransform expected = new AffineTransform(1d, Math.tan(Math.toRadians(143)), 0d, 1d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("skewY(143)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void noSkewYValuesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);

        TransformUtils.parseTransform("skewY()");
    }

    @Test
    public void twoSkewYValuesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);

        TransformUtils.parseTransform("skewY(1 2)");
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