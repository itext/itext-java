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
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfPadesSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.TestSignUtils;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.AdvancedTestCrlClient;
import com.itextpdf.signatures.testutils.client.AdvancedTestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("BouncyCastleIntegrationTest")
public class PdfPadesAdvancedTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesAdvancedTest/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesAdvancedTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/PdfPadesAdvancedTest/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static Iterable<Object[]> createParameters() {
        List<Object[]> parameters = new ArrayList<>();
        parameters.addAll(createParametersUsingRootName("rootCertNoCrlNoOcsp", 0, 0));
        parameters.addAll(createParametersUsingRootName("rootCertCrlOcsp", 0, 1));
        parameters.addAll(createParametersUsingRootName("rootCertCrlNoOcsp", 1, 0));
        parameters.addAll(createParametersUsingRootName("rootCertOcspNoCrl", 0, 1));
        return parameters;
    }
    
    private static List<Object[]> createParametersUsingRootName(String rootCertName, int crlsForRoot, int ocspForRoot) {
        return Arrays.asList(
                new Object[] {"signCertCrlOcsp.pem", rootCertName + ".pem", false, "_signCertCrlOcsp_" + rootCertName,
                        0, 1, crlsForRoot, ocspForRoot},
                new Object[] {"signCertCrlOcsp.pem", rootCertName + ".pem", true, "_signCertCrlOcsp_" + rootCertName + "_revoked",
                        1, 0, crlsForRoot, ocspForRoot},
                new Object[] {"signCertOcspNoCrl.pem", rootCertName + ".pem", false, "_signCertOcspNoCrl_" + rootCertName,
                        0, 1, crlsForRoot, ocspForRoot},
                new Object[] {"signCertOcspNoCrl.pem", rootCertName + ".pem", true, "_signCertOcspNoCrl_" + rootCertName + "_revoked",
                        0, 0, crlsForRoot, ocspForRoot},
                new Object[] {"signCertNoOcspNoCrl.pem", rootCertName + ".pem", false, "_signCertNoOcspNoCrl_" + rootCertName,
                        0, 0, crlsForRoot, ocspForRoot},
                new Object[] {"signCertCrlNoOcsp.pem", rootCertName + ".pem", false, "_signCertCrlNoOcsp_" + rootCertName,
                        1, 0, crlsForRoot, ocspForRoot}
        );
    }

    @ParameterizedTest(name = "{3}: signing cert: {0}; root cert: {1}; revoked: {2}")
    @MethodSource("createParameters")
    public void signWithAdvancedClientsTest(String signingCertName, String rootCertName, Boolean isOcspRevoked,
            String cmpFilePostfix, Integer amountOfCrlsForSign, Integer amountOfOcspsForSign,
            Integer amountOfCrlsForRoot, Integer amountOfOcspsForRoot)
            throws IOException, GeneralSecurityException, AbstractOperatorCreationException, AbstractPKCSException, AbstractOCSPException {
        String srcFileName = SOURCE_FOLDER + "helloWorldDoc.pdf";
        String signCertFileName = CERTS_SRC + signingCertName;
        String rootCertFileName = CERTS_SRC + rootCertName;
        String tsaCertFileName = CERTS_SRC + "tsCertRsa.pem";

        X509Certificate signRsaCert = (X509Certificate) PemFileHelper.readFirstChain(signCertFileName)[0];
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        PrivateKey rootPrivateKey = PemFileHelper.readFirstKey(rootCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);

        AdvancedTestOcspClient testOcspClient = new AdvancedTestOcspClient();
        TestOcspResponseBuilder ocspBuilderMainCert = new TestOcspResponseBuilder(rootCert, rootPrivateKey);
        if ((boolean) isOcspRevoked) {
            ocspBuilderMainCert.setCertificateStatus(FACTORY.createRevokedStatus(TimeTestUtil.TEST_DATE_TIME,
                    FACTORY.createCRLReason().getKeyCompromise()));
        }
        TestOcspResponseBuilder ocspBuilderRootCert = new TestOcspResponseBuilder(rootCert, rootPrivateKey);
        testOcspClient.addBuilderForCertIssuer(signRsaCert, ocspBuilderMainCert);
        testOcspClient.addBuilderForCertIssuer(rootCert, ocspBuilderRootCert);

        AdvancedTestCrlClient testCrlClient = new AdvancedTestCrlClient();
        TestCrlBuilder crlBuilderMainCert = new TestCrlBuilder(rootCert, rootPrivateKey);
        crlBuilderMainCert.addCrlEntry(signRsaCert, FACTORY.createCRLReason().getKeyCompromise());
        crlBuilderMainCert.addCrlEntry(rootCert, FACTORY.createCRLReason().getKeyCompromise());
        
        TestCrlBuilder crlBuilderRootCert = new TestCrlBuilder(rootCert, rootPrivateKey);
        crlBuilderRootCert.addCrlEntry(rootCert, FACTORY.createCRLReason().getKeyCompromise());
        testCrlClient.addBuilderForCertIssuer(signRsaCert, crlBuilderMainCert);
        testCrlClient.addBuilderForCertIssuer(rootCert, crlBuilderRootCert);

        SignerProperties signerProperties = createSignerProperties();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfPadesSigner padesSigner = new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)),
                outputStream);
        padesSigner.setOcspClient(testOcspClient);
        padesSigner.setCrlClient(testCrlClient);

        IExternalSignature pks =
                new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        Certificate[] signRsaChain = new Certificate[] {signRsaCert, rootCert};
        if (signCertFileName.contains("NoOcspNoCrl") || (signCertFileName.contains("OcspNoCrl") && (boolean) isOcspRevoked)) {
            try {
                Exception exception = Assertions.assertThrows(PdfException.class,
                        () -> padesSigner.signWithBaselineLTAProfile(signerProperties, signRsaChain, pks, testTsa));
                Assertions.assertEquals(SignExceptionMessageConstant.NO_REVOCATION_DATA_FOR_SIGNING_CERTIFICATE,
                        exception.getMessage());
            } finally {
                outputStream.close();
            }
        } else {
            padesSigner.signWithBaselineLTAProfile(signerProperties, signRsaChain, pks, testTsa);

            TestSignUtils.basicCheckSignedDoc(new ByteArrayInputStream(outputStream.toByteArray()), "Signature1");
            assertDss(outputStream, rootCert, signRsaCert, (X509Certificate) tsaChain[0], (X509Certificate) tsaChain[1],
                    amountOfCrlsForRoot, amountOfCrlsForSign, amountOfOcspsForRoot, amountOfOcspsForSign);
        }
    }

    private void assertDss(ByteArrayOutputStream outputStream, X509Certificate rootCert, X509Certificate signRsaCert,
            X509Certificate tsaCert, X509Certificate rootTsaCert, Integer amountOfCrlsForRoot,
            Integer amountOfCrlsForSign, Integer amountOfOcspsForRoot, Integer amountOfOcspsForSign)
            throws AbstractOCSPException, CertificateException, IOException, CRLException {
        Map<String, Integer> expectedNumberOfCrls = new HashMap<>();
        if (amountOfCrlsForRoot + amountOfCrlsForSign != 0) {
            expectedNumberOfCrls.put(rootCert.getSubjectX500Principal().getName(), amountOfCrlsForRoot + amountOfCrlsForSign);
        }
        Map<String, Integer> expectedNumberOfOcsps = new HashMap<>();
        if (amountOfOcspsForRoot + amountOfOcspsForSign != 0) {
            expectedNumberOfOcsps.put(rootCert.getSubjectX500Principal().getName(), amountOfOcspsForRoot + amountOfOcspsForSign);
        }
        List<String> expectedCerts = Arrays.asList(getCertName(rootCert), getCertName(signRsaCert),
                getCertName(tsaCert), getCertName(rootTsaCert));
        TestSignUtils.assertDssDict(new ByteArrayInputStream(outputStream.toByteArray()), expectedNumberOfCrls,
                expectedNumberOfOcsps, expectedCerts);
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
