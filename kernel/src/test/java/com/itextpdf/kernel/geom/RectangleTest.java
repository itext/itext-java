/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.geom;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
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
        boolean result = one.overlaps(two);
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
        boolean result = one.overlaps(two);
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
        boolean result = one.contains(two);
        Assert.assertTrue(result);
    }

    @Test
    public void envelopsTest02() {
        //two identical rectangles
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(0, 0, 10, 10);
        boolean result = one.contains(two);
        Assert.assertTrue(result);

    }

    @Test
    public void envelopsTest03() {
        //One intersects two but does not envelop
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(5, 5, 10, 10);
        boolean result = one.contains(two);
        Assert.assertFalse(result);
    }

    @Test
    public void envelopsTest04() {
        //one and two do not
        Rectangle one = new Rectangle(0, 0, 10, 10);
        Rectangle two = new Rectangle(-15, -15, 10, 10);
        boolean result = one.contains(two);
        Assert.assertFalse(result);
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
        Assert.assertTrue(areEqual);
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

        Assert.assertTrue(noIntersection);
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

        Assert.assertTrue(areEqual);
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
        Assert.assertTrue(areEqual);
    }

    @Test
    public void createBoundingRectangleFromQuadPointsTest01() {
        Rectangle actual, expected;
        float[] points = {0, 0, 2, 1, 1, 2, -2, 1};
        PdfArray quadpoints = new PdfArray(points);

        expected = new Rectangle(-2, 0, 4, 2);
        actual = Rectangle.createBoundingRectangleFromQuadPoint(quadpoints);
        Boolean areEqual = expected.equalsWithEpsilon(actual);
        Assert.assertTrue(areEqual);

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

        Assert.assertTrue(exception);
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
        for(int i=0; i<expected.size();i++){
            areEqual = areEqual && expected.get(i).equalsWithEpsilon(actual.get(i));
        }
        Assert.assertTrue(areEqual);
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

        Assert.assertTrue(exception);
    }
}
