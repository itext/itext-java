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
package com.itextpdf.layout;

import com.itextpdf.layout.property.BackgroundPosition;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BackgroundPositionUnitTest extends ExtendedITextTest {

    @Test
    public void defaultConstructorTest() {
        BackgroundPosition position = new BackgroundPosition();

        Assert.assertEquals(new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.LEFT).setPositionY(BackgroundPosition.PositionY.TOP)
                .setXShift(new UnitValue(UnitValue.POINT, 0)).setYShift(new UnitValue(UnitValue.POINT, 0)), position);
    }

    @Test
    public void processPercentageTest() {
        BackgroundPosition position = new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.RIGHT).setPositionY(BackgroundPosition.PositionY.TOP)
                .setXShift(new UnitValue(UnitValue.POINT, 30)).setYShift(new UnitValue(UnitValue.PERCENT, 10));

        UnitValue xPosition = UnitValue.createPointValue(0);
        UnitValue yPosition = UnitValue.createPointValue(0);
        position.calculatePositionValues(1000, 300, xPosition, yPosition);

        Assert.assertEquals(new UnitValue(UnitValue.POINT, 970), xPosition);
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 30), yPosition);

        position = new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.CENTER).setPositionY(BackgroundPosition.PositionY.BOTTOM)
                .setXShift(new UnitValue(UnitValue.POINT, 30)).setYShift(new UnitValue(UnitValue.PERCENT, 10));

        xPosition = UnitValue.createPointValue(0);
        yPosition = UnitValue.createPointValue(0);
        position.calculatePositionValues(1000, 300, xPosition, yPosition);

        Assert.assertEquals(new UnitValue(UnitValue.POINT, 0), xPosition);
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 270), yPosition);
    }

    @Test
    public void processValidPositionTest() {
        BackgroundPosition position = new BackgroundPosition().setXShift(new UnitValue(UnitValue.PERCENT, 17));
        final UnitValue valueX = new UnitValue(UnitValue.PERCENT, 0);
        final UnitValue valueY = new UnitValue(UnitValue.PERCENT, 0);
        position.calculatePositionValues(1000, 1000, valueX, valueY);
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 170), valueX);
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 0), valueY);
    }

    @Test
    public void processInvalidPositionTest() {
        BackgroundPosition position = new BackgroundPosition()
                .setPositionX(BackgroundPosition.PositionX.CENTER).setXShift(new UnitValue(UnitValue.PERCENT, 17))
                .setPositionY(BackgroundPosition.PositionY.CENTER).setYShift(UnitValue.createPointValue(40));
        final UnitValue valueX = new UnitValue(UnitValue.PERCENT, 0);
        final UnitValue valueY = new UnitValue(UnitValue.PERCENT, 0);
        position.calculatePositionValues(1000, 1000, valueX, valueY);
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 0), valueX);
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 0), valueY);
    }

    @Test
    public void processCenterWithoutShiftPositionTest() {
        BackgroundPosition position = new BackgroundPosition()
                .setPositionX(BackgroundPosition.PositionX.CENTER).setXShift(null)
                .setPositionY(BackgroundPosition.PositionY.CENTER).setYShift(null);
        final UnitValue valueX = new UnitValue(UnitValue.PERCENT, 0);
        final UnitValue valueY = new UnitValue(UnitValue.PERCENT, 0);
        position.calculatePositionValues(1000, 1000, valueX, valueY);
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 500), valueX);
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 500), valueY);
    }

    @Test
    public void processCenterWithZeroShiftPositionTest() {
        BackgroundPosition position = new BackgroundPosition()
                .setPositionX(BackgroundPosition.PositionX.CENTER).setXShift(UnitValue.createPointValue(0))
                .setPositionY(BackgroundPosition.PositionY.CENTER).setYShift(UnitValue.createPercentValue(0));
        final UnitValue valueX = new UnitValue(UnitValue.PERCENT, 0);
        final UnitValue valueY = new UnitValue(UnitValue.PERCENT, 0);
        position.calculatePositionValues(1000, 1000, valueX, valueY);
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 500), valueX);
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 500), valueY);
    }

    @Test
    public void processCenterWithAlmostZeroShiftPositionTest() {
        BackgroundPosition position = new BackgroundPosition()
                .setPositionX(BackgroundPosition.PositionX.CENTER).setXShift(UnitValue.createPointValue(0.002f))
                .setPositionY(BackgroundPosition.PositionY.CENTER).setYShift(UnitValue.createPercentValue(0.0002f));
        final UnitValue valueX = new UnitValue(UnitValue.PERCENT, 0);
        final UnitValue valueY = new UnitValue(UnitValue.PERCENT, 0);
        position.calculatePositionValues(1000, 1000, valueX, valueY);
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 0), valueX);
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 0), valueY);
    }

    @Test
    public void gettersTest() {
        BackgroundPosition position = new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.RIGHT).setPositionY(BackgroundPosition.PositionY.BOTTOM)
                .setXShift(new UnitValue(UnitValue.POINT, 30)).setYShift(new UnitValue(UnitValue.PERCENT, 10));

        Assert.assertEquals(BackgroundPosition.PositionX.RIGHT, position.getPositionX());
        Assert.assertEquals(BackgroundPosition.PositionY.BOTTOM, position.getPositionY());
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 30), position.getXShift());
        Assert.assertEquals(new UnitValue(UnitValue.PERCENT, 10), position.getYShift());
    }

    @Test
    public void settersTest() {
        BackgroundPosition position = new BackgroundPosition();

        position.setPositionX(BackgroundPosition.PositionX.RIGHT);
        position.setPositionY(BackgroundPosition.PositionY.BOTTOM);
        position.setXShift(new UnitValue(UnitValue.POINT, 30));
        position.setYShift(new UnitValue(UnitValue.PERCENT, 10));

        Assert.assertEquals(BackgroundPosition.PositionX.RIGHT, position.getPositionX());
        Assert.assertEquals(BackgroundPosition.PositionY.BOTTOM, position.getPositionY());
        Assert.assertEquals(new UnitValue(UnitValue.POINT, 30), position.getXShift());
        Assert.assertEquals(new UnitValue(UnitValue.PERCENT, 10), position.getYShift());
    }

    @Test
    public void equalsTest() {
        BackgroundPosition position1 = new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.RIGHT).setPositionY(BackgroundPosition.PositionY.BOTTOM)
                .setXShift(new UnitValue(UnitValue.POINT, 30)).setYShift(new UnitValue(UnitValue.PERCENT, 10));

        BackgroundPosition position2 = new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.RIGHT).setPositionY(BackgroundPosition.PositionY.BOTTOM)
                .setXShift(new UnitValue(UnitValue.POINT, 30)).setYShift(new UnitValue(UnitValue.PERCENT, 10));

        Assert.assertEquals(position1, position2);
    }

    @Test
    public void equalsSameObjectTest() {
        BackgroundPosition position1 = new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.RIGHT).setPositionY(BackgroundPosition.PositionY.BOTTOM)
                .setXShift(new UnitValue(UnitValue.POINT, 30)).setYShift(new UnitValue(UnitValue.PERCENT, 10));

        Assert.assertEquals(position1, position1);
    }

    @Test
    public void equalsNullTest() {
        BackgroundPosition position1 = new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.RIGHT).setPositionY(BackgroundPosition.PositionY.BOTTOM)
                .setXShift(new UnitValue(UnitValue.POINT, 30)).setYShift(new UnitValue(UnitValue.PERCENT, 10));

        Assert.assertNotEquals(position1, null);
    }

    @Test
    public void equalsWrongTypeTest() {
        BackgroundPosition position1 = new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.RIGHT).setPositionY(BackgroundPosition.PositionY.BOTTOM)
                .setXShift(new UnitValue(UnitValue.POINT, 30)).setYShift(new UnitValue(UnitValue.PERCENT, 10));

        Assert.assertNotEquals(position1, 5);
    }

    @Test
    public void hashCodeTest() {
        BackgroundPosition position = new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.RIGHT).setPositionY(BackgroundPosition.PositionY.BOTTOM)
                .setXShift(new UnitValue(UnitValue.POINT, 30)).setYShift(new UnitValue(UnitValue.PERCENT, 10));

        Assert.assertEquals(1028641704, position.hashCode());

        position.setXShift(new UnitValue(UnitValue.POINT, 37));

        Assert.assertEquals(1101779880, position.hashCode());
    }
}
