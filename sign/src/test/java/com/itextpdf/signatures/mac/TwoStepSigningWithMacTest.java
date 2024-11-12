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
import com.itextpdf.kernel.mac.IMacContainerLocator;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.ExternalBlankSignatureContainer;
import com.itextpdf.signatures.PKCS7ExternalSignatureContainer;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfTwoPhaseSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleIntegrationTest")
public class TwoStepSigningWithMacTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/mac/TwoStepSigningWithMacTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/mac/TwoStepSigningWithMacTest/";
    private static final byte[] ENCRYPTION_PASSWORD = "123".getBytes();
    private static final char[] PRIVATE_KEY_PASSWORD = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void signDeferredWithReaderTest() throws Exception {
        String fileName = "signDeferredWithReaderTest.pdf";
        String srcFileName = SOURCE_FOLDER + "macEncryptedDoc.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + "signCertRsa01.pem";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);
        ReaderProperties properties = new ReaderProperties().setPassword(ENCRYPTION_PASSWORD);

        ByteArrayOutputStream preparedDocument = new ByteArrayOutputStream();
        try (PdfReader reader = new PdfReader(srcFileName, properties);
                OutputStream outputStream = preparedDocument) {
            PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties());
            pdfSigner.signExternalContainer(new ExternalBlankSignatureContainer(
                    PdfName.Adobe_PPKLite, PdfName.Adbe_pkcs7_detached), 5000);
        }

        try (PdfReader reader = new PdfReader(new ByteArrayInputStream(preparedDocument.toByteArray()), properties);
                OutputStream outputStream = FileUtil.getFileOutputStream(outputFileName)) {
            PdfSigner.signDeferred(reader, "Signature1", outputStream,
                    new PKCS7ExternalSignatureContainer(signRsaPrivateKey, signRsaChain, "SHA-512"));
        }

        Assertions.assertNull(
                SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
    }

    @Test
    public void signDeferredWithDocumentTest() throws Exception {
        String fileName = "signDeferredWithDocumentTest.pdf";
        String srcFileName = SOURCE_FOLDER + "macEncryptedDoc.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + "signCertRsa01.pem";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);
        ReaderProperties properties = new ReaderProperties().setPassword(ENCRYPTION_PASSWORD);

        ByteArrayOutputStream preparedDocument = new ByteArrayOutputStream();
        try (PdfReader reader = new PdfReader(srcFileName, properties);
                OutputStream outputStream = preparedDocument) {
            PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties());
            pdfSigner.signExternalContainer(new ExternalBlankSignatureContainer(
                    PdfName.Adobe_PPKLite, PdfName.Adbe_pkcs7_detached), 5000);
        }

        try (PdfDocument document = new PdfDocument(new PdfReader(new ByteArrayInputStream(preparedDocument.toByteArray()), properties));
                OutputStream outputStream = FileUtil.getFileOutputStream(outputFileName)) {
            PdfSigner.signDeferred(document, "Signature1", outputStream,
                    new PKCS7ExternalSignatureContainer(signRsaPrivateKey, signRsaChain, "SHA-512"));
        }

        Assertions.assertNull(
                SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
    }

    @Test
    public void twoPhaseSignerWithReaderTest() throws Exception {
        String fileName = "twoPhaseSignerWithReaderTest.pdf";
        String srcFileName = SOURCE_FOLDER + "macEncryptedDoc.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + "signCertRsa01.pem";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);
        ReaderProperties properties = new ReaderProperties().setPassword(ENCRYPTION_PASSWORD);

        try (PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(srcFileName), properties);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfTwoPhaseSigner signer = new PdfTwoPhaseSigner(reader, outputStream);

            SignerProperties signerProperties = new SignerProperties();
            byte[] digest = signer.prepareDocumentForSignature(signerProperties, "SHA-512", PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, 5000, false);

            String fieldName = signerProperties.getFieldName();
            byte[] signData = signDigest(digest, signRsaChain, signRsaPrivateKey);

            try (OutputStream outputStreamPhase2 = FileUtil.getFileOutputStream(outputFileName);
                    PdfReader newReader = new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()), properties)) {
                PdfTwoPhaseSigner.addSignatureToPreparedDocument(newReader, fieldName, outputStreamPhase2, signData);
            }
        }

        Assertions.assertNull(
                SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
    }

    @Test
    public void twoPhaseSignerWithDocumentTest() throws Exception {
        String fileName = "twoPhaseSignerWithDocumentTest.pdf";
        String srcFileName = SOURCE_FOLDER + "macEncryptedDoc.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String signCertFileName = CERTS_SRC + "signCertRsa01.pem";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PRIVATE_KEY_PASSWORD);
        ReaderProperties properties = new ReaderProperties().setPassword(ENCRYPTION_PASSWORD);

        try (PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(srcFileName), properties);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfTwoPhaseSigner signer = new PdfTwoPhaseSigner(reader, outputStream);

            SignerProperties signerProperties = new SignerProperties();
            byte[] digest = signer.prepareDocumentForSignature(signerProperties, "SHA-512", PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, 5000, false);

            String fieldName = signerProperties.getFieldName();
            byte[] signData = signDigest(digest, signRsaChain, signRsaPrivateKey);

            try (OutputStream outputStreamPhase2 = FileUtil.getFileOutputStream(outputFileName);
                    PdfDocument document = new PdfDocument(new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()), properties))) {
                PdfTwoPhaseSigner.addSignatureToPreparedDocument(document, fieldName, outputStreamPhase2, signData);
            }
        }

        Assertions.assertNull(
                SignaturesCompareTool.compareSignatures(outputFileName, cmpFileName, properties, properties));
    }

    private byte[] signDigest(byte[] data, Certificate[] chain, PrivateKey pk) throws GeneralSecurityException {
        PdfPKCS7 sgn = new PdfPKCS7((PrivateKey) null, chain, "SHA-512", null, new BouncyCastleDigest(), false);
        byte[] sh = sgn.getAuthenticatedAttributeBytes(data, PdfSigner.CryptoStandard.CMS, null, null);

        PrivateKeySignature pkSign = new PrivateKeySignature(pk, "SHA-512",
                BouncyCastleFactoryCreator.getFactory().getProviderName());
        byte[] signData = pkSign.sign(sh);

        sgn.setExternalSignatureValue(signData, null, pkSign.getSignatureAlgorithmName(), pkSign.getSignatureMechanismParameters());

        return sgn.getEncodedPKCS7(data, PdfSigner.CryptoStandard.CMS, null, null, null);
    }
}
