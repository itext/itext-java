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
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.signatures.validation.SignatureValidationProperties;
import com.itextpdf.signatures.validation.SignatureValidator;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.CertificateSources;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.TimeBasedContexts;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.context.ValidatorContexts;
import com.itextpdf.signatures.validation.mocks.MockRevocationDataValidator;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
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
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Arrays;

@Tag("BouncyCastleIntegrationTest")
public class PostQuantumAlgorithmsTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final boolean FIPS_MODE = "BCFIPS".equals(BOUNCY_CASTLE_FACTORY.getProviderName());

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/PostQuantumAlgorithmsTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/PostQuantumAlgorithmsTest/";

    private static final String SOURCE_FILE = SOURCE_FOLDER + "helloWorldDoc.pdf";
    private static final String SIGNATURE_FIELD = "Signature";
    private static final char[] KEY_PASSPHRASE = "testpassphrase".toCharArray();

    @BeforeAll
    public static void setup() {
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static Iterable<Object[]> algorithms() {
        return Arrays.asList(new Object[][]{
                {"ML-DSA-44", DigestAlgorithms.SHA3_256, OID.ML_DSA_44, 2420},
                {"ML-DSA-65", DigestAlgorithms.SHA3_384, OID.ML_DSA_65, 3309},
                {"ML-DSA-87", DigestAlgorithms.SHA3_512, OID.ML_DSA_87, 4627},

                {"slh-dsa-sha2-128s", DigestAlgorithms.SHA256, OID.SLH_DSA_SHA2_128S, 7856},
                {"slh-dsa-sha2-128f", DigestAlgorithms.SHA256, OID.SLH_DSA_SHA2_128F, 17088},
                {"slh-dsa-shake-128s", DigestAlgorithms.SHAKE256, OID.SLH_DSA_SHAKE_128S, 7856},
                {"slh-dsa-shake-128f", DigestAlgorithms.SHAKE256, OID.SLH_DSA_SHAKE_128F, 17088},

                {"slh-dsa-sha2-192s", DigestAlgorithms.SHA512, OID.SLH_DSA_SHA2_192S, 16224},
                {"slh-dsa-sha2-192f", DigestAlgorithms.SHA512, OID.SLH_DSA_SHA2_192F, 35664},
                {"slh-dsa-shake-192s", DigestAlgorithms.SHAKE256, OID.SLH_DSA_SHAKE_192S, 16224},
                {"slh-dsa-shake-192f", DigestAlgorithms.SHAKE256, OID.SLH_DSA_SHAKE_192F, 35664},

                {"slh-dsa-sha2-256s", DigestAlgorithms.SHA512, OID.SLH_DSA_SHA2_256S, 29792},
                {"slh-dsa-sha2-256f", DigestAlgorithms.SHA512, OID.SLH_DSA_SHA2_256F, 49856},
                {"slh-dsa-shake-256s", DigestAlgorithms.SHAKE256, OID.SLH_DSA_SHAKE_256S, 29792},
                {"slh-dsa-shake-256f", DigestAlgorithms.SHAKE256, OID.SLH_DSA_SHAKE_256F, 49856}
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algorithms")
    @LogMessages(messages =
    @LogMessage(messageTemplate = KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC), ignore = true)
    public void signVerifyPQCTest(String signatureAlgo, String digestAlgo, String expectedSigAlgoIdentifier,
                                  int signatureBytesSize) throws Exception {
        String outFile = Paths.get(DESTINATION_FOLDER, signatureAlgo + ".pdf").toString();
        String certPath = SOURCE_FOLDER + "cert_" + signatureAlgo + ".pem";
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        if (FIPS_MODE) {
            Exception e = Assertions.assertThrows(Exception.class,
                    () -> doSign(signatureAlgo, digestAlgo, certPath, outFile, signatureBytesSize));
            Assertions.assertTrue(e.getMessage().contains(MessageFormatUtil.format(
                    KernelExceptionMessageConstant.NO_SUCH_ALGORITHM_FOR_PROVIDER_BCFIPS, expectedSigAlgoIdentifier)) ||
                    KernelExceptionMessageConstant.NO_SUCH_ALGORITHM.equals(e.getMessage()));
        } else {
            doSign(signatureAlgo, digestAlgo, certPath, outFile, signatureBytesSize);
            doVerify(outFile, expectedSigAlgoIdentifier);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algorithms")
    @LogMessages(messages =
    @LogMessage(messageTemplate = KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC), ignore = true)
    public void signExternalContainerPQCTest(String signatureAlgo, String digestAlgo, String expectedSigAlgoIdentifier,
                                             int signatureBytesSize) throws Exception {
        String outFile = Paths.get(DESTINATION_FOLDER, "ext_cont_" + signatureAlgo + ".pdf").toString();
        String certPath = SOURCE_FOLDER + "cert_" + signatureAlgo + ".pem";
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        if (FIPS_MODE) {
            Exception e = Assertions.assertThrows(Exception.class,
                    () -> doSignExternalContainer(digestAlgo, certPath, outFile, signatureBytesSize));
            Assertions.assertTrue(e.getMessage().contains(MessageFormatUtil.format(
                    KernelExceptionMessageConstant.NO_SUCH_ALGORITHM_FOR_PROVIDER_BCFIPS, expectedSigAlgoIdentifier)) ||
                    KernelExceptionMessageConstant.NO_SUCH_ALGORITHM.equals(e.getMessage()));
        } else {
            doSignExternalContainer(digestAlgo, certPath, outFile, signatureBytesSize);
            doVerify(outFile, expectedSigAlgoIdentifier);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algorithms")
    @LogMessages(messages =
    @LogMessage(messageTemplate = KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC), ignore = true)
    public void signDeferredPQCTest(String signatureAlgo, String digestAlgo, String expectedSigAlgoIdentifier,
                                    int signatureBytesSize) throws Exception {
        String preparedFile = Paths.get(DESTINATION_FOLDER, "prep_" + signatureAlgo + ".pdf").toString();
        String outFile = Paths.get(DESTINATION_FOLDER, "deferred_" + signatureAlgo + ".pdf").toString();
        String certPath = SOURCE_FOLDER + "cert_" + signatureAlgo + ".pem";
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        prepareDocForSignDeferred(preparedFile, signatureBytesSize);
        if (FIPS_MODE) {
            Exception e = Assertions.assertThrows(Exception.class,
                    () -> doSignDeferred(preparedFile, outFile, digestAlgo, certPath));
            Assertions.assertTrue(e.getMessage().contains(MessageFormatUtil.format(
                    KernelExceptionMessageConstant.NO_SUCH_ALGORITHM_FOR_PROVIDER_BCFIPS, expectedSigAlgoIdentifier)) ||
                    KernelExceptionMessageConstant.NO_SUCH_ALGORITHM.equals(e.getMessage()));
        } else {
            doSignDeferred(preparedFile, outFile, digestAlgo, certPath);
            doVerify(outFile, expectedSigAlgoIdentifier);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algorithms")
    @LogMessages(messages =
    @LogMessage(messageTemplate = KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC), ignore = true)
    public void twoPhaseSigningPQCTest(String signatureAlgo, String digestAlgo, String expectedSigAlgoIdentifier,
                                       int signatureBytesSize) throws Exception {
        String outFile = Paths.get(DESTINATION_FOLDER, "two_phase_" + signatureAlgo + ".pdf").toString();
        String certPath = SOURCE_FOLDER + "cert_" + signatureAlgo + ".pem";
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        if (FIPS_MODE) {
            Exception e = Assertions.assertThrows(Exception.class,
                    () -> doTwoPhaseSigning(digestAlgo, certPath, outFile, signatureBytesSize));
            Assertions.assertTrue(e.getMessage().contains(MessageFormatUtil.format(
                    KernelExceptionMessageConstant.NO_SUCH_ALGORITHM_FOR_PROVIDER_BCFIPS, expectedSigAlgoIdentifier)) ||
                    KernelExceptionMessageConstant.NO_SUCH_ALGORITHM.equals(e.getMessage()));
        } else {
            doTwoPhaseSigning(digestAlgo, certPath, outFile, signatureBytesSize);
            doVerify(outFile, expectedSigAlgoIdentifier);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algorithms")
    @LogMessages(messages =
    @LogMessage(messageTemplate = KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC), ignore = true)
    public void timestampPQCTest(String signatureAlgo, String digestAlgo, String expectedSigAlgoIdentifier,
                                 int signatureBytesSize) throws Exception {
        String outFile = Paths.get(DESTINATION_FOLDER, "timestamp_" + signatureAlgo + ".pdf").toString();
        String certPath = SOURCE_FOLDER + "timestamp/ts_cert_" + signatureAlgo + ".pem";
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        if (FIPS_MODE) {
            Exception e = Assertions.assertThrows(Exception.class,
                    () -> doTimestamp(signatureAlgo, digestAlgo, certPath, outFile, signatureBytesSize));
            Assertions.assertTrue(e.getMessage().contains(MessageFormatUtil.format(
                    KernelExceptionMessageConstant.NO_SUCH_ALGORITHM_FOR_PROVIDER_BCFIPS, expectedSigAlgoIdentifier)) ||
                    KernelExceptionMessageConstant.NO_SUCH_ALGORITHM.equals(e.getMessage()));
        } else {
            doTimestamp(signatureAlgo, digestAlgo, certPath, outFile, signatureBytesSize);
            doVerify(outFile, expectedSigAlgoIdentifier);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algorithms")
    @LogMessages(messages =
    @LogMessage(messageTemplate = KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC), ignore = true)
    public void padesLTASignatureLevelPQCTest(String signatureAlgo, String digestAlgo, String expectedSigAlgoIdentifier,
                                              int signatureBytesSize) throws Exception {
        String outFile = Paths.get(DESTINATION_FOLDER, "pades_LTA_" + signatureAlgo + ".pdf").toString();
        String signCertFileName = SOURCE_FOLDER + "chain/sign_" + signatureAlgo + ".pem";
        String tsaCertFileName = SOURCE_FOLDER + "timestamp/ts_cert_" + signatureAlgo + ".pem";
        String caCertFileName = SOURCE_FOLDER + "cert_" + signatureAlgo + ".pem";
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        if (FIPS_MODE) {
            Exception e = Assertions.assertThrows(Exception.class,
                    () -> signWithBaselineLTAProfile(signatureAlgo, digestAlgo, signatureBytesSize, signCertFileName,
                            tsaCertFileName, caCertFileName, outFile));
            Assertions.assertTrue(e.getMessage().contains(MessageFormatUtil.format(
                    KernelExceptionMessageConstant.NO_SUCH_ALGORITHM_FOR_PROVIDER_BCFIPS, expectedSigAlgoIdentifier)) ||
                    KernelExceptionMessageConstant.NO_SUCH_ALGORITHM.equals(e.getMessage()));
        } else {
            signWithBaselineLTAProfile(signatureAlgo, digestAlgo, signatureBytesSize, signCertFileName, tsaCertFileName,
                    caCertFileName, outFile);
            validateExistingSignature(outFile, caCertFileName, tsaCertFileName);
        }
    }

    private static void doSign(String signatureAlgo, String digestAlgo, String certPath, String outFile,
                               int signatureBytesSize) throws Exception {
        Certificate[] signChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE);
        IExternalSignature pks = new PrivateKeySignature(signPrivateKey, digestAlgo, signatureAlgo,
                BOUNCY_CASTLE_FACTORY.getProviderName(), null);

        try (OutputStream out = FileUtil.getFileOutputStream(outFile)) {
            PdfSigner signer = new PdfSigner(new PdfReader(SOURCE_FILE), out, new StampingProperties());
            SignerProperties signerProperties = getSignerProperties("Approval test signature.\nCreated by iText.");
            signer.setSignerProperties(signerProperties);

            signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, null, null, signatureBytesSize * 3,
                    PdfSigner.CryptoStandard.CMS);
        }
    }

    private static void doSignExternalContainer(String digestAlgo, String certPath, String outFile,
                                                int signatureBytesSize) throws Exception {
        Certificate[] signChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE);

        try (OutputStream out = FileUtil.getFileOutputStream(outFile)) {
            PdfSigner signer = new PdfSigner(new PdfReader(SOURCE_FILE), out, new StampingProperties());
            SignerProperties signerProperties = getSignerProperties("Sign external container.\nCreated by iText.");
            signer.setSignerProperties(signerProperties);

            PKCS7ExternalSignatureContainer pkcs7ExternalSignatureContainer = new PKCS7ExternalSignatureContainer(
                    signPrivateKey, signChain, digestAlgo);

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

    private static void doSignDeferred(String srcFile, String outFile, String digestAlgo, String certPath)
            throws Exception {
        Certificate[] signChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE);
        IExternalSignatureContainer extSigContainer =
                new PKCS7ExternalSignatureContainer(signPrivateKey, signChain, digestAlgo);
        try (PdfReader reader = new PdfReader(srcFile);
             OutputStream outStream = FileUtil.getFileOutputStream(outFile)) {
            PdfSigner.signDeferred(reader, SIGNATURE_FIELD, outStream, extSigContainer);
        }
    }

    private static void doTwoPhaseSigning(String digestAlgo, String certPath, String outFile, int signatureBytesSize)
            throws Exception {
        Certificate[] signChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE);

        try (PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(SOURCE_FILE));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfTwoPhaseSigner signer = new PdfTwoPhaseSigner(reader, outputStream);

            SignerProperties signerProperties = getSignerProperties("Two-phase signing.\nCreated by iText.");
            byte[] digest = signer.prepareDocumentForSignature(signerProperties, digestAlgo, PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, 3 * signatureBytesSize, false);

            byte[] signData = signDigest(digest, signChain, signPrivateKey, digestAlgo);

            try (OutputStream outputStreamPhase2 = FileUtil.getFileOutputStream(outFile);
                 PdfReader newReader = new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()))) {
                PdfTwoPhaseSigner.addSignatureToPreparedDocument(newReader, SIGNATURE_FIELD, outputStreamPhase2,
                        signData);
            }
        }
    }

    private static byte[] signDigest(byte[] data, Certificate[] chain, PrivateKey pk, String digestAlgo)
            throws Exception {
        PdfPKCS7 sgn = new PdfPKCS7((PrivateKey) null, chain, digestAlgo, null, new BouncyCastleDigest(), false);
        byte[] sh = sgn.getAuthenticatedAttributeBytes(data, PdfSigner.CryptoStandard.CMS, null, null);

        PrivateKeySignature pkSign = new PrivateKeySignature(pk, digestAlgo,
                BouncyCastleFactoryCreator.getFactory().getProviderName());
        byte[] signData = pkSign.sign(sh);

        sgn.setExternalSignatureValue(signData, null, pkSign.getSignatureAlgorithmName(),
                pkSign.getSignatureMechanismParameters());

        return sgn.getEncodedPKCS7(data, PdfSigner.CryptoStandard.CMS, null, null, null);
    }

    private static void doTimestamp(String signatureAlgo, String digestAlgo, String certPath, String outFile,
                                    int signatureBytesSize) throws Exception {
        Certificate[] tsaChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE);

        try (OutputStream out = FileUtil.getFileOutputStream(outFile)) {
            PdfSigner signer = new PdfSigner(new PdfReader(SOURCE_FILE), out, new StampingProperties());
            SignerProperties signerProperties = getSignerProperties("Timestamp signature.\nCreated by iText.");
            signer.setSignerProperties(signerProperties);

            ITSAClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey, 3 * signatureBytesSize,
                    signatureAlgo, digestAlgo);
            signer.timestamp(testTsa, SIGNATURE_FIELD);
        }
    }

    private static void signWithBaselineLTAProfile(String signatureAlgo, String digestAlgo, int signatureBytesSize,
                                                   String signCertFileName, String tsaCertFileName,
                                                   String caCertFileName, String outFile) throws Exception {
        Certificate[] signChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, KEY_PASSPHRASE);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, KEY_PASSPHRASE);
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, KEY_PASSPHRASE);

        SignerProperties signerProperties = getSignerProperties("Sign with baseline-LTA profile.\nCreated by iText.");

        PdfPadesSigner padesSigner = new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(SOURCE_FILE)),
                FileUtil.getFileOutputStream(outFile));
        padesSigner.setEstimatedSize(10 * signatureBytesSize);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey, 3 * signatureBytesSize,
                signatureAlgo, digestAlgo);
        ICrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey, signatureAlgo);
        TestOcspClient ocspClient = new TestOcspClient()
                .addBuilderForCertIssuer(caCert, caPrivateKey, signatureAlgo)
                .addBuilderForCertIssuer((X509Certificate) tsaChain[0], tsaPrivateKey, signatureAlgo);
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient).setTimestampSignatureName("timestampSig1");

        padesSigner.signWithBaselineLTAProfile(signerProperties, signChain, signPrivateKey, testTsa);
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

    private static void validateExistingSignature(String src, String rootCert, String tsaCert) throws Exception {
        SignatureValidationProperties properties = getSignatureValidationProperties();

        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCert)[0];
        X509Certificate caTsaCert = (X509Certificate) PemFileHelper.readFirstChain(tsaCert)[0];
        IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
        certificateRetriever.setTrustedCertificates(Arrays.asList(caCert, caTsaCert));

        ValidatorChainBuilder validatorChainBuilder = new ValidatorChainBuilder()
                .withIssuingCertificateRetrieverFactory(() -> certificateRetriever)
                .withRevocationDataValidatorFactory(() -> new MockRevocationDataValidator())
                .withSignatureValidationProperties(properties);

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(src))) {
            SignatureValidator validator = validatorChainBuilder.buildSignatureValidator(document);
            report = validator.validateSignatures();
        }
        assert (report.getValidationResult().equals(ValidationReport.ValidationResult.VALID));
    }

    private static SignatureValidationProperties getSignatureValidationProperties() {
        SignatureValidationProperties properties = new SignatureValidationProperties();
        properties.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        properties.setFreshness(ValidatorContexts.all(), CertificateSources.all(),
                TimeBasedContexts.of(TimeBasedContext.HISTORICAL), Duration.ofDays(0));
        properties.setContinueAfterFailure(
                ValidatorContexts.of(ValidatorContext.OCSP_VALIDATOR, ValidatorContext.CRL_VALIDATOR),
                CertificateSources.of(CertificateSource.CRL_ISSUER, CertificateSource.OCSP_ISSUER,
                        CertificateSource.CERT_ISSUER), true);
        return properties;
    }
}
