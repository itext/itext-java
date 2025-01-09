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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class XrefStreamDocumentUpdatesTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/XrefStreamDocumentUpdatesTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/XrefStreamDocumentUpdatesTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    public void readFreeRefReusingInIncrementTest() throws IOException {
        PdfDocument document = new PdfDocument(new PdfReader
                (sourceFolder + "readFreeRefReusingInIncrement.pdf"));

        PdfArray array = (PdfArray)  document.getCatalog().getPdfObject()
                .get(new PdfName("CustomKey"));

        Assertions.assertTrue(array instanceof PdfArray);
        Assertions.assertEquals(0, array.size());
    }

    @Test
    public void notReuseIndirectRefForObjectStreamTest() throws IOException {
        String inputFile = sourceFolder + "notReuseIndirectRefForObjectStream.pdf";
        String outputFile = destinationFolder + "adjustingsInObjStm.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputFile),
                CompareTool.createTestPdfWriter(outputFile).setCompressionLevel(CompressionConstants.NO_COMPRESSION));

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
        Assertions.assertEquals(10, expectNumberOfObjects);
        Assertions.assertEquals(PdfName.ObjStm, pageDict.get(PdfName.Type));
    }

    @Test
    public void notReuseIndRefForObjStreamInIncrementTest() throws IOException {
        String inputFile = sourceFolder + "notReuseIndirectRefForObjectStream.pdf";
        String outputFile = destinationFolder + "adjustingsInObjStmInIncrement.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputFile),
                CompareTool.createTestPdfWriter(outputFile).setCompressionLevel(CompressionConstants.NO_COMPRESSION),
                new StampingProperties().useAppendMode());

        PdfObject newObj = pdfDoc.getPage(1).getPdfObject();
        newObj.setModified();
        pdfDoc.close();

        PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "adjustingsInObjStmInIncrement.pdf"));

        PdfDictionary objStmDict = (PdfDictionary) doc.getPdfObject(8);

        int expectNumberOfObjects = doc.getNumberOfPdfObjects();

        //output pdf document should be openable
        Assertions.assertEquals(9, expectNumberOfObjects);
        Assertions.assertEquals(PdfName.ObjStm, objStmDict.get(PdfName.Type));
        doc.close();
    }

    @Test
    public void freeRefReuseWhenAddNewObjTest() throws IOException {
        String filename = destinationFolder + "freeRefReuseWhenAddNewObj.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(new PdfReader(sourceFolder + "pdfWithRemovedObjInOldVer.pdf"),
                CompareTool.createTestPdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION),
                new StampingProperties().useAppendMode());
        pdfDoc1.getCatalog().getPdfObject().put(new PdfName("CustomKey"), new PdfArray().makeIndirect(pdfDoc1));
        PdfObject newObj = pdfDoc1.getCatalog().getPdfObject();
        newObj.setModified();

        int expectObjNumber = pdfDoc1.getCatalog().getPdfObject().get(new PdfName("CustomKey"))
                .getIndirectReference().getObjNumber();
        int expectGenNumber = pdfDoc1.getCatalog().getPdfObject().get(new PdfName("CustomKey"))
                .getIndirectReference().getGenNumber();

        PdfXrefTable xref = pdfDoc1.getXref();

        Assertions.assertEquals(8, expectObjNumber);
        Assertions.assertEquals(0, expectGenNumber);
        Assertions.assertTrue(xref.get(5).isFree());

        pdfDoc1.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void checkEncryptionInXrefStmInIncrementsTest() throws IOException, InterruptedException {
        String inFileName = sourceFolder + "encryptedDocWithXrefStm.pdf";
        String outFileName = destinationFolder + "checkEncryptionInXrefStmInIncrements.pdf";

        PdfReader pdfReader = new PdfReader(inFileName).setUnethicalReading(true);

        PdfDocument pdfDocument = new PdfDocument(pdfReader, CompareTool.createTestPdfWriter(outFileName),
                new StampingProperties().useAppendMode().preserveEncryption());

        PdfDictionary xrefStm = (PdfDictionary) pdfDocument.getPdfObject(6);

        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, inFileName, destinationFolder));
        Assertions.assertEquals(PdfName.XRef, xrefStm.get(PdfName.Type));
    }

    @Test
    public void hybridReferenceInIncrementsTest() throws IOException, InterruptedException {
        String inFileName = sourceFolder + "hybridReferenceDocument.pdf";
        String outFileName = destinationFolder + "hybridReferenceInIncrements.pdf";

        PdfReader pdfReader = new PdfReader(inFileName);

        PdfDocument pdfDocument = new PdfDocument(pdfReader, CompareTool.createTestPdfWriter(outFileName),
                new StampingProperties().useAppendMode());

        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, inFileName, destinationFolder));
    }

    @Test
    public void xrefStmInWriteModeTest() throws IOException {
        String fileName = destinationFolder + "xrefStmInWriteMode.pdf";

        PdfWriter writer = CompareTool.createTestPdfWriter(fileName, new WriterProperties().setFullCompressionMode(true)
                .setCompressionLevel(CompressionConstants.NO_COMPRESSION));
        PdfDocument pdfDocument = new PdfDocument(writer);
        PdfPage page = pdfDocument.addNewPage();

        PdfTextAnnotation textannot = new PdfTextAnnotation(new Rectangle(100, 600, 50, 40));
        textannot
                .setText(new PdfString("Text Annotation 01"))
                .setContents(new PdfString("Some contents..."));
        page.addAnnotation(textannot);
        pdfDocument.close();


        PdfDocument doc = new PdfDocument(CompareTool.createOutputReader(fileName));

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

        Assertions.assertEquals(((PdfNumber) doc.getTrailer().get(PdfName.Size)).intValue(), doc.getNumberOfPdfObjects());
        doc.close();
        Assertions.assertEquals(1, xrefTableCounter);
    }

    @Test
    public void xrefStmInAppendModeTest() throws IOException {
        String fileName = destinationFolder + "xrefStmInAppendMode.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "xrefStmInWriteMode.pdf"),
                CompareTool.createTestPdfWriter(fileName).setCompressionLevel(CompressionConstants.NO_COMPRESSION),
                new StampingProperties().useAppendMode());
        pdfDocument.close();


        PdfDocument doc = new PdfDocument(CompareTool.createOutputReader(fileName));

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

        Assertions.assertEquals(((PdfNumber) doc.getTrailer().get(PdfName.Size)).intValue(), doc.getNumberOfPdfObjects());
        doc.close();
        Assertions.assertEquals(2, xrefTableCounter);
    }

    @Test
    public void closeDocumentWithoutModificationsTest() throws IOException {
        String fileName = destinationFolder + "xrefStmInAppendMode.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "xrefStmInWriteMode.pdf"),
                CompareTool.createTestPdfWriter(fileName).setCompressionLevel(CompressionConstants.NO_COMPRESSION),
                new StampingProperties().useAppendMode());
        // Clear state for document info indirect reference so that there are no modified objects
        // in the document due to which, the document will have only one xref table.
        pdfDocument.getDocumentInfo().getPdfObject().getIndirectReference().clearState(PdfObject.MODIFIED);
        pdfDocument.close();


        PdfDocument doc = new PdfDocument(CompareTool.createOutputReader(fileName));

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

        Assertions.assertEquals(((PdfNumber) doc.getTrailer().get(PdfName.Size)).intValue(), doc.getNumberOfPdfObjects());
        doc.close();
        Assertions.assertEquals(1, xrefTableCounter);
    }

    @Test
    public void hybridReferenceIncrementTwiceTest() throws IOException, InterruptedException {
        String inFileName = sourceFolder + "hybridReferenceDocument.pdf";
        String outFileName = destinationFolder + "hybridReferenceDocumentUpdateTwice.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(
                new PdfReader(inFileName),
                new PdfWriter(destinationFolder + "hybridReferenceDocumentUpdate.pdf"),
                new StampingProperties().useAppendMode()
        );
        pdfDoc1.close();

        PdfDocument pdfDoc2 = new PdfDocument(
                new PdfReader(destinationFolder + "hybridReferenceDocumentUpdate.pdf"),
                CompareTool.createTestPdfWriter(outFileName),
                new StampingProperties().useAppendMode()
        );
        pdfDoc2.close();

        //if document processed correctly, no errors should occur
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, inFileName, destinationFolder));
    }
}
