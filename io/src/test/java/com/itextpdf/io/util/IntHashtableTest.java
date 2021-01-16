package com.itextpdf.io.util;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class IntHashtableTest extends ExtendedITextTest {

    @Test
    public void cloneTest() throws CloneNotSupportedException {
        IntHashtable hashtable = fillTable();
        IntHashtable clonedTable = (IntHashtable) hashtable.clone();
        int[] keysArray = hashtable.getKeys();
        int[] clonedKeysArray = clonedTable.getKeys();
        Assert.assertEquals(keysArray.length, clonedKeysArray.length);
        for (int i = 0; i < keysArray.length; i++) {
            Assert.assertEquals(keysArray[i], clonedKeysArray[i]);
            Assert.assertEquals(hashtable.get(keysArray[i]), clonedTable.get(clonedKeysArray[i]));
        }
    }

    @Test
    public void countIsEqualTest() throws CloneNotSupportedException {
        IntHashtable hashtable = fillTable();
        IntHashtable clonedTable = (IntHashtable) hashtable.clone();
        Assert.assertEquals(hashtable.count, clonedTable.count);
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
