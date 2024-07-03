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
package com.itextpdf.kernel.utils.objectpathitems;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ArrayPathItemTest extends ExtendedITextTest {

    @Test
    public void equalsAndHashCodeTest() {
        int index = 1;
        ArrayPathItem arrayPathItem1 = new ArrayPathItem(index);
        ArrayPathItem arrayPathItem2 = new ArrayPathItem(index);

        boolean result = arrayPathItem1.equals(arrayPathItem2);
        Assertions.assertTrue(result);
        Assertions.assertEquals(arrayPathItem1.hashCode(), arrayPathItem2.hashCode());
    }

    @Test
    public void notEqualsAndHashCodeTest() {
        ArrayPathItem arrayPathItem1 = new ArrayPathItem(1);
        ArrayPathItem arrayPathItem2 = new ArrayPathItem(2);

        boolean result = arrayPathItem1.equals(arrayPathItem2);
        Assertions.assertFalse(result);
        Assertions.assertNotEquals(arrayPathItem1.hashCode(), arrayPathItem2.hashCode());
    }

    @Test
    public void getIndexTest() {
        int index = 1;
        ArrayPathItem arrayPathItem = new ArrayPathItem(index);

        Assertions.assertEquals(index, arrayPathItem.getIndex());
    }
}
