/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Category(UnitTest.class)
public class PdfArrayTest {

    @Test
    public void testValuesIndirectContains() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(0).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc));
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4));
        array.add(new PdfNumber(5));

        Assert.assertTrue(array.contains(array.get(0, false)));
        Assert.assertTrue(array.contains(array.get(1, false)));
        Assert.assertTrue(array.contains(array.get(2).getIndirectReference()));
        Assert.assertTrue(array.contains(array.get(3).getIndirectReference()));
        Assert.assertTrue(array.contains(array.get(4)));
        Assert.assertTrue(array.contains(array.get(5)));
    }

    @Test
    public void testValuesIndirectRemove() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4).makeIndirect(doc));
        array.add(new PdfNumber(5));
        array.add(new PdfNumber(6));

        array.remove(array.get(0, false));
        array.remove(array.get(0, false));
        array.remove(array.get(0).getIndirectReference());
        array.remove(array.get(0).getIndirectReference());
        array.remove(array.get(0));
        array.remove(array.get(0));

        Assert.assertEquals(0, array.size());
    }

    @Test
    public void testContains() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4).makeIndirect(doc));
        array.add(new PdfNumber(5));
        array.add(new PdfNumber(6));

        PdfArray array2 = new PdfArray();
        array2.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(3).makeIndirect(doc));
        array2.add(new PdfNumber(4).makeIndirect(doc));
        array2.add(new PdfNumber(5));
        array2.add(new PdfNumber(6));

        for (PdfObject obj : array2) {
            Assert.assertTrue(array.contains(obj));
        }

        for (int i = 0; i < array2.size(); i++) {
            Assert.assertTrue(array.contains(array2.get(i)));
        }
    }

    @Test
    public void testRemove() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4).makeIndirect(doc));
        array.add(new PdfNumber(5));
        array.add(new PdfNumber(6));

        PdfArray array2 = new PdfArray();
        array2.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(3).makeIndirect(doc));
        array2.add(new PdfNumber(4).makeIndirect(doc));
        array2.add(new PdfNumber(5));
        array2.add(new PdfNumber(6));

        for (PdfObject obj : array2) {
            array.remove(obj);
        }

        Assert.assertEquals(0, array.size());
    }

    @Test
    public void testRemove2() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4).makeIndirect(doc));
        array.add(new PdfNumber(5));
        array.add(new PdfNumber(6));

        PdfArray array2 = new PdfArray();
        array2.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(3).makeIndirect(doc));
        array2.add(new PdfNumber(4).makeIndirect(doc));
        array2.add(new PdfNumber(5));
        array2.add(new PdfNumber(6));

        for (int i = 0; i < array2.size(); i++) {
            array.remove(array2.get(i));
        }

        Assert.assertEquals(0, array.size());
    }
    @Test
    public void testIndexOf() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4).makeIndirect(doc));
        array.add(new PdfNumber(5));
        array.add(new PdfNumber(6));

        PdfArray array2 = new PdfArray();
        array2.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(3).makeIndirect(doc));
        array2.add(new PdfNumber(4).makeIndirect(doc));
        array2.add(new PdfNumber(5));
        array2.add(new PdfNumber(6));

        int i = 0;
        for (PdfObject obj : array2) {
            Assert.assertEquals(i++, array.indexOf(obj));
        }
    }

    @Test
    public void testIndexOf2() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4).makeIndirect(doc));
        array.add(new PdfNumber(5));
        array.add(new PdfNumber(6));

        PdfArray array2 = new PdfArray();
        array2.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(3).makeIndirect(doc));
        array2.add(new PdfNumber(4).makeIndirect(doc));
        array2.add(new PdfNumber(5));
        array2.add(new PdfNumber(6));

        for (int i = 0; i < array2.size(); i++) {
            Assert.assertEquals(i, array.indexOf(array2.get(i)));
        }
    }
}
