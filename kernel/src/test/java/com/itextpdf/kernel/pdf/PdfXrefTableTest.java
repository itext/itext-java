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

import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfXrefTableTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfXrefTableTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfXrefTableTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterClass
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void testCreateAndUpdateXMP() throws IOException {
        String created = destinationFolder + "testCreateAndUpdateXMP_create.pdf";
        String updated = destinationFolder + "testCreateAndUpdateXMP_update.pdf";
        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(created));
        pdfDocument.addNewPage();

        // create XMP metadata
        pdfDocument.getXmpMetadata(true);
        pdfDocument.close();


        pdfDocument = new PdfDocument(CompareTool.createOutputReader(created), CompareTool.createTestPdfWriter(updated));
        PdfXrefTable xref = pdfDocument.getXref();

        PdfDictionary catalog = pdfDocument.getCatalog().getPdfObject();
        ((PdfIndirectReference)catalog.remove(PdfName.Metadata)).setFree();

        PdfIndirectReference ref0 = xref.get(0);
        PdfIndirectReference freeRef = xref.get(6);
        pdfDocument.close();

        /*
        Current xref structure:
        xref
        0 8
        0000000006 65535 f % this is object 0; 6 refers to free object 6
        0000000203 00000 n
        0000000510 00000 n
        0000000263 00000 n
        0000000088 00000 n
        0000000015 00000 n
        0000000000 00001 f % this is object 6; 0 refers to free object 0; note generation number
        0000000561 00000 n
        */

        Assert.assertTrue(freeRef.isFree());
        Assert.assertEquals(ref0.offsetOrIndex, freeRef.objNr);
        Assert.assertEquals(1, freeRef.genNr);
    }

    @Test
    public void testCreateAndUpdateTwiceXMP() throws IOException {
        String created = destinationFolder + "testCreateAndUpdateTwiceXMP_create.pdf";
        String updated = destinationFolder + "testCreateAndUpdateTwiceXMP_update.pdf";
        String updatedAgain = destinationFolder + "testCreateAndUpdateTwiceXMP_updatedAgain.pdf";
        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(created));
        pdfDocument.addNewPage();

        // create XMP metadata
        pdfDocument.getXmpMetadata(true);
        pdfDocument.close();


        pdfDocument = new PdfDocument(CompareTool.createOutputReader(created), CompareTool.createTestPdfWriter(updated));

        PdfDictionary catalog = pdfDocument.getCatalog().getPdfObject();
        ((PdfIndirectReference)catalog.remove(PdfName.Metadata)).setFree();

        pdfDocument.close();


        pdfDocument = new PdfDocument(CompareTool.createOutputReader(updated), CompareTool.createTestPdfWriter(updatedAgain));

        catalog = pdfDocument.getCatalog().getPdfObject();
        ((PdfIndirectReference)catalog.remove(PdfName.Metadata)).setFree();

        PdfXrefTable xref = pdfDocument.getXref();
        PdfIndirectReference ref0 = xref.get(0);
        PdfIndirectReference freeRef1 = xref.get(6);
        PdfIndirectReference freeRef2 = xref.get(7);

        pdfDocument.close();

        /*
        Current xref structure:
        xref
        0 9
        0000000006 65535 f % this is object 0; 6 refers to free object 6
        0000000203 00000 n
        0000000510 00000 n
        0000000263 00000 n
        0000000088 00000 n
        0000000015 00000 n
        0000000007 00001 f % this is object 6; 7 refers to free object 7; note generation number
        0000000000 00001 f % this is object 7; 0 refers to free object 0; note generation number
        0000000561 00000 n
        */

        Assert.assertTrue(freeRef1.isFree());
        Assert.assertEquals(ref0.offsetOrIndex, freeRef1.objNr);
        Assert.assertEquals(1, freeRef1.genNr);
        Assert.assertTrue(freeRef2.isFree());
        Assert.assertEquals(freeRef1.offsetOrIndex, freeRef2.objNr);
        Assert.assertEquals(1, freeRef2.genNr);
        pdfDocument.close();
    }
}
