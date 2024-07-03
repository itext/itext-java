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

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class DictPathItemTest extends ExtendedITextTest {

    @Test
    public void equalsAndHashCodeTest() {
        PdfName name = new PdfName("test");
        DictPathItem dictPathItem1 = new DictPathItem(name);
        DictPathItem dictPathItem2 = new DictPathItem(name);

        boolean result = dictPathItem1.equals(dictPathItem2);
        Assertions.assertTrue(result);
        Assertions.assertEquals(dictPathItem1.hashCode(), dictPathItem2.hashCode());
    }

    @Test
    public void notEqualsAndHashCodeTest() {
        DictPathItem dictPathItem1 = new DictPathItem(new PdfName("test"));
        DictPathItem dictPathItem2 = new DictPathItem(new PdfName("test2"));

        boolean result = dictPathItem1.equals(dictPathItem2);
        Assertions.assertFalse(result);
        Assertions.assertNotEquals(dictPathItem1.hashCode(), dictPathItem2.hashCode());
    }

    @Test
    public void getKeyTest() {
        PdfName name = new PdfName("test");
        DictPathItem dictPathItem = new DictPathItem(name);

        Assertions.assertEquals(name, dictPathItem.getKey());
    }
}
