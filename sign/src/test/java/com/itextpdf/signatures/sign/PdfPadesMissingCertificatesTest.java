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
package com.itextpdf.signatures.sign;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.IIssuingCertificateRetriever;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.PdfPadesSigner;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.TestSignUtils;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.client.AdvancedTestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfPadesMissingCertificatesTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesMissingCertificatesTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/signatures/sign/PdfPadesMissingCertificatesTest/";
    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void certificateFromAiaIsIncorrectTest() throws Exception {
        String srcFileName = SOURCE_FOLDER + "helloWorldDoc.pdf";
        String rootCertFileName = CERTS_SRC + "root.pem";
        String intermediateCertFileName = CERTS_SRC + "intermediate.pem";
        String signCertFileName = CERTS_SRC + "sign.pem";

        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(signCertFileName)[0];
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);

        SignerProperties signerProperties = createSignerProperties();

        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        X509Certificate intermediateCert = (X509Certificate) PemFileHelper.readFirstChain(intermediateCertFileName)[0];

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfPadesSigner padesSigner =
                new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)), outputStream);
        IssuingCertificateRetriever issuingCertificateRetriever = new IssuingCertificateRetriever() {
            @Override
            protected InputStream getIssuerCertByURI(String uri) throws IOException {
                return FileUtil.getInputStreamForFile(rootCertFileName);
            }
        };
        issuingCertificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
        padesSigner.setIssuingCertificateRetriever(issuingCertificateRetriever);


        Certificate[] signChain = new Certificate[]{signCert, rootCert};
        padesSigner.signWithBaselineBProfile(signerProperties, signChain, signPrivateKey);

        outputStream.close();

        TestSignUtils.basicCheckSignedDoc(new ByteArrayInputStream(outputStream.toByteArray()), "Signature1");

        List<X509Certificate> certs = Arrays.asList(rootCert, intermediateCert, signCert);
        TestSignUtils.signedDocumentContainsCerts(new ByteArrayInputStream(outputStream.toByteArray()), certs, "Signature1");
    }

    @Test
    public void retrieveMissingCertificatesTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException, AbstractOCSPException {
        String srcFileName = SOURCE_FOLDER + "helloWorldDoc.pdf";
        String rootCertFileName = CERTS_SRC + "root.pem";
        String intermediateCertFileName = CERTS_SRC + "intermediate.pem";
        String signCertFileName = CERTS_SRC + "sign.pem";
        String rootCrlFileName = CERTS_SRC + "crlRoot.pem";
        String intermediateCrlFileName = CERTS_SRC + "crlIntermediate.pem";
        String crlCertFileName = CERTS_SRC + "crlCert.pem";
        String rootOcspFileName = CERTS_SRC + "ocspRoot.pem";
        String intermediateOscpFileName = CERTS_SRC + "ocspIntermediate.pem";
        String ocspCertFileName = CERTS_SRC + "ocspCert.pem";
        String rootTsaFileName = CERTS_SRC + "tsaRoot.pem";
        String intermediateTsaFileName = CERTS_SRC + "tsaIntermediate.pem";
        String tsaCertFileName = CERTS_SRC + "tsaCert.pem";
        String crlSignedByCA = CERTS_SRC + "crlSignedByCA.crl";
        String crlSignedByCrlCert = CERTS_SRC + "crlSignedByCrlCert.crl";

        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(signCertFileName)[0];
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        X509Certificate crlCert = (X509Certificate) PemFileHelper.readFirstChain(crlCertFileName)[0];
        X509Certificate ocspCert = (X509Certificate) PemFileHelper.readFirstChain(ocspCertFileName)[0];
        PrivateKey ocspPrivateKey = PemFileHelper.readFirstKey(ocspCertFileName, PASSWORD);
        X509Certificate tsaCert = (X509Certificate) PemFileHelper.readFirstChain(tsaCertFileName)[0];
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);

        SignerProperties signerProperties = createSignerProperties();
        TestTsaClient testTsa = new TestTsaClient(Collections.singletonList(tsaCert), tsaPrivateKey);

        CrlClientOnline testCrlClient = new CrlClientOnline() {
            @Override
            protected InputStream getCrlResponse(X509Certificate cert, URL urlt) throws IOException {
                if (urlt.toString().contains("sign-crl")) {
                    return FileUtil.getInputStreamForFile(crlSignedByCrlCert);
                }
                return FileUtil.getInputStreamForFile(crlSignedByCA);
            }
        };

        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        X509Certificate crlRootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCrlFileName)[0];
        X509Certificate ocspRootCert = (X509Certificate) PemFileHelper.readFirstChain(rootOcspFileName)[0];
        X509Certificate tsaRootCert = (X509Certificate) PemFileHelper.readFirstChain(rootTsaFileName)[0];
        X509Certificate crlIntermediateCert = (X509Certificate) PemFileHelper.readFirstChain(intermediateCrlFileName)[0];
        X509Certificate ocspIntermediateCert = (X509Certificate) PemFileHelper.readFirstChain(intermediateOscpFileName)[0];
        X509Certificate tsaIntermediateCert = (X509Certificate) PemFileHelper.readFirstChain(intermediateTsaFileName)[0];
        X509Certificate intermediateCert = (X509Certificate) PemFileHelper.readFirstChain(intermediateCertFileName)[0];

        AdvancedTestOcspClient ocspClient = new AdvancedTestOcspClient();
        ocspClient.addBuilderForCertIssuer(signCert, ocspCert, ocspPrivateKey);
        ocspClient.addBuilderForCertIssuer(ocspIntermediateCert, ocspCert, ocspPrivateKey);
        ocspClient.addBuilderForCertIssuer(crlIntermediateCert, ocspCert, ocspPrivateKey);
        ocspClient.addBuilderForCertIssuer(tsaIntermediateCert, ocspCert, ocspPrivateKey);

        ocspClient.addBuilderForCertIssuer(rootCert, ocspCert, ocspPrivateKey);
        ocspClient.addBuilderForCertIssuer(crlRootCert, ocspCert, ocspPrivateKey);
        ocspClient.addBuilderForCertIssuer(ocspRootCert, ocspCert, ocspPrivateKey);
        ocspClient.addBuilderForCertIssuer(tsaRootCert, ocspCert, ocspPrivateKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfPadesSigner padesSigner =
                new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)), outputStream);
        padesSigner.setCrlClient(testCrlClient);
        padesSigner.setOcspClient(ocspClient);
        IIssuingCertificateRetriever issuingCertificateRetriever = new IssuingCertificateRetriever() {
            @Override
            protected InputStream getIssuerCertByURI(String uri) throws IOException {
                if (uri.contains("crl_cert")) {
                    return FileUtil.getInputStreamForFile(crlCertFileName);
                }
                if (uri.contains("crl_intermediate")) {
                    return FileUtil.getInputStreamForFile(intermediateCrlFileName);
                }
                if (uri.contains("crl_root")) {
                    return FileUtil.getInputStreamForFile(rootCrlFileName);
                }
                if (uri.contains("tsa_intermediate")) {
                    return FileUtil.getInputStreamForFile(intermediateTsaFileName);
                }
                if (uri.contains("tsa_root")) {
                    return FileUtil.getInputStreamForFile(rootTsaFileName);
                }
                if (uri.contains("ocsp_intermediate")) {
                    return FileUtil.getInputStreamForFile(intermediateOscpFileName);
                }
                if (uri.contains("ocsp_root")) {
                    return FileUtil.getInputStreamForFile(rootOcspFileName);
                }
                if (uri.contains("intermediate")) {
                    return FileUtil.getInputStreamForFile(intermediateCertFileName);
                }
                return FileUtil.getInputStreamForFile(rootCertFileName);
            }
        };
        padesSigner.setIssuingCertificateRetriever(issuingCertificateRetriever);


        Certificate[] signChain = new Certificate[]{signCert};
        padesSigner.signWithBaselineLTProfile(signerProperties, signChain, signPrivateKey, testTsa);

        outputStream.close();

        TestSignUtils.basicCheckSignedDoc(new ByteArrayInputStream(outputStream.toByteArray()), "Signature1");

        Map<String, Integer> expectedNumberOfCrls = new HashMap<>();
        Map<String, Integer> expectedNumberOfOcsps = new HashMap<>();
        // It is expected to have two CRL responses, one for intermediate cert and another for leaf CRL/OCSP/TSA certs.
        expectedNumberOfCrls.put(crlCert.getSubjectX500Principal().getName(), 1);
        expectedNumberOfCrls.put(rootCert.getSubjectX500Principal().getName(), 1);
        // It is expected to have OCSP responses for all the root, CRL/OCSP/TSA intermediate certs, and signing cert.
        expectedNumberOfOcsps.put(ocspCert.getSubjectX500Principal().getName(), 8);
        List<String> certs = Arrays.asList(getCertName(rootCert), getCertName(crlRootCert), getCertName(crlCert),
                getCertName(ocspCert), getCertName(tsaRootCert), getCertName(crlIntermediateCert),
                getCertName(ocspIntermediateCert), getCertName(tsaIntermediateCert), getCertName(ocspRootCert),
                getCertName(signCert), getCertName(tsaCert), getCertName(intermediateCert));
        TestSignUtils.assertDssDict(new ByteArrayInputStream(outputStream.toByteArray()),
                expectedNumberOfCrls, expectedNumberOfOcsps, certs);
    }

    @Test
    public void retrieveMissingCertificatesUsingTrustedStoreTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException, AbstractOCSPException {
        String srcFileName = SOURCE_FOLDER + "helloWorldDoc.pdf";
        String rootCertFileName = SOURCE_FOLDER + "root.pem";
        String signCertFileName = SOURCE_FOLDER + "sign.pem";
        String rootCrlFileName = SOURCE_FOLDER + "crlRoot.pem";
        String crlCertFileName = SOURCE_FOLDER + "crlCert.pem";
        String tsaCertFileName = SOURCE_FOLDER + "tsCert.pem";
        String crlSignedByCA = SOURCE_FOLDER + "crlWithRootIssuer.crl";
        String crlSignedByCrlCert = SOURCE_FOLDER + "crlWithCrlIssuer.crl";

        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(signCertFileName)[0];
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        X509Certificate crlCert = (X509Certificate) PemFileHelper.readFirstChain(crlCertFileName)[0];
        X509Certificate tsaCert = (X509Certificate) PemFileHelper.readFirstChain(tsaCertFileName)[0];
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);

        SignerProperties signerProperties = createSignerProperties();
        TestTsaClient testTsa = new TestTsaClient(Collections.singletonList(tsaCert), tsaPrivateKey);

        CrlClientOnline testCrlClient = new CrlClientOnline() {
            @Override
            protected InputStream getCrlResponse(X509Certificate cert, URL urlt) throws IOException {
                if (urlt.toString().contains("cert-crl")) {
                    return FileUtil.getInputStreamForFile(crlSignedByCrlCert);
                }
                return FileUtil.getInputStreamForFile(crlSignedByCA);
            }
        };

        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        X509Certificate crlRootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCrlFileName)[0];

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfPadesSigner padesSigner =
                new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)), outputStream);
        padesSigner.setCrlClient(testCrlClient);

        List<Certificate> trustedCertificates = new ArrayList<>();
        trustedCertificates.add(rootCert);
        trustedCertificates.add(crlRootCert);
        trustedCertificates.add(crlCert);
        padesSigner.setTrustedCertificates(trustedCertificates);

        Certificate[] signChain = new Certificate[]{signCert};
        padesSigner.signWithBaselineLTProfile(signerProperties, signChain, signPrivateKey, testTsa);

        outputStream.close();

        TestSignUtils.basicCheckSignedDoc(new ByteArrayInputStream(outputStream.toByteArray()), "Signature1");

        Map<String, Integer> expectedNumberOfCrls = new HashMap<>();
        Map<String, Integer> expectedNumberOfOcsps = new HashMap<>();
        // It is expected to have two CRL responses, one for signing cert and another for CRL response.
        expectedNumberOfCrls.put(crlCert.getSubjectX500Principal().getName(), 1);
        expectedNumberOfCrls.put(rootCert.getSubjectX500Principal().getName(), 1);
        List<String> certs = Arrays.asList(getCertName(rootCert), getCertName(crlRootCert), getCertName(crlCert),
                getCertName(signCert), getCertName(tsaCert));
        TestSignUtils.assertDssDict(new ByteArrayInputStream(outputStream.toByteArray()),
                expectedNumberOfCrls, expectedNumberOfOcsps, certs);
    }

    private String getCertName(X509Certificate certificate) {
        return certificate.getSubjectX500Principal().getName();
    }

    private SignerProperties createSignerProperties() {
        SignerProperties signerProperties = new SignerProperties();
        signerProperties.setFieldName("Signature1");
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent("Approval test signature.\nCreated by iText.");
        signerProperties.setPageRect(new Rectangle(50, 650, 200, 100))
                .setSignatureAppearance(appearance);

        return signerProperties;
    }
}
