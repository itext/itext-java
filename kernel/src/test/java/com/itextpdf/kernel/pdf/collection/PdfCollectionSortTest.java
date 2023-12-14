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
package com.itextpdf.kernel.pdf.collection;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfCollectionSortTest extends ExtendedITextTest {

    @Test
    public void oneKeyConstructorTest() {
        final String key = "testKey";
        PdfCollectionSort sort = new PdfCollectionSort(key);

        Assert.assertEquals(key, sort.getPdfObject().getAsName(PdfName.S).getValue());
    }

    @Test
    public void multipleKeysConstructorTest() {
        final String[] keys = { "testKey1", "testKey2", "testKey3"};
        PdfCollectionSort sort = new PdfCollectionSort(keys);

        for(int i = 0; i < keys.length; i++) {
            Assert.assertEquals(keys[i], sort.getPdfObject().getAsArray(PdfName.S).getAsName(i).getValue());
        }
    }

    @Test
    public void sortOrderForOneKeyTest() {
        final String key = "testKey";
        final boolean testAscending = true;
        PdfCollectionSort sort = new PdfCollectionSort(key);

        Assert.assertNull(sort.getPdfObject().get(PdfName.A));

        sort.setSortOrder(testAscending);

        Assert.assertTrue(sort.getPdfObject().getAsBool(PdfName.A));
    }

    @Test
    public void incorrectSortOrderForOneKeyTest() {
        final String key = "testKey";
        final boolean[] testAscendings = {true, false};
        PdfCollectionSort sort = new PdfCollectionSort(key);

        // this line will throw an exception as number of parameters of setSortOrder()
        // method should be exactly the same as number of keys of PdfCollectionSort
        // here we have one key but two params
        Exception e = Assert.assertThrows(PdfException.class,
                () -> sort.setSortOrder(testAscendings)
        );
        Assert.assertEquals(PdfException.YouNeedASingleBooleanForThisCollectionSortDictionary, e.getMessage());
    }

    @Test
    public void sortOrderForMultipleKeysTest() {
        final String[] keys = { "testKey1", "testKey2", "testKey3"};
        final boolean[] testAscendings = {true, false, true};

        PdfCollectionSort sort = new PdfCollectionSort(keys);

        sort.setSortOrder(testAscendings);

        for(int i = 0; i < testAscendings.length; i++) {
            Assert.assertEquals(testAscendings[i], sort.getPdfObject().getAsArray(PdfName.A).getAsBoolean(i).getValue());
        }
    }

    @Test
    public void singleSortOrderForMultipleKeysTest() {
        final String[] keys = { "testKey1", "testKey2", "testKey3"};
        final boolean testAscending = true;

        PdfCollectionSort sort = new PdfCollectionSort(keys);

        // this line will throw an exception as number of parameters of setSortOrder()
        // method should be exactly the same as number of keys of PdfCollectionSort
        // here we have three keys but one param
        Exception e = Assert.assertThrows(PdfException.class,
                () -> sort.setSortOrder(testAscending)
        );
        Assert.assertEquals(PdfException.YouHaveToDefineABooleanArrayForThisCollectionSortDictionary, e.getMessage());
    }

    @Test
    public void incorrectMultipleSortOrderForMultipleKeysTest() {
        final String[] keys = { "testKey1", "testKey2", "testKey3"};
        final boolean[] testAscendings = {true, false};

        PdfCollectionSort sort = new PdfCollectionSort(keys);


        // this line will throw an exception as number of parameters of setSortOrder()
        // method should be exactly the same as number of keys of PdfCollectionSort
        // here we have three keys but two params
        Exception e = Assert.assertThrows(PdfException.class,
                () -> sort.setSortOrder(testAscendings)
        );
        Assert.assertEquals(PdfException.NumberOfBooleansInTheArrayDoesntCorrespondWithTheNumberOfFields, e.getMessage());
    }

    @Test
    public void isWrappedObjectMustBeIndirectTest() {
        final String key = "testKey";
        Assert.assertFalse(new PdfCollectionSort(key).isWrappedObjectMustBeIndirect());
    }
}
