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

import com.itextpdf.layout.properties.SizingValue.SizingValueType;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SizingValueTest extends ExtendedITextTest {
    @Test
    public void unitValueTest() {
        SizingValue value = SizingValue.createUnitValue(UnitValue.createPointValue(3.2f));
        Assert.assertEquals(SizingValueType.UNIT, value.getType());
        Assert.assertEquals(3.2f, value.getUnitValue().getValue(), 0.00001);
        Assert.assertEquals(3.2f, (float) value.getAbsoluteValue(), 0.00001);

        value = SizingValue.createUnitValue(UnitValue.createPercentValue(30));
        Assert.assertEquals(SizingValueType.UNIT, value.getType());
        Assert.assertNull(value.getAbsoluteValue());
        Assert.assertEquals(30, value.getUnitValue().getValue(), 0.00001);
    }

    @Test
    public void minMaxContentTest() {
        SizingValue value = SizingValue.createMinContentValue();
        Assert.assertEquals(SizingValueType.MIN_CONTENT, value.getType());
        Assert.assertNull(value.getAbsoluteValue());

        value = SizingValue.createMaxContentValue();
        Assert.assertEquals(SizingValueType.MAX_CONTENT, value.getType());
        Assert.assertNull(value.getAbsoluteValue());
    }

    @Test
    public void autoTest() {
        SizingValue value = SizingValue.createAutoValue();
        Assert.assertEquals(SizingValueType.AUTO, value.getType());
        Assert.assertNull(value.getAbsoluteValue());
    }
}
