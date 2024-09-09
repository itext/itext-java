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
package com.itextpdf.kernel.mac;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.mac.MacProperties.MacDigestAlgorithm;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleIntegrationTest")
public class MacIntegrityProtectorReadingAndRewritingTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/mac/MacIntegrityProtectorReadingAndRewritingTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/mac/MacIntegrityProtectorReadingAndRewritingTest/";
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/kernel/mac/MacIntegrityProtectorReadingAndRewritingTest/certs/";
    private static final byte[] PASSWORD = "123".getBytes();
    private static final String PROVIDER_NAME = BouncyCastleFactoryCreator.getFactory().getProviderName();

    @BeforeAll
    public static void beforeClass() {
        Assumptions.assumeTrue("BC".equals(PROVIDER_NAME));
        createOrClearDestinationFolder(DESTINATION_FOLDER);
        Security.addProvider(BouncyCastleFactoryCreator.getFactory().getProvider());
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void appendModeTest() throws IOException, InterruptedException {
        String fileName = "appendModeTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "macProtectedDocument.pdf",
                new ReaderProperties().setPassword(PASSWORD)), CompareTool.createTestPdfWriter(outputFileName),
                new StampingProperties().useAppendMode())) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        Assertions.assertNull(new CompareTool().enableEncryptionCompare().compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
    }

    @Test
    public void preserveEncryptionTest() throws IOException, InterruptedException {
        String fileName = "preserveEncryptionTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        try (PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "macProtectedDocument.pdf", new ReaderProperties().setPassword(PASSWORD)),
                CompareTool.createTestPdfWriter(outputFileName),
                new StampingProperties().preserveEncryption())) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        Assertions.assertNull(new CompareTool().enableEncryptionCompare().compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
    }

    @Test
    public void writerPropertiesTest() throws IOException, InterruptedException {
        String fileName = "writerPropertiesTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        MacProperties macProperties = new MacProperties(MacDigestAlgorithm.SHA_512);
        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_256, macProperties);

        try (PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "macProtectedDocument.pdf", new ReaderProperties().setPassword(PASSWORD)),
                CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        Assertions.assertNull(new CompareTool().compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
    }

    @Test
    public void macShouldNotBePreservedWithEncryptionTest() throws IOException, InterruptedException {
        String fileName = "macShouldNotBePreservedWithEncryptionTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_1_7)
                .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_128);
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "macProtectedDocument.pdf",
                new ReaderProperties().setPassword(PASSWORD)), CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        Assertions.assertNull(new CompareTool().enableEncryptionCompare().compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
    }

    @Test
    public void macShouldNotBePreservedTest() throws IOException, InterruptedException {
        String fileName = "macShouldNotBePreservedTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        try (PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "macProtectedDocument.pdf", new ReaderProperties().setPassword(PASSWORD)),
                CompareTool.createTestPdfWriter(outputFileName))) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        Assertions.assertNull(new CompareTool().compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
    }

    @Test
    public void invalidMacTokenTest() {
        String fileName = "invalidMacTokenTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;

        String exceptionMessage = Assertions.assertThrows(PdfException.class, () -> {
            try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "invalidMacProtectedDocument.pdf",
                    new ReaderProperties().setPassword(PASSWORD)), CompareTool.createTestPdfWriter(outputFileName))) {
                pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
            }
        }).getMessage();
        Assertions.assertEquals(KernelExceptionMessageConstant.MAC_VALIDATION_FAILED, exceptionMessage);
    }

    @Test
    public void invalidPublicKeyMacProtectedDocumentTest() throws Exception {
        String fileName = "invalidPublicKeyMacProtectedDocumentTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;

        Certificate certificate = CryptoUtil.readPublicCertificate(
                FileUtil.getInputStreamForFile(CERTS_SRC + "SHA256withRSA.cer"));
        PrivateKey privateKey = MacIntegrityProtectorCreationTest.getPrivateKey(CERTS_SRC + "SHA256withRSA.key");
        String exceptionMessage = Assertions.assertThrows(PdfException.class, () -> {
            try (PdfDocument pdfDoc = new PdfDocument(
                    new PdfReader(SOURCE_FOLDER + "invalidPublicKeyMacProtectedDocument.pdf",
                            new ReaderProperties().setPublicKeySecurityParams(certificate, privateKey, PROVIDER_NAME,
                                    null)), CompareTool.createTestPdfWriter(outputFileName))) {
                pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
            }
        }).getMessage();
        Assertions.assertEquals(KernelExceptionMessageConstant.MAC_VALIDATION_FAILED,
                exceptionMessage);
    }

    @Test
    public void readThirdPartyMacProtectedDocumentTest() {
        AssertUtil.doesNotThrow(() -> {
            try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "thirdPartyMacProtectedDocument.pdf",
                    new ReaderProperties().setPassword(PASSWORD)))) {
            }
        });
    }

    @Test
    public void readThirdPartyPublicKeyMacProtectedDocumentTest() throws Exception {
        PrivateKey privateKey = MacIntegrityProtectorCreationTest.getPrivateKey(CERTS_SRC + "keyForEncryption.pem");
        Certificate certificate = CryptoUtil.readPublicCertificate(
                FileUtil.getInputStreamForFile(CERTS_SRC + "certForEncryption.crt"));
        AssertUtil.doesNotThrow(() -> {
            try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "thirdPartyPublicKeyMacProtectedDocument.pdf",
                    new ReaderProperties().setPublicKeySecurityParams(certificate, privateKey, PROVIDER_NAME, null)))) {
            }
        });
    }

    @Test
    public void readMacProtectedPdf1_7() {
        AssertUtil.doesNotThrow(() -> {
            try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "macProtectedDocumentPdf1_7.pdf",
                    new ReaderProperties().setPassword(PASSWORD)))) {
            }
        });
    }
}
