package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.kernel.geom.Point;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EllipticalPathOperatorTest {
    // tests for coordinates
    @Test
    public void testBasicParameterSet() {
        EllipticalCurveTo absoluteElliptic = new EllipticalCurveTo();
        // String array length = 7
        absoluteElliptic.setCoordinates(new String[]{"40", "40", "0", "0", "0", "20", "20"}, new Point());
        String[][] result = absoluteElliptic.getCoordinates();
        Assert.assertEquals(1, result.length);
        Assert.assertEquals(7, result[0].length);
    }

    @Test
    public void testTooManyParameterSet() {
        EllipticalCurveTo absoluteElliptic = new EllipticalCurveTo();
        // String array length = 8
        absoluteElliptic.setCoordinates(new String[]{"40", "40", "0", "0", "0", "20", "20", "1"}, new Point());
        String[][] result = absoluteElliptic.getCoordinates();
        Assert.assertEquals(1, result.length);
        Assert.assertEquals(7, result[0].length);
    }

    @Test
    public void testIncorrectMultipleParameterSets() {
        EllipticalCurveTo absoluteElliptic = new EllipticalCurveTo();
        // String array length = 13
        absoluteElliptic.setCoordinates(new String[]{"40", "40", "0", "0", "0", "20", "20", "40", "40", "0", "0", "0", "20"}, new Point());
        String[][] result = absoluteElliptic.getCoordinates();
        Assert.assertEquals(1, result.length);
        Assert.assertEquals(7, result[0].length);
    }

    @Test
    public void testMultipleParameterSet() {
        EllipticalCurveTo absoluteElliptic = new EllipticalCurveTo();
        // String array length = 14
        absoluteElliptic.setCoordinates(new String[]{"40", "40", "0", "0", "0", "20", "20", "40", "40", "0", "0", "0", "20", "20"}, new Point());
        String[][] result = absoluteElliptic.getCoordinates();
        Assert.assertEquals(2, result.length);
        Assert.assertEquals(7, result[0].length);
        Assert.assertEquals(7, result[1].length);
    }

    @Test
    public void testRandomParameterAmountSet() {
        EllipticalCurveTo absoluteElliptic = new EllipticalCurveTo();
        // String array length = 17
        absoluteElliptic.setCoordinates(new String[]{"40", "40", "0", "0", "0", "20", "20", "40", "40", "0", "0", "0", "20", "20", "0", "1", "2"}, new Point());
        String[][] result = absoluteElliptic.getCoordinates();
        Assert.assertEquals(2, result.length);
        Assert.assertEquals(7, result[0].length);
        Assert.assertEquals(7, result[1].length);
    }

    @Test
    public void testNotEnoughParameterSet() {
        EllipticalCurveTo absoluteElliptic = new EllipticalCurveTo();
        // String array length = 6
        absoluteElliptic.setCoordinates(new String[]{"40", "0", "0", "0", "20", "20"}, new Point());
        String[][] result = absoluteElliptic.getCoordinates();
        Assert.assertEquals(0, result.length);
    }

    @Test
    public void testNoParameterSet() {
        EllipticalCurveTo absoluteElliptic = new EllipticalCurveTo();
        // String array length = 0
        absoluteElliptic.setCoordinates(new String[]{}, new Point());
        String[][] result = absoluteElliptic.getCoordinates();
        Assert.assertEquals(0, result.length);
    }

    // rotate tests
    private void assertPointArrayArrayEquals(Point[][] expected, Point[][] actual) {
        Assert.assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertPointArrayEquals(expected[i], actual[i]);
        }
    }

    private void assertPointArrayEquals(Point[] expected, Point[] actual) {
        Assert.assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i].x, actual[i].x, 0.00001);
            Assert.assertEquals(expected[i].y, actual[i].y, 0.00001);
        }
    }

    @Test
    public void zeroRotationOriginTest() {
        Point[][] input = new Point[][]{{new Point(50, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, 0.0, new Point(0, 0));
        assertPointArrayArrayEquals(actual, input);
    }

    @Test
    public void fullCircleRotationOriginTest() {
        Point[][] input = new Point[][]{{new Point(50, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, 2 * Math.PI, new Point(0, 0));
        assertPointArrayArrayEquals(actual, input);
    }

    @Test
    public void halfCircleRotationOriginTest() {
        Point[][] input = new Point[][]{{new Point(50, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, Math.PI, new Point(0, 0));

        Point[][] expected = new Point[][]{{new Point(-50, -30)}};

        assertPointArrayArrayEquals(expected, actual);
    }

    @Test
    public void thirtyDegreesRotationOriginTest() {
        Point[][] input = new Point[][]{{new Point(0, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, -Math.PI / 6, new Point(0, 0));

        Point[][] expected = new Point[][]{{new Point(15, Math.cos(Math.PI / 6) * 30)}};

        assertPointArrayArrayEquals(expected, actual);
    }

    @Test
    public void fortyFiveDegreesRotationOriginTest() {
        Point[][] input = new Point[][]{{new Point(0, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, -Math.PI / 4, new Point(0, 0));

        Point[][] expected = new Point[][]{{new Point(Math.sin(Math.PI / 4) * 30, Math.sin(Math.PI / 4) * 30)}};

        assertPointArrayArrayEquals(expected, actual);
    }

    @Test
    public void sixtyDegreesRotationOriginTest() {
        Point[][] input = new Point[][]{{new Point(0, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, -Math.PI / 3, new Point(0, 0));

        Point[][] expected = new Point[][]{{new Point(Math.sin(Math.PI / 3) * 30, 15)}};

        assertPointArrayArrayEquals(expected, actual);
    }

    @Test
    public void ninetyDegreesRotationOriginTest() {
        Point[][] input = new Point[][]{{new Point(0, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, -Math.PI / 2, new Point(0, 0));

        Point[][] expected = new Point[][]{{new Point(30, 0)}};

        assertPointArrayArrayEquals(expected, actual);
    }


    @Test
    public void zeroRotationRandomPointTest() {
        Point[][] input = new Point[][]{{new Point(50, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, 0.0, new Point(40, 90));
        assertPointArrayArrayEquals(actual, input);
    }

    @Test
    public void fullCircleRotationRandomPointTest() {
        Point[][] input = new Point[][]{{new Point(50, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, 2 * Math.PI, new Point(-200, 50));
        assertPointArrayArrayEquals(actual, input);
    }

    @Test
    public void halfCircleRotationRandomPointTest() {
        Point[][] input = new Point[][]{{new Point(50, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, Math.PI, new Point(-20, -20));
        Point[][] expected = new Point[][]{{new Point(-90, -70)}};

        assertPointArrayArrayEquals(expected, actual);
    }

    @Test
    public void thirtyDegreesRotationRandomPointTest() {
        Point[][] input = new Point[][]{{new Point(0, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, -Math.PI / 6, new Point(100, 100));

        Point[][] expected = new Point[][]{{new Point(-21.60253882, 89.37822282)}};

        assertPointArrayArrayEquals(expected, actual);
    }

    @Test
    public void fortyFiveDegreesRotationRandomPointTest() {
        Point[][] input = new Point[][]{{new Point(0, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, -Math.PI / 4, new Point(20, 0));

        Point[][] expected = new Point[][]{{new Point(27.07106769, 35.35533845)}};

        assertPointArrayArrayEquals(expected, actual);
    }

    @Test
    public void sixtyDegreesRotationRandomPointTest() {
        Point[][] input = new Point[][]{{new Point(0, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, -Math.PI / 3, new Point(0, -50));

        Point[][] expected = new Point[][]{{new Point(69.28203105, -10)}};

        assertPointArrayArrayEquals(expected, actual);
    }

    @Test
    public void ninetyDegreesRotationRandomPointTest() {
        Point[][] input = new Point[][]{{new Point(0, 30)}};
        Point[][] actual = EllipticalCurveTo.rotate(input, -Math.PI / 2, new Point(-0, 20));

        Point[][] expected = new Point[][]{{new Point(10, 20)}};

        assertPointArrayArrayEquals(expected, actual);
    }
}
