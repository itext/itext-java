package com.itextpdf.kernel.geom;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

@Category(UnitTest.class)
public class RectangleTest extends ExtendedITextTest {

    @Test
    public void rectangleOverlapTest01() {
        //Intersection
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(5, 5, 5, 5);
        Boolean result = one.overlaps(two);
        Assert.assertTrue(result);

        //envelopment
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(2, 2, 5, 5);
        result = one.overlaps(two);
        Assert.assertTrue(result);

        //identical
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(0, 0, 10, 10);
        result = one.overlaps(two);
        Assert.assertTrue(result);

    }

    @Test
    public void rectangleOverlapTest02() {
        //Left
        //Top left
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(15, 15, 10, 10);
        Boolean result = one.overlaps(two);
        Assert.assertFalse(result);
        //Middle left
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(15, 5, 10, 10);
        result = one.overlaps(two);
        Assert.assertFalse(result);
        //Lower left
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(15, -5, 10, 10);
        result = one.overlaps(two);
        Assert.assertFalse(result);

        //Bottom
        //Bottom left
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(5, -15, 10, 10);
        result = one.overlaps(two);
        Assert.assertFalse(result);
        //Bottom right
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(-5, -15, 10, 10);
        result = one.overlaps(two);
        Assert.assertFalse(result);

        //Right
        //Lower right
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(-15, -5, 10, 10);
        result = one.overlaps(two);
        Assert.assertFalse(result);
        //Upper right
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(-15, 5, 10, 10);
        result = one.overlaps(two);
        Assert.assertFalse(result);

        //Top
        //Top right
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(-5, 15, 10, 10);
        result = one.overlaps(two);
        Assert.assertFalse(result);
        //Top left
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(5, 15, 10, 10);
        result = one.overlaps(two);
        Assert.assertFalse(result);

    }


    @Test
    public void envelopTest01() {
        //one contains two
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(5, 5, 5, 5);
        Boolean result = one.contains(two);
        Assert.assertTrue(result);
    }

    @Test
    public void envelopsTest02() {
        //two identical rectangles
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(0, 0, 10, 10);
        Boolean result = one.contains(two);
        Assert.assertTrue(result);

    }

    @Test
    public void envelopsTest03() {
        //One intersects two but does not envelop
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(5, 5, 10, 10);
        Boolean result = one.contains(two);
        Assert.assertFalse(result);
    }

    @Test
    public void envelopsTest04() {
        //one and two do not
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(-15, -15, 10, 10);
        Boolean result = one.contains(two);
        Assert.assertFalse(result);
    }

