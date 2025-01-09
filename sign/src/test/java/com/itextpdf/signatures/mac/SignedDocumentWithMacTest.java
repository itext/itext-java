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
package com.itextpdf.signatures.mac;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.ExternalBlankSignatureContainer;
import com.itextpdf.signatures.PKCS7ExternalSignatureContainer;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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

    public static Iterable<Object[]> createParameters() {
        return Arrays.asList(new Object[] {"signCertRsa01.pem", "signDetached"},
                new Object[] {"tsaCert.pem", "timestamping"},
                new Object[] {"signCertRsa01.pem", "signExternalContainerReal"},
                new Object[] {"signCertRsa01.pem", "signExternalContainerBlank"});
    }

    @ParameterizedTest(name = "Signing operation: {1}")
    @MethodSource("createParameters")
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void signMacProtectedDocTest(String certName, String signingOperation) throws Exception {
        String fileName = "signMacProtectedDocTest_" + signingOperation + ".pdf";
        String srcFileName = SOURCE_FOLDER + "macEncryptedDoc.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + certName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);

        try (PdfReader reader = new PdfReader(srcFileName, new ReaderProperties().setPassword(ENCRYPTION_PASSWORD));
                OutputStream outputStream = FileUtil.getFileOutputStream(outputFileName)) {
            PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties());
            performSigningOperation(signingOperation, pdfSigner, signRsaPrivateKey, signRsaChain);
        }

        ReaderProperties properties = new ReaderProperties().setPassword(ENCRYPTION_PASSWORD);
        Assertions.assertNull(
                SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
    }

    @ParameterizedTest(name = "Signing operation: {1}")
    @MethodSource("createParameters")
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void signNotMacProtectedDocTest(String certName, String signingOperation) throws Exception {
        String fileName = "signNotMacProtectedDocTest_" + signingOperation + ".pdf";
        String srcFileName = SOURCE_FOLDER + "noMacProtectionDocument.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + certName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);

        try (PdfReader reader = new PdfReader(srcFileName, new ReaderProperties().setPassword(ENCRYPTION_PASSWORD));
                OutputStream outputStream = FileUtil.getFileOutputStream(outputFileName)) {
            PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties());
            performSigningOperation(signingOperation, pdfSigner, signRsaPrivateKey, signRsaChain);
        }

        ReaderProperties properties = new ReaderProperties().setPassword(ENCRYPTION_PASSWORD);
        Assertions.assertNull(
                SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
    }

    @ParameterizedTest(name = "Signing operation: {1}")
    @MethodSource("createParameters")
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void signNotMacProtectedDoc17Test(String certName, String signingOperation) throws Exception {
        String fileName = "signNotMacProtectedDoc17Test_" + signingOperation + ".pdf";
        String srcFileName = SOURCE_FOLDER + "noMacProtectionDocument_1_7.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + certName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);

        try (PdfReader reader = new PdfReader(srcFileName, new ReaderProperties().setPassword(ENCRYPTION_PASSWORD));
                OutputStream outputStream = FileUtil.getFileOutputStream(outputFileName)) {
            PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties());
            performSigningOperation(signingOperation, pdfSigner, signRsaPrivateKey, signRsaChain);
        }

        if (!signingOperation.equals("signExternalContainerBlank")) {
            ReaderProperties properties = new ReaderProperties().setPassword(ENCRYPTION_PASSWORD);
            Assertions.assertNull(
                    SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
        }
    }

    @ParameterizedTest(name = "Signing operation: {1}")
    @MethodSource("createParameters")
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void signNotMacProtectedDocInAppendModeTest(String certName, String signingOperation) throws Exception {
        // MAC should not be added in append mode
        String fileName = "signNotMacProtectedDocInAppendModeTest_" + signingOperation + ".pdf";
        String srcFileName = SOURCE_FOLDER + "noMacProtectionDocument.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + certName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);

        try (PdfReader reader = new PdfReader(srcFileName, new ReaderProperties().setPassword(ENCRYPTION_PASSWORD));
                OutputStream outputStream = FileUtil.getFileOutputStream(outputFileName)) {
            PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties().useAppendMode());
            performSigningOperation(signingOperation, pdfSigner, signRsaPrivateKey, signRsaChain);
        }

        if (!signingOperation.equals("signExternalContainerBlank")) {
            ReaderProperties properties = new ReaderProperties().setPassword(ENCRYPTION_PASSWORD);
            Assertions.assertNull(
                    SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
        }
    }

    @ParameterizedTest(name = "Signing operation: {1}")
    @MethodSource("createParameters")
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void signMacProtectedDocInAppendModeTest(String certName, String signingOperation) throws Exception {
        String fileName = "signMacProtectedDocInAppendModeTest_" + signingOperation + ".pdf";
        String srcFileName = SOURCE_FOLDER + "macEncryptedDoc.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + certName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);

        try (PdfReader reader = new PdfReader(srcFileName, new ReaderProperties().setPassword(ENCRYPTION_PASSWORD));
                OutputStream outputStream = FileUtil.getFileOutputStream(outputFileName)) {
            PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties().useAppendMode());
            performSigningOperation(signingOperation, pdfSigner, signRsaPrivateKey, signRsaChain);
        }

        ReaderProperties properties = new ReaderProperties().setPassword(ENCRYPTION_PASSWORD);
        Assertions.assertNull(
                SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
    }

    @ParameterizedTest(name = "Signing operation: {1}")
    @MethodSource("createParameters")
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void signMacProtectedDocWithSHA3_384Test(String certName, String signingOperation) throws Exception {
        String fileName = "signMacProtectedDocWithSHA3_384Test_" + signingOperation + ".pdf";
        String srcFileName = SOURCE_FOLDER + "macEncryptedDocSHA3_384.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + certName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);

        try (PdfReader reader = new PdfReader(srcFileName, new ReaderProperties().setPassword(ENCRYPTION_PASSWORD));
                OutputStream outputStream = FileUtil.getFileOutputStream(outputFileName)) {
            PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties());
            performSigningOperation(signingOperation, pdfSigner, signRsaPrivateKey, signRsaChain);
        }

        ReaderProperties properties = new ReaderProperties().setPassword(ENCRYPTION_PASSWORD);
        Assertions.assertNull(
                SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
    }

    @ParameterizedTest(name = "Signing operation: {1}")
    @MethodSource("createParameters")
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void signMacPublicEncryptionDocTest(String certName, String signingOperation) throws Exception {
        try {
            BouncyCastleFactoryCreator.getFactory().isEncryptionFeatureSupported(0, true);
        } catch (Exception ignored) {
            Assumptions.assumeTrue(false);
        }
        String fileName = "signMacPublicEncryptionDocTest_" + signingOperation + ".pdf";
        String srcFileName = SOURCE_FOLDER + "macEncryptedWithPublicHandlerDoc.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + certName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);
        Certificate certificate = CryptoUtil.readPublicCertificate(FileUtil.getInputStreamForFile(CERTS_SRC + "SHA256withRSA.cer"));
        PrivateKey privateKey = PemFileHelper.readFirstKey(CERTS_SRC + "SHA256withRSA.key", PRIVATE_KEY_PASSWORD);
        ReaderProperties properties = new ReaderProperties().setPublicKeySecurityParams(certificate, privateKey, FACTORY.getProviderName(), null);

        try (PdfReader reader = new PdfReader(srcFileName, properties);
                OutputStream outputStream = FileUtil.getFileOutputStream(outputFileName)) {
            PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties());
            performSigningOperation(signingOperation, pdfSigner, signRsaPrivateKey, signRsaChain);
        }

        Assertions.assertNull(
                SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
    }

    private static void performSigningOperation(String signingOperation, PdfSigner pdfSigner, PrivateKey privateKey, Certificate[] chain)
            throws Exception {
        switch (signingOperation) {
            case "signDetached":
                performSignDetached(pdfSigner, privateKey, chain);
                break;
            case "timestamping":
                performTimestamping(pdfSigner, privateKey, chain);
                break;
            case "signExternalContainerReal":
                performSignExternalContainerReal(pdfSigner, privateKey, chain);
                break;
            case "signExternalContainerBlank":
                performSignExternalContainerBlank(pdfSigner);
                break;
        }
    }

    private static void performSignDetached(PdfSigner pdfSigner, PrivateKey privateKey, Certificate[] chain) throws Exception {
        pdfSigner.signDetached(
                new PrivateKeySignature(privateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName()),
                chain, null, null, null, 0, CryptoStandard.CADES);
    }

    private static void performSignExternalContainerReal(PdfSigner pdfSigner, PrivateKey privateKey, Certificate[] chain)
            throws GeneralSecurityException, IOException {
        pdfSigner.signExternalContainer(new PKCS7ExternalSignatureContainer(privateKey, chain, "SHA-512"), 5000);
    }

    private static void performSignExternalContainerBlank(PdfSigner pdfSigner)
            throws GeneralSecurityException, IOException {
        pdfSigner.signExternalContainer(new ExternalBlankSignatureContainer(PdfName.Adobe_PPKLite, PdfName.Adbe_pkcs7_detached), 5000);
    }

    private static void performTimestamping(PdfSigner pdfSigner, PrivateKey privateKey, Certificate[] chain)
            throws GeneralSecurityException, IOException {
        pdfSigner.timestamp(new TestTsaClient(Arrays.asList(chain), privateKey), "timestamp1");
    }
}
