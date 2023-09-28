/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

@Category(BouncyCastleIntegrationTest.class)
public class TwoPhaseSigningTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/TwoPhaseSigningTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/TwoPhaseSigningTest/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    private static final String SIMPLE_DOC_PATH = SOURCE_FOLDER + "SimpleDoc.pdf";

    private static final String DIGEST_ALGORITHM = DigestAlgorithms.SHA384;

    public static final String FIELD_NAME = "Signature1";

    private PrivateKey pk;
    private Certificate[] chain;

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Before
    public void init()
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {
        pk = PemFileHelper.readFirstKey(CERTS_SRC + "signCertRsa01.pem", PASSWORD);
        chain = PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsa01.pem");
    }

    @Test
    public void testPreparationWithClosedPdfSigner() throws IOException, GeneralSecurityException {
        // prepare the file
        try (PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(SIMPLE_DOC_PATH));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfSigner signer = new PdfSigner(reader, outputStream, new StampingProperties());

            signer.prepareDocumentForSignature(DigestAlgorithms.SHA384, PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, 5000, false);

            Exception e = Assert.assertThrows(PdfException.class, () -> {
                byte[] digest = signer.prepareDocumentForSignature(DigestAlgorithms.SHA384, PdfName.Adobe_PPKLite,
                        PdfName.Adbe_pkcs7_detached, 5000, false);
            });
            Assert.assertEquals(SignExceptionMessageConstant.THIS_INSTANCE_OF_PDF_SIGNER_ALREADY_CLOSED, e.getMessage());
        }
    }

    @Test
    public void testCompletionWithWrongFieldName() throws IOException {
        byte[] signData = new byte[4096];
        // open prepared document
        try (PdfDocument preparedDoc =
                     new PdfDocument(new PdfReader(new File(SOURCE_FOLDER + "2PhasePreparedSignature.pdf")));
             OutputStream signedDoc = new ByteArrayOutputStream()) {
            // add signature
            Exception e = Assert.assertThrows(PdfException.class, () -> {
                PdfSigner.addSignatureToPreparedDocument(preparedDoc, "wrong" + FIELD_NAME, signedDoc, signData);
            });

            Assert.assertEquals(MessageFormatUtil.format(
                    SignExceptionMessageConstant.THERE_IS_NO_FIELD_IN_THE_DOCUMENT_WITH_SUCH_NAME,
                    "wrong" + FIELD_NAME), e.getMessage());
        }
    }

    @Test
    public void testCompletionWithNotEnoughSpace() throws IOException {
        byte[] signData = new byte[20000];
        // open prepared document
        try (PdfDocument preparedDoc =
                     new PdfDocument(new PdfReader(new File(SOURCE_FOLDER + "2PhasePreparedSignature.pdf")));
             OutputStream signedDoc = new ByteArrayOutputStream()) {
            // add signature
            Exception e = Assert.assertThrows(PdfException.class, () -> {
                PdfSigner.addSignatureToPreparedDocument(preparedDoc, FIELD_NAME, signedDoc, signData);
            });

            Assert.assertEquals(SignExceptionMessageConstant.AVAILABLE_SPACE_IS_NOT_ENOUGH_FOR_SIGNATURE,
                    e.getMessage());
        }
    }

    @Test
    public void testCompletionWithSignatureFieldNotLastOne() throws IOException, GeneralSecurityException {
        try (PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "2PhasePreparedSignature.pdf"));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfSigner signer = new PdfSigner(reader, outputStream, new StampingProperties());

            // Add second signature field
            byte[] digest = signer.prepareDocumentForSignature(DIGEST_ALGORITHM, PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, 5000, false);

            byte[] signData = new byte[1024];
            try (OutputStream outputStreamPhase2 = FileUtil.getFileOutputStream(DESTINATION_FOLDER + "2PhaseCompleteCycle.pdf");
                 PdfDocument doc = new PdfDocument(new PdfReader(new ByteArrayInputStream(outputStream.toByteArray())))) {

                Exception e = Assert.assertThrows(PdfException.class, () -> {
                    PdfSigner.addSignatureToPreparedDocument(doc, FIELD_NAME, outputStreamPhase2, signData);
                });

                Assert.assertEquals(MessageFormatUtil.format(SignExceptionMessageConstant.
                        SIGNATURE_WITH_THIS_NAME_IS_NOT_THE_LAST_IT_DOES_NOT_COVER_WHOLE_DOCUMENT, FIELD_NAME), e.getMessage());
            }
        }
    }

    @Test
    public void testPreparation() throws IOException, GeneralSecurityException {
        // prepare the file
        try (PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(SIMPLE_DOC_PATH));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfSigner signer = new PdfSigner(reader, outputStream, new StampingProperties());
            String fieldName = signer.getFieldName();

            byte[] digest = signer.prepareDocumentForSignature(DigestAlgorithms.SHA384, PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, 5000, false);

            try (PdfDocument cmp_document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "cmp_prepared.pdf"));
                 PdfDocument outDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(outputStream.toByteArray())))) {

                SignatureUtil signatureUtil = new SignatureUtil(cmp_document);
                PdfSignature cmpSignature = signatureUtil.getSignature(fieldName);

                signatureUtil = new SignatureUtil(outDocument);
                PdfSignature outSignature = signatureUtil.getSignature(fieldName);
                try {
                    Assert.assertTrue(signatureUtil.signatureCoversWholeDocument(FIELD_NAME));
                    Assert.assertArrayEquals(cmpSignature.getContents().getValueBytes(), outSignature.getContents().getValueBytes());
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
            PdfSigner signer = new PdfSigner(reader, outputStream, new StampingProperties());

            byte[] digest = signer.prepareDocumentForSignature(DIGEST_ALGORITHM, PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, 5000, false);

            String fieldName = signer.getFieldName();

            // Phase 2 sign the document digest
            byte[] signData = signDigest(digest, DIGEST_ALGORITHM);

            try (OutputStream outputStreamPhase2 = FileUtil.getFileOutputStream(DESTINATION_FOLDER + "2PhaseCompleteCycle.pdf");
                 PdfDocument doc = new PdfDocument(new PdfReader(new ByteArrayInputStream(outputStream.toByteArray())))) {
                PdfSigner.addSignatureToPreparedDocument(doc, fieldName, outputStreamPhase2, signData);
            }
            Assert.assertNull(SignaturesCompareTool.compareSignatures(DESTINATION_FOLDER + "2PhaseCompleteCycle.pdf",
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
        try (PdfDocument preparedDoc =
                     new PdfDocument(new PdfReader(new File(SOURCE_FOLDER + "2PhasePreparedSignature.pdf")));
             OutputStream signedDoc = FileUtil.getFileOutputStream(DESTINATION_FOLDER + "2PhaseCompletion.pdf")) {
            // add signature
            PdfSigner.addSignatureToPreparedDocument(preparedDoc, FIELD_NAME,signedDoc , signData);
        }

        Assert.assertNull(SignaturesCompareTool.compareSignatures(DESTINATION_FOLDER + "2PhaseCompletion.pdf",
                SOURCE_FOLDER + "cmp_2PhaseCompleteCycle.pdf"));
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

        return sgn.getEncodedPKCS7(data, PdfSigner.CryptoStandard.CMS, null, null,null);
    }

}
