/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.layout.property;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.property.BackgroundRepeat.BackgroundRepeatValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BackgroundRepeatUnitTest extends ExtendedITextTest {
    private static final double EPSILON = 0.000001;

    @Test
    public void defaultConstructorTest() {
        final BackgroundRepeat backgroundRepeat = new BackgroundRepeat();
        Assert.assertEquals(BackgroundRepeatValue.REPEAT, backgroundRepeat.getXAxisRepeat());
        Assert.assertEquals(BackgroundRepeatValue.REPEAT, backgroundRepeat.getYAxisRepeat());
    }

    @Test
    public void oneBackgroundRepeatValueConstructorTest() {
        final BackgroundRepeat backgroundRepeat = new BackgroundRepeat(BackgroundRepeatValue.ROUND);
        Assert.assertEquals(BackgroundRepeatValue.ROUND, backgroundRepeat.getXAxisRepeat());
        Assert.assertEquals(BackgroundRepeatValue.ROUND, backgroundRepeat.getYAxisRepeat());
    }

    @Test
    public void twoBackgroundRepeatValueConstructorTest() {
        final BackgroundRepeat backgroundRepeat = new BackgroundRepeat(BackgroundRepeatValue.SPACE, BackgroundRepeatValue.ROUND);
        Assert.assertEquals(BackgroundRepeatValue.SPACE, backgroundRepeat.getXAxisRepeat());
        Assert.assertEquals(BackgroundRepeatValue.ROUND, backgroundRepeat.getYAxisRepeat());
    }

    @Test
    public void isNoRepeatOnAxis() {
        BackgroundRepeat backgroundRepeat = new BackgroundRepeat(BackgroundRepeatValue.SPACE, BackgroundRepeatValue.REPEAT);
        Assert.assertFalse(backgroundRepeat.isNoRepeatOnXAxis());
        Assert.assertFalse(backgroundRepeat.isNoRepeatOnYAxis());

        backgroundRepeat = new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT, BackgroundRepeatValue.ROUND);
        Assert.assertTrue(backgroundRepeat.isNoRepeatOnXAxis());
        Assert.assertFalse(backgroundRepeat.isNoRepeatOnYAxis());

        backgroundRepeat = new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT);
        Assert.assertTrue(backgroundRepeat.isNoRepeatOnXAxis());
        Assert.assertTrue(backgroundRepeat.isNoRepeatOnYAxis());
    }

    @Test
    public void prepareRectangleRepeatNoRepeatTest() {
        final BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeatValue.REPEAT, BackgroundRepeatValue.NO_REPEAT);
        Rectangle imageRect = new Rectangle(0, 0, 50, 60);
        Rectangle originalRect = new Rectangle(imageRect);
        Rectangle availableArea = new Rectangle(0, 0, 160, 123);

        Point whitespace = repeat.prepareRectangleToDrawingAndGetWhitespace(imageRect, availableArea, new BackgroundSize());
        Assert.assertEquals(0, whitespace.getX(), EPSILON);
        Assert.assertEquals(0, whitespace.getY(), EPSILON);
        Assert.assertTrue(originalRect.equalsWithEpsilon(imageRect));
    }

    @Test
    public void prepareRectangleSpaceRepeatTest() {
        final BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeatValue.SPACE, BackgroundRepeatValue.REPEAT);
        Rectangle imageRect = new Rectangle(0, 0, 50, 60);
        Rectangle originalRect = new Rectangle(imageRect);
        Rectangle availableArea = new Rectangle(0, 0, 160, 123);

        Point whitespace = repeat.prepareRectangleToDrawingAndGetWhitespace(imageRect, availableArea, new BackgroundSize());
        Assert.assertEquals(5, whitespace.getX(), EPSILON);
        Assert.assertEquals(0, whitespace.getY(), EPSILON);
        Assert.assertTrue(originalRect.equalsWithEpsilon(imageRect));
    }

    @Test
    public void prepareRectangleNoRepeatSpaceTest() {
        final BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT, BackgroundRepeatValue.SPACE);
        Rectangle imageRect = new Rectangle(0, 63, 50, 60);
        Rectangle originalRect = new Rectangle(imageRect);
        Rectangle availableArea = new Rectangle(0, 0, 160, 123);

        Point whitespace = repeat.prepareRectangleToDrawingAndGetWhitespace(imageRect, availableArea, new BackgroundSize());
        Assert.assertEquals(0, whitespace.getX(), EPSILON);
        Assert.assertEquals(3, whitespace.getY(), EPSILON);
        Assert.assertTrue(originalRect.equalsWithEpsilon(imageRect));
    }

    @Test
    public void prepareRectangleSpaceSpaceTest() {
        final BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeatValue.SPACE);
        Rectangle imageRect = new Rectangle(0, 63, 50, 60);
        Rectangle originalRect = new Rectangle(imageRect);
        Rectangle availableArea = new Rectangle(0, 0, 160, 123);

        Point whitespace = repeat.prepareRectangleToDrawingAndGetWhitespace(imageRect, availableArea, new BackgroundSize());
        Assert.assertEquals(5, whitespace.getX(), EPSILON);
        Assert.assertEquals(3, whitespace.getY(), EPSILON);
        Assert.assertTrue(originalRect.equalsWithEpsilon(imageRect));
    }

    @Test
    public void prepareRectangleSpaceSpaceNoAvailableSpaceTest() {
        final BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeatValue.SPACE);
        Rectangle imageRect = new Rectangle(0, -5, 50, 60);
        Rectangle originalRect = new Rectangle(imageRect);
        Rectangle availableArea = new Rectangle(0, 0, 45, 55);

        Point whitespace = repeat.prepareRectangleToDrawingAndGetWhitespace(imageRect, availableArea, new BackgroundSize());
        Assert.assertEquals(0, whitespace.getX(), EPSILON);
        Assert.assertEquals(0, whitespace.getY(), EPSILON);
        Assert.assertTrue(originalRect.equalsWithEpsilon(imageRect));
    }

    @Test
    public void prepareRectangleRoundNoRepeatLessAndMoreHalfImageSizeTest() {
        final BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeatValue.ROUND, BackgroundRepeatValue.NO_REPEAT);
        Rectangle imageRect = new Rectangle(0, 0, 50, 70);
        Rectangle availableArea = new Rectangle(0, 0, 120, 180);

        Point whitespace = repeat.prepareRectangleToDrawingAndGetWhitespace(imageRect, availableArea, new BackgroundSize());
        Assert.assertEquals(0, whitespace.getX(), EPSILON);
        Assert.assertEquals(0, whitespace.getY(), EPSILON);
        Assert.assertEquals(0, imageRect.getX(), EPSILON);
        Assert.assertEquals(-14, imageRect.getY(), EPSILON);
        Assert.assertEquals(60, imageRect.getWidth(), EPSILON);
        Assert.assertEquals(84, imageRect.getHeight(), EPSILON);
    }

    @Test
    public void prepareRectangleRepeatRoundLessHalfImageSizeTest() {
        final BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeatValue.REPEAT, BackgroundRepeatValue.ROUND);
        Rectangle imageRect = new Rectangle(0, 0, 50, 75);
        Rectangle availableArea = new Rectangle(0, 0, 120, 180);

        Point whitespace = repeat.prepareRectangleToDrawingAndGetWhitespace(imageRect, availableArea, new BackgroundSize());
        Assert.assertEquals(0, whitespace.getX(), EPSILON);
        Assert.assertEquals(0, whitespace.getY(), EPSILON);
        Assert.assertEquals(0, imageRect.getX(), EPSILON);
        Assert.assertEquals(-15, imageRect.getY(), EPSILON);
        Assert.assertEquals(60, imageRect.getWidth(), EPSILON);
        Assert.assertEquals(90, imageRect.getHeight(), EPSILON);
    }

    @Test
    public void prepareRectangleRoundRoundLessAndMoreHalfImageSizeTest() {
        final BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeatValue.ROUND);
        Rectangle imageRect = new Rectangle(0, 0, 50, 70);
        Rectangle availableArea = new Rectangle(0, 0, 120, 180);

        Point whitespace = repeat.prepareRectangleToDrawingAndGetWhitespace(imageRect, availableArea, new BackgroundSize());
        Assert.assertEquals(0, whitespace.getX(), EPSILON);
        Assert.assertEquals(0, whitespace.getY(), EPSILON);
        Assert.assertEquals(0, imageRect.getX(), EPSILON);
        Assert.assertEquals(10, imageRect.getY(), EPSILON);
        Assert.assertEquals(60, imageRect.getWidth(), EPSILON);
        Assert.assertEquals(60, imageRect.getHeight(), EPSILON);
    }

    @Test
    public void prepareRectangleRoundRoundMoreAndLessHalfImageSizeTest() {
        final BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeatValue.ROUND);
        Rectangle imageRect = new Rectangle(0, 0, 50, 70);
        Rectangle availableArea = new Rectangle(0, 0, 144, 160);

        Point whitespace = repeat.prepareRectangleToDrawingAndGetWhitespace(imageRect, availableArea, new BackgroundSize());
        Assert.assertEquals(0, whitespace.getX(), EPSILON);
        Assert.assertEquals(0, whitespace.getY(), EPSILON);
        Assert.assertEquals(0, imageRect.getX(), EPSILON);
        Assert.assertEquals(-10, imageRect.getY(), EPSILON);
        Assert.assertEquals(48, imageRect.getWidth(), EPSILON);
        Assert.assertEquals(80, imageRect.getHeight(), EPSILON);
    }

    @Test
    public void prepareRectangleRoundRoundMoreHalfImageSizeTest() {
        final BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeatValue.ROUND);
        Rectangle imageRect = new Rectangle(0, 0, 50, 70);
        Rectangle availableArea = new Rectangle(0, 0, 144, 180);

        Point whitespace = repeat.prepareRectangleToDrawingAndGetWhitespace(imageRect, availableArea, new BackgroundSize());
        Assert.assertEquals(0, whitespace.getX(), EPSILON);
        Assert.assertEquals(0, whitespace.getY(), EPSILON);
        Assert.assertEquals(0, imageRect.getX(), EPSILON);
        Assert.assertEquals(10, imageRect.getY(), EPSILON);
        Assert.assertEquals(48, imageRect.getWidth(), EPSILON);
        Assert.assertEquals(60, imageRect.getHeight(), EPSILON);
    }

    @Test
    public void prepareRectangleRoundRoundLessHalfImageSizeTest() {
        final BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeatValue.ROUND);
        Rectangle imageRect = new Rectangle(0, 0, 50, 70);
        Rectangle availableArea = new Rectangle(0, 0, 120, 160);

        Point whitespace = repeat.prepareRectangleToDrawingAndGetWhitespace(imageRect, availableArea, new BackgroundSize());
        Assert.assertEquals(0, whitespace.getX(), EPSILON);
        Assert.assertEquals(0, whitespace.getY(), EPSILON);
        Assert.assertEquals(0, imageRect.getX(), EPSILON);
        Assert.assertEquals(-10, imageRect.getY(), EPSILON);
        Assert.assertEquals(60, imageRect.getWidth(), EPSILON);
        Assert.assertEquals(80, imageRect.getHeight(), EPSILON);
    }

    @Test
    public void prepareRectangleSpaceRoundMoreHalfImageSizeTest() {
        final BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeatValue.SPACE, BackgroundRepeatValue.ROUND);
        Rectangle imageRect = new Rectangle(0, 0, 50, 75);
        Rectangle availableArea = new Rectangle(0, 0, 130, 180);

        Point whitespace = repeat.prepareRectangleToDrawingAndGetWhitespace(imageRect, availableArea, new BackgroundSize());
        Assert.assertEquals(10, whitespace.getX(), EPSILON);
        Assert.assertEquals(0, whitespace.getY(), EPSILON);
        Assert.assertEquals(0, imageRect.getX(), EPSILON);
        Assert.assertEquals(-15, imageRect.getY(), EPSILON);
        Assert.assertEquals(60, imageRect.getWidth(), EPSILON);
        Assert.assertEquals(90, imageRect.getHeight(), EPSILON);
    }

    @Test
    public void prepareRectangleRoundSpaceLessHalfImageSizeTest() {
        final BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeatValue.ROUND, BackgroundRepeatValue.SPACE);
        Rectangle imageRect = new Rectangle(0, 0, 50, 75);
        Rectangle availableArea = new Rectangle(0, 0, 120, 369);

        Point whitespace = repeat.prepareRectangleToDrawingAndGetWhitespace(imageRect, availableArea, new BackgroundSize());
        Assert.assertEquals(0, whitespace.getX(), EPSILON);
        Assert.assertEquals(3, whitespace.getY(), EPSILON);
        Assert.assertEquals(0, imageRect.getX(), EPSILON);
        Assert.assertEquals(279, imageRect.getY(), EPSILON);
        Assert.assertEquals(60, imageRect.getWidth(), EPSILON);
        Assert.assertEquals(90, imageRect.getHeight(), EPSILON);
    }
}
