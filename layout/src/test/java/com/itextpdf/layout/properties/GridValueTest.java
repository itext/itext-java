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
package com.itextpdf.layout.properties;

import com.itextpdf.layout.properties.grid.AutoValue;
import com.itextpdf.layout.properties.grid.FitContentValue;
import com.itextpdf.layout.properties.grid.FlexValue;
import com.itextpdf.layout.properties.grid.GridValue;
import com.itextpdf.layout.properties.grid.LengthValue;
import com.itextpdf.layout.properties.grid.MaxContentValue;
import com.itextpdf.layout.properties.grid.MinContentValue;
import com.itextpdf.layout.properties.grid.MinMaxValue;
import com.itextpdf.layout.properties.grid.PercentValue;
import com.itextpdf.layout.properties.grid.PointValue;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class GridValueTest extends ExtendedITextTest {
    @Test
    public void unitValueTest() {
        LengthValue value = new PointValue(3.2f);
        Assertions.assertEquals(value.getType(), GridValue.ValueType.POINT);
        Assertions.assertEquals(3.2f, (float) value.getValue(), 0.00001);

        value = new PercentValue(30f);
        Assertions.assertEquals(value.getType(), GridValue.ValueType.PERCENT);
        Assertions.assertEquals(30, (float) value.getValue(), 0.00001);
    }

    @Test
    public void minMaxContentTest() {
        GridValue value = MinContentValue.VALUE;
        Assertions.assertEquals(value.getType(), GridValue.ValueType.MIN_CONTENT);

        value = MaxContentValue.VALUE;
        Assertions.assertEquals(value.getType(), GridValue.ValueType.MAX_CONTENT);
    }

    @Test
    public void autoTest() {
        GridValue value = AutoValue.VALUE;
        Assertions.assertEquals(value.getType(), GridValue.ValueType.AUTO);
    }

    @Test
    public void flexValueTest() {
        FlexValue value = new FlexValue(1.5f);
        Assertions.assertEquals(value.getType(), GridValue.ValueType.FLEX);
        Assertions.assertEquals(1.5f, (float) value.getFlex(), 0.00001);
    }

    @Test
    public void fitContentTest() {
        FitContentValue value = new FitContentValue(new PointValue(50.0f));
        Assertions.assertEquals(value.getType(), GridValue.ValueType.FIT_CONTENT);
        Assertions.assertEquals(new PointValue(50.0f).getValue(), value.getLength().getValue(), 0.00001);
        value = new FitContentValue(UnitValue.createPercentValue(20.0f));
        Assertions.assertEquals(new PercentValue(20.0f).getValue(), value.getLength().getValue(), 0.00001);
    }

    @Test
    public void minMaxTest() {
        MinMaxValue value = new MinMaxValue(new PointValue(50.0f), new FlexValue(2.0f));
        Assertions.assertEquals(value.getType(), GridValue.ValueType.MINMAX);
        Assertions.assertEquals(new PointValue(50.0f).getValue(), ((PointValue)value.getMin()).getValue(), 0.00001);
        Assertions.assertEquals(new FlexValue(2.0f).getFlex(), ((FlexValue)value.getMax()).getFlex(), 0.00001);
    }
}
