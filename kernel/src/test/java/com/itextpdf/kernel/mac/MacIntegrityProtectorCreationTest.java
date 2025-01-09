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
package com.itextpdf.kernel.mac;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.EnumUtil;
import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.VersionConforming;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.mac.MacProperties.KeyWrappingAlgorithm;
import com.itextpdf.kernel.mac.MacProperties.MacAlgorithm;
import com.itextpdf.kernel.mac.MacProperties.MacDigestAlgorithm;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.utils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
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
public class MacIntegrityProtectorCreationTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/mac/MacIntegrityProtectorCreationTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/mac/MacIntegrityProtectorCreationTest/";
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/kernel/mac/MacIntegrityProtectorCreationTest/certs/";
    private static final byte[] PASSWORD = "123".getBytes();
    private static final String PROVIDER_NAME = BouncyCastleFactoryCreator.getFactory().getProviderName();

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
        Security.addProvider(BouncyCastleFactoryCreator.getFactory().getProvider());
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void standaloneMacStandardEncryptionTest() throws IOException, InterruptedException {
        String fileName = "standaloneMacStandardEncryptionTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_256,
                        new MacProperties(MacDigestAlgorithm.SHA_256));

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        Assertions.assertNull(new CompareTool().enableEncryptionCompare(false).compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
    }

    @Test
    public void noMacProtectionTest() throws IOException, InterruptedException {
        String fileName = "noMacProtectionTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_256, null);

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        Assertions.assertNull(new CompareTool().enableEncryptionCompare().compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void macEncryptionWithAesGcmTest() throws IOException, InterruptedException {
        String fileName = "macEncryptionWithAesGsmTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_GCM,
                        new MacProperties(MacDigestAlgorithm.SHA_256));

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        Assertions.assertNull(new CompareTool().enableEncryptionCompare(false).compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.PDF_WRITER_CLOSING_FAILED)
    })
    public void standaloneMacUnwritableStreamTest() throws IOException {
        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_256,
                        new MacProperties(MacDigestAlgorithm.SHA_256));
        ByteArrayOutputStream unwritableStream = new ByteArrayOutputStream() {
            @Override
            public void write(byte[] b, int off, int len) {
                throw new RuntimeException("expected");
            }
        };

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(unwritableStream, writerProperties))) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }

        unwritableStream.close();
    }

    @Test
    public void standaloneMacWithAllHashAlgorithmsTest() throws IOException, InterruptedException {
        for (int i = 0; i < EnumUtil.getAllValuesOfEnum(MacDigestAlgorithm.class).size(); i++) {
            String fileName = "standaloneMacWithAllHashAlgorithmsTest" + (i + 1) + ".pdf";
            String outputFileName = DESTINATION_FOLDER + fileName;
            String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

            MacProperties macProperties = new MacProperties(EnumUtil.getAllValuesOfEnum(MacDigestAlgorithm.class).get(i),
                    MacAlgorithm.HMAC_WITH_SHA_256, KeyWrappingAlgorithm.AES_256_NO_PADD);
            WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                    .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_256,
                            macProperties);

            try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
                pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
            }
            Assertions.assertNull(new CompareTool().enableEncryptionCompare(false).compareByContent(
                    outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
        }
    }

    @Test
    public void standaloneMacPdfVersionNotSetTest() {
        String fileName = "standaloneMacPdfVersionNotSetTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;

        MacProperties macProperties = new MacProperties(MacDigestAlgorithm.SHA_256, MacAlgorithm.HMAC_WITH_SHA_256,
                KeyWrappingAlgorithm.AES_256_NO_PADD);
        WriterProperties writerProperties = new WriterProperties()
                .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_256, macProperties);

        String exceptionMessage = Assertions.assertThrows(PdfException.class, () -> {
            try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
                pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
            }
        }).getMessage();
        Assertions.assertEquals(KernelExceptionMessageConstant.MAC_FOR_PDF_2, exceptionMessage);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void addMacOnPreserveEncryptionTest() throws IOException, InterruptedException {
        String fileName = "addMacOnPreserveEncryptionTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "noMacProtectionDocument.pdf",
                new ReaderProperties().setPassword(PASSWORD)),
                CompareTool.createTestPdfWriter(outputFileName, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                new StampingProperties().preserveEncryption())) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        Assertions.assertNull(new CompareTool().enableEncryptionCompare(false).compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void addMacOnAppendModeTest() throws IOException, InterruptedException {
        // MAC should not be added in append mode
        String fileName = "addMacOnAppendModeTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "noMacProtectionDocument.pdf",
                new ReaderProperties().setPassword(PASSWORD)),
                CompareTool.createTestPdfWriter(outputFileName, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                new StampingProperties().useAppendMode())) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        Assertions.assertNull(new CompareTool().enableEncryptionCompare().compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
    }

    @Test
    public void addMacWithDisableMacPropertyTest() throws IOException, InterruptedException {
        // MAC should not be added in disable MAC mode even if it was provided with writer properties
        String fileName = "addMacWithDisableMacPropertyTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        MacProperties macProperties = new MacProperties(MacDigestAlgorithm.SHA_384);
        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_256, macProperties);
        try (PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "noMacProtectionDocument.pdf", new ReaderProperties().setPassword(PASSWORD)),
                new PdfWriter(outputFileName, writerProperties), new StampingProperties().disableMac())) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        Assertions.assertNull(new CompareTool().enableEncryptionCompare().compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void addMacOnPreserveEncryptionWhileDowngradingTest() throws IOException, InterruptedException {
        String fileName = "addMacOnPreserveEncryptionWhileDowngradingTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "noMacProtectionDocument.pdf",
                new ReaderProperties().setPassword(PASSWORD)),
                CompareTool.createTestPdfWriter(outputFileName, new WriterProperties().setPdfVersion(PdfVersion.PDF_1_7)),
                new StampingProperties().preserveEncryption())) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        Assertions.assertNull(new CompareTool().enableEncryptionCompare().compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = VersionConforming.DEPRECATED_AES256_REVISION),
            @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT, ignore = true)})
    public void addMacOnPreserveEncryptionFor17DocTest() throws IOException, InterruptedException {
        // We can't embed MAC into encrypted documents during the conversion from earlier PDF version
        // because their encryption does not support this. So WriterProperties should be used iso preserveEncryption
        String fileName = "addMacOnPreserveEncryptionFor17DocTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "noMacProtectionDocument_1_7.pdf",
                new ReaderProperties().setPassword(PASSWORD)),
                CompareTool.createTestPdfWriter(outputFileName, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                new StampingProperties().preserveEncryption())) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        Assertions.assertNull(new CompareTool().enableEncryptionCompare().compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void standaloneMacOldEncryptionAlgorithmTest() {
        String fileName = "standaloneMacOldEncryptionAlgorithmTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;

        MacProperties macProperties = new MacProperties(MacDigestAlgorithm.SHA_256, MacAlgorithm.HMAC_WITH_SHA_256,
                KeyWrappingAlgorithm.AES_256_NO_PADD);
        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_128, macProperties);

        String exceptionMessage = Assertions.assertThrows(PdfException.class, () -> {
            try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
                pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
            }
        }).getMessage();
        Assertions.assertEquals(KernelExceptionMessageConstant.MAC_FOR_ENCRYPTION_5, exceptionMessage);
    }

    @Test
    public void standaloneMacPublicKeyEncryptionTest() throws Exception {
        try {
            BouncyCastleFactoryCreator.getFactory().isEncryptionFeatureSupported(0, true);
        } catch (Exception ignored) {
            Assumptions.assumeTrue(false);
        }
        Assumptions.assumeTrue(!BouncyCastleFactoryCreator.getFactory().isInApprovedOnlyMode());
        String fileName = "standaloneMacPublicKeyEncryptionTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate certificate = CryptoUtil.readPublicCertificate(FileUtil.getInputStreamForFile(CERTS_SRC + "SHA256withRSA.cer"));

        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                .setPublicKeyEncryption(new Certificate[] {certificate}, new int[] {-1}, EncryptionConstants.ENCRYPTION_AES_256,
                        new MacProperties(MacDigestAlgorithm.SHA_256));
        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }
        PrivateKey privateKey = getPrivateKey(CERTS_SRC + "SHA256withRSA.key");
        CompareTool compareTool = new CompareTool();
        compareTool.getCmpReaderProperties().setPublicKeySecurityParams(certificate, privateKey, PROVIDER_NAME, null);
        compareTool.getOutReaderProperties().setPublicKeySecurityParams(certificate, privateKey, PROVIDER_NAME, null);

        Assertions.assertNull(compareTool.compareByContent(outputFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    // TODO DEVSIX-8635 - Verify MAC permission and embed MAC in stamping mode for public key encryption
    public void addMacOnPreservePublicKeyEncryptionTest() throws Exception {
        try {
            BouncyCastleFactoryCreator.getFactory().isEncryptionFeatureSupported(0, true);
        } catch (Exception ignored) {
            Assumptions.assumeTrue(false);
        }

        String fileName = "addMacOnPreservePublicKeyEncryptionTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate certificate = CryptoUtil.readPublicCertificate(
                FileUtil.getInputStreamForFile(CERTS_SRC + "SHA256withRSA.cer"));
        PrivateKey privateKey = getPrivateKey(CERTS_SRC + "SHA256withRSA.key");
        ReaderProperties readerProperties = new ReaderProperties();
        readerProperties.setPublicKeySecurityParams(certificate, privateKey, PROVIDER_NAME, null);
        try (PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "noMacProtectionPublicKeyEncryptionDocument.pdf", readerProperties),
                CompareTool.createTestPdfWriter(outputFileName), new StampingProperties().preserveEncryption())) {
            pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
        }

        CompareTool compareTool = new CompareTool();
        compareTool.getCmpReaderProperties().setPublicKeySecurityParams(certificate, privateKey, PROVIDER_NAME, null);
        compareTool.getOutReaderProperties().setPublicKeySecurityParams(certificate, privateKey, PROVIDER_NAME, null);

        Assertions.assertNull(compareTool.compareByContent(outputFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    public static PrivateKey getPrivateKey(String keyName) throws IOException, AbstractPKCSException, AbstractOperatorCreationException {
        return PemFileHelper.readPrivateKeyFromPemFile(
                FileUtil.getInputStreamForFile(keyName), "testpassphrase".toCharArray());
    }
}
