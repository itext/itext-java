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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class EncryptedEmbeddedStreamsHandlerTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/EncryptedEmbeddedStreamsHandlerTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/EncryptedEmbeddedStreamsHandlerTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void noReaderStandardEncryptionAddFileAttachment() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "noReaderStandardEncryptionAddFileAttachment.pdf";
        String cmpFileName = sourceFolder + "cmp_noReaderStandardEncryptionAddFileAttachment.pdf";

        PdfDocument pdfDocument = createEncryptedDocument(EncryptionConstants.STANDARD_ENCRYPTION_128, outFileName);
        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                pdfDocument, "file".getBytes(), "description", "file.txt", null, null, null);
        pdfDocument.addFileAttachment("file.txt", fs);

        pdfDocument.addNewPage();
        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff", "password".getBytes(), "password".getBytes()));
    }

    @Test
    public void noReaderAesEncryptionAddFileAttachment() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "noReaderAesEncryptionAddFileAttachment.pdf";
        String cmpFileName = sourceFolder + "cmp_noReaderAesEncryptionAddFileAttachment.pdf";

        PdfDocument pdfDocument = createEncryptedDocument(EncryptionConstants.ENCRYPTION_AES_128, outFileName);
        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                pdfDocument, "file".getBytes(), "description", "file.txt", null, null, null);
        pdfDocument.addFileAttachment("file.txt", fs);

        pdfDocument.addNewPage();
        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff", "password".getBytes(), "password".getBytes()));
    }

    @Test
    public void withReaderStandardEncryptionAddFileAttachment() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "withReaderStandardEncryptionAddFileAttachment.pdf";
        String cmpFileName = sourceFolder + "cmp_withReaderStandardEncryptionAddFileAttachment.pdf";

        PdfReader reader = new PdfReader(sourceFolder + "pdfWithFileAttachments.pdf", new ReaderProperties().setPassword("password".getBytes()));
        // Setting compression level to zero doesn't affect the encryption at any level.
        // We do it to simplify observation of the resultant PDF.
        PdfDocument pdfDocument = new PdfDocument(reader, new PdfWriter(outFileName).setCompressionLevel(0));
        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                pdfDocument, "file".getBytes(), "description", "file.txt", null, null, null);
        pdfDocument.addFileAttachment("file.txt", fs);

        pdfDocument.addNewPage();
        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void noReaderStandardEncryptionAddAnnotation() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "noReaderStandardEncryptionAddAnnotation.pdf";
        String cmpFileName = sourceFolder + "cmp_noReaderStandardEncryptionAddAnnotation.pdf";

        PdfDocument pdfDocument = createEncryptedDocument(EncryptionConstants.STANDARD_ENCRYPTION_128, outFileName);
        pdfDocument.addNewPage();
        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                pdfDocument, "file".getBytes(), "description", "file.txt", null, null, null);
        pdfDocument.getPage(1).addAnnotation(new PdfFileAttachmentAnnotation(new Rectangle(100, 100), fs));

        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff", "password".getBytes(), "password".getBytes()));
    }

    @Test
    public void withReaderStandardEncryptionAddAnnotation() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "withReaderStandardEncryptionAddAnnotation.pdf";
        String cmpFileName = sourceFolder + "cmp_withReaderStandardEncryptionAddAnnotation.pdf";

        PdfReader reader = new PdfReader(sourceFolder + "pdfWithFileAttachmentAnnotations.pdf", new ReaderProperties().setPassword("password".getBytes()));
        // Setting compression level to zero doesn't affect the encryption at any level.
        // We do it to simplify observation of the resultant PDF.
        PdfDocument pdfDocument = new PdfDocument(reader, new PdfWriter(outFileName).setCompressionLevel(0));
        pdfDocument.addNewPage();
        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                pdfDocument, "file".getBytes(), "description", "file.txt", null, null, null);
        pdfDocument.getPage(1).addAnnotation(new PdfFileAttachmentAnnotation(new Rectangle(100, 100), fs));

        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void readerWithoutEncryptionWriterStandardEncryption() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "readerWithoutEncryptionWriterStandardEncryption.pdf";
        String cmpFileName = sourceFolder + "cmp_readerWithoutEncryptionWriterStandardEncryption.pdf";

        PdfReader reader = new PdfReader(sourceFolder + "pdfWithUnencryptedAttachmentAnnotations.pdf");
        PdfDocument pdfDocument = createEncryptedDocument(reader, EncryptionConstants.STANDARD_ENCRYPTION_128, outFileName);
        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                pdfDocument, "file".getBytes(), "description", "file.txt", null, null, null);
        pdfDocument.addFileAttachment("new attachment", fs);

        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff", "password".getBytes(), "password".getBytes()));
    }

    private PdfDocument createEncryptedDocument(int encryptionAlgorithm, String outFileName) throws IOException {
        PdfWriter writer = new PdfWriter(outFileName,
                new WriterProperties().setStandardEncryption(
                        "password".getBytes(), "password".getBytes(), 0, encryptionAlgorithm | EncryptionConstants.EMBEDDED_FILES_ONLY));
        // Setting compression level to zero doesn't affect the encryption at any level.
        // We do it to simplify observation of the resultant PDF.
        writer.setCompressionLevel(0);
        return new PdfDocument(writer);
    }

    private PdfDocument createEncryptedDocument(PdfReader reader, int encryptionAlgorithm, String outFileName) throws IOException {
        PdfWriter writer = new PdfWriter(outFileName,
                new WriterProperties().setStandardEncryption(
                        "password".getBytes(), "password".getBytes(), 0, encryptionAlgorithm | EncryptionConstants.EMBEDDED_FILES_ONLY));
        // Setting compression level to zero doesn't affect the encryption at any level.
        // We do it to simplify observation of the resultant PDF.
        writer.setCompressionLevel(0);
        return new PdfDocument(reader, writer);
    }
}
