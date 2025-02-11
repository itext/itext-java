/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.geom;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class RectangleTest extends ExtendedITextTest {
    private static final float OVERLAP_EPSILON = 0.1f;

    @Test
    public void overlapWithEpsilon() {
        Rectangle first = new Rectangle(0, 0, 10, 10);
        Rectangle second = new Rectangle(-10, 0, 10.09f, 5);

        Assertions.assertFalse(first.overlaps(second, OVERLAP_EPSILON));
        second.setWidth(10.11f);
        Assertions.assertTrue(first.overlaps(second, OVERLAP_EPSILON));

        second = new Rectangle(5, 9.91f, 5, 5);
        Assertions.assertFalse(first.overlaps(second, OVERLAP_EPSILON));
        second.setY(9.89f);
        Assertions.assertTrue(first.overlaps(second, OVERLAP_EPSILON));

        second = new Rectangle(9.91f, 0, 5, 5);
        Assertions.assertFalse(first.overlaps(second, OVERLAP_EPSILON));
        second.setX(9.89f);
        Assertions.assertTrue(first.overlaps(second, OVERLAP_EPSILON));

        second = new Rectangle(5, -10, 5, 10.09f);
        Assertions.assertFalse(first.overlaps(second, OVERLAP_EPSILON));
        second.setHeight(10.11f);
        Assertions.assertTrue(first.overlaps(second, OVERLAP_EPSILON));
    }

    @Test
    public void overlapWithNegativeEpsilon() {
        Rectangle first = new Rectangle(0, 0, 10, 10);
        Rectangle second = new Rectangle(-10, 0, 9.89f, 5);

        Assertions.assertFalse(first.overlaps(second, -OVERLAP_EPSILON));
        second.setWidth(9.91f);
        Assertions.assertTrue(first.overlaps(second, -OVERLAP_EPSILON));

        second = new Rectangle(5, 10.11f, 5, 5);
        Assertions.assertFalse(first.overlaps(second, -OVERLAP_EPSILON));
        second.setY(10.09f);
        Assertions.assertTrue(first.overlaps(second, -OVERLAP_EPSILON));

        second = new Rectangle(10.11f, 0, 5, 5);
        Assertions.assertFalse(first.overlaps(second, -OVERLAP_EPSILON));
        second.setX(10.09f);
        Assertions.assertTrue(first.overlaps(second, -OVERLAP_EPSILON));

        second = new Rectangle(5, -10, 5, 9.89f);
        Assertions.assertFalse(first.overlaps(second, -OVERLAP_EPSILON));
        second.setHeight(9.91f);
        Assertions.assertTrue(first.overlaps(second, -OVERLAP_EPSILON));
    }

    @Test
    public void rectangleOverlapTest01() {
        //Intersection
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(5, 5, 5, 5);
        boolean result = one.overlaps(two);
        Assertions.assertTrue(result);

        //envelopment
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(2, 2, 5, 5);
        result = one.overlaps(two);
        Assertions.assertTrue(result);

        //identical
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(0, 0, 10, 10);
        result = one.overlaps(two);
        Assertions.assertTrue(result);

    }

    @Test
    public void rectangleOverlapTest02() {
        //Left
        //Top left
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(15, 15, 10, 10);
        boolean result = one.overlaps(two);
        Assertions.assertFalse(result);
        //Middle left
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(15, 5, 10, 10);
        result = one.overlaps(two);
        Assertions.assertFalse(result);
        //Lower left
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(15, -5, 10, 10);
        result = one.overlaps(two);
        Assertions.assertFalse(result);

        //Bottom
        //Bottom left
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(5, -15, 10, 10);
        result = one.overlaps(two);
        Assertions.assertFalse(result);
        //Bottom right
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(-5, -15, 10, 10);
        result = one.overlaps(two);
        Assertions.assertFalse(result);

        //Right
        //Lower right
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(-15, -5, 10, 10);
        result = one.overlaps(two);
        Assertions.assertFalse(result);
        //Upper right
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(-15, 5, 10, 10);
        result = one.overlaps(two);
        Assertions.assertFalse(result);

        //Top
        //Top right
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(-5, 15, 10, 10);
        result = one.overlaps(two);
        Assertions.assertFalse(result);
        //Top left
        one = new Rectangle(0, 0, 10, 10);
        two = new Rectangle(5, 15, 10, 10);
        result = one.overlaps(two);
        Assertions.assertFalse(result);

    }


    @Test
    public void envelopTest01() {
        //one contains two
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(5, 5, 5, 5);
        boolean result = one.contains(two);
        Assertions.assertTrue(result);
    }

    @Test
    public void envelopsTest02() {
        //two identical rectangles
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(0, 0, 10, 10);
        boolean result = one.contains(two);
        Assertions.assertTrue(result);

    }

    @Test
    public void envelopsTest03() {
        //One intersects two but does not envelop
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(5, 5, 10, 10);
        boolean result = one.contains(two);
        Assertions.assertFalse(result);
    }

    @Test
    public void envelopsTest04() {
        //one and two do not
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(-15, -15, 10, 10);
        boolean result = one.contains(two);
        Assertions.assertFalse(result);
    }

    @Test
    public void getIntersectionTest01() {
        //Cases where there is an intersection rectangle
        Rectangle main, second, actual, expected;
        boolean areEqual;
        main = new Rectangle(2, 2, 8, 8);
        //A. Main rectangle is greater in both dimension than second rectangle
        second = new Rectangle(4, 8, 4, 4);
        //1.Middle top
        expected = new Rectangle(4, 8, 4, 2);
        actual = main.getIntersection(second);
        areEqual = expected.equalsWithEpsilon(actual);
        //2.Middle Right
        second.moveRight(4);
        expected = new Rectangle(8, 8, 2, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //3.Right middle
        second.moveDown(4);
        expected = new Rectangle(8, 4, 2, 4);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //4.Bottom right
        second.moveDown(4);
        expected = new Rectangle(8, 2, 2, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //5.Bottom middle
        second.moveLeft(4);
        expected = new Rectangle(4, 2, 4, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //6.Bottom Left
        second.moveLeft(4);
        expected = new Rectangle(2, 2, 2, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //7.Left Middle
        second.moveUp(4);
        expected = new Rectangle(2, 4, 2, 4);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //8.Left Top
        second.moveUp(4);
        expected = new Rectangle(2, 8, 2, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //B. Main rectangle is greater in width but not height than second rectangle
        //1. Left
        second = new Rectangle(0, 0, 4, 12);
        expected = new Rectangle(2, 2, 2, 8);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //2. Middle
        second.moveRight(4);
        expected = new Rectangle(4, 2, 4, 8);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //3. Right
        second.moveRight(4);
        expected = new Rectangle(8, 2, 2, 8);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //C. Main rectangle is greater in height but not width than second rectangle
        //1. Top
        second = new Rectangle(0, 8, 12, 4);
        expected = new Rectangle(2, 8, 8, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //2. Middle
        second.moveDown(4);
        expected = new Rectangle(2, 4, 8, 4);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //3. Bottom
        second.moveDown(4);
        expected = new Rectangle(2, 2, 8, 2);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));

        //Check if any have failed
        Assertions.assertTrue(areEqual);
    }

    @Test
    public void getIntersectionTest02() {
        //Cases where the two rectangles do not intersect
        Rectangle main, second, actual;
        boolean noIntersection;
        main = new Rectangle(2, 2, 8, 8);
        //Top
        second = new Rectangle(4, 12, 4, 4);
        actual = main.getIntersection(second);
        noIntersection = actual == null;
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

        Assertions.assertTrue(noIntersection);
    }

    @Test
    public void getIntersectionTest03() {
        //Edge cases: envelopment
        //A equal rectangles
        Rectangle main, second, actual, expected;
        boolean areEqual;
        main = new Rectangle(2, 2, 8, 8);
        second = new Rectangle(main);
        expected = new Rectangle(main);
        actual = main.getIntersection(second);
        areEqual = expected.equalsWithEpsilon(actual);
        //B main contains second
        main = new Rectangle(2, 2, 8, 8);
        second = new Rectangle(4, 4, 4, 4);
        expected = new Rectangle(second);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //C second contains main
        main = new Rectangle(2, 2, 8, 8);
        second = new Rectangle(0, 0, 12, 12);
        expected = new Rectangle(main);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));

        Assertions.assertTrue(areEqual);
    }

    @Test
    public void getIntersectionTest04() {
        //Edge case: intersections on edges
        Rectangle main, second, actual, expected;
        boolean areEqual;
        main = new Rectangle(2, 2, 8, 8);
        //Top
        second = new Rectangle(4, 10, 4, 4);
        expected = new Rectangle(4, 10, 4, 0);
        actual = main.getIntersection(second);
        areEqual = expected.equalsWithEpsilon(actual);
        //Right
        second = new Rectangle(10, 4, 4, 4);
        expected = new Rectangle(10, 4, 0, 4);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //Bottom
        second = new Rectangle(4, -2, 4, 4);
        expected = new Rectangle(4, 2, 4, 0);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //Left
        second = new Rectangle(-2, 4, 4, 4);
        expected = new Rectangle(2, 4, 0, 4);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //Edge case: intersection on corners
        //Top-Left
        second = new Rectangle(-2, 10, 4, 4);
        expected = new Rectangle(2, 10, 0, 0);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //Top-Right
        second = new Rectangle(10, 10, 4, 4);
        expected = new Rectangle(10, 10, 0, 0);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //Bottom-Right
        second = new Rectangle(10, -2, 4, 4);
        expected = new Rectangle(10, 2, 0, 0);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        //Bottom-Left
        second = new Rectangle(-2, -2, 4, 4);
        expected = new Rectangle(2, 2, 0, 0);
        actual = main.getIntersection(second);
        areEqual = areEqual && (expected.equalsWithEpsilon(actual));
        Assertions.assertTrue(areEqual);
    }

    @Test
    public void createBoundingRectangleFromQuadPointsTest01() {
        Rectangle actual, expected;
        float[] points = {0, 0, 2, 1, 1, 2, -2, 1};
        PdfArray quadpoints = new PdfArray(points);

        expected = new Rectangle(-2, 0, 4, 2);
        actual = Rectangle.createBoundingRectangleFromQuadPoint(quadpoints);
        Boolean areEqual = expected.equalsWithEpsilon(actual);
        Assertions.assertTrue(areEqual);

    }

    @Test
    public void createBoundingRectangleFromQuadPointsTest02() {
        float[] points = {0, 0, 2, 1, 1, 2, -2, 1, 0};
        PdfArray quadpoints = new PdfArray(points);

        boolean exception = false;
        try {
            Rectangle.createBoundingRectangleFromQuadPoint(quadpoints);
        } catch (PdfException e) {
            exception = true;
        }

        Assertions.assertTrue(exception);
    }

    @Test
    public void createBoundingRectanglesFromQuadPointsTest01() {
        List<Rectangle> actual, expected;
        boolean areEqual = true;
        float[] points = {0, 0, 2, 1, 1, 2, -2, 1,
                0, -1, 2, 0, 1, 1, -2, 0};
        PdfArray quadpoints = new PdfArray(points);
        expected = new ArrayList<Rectangle>();
        expected.add(new Rectangle(-2, 0, 4, 2));
        expected.add(new Rectangle(-2, -1, 4, 2));
        actual = Rectangle.createBoundingRectanglesFromQuadPoint(quadpoints);
        for (int i = 0; i < expected.size(); i++) {
            areEqual = areEqual && expected.get(i).equalsWithEpsilon(actual.get(i));
        }
        Assertions.assertTrue(areEqual);
    }

    @Test
    public void createBoundingRectanglesFromQuadPointsTest02() {
        float[] points = {0, 0, 2, 1, 1, 2, -2, 1,
                0, -1, 2, 0, 1, 1, -2, 0,
                1};
        PdfArray quadpoints = new PdfArray(points);
        boolean exception = false;
        try {
            Rectangle.createBoundingRectanglesFromQuadPoint(quadpoints);
        } catch (PdfException e) {
            exception = true;
        }

        Assertions.assertTrue(exception);
    }

    @Test
    public void translateOnRotatedPageTest01() {
        // we need a page with set rotation and page size to test Rectangle#getRectangleOnRotatedPage
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfPage page = pdfDocument.addNewPage();
        Assertions.assertTrue(PageSize.A4.equalsWithEpsilon(page.getPageSize()));

        // Test rectangle
        Rectangle testRectangle = new Rectangle(200, 200, 100, 200);

        Assertions.assertEquals(0, page.getRotation());
        Assertions.assertTrue(new Rectangle(200, 200, 100, 200).equalsWithEpsilon(Rectangle.getRectangleOnRotatedPage(testRectangle, page)));

        page.setRotation(90);
        Assertions.assertEquals(90, page.getRotation());
        Assertions.assertTrue(new Rectangle(195, 200, 200, 100).equalsWithEpsilon(Rectangle.getRectangleOnRotatedPage(testRectangle, page)));

        page.setRotation(180);
        Assertions.assertEquals(180, page.getRotation());
        Assertions.assertTrue(new Rectangle(295, 442, 100, 200).equalsWithEpsilon(Rectangle.getRectangleOnRotatedPage(testRectangle, page)));

        page.setRotation(270);
        Assertions.assertEquals(270, page.getRotation());
        Assertions.assertTrue(new Rectangle(200, 542, 200, 100).equalsWithEpsilon(Rectangle.getRectangleOnRotatedPage(testRectangle, page)));

        page.setRotation(360);
        Assertions.assertEquals(0, page.getRotation());
        Assertions.assertTrue(new Rectangle(200, 200, 100, 200).equalsWithEpsilon(Rectangle.getRectangleOnRotatedPage(testRectangle, page)));
    }

    @Test
    public void calculateBBoxTest() {
        Point a = new Point(100, 100);
        Point b = new Point(200, 100);
        Point c = new Point(200, 200);
        Point d = new Point(100, 200);


        // Zero rotation
        Rectangle.calculateBBox(Arrays.asList(a, b, c, d));
        Assertions.assertTrue(new Rectangle(100, 100, 100, 100).equalsWithEpsilon(Rectangle.calculateBBox(Arrays.asList(a, b, c, d))));

        // 270 degree rotation
        a = new Point(200, 100);
        b = new Point(200, 200);
        c = new Point(100, 200);
        d = new Point(100, 100);

        Assertions.assertTrue(new Rectangle(100, 100, 100, 100).equalsWithEpsilon(Rectangle.calculateBBox(Arrays.asList(a, b, c, d))));

        // it looks as follows:
        // dxxxxxx
        // xxxxxxx
        // cxxxxxa
        // xxxxxxx
        // xxxxxxb
        a = new Point(200, 100);
        b = new Point(200, 0);
        c = new Point(0, 100);
        d = new Point(0, 200);

        Assertions.assertTrue(new Rectangle(0, 0, 200, 200).equalsWithEpsilon(Rectangle.calculateBBox(Arrays.asList(a, b, c, d))));

    }

    @Test
    public void setBBoxWithoutNormalizationTest() {
        Rectangle rectangle = new Rectangle(0, 0, 100, 200);
        Assertions.assertEquals(0, rectangle.getX(), 1e-5);
        Assertions.assertEquals(0, rectangle.getY(), 1e-5);
        Assertions.assertEquals(100, rectangle.getWidth(), 1e-5);
        Assertions.assertEquals(200, rectangle.getHeight(), 1e-5);

        //set bBox without any normalization needed
        rectangle.setBbox(10, 10, 90, 190);
        Assertions.assertEquals(10, rectangle.getX(), 1e-5);
        Assertions.assertEquals(10, rectangle.getY(), 1e-5);
        Assertions.assertEquals(80, rectangle.getWidth(), 1e-5);
        Assertions.assertEquals(180, rectangle.getHeight(), 1e-5);
    }

    @Test
    public void setBBoxNormalizeXTest() {
        Rectangle rectangle = new Rectangle(0, 0, 100, 200);
        Assertions.assertEquals(0, rectangle.getX(), 1e-5);
        Assertions.assertEquals(0, rectangle.getY(), 1e-5);
        Assertions.assertEquals(100, rectangle.getWidth(), 1e-5);
        Assertions.assertEquals(200, rectangle.getHeight(), 1e-5);

        //set bBox where llx > urx
        rectangle.setBbox(90, 10, 10, 190);
        Assertions.assertEquals(10, rectangle.getX(), 1e-5);
        Assertions.assertEquals(10, rectangle.getY(), 1e-5);
        Assertions.assertEquals(80, rectangle.getWidth(), 1e-5);
        Assertions.assertEquals(180, rectangle.getHeight(), 1e-5);
    }

    @Test
    public void setBBoxNormalizeYTest() {
        Rectangle rectangle = new Rectangle(0, 0, 100, 200);
        Assertions.assertEquals(0, rectangle.getX(), 1e-5);
        Assertions.assertEquals(0, rectangle.getY(), 1e-5);
        Assertions.assertEquals(100, rectangle.getWidth(), 1e-5);
        Assertions.assertEquals(200, rectangle.getHeight(), 1e-5);

        //set bBox where lly > ury
        rectangle.setBbox(10, 190, 90, 10);
        Assertions.assertEquals(10, rectangle.getX(), 1e-5);
        Assertions.assertEquals(10, rectangle.getY(), 1e-5);
        Assertions.assertEquals(80, rectangle.getWidth(), 1e-5);
        Assertions.assertEquals(180, rectangle.getHeight(), 1e-5);
    }

    @Test
    public void setXTest() {
        Rectangle rectangle = new Rectangle(0,0,100,200);
        Assertions.assertEquals(0, rectangle.getX(), 1e-5);

        rectangle.setX(50);
        Assertions.assertEquals(50, rectangle.getX(), 1e-5);
    }

    @Test
    public void setYTest() {
        Rectangle rectangle = new Rectangle(0,0,100,200);
        Assertions.assertEquals(0, rectangle.getY(), 1e-5);

        rectangle.setY(50);
        Assertions.assertEquals(50, rectangle.getY(), 1e-5);
    }

    @Test
    public void setWidthTest() {
        Rectangle rectangle = new Rectangle(0,0,100,200);
        Assertions.assertEquals(100, rectangle.getWidth(), 1e-5);

        rectangle.setWidth(50);
        Assertions.assertEquals(50, rectangle.getWidth(), 1e-5);
    }

    @Test
    public void setHeightTest() {
        Rectangle rectangle = new Rectangle(0,0,100,200);
        Assertions.assertEquals(200, rectangle.getHeight(), 1e-5);

        rectangle.setHeight(50);
        Assertions.assertEquals(50, rectangle.getHeight(), 1e-5);
    }

    @Test
    public void increaseHeightTest() {
        Rectangle rectangle = new Rectangle(0,0,100,200);
        Assertions.assertEquals(200, rectangle.getHeight(), 1e-5);

        rectangle.increaseHeight(50);
        Assertions.assertEquals(250, rectangle.getHeight(), 1e-5);
    }

    @Test
    public void decreaseHeightTest() {
        Rectangle rectangle = new Rectangle(0,0,100,200);
        Assertions.assertEquals(200, rectangle.getHeight(), 1e-5);

        rectangle.decreaseHeight(50);
        Assertions.assertEquals(150, rectangle.getHeight(), 1e-5);
    }

    @Test
    public void applyMarginsShrinkTest() {
        Rectangle rectangle = new Rectangle(0,0,100,200);
        Assertions.assertEquals(0, rectangle.getX(), 1e-5);
        Assertions.assertEquals(0, rectangle.getY(), 1e-5);
        Assertions.assertEquals(100, rectangle.getWidth(), 1e-5);
        Assertions.assertEquals(200, rectangle.getHeight(), 1e-5);

        //shrink the rectangle
        rectangle.applyMargins(20,20,20,20, false);
        Assertions.assertEquals(20, rectangle.getX(), 1e-5);
        Assertions.assertEquals(20, rectangle.getY(), 1e-5);
        Assertions.assertEquals(60, rectangle.getWidth(), 1e-5);
        Assertions.assertEquals(160, rectangle.getHeight(), 1e-5);
    }

    @Test
    public void applyMarginsExpandTest() {
        Rectangle rectangle = new Rectangle(20,20,100,200);
        Assertions.assertEquals(20, rectangle.getX(), 1e-5);
        Assertions.assertEquals(20, rectangle.getY(), 1e-5);
        Assertions.assertEquals(100, rectangle.getWidth(), 1e-5);
        Assertions.assertEquals(200, rectangle.getHeight(), 1e-5);

        //expand the rectangle
        rectangle.applyMargins(10,10,10,10, true);
        Assertions.assertEquals(10, rectangle.getX(), 1e-5);
        Assertions.assertEquals(10, rectangle.getY(), 1e-5);
        Assertions.assertEquals(120, rectangle.getWidth(), 1e-5);
        Assertions.assertEquals(220, rectangle.getHeight(), 1e-5);
    }

    @Test
    public void toStringTest() {
        Rectangle rectangle = new Rectangle(0, 0, 100f, 200f);
        String rectangleString = rectangle.toString();
        //Using contains() to check for value instead of equals() on the whole string due to the
        //differences between decimal numbers formatting in java and .NET.
        Assertions.assertTrue(rectangleString.contains("100"));
        Assertions.assertTrue(rectangleString.contains("200"));
    }

    @Test
    public void cloneTest() {
        PageSize originalPageSize = new PageSize(15, 20);
        PageSize copyAsPageSize = (PageSize) originalPageSize.clone();
        Rectangle copyAsRectangle = ((Rectangle) originalPageSize).clone();
        Assertions.assertEquals(PageSize.class, copyAsPageSize.getClass());
        Assertions.assertEquals(PageSize.class, copyAsRectangle.getClass());
    }

    @Test
    public void decreaseWidthTest() {
        Rectangle rectangle = new Rectangle(100, 200);
        rectangle.decreaseWidth(10);
        Assertions.assertEquals(90, rectangle.getWidth(), Rectangle.EPS);
    }
    
    @Test
    public void increaseWidthTest() {
        Rectangle rectangle = new Rectangle(100, 200);
        rectangle.increaseWidth(10);
        Assertions.assertEquals(110, rectangle.getWidth(), Rectangle.EPS);
    }
}
