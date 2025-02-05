/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.Stack;

@Tag("IntegrationTest")
public class ObjectPathTest extends ExtendedITextTest {

    private PdfDocument testCmp;
    private PdfDocument testOut;

    @BeforeEach
    public void setUpPdfDocuments() {
        testCmp = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        testCmp.addNewPage();
        testCmp.addNewPage();

        testOut = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        testOut.addNewPage();
        testOut.addNewPage();
    }

    @AfterEach
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

        Assertions.assertEquals(cmpIndirect, objectPath.getBaseCmpObject());
        Assertions.assertEquals(outIndirect, objectPath.getBaseOutObject());
        Assertions.assertEquals(localPath, objectPath.getLocalPath());
        Assertions.assertEquals(indirectPathItems, objectPath.getIndirectPath());
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

        Assertions.assertNotEquals(0, objectPath1.hashCode());
        Assertions.assertEquals(objectPath1.hashCode(), objectPath2.hashCode());
    }

    @Test
    public void hashCodeWithNullParametersTest() {
        Stack<LocalPathItem> localPath = new Stack<>();
        Stack<IndirectPathItem> indirectPathItems = new Stack<>();

        ObjectPath objectPath1 = new ObjectPath(null, null, localPath, indirectPathItems);
        ObjectPath objectPath2 = new ObjectPath(null, null, localPath, indirectPathItems);

        Assertions.assertEquals(0, objectPath1.hashCode());
        Assertions.assertEquals(objectPath1.hashCode(), objectPath2.hashCode());
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
        Assertions.assertTrue(result);
        Assertions.assertEquals(objectPath1.hashCode(), objectPath2.hashCode());
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
        Assertions.assertFalse(result);
        Assertions.assertNotEquals(objectPath1.hashCode(), objectPath2.hashCode());
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
        Assertions.assertTrue(result);
        Assertions.assertEquals(objectPath1.hashCode(), objectPath2.hashCode());
    }
}
