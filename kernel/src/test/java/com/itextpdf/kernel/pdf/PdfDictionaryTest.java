/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Category(UnitTest.class)
public class PdfDictionaryTest {

    @Test
    public void testEntrySet() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        List<Integer> nums = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        for (Map.Entry<PdfName, PdfObject> e: dict.entrySet()) {
            Assert.assertEquals(e.getKey().toString(), "/"+e.getValue());
            if (!nums.remove((Object)((PdfNumber)e.getValue()).intValue())) {
                Assert.fail("Element not found");
            }
        }
    }

    @Test
    public void testEntrySetContains() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        for (Map.Entry<PdfName, PdfObject> e: dict.entrySet()) {
            Assert.assertTrue(dict.entrySet().contains(e));
        }
    }

    @Test
    public void testEntrySetRemove() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        List<Map.Entry<PdfName, PdfObject>> toRemove = new ArrayList<>();
        for (Map.Entry<PdfName, PdfObject> e: dict.entrySet()) {
            toRemove.add(e);
        }
        for (Map.Entry<PdfName, PdfObject> e: toRemove) {
            Assert.assertTrue(dict.entrySet().remove(e));
        }
        Assert.assertEquals(0, dict.entrySet().size());
        Assert.assertEquals(0, dict.values().size());
        Assert.assertEquals(0, dict.size());
    }

    @Test
    public void testEntrySetRemove2() {
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

        for (Map.Entry<PdfName, PdfObject> e: dict2.entrySet()) {
            dict.entrySet().remove(e);
        }

        Assert.assertEquals(0, dict.entrySet().size());
        Assert.assertEquals(0, dict.values().size());
        Assert.assertEquals(0, dict.size());
    }

    @Test
    public void testEntrySetRemoveAll() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        List<Map.Entry<PdfName, PdfObject>> toRemove = new ArrayList<>();
        for (Map.Entry<PdfName, PdfObject> e: dict.entrySet()) {
            toRemove.add(e);
        }

        dict.entrySet().removeAll(toRemove);
        Assert.assertEquals(0, dict.entrySet().size());
        Assert.assertEquals(0, dict.values().size());
        Assert.assertEquals(0, dict.size());
    }

    @Test
    public void testEntrySetRemoveAll2() {
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

        dict.entrySet().removeAll(dict2.entrySet());
        Assert.assertEquals(0, dict.entrySet().size());
        Assert.assertEquals(0, dict.values().size());
        Assert.assertEquals(0, dict.size());
    }

    @Test
    public void testEntrySetRetainAll() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        List<Map.Entry<PdfName, PdfObject>> toRemove = new ArrayList<>();
        int i = 0;
        for (Map.Entry<PdfName, PdfObject> e: dict.entrySet()) {
            toRemove.add(e);
            if (i++ > 2) break;
        }

        dict.entrySet().retainAll(toRemove);
        Assert.assertEquals(4, dict.entrySet().size());
        Assert.assertEquals(4, dict.values().size());
        Assert.assertEquals(4, dict.size());
    }

    @Test
    public void testEntrySetClear() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        dict.entrySet().clear();
        Assert.assertEquals(0, dict.entrySet().size());
        Assert.assertEquals(0, dict.values().size());
        Assert.assertEquals(0, dict.size());
    }

    @Test
    public void testValues() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        List<Integer> nums = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        for (Map.Entry<PdfName, PdfObject> e: dict.entrySet()) {
            Assert.assertEquals(e.getKey().toString(), "/"+e.getValue());
            if (!nums.remove((Object)((PdfNumber)e.getValue()).intValue())) {
                Assert.fail("Element not found");
            }
        }
    }

    @Test
    public void testValuesContains() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        for (PdfObject v: dict.values()) {
            Assert.assertTrue(dict.values().contains(v));
        }
    }

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
    public void testValuesIndirectContains() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict = new PdfDictionary();
        dict.put(new PdfName("1"), new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("2"), new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        dict.put(new PdfName("3"), new PdfNumber(3).makeIndirect(doc));
        dict.put(new PdfName("4"), new PdfNumber(4).makeIndirect(doc));
        dict.put(new PdfName("5"), new PdfNumber(5));
        dict.put(new PdfName("6"), new PdfNumber(6));

        Assert.assertTrue(dict.values().contains(dict.get(new PdfName("1"), false)));
        Assert.assertTrue(dict.values().contains(dict.get(new PdfName("2"), false)));
        Assert.assertTrue(dict.values().contains(dict.get(new PdfName("3")).getIndirectReference()));
        Assert.assertTrue(dict.values().contains(dict.get(new PdfName("4")).getIndirectReference()));
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
