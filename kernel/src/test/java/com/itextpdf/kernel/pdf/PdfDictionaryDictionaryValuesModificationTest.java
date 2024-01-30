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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfDictionaryDictionaryValuesModificationTest extends ExtendedITextTest {

    @Test
    public void testValuesRemove() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        List<PdfObject> toRemove = new ArrayList<>();
        for (PdfObject v: dict.values()) {
            toRemove.add(v);
        }
        for (PdfObject v: toRemove) {
            Assert.assertTrue(dict.values().remove(v));
        }
        Assert.assertEquals(0, dict.entrySet().size());
        Assert.assertEquals(0, dict.values().size());
        Assert.assertEquals(0, dict.size());
    }

    @Test
    public void testValuesIndirectRemove() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        Assert.assertTrue(dict.values().remove(dict.get(new PdfName("1"), false)));
        Assert.assertTrue(dict.values().remove(dict.get(new PdfName("2"), false)));
        Assert.assertTrue(dict.values().remove(dict.get(new PdfName("3")).getIndirectReference()));
        Assert.assertTrue(dict.values().remove(dict.get(new PdfName("4")).getIndirectReference()));

        Assert.assertEquals(2, dict.entrySet().size());
        Assert.assertEquals(2, dict.values().size());
        Assert.assertEquals(2, dict.size());
    }

    @Test
    public void testValuesRemove2() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        PdfDictionary dict2 = new PdfDictionary();
        dict2.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict2.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict2.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict2.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict2.put(new PdfName("5"), new PdfNumber(5));
        dict2.put(new PdfName("6"), new PdfNumber(6));

        for (PdfObject v: dict2.values()) {
            dict.values().remove(v);
        }
        Assert.assertEquals(0, dict.entrySet().size());
        Assert.assertEquals(0, dict.values().size());
        Assert.assertEquals(0, dict.size());

    }

    @Test
    public void testValuesRemoveAll() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        List<PdfObject> toRemove = new ArrayList<>();
        for (PdfObject v: dict.values()) {
            toRemove.add(v);
        }

        dict.values().removeAll(toRemove);
        Assert.assertEquals(0, dict.entrySet().size());
        Assert.assertEquals(0, dict.values().size());
        Assert.assertEquals(0, dict.size());

    }

    @Test
    public void testValuesRemoveAll2() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        PdfDictionary dict2 = new PdfDictionary();
        dict2.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict2.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict2.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict2.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict2.put(new PdfName("5"), new PdfNumber(5));
        dict2.put(new PdfName("6"), new PdfNumber(6));

        dict.values().removeAll(dict2.values());

        Assert.assertEquals(0, dict.entrySet().size());
        Assert.assertEquals(0, dict.values().size());
        Assert.assertEquals(0, dict.size());

    }

    @Test
    public void testValuesRetainAll() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        List<PdfObject> toRemove = new ArrayList<>();
        int i = 0;
        for (PdfObject v: dict.values()) {
            toRemove.add(v);
            if (i++ > 2) break;
        }

        dict.values().retainAll(toRemove);
        Assert.assertEquals(4, dict.entrySet().size());
        Assert.assertEquals(4, dict.values().size());
        Assert.assertEquals(4, dict.size());

    }

    @Test
    public void testValuesClear() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        dict.values().clear();
        Assert.assertEquals(0, dict.entrySet().size());
        Assert.assertEquals(0, dict.values().size());
        Assert.assertEquals(0, dict.size());
    }
}
