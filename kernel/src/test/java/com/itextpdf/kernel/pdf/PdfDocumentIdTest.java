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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfReader.StrictnessLevel;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfDocumentIdTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDocumentTestID/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfDocumentTestID/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void changeIdTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String value = "Modified ID 1234";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setModifiedDocumentId(new PdfString((value)));
        PdfWriter writer = new PdfWriter(baos, writerProperties);
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.addNewPage();
        pdfDocument.close();

        byte[] documentBytes = baos.toByteArray();

        baos.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(documentBytes));
        pdfDocument = new PdfDocument(reader);
        PdfArray idArray = pdfDocument.getTrailer().getAsArray(PdfName.ID);
        Assertions.assertNotNull(idArray);
        String extractedValue = idArray.getAsString(1).getValue();
        pdfDocument.close();

        Assertions.assertEquals(value, extractedValue);
    }

    @Test
    public void changeIdTest02() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new PdfException(e);
        }
        PdfString initialId = new PdfString(md5.digest("Initial ID 56789".getBytes()));
        PdfWriter writer = new PdfWriter(baos, new WriterProperties().setInitialDocumentId(initialId));
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.addNewPage();
        pdfDocument.close();

        byte[] documentBytes = baos.toByteArray();

        baos.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(documentBytes));
        pdfDocument = new PdfDocument(reader);
        PdfArray idArray = pdfDocument.getTrailer().getAsArray(PdfName.ID);
        Assertions.assertNotNull(idArray);
        PdfString extractedString = idArray.getAsString(1);
        pdfDocument.close();

        Assertions.assertEquals(initialId, extractedString);
    }

    @Test
    public void changeIdTest03() throws IOException {
        ByteArrayOutputStream baosInitial = new ByteArrayOutputStream();
        ByteArrayOutputStream baosModified = new ByteArrayOutputStream();

        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new PdfException(e);
        }
        PdfString initialId = new PdfString(md5.digest("Initial ID 56789".getBytes()));
        PdfString modifiedId = new PdfString("Modified ID 56789");

        PdfWriter writer = new PdfWriter(baosInitial, new WriterProperties()
                .setInitialDocumentId(initialId).setModifiedDocumentId(modifiedId));
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.addNewPage();
        pdfDocument.close();

        PdfReader reader = new PdfReader(new RandomAccessSourceFactory().createSource(baosInitial.toByteArray()), new ReaderProperties());
        pdfDocument = new PdfDocument(reader);
        PdfArray idArray = pdfDocument.getTrailer().getAsArray(PdfName.ID);
        pdfDocument.close();
        Assertions.assertNotNull(idArray);
        PdfString extractedInitialValue = idArray.getAsString(0);
        Assertions.assertEquals(initialId, extractedInitialValue);
        PdfString extractedModifiedValue = idArray.getAsString(1);
        Assertions.assertEquals(modifiedId, extractedModifiedValue);


        pdfDocument = new PdfDocument(new PdfReader(new RandomAccessSourceFactory().createSource(baosInitial.toByteArray()), new ReaderProperties()),
                new PdfWriter(baosModified));
        new PdfCanvas(pdfDocument.addNewPage())
                .saveState()
                .lineTo(100, 100)
                .moveTo(100, 100)
                .stroke()
                .restoreState();
        pdfDocument.close();

        reader = new PdfReader(new RandomAccessSourceFactory().createSource(baosModified.toByteArray()), new ReaderProperties());
        pdfDocument = new PdfDocument(reader);
        idArray = pdfDocument.getTrailer().getAsArray(PdfName.ID);
        pdfDocument.close();
        Assertions.assertNotNull(idArray);
        extractedInitialValue = idArray.getAsString(0);
        Assertions.assertEquals(initialId, extractedInitialValue);
        extractedModifiedValue = idArray.getAsString(1);
        Assertions.assertNotEquals(modifiedId, extractedModifiedValue);
    }

    @Test
    public void fetchReaderIdTest() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new PdfException(e);
        }
        PdfString initialId = new PdfString(md5.digest("Initial ID 56789".getBytes()));
        PdfWriter writer = new PdfWriter(baos, new WriterProperties().setInitialDocumentId(initialId));
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.addNewPage();
        pdfDocument.close();

        byte[] documentBytes = baos.toByteArray();

        baos.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(documentBytes));
        pdfDocument = new PdfDocument(reader);
        String firstOriginalId = new String(reader.getOriginalFileId());
        String secondOriginalId = new String(reader.getOriginalFileId());
        String firstModifiedId = new String(reader.getModifiedFileId());
        String secondModifiedId = new String(reader.getModifiedFileId());

        Assertions.assertEquals(firstOriginalId, secondOriginalId);
        Assertions.assertEquals(firstModifiedId, secondModifiedId);
    }

    @Test
    public void writerPropertiesPriorityTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new PdfException(e);
        }
        PdfString originalId = new PdfString(md5.digest("Initial ID 01234".getBytes()));
        PdfString modifiedId = new PdfString(md5.digest("Modified ID 56789".getBytes()));
        PdfWriter writer = new PdfWriter(baos, new WriterProperties().setInitialDocumentId(originalId).setModifiedDocumentId(modifiedId));
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.addNewPage();
        pdfDocument.close();

        byte[] documentBytes = baos.toByteArray();

        baos.close();

        PdfString newOriginalId = new PdfString(md5.digest("Initial ID 98765".getBytes()));
        PdfString newModifiedId = new PdfString(md5.digest("Modified ID 43210".getBytes()));

        PdfReader reader = new PdfReader(new ByteArrayInputStream(documentBytes));
        PdfWriter newWriter = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties()
                .setInitialDocumentId(newOriginalId)
                .setModifiedDocumentId(newModifiedId));
        pdfDocument = new PdfDocument(reader, newWriter);

        String extractedOriginalId = pdfDocument.getOriginalDocumentId().getValue();
        String extractedModifiedId = pdfDocument.getModifiedDocumentId().getValue();

        pdfDocument.close();

        Assertions.assertEquals(extractedOriginalId, newOriginalId.getValue());
        Assertions.assertEquals(extractedModifiedId, newModifiedId.getValue());
    }

    @Test
    public void readPdfWithTwoStringIdsTest() throws IOException{
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "pdfWithTwoStringIds.pdf"));
        String originalId = null;
        String modifiedId = null;
        if(pdfDocument.getOriginalDocumentId() != null) {
            originalId = pdfDocument.getOriginalDocumentId().getValue();
        }
        if(pdfDocument.getModifiedDocumentId() != null) {
            modifiedId = pdfDocument.getModifiedDocumentId().getValue();
        }
        pdfDocument.close();

        Assertions.assertNotNull(originalId);
        Assertions.assertNotNull(modifiedId);

    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_IDS_ARE_CORRUPTED)
    })
    public void readPdfWithTwoNumberIdsTest() throws IOException{
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "pdfWithTwoNumberIds.pdf"));
        String originalId = null;
        String modifiedId = null;
        if(pdfDocument.getOriginalDocumentId() != null) {
            originalId = pdfDocument.getOriginalDocumentId().getValue();
        }
        if(pdfDocument.getModifiedDocumentId() != null) {
            modifiedId = pdfDocument.getModifiedDocumentId().getValue();
        }

        pdfDocument.close();

        Assertions.assertNull(originalId);
        Assertions.assertNull(modifiedId);

    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_IDS_ARE_CORRUPTED)
    })
    public void readPdfWithOneNumberOneStringIdsTest() throws IOException{
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "pdfWithOneNumberOneStringIds.pdf"));
        String originalId = null;
        String modifiedId = null;
        if(pdfDocument.getOriginalDocumentId() != null) {
            originalId = pdfDocument.getOriginalDocumentId().getValue();
        }
        if(pdfDocument.getModifiedDocumentId() != null) {
            modifiedId = pdfDocument.getModifiedDocumentId().getValue();
        }
        pdfDocument.close();

        Assertions.assertNull(originalId);
        Assertions.assertNotNull(modifiedId);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_IDS_ARE_CORRUPTED)
    })
    public void readPdfWithOneStringIdValueTest() throws IOException{
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "pdfWithOneStringId.pdf"));
        String originalId = null;
        String modifiedId = null;
        if(pdfDocument.getOriginalDocumentId() != null) {
            originalId = pdfDocument.getOriginalDocumentId().getValue();
        }
        if(pdfDocument.getModifiedDocumentId() != null) {
            modifiedId = pdfDocument.getModifiedDocumentId().getValue();
        }
        pdfDocument.close();

        Assertions.assertNull(originalId);
        Assertions.assertNull(modifiedId);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_IDS_ARE_CORRUPTED)
    })
    public void readPdfWithOneNumberIdValueTest() throws IOException{
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "pdfWithOneNumberId.pdf"));
        String originalId = null;
        String modifiedId = null;
        if(pdfDocument.getOriginalDocumentId() != null) {
            originalId = pdfDocument.getOriginalDocumentId().getValue();
        }
        if(pdfDocument.getModifiedDocumentId() != null) {
            modifiedId = pdfDocument.getModifiedDocumentId().getValue();
        }
        pdfDocument.close();

        Assertions.assertNull(originalId);
        Assertions.assertNull(modifiedId);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_IDS_ARE_CORRUPTED)
    })
    public void readPdfWithNoIdTest() throws IOException{
        PdfReader reader = new PdfReader(sourceFolder + "pdfWithNoId.pdf");

        PdfDocument pdfDocument = new PdfDocument(reader);
        String originalId = null;
        String modifiedId = null;
        if(pdfDocument.getOriginalDocumentId() != null) {
            originalId = pdfDocument.getOriginalDocumentId().getValue();
        }
        if(pdfDocument.getModifiedDocumentId() != null) {
            modifiedId = pdfDocument.getModifiedDocumentId().getValue();
        }
        pdfDocument.close();

        Assertions.assertNull(originalId);
        Assertions.assertNull(modifiedId);

        Assertions.assertEquals(0, reader.getOriginalFileId().length);
        Assertions.assertEquals(0, reader.getModifiedFileId().length);
    }

    @Test
    public void readPdfWithNoIdAndConservativeReadingTest() throws IOException{
        try (PdfReader reader = new PdfReader(sourceFolder + "pdfWithNoId.pdf")
                .setStrictnessLevel(StrictnessLevel.CONSERVATIVE)) {

            Exception e = Assertions.assertThrows(PdfException.class, () -> new PdfDocument(reader));
            Assertions.assertEquals(IoLogMessageConstant.DOCUMENT_IDS_ARE_CORRUPTED, e.getMessage());
        }
    }


//    @Test
//    public void appendModeTest() {
//        String originalId;
//        String newOriginalId;
//        String appendModeNewOriginalId;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PdfWriter initialWriter = CompareTool.createTestPdfWriter(baos, new WriterProperties().setInitialDocumentId(originalId));
//        PdfWriter newWriter = CompareTool.createTestPdfWriter(baos, new WriterProperties().setInitialDocumentId(newOriginalId));
//        PdfWriter appendModeWriter = CompareTool.createTestPdfWriter(baos, new WriterProperties().setInitialDocumentId(appendModeNewOriginalId));
//
//
//    }
//
//    @Test
//    public void encryptionAes128Test() {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PdfString originalId = new PdfString("Original ID 56789");
//        PdfWriter initialWriter = CompareTool.createTestPdfWriter(baos, new WriterProperties().setInitialDocumentId(originalId));
//
//        Assertions.assertNotEquals();
//        Assertions.assertEquals();
//
//    }
}
