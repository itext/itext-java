package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SvgCoordinateUtilsTest extends ExtendedITextTest {

    private final static double delta = 0.0000001;

    @Test
    public void calculateAngleBetweenTwoVectors45degTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(1, 1, 0);
        double expected = Math.PI / 4;
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, delta);
    }

    @Test
    public void calculateAngleBetweenTwoVectors45degInverseTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(1, -1, 0);
        double expected = Math.PI / 4;
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, delta);
    }

    @Test
    public void calculateAngleBetweenTwoVectors135degTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(-1, 1, 0);
        double expected = (Math.PI - Math.PI / 4);
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, delta);
    }

    @Test
    public void calculateAngleBetweenTwoVectors135degInverseTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(-1, -1, 0);
        double expected = (Math.PI - Math.PI / 4);
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, delta);
    }


    @Test
    public void calculateAngleBetweenTwoVectors90degTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(0, 1, 0);
        double expected =  Math.PI / 2;
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, delta);
    }

    @Test
    public void calculateAngleBetweenTwoVectors180degTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(-1, 0, 0);
        double expected =  Math.PI;
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, delta);
    }
}
