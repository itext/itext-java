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
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PadesTwoPhaseSigningHelper;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.TestSignUtils;
import com.itextpdf.signatures.cms.CMSContainer;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("BouncyCastleIntegrationTest")
public class PadesTwoPhaseSigningLevelsTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final boolean FIPS_MODE = "BCFIPS".equals(FACTORY.getProviderName());

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/sign/PadesTwoPhaseSigningLevelsTest/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PadesTwoPhaseSigningLevelsTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PadesTwoPhaseSigningLevelsTest/";

    private String signAlgorithm;
    private String signCertName;
    private String rootCertName;

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    public void setUp(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
        switch (this.signAlgorithm) {
            case "RSA":
                signCertName = "signCertRsa01.pem";
                rootCertName = "rootRsa.pem";
                break;
            case "RSASSA":
                signCertName = "signRSASSA.pem";
                rootCertName = "rootRSASSA.pem";
                break;
            case "ED448":
                signCertName = "signEd448.pem";
                rootCertName = "rootEd448.pem";
                break;
        }
        if ("ED448".equals(signAlgorithm)) {
            Assumptions.assumeFalse(FACTORY.isInApprovedOnlyMode());
        }
    }

    public static Iterable<Object[]> CreateParameters() {
        return Arrays.asList(new Object[] {true, DigestAlgorithms.SHA256, "RSA", 1},
                new Object[] {false, DigestAlgorithms.SHA256, "RSASSA", 2},
                new Object[] {false, DigestAlgorithms.SHAKE256, "ED448", 3},
                new Object[] {false, DigestAlgorithms.SHA3_384, "RSA", 4});
    }

    @ParameterizedTest(name = "{3}: folder path: {0}; digest algorithm: {1}; signature algorithm: {2}")
    @MethodSource("CreateParameters")
    // Android-Conversion-Ignore-Test (TODO DEVSIX-8113 Fix signatures tests)
    public void twoStepSigningBaselineBTest(Boolean useTempFolder, String digestAlgorithm, String signAlgorithm,
            Integer comparisonPdfId) throws Exception {
        setUp(signAlgorithm);
        String fileName = "twoStepSigningBaselineBTest" + comparisonPdfId + ".pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + signCertName;
        String rootCertFileName = certsSrc + rootCertName;

        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(signCertFileName)[0];
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        Certificate[] certChain = new X509Certificate[] {signCert, rootCert};
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);

        PadesTwoPhaseSigningHelper twoPhaseSigningHelper = new PadesTwoPhaseSigningHelper();
        
        if ((boolean) useTempFolder) {
            twoPhaseSigningHelper.setTemporaryDirectoryPath(destinationFolder);
        }

        try (ByteArrayOutputStream preparedDoc = new ByteArrayOutputStream()) {
            if (DigestAlgorithms.SHAKE256.equals(digestAlgorithm) && FIPS_MODE) {
                Assertions.assertThrows(NoSuchAlgorithmException.class, () ->
                        twoPhaseSigningHelper.createCMSContainerWithoutSignature(certChain, digestAlgorithm,
                                new PdfReader(srcFileName), preparedDoc, createSignerProperties()));
                return;
            }
            CMSContainer container = twoPhaseSigningHelper.createCMSContainerWithoutSignature(certChain,
                    digestAlgorithm, new PdfReader(srcFileName), preparedDoc, createSignerProperties());

            IExternalSignature externalSignature;
            if ("RSASSA".equals(signAlgorithm)) {
                externalSignature = new PrivateKeySignature(signPrivateKey, digestAlgorithm, "RSASSA-PSS", FACTORY.getProviderName(), null);
            } else {
                externalSignature = new PrivateKeySignature(signPrivateKey, digestAlgorithm, FACTORY.getProviderName());
            }
            
            twoPhaseSigningHelper.signCMSContainerWithBaselineBProfile(externalSignature,
                    new PdfReader(new ByteArrayInputStream(preparedDoc.toByteArray())),
                    FileUtil.getFileOutputStream(outFileName), "Signature1", container);
        }
        TestSignUtils.basicCheckSignedDoc(outFileName, "Signature1");
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @ParameterizedTest(name = "{3}: folder path: {0}; digest algorithm: {1}; signature algorithm: {2}")
    @MethodSource("CreateParameters")
    // Android-Conversion-Ignore-Test (TODO DEVSIX-8113 Fix signatures tests)
    public void twoStepSigningBaselineTTest(Boolean useTempFolder, String digestAlgorithm, String signAlgorithm,
            Integer comparisonPdfId) throws Exception {
        setUp(signAlgorithm);
        String fileName = "twoStepSigningBaselineTTest" + comparisonPdfId + ".pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + signCertName;
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String rootCertFileName = certsSrc + rootCertName;

        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(signCertFileName)[0];
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        Certificate[] certChain = new X509Certificate[] {signCert, rootCert};
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);

        PadesTwoPhaseSigningHelper twoPhaseSigningHelper = new PadesTwoPhaseSigningHelper();

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);

        twoPhaseSigningHelper.setTSAClient(testTsa);
        if ((boolean) useTempFolder) {
            twoPhaseSigningHelper.setTemporaryDirectoryPath(destinationFolder);
        }

        try (ByteArrayOutputStream preparedDoc = new ByteArrayOutputStream()) {
            if (DigestAlgorithms.SHAKE256.equals(digestAlgorithm) && FIPS_MODE) {
                Assertions.assertThrows(NoSuchAlgorithmException.class, () ->
                        twoPhaseSigningHelper.createCMSContainerWithoutSignature(certChain, digestAlgorithm,
                                new PdfReader(srcFileName), preparedDoc, createSignerProperties()));
                return;
            }
            CMSContainer container = twoPhaseSigningHelper.createCMSContainerWithoutSignature(certChain,
                    digestAlgorithm, new PdfReader(srcFileName), preparedDoc, createSignerProperties());

            IExternalSignature externalSignature;
            if ("RSASSA".equals(signAlgorithm)) {
                externalSignature = new PrivateKeySignature(signPrivateKey, digestAlgorithm, "RSASSA-PSS", FACTORY.getProviderName(), null);
            } else {
                externalSignature = new PrivateKeySignature(signPrivateKey, digestAlgorithm, FACTORY.getProviderName());
            }
            twoPhaseSigningHelper.signCMSContainerWithBaselineTProfile(externalSignature,
                    new PdfReader(new ByteArrayInputStream(preparedDoc.toByteArray())),
                    FileUtil.getFileOutputStream(outFileName), "Signature1", container);
        }
        TestSignUtils.basicCheckSignedDoc(outFileName, "Signature1");
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @ParameterizedTest(name = "{3}: folder path: {0}; digest algorithm: {1}; signature algorithm: {2}")
    @MethodSource("CreateParameters")
    // Android-Conversion-Ignore-Test (TODO DEVSIX-8113 Fix signatures tests)
    public void twoStepSigningBaselineLTTest(Boolean useTempFolder, String digestAlgorithm, String signAlgorithm,
            Integer comparisonPdfId) throws Exception {
        setUp(signAlgorithm);
        String fileName = "twoStepSigningBaselineLTTest" + comparisonPdfId + (FIPS_MODE && "RSASSA".equals(signAlgorithm) ? "_FIPS.pdf" : ".pdf");
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + signCertName;
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String caCertFileName = certsSrc + "rootRsa.pem";
        String rootCertFileName = certsSrc + rootCertName;

        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(signCertFileName)[0];
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        Certificate[] certChain = new X509Certificate[] {signCert, rootCert};
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, PASSWORD);

        PadesTwoPhaseSigningHelper twoPhaseSigningHelper = new PadesTwoPhaseSigningHelper();

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        TestCrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        crlClient.addBuilderForCertIssuer(rootCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        ocspClient.addBuilderForCertIssuer(rootCert, caPrivateKey);


        twoPhaseSigningHelper.setCrlClient(crlClient).setOcspClient(ocspClient).setTSAClient(testTsa);
        if ((boolean) useTempFolder) {
            twoPhaseSigningHelper.setTemporaryDirectoryPath(destinationFolder);
        }

        try (ByteArrayOutputStream preparedDoc = new ByteArrayOutputStream()) {
            if (DigestAlgorithms.SHAKE256.equals(digestAlgorithm) && FIPS_MODE) {
                Assertions.assertThrows(NoSuchAlgorithmException.class, () ->
                        twoPhaseSigningHelper.createCMSContainerWithoutSignature(certChain, digestAlgorithm,
                                new PdfReader(srcFileName), preparedDoc, createSignerProperties()));
                return;
            }
            CMSContainer container = twoPhaseSigningHelper.createCMSContainerWithoutSignature(certChain,
                    digestAlgorithm, new PdfReader(srcFileName), preparedDoc, createSignerProperties());

            IExternalSignature externalSignature;
            if ("RSASSA".equals(signAlgorithm)) {
                externalSignature = new PrivateKeySignature(signPrivateKey, digestAlgorithm, "RSASSA-PSS", FACTORY.getProviderName(), null);
            } else {
                externalSignature = new PrivateKeySignature(signPrivateKey, digestAlgorithm, FACTORY.getProviderName());
            }
            twoPhaseSigningHelper.signCMSContainerWithBaselineLTProfile(externalSignature,
                    new PdfReader(new ByteArrayInputStream(preparedDoc.toByteArray())),
                    FileUtil.getFileOutputStream(outFileName), "Signature1", container);
        }
        TestSignUtils.basicCheckSignedDoc(outFileName, "Signature1");
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @ParameterizedTest(name = "{3}: folder path: {0}; digest algorithm: {1}; signature algorithm: {2}")
    @MethodSource("CreateParameters")
    // Android-Conversion-Ignore-Test (TODO DEVSIX-8113 Fix signatures tests)
    public void twoStepSigningBaselineLTATest(Boolean useTempFolder, String digestAlgorithm, String signAlgorithm,
            Integer comparisonPdfId) throws Exception {
        setUp(signAlgorithm);
        String fileName = "twoStepSigningBaselineLTATest" + comparisonPdfId + (FIPS_MODE && "RSASSA".equals(signAlgorithm) ? "_FIPS.pdf" : ".pdf");
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + signCertName;
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String caCertFileName = certsSrc + "rootRsa.pem";;
        String rootCertFileName = certsSrc + rootCertName;

        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(signCertFileName)[0];
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        Certificate[] certChain = new X509Certificate[] {signCert, rootCert};
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, PASSWORD);

        PadesTwoPhaseSigningHelper twoPhaseSigningHelper = new PadesTwoPhaseSigningHelper();

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        TestCrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        crlClient.addBuilderForCertIssuer(rootCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        ocspClient.addBuilderForCertIssuer(rootCert, caPrivateKey);

        twoPhaseSigningHelper.setCrlClient(crlClient).setOcspClient(ocspClient).setTSAClient(testTsa)
                .setTimestampSignatureName("timestampSig1");
        if ((boolean) useTempFolder) {
            twoPhaseSigningHelper.setTemporaryDirectoryPath(destinationFolder);
        }
        
        try (ByteArrayOutputStream preparedDoc = new ByteArrayOutputStream()) {
            if (DigestAlgorithms.SHAKE256.equals(digestAlgorithm) && FIPS_MODE) {
                Assertions.assertThrows(NoSuchAlgorithmException.class, () ->
                        twoPhaseSigningHelper.createCMSContainerWithoutSignature(certChain, digestAlgorithm,
                                new PdfReader(srcFileName), preparedDoc, createSignerProperties()));
                return;
            }
            CMSContainer container = twoPhaseSigningHelper.createCMSContainerWithoutSignature(certChain,
                    digestAlgorithm, new PdfReader(srcFileName), preparedDoc, createSignerProperties());

            IExternalSignature externalSignature;
            if ("RSASSA".equals(signAlgorithm)) {
                externalSignature = new PrivateKeySignature(signPrivateKey, digestAlgorithm, "RSASSA-PSS", FACTORY.getProviderName(), null);
            } else {
                externalSignature = new PrivateKeySignature(signPrivateKey, digestAlgorithm, FACTORY.getProviderName());
            }
            twoPhaseSigningHelper.signCMSContainerWithBaselineLTAProfile(externalSignature,
                    new PdfReader(new ByteArrayInputStream(preparedDoc.toByteArray())),
                    FileUtil.getFileOutputStream(outFileName), "Signature1", container);
        }
        TestSignUtils.basicCheckSignedDoc(outFileName, "Signature1");
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    private SignerProperties createSignerProperties() {
        SignerProperties signerProperties = new SignerProperties();
        signerProperties.setFieldName("Signature1");
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(signerProperties.getFieldName())
                .setContent("Approval test signature.\nCreated by iText.");
        signerProperties.setPageRect(new Rectangle(50, 650, 200, 100))
                .setSignatureAppearance(appearance);

        return signerProperties;
    }
}
