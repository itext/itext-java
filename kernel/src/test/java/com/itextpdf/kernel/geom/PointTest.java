/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PointTest extends ExtendedITextTest {
    private static double EPSILON_COMPARISON = 1E-12;

    @Test
    public void defaultConstructorTest() {
        Point first = new Point();
        Assertions.assertEquals(0, first.getX(), EPSILON_COMPARISON);
        Assertions.assertEquals(0, first.getY(), EPSILON_COMPARISON);
    }

    @Test
    public void doubleParamConstructorTest() {
        Point first = new Point(0.13, 1.1);
        Assertions.assertEquals(0.13, first.getX(), EPSILON_COMPARISON);
        Assertions.assertEquals(1.1, first.getY(), EPSILON_COMPARISON);
    }

    @Test
    public void intParamConstructorTest() {
        Point first = new Point(2, 3);
        Assertions.assertEquals(2, first.getX(), EPSILON_COMPARISON);
        Assertions.assertEquals(3, first.getY(), EPSILON_COMPARISON);
    }

    @Test
    public void equalsItselfTest() {
        Point first = new Point(1.23, 1.1);

        Assertions.assertTrue(first.equals(first));
        Assertions.assertEquals(first.hashCode(), first.hashCode());
    }

    @Test
    public void equalsToAnotherPointTest() {
        Point first = new Point(1.23, 1.1);
        Point second = new Point(1.23, 1.1);

        Assertions.assertTrue(first.equals(second));
        Assertions.assertTrue(second.equals(first));
        Assertions.assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void notEqualsToAnotherPointTest() {
        Point first = new Point(1.23, 1.1);
        Point second = new Point(1.23, 1.2);

        Assertions.assertFalse(first.equals(second));
        Assertions.assertFalse(second.equals(first));
        Assertions.assertNotEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void notEqualsToNullTest() {
        Point first = new Point(1.23, 1.1);
        Assertions.assertFalse(first.equals(null));
    }

    @Test
    public void distanceByCoordinatesTest() {
        Point first = new Point(1, 1);
        Point second = new Point(1.1, 1.1);

        double expected = Math.sqrt(0.02);
        Assertions.assertEquals(expected, first.distance(second.getX(), second.getY()), EPSILON_COMPARISON);
    }

    @Test
    public void distanceByPointTest() {
        Point first = new Point(1, 1);
        Point second = new Point(1.1, 1.1);

        double expected = Math.sqrt(0.02);
        Assertions.assertEquals(expected, first.distance(second), EPSILON_COMPARISON);
    }

    @Test
    public void distanceItselfTest() {
        Point first = new Point(1, 1);
        Assertions.assertEquals(0, first.distance(first), EPSILON_COMPARISON);
    }

    @Test
    public void toStringTest() {
        Point first = new Point(1.23, 1.1);
        Assertions.assertEquals("Point: [x=1.23,y=1.1]", first.toString());
    }

    @Test
    public void cloneTest() {
        Point first = new Point(1.23, 1.1);
        Point clone = (Point) first.clone();
        Assertions.assertEquals(first, clone);
        Assertions.assertEquals(first.hashCode(), clone.hashCode());
    }

    @Test
    public void moveTest() {
        float w = 3.73f;
        float h = 5.23f;
        Rectangle rectangle = new Rectangle(0, 0, w, h);
        Point[] expectedPoints = rectangle.toPointsArray();

        Point point = new Point(0, 0);

        point.move(w, 0);
        Assertions.assertEquals(expectedPoints[1], point);
        point.move(0, h);
        Assertions.assertEquals(expectedPoints[2], point);
        point.move(-w, 0);
        Assertions.assertEquals(expectedPoints[3], point);
        point.move(0, -h);
        Assertions.assertEquals(expectedPoints[0], point);
    }

    @Test
    public void pointVsItLocationTest() {
        Point first = new Point(1.23, 1.1);

        Point location = first.getLocation();
        Assertions.assertTrue(first != location && first.equals(location));
    }

    @Test
    public void setLocationByDoubleParamTest() {
        Point first = new Point(1.23, 1.1);
        Point second = new Point(3.59, 0.87);

        Assertions.assertNotEquals(first, second);
        first.setLocation(second.getX(), second.getY());
        Assertions.assertEquals(first, second);
    }
}
