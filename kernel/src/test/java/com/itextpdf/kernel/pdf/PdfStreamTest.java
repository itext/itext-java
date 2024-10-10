/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.util.Collections;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("BouncyCastleIntegrationTest")
public class PdfStreamTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfStreamTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfStreamTest/";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void streamAppendDataOnJustCopiedWithCompression() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "pageWithContent.pdf";
        String cmpFile = sourceFolder + "cmp_streamAppendDataOnJustCopiedWithCompression.pdf";
        String destFile = destinationFolder + "streamAppendDataOnJustCopiedWithCompression.pdf";

        PdfDocument srcDocument = new PdfDocument(new PdfReader(srcFile));
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destFile));
        srcDocument.copyPagesTo(1, 1, document);
        srcDocument.close();

        String newContentString = "BT\n" +
                "/F1 36 Tf\n" +
                "50 700 Td\n" +
                "(new content here!) Tj\n" +
                "ET";
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        document.getPage(1).getLastContentStream().setData(newContent, true);

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6445 fix different DeflaterOutputStream behavior)
    public void runLengthEncodingTest01() throws IOException {
        String srcFile = sourceFolder + "runLengthEncodedImages.pdf";

        PdfDocument document = new PdfDocument(new PdfReader(srcFile));

        PdfImageXObject im1 = document.getPage(1).getResources().getImage(new PdfName("Im1"));
        PdfImageXObject im2 = document.getPage(1).getResources().getImage(new PdfName("Im2"));

        byte[] imgBytes1 = im1.getImageBytes();
        byte[] imgBytes2 = im2.getImageBytes();

        document.close();

        byte[] cmpImgBytes1 = readFile(sourceFolder + "cmp_img1.jpg");
        byte[] cmpImgBytes2 = readFile(sourceFolder + "cmp_img2.jpg");

        Assertions.assertArrayEquals(imgBytes1, cmpImgBytes1);
        Assertions.assertArrayEquals(imgBytes2, cmpImgBytes2);
    }

    @Test
    public void indirectRefInFilterAndNoTaggedPdfTest() throws IOException {
        String inFile = sourceFolder + "indirectRefInFilterAndNoTaggedPdf.pdf";
        String outFile = destinationFolder + "destIndirectRefInFilterAndNoTaggedPdf.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(inFile));
        PdfDocument outDoc = new PdfDocument(new PdfReader(inFile), CompareTool.createTestPdfWriter(outFile));
        outDoc.close();

        PdfDocument doc = new PdfDocument(CompareTool.createOutputReader(outFile));

        PdfStream outStreamIm1 = doc.getFirstPage().getResources().getResource(PdfName.XObject)
                .getAsStream(new PdfName("Im1"));
        PdfStream outStreamIm2 = doc.getFirstPage().getResources().getResource(PdfName.XObject)
                .getAsStream(new PdfName("Im2"));

        PdfStream cmpStreamIm1 = srcDoc.getFirstPage().getResources().getResource(PdfName.XObject)
                .getAsStream(new PdfName("Im1"));
        PdfStream cmpStreamIm2 = srcDoc.getFirstPage().getResources().getResource(PdfName.XObject)
                .getAsStream(new PdfName("Im2"));

        Assertions.assertNull(new CompareTool().compareStreamsStructure(outStreamIm1, cmpStreamIm1));
        Assertions.assertNull(new CompareTool().compareStreamsStructure(outStreamIm2, cmpStreamIm2));

        srcDoc.close();
        outDoc.close();
    }

    @Test
    public void cryptFilterFlushedBeforeReadStreamTest() throws IOException {
        String file = sourceFolder + "cryptFilterTest.pdf";
        String destFile = destinationFolder + "cryptFilterReadStreamTest.pdf";

        PdfReader reader = new com.itextpdf.kernel.pdf.PdfReader(file,
                new ReaderProperties().setPassword("World".getBytes(StandardCharsets.ISO_8859_1)));
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256 | EncryptionConstants.DO_NOT_ENCRYPT_METADATA;
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS;
        WriterProperties writerProperties = new WriterProperties().setStandardEncryption(
                "World".getBytes(StandardCharsets.ISO_8859_1),
                "Hello".getBytes(StandardCharsets.ISO_8859_1), permissions, encryptionType);
        PdfWriter writer = CompareTool.createTestPdfWriter(destFile, writerProperties.addXmpMetadata());

        PdfDocument doc = new PdfDocument(reader, writer);
        ((PdfStream)doc.getPdfObject(5)).getBytes();
        //Simulating that this flush happened automatically before normal stream flushing in close method
        ((PdfStream)doc.getPdfObject(5)).get(PdfName.Filter).flush();
        Exception exception = Assertions.assertThrows(PdfException.class, () -> doc.close());
        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.FLUSHED_STREAM_FILTER_EXCEPTION, "5", "0"),
                exception.getMessage());
    }

    @Test
    public void cryptFilterFlushedBeforeStreamTest() throws IOException {
        String file = sourceFolder + "cryptFilterTest.pdf";
        String destFile = destinationFolder + "cryptFilterStreamNotReadTest.pdf";

        PdfReader reader = new com.itextpdf.kernel.pdf.PdfReader(file,
                new ReaderProperties().setPassword("World".getBytes(StandardCharsets.ISO_8859_1)));
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256 | EncryptionConstants.DO_NOT_ENCRYPT_METADATA;
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS;
        WriterProperties writerProperties = new WriterProperties().setStandardEncryption(
                "World".getBytes(StandardCharsets.ISO_8859_1),
                "Hello".getBytes(StandardCharsets.ISO_8859_1), permissions, encryptionType);
        PdfWriter writer = CompareTool.createTestPdfWriter(destFile, writerProperties.addXmpMetadata());

        PdfDocument doc = new PdfDocument(reader, writer);
        //Simulating that this flush happened automatically before normal stream flushing in close method
        ((PdfStream)doc.getPdfObject(5)).get(PdfName.Filter).flush();
        Exception exception = Assertions.assertThrows(PdfException.class, () -> doc.close());
        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.FLUSHED_STREAM_FILTER_EXCEPTION, "5", "0"),
                exception.getMessage());
    }

    @Test
    public void cryptFilterFlushedAfterStreamTest() throws IOException, InterruptedException {
        String file = sourceFolder + "cryptFilterTest.pdf";
        String cmpFile = sourceFolder + "cmp_cryptFilterTest.pdf";
        String destFile = destinationFolder + "cryptFilterTest.pdf";
        byte[] user = "Hello".getBytes(StandardCharsets.ISO_8859_1);
        byte[] owner = "World".getBytes(StandardCharsets.ISO_8859_1);

        PdfReader reader = new com.itextpdf.kernel.pdf.PdfReader(file,
                new ReaderProperties().setPassword(owner));
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256 | EncryptionConstants.DO_NOT_ENCRYPT_METADATA;
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS;
        WriterProperties writerProperties = new WriterProperties().setStandardEncryption(user, owner, permissions,
                encryptionType);
        PdfWriter writer = CompareTool.createTestPdfWriter(destFile, writerProperties.addXmpMetadata());
        writer.setCompressionLevel(-1);

        PdfDocument doc = new PdfDocument(reader, writer);
        PdfObject cryptFilter = ((PdfStream)doc.getPdfObject(5)).get(PdfName.Filter);
        doc.getPdfObject(5).flush();
        //Simulating that this flush happened automatically before normal stream flushing in close method
        cryptFilter.flush();
        doc.close();
        CompareTool compareTool = new CompareTool().enableEncryptionCompare();
        String compareResult = compareTool.compareByContent(destFile, cmpFile, destinationFolder, "diff_", user, user);
        if (compareResult != null) {
            fail(compareResult);
        }
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FILTER_WAS_ALREADY_FLUSHED, count = 2, logLevel =
                    LogLevelConstants.INFO)
    })
    @Test
    public void indirectFilterInCatalogTest() throws IOException, InterruptedException {
        String file = sourceFolder + "indFilterInCatalog.pdf";
        String cmpFile = sourceFolder + "cmp_indFilterInCatalog.pdf";
        String destFile = destinationFolder + "indFilterInCatalog.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(file), CompareTool.createTestPdfWriter(destFile));
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FILTER_WAS_ALREADY_FLUSHED, count = 2, logLevel =
                    LogLevelConstants.INFO)
    })
    @Test
    public void userDefinedCompressionWithIndirectFilterInCatalogTest() throws IOException, InterruptedException {
        String file = sourceFolder + "indFilterInCatalog.pdf";
        String cmpFile = sourceFolder + "cmp_indFilterInCatalog.pdf";
        String destFile = destinationFolder + "indFilterInCatalog.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(file), CompareTool.createTestPdfWriter(destFile));
        PdfStream stream = (PdfStream) doc.getPdfObject(5);
        stream.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FILTER_WAS_ALREADY_FLUSHED, count = 2, logLevel =
                    LogLevelConstants.INFO)
    })
    @Test
    public void indirectFilterFlushedBeforeStreamTest() throws IOException, InterruptedException {
        String file = sourceFolder + "indFilterInCatalog.pdf";
        String cmpFile = sourceFolder + "cmp_indFilterInCatalog.pdf";
        String destFile = destinationFolder + "indFilterInCatalog.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(file), CompareTool.createTestPdfWriter(destFile));

        // Simulate the case in which filter is somehow already flushed before stream.
        // Either directly by user or because of any other reason.
        PdfObject filterObject = pdfDoc.getPdfObject(6);
        //Simulating that this flush happened automatically before normal stream flushing in close method
        filterObject.flush();
        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FILTER_WAS_ALREADY_FLUSHED, count = 2, logLevel =
                    LogLevelConstants.INFO)
    })
    @Test
    public void indirectFilterMarkedToBeFlushedBeforeStreamTest() throws IOException, InterruptedException {
        String file = sourceFolder + "indFilterInCatalog.pdf";
        String cmpFile = sourceFolder + "cmp_indFilterInCatalog.pdf";
        String destFile = destinationFolder + "indFilterInCatalog.pdf";

        PdfWriter writer = CompareTool.createTestPdfWriter(destFile);
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(file), writer);

        // Simulate the case when indirect filter object is marked to be flushed before the stream itself.
        PdfObject filterObject = pdfDoc.getPdfObject(6);
        filterObject.getIndirectReference().setState(PdfObject.MUST_BE_FLUSHED);

        // The image stream will be marked as MUST_BE_FLUSHED after page is flushed.
        pdfDoc.getFirstPage().getPdfObject().getIndirectReference().setState(PdfObject.MUST_BE_FLUSHED);

        // There was a NPE because FlateFilter was already flushed.
        writer.flushWaitingObjects(Collections.<PdfIndirectReference>emptySet());
        // There also was a NPE because FlateFilter was already flushed.
        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FILTER_WAS_ALREADY_FLUSHED, logLevel =
                    LogLevelConstants.INFO)
    })
    @Test
    public void decodeParamsFlushedBeforeStreamTest() throws IOException, InterruptedException {
        String file = sourceFolder + "decodeParamsTest.pdf";
        String cmpFile = sourceFolder + "cmp_decodeParamsTest.pdf";
        String destFile = destinationFolder + "decodeParamsTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(file), CompareTool.createTestPdfWriter(destFile));
        PdfStream stream = (PdfStream) doc.getPdfObject(7);
        stream.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
        //Simulating that this flush happened automatically before normal stream flushing in close method
        stream.get(PdfName.DecodeParms).makeIndirect(stream.getIndirectReference().getDocument()).flush();
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FILTER_WAS_ALREADY_FLUSHED, logLevel =
                    LogLevelConstants.INFO)
    })
    @Test
    public void decodeParamsPredictorFlushedBeforeStreamTest() throws IOException, InterruptedException {
        String file = sourceFolder + "decodeParamsTest.pdf";
        String cmpFile = sourceFolder + "cmp_decodeParamsPredictorTest.pdf";
        String destFile = destinationFolder + "decodeParamsPredictorTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(file), CompareTool.createTestPdfWriter(destFile));
        PdfStream stream = (PdfStream) doc.getPdfObject(7);
        stream.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
        //Simulating that this flush happened automatically before normal stream flushing in close method
        ((PdfDictionary)stream.get(PdfName.DecodeParms)).get(PdfName.Predictor).makeIndirect(stream.getIndirectReference()
                .getDocument()).flush();
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FILTER_WAS_ALREADY_FLUSHED, logLevel =
                    LogLevelConstants.INFO)
    })
    @Test
    public void decodeParamsColumnsFlushedBeforeStreamTest() throws IOException, InterruptedException {
        String file = sourceFolder + "decodeParamsTest.pdf";
        String cmpFile = sourceFolder + "cmp_decodeParamsColumnsTest.pdf";
        String destFile = destinationFolder + "decodeParamsColumnsTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(file), CompareTool.createTestPdfWriter(destFile));
        PdfStream stream = (PdfStream) doc.getPdfObject(7);
        stream.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
        //Simulating that this flush happened automatically before normal stream flushing in close method
        ((PdfDictionary)stream.get(PdfName.DecodeParms)).get(PdfName.Columns).makeIndirect(stream.getIndirectReference()
                .getDocument()).flush();
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FILTER_WAS_ALREADY_FLUSHED, logLevel =
                    LogLevelConstants.INFO)
    })
    @Test
    public void decodeParamsColorsFlushedBeforeStreamTest() throws IOException, InterruptedException {
        String file = sourceFolder + "decodeParamsTest.pdf";
        String cmpFile = sourceFolder + "cmp_decodeParamsColorsTest.pdf";
        String destFile = destinationFolder + "decodeParamsColorsTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(file), CompareTool.createTestPdfWriter(destFile));
        PdfStream stream = (PdfStream) doc.getPdfObject(7);
        stream.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
        //Simulating that this flush happened automatically before normal stream flushing in close method
        ((PdfDictionary)stream.get(PdfName.DecodeParms)).get(PdfName.Colors).makeIndirect(stream.getIndirectReference()
                .getDocument()).flush();
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FILTER_WAS_ALREADY_FLUSHED, logLevel =
                    LogLevelConstants.INFO)
    })
    @Test
    public void decodeParamsBitsPerComponentFlushedBeforeStreamTest() throws IOException, InterruptedException {
        String file = sourceFolder + "decodeParamsTest.pdf";
        String cmpFile = sourceFolder + "cmp_decodeParamsBitsPerComponentTest.pdf";
        String destFile = destinationFolder + "decodeParamsBitsPerComponentTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(file), CompareTool.createTestPdfWriter(destFile));
        PdfStream stream = (PdfStream) doc.getPdfObject(7);
        stream.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
        //Simulating that this flush happened automatically before normal stream flushing in close method
        ((PdfDictionary)stream.get(PdfName.DecodeParms)).get(PdfName.BitsPerComponent).makeIndirect(stream.getIndirectReference()
                .getDocument()).flush();
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FILTER_WAS_ALREADY_FLUSHED, count = 2, logLevel =
                    LogLevelConstants.INFO)
    })
    @Test
    public void flateFilterFlushedWhileDecodeTest() throws IOException, InterruptedException {
        String file = sourceFolder + "decodeParamsTest.pdf";
        String cmpFile = sourceFolder + "cmp_flateFilterFlushedWhileDecodeTest.pdf";
        String destFile = destinationFolder + "flateFilterFlushedWhileDecodeTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(file), CompareTool.createTestPdfWriter(destFile));
        PdfStream stream = (PdfStream) doc.getPdfObject(7);
        stream.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
        stream.remove(PdfName.Filter);
        stream.put(PdfName.Filter, new PdfName(PdfName.FlateDecode.value));
        //Simulating that this flush happened automatically before normal stream flushing in close method
        stream.get(PdfName.Filter).makeIndirect(stream.getIndirectReference().getDocument()).flush();
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FILTER_WAS_ALREADY_FLUSHED, count = 2, logLevel =
                    LogLevelConstants.INFO)
    })
    @Test
    public void arrayFlateFilterFlushedWhileDecodeTest() throws IOException, InterruptedException {
        String file = sourceFolder + "decodeParamsTest.pdf";
        String cmpFile = sourceFolder + "cmp_arrayFlateFilterFlushedWhileDecodeTest.pdf";
        String destFile = destinationFolder + "arrayFlateFilterFlushedWhileDecodeTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(file), CompareTool.createTestPdfWriter(destFile));
        PdfStream stream = (PdfStream) doc.getPdfObject(7);
        stream.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
        stream.remove(PdfName.Filter);
        stream.put(PdfName.Filter, new PdfArray(new PdfName(PdfName.FlateDecode.value)));
        //Simulating that this flush happened automatically before normal stream flushing in close method
        stream.get(PdfName.Filter).makeIndirect(stream.getIndirectReference().getDocument()).flush();
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder));
    }
}
