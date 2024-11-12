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
package com.itextpdf.signatures.sign;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfTwoPhaseSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.cms.AlgorithmIdentifier;
import com.itextpdf.signatures.cms.CMSContainer;
import com.itextpdf.signatures.cms.SignerInfo;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleIntegrationTest")
public class TwoPhaseSigningTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/TwoPhaseSigningTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/TwoPhaseSigningTest/";
    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    private static final String SIMPLE_DOC_PATH = SOURCE_FOLDER + "SimpleDoc.pdf";

    private static final String DIGEST_ALGORITHM = DigestAlgorithms.SHA384;
    private static final String DIGEST_ALGORITHM_OID = OID.SHA_384;

    public static final String FIELD_NAME = "Signature1";

    private PrivateKey pk;
    private X509Certificate[] chain;

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void init()
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {
        pk = PemFileHelper.readFirstKey(CERTS_SRC + "signCertRsa01.pem", PASSWORD);
        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsa01.pem");
        chain = new X509Certificate[certChain.length];
        for (int i = 0; i < certChain.length; i++) {
            chain[i] = (X509Certificate) certChain[i];
        }
    }

    @Test
    public void testPreparationWithClosedPdfSigner() throws IOException, GeneralSecurityException {
        // prepare the file
        try (PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(SIMPLE_DOC_PATH));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfTwoPhaseSigner signer = new PdfTwoPhaseSigner(reader, outputStream);

            signer.prepareDocumentForSignature(new SignerProperties(), DIGEST_ALGORITHM, PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, 5000, false);

            Exception e = Assertions.assertThrows(PdfException.class, () -> {
                byte[] digest = signer.prepareDocumentForSignature(new SignerProperties(),DIGEST_ALGORITHM, PdfName.Adobe_PPKLite,
                        PdfName.Adbe_pkcs7_detached, 5000, false);
            });
            Assertions.assertEquals(SignExceptionMessageConstant.THIS_INSTANCE_OF_PDF_SIGNER_ALREADY_CLOSED, e.getMessage());
        }
    }

    @Test
    public void testCompletionWithWrongFieldName() throws IOException {
        byte[] signData = new byte[4096];
        // open prepared document
        try (PdfReader reader = new PdfReader(new File(SOURCE_FOLDER + "2PhasePreparedSignature.pdf"));
             OutputStream signedDoc = new ByteArrayOutputStream()) {
            // add signature
            Exception e = Assertions.assertThrows(PdfException.class, () ->
                    PdfTwoPhaseSigner.addSignatureToPreparedDocument(reader, "wrong" + FIELD_NAME, signedDoc, signData));

            Assertions.assertEquals(MessageFormatUtil.format(
                    SignExceptionMessageConstant.THERE_IS_NO_FIELD_IN_THE_DOCUMENT_WITH_SUCH_NAME,
                    "wrong" + FIELD_NAME), e.getMessage());
        }
    }

    @Test
    public void testCompletionWithWrongFieldNameAndDeprecatedApiTest() throws IOException {
        byte[] signData = new byte[4096];
        // open prepared document
        try (PdfDocument document = new PdfDocument(new PdfReader(new File(SOURCE_FOLDER + "2PhasePreparedSignature.pdf")));
                OutputStream signedDoc = new ByteArrayOutputStream()) {
            // add signature
            Exception e = Assertions.assertThrows(PdfException.class, () ->
                    PdfTwoPhaseSigner.addSignatureToPreparedDocument(document, "wrong" + FIELD_NAME, signedDoc, signData));

            Assertions.assertEquals(MessageFormatUtil.format(
                    SignExceptionMessageConstant.THERE_IS_NO_FIELD_IN_THE_DOCUMENT_WITH_SUCH_NAME,
                    "wrong" + FIELD_NAME), e.getMessage());
        }
    }

    @Test
    public void testCompletionWithNotEnoughSpace() throws IOException {
        byte[] signData = new byte[20000];
        // open prepared document
        try (PdfReader reader = new PdfReader(new File(SOURCE_FOLDER + "2PhasePreparedSignature.pdf"));
             OutputStream signedDoc = new ByteArrayOutputStream()) {
            // add signature
            Exception e = Assertions.assertThrows(PdfException.class, () ->
                    PdfTwoPhaseSigner.addSignatureToPreparedDocument(reader, FIELD_NAME, signedDoc, signData));

            Assertions.assertEquals(SignExceptionMessageConstant.AVAILABLE_SPACE_IS_NOT_ENOUGH_FOR_SIGNATURE,
                    e.getMessage());
        }
    }

    @Test
    public void testCompletionWithSignatureFieldNotLastOne() throws IOException, GeneralSecurityException {
        try (PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "2PhasePreparedSignature.pdf"));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfTwoPhaseSigner signer = new PdfTwoPhaseSigner(reader, outputStream);

            // Add second signature field
            byte[] digest = signer.prepareDocumentForSignature(new SignerProperties(), DIGEST_ALGORITHM, PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, 5000, false);

            byte[] signData = new byte[1024];
            try (OutputStream outputStreamPhase2 = FileUtil.getFileOutputStream(DESTINATION_FOLDER + "2PhaseCompleteCycle.pdf");
                    PdfReader newReader = new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()))) {

                Exception e = Assertions.assertThrows(PdfException.class, () ->
                        PdfTwoPhaseSigner.addSignatureToPreparedDocument(newReader, FIELD_NAME, outputStreamPhase2, signData));

                Assertions.assertEquals(MessageFormatUtil.format(SignExceptionMessageConstant.
                        SIGNATURE_WITH_THIS_NAME_IS_NOT_THE_LAST_IT_DOES_NOT_COVER_WHOLE_DOCUMENT, FIELD_NAME), e.getMessage());
            }
        }
    }

    @Test
    public void testPreparation() throws IOException, GeneralSecurityException {
        // prepare the file
        try (PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(SIMPLE_DOC_PATH));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfTwoPhaseSigner signer = new PdfTwoPhaseSigner(reader, outputStream);

            SignerProperties signerProperties = new SignerProperties();
            byte[] digest = signer.prepareDocumentForSignature(signerProperties, DIGEST_ALGORITHM, PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, 5000, false);

            String fieldName = signerProperties.getFieldName();


            try (PdfDocument cmp_document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "cmp_prepared.pdf"));
                 PdfDocument outDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(outputStream.toByteArray())))) {

                SignatureUtil signatureUtil = new SignatureUtil(cmp_document);
                PdfSignature cmpSignature = signatureUtil.getSignature(fieldName);

                signatureUtil = new SignatureUtil(outDocument);
                PdfSignature outSignature = signatureUtil.getSignature(fieldName);
                try {
                    Assertions.assertTrue(signatureUtil.signatureCoversWholeDocument(FIELD_NAME));
                    Assertions.assertArrayEquals(cmpSignature.getContents().getValueBytes(), outSignature.getContents().getValueBytes());
                } catch (Exception e) {
                    OutputStream fs = FileUtil.getFileOutputStream(DESTINATION_FOLDER + "testPreparation.pdf");
                    fs.write(outputStream.toByteArray());
                    fs.close();
                }
            }
        }
    }

    @Test
    public void testCompleteCycle() throws IOException, GeneralSecurityException {
        // Phase 1 prepare the document and get the documents digest and the fieldname of the created signature
        try (PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(SIMPLE_DOC_PATH));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfTwoPhaseSigner signer = new PdfTwoPhaseSigner(reader, outputStream);

            SignerProperties signerProperties = new SignerProperties();
            byte[] digest = signer.prepareDocumentForSignature(signerProperties, DIGEST_ALGORITHM, PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, 5000, false);

            String fieldName = signerProperties.getFieldName();

            // Phase 2 sign the document digest
            byte[] signData = signDigest(digest, DIGEST_ALGORITHM);

            try (OutputStream outputStreamPhase2 = FileUtil.getFileOutputStream(DESTINATION_FOLDER + "2PhaseCompleteCycle.pdf");
                    PdfReader newReader = new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()))) {
                PdfTwoPhaseSigner.addSignatureToPreparedDocument(newReader, fieldName, outputStreamPhase2, signData);
            }
            Assertions.assertNull(SignaturesCompareTool.compareSignatures(DESTINATION_FOLDER + "2PhaseCompleteCycle.pdf",
                    SOURCE_FOLDER + "cmp_2PhaseCompleteCycle.pdf"));
        }
    }

    @Test
    public void testCompletion() throws IOException, GeneralSecurityException {
        // read data
        byte[] signData = new byte[4096];
        try (InputStream signdataS = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "signeddata.bin")) {
            signdataS.read(signData);
        }
        // open prepared document
        try (PdfReader reader = new PdfReader(new File(SOURCE_FOLDER + "2PhasePreparedSignature.pdf"));
             OutputStream signedDoc = FileUtil.getFileOutputStream(DESTINATION_FOLDER + "2PhaseCompletion.pdf")) {
            // add signature
            PdfTwoPhaseSigner.addSignatureToPreparedDocument(reader, FIELD_NAME, signedDoc, signData);
        }

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(DESTINATION_FOLDER + "2PhaseCompletion.pdf",
                SOURCE_FOLDER + "cmp_2PhaseCompleteCycle.pdf"));
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-8113 Fix signatures tests)
    public void testWithCMS() throws IOException, GeneralSecurityException {
        String signatureName = "Signature1";

        try (ByteArrayOutputStream phaseOneOS = new ByteArrayOutputStream()) {
            // Phase 1 prepare the document, add the partial CMS  and get the documents digest of signed attributes
            byte[] dataToEncrypt = prepareDocumentAndCMS(new File(SIMPLE_DOC_PATH), phaseOneOS, signatureName);

            // Phase 2 sign the document digest
            //simulating server side
            byte[] signaturedata = serverSideSigning(dataToEncrypt);

            String signedDocumentName = DESTINATION_FOLDER + "2PhaseCompleteCycleCMS.pdf";
            //phase 2.1 extract CMS from the prepared document
            try (OutputStream outputStreamPhase2 = FileUtil.getFileOutputStream(signedDocumentName);
                    PdfReader inputDoc = new PdfReader(new ByteArrayInputStream(phaseOneOS.toByteArray()));
                    PdfDocument doc = new PdfDocument(inputDoc)) {

                SignatureUtil su = new SignatureUtil(doc);
                PdfSignature sig = su.getSignature(signatureName);
                PdfString encodedCMS = sig.getContents();
                byte[] encodedCMSdata = encodedCMS.getValueBytes();
                CMSContainer cmsToUpdate = new CMSContainer(encodedCMSdata);

                //phase 2.2 add the signatureValue to the CMS
                cmsToUpdate.getSignerInfo().setSignature(signaturedata);

                //if needed a time stamp could be added here

                //Phase 2.3 add the updated CMS to the document
                PdfTwoPhaseSigner.addSignatureToPreparedDocument(
                        new PdfReader(new ByteArrayInputStream(phaseOneOS.toByteArray())),
                        signatureName, outputStreamPhase2, cmsToUpdate);
            }

            // validate signature
            try (PdfReader reader = new PdfReader(signedDocumentName);
                 PdfDocument finalDoc = new PdfDocument(reader)) {
                SignatureUtil su = new SignatureUtil(finalDoc);

                PdfPKCS7 cms = su.readSignatureData(signatureName);
                Assertions.assertTrue(cms.verifySignatureIntegrityAndAuthenticity(), "Signature should be valid");
            }
            // compare result
            Assertions.assertNull(SignaturesCompareTool.compareSignatures(signedDocumentName,
                    SOURCE_FOLDER + "cmp_2PhaseCompleteCycleCMS.pdf"));
        }
    }

    private byte[] signDigest(byte[] data, String hashAlgorithm) throws GeneralSecurityException {
        PdfPKCS7 sgn = new PdfPKCS7((PrivateKey) null, chain, hashAlgorithm, null, new BouncyCastleDigest(), false);
        byte[] sh = sgn.getAuthenticatedAttributeBytes(data, PdfSigner.CryptoStandard.CMS, null, null);

        PrivateKeySignature pkSign = new PrivateKeySignature(pk, hashAlgorithm,
                BouncyCastleFactoryCreator.getFactory().getProviderName());
        byte[] signData = pkSign.sign(sh);

        sgn.setExternalSignatureValue(
                signData,
                null,
                pkSign.getSignatureAlgorithmName(),
                pkSign.getSignatureMechanismParameters()
        );

        return sgn.getEncodedPKCS7(data, PdfSigner.CryptoStandard.CMS, null, null, null);
    }

    private byte[] prepareDocumentAndCMS(File document, ByteArrayOutputStream preparedOS, String signatureName)
            throws IOException, GeneralSecurityException {
        try (PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(document));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfTwoPhaseSigner signer = new PdfTwoPhaseSigner(reader, outputStream);

            SignerProperties signerProperties = new SignerProperties();
            signerProperties.setFieldName(signatureName);
            byte[] digest = signer.prepareDocumentForSignature(signerProperties, DIGEST_ALGORITHM, PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, 5000, false);

            String fieldName = signerProperties.getFieldName();

            // Phase 1.1 prepare the CMS
            CMSContainer cms = new CMSContainer();
            SignerInfo signerInfo = new SignerInfo();

            //signerInfo.setSigningCertificateAndAddToSignedAttributes(chain[0], OID.ID_SHA384);
            signerInfo.setSigningCertificate(chain[0]);
            // in the two phase scenario,; we don't have the private key! So we start from the signing certificate

            IX509CertificateHolder bcCert = FACTORY.createJcaX509CertificateHolder(chain[0]);


            String algorithmOid = bcCert.getSignatureAlgorithm().getAlgorithm().getId();

            signerInfo.setSignatureAlgorithm(new AlgorithmIdentifier(algorithmOid));
            signerInfo.setDigestAlgorithm(new AlgorithmIdentifier(DIGEST_ALGORITHM_OID));
            signerInfo.setMessageDigest(digest);
            cms.setSignerInfo(signerInfo);
            cms.addCertificates(chain);

            byte[] signedAttributesToSign = cms.getSerializedSignedAttributes();

            MessageDigest sha = MessageDigest.getInstance(DIGEST_ALGORITHM);
            byte[] dataToSign = sha.digest(signedAttributesToSign);
            // now we store signedAttributesToSign together with the prepared document and send
            // dataToSign to the signing instance

            try (PdfReader newReader = new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()))) {
                PdfTwoPhaseSigner.addSignatureToPreparedDocument(newReader, fieldName, preparedOS, cms.serialize());
            }

            return dataToSign;
        }
    }

    private byte[] serverSideSigning(byte[] dataToEncrypt) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String signingAlgoritmName = pk.getAlgorithm();
        if ("EC".equals(signingAlgoritmName)) {
            signingAlgoritmName = "ECDSA";
        }
        // package digest in a DigestInfo structure before encrypting with the private key
        org.bouncycastle.asn1.x509.AlgorithmIdentifier sha256Aid = new org.bouncycastle.asn1.x509.AlgorithmIdentifier(
                new ASN1ObjectIdentifier(DIGEST_ALGORITHM_OID), DERNull.INSTANCE);
        DigestInfo di = new DigestInfo(sha256Aid, dataToEncrypt);
        //sign SHA256 with RSA

        byte[] encodedDigestInfo = di.toASN1Primitive().getEncoded();

        Cipher cipher = Cipher.getInstance(signingAlgoritmName);
        cipher.init(Cipher.ENCRYPT_MODE, pk);
        return cipher.doFinal(encodedDigestInfo);
    }
}

