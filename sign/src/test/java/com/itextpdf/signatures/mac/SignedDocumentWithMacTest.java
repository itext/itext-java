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
package com.itextpdf.signatures.mac;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleIntegrationTest")
public class SignedDocumentWithMacTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/mac/SignedDocumentWithMacTest/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/mac/SignedDocumentWithMacTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/mac/SignedDocumentWithMacTest/";
    private static final byte[] ENCRYPTION_PASSWORD = "123".getBytes();
    private static final char[] PRIVATE_KEY_PASSWORD = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void signMacProtectedDocTest() throws Exception {
        String fileName = "signMacProtectedDocTest.pdf";
        String srcFileName = SOURCE_FOLDER + "macEncryptedDoc.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + "signCertRsa01.pem";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);

        try (PdfReader reader = new PdfReader(srcFileName, new ReaderProperties().setPassword(ENCRYPTION_PASSWORD));
                OutputStream outputStream = FileUtil.getFileOutputStream(outputFileName)) {
            PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties());
            performSignDetached(pdfSigner, signRsaPrivateKey, signRsaChain);
        }

        ReaderProperties properties = new ReaderProperties().setPassword(ENCRYPTION_PASSWORD);
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void signMacProtectedDocInAppendModeTest() throws Exception {
        String fileName = "signMacProtectedDocInAppendModeTest.pdf";
        String srcFileName = SOURCE_FOLDER + "macEncryptedDoc.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + "signCertRsa01.pem";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);

        try (PdfReader reader = new PdfReader(srcFileName, new ReaderProperties().setPassword(ENCRYPTION_PASSWORD));
                OutputStream outputStream = FileUtil.getFileOutputStream(outputFileName)) {
            PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties().useAppendMode());
            performSignDetached(pdfSigner, signRsaPrivateKey, signRsaChain);
        }

        ReaderProperties properties = new ReaderProperties().setPassword(ENCRYPTION_PASSWORD);
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void signMacProtectedDocWithSHA3_384Test() throws Exception {
        String fileName = "signMacProtectedDocWithSHA3_384Test.pdf";
        String srcFileName = SOURCE_FOLDER + "macEncryptedDocSHA3_384.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + "signCertRsa01.pem";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);

        try (PdfReader reader = new PdfReader(srcFileName, new ReaderProperties().setPassword(ENCRYPTION_PASSWORD));
                OutputStream outputStream = FileUtil.getFileOutputStream(outputFileName)) {
            PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties());
            performSignDetached(pdfSigner, signRsaPrivateKey, signRsaChain);
        }

        ReaderProperties properties = new ReaderProperties().setPassword(ENCRYPTION_PASSWORD);
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void signMacPublicEncryptionDocTest() throws Exception {
        try {
            BouncyCastleFactoryCreator.getFactory().isEncryptionFeatureSupported(0, true);
        } catch (Exception ignored) {
            Assumptions.assumeTrue(false);
        }
        String fileName = "signMacPublicEncryptionDocTest.pdf";
        String srcFileName = SOURCE_FOLDER + "macEncryptedWithPublicHandlerDoc.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + "signCertRsa01.pem";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);
        Certificate certificate = CryptoUtil.readPublicCertificate(FileUtil.getInputStreamForFile(CERTS_SRC + "SHA256withRSA.cer"));
        PrivateKey privateKey = PemFileHelper.readFirstKey(CERTS_SRC + "SHA256withRSA.key", PRIVATE_KEY_PASSWORD);
        ReaderProperties properties = new ReaderProperties().setPublicKeySecurityParams(certificate, privateKey, FACTORY.getProviderName(), null);

        try (PdfReader reader = new PdfReader(srcFileName, properties);
                OutputStream outputStream = FileUtil.getFileOutputStream(outputFileName)) {
            PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties());
            performSignDetached(pdfSigner, signRsaPrivateKey, signRsaChain);
        }

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void readSignedMacProtectedInvalidDocTest() {
        String srcFileName = SOURCE_FOLDER + "signedMacProtectedInvalidDoc.pdf";

        String exceptionMessage = Assertions.assertThrows(PdfException.class, () -> {
            try (PdfDocument ignored = new PdfDocument(
                    new PdfReader(srcFileName, new ReaderProperties().setPassword(ENCRYPTION_PASSWORD)))) {
                // Do nothing.
            }
        }).getMessage();
        Assertions.assertEquals(KernelExceptionMessageConstant.MAC_VALIDATION_FAILED, exceptionMessage);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void updateSignedMacProtectedDocumentTest() throws Exception {
        String fileName = "updateSignedMacProtectedDocumentTest.pdf";
        String srcFileName = SOURCE_FOLDER + "thirdPartyMacProtectedAndSignedDocument.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        try (PdfDocument ignored = new PdfDocument(
                new PdfReader(srcFileName, new ReaderProperties().setPassword(ENCRYPTION_PASSWORD)),
                new PdfWriter(FileUtil.getFileOutputStream(outputFileName)),
                new StampingProperties().useAppendMode())) {
            // Do nothing.
        }

        // This call produces INFO log from AESCipher caused by exception while decrypting. The reason is that,
        // while comparing encrypted signed documents, CompareTool needs to mark signature value as unencrypted.
        // Instead, it tries to decrypt not encrypted value which results in exception.
        Assertions.assertNull(new CompareTool().compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", ENCRYPTION_PASSWORD, ENCRYPTION_PASSWORD));
    }

    private static void performSignDetached(PdfSigner pdfSigner, PrivateKey privateKey, Certificate[] chain) throws Exception {
        pdfSigner.signDetached(
                new PrivateKeySignature(privateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName()),
                chain, null, null, null, 0, CryptoStandard.CADES);
    }
}
