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

@Tag("IntegrationTest")
public class IndirectPathItemTest extends ExtendedITextTest {

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

        IndirectPathItem indirectPathItem = new IndirectPathItem(cmpIndirect, outIndirect);

        Assertions.assertEquals(cmpIndirect, indirectPathItem.getCmpObject());
        Assertions.assertEquals(outIndirect, indirectPathItem.getOutObject());
    }

    @Test
    public void equalsAndHashCodeTest() {
        PdfIndirectReference cmpIndirect = testCmp.getFirstPage().getPdfObject().getIndirectReference();
        PdfIndirectReference outIndirect = testOut.getFirstPage().getPdfObject().getIndirectReference();

        IndirectPathItem indirectPathItem1 = new IndirectPathItem(cmpIndirect, outIndirect);
        IndirectPathItem indirectPathItem2 = new IndirectPathItem(cmpIndirect, outIndirect);

        boolean result = indirectPathItem1.equals(indirectPathItem2);
        Assertions.assertTrue(result);
        Assertions.assertEquals(indirectPathItem1.hashCode(), indirectPathItem2.hashCode());
    }

    @Test
    public void notEqualsCmpObjAndHashCodeTest() {
        PdfIndirectReference cmpIndirect1 = testCmp.getFirstPage().getPdfObject().getIndirectReference();
        PdfIndirectReference outIndirect1 = testOut.getFirstPage().getPdfObject().getIndirectReference();
        IndirectPathItem indirectPathItem1 = new IndirectPathItem(cmpIndirect1, outIndirect1);

        PdfIndirectReference cmpIndirect2 = testCmp.getPage(2).getPdfObject().getIndirectReference();
        PdfIndirectReference outIndirect2 = testOut.getFirstPage().getPdfObject().getIndirectReference();
        IndirectPathItem indirectPathItem2 = new IndirectPathItem(cmpIndirect2, outIndirect2);

        boolean result = indirectPathItem1.equals(indirectPathItem2);
        Assertions.assertFalse(result);
        Assertions.assertNotEquals(indirectPathItem1.hashCode(), indirectPathItem2.hashCode());
    }

    @Test
    public void notEqualsOutObjAndHashCodeTest() {
        PdfIndirectReference cmpIndirect1 = testCmp.getFirstPage().getPdfObject().getIndirectReference();
        PdfIndirectReference outIndirect1 = testOut.getFirstPage().getPdfObject().getIndirectReference();
        IndirectPathItem indirectPathItem1 = new IndirectPathItem(cmpIndirect1, outIndirect1);

        PdfIndirectReference cmpIndirect2 = testCmp.getFirstPage().getPdfObject().getIndirectReference();
        PdfIndirectReference outIndirect2 = testOut.getPage(2).getPdfObject().getIndirectReference();
        IndirectPathItem indirectPathItem2 = new IndirectPathItem(cmpIndirect2, outIndirect2);

        boolean result = indirectPathItem1.equals(indirectPathItem2);
        Assertions.assertFalse(result);
        Assertions.assertNotEquals(indirectPathItem1.hashCode(), indirectPathItem2.hashCode());
    }
}
