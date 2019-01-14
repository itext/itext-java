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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 *
 * @author benoit
 */
@Category(IntegrationTest.class)
public class PdfXrefTableTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfXrefTableTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfXrefTableTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void testCreateAndUpdateXMP() throws IOException {
        String created = destinationFolder + "testCreateAndUpdateXMP_create.pdf";
        String updated = destinationFolder + "testCreateAndUpdateXMP_update.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(created));
        pdfDocument.addNewPage();

        pdfDocument.getXmpMetadata(true); // create XMP metadata
        pdfDocument.close();


        pdfDocument = new PdfDocument(new PdfReader(created), new PdfWriter(updated));
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
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(created));
        pdfDocument.addNewPage();

        pdfDocument.getXmpMetadata(true); // create XMP metadata
        pdfDocument.close();


        pdfDocument = new PdfDocument(new PdfReader(created), new PdfWriter(updated));

        PdfDictionary catalog = pdfDocument.getCatalog().getPdfObject();
        ((PdfIndirectReference)catalog.remove(PdfName.Metadata)).setFree();

        pdfDocument.close();


        pdfDocument = new PdfDocument(new PdfReader(updated), new PdfWriter(updatedAgain));

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
