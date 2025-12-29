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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Arrays;

@Tag("BouncyCastleIntegrationTest")
public class PostQuantumExperimentalAlgorithmsTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final boolean FIPS_MODE = "BCFIPS".equals(BOUNCY_CASTLE_FACTORY.getProviderName());
    private static final boolean IS_NATIVE = System.getProperty("org.graalvm.nativeimage.imagecode") != null;

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/signatures/PostQuantumExperimentalAlgorithmsTest/";
    private static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/signatures/PostQuantumExperimentalAlgorithmsTest/";

    private static final String SOURCE_FILE = SOURCE_FOLDER + "helloWorldDoc.pdf";
    private static final String SIGNATURE_FIELD = "Signature";
    private static final char[] KEY_PASSPHRASE = "testpassphrase".toCharArray();

    private static final String PICNIC_OID = "1.3.6.1.4.1.22554.2.6.2.2";
    private static final String DIGEST_ALGO = DigestAlgorithms.SHAKE256;
    private static final String XMSS = "XMSS";

    @BeforeAll
    public static void setup() {
        Assumptions.assumeFalse(FIPS_MODE);
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getProvider());
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getPqcProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static Iterable<Object[]> algorithms() {
        return Arrays.asList(new Object[][]{
                {"Falcon-512", "1.3.9999.3.11", 1500},
                {"Falcon-1024", "1.3.9999.3.14", 2500},

                {"Picnic3-L1", PICNIC_OID, 100000},
                {"Picnic-L1-FS", PICNIC_OID, 100000},
                {"Picnic-L1-Full", PICNIC_OID, 100000},
                {"Picnic-L1-UR", PICNIC_OID, 100000},

                {"LMS", "1.2.840.113549.1.9.16.3.17", 10000},
                {XMSS, "1.3.6.1.4.1.22554.2.2.10", 10000}
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algorithms")
    @LogMessages(messages =
    @LogMessage(messageTemplate = KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC), ignore = true)
    public void signVerifyPQCTest(String signatureAlgo, String expectedSigAlgoIdentifier, int signatureBytesSize)
            throws Exception {
        checkXMSSInNative(signatureAlgo);

        String outFile = Paths.get(DESTINATION_FOLDER, signatureAlgo + ".pdf").toString();
        String certPath = SOURCE_FOLDER + "cert_" + signatureAlgo + ".pem";
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        String finalSignatureAlgo = signatureAlgo.contains("Picnic") ? "Picnic" : signatureAlgo;

        doSign(finalSignatureAlgo, certPath, outFile, signatureBytesSize);
        doVerify(outFile, expectedSigAlgoIdentifier);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algorithms")
    @LogMessages(messages =
    @LogMessage(messageTemplate = KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC), ignore = true)
    public void signExternalContainerPQCTest(String signatureAlgo, String expectedSigAlgoIdentifier,
                                             int signatureBytesSize) throws Exception {
        checkXMSSInNative(signatureAlgo);

        String outFile = Paths.get(DESTINATION_FOLDER, "ext_cont_" + signatureAlgo + ".pdf").toString();
        String certPath = SOURCE_FOLDER + "cert_" + signatureAlgo + ".pem";
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        doSignExternalContainer(certPath, outFile, signatureBytesSize);
        doVerify(outFile, expectedSigAlgoIdentifier);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algorithms")
    @LogMessages(messages =
    @LogMessage(messageTemplate = KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC), ignore = true)
    public void signDeferredPQCTest(String signatureAlgo, String expectedSigAlgoIdentifier, int signatureBytesSize)
            throws Exception {
        checkXMSSInNative(signatureAlgo);

        String preparedFile = Paths.get(DESTINATION_FOLDER, "prep_" + signatureAlgo + ".pdf").toString();
        String outFile = Paths.get(DESTINATION_FOLDER, "deferred_" + signatureAlgo + ".pdf").toString();
        String certPath = SOURCE_FOLDER + "cert_" + signatureAlgo + ".pem";
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        prepareDocForSignDeferred(preparedFile, signatureBytesSize);
        doSignDeferred(preparedFile, outFile, certPath);
        doVerify(outFile, expectedSigAlgoIdentifier);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algorithms")
    @LogMessages(messages =
    @LogMessage(messageTemplate = KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC), ignore = true)
    public void twoPhaseSigningPQCTest(String signatureAlgo, String expectedSigAlgoIdentifier,
                                       int signatureBytesSize) throws Exception {
        checkXMSSInNative(signatureAlgo);

        String outFile = Paths.get(DESTINATION_FOLDER, "two_phase_" + signatureAlgo + ".pdf").toString();
        String certPath = SOURCE_FOLDER + "cert_" + signatureAlgo + ".pem";
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        doTwoPhaseSigning(certPath, outFile, signatureBytesSize);
        doVerify(outFile, expectedSigAlgoIdentifier);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algorithms")
    @LogMessages(messages =
    @LogMessage(messageTemplate = KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC), ignore = true)
    public void timestampPQCTest(String signatureAlgo, String expectedSigAlgoIdentifier, int signatureBytesSize)
            throws Exception {
        checkXMSSInNative(signatureAlgo);

        String outFile = Paths.get(DESTINATION_FOLDER, "timestamp_" + signatureAlgo + ".pdf").toString();
        String certPath = SOURCE_FOLDER + "timestamp/ts_cert_" + signatureAlgo + ".pem";
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        String finalSignatureAlgo = signatureAlgo.contains("Picnic") ? "Picnic" : signatureAlgo;

        doTimestamp(finalSignatureAlgo, certPath, outFile, signatureBytesSize);
        // OIDs are different for timestamp signatures, but they're not final since we use experimental algorithms.
        doVerify(outFile, null);
    }

    private static void doSign(String signatureAlgo, String certPath, String outFile, int signatureBytesSize)
            throws Exception {
        Certificate[] signChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE,
                BOUNCY_CASTLE_FACTORY.getPqcProvider());
        IExternalSignature pks = new PrivateKeySignature(signPrivateKey,
                PostQuantumExperimentalAlgorithmsTest.DIGEST_ALGO, signatureAlgo,
                BOUNCY_CASTLE_FACTORY.getPqcProvider().getName(), null);

        try (OutputStream out = FileUtil.getFileOutputStream(outFile)) {
            PdfSigner signer = new PdfSigner(new PdfReader(SOURCE_FILE), out, new StampingProperties());
            SignerProperties signerProperties = getSignerProperties("Approval test signature.\nCreated by iText.");
            signer.setSignerProperties(signerProperties);

            signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, null, null, signatureBytesSize * 3,
                    PdfSigner.CryptoStandard.CMS);
        }
    }

    private static void doSignExternalContainer(String certPath, String outFile, int signatureBytesSize)
            throws Exception {
        Certificate[] signChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE,
                BOUNCY_CASTLE_FACTORY.getPqcProvider());

        try (OutputStream out = FileUtil.getFileOutputStream(outFile)) {
            PdfSigner signer = new PdfSigner(new PdfReader(SOURCE_FILE), out, new StampingProperties());
            SignerProperties signerProperties = getSignerProperties("Sign external container.\nCreated by iText.");
            signer.setSignerProperties(signerProperties);

            PKCS7ExternalSignatureContainer pkcs7ExternalSignatureContainer = new PKCS7ExternalSignatureContainer(
                    signPrivateKey, signChain, PostQuantumExperimentalAlgorithmsTest.DIGEST_ALGO);

            signer.signExternalContainer(pkcs7ExternalSignatureContainer, 3 * signatureBytesSize);
        }
    }

    private static void prepareDocForSignDeferred(String output, int estimatedSize)
            throws IOException, GeneralSecurityException {
        PdfSigner signer = new PdfSigner(new PdfReader(SOURCE_FILE), FileUtil.getFileOutputStream(output),
                new StampingProperties());
        SignerProperties signerProperties = getSignerProperties("Signature field which signing is deferred.");
        signer.setSignerProperties(signerProperties);

        PdfName filter = PdfName.Adobe_PPKLite;
        PdfName subFilter = PdfName.Adbe_pkcs7_detached;
        IExternalSignatureContainer external = new ExternalBlankSignatureContainer(filter, subFilter);
        signer.signExternalContainer(external, 3 * estimatedSize);
    }

    private static void doSignDeferred(String srcFile, String outFile, String certPath) throws Exception {
        Certificate[] signChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE,
                BOUNCY_CASTLE_FACTORY.getPqcProvider());
        IExternalSignatureContainer extSigContainer = new PKCS7ExternalSignatureContainer(signPrivateKey, signChain,
                PostQuantumExperimentalAlgorithmsTest.DIGEST_ALGO);
        try (PdfReader reader = new PdfReader(srcFile);
             OutputStream outStream = FileUtil.getFileOutputStream(outFile)) {
            PdfSigner.signDeferred(reader, SIGNATURE_FIELD, outStream, extSigContainer);
        }
    }

    private static void doTwoPhaseSigning(String certPath, String outFile, int signatureBytesSize) throws Exception {
        Certificate[] signChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE,
                BOUNCY_CASTLE_FACTORY.getPqcProvider());

        try (PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(SOURCE_FILE));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfTwoPhaseSigner signer = new PdfTwoPhaseSigner(reader, outputStream);

            SignerProperties signerProperties = getSignerProperties("Two-phase signing.\nCreated by iText.");
            byte[] digest = signer.prepareDocumentForSignature(signerProperties,
                    PostQuantumExperimentalAlgorithmsTest.DIGEST_ALGO, PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, 3 * signatureBytesSize, false);

            byte[] signData = signDigest(digest, signChain, signPrivateKey);

            try (OutputStream outputStreamPhase2 = FileUtil.getFileOutputStream(outFile);
                 PdfReader newReader = new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()))) {
                PdfTwoPhaseSigner.addSignatureToPreparedDocument(newReader, SIGNATURE_FIELD, outputStreamPhase2,
                        signData);
            }
        }
    }

    private static byte[] signDigest(byte[] data, Certificate[] chain, PrivateKey pk) throws Exception {
        PdfPKCS7 sgn = new PdfPKCS7((PrivateKey) null, chain, PostQuantumExperimentalAlgorithmsTest.DIGEST_ALGO, null,
                new BouncyCastleDigest(), false);
        byte[] sh = sgn.getAuthenticatedAttributeBytes(data, PdfSigner.CryptoStandard.CMS, null, null);

        PrivateKeySignature pkSign = new PrivateKeySignature(pk, PostQuantumExperimentalAlgorithmsTest.DIGEST_ALGO,
                BOUNCY_CASTLE_FACTORY.getPqcProvider().getName());
        byte[] signData = pkSign.sign(sh);

        sgn.setExternalSignatureValue(signData, null, pkSign.getSignatureAlgorithmName(),
                pkSign.getSignatureMechanismParameters());

        return sgn.getEncodedPKCS7(data, PdfSigner.CryptoStandard.CMS, null, null, null);
    }

    private static void doTimestamp(String signatureAlgo, String certPath, String outFile, int signatureBytesSize)
            throws Exception {
        Certificate[] tsaChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE,
                BOUNCY_CASTLE_FACTORY.getPqcProvider());

        try (OutputStream out = FileUtil.getFileOutputStream(outFile)) {
            PdfSigner signer = new PdfSigner(new PdfReader(SOURCE_FILE), out, new StampingProperties());
            SignerProperties signerProperties = getSignerProperties("Timestamp signature.\nCreated by iText.");
            signer.setSignerProperties(signerProperties);

            ITSAClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey, 3 * signatureBytesSize,
                    signatureAlgo, PostQuantumExperimentalAlgorithmsTest.DIGEST_ALGO);
            signer.timestamp(testTsa, SIGNATURE_FIELD);
        }
    }

    private static SignerProperties getSignerProperties(String description) {
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent(description);
        return new SignerProperties()
                .setFieldName(SIGNATURE_FIELD)
                .setPageRect(new Rectangle(50, 650, 200, 100))
                .setReason("Test")
                .setLocation("TestCity")
                .setSignatureAppearance(appearance);
    }

    private static void doVerify(String fileName, String expectedSigAlgoIdentifier) throws Exception {
        try (PdfReader r = new PdfReader(fileName); PdfDocument pdfDoc = new PdfDocument(r)) {
            SignatureUtil u = new SignatureUtil(pdfDoc);
            PdfPKCS7 data = u.readSignatureData(SIGNATURE_FIELD, BOUNCY_CASTLE_FACTORY.getProviderName());
            Assertions.assertTrue(data.verifySignatureIntegrityAndAuthenticity());
            if (expectedSigAlgoIdentifier != null) {
                Assertions.assertEquals(expectedSigAlgoIdentifier, data.getSignatureMechanismOid());
            }
        }
    }

    private static void checkXMSSInNative(String signatureAlgo) {
        if (IS_NATIVE && XMSS.equals(signatureAlgo)) {
            // TODO DEVSIX-9622 GraalVM: investigate XMSS PQC test failure (reproduces for graalvm-23.0.1)
            Assumptions.assumeTrue(false);
        }
    }
}
