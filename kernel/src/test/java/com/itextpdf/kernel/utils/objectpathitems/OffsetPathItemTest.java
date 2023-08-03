/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.utils.objectpathitems;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class OffsetPathItemTest extends ExtendedITextTest {

    @Test
    public void equalsAndHashCodeTest() {
        int offset = 1;
        OffsetPathItem offsetPathItem1 = new OffsetPathItem(offset);
        OffsetPathItem offsetPathItem2 = new OffsetPathItem(offset);

        boolean result = offsetPathItem1.equals(offsetPathItem2);
        Assert.assertTrue(result);
        Assert.assertEquals(offsetPathItem1.hashCode(), offsetPathItem2.hashCode());
    }

    @Test
    public void notEqualsAndHashCodeTest() {
        OffsetPathItem offsetPathItem1 = new OffsetPathItem(1);
        OffsetPathItem offsetPathItem2 = new OffsetPathItem(2);

        boolean result = offsetPathItem1.equals(offsetPathItem2);
        Assert.assertFalse(result);
        Assert.assertNotEquals(offsetPathItem1.hashCode(), offsetPathItem2.hashCode());
    }

    @Test
    public void getIndexTest() {
        int offset = 1;
        OffsetPathItem offsetPathItem = new OffsetPathItem(offset);

        Assert.assertEquals(offset, offsetPathItem.getOffset());
    }
}
