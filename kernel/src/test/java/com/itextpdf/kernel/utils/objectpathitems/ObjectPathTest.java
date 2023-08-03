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
package com.itextpdf.kernel.utils.objectpathitems;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Stack;

@Category(IntegrationTest.class)
public class ObjectPathTest extends ExtendedITextTest {

    private PdfDocument testCmp;
    private PdfDocument testOut;

    @Before
    public void setUpPdfDocuments() {
        testCmp = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        testCmp.addNewPage();
        testCmp.addNewPage();

        testOut = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        testOut.addNewPage();
        testOut.addNewPage();
    }

    @After
    public void closePdfDocuments() {
        testCmp.close();
        testOut.close();
    }

    @Test
    public void getIndirectObjectsTest() {
        PdfIndirectReference cmpIndirect = testCmp.getFirstPage().getPdfObject().getIndirectReference();
        PdfIndirectReference outIndirect = testOut.getFirstPage().getPdfObject().getIndirectReference();
        Stack<LocalPathItem> localPath = new Stack<>();
        localPath.push(new ArrayPathItem(1));
        Stack<IndirectPathItem> indirectPathItems = new Stack<>();
        indirectPathItems.push(new IndirectPathItem(cmpIndirect, outIndirect));

        ObjectPath objectPath = new ObjectPath(cmpIndirect, outIndirect, localPath, indirectPathItems);

        Assert.assertEquals(cmpIndirect, objectPath.getBaseCmpObject());
        Assert.assertEquals(outIndirect, objectPath.getBaseOutObject());
        Assert.assertEquals(localPath, objectPath.getLocalPath());
        Assert.assertEquals(indirectPathItems, objectPath.getIndirectPath());
    }

    @Test
    public void hashCodeWithoutNullParametersTest() {
        PdfIndirectReference cmpIndirect = testCmp.getFirstPage().getPdfObject().getIndirectReference();
        PdfIndirectReference outIndirect = testOut.getFirstPage().getPdfObject().getIndirectReference();
        Stack<LocalPathItem> localPath = new Stack<>();
        localPath.push(new ArrayPathItem(1));
        Stack<IndirectPathItem> indirectPathItems = new Stack<>();
        indirectPathItems.push(new IndirectPathItem(cmpIndirect, outIndirect));

        ObjectPath objectPath1 = new ObjectPath(cmpIndirect, outIndirect, localPath, indirectPathItems);
        ObjectPath objectPath2 = new ObjectPath(cmpIndirect, outIndirect, localPath, indirectPathItems);

        Assert.assertNotEquals(0, objectPath1.hashCode());
        Assert.assertEquals(objectPath1.hashCode(), objectPath2.hashCode());
    }

    @Test
    public void hashCodeWithNullParametersTest() {
        Stack<LocalPathItem> localPath = new Stack<>();
        Stack<IndirectPathItem> indirectPathItems = new Stack<>();

        ObjectPath objectPath1 = new ObjectPath(null, null, localPath, indirectPathItems);
        ObjectPath objectPath2 = new ObjectPath(null, null, localPath, indirectPathItems);

        Assert.assertEquals(0, objectPath1.hashCode());
        Assert.assertEquals(objectPath1.hashCode(), objectPath2.hashCode());
    }

    @Test
    public void equalsAndHashCodeTest() {
        PdfIndirectReference cmpIndirect = testCmp.getFirstPage().getPdfObject().getIndirectReference();
        PdfIndirectReference outIndirect = testOut.getFirstPage().getPdfObject().getIndirectReference();
        Stack<LocalPathItem> localPath = new Stack<>();
        localPath.push(new ArrayPathItem(1));
        localPath.push(new ArrayPathItem(2));
        localPath.push(new ArrayPathItem(3));
        Stack<IndirectPathItem> indirectPathItems = new Stack<>();
        indirectPathItems.push(new IndirectPathItem(cmpIndirect, outIndirect));

        ObjectPath objectPath1 = new ObjectPath(cmpIndirect, outIndirect, localPath, indirectPathItems);
        ObjectPath objectPath2 = new ObjectPath(cmpIndirect, outIndirect, localPath, indirectPathItems);

        boolean result = objectPath1.equals(objectPath2);
        Assert.assertTrue(result);
        Assert.assertEquals(objectPath1.hashCode(), objectPath2.hashCode());
    }

    @Test
    public void notEqualsAndHashCodeTest() {
        PdfIndirectReference cmpIndirect = testCmp.getFirstPage().getPdfObject().getIndirectReference();
        PdfIndirectReference outIndirect = testOut.getFirstPage().getPdfObject().getIndirectReference();
        Stack<LocalPathItem> localPath = new Stack<>();
        localPath.push(new ArrayPathItem(1));
        Stack<IndirectPathItem> indirectPathItems = new Stack<>();
        indirectPathItems.push(new IndirectPathItem(cmpIndirect, outIndirect));

        ObjectPath objectPath1 = new ObjectPath(cmpIndirect, outIndirect, localPath, indirectPathItems);

        localPath = new Stack<>();
        indirectPathItems = new Stack<>();
        ObjectPath objectPath2 = new ObjectPath(cmpIndirect, outIndirect, localPath, indirectPathItems);

        boolean result = objectPath1.equals(objectPath2);
        Assert.assertFalse(result);
        Assert.assertNotEquals(objectPath1.hashCode(), objectPath2.hashCode());
    }

    @Test
    public void cloneConstructorTest() {
        PdfIndirectReference cmpIndirect = testCmp.getFirstPage().getPdfObject().getIndirectReference();
        PdfIndirectReference outIndirect = testOut.getFirstPage().getPdfObject().getIndirectReference();
        Stack<LocalPathItem> localPath = new Stack<>();
        localPath.push(new ArrayPathItem(1));
        Stack<IndirectPathItem> indirectPathItems = new Stack<>();
        indirectPathItems.push(new IndirectPathItem(cmpIndirect, outIndirect));

        ObjectPath objectPath1 = new ObjectPath(cmpIndirect, outIndirect, localPath, indirectPathItems);

        ObjectPath objectPath2 = new ObjectPath(objectPath1);

        boolean result = objectPath1.equals(objectPath2);
        Assert.assertTrue(result);
        Assert.assertEquals(objectPath1.hashCode(), objectPath2.hashCode());
    }
}
