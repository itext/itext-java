/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ExtendedITextTest;
import static com.itextpdf.test.ITextTest.createOrClearDestinationFolder;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import junit.framework.Assert;

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
        pdfDocument.close();
        pdfDocument = new PdfDocument(new PdfReader(updated));
        PdfXrefTable xref = pdfDocument.getXref();
        PdfIndirectReference freeRef = xref.get(xref.size() - 2); // 6

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
        Assert.assertEquals(xref.get(0).offsetOrIndex, freeRef.objNr);
        Assert.assertEquals(1, freeRef.genNr);
        pdfDocument.close();
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
        pdfDocument.close();
        pdfDocument = new PdfDocument(new PdfReader(updated), new PdfWriter(updatedAgain));
        pdfDocument.close();
        pdfDocument = new PdfDocument(new PdfReader(updatedAgain));
        PdfXrefTable xref = pdfDocument.getXref();
        PdfIndirectReference freeRef1 = xref.get(xref.size() - 3); // 6
        PdfIndirectReference freeRef2 = xref.get(xref.size() - 2); // 7
        
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
        0000000007 00002 f % this is object 6; 7 refers to free object 7; note generation number
        0000000000 00001 f % this is object 7; 0 refers to free object 0; note generation number
        0000000561 00000 n
        */

        Assert.assertTrue(freeRef1.isFree());
        Assert.assertEquals(xref.get(0).offsetOrIndex, freeRef1.objNr);
        Assert.assertEquals(2, freeRef1.genNr);
        Assert.assertTrue(freeRef2.isFree());
        Assert.assertEquals(freeRef1.offsetOrIndex, freeRef2.objNr);
        Assert.assertEquals(1, freeRef2.genNr);
        pdfDocument.close();
    }
}
