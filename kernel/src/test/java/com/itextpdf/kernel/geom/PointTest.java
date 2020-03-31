/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PointTest extends ExtendedITextTest {
    private static double EPSILON_COMPARISON = 1E-12;

    @Test
    public void defaultConstructorTest() {
        Point first = new Point();
        Assert.assertEquals(0, first.x, EPSILON_COMPARISON);
        Assert.assertEquals(0, first.y, EPSILON_COMPARISON);
    }

    @Test
    public void doubleParamConstructorTest() {
        Point first = new Point(0.13, 1.1);
        Assert.assertEquals(0.13, first.getX(), EPSILON_COMPARISON);
        Assert.assertEquals(1.1, first.getY(), EPSILON_COMPARISON);
    }

    @Test
    public void intParamConstructorTest() {
        Point first = new Point(2, 3);
        Assert.assertEquals(2, first.x, EPSILON_COMPARISON);
        Assert.assertEquals(3, first.y, EPSILON_COMPARISON);
    }

    @Test
    public void copyConstructorTest() {
        Point second = new Point(new Point(0.13, 1.1));
        Assert.assertEquals(0.13, second.getX(), EPSILON_COMPARISON);
        Assert.assertEquals(1.1, second.getY(), EPSILON_COMPARISON);
    }

    @Test
    public void equalsItselfTest() {
        Point first = new Point(1.23, 1.1);

        Assert.assertTrue(first.equals(first));
        Assert.assertEquals(first.hashCode(), first.hashCode());
    }

    @Test
    public void equalsToAnotherPointTest() {
        Point first = new Point(1.23, 1.1);
        Point second = new Point(1.23, 1.1);

        Assert.assertTrue(first.equals(second));
        Assert.assertTrue(second.equals(first));
        Assert.assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void notEqualsToAnotherPointTest() {
        Point first = new Point(1.23, 1.1);
        Point second = new Point(1.23, 1.2);

        Assert.assertFalse(first.equals(second));
        Assert.assertFalse(second.equals(first));
        Assert.assertNotEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void notEqualsToNullTest() {
        Point first = new Point(1.23, 1.1);
        Assert.assertFalse(first.equals(null));
    }

    @Test
    public void distanceSquareBetweenCoordinatesTest() {
        Point first = new Point(1, 1);
        Point second = new Point(1.1, 1.1);

        double expected = 0.02;
        Assert.assertEquals(expected, Point.distanceSq(first.x, first.y, second.x, second.y), EPSILON_COMPARISON);
    }

    @Test
    public void distanceSquareByCoordinatesTest() {
        Point first = new Point(1, 1);
        Point second = new Point(1.1, 1.1);

        double expected = 0.02;
        Assert.assertEquals(expected, first.distanceSq(second.x, second.y), EPSILON_COMPARISON);
    }

    @Test
    public void distanceSquareByPointTest() {
        Point first = new Point(1, 1);
        Point second = new Point(1.1, 1.1);

        double expected = 0.02;
        Assert.assertEquals(expected, first.distanceSq(second), EPSILON_COMPARISON);
    }

    @Test
    public void distanceItselfSquareTest() {
        Point first = new Point(1, 1);
        Assert.assertEquals(0, first.distanceSq(first), EPSILON_COMPARISON);
    }

    @Test
    public void distanceBetweenCoordinatesTest() {
        Point first = new Point(1, 1);
        Point second = new Point(1.1, 1.1);

        double expected = Math.sqrt(0.02);
        Assert.assertEquals(expected, Point.distance(first.x, first.y, second.x, second.y), EPSILON_COMPARISON);
    }

    @Test
    public void distanceByCoordinatesTest() {
        Point first = new Point(1, 1);
        Point second = new Point(1.1, 1.1);

        double expected = Math.sqrt(0.02);
        Assert.assertEquals(expected, first.distance(second.x, second.y), EPSILON_COMPARISON);
    }

    @Test
    public void distanceByPointTest() {
        Point first = new Point(1, 1);
        Point second = new Point(1.1, 1.1);

        double expected = Math.sqrt(0.02);
        Assert.assertEquals(expected, first.distance(second), EPSILON_COMPARISON);
    }

    @Test
    public void distanceItselfTest() {
        Point first = new Point(1, 1);
        Assert.assertEquals(0, first.distance(first), EPSILON_COMPARISON);
    }

    @Test
    public void toStringTest() {
        Point first = new Point(1.23, 1.1);
        Assert.assertEquals("Point: [x=1.23,y=1.1]", first.toString());
    }

    @Test
    public void cloneTest() {
        Point first = new Point(1.23, 1.1);
        Point clone = (Point) first.clone();
        Assert.assertEquals(first, clone);
        Assert.assertEquals(first.hashCode(), clone.hashCode());
    }

    @Test
    public void translateTest() {
        float w = 3.73f;
        float h = 5.23f;
        Rectangle rectangle = new Rectangle(0, 0, w, h);
        Point[] expectedPoints = rectangle.toPointsArray();

        Point point = new Point(0, 0);

        point.translate(w, 0);
        Assert.assertEquals(expectedPoints[1], point);
        point.translate(0, h);
        Assert.assertEquals(expectedPoints[2], point);
        point.translate(-w, 0);
        Assert.assertEquals(expectedPoints[3], point);
        point.translate(0, -h);
        Assert.assertEquals(expectedPoints[0], point);
    }

    @Test
    public void pointVsItLocationTest() {
        Point first = new Point(1.23, 1.1);

        Point location = first.getLocation();
        Assert.assertTrue(first != location && first.equals(location));
    }

    @Test
    public void setLocationByPointTest() {
        Point first = new Point(1.23, 1.1);
        Point second = new Point(3.59, 0.87);

        Assert.assertNotEquals(first, second);
        first.setLocation(second);
        Assert.assertEquals(first, second);
    }

    @Test
    public void setLocationByDoubleParamTest() {
        Point first = new Point(1.23, 1.1);
        Point second = new Point(3.59, 0.87);

        Assert.assertNotEquals(first, second);
        first.setLocation(second.x, second.y);
        Assert.assertEquals(first, second);
    }

    @Test
    public void setLocationByIntParamTest() {
        Point first = new Point(1.23, 1.1);
        Point second = new Point(3.59, 0.87);

        Assert.assertNotEquals(first, second);
        first.setLocation((int) second.x, (int) second.y);
        Assert.assertEquals(first, new Point(3, 0));
    }

    @Test
    public void movePointTest() {
        Point first = new Point(1.23, 1.1);
        Point second = new Point(3.59, 0.87);

        Assert.assertNotEquals(first, second);
        first.move(second.x, second.y);
        Assert.assertEquals(first, second);
    }
}
