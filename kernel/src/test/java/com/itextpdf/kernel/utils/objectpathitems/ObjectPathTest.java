/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
