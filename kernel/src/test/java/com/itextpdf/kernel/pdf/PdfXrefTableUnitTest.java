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
package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfXrefTableUnitTest extends ExtendedITextTest {
    @Test
    public void checkNumberOfIndirectObjectsTest() {
        PdfXrefTable table = new PdfXrefTable();
        Assert.assertEquals(0, table.getCountOfIndirectObjects());

        int numberOfReferences = 10;

        for (int i = 0; i < numberOfReferences; i++) {
            table.add(new PdfIndirectReference(null, i + 1));
        }

        Assert.assertEquals(numberOfReferences, table.getCountOfIndirectObjects());
    }

    @Test
    public void checkNumberOfIndirectObjectsWithFreeReferencesTest() {
        PdfXrefTable table = new PdfXrefTable();

        int numberOfReferences = 10;

        for (int i = 0; i < numberOfReferences; i++) {
            table.add(new PdfIndirectReference(null, i + 1));
        }

        table.initFreeReferencesList(null);

        int freeReferenceNumber = 5;
        table.freeReference(table.get(freeReferenceNumber));

        Assert.assertEquals(numberOfReferences - 1, table.getCountOfIndirectObjects());
        Assert.assertTrue(table.get(freeReferenceNumber).isFree());
    }

    @Test
    public void checkNumberOfIndirectObjectsWithRandomNumbersTest() {
        PdfXrefTable table = new PdfXrefTable();

        int numberOfReferences = 10;

        for (int i = 0; i < numberOfReferences; i++) {
            table.add(new PdfIndirectReference(null, i * 25));
        }

        Assert.assertEquals(numberOfReferences, table.getCountOfIndirectObjects());
        Assert.assertEquals(226, table.size());
    }
}
