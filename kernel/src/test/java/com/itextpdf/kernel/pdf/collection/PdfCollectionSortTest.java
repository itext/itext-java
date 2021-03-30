/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.collection;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfCollectionSortTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

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
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.YouNeedASingleBooleanForThisCollectionSortDictionary);

        final String key = "testKey";
        final boolean[] testAscendings = {true, false};
        PdfCollectionSort sort = new PdfCollectionSort(key);

        sort.setSortOrder(testAscendings);

        // this line will throw an exception as number of parameters of setSortOrder()
        // method should be exactly the same as number of keys of PdfCollectionSort
        // here we have one key but two params
        Assert.assertTrue(sort.getPdfObject().getAsBool(PdfName.A));
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
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.YouHaveToDefineABooleanArrayForThisCollectionSortDictionary);

        final String[] keys = { "testKey1", "testKey2", "testKey3"};
        final boolean testAscending = true;

        PdfCollectionSort sort = new PdfCollectionSort(keys);

        // this line will throw an exception as number of parameters of setSortOrder()
        // method should be exactly the same as number of keys of PdfCollectionSort
        // here we have three keys but one param
        sort.setSortOrder(testAscending);
    }

    @Test
    public void incorrectMultipleSortOrderForMultipleKeysTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.NumberOfBooleansInTheArrayDoesntCorrespondWithTheNumberOfFields);

        final String[] keys = { "testKey1", "testKey2", "testKey3"};
        final boolean[] testAscendings = {true, false};

        PdfCollectionSort sort = new PdfCollectionSort(keys);


        // this line will throw an exception as number of parameters of setSortOrder()
        // method should be exactly the same as number of keys of PdfCollectionSort
        // here we have three keys but two params
        sort.setSortOrder(testAscendings);
    }

    @Test
    public void isWrappedObjectMustBeIndirectTest() {
        final String key = "testKey";
        Assert.assertFalse(new PdfCollectionSort(key).isWrappedObjectMustBeIndirect());
    }
}
