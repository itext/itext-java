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

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class GridValueTest extends ExtendedITextTest {
    @Test
    public void unitValueTest() {
        GridValue value = GridValue.createPointValue(3.2f);
        Assert.assertTrue(value.isPointValue());
        Assert.assertEquals(3.2f, value.getValue(), 0.00001);

        value = GridValue.createPercentValue(30f);
        Assert.assertTrue(value.isPercentValue());
        Assert.assertEquals(30, value.getValue(), 0.00001);
    }

    @Test
    public void minMaxContentTest() {
        GridValue value = GridValue.createMinContentValue();
        Assert.assertTrue(value.isMinContentValue());
        Assert.assertNull(value.getValue());

        value = GridValue.createMaxContentValue();
        Assert.assertTrue(value.isMaxContentValue());
        Assert.assertNull(value.getValue());
    }

    @Test
    public void autoTest() {
        GridValue value = GridValue.createAutoValue();
        Assert.assertTrue(value.isAutoValue());
        Assert.assertNull(value.getValue());
    }

    @Test
    public void flexValueTest() {
        GridValue value = GridValue.createFlexValue(1.5f);
        Assert.assertTrue(value.isFlexibleValue());
        Assert.assertEquals(1.5f, (float) value.getValue(), 0.00001);
    }
}
