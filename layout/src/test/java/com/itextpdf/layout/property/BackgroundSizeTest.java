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

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BackgroundSizeTest extends ExtendedITextTest {

    @Test
    public void constructorTest() {
        final BackgroundSize size = new BackgroundSize();

        Assert.assertFalse(size.isContain());
        Assert.assertFalse(size.isCover());
        Assert.assertNull(size.getBackgroundWidthSize());
        Assert.assertNull(size.getBackgroundHeightSize());
    }

    @Test
    public void clearAndSetToCoverTest() {
        final BackgroundSize size = new BackgroundSize();

        size.setBackgroundSizeToValues(UnitValue.createPointValue(10), UnitValue.createPointValue(10));
        size.setBackgroundSizeToCover();

        Assert.assertFalse(size.isContain());
        Assert.assertTrue(size.isCover());
        Assert.assertNull(size.getBackgroundWidthSize());
        Assert.assertNull(size.getBackgroundHeightSize());
    }

    @Test
    public void clearAndSetToContainTest() {
        final BackgroundSize size = new BackgroundSize();

        size.setBackgroundSizeToValues(UnitValue.createPointValue(10), UnitValue.createPointValue(10));
        size.setBackgroundSizeToContain();

        Assert.assertTrue(size.isContain());
        Assert.assertFalse(size.isCover());
        Assert.assertNull(size.getBackgroundWidthSize());
        Assert.assertNull(size.getBackgroundHeightSize());
    }
}
