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
public class SkewXTransformationTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void normalSkewXTest() {
        AffineTransform expected = new AffineTransform(1d, Math.tan(Math.toRadians(CssUtils.parseAbsoluteLength("143"))), 0d, 1d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("skewX(143)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void noSkewXValuesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);

        TransformUtils.parseTransform("skewX()");
    }

    @Test
    public void twoSkewXValuesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);

        TransformUtils.parseTransform("skewX(1 2)");
    }

    @Test
    public void negativeSkewXTest() {
        AffineTransform expected = new AffineTransform(1d, Math.tan(Math.toRadians(CssUtils.parseAbsoluteLength("-26"))), 0d, 1d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("skewX(-26)");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void ninetyDegreesTest() {
        AffineTransform expected = new AffineTransform(1d, Math.tan(Math.toRadians(CssUtils.parseAbsoluteLength("90"))), 0d, 1d, 0d, 0d);
        AffineTransform actual = TransformUtils.parseTransform("skewX(90)");

        Assert.assertEquals(expected, actual);
    }
}