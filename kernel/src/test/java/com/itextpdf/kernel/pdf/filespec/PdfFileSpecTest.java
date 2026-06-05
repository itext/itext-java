package com.itextpdf.kernel.pdf.filespec;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.InputStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfFileSpecTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/kernel/pdf/filespec/PdfFileSpecTest/";
    private static final String DESTINATION_FOLDER =
            TestUtil.getOutputPath() + "/kernel/pdf/filespec/PdfFileSpecTest/";

    private static final int DUPLICATE_ADDS = 2;
    // 1MB payload
    private static final int PAYLOAD_BYTES = 1024 * 1024;

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.NAME_ALREADY_EXISTS_IN_THE_NAME_TREE,
            count = 1))
    public void createEmbeddedFileSpecWithByteArraysTest() throws Exception {
        String filename = DESTINATION_FOLDER + "byteArrays.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename))) {
            pdfDoc.addNewPage();

            for (int i = 0; i < DUPLICATE_ADDS; ++i) {
                byte[] payload = new byte[PAYLOAD_BYTES];
                // Make first byte indexed so each payload differs (rules out content-based dedup).
                payload[0] = (byte) (i & 0xFF);

                PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(
                        pdfDoc,
                        payload,
                        "Iteration " + i,
                        "attachment-bytes.bin",
                        new PdfName("application/octet-stream"),
                        null,
                        PdfName.Data
                );
                pdfDoc.addFileAttachment("attachment-bytes.bin", spec);
            }
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(filename, SOURCE_FOLDER + "cmp_byteArrays.pdf",
                        DESTINATION_FOLDER,
                        "diff_"));
    }

    @Test
    public void createEmbeddedFileSpecWithFilePathTest() throws Exception {
        String filename = DESTINATION_FOLDER + "filePaths.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename))) {
            pdfDoc.addNewPage();

            PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(
                    pdfDoc,
                    SOURCE_FOLDER + "attachment-64.txt",
                    "FileSpec Stream Closing Test",
                    "attachment-64.txt",
                    new PdfName("application/octet-stream"),
                    null,
                    PdfName.Data
            );
            pdfDoc.addFileAttachment("attachment-64.txt", spec);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(filename, SOURCE_FOLDER + "cmp_attachment-64.pdf",
                        DESTINATION_FOLDER,
                        "diff_"));
    }

    @Test
    public void createEmbeddedFileSpecWithStreamClosingTest() throws Exception {
        String filename = DESTINATION_FOLDER + "streamClosing.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename))) {
            pdfDoc.addNewPage();

            try (InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "attachment-64.txt")) {
                PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(
                        pdfDoc,
                        is,
                        "FileSpec Stream Closing Test",
                        "attachment-64.txt",
                        new PdfName("application/octet-stream"),
                        null,
                        PdfName.Data
                );
                pdfDoc.addFileAttachment("attachment-64.txt", spec);
            }
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(filename, SOURCE_FOLDER + "cmp_attachment-64.pdf",
                        DESTINATION_FOLDER,
                        "diff_"));
    }
}
