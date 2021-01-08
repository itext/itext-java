/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class XrefStreamDocumentUpdatesTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/XrefStreamDocumentUpdatesTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/XrefStreamDocumentUpdatesTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void readFreeRefReusingInIncrementTest() throws IOException {
        PdfDocument document = new PdfDocument(new PdfReader
                (sourceFolder + "readFreeRefReusingInIncrement.pdf"));

        PdfArray array = (PdfArray)  document.getCatalog().getPdfObject()
                .get(new PdfName("CustomKey"));

        Assert.assertTrue(array instanceof PdfArray);
        Assert.assertEquals(0, array.size());
    }

    @Test
    public void notReuseIndirectRefForObjectStreamTest() throws IOException {
        String inputFile = sourceFolder + "notReuseIndirectRefForObjectStream.pdf";
        String outputFile = destinationFolder + "adjustingsInObjStm.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputFile),
                new PdfWriter(outputFile).setCompressionLevel(CompressionConstants.NO_COMPRESSION));

        PdfArray media = pdfDoc.getPage(1).getPdfObject().getAsArray(PdfName.MediaBox);
        media.remove(2);
        media.add(new PdfNumber(500));
        media.setModified();

        pdfDoc.close();

        PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "adjustingsInObjStm.pdf"));
        PdfObject object = doc.getPdfObject(8);
        PdfDictionary pageDict = (PdfDictionary) object;

        int expectNumberOfObjects = pdfDoc.getNumberOfPdfObjects();

        //output pdf document should be openable
        Assert.assertEquals(10, expectNumberOfObjects);
        Assert.assertEquals(PdfName.ObjStm, pageDict.get(PdfName.Type));
    }

    @Test
    public void notReuseIndRefForObjStreamInIncrementTest() throws IOException {
        String inputFile = sourceFolder + "notReuseIndirectRefForObjectStream.pdf";
        String outputFile = destinationFolder + "adjustingsInObjStmInIncrement.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputFile),
                new PdfWriter(outputFile).setCompressionLevel(CompressionConstants.NO_COMPRESSION),
                new StampingProperties().useAppendMode());

        PdfObject newObj = pdfDoc.getPage(1).getPdfObject();
        newObj.setModified();
        pdfDoc.close();

        PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "adjustingsInObjStmInIncrement.pdf"));

        PdfDictionary objStmDict = (PdfDictionary) doc.getPdfObject(8);

        int expectNumberOfObjects = doc.getNumberOfPdfObjects();

        //output pdf document should be openable
        Assert.assertEquals(9, expectNumberOfObjects);
        Assert.assertEquals(PdfName.ObjStm, objStmDict.get(PdfName.Type));
        doc.close();
    }

    @Test
    public void freeRefReuseWhenAddNewObjTest() throws IOException {
        String filename = destinationFolder + "freeRefReuseWhenAddNewObj.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(new PdfReader(sourceFolder + "pdfWithRemovedObjInOldVer.pdf"),
                new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION),
                new StampingProperties().useAppendMode());
        pdfDoc1.getCatalog().getPdfObject().put(new PdfName("CustomKey"), new PdfArray().makeIndirect(pdfDoc1));
        PdfObject newObj = pdfDoc1.getCatalog().getPdfObject();
        newObj.setModified();

        int expectObjNumber = pdfDoc1.getCatalog().getPdfObject().get(new PdfName("CustomKey"))
                .getIndirectReference().getObjNumber();
        int expectGenNumber = pdfDoc1.getCatalog().getPdfObject().get(new PdfName("CustomKey"))
                .getIndirectReference().getGenNumber();

        PdfXrefTable xref = pdfDoc1.getXref();

        Assert.assertEquals(8, expectObjNumber);
        Assert.assertEquals(0, expectGenNumber);
        Assert.assertTrue(xref.get(5).isFree());
        
        pdfDoc1.close();
    }

    @Test
    public void checkEncryptionInXrefStmInIncrementsTest() throws IOException, InterruptedException {
        String inFileName = sourceFolder + "encryptedDocWithXrefStm.pdf";
        String outFileName = destinationFolder + "checkEncryptionInXrefStmInIncrements.pdf";

        PdfReader pdfReader = new PdfReader(inFileName).setUnethicalReading(true);

        PdfDocument pdfDocument = new PdfDocument(pdfReader, new PdfWriter(outFileName),
                new StampingProperties().useAppendMode().preserveEncryption());

        PdfDictionary xrefStm = (PdfDictionary) pdfDocument.getPdfObject(6);

        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, inFileName, destinationFolder));
        Assert.assertEquals(PdfName.XRef, xrefStm.get(PdfName.Type));
    }

    @Test
    public void hybridReferenceInIncrementsTest() throws IOException, InterruptedException {
        String inFileName = sourceFolder + "hybridReferenceDocument.pdf";
        String outFileName = destinationFolder + "hybridReferenceInIncrements.pdf";

        PdfReader pdfReader = new PdfReader(inFileName);

        PdfDocument pdfDocument = new PdfDocument(pdfReader, new PdfWriter(outFileName),
                new StampingProperties().useAppendMode());

        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, inFileName, destinationFolder));
    }

    @Test
    public void xrefStmInWriteModeTest() throws IOException {
        String fileName = destinationFolder + "xrefStmInWriteMode.pdf";

        PdfWriter writer = new PdfWriter(fileName, new WriterProperties().setFullCompressionMode(true)
                .setCompressionLevel(CompressionConstants.NO_COMPRESSION));
        PdfDocument pdfDocument = new PdfDocument(writer);
        PdfPage page = pdfDocument.addNewPage();

        PdfTextAnnotation textannot = new PdfTextAnnotation(new Rectangle(100, 600, 50, 40));
        textannot
                .setText(new PdfString("Text Annotation 01"))
                .setContents(new PdfString("Some contents..."));
        page.addAnnotation(textannot);
        pdfDocument.close();


        PdfDocument doc = new PdfDocument(new PdfReader(fileName));

        int xrefTableCounter = 0;
        for (int i = 1; i < doc.getNumberOfPdfObjects(); i++) {
            PdfObject obj = doc.getPdfObject(i);

            if (obj instanceof PdfDictionary) {
                PdfDictionary objStmDict = (PdfDictionary) doc.getPdfObject(i);
                PdfObject type = objStmDict.get(PdfName.Type);

                if (type != null && type.equals(PdfName.XRef)) {
                    xrefTableCounter++;
                }
            }
        }

        Assert.assertEquals(((PdfNumber) doc.getTrailer().get(PdfName.Size)).intValue(), doc.getNumberOfPdfObjects());
        doc.close();
        Assert.assertEquals(1, xrefTableCounter);
    }

    @Test
    public void xrefStmInAppendModeTest() throws IOException {
        String fileName = destinationFolder + "xrefStmInAppendMode.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "xrefStmInWriteMode.pdf"),
                new PdfWriter(fileName).setCompressionLevel(CompressionConstants.NO_COMPRESSION),
                new StampingProperties().useAppendMode());
        pdfDocument.close();


        PdfDocument doc = new PdfDocument(new PdfReader(fileName));

        int xrefTableCounter = 0;
        for (int i = 1; i < doc.getNumberOfPdfObjects(); i++) {
            PdfObject obj = doc.getPdfObject(i);

            if (obj instanceof PdfDictionary) {
                PdfDictionary objStmDict = (PdfDictionary) doc.getPdfObject(i);
                PdfObject type = objStmDict.get(PdfName.Type);

                if (type != null && type.equals(PdfName.XRef)) {
                    xrefTableCounter++;
                }
            }
        }

        Assert.assertEquals(((PdfNumber) doc.getTrailer().get(PdfName.Size)).intValue(), doc.getNumberOfPdfObjects());
        doc.close();
        Assert.assertEquals(2, xrefTableCounter);
    }

    @Test
    public void closeDocumentWithoutModificationsTest() throws IOException {
        String fileName = destinationFolder + "xrefStmInAppendMode.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "xrefStmInWriteMode.pdf"),
                new PdfWriter(fileName).setCompressionLevel(CompressionConstants.NO_COMPRESSION),
                new StampingProperties().useAppendMode());
        // Clear state for document info indirect reference so that there are no modified objects
        // in the document due to which, the document will have only one href table.
        pdfDocument.getDocumentInfo().getPdfObject().getIndirectReference().clearState(PdfObject.MODIFIED);
        pdfDocument.close();


        PdfDocument doc = new PdfDocument(new PdfReader(fileName));

        int xrefTableCounter = 0;
        for (int i = 1; i < doc.getNumberOfPdfObjects(); i++) {
            PdfObject obj = doc.getPdfObject(i);

            if (obj instanceof PdfDictionary) {
                PdfDictionary objStmDict = (PdfDictionary) doc.getPdfObject(i);
                PdfObject type = objStmDict.get(PdfName.Type);

                if (type != null && type.equals(PdfName.XRef)) {
                    xrefTableCounter++;
                }
            }
        }

        Assert.assertEquals(((PdfNumber) doc.getTrailer().get(PdfName.Size)).intValue(), doc.getNumberOfPdfObjects());
        doc.close();
        Assert.assertEquals(1, xrefTableCounter);
    }
}
