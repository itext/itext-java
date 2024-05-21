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
package com.itextpdf.layout.properties;

import com.itextpdf.layout.properties.GridValue.GridValueType;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class GridValueTest extends ExtendedITextTest {
    @Test
    public void sizingValueTest() {
        GridValue value = GridValue.createSizeValue(SizingValue.createUnitValue(UnitValue.createPointValue(1.3f)));
        Assert.assertEquals(GridValueType.SIZING, value.getType());
        Assert.assertEquals(1.3f, (float) value.getAbsoluteValue(), 0.00001);

        value = GridValue.createSizeValue(SizingValue.createUnitValue(UnitValue.createPercentValue(30)));
        Assert.assertEquals(GridValueType.SIZING, value.getType());
        Assert.assertNull(value.getAbsoluteValue());
        Assert.assertEquals(30, value.getSizingValue().getUnitValue().getValue(), 0.00001);
    }

    @Test
    public void unitValueTest() {
        GridValue value = GridValue.createUnitValue(UnitValue.createPointValue(1.3f));
        Assert.assertEquals(GridValueType.SIZING, value.getType());
        Assert.assertEquals(1.3f, (float) value.getAbsoluteValue(), 0.00001);

        value = GridValue.createUnitValue(UnitValue.createPercentValue(30));
        Assert.assertEquals(GridValueType.SIZING, value.getType());
        Assert.assertNull(value.getAbsoluteValue());
        Assert.assertEquals(30, value.getSizingValue().getUnitValue().getValue(), 0.00001);
    }

    @Test
    public void flexValueTest() {
        GridValue value = GridValue.createFlexValue(1.5f);
        Assert.assertEquals(GridValueType.FLEX, value.getType());
        Assert.assertNull(value.getAbsoluteValue());
        Assert.assertEquals(1.5f, (float) value.getFlexValue(), 0.00001);
    }
}
