/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
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
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;

@Tag("IntegrationTest")
public class SignatureCreatorTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/SignatureCreatorTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/SignatureCreatorTest/";

    private static final String SOURCE_FILE = SOURCE_FOLDER + "helloWorldDoc.pdf";
    private static final String SIGNATURE_FIELD = "Signature";
    private static final char[] KEY_PASSPHRASE = "testpassphrase".toCharArray();
    private static final String CERT_FOLDER = "./src/test/resources/com/itextpdf/signatures/certs/";

    @BeforeAll
    public static void setup() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    private static Iterable<Object[]> createSignatureCreatorParameters() {
        return Arrays.asList(
                new Object[]{null},
                new Object[]{"Custom Signature creator"});
    }

    @ParameterizedTest(name = "signature creator : {0}")
    @MethodSource("createSignatureCreatorParameters")
    public void signVerifySignatureCreatorTest(String signatureCreator) throws Exception {
        String outFile = Paths.get(DESTINATION_FOLDER, "signVerify"
                + (signatureCreator == null ? "NoSignatureCreator" : "CustomSignatureCreator") + ".pdf").toString();
        String certPath = CERT_FOLDER + "signCertRsaWithChain.pem";
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        Certificate[] signChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE);
        IExternalSignature pks = new PrivateKeySignature(signPrivateKey, DigestAlgorithms.SHA256,
                BOUNCY_CASTLE_FACTORY.getProviderName());

        try (OutputStream out = FileUtil.getFileOutputStream(outFile)) {
            PdfSigner signer = new PdfSigner(new PdfReader(SOURCE_FILE), out, new StampingProperties());
            SignerProperties signerProperties = getSignerProperties(
                    "Approval test signature.\nCreated by iText.", signatureCreator);
            signer.setSignerProperties(signerProperties);
            signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, null, null,
                    0, PdfSigner.CryptoStandard.CMS);
        }

        assertSignatureCreator(getSignatureCreator(new PdfDocument(new PdfReader(outFile)), SIGNATURE_FIELD),
                signatureCreator);
    }

    @ParameterizedTest(name = "signature creator : {0}")
    @MethodSource("createSignatureCreatorParameters")
    public void signExternalContainerSignatureCreatorTest(String signatureCreator) throws Exception {
        String certPath = CERT_FOLDER + "signCertRsaWithChain.pem";
        String outFile = Paths.get(DESTINATION_FOLDER, "signExternalContainer"
                + (signatureCreator == null ? "NoSignatureCreator" : "CustomSignatureCreator") + ".pdf").toString();
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        Certificate[] signChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE);

        try (OutputStream out = FileUtil.getFileOutputStream(outFile)) {
            PdfSigner signer = new PdfSigner(new PdfReader(SOURCE_FILE), out, new StampingProperties());
            SignerProperties signerProperties = getSignerProperties(
                    "Sign external container.\nCreated by iText.", signatureCreator);
            signer.setSignerProperties(signerProperties);
            IExternalSignatureContainer extSigContainer = new PKCS7ExternalSignatureContainer(signPrivateKey, signChain,
                    DigestAlgorithms.SHA256);
            signer.signExternalContainer(extSigContainer, 5000);
        }

        assertSignatureCreator(getSignatureCreator(new PdfDocument(new PdfReader(outFile)), SIGNATURE_FIELD),
                signatureCreator);
    }

    @ParameterizedTest(name = "signature creator : {0}")
    @MethodSource("createSignatureCreatorParameters")
    public void signDeferredSignatureCreatorTest(String signatureCreator) throws Exception {
        String certPath = CERT_FOLDER + "signCertRsaWithChain.pem";
        String preparedFile = Paths.get(DESTINATION_FOLDER, "preparedDoc"
                + (signatureCreator == null ? "NoSignatureCreator" : "CustomSignatureCreator") + ".pdf").toString();
        String outFile = Paths.get(DESTINATION_FOLDER, "signDeferred"
                + (signatureCreator == null ? "NoSignatureCreator" : "CustomSignatureCreator") + ".pdf").toString();
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        Certificate[] signChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE);

        PdfSigner signer = new PdfSigner(new PdfReader(SOURCE_FILE), FileUtil.getFileOutputStream(preparedFile),
                new StampingProperties());
        SignerProperties signerProperties = getSignerProperties(
                "Signature field which signing is deferred.", signatureCreator);
        signer.setSignerProperties(signerProperties);
        IExternalSignatureContainer external = new ExternalBlankSignatureContainer(
                PdfName.Adobe_PPKLite, PdfName.Adbe_pkcs7_detached);
        signer.signExternalContainer(external, 5000);
        IExternalSignatureContainer extSigContainer = new PKCS7ExternalSignatureContainer(signPrivateKey, signChain,
                DigestAlgorithms.SHA256);
        try (PdfReader reader = new PdfReader(preparedFile);
             OutputStream outStream = FileUtil.getFileOutputStream(outFile)) {
            PdfSigner.signDeferred(reader, SIGNATURE_FIELD, outStream, extSigContainer);
        }

        assertSignatureCreator(getSignatureCreator(new PdfDocument(new PdfReader(outFile)), SIGNATURE_FIELD),
                signatureCreator);
    }

    @ParameterizedTest(name = "signature creator : {0}")
    @MethodSource("createSignatureCreatorParameters")
    public void twoPhaseSigningSignatureCreatorTest(String signatureCreator) throws Exception {
        String certPath = CERT_FOLDER + "signCertRsaWithChain.pem";
        String outFile = Paths.get(DESTINATION_FOLDER, "twoPhaseSigning"
                + (signatureCreator == null ? "NoSignatureCreator" : "CustomSignatureCreator") + ".pdf").toString();
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        Certificate[] signChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE);

        try (PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(SOURCE_FILE));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfTwoPhaseSigner signer = new PdfTwoPhaseSigner(reader, outputStream);
            SignerProperties signerProperties = getSignerProperties(
                    "Two-phase signing.\nCreated by iText.", signatureCreator);
            byte[] digest = signer.prepareDocumentForSignature(signerProperties, DigestAlgorithms.SHA256,
                    PdfName.Adobe_PPKLite, PdfName.Adbe_pkcs7_detached, 5000,
                    false);
            PdfPKCS7 sgn = new PdfPKCS7((PrivateKey) null, signChain, DigestAlgorithms.SHA256, null,
                    new BouncyCastleDigest(), false);
            byte[] sh = sgn.getAuthenticatedAttributeBytes(digest, PdfSigner.CryptoStandard.CMS, null,
                    null);
            PrivateKeySignature pkSign = new PrivateKeySignature(signPrivateKey, DigestAlgorithms.SHA256,
                    BouncyCastleFactoryCreator.getFactory().getProviderName());
            byte[] signData = pkSign.sign(sh);
            sgn.setExternalSignatureValue(signData, null, pkSign.getSignatureAlgorithmName(),
                    pkSign.getSignatureMechanismParameters());
            byte[] data = sgn.getEncodedPKCS7(digest, PdfSigner.CryptoStandard.CMS, null, null,
                    null);
            try (OutputStream outputStreamPhase2 = FileUtil.getFileOutputStream(outFile);
                 PdfReader newReader = new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()))) {
                PdfTwoPhaseSigner.addSignatureToPreparedDocument(newReader, SIGNATURE_FIELD, outputStreamPhase2,
                        data);
            }
        }

        assertSignatureCreator(getSignatureCreator(new PdfDocument(new PdfReader(outFile)), SIGNATURE_FIELD),
                signatureCreator);
    }

    @ParameterizedTest(name = "signature creator : {0}")
    @MethodSource("createSignatureCreatorParameters")
    public void timestampSignatureCreatorTest(String signatureCreator) throws Exception {
        String certPath = CERT_FOLDER + "tsCertRsa.pem";
        String outFile = Paths.get(DESTINATION_FOLDER, "timestamp"
                + (signatureCreator == null ? "NoSignatureCreator" : "CustomSignatureCreator") + ".pdf").toString();
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        Certificate[] tsaChain = PemFileHelper.readFirstChain(certPath);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(certPath, KEY_PASSPHRASE);

        try (OutputStream out = FileUtil.getFileOutputStream(outFile)) {
            PdfSigner signer = new PdfSigner(new PdfReader(SOURCE_FILE), out, new StampingProperties());
            SignerProperties signerProperties = getSignerProperties(
                    "Timestamp signature.\nCreated by iText.", signatureCreator);
            signer.setSignerProperties(signerProperties);
            ITSAClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
            signer.timestamp(testTsa, SIGNATURE_FIELD);
        }

        assertSignatureCreator(getSignatureCreator(new PdfDocument(new PdfReader(outFile)), SIGNATURE_FIELD),
                signatureCreator);
    }

    @ParameterizedTest(name = "signature creator : {0}")
    @MethodSource("createSignatureCreatorParameters")
    public void padesLTASignatureLevelSignatureCreatorTest(String signatureCreator) throws Exception {
        String signCertFileName = CERT_FOLDER + "signCertRsa01.pem";
        String tsaCertFileName = CERT_FOLDER + "tsCertRsa.pem";
        String caCertFileName = CERT_FOLDER + "rootRsa.pem";
        String outFile = Paths.get(DESTINATION_FOLDER, "padesLTASignatureLevel"
                + (signatureCreator == null ? "NoSignatureCreator" : "CustomSignatureCreator") + ".pdf").toString();
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outFile));

        Certificate[] signChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, KEY_PASSPHRASE);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, KEY_PASSPHRASE);
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, KEY_PASSPHRASE);

        SignerProperties signerProperties = getSignerProperties(
                "Sign with baseline-LTA profile.\nCreated by iText.", signatureCreator);
        PdfPadesSigner padesSigner = new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(SOURCE_FILE)),
                FileUtil.getFileOutputStream(outFile));
        padesSigner.setEstimatedSize(0);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        ICrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey,
                DigestAlgorithms.SHA256);
        TestOcspClient ocspClient = new TestOcspClient()
                .addBuilderForCertIssuer(caCert, caPrivateKey, "SHA256withRSA")
                .addBuilderForCertIssuer((X509Certificate) tsaChain[0], tsaPrivateKey, DigestAlgorithms.SHA256);
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient).setTimestampSignatureName("timestampSig1");
        padesSigner.signWithBaselineLTAProfile(signerProperties, signChain, signPrivateKey, testTsa);

        PdfDocument outDoc = new PdfDocument(new PdfReader(outFile));
        Assertions.assertEquals("", getSignatureCreator(outDoc, "timestampSig1"));
        assertSignatureCreator(getSignatureCreator(outDoc, SIGNATURE_FIELD), signatureCreator);
    }

    private static String getSignatureCreator(PdfDocument document, String signatureName) {
        SignatureUtil signatureUtil = new SignatureUtil(document);
        PdfSignature signature = signatureUtil.getSignature(signatureName);
        return signature.getPdfObject().getAsDictionary(PdfName.Prop_Build).getAsDictionary(PdfName.App)
                .getAsName(PdfName.Name).getValue();
    }

    private static void assertSignatureCreator(String actualSignatureCreator, String expectedSignatureCreator)
            throws IOException {
        if (expectedSignatureCreator == null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfDocument doc = new PdfDocument(new PdfWriter(outputStream));
            doc.addNewPage();
            doc.close();
            try (PdfDocument regularPdf = new PdfDocument(new PdfReader(
                    new ByteArrayInputStream(outputStream.toByteArray())))) {
                Assertions.assertEquals(regularPdf.getDocumentInfo().getProducer(), actualSignatureCreator);
            }
        } else {
            Assertions.assertEquals(expectedSignatureCreator, actualSignatureCreator);
        }
    }

    private static SignerProperties getSignerProperties(String description, String signatureCreator) {
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent(description);
        SignerProperties signerProperties = new SignerProperties()
                .setFieldName(SIGNATURE_FIELD)
                .setPageRect(new Rectangle(50, 650, 200, 100))
                .setReason("Test")
                .setLocation("TestCity")
                .setSignatureAppearance(appearance);
        if (signatureCreator != null) {
            signerProperties.setSignatureCreator(signatureCreator);
        }

        return signerProperties;
    }
}
