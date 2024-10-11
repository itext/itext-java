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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class BackgroundSizeTest extends ExtendedITextTest {

    @Test
    public void constructorTest() {
        final BackgroundSize size = new BackgroundSize();

        Assertions.assertFalse(size.isContain());
        Assertions.assertFalse(size.isCover());
        Assertions.assertNull(size.getBackgroundWidthSize());
        Assertions.assertNull(size.getBackgroundHeightSize());
    }

    @Test
    public void clearAndSetToCoverTest() {
        final BackgroundSize size = new BackgroundSize();

        size.setBackgroundSizeToValues(UnitValue.createPointValue(10), UnitValue.createPointValue(10));
        size.setBackgroundSizeToCover();

        Assertions.assertFalse(size.isContain());
        Assertions.assertTrue(size.isCover());
        Assertions.assertNull(size.getBackgroundWidthSize());
        Assertions.assertNull(size.getBackgroundHeightSize());
    }

    @Test
    public void clearAndSetToContainTest() {
        final BackgroundSize size = new BackgroundSize();

        size.setBackgroundSizeToValues(UnitValue.createPointValue(10), UnitValue.createPointValue(10));
        size.setBackgroundSizeToContain();

        Assertions.assertTrue(size.isContain());
        Assertions.assertFalse(size.isCover());
        Assertions.assertNull(size.getBackgroundWidthSize());
        Assertions.assertNull(size.getBackgroundHeightSize());
    }
}
