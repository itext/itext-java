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
package com.itextpdf.io.util;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class IntHashtableTest extends ExtendedITextTest {

    @Test
    public void cloneTest() throws CloneNotSupportedException {
        IntHashtable hashtable = fillTable();
        IntHashtable clonedTable = (IntHashtable) hashtable.clone();
        int[] keysArray = hashtable.getKeys();
        int[] clonedKeysArray = clonedTable.getKeys();
        Assertions.assertEquals(keysArray.length, clonedKeysArray.length);
        for (int i = 0; i < keysArray.length; i++) {
            Assertions.assertEquals(keysArray[i], clonedKeysArray[i]);
            Assertions.assertEquals(hashtable.get(keysArray[i]), clonedTable.get(clonedKeysArray[i]));
        }
    }

    @Test
    public void countIsEqualTest() throws CloneNotSupportedException {
        IntHashtable hashtable = fillTable();
        IntHashtable clonedTable = (IntHashtable) hashtable.clone();
        Assertions.assertEquals(hashtable.count, clonedTable.count);
    }

    private static IntHashtable fillTable() {
        IntHashtable hashtable = new IntHashtable();
        hashtable.put(1, 0);
        hashtable.put(0, 1);
        hashtable.put(-1, 2);
        hashtable.put(2, -1);
        return hashtable;
    }
}