    @Test
    public void getIntersectionTest01() {
        //Cases where there is an intersection rectangle
        Rectangle main, second, actual, expected;
        Boolean areEqual = true;
        main = new Rectangle(2, 2, 8, 8);
        //A. Main rectangle is greater in both dimension than second rectangle
        second = new Rectangle(4, 8, 4, 4);
        //1.Middle top
        expected = new Rectangle(4, 8, 4, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //2.Middle Right
        second.moveRight(4);
        expected = new Rectangle(8, 8, 2, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //3.Right middle
        second.moveDown(4);
        expected = new Rectangle(8, 4, 2, 4);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //4.Bottom right
        second.moveDown(4);
        expected = new Rectangle(8, 2, 2, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //5.Bottom middle
        second.moveLeft(4);
        expected = new Rectangle(4, 2, 4, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //6.Bottom Left
        second.moveLeft(4);
        expected = new Rectangle(2, 2, 2, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //7.Left Middle
        second.moveUp(4);
        expected = new Rectangle(2, 4, 2, 4);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //8.Left Top
        second.moveUp(4);
        expected = new Rectangle(2, 8, 2, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //B. Main rectangle is greater in width but not height than second rectangle
        //1. Left
        second = new Rectangle(0, 0, 4, 12);
        expected = new Rectangle(2, 2, 2, 8);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //2. Middle
        second.moveRight(4);
        expected = new Rectangle(4, 2, 4, 8);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //3. Right
        second.moveRight(4);
        expected = new Rectangle(8, 2, 2, 8);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //C. Main rectangle is greater in height but not width than second rectangle
        //1. Top
        second = new Rectangle(0, 8, 12, 4);
        expected = new Rectangle(2, 8, 8, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //2. Middle
        second.moveDown(4);
        expected = new Rectangle(2, 4, 8, 4);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //3. Bottom
        second.moveDown(4);
        expected = new Rectangle(2, 2, 8, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));

        //Check if any have failed
        Assert.assertTrue(areEqual);
    }

    @Test
    public void getIntersectionTest02() {
        //Cases where the two rectangles do not intersect
        Rectangle main, second, actual, expected;
        Boolean noIntersection = true;
        main = new Rectangle(2, 2, 8, 8);
        //Top
        second = new Rectangle(4, 12, 4, 4);
        actual = main.getIntersection(second);
        noIntersection = noIntersection && ((actual) == null);
        //Right
        second = new Rectangle(12, 4, 4, 4);
        actual = main.getIntersection(second);
        noIntersection = noIntersection && ((actual) == null);
        //Bottom
        second = new Rectangle(4, -8, 4, 4);
        actual = main.getIntersection(second);
        noIntersection = noIntersection && ((actual) == null);
        //Left
        second = new Rectangle(-8, 4, 4, 4);
        actual = main.getIntersection(second);
        noIntersection = noIntersection && ((actual) == null);

        Assert.assertTrue(noIntersection);
    }

    @Test
    public void getIntersectionTest03() {
        //Edge cases: envelopment
        //A equal rectangles
        Rectangle main, second, actual, expected;
        Boolean areEqual = true;
        main = new Rectangle(2, 2, 8, 8);
        second = new Rectangle(main);
        expected = new Rectangle(main);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //B main contains second
        main = new Rectangle(2, 2, 8, 8);
        second = new Rectangle(4, 4, 4, 4);
        expected = new Rectangle(second);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //C second contains main
        main = new Rectangle(2, 2, 8, 8);
        second = new Rectangle(0, 0, 12, 12);
        expected = new Rectangle(main);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));

        Assert.assertTrue(areEqual);
    }

    @Test
    public void getIntersectionTest04() {
        //Edge case: intersections on edges
        Rectangle main, second, actual, expected;
        Boolean areEqual = true;
        main = new Rectangle(2, 2, 8, 8);
        //Top
        second = new Rectangle(4, 10, 4, 4);
        expected = new Rectangle(4, 10, 4, 0);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //Right
        second = new Rectangle(10, 4, 4, 4);
        expected = new Rectangle(10, 4, 0, 4);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //Bottom
        second = new Rectangle(4, -2, 4, 4);
        expected = new Rectangle(4, 2, 4, 0);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //Left
        second = new Rectangle(-2, 4, 4, 4);
        expected = new Rectangle(2, 4, 0, 4);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //Edge case: intersection on corners
        //Top-Left
        second = new Rectangle(-2, 10, 4, 4);
        expected = new Rectangle(2, 10, 0, 0);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //Top-Right
        second = new Rectangle(10, 10, 4, 4);
        expected = new Rectangle(10, 10, 0, 0);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //Bottom-Right
        second = new Rectangle(10, -2, 4, 4);
        expected = new Rectangle(10, 2, 0, 0);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        //Bottom-Left
        second = new Rectangle(-2, -2, 4, 4);
        expected = new Rectangle(2, 2, 0, 0);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equals(actual));
        Assert.assertTrue(areEqual);
    }

    @Test
    public void createBoundingRectangleFromQuadPointsTest01() {
        Rectangle actual, expected;
        float[] points = {0, 0, 2, 1, 1, 2, -2, 1};
        PdfArray quadpoints = new PdfArray(points);

        expected = new Rectangle(-2, 0, 4, 2);
        actual = Rectangle.createBoundingRectangleFromQuadPoint(quadpoints);

        Assert.assertEquals(expected, actual);

    }

    @Test(expected = PdfException.class)
    public void createBoundingRectangleFromQuadPointsTest02() {
        Rectangle actual, expected;
        float[] points = {0, 0, 2, 1, 1, 2, -2, 1, 0};
        PdfArray quadpoints = new PdfArray(points);

        expected = new Rectangle(-2, 0, 4, 2);
        actual = Rectangle.createBoundingRectangleFromQuadPoint(quadpoints);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void createBoundingRectanglesFromQuadPointsTest01() {
        List<Rectangle> actual, expected;
        float[] points = {0, 0, 2, 1, 1, 2, -2, 1,
                0, -1, 2, 0, 1, 1, -2, 0};
        PdfArray quadpoints = new PdfArray(points);
        expected = new ArrayList<Rectangle>();
        expected.add(new Rectangle(-2, 0, 4, 2));
        expected.add(new Rectangle(-2, -1, 4, 2));
        actual = Rectangle.createBoundingRectanglesFromQuadPoint(quadpoints);
        Assert.assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test(expected = PdfException.class)
    public void createBoundingRectanglesFromQuadPointsTest02() {
        List<Rectangle> actual, expected;
        float[] points = {0, 0, 2, 1, 1, 2, -2, 1,
                0, -1, 2, 0, 1, 1, -2, 0,
                1};
        PdfArray quadpoints = new PdfArray(points);
        expected = new ArrayList<Rectangle>();
        expected.add(new Rectangle(-2, 0, 4, 2));
        expected.add(new Rectangle(-2, -1, 4, 2));
        actual = Rectangle.createBoundingRectanglesFromQuadPoint(quadpoints);
        Assert.assertArrayEquals(expected.toArray(), actual.toArray());
    }

}
