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
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfPadesSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.TestSignUtils;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.AdvancedTestCrlClient;
import com.itextpdf.signatures.testutils.client.AdvancedTestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
@Category(BouncyCastleIntegrationTest.class)
public class PdfPadesAdvancedTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesAdvancedTest/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesAdvancedTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/PdfPadesAdvancedTest/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();
    
    private final String signingCertName;
    private final String rootCertName;
    private final Boolean isOcspRevoked;
    private final String cmpFilePostfix;
    
    private final Integer amountOfCrlsForSign;
    private final Integer amountOfOcspsForSign;
    private final Integer amountOfCrlsForRoot;
    private final Integer amountOfOcspsForRoot;

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public PdfPadesAdvancedTest(Object signingCertName, Object rootCertName, Object isOcspRevoked, Object cmpFilePostfix,
            Object amountOfCrlsForSign, Object amountOfOcspsForSign, Object amountOfCrlsForRoot, Object amountOfOcspsForRoot) {
        this.signingCertName = (String) signingCertName;
        this.rootCertName = (String) rootCertName;
        this.isOcspRevoked = (Boolean) isOcspRevoked;
        this.cmpFilePostfix = (String) cmpFilePostfix;
        
        this.amountOfCrlsForSign = (Integer) amountOfCrlsForSign;
        this.amountOfOcspsForSign = (Integer) amountOfOcspsForSign;
        this.amountOfCrlsForRoot = (Integer) amountOfCrlsForRoot;
        this.amountOfOcspsForRoot = (Integer) amountOfOcspsForRoot;
    }

    @Parameterized.Parameters(name = "{3}: signing cert: {0}; root cert: {1}; revoked: {2}")
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

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.OCSP_STATUS_IS_REVOKED), ignore = true)
    public void signWithAdvancedClientsTest()
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

        AdvancedTestOcspClient testOcspClient = new AdvancedTestOcspClient(null);
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
                Exception exception = Assert.assertThrows(PdfException.class,
                        () -> padesSigner.signWithBaselineLTAProfile(signerProperties, signRsaChain, pks, testTsa));
                Assert.assertEquals(SignExceptionMessageConstant.NO_REVOCATION_DATA_FOR_SIGNING_CERTIFICATE,
                        exception.getMessage());
            } finally {
                outputStream.close();
            }
        } else {
            padesSigner.signWithBaselineLTAProfile(signerProperties, signRsaChain, pks, testTsa);

            TestSignUtils.basicCheckSignedDoc(new ByteArrayInputStream(outputStream.toByteArray()), "Signature1");
            assertDss(outputStream, rootCert);
        }
    }
    
    private void assertDss(ByteArrayOutputStream outputStream, X509Certificate rootCert)
            throws AbstractOCSPException, CertificateException, IOException, CRLException {
        Map<String, Integer> expectedNumberOfCrls = new HashMap<>();
        if (amountOfCrlsForRoot + amountOfCrlsForSign != 0) {
            expectedNumberOfCrls.put(rootCert.getSubjectDN().getName(), amountOfCrlsForRoot + amountOfCrlsForSign);
        }
        Map<String, Integer> expectedNumberOfOcsps = new HashMap<>();
        if (amountOfOcspsForRoot + amountOfOcspsForSign != 0) {
            expectedNumberOfOcsps.put(rootCert.getSubjectDN().getName(), amountOfOcspsForRoot + amountOfOcspsForSign);
        }
        TestSignUtils.assertDssDict(new ByteArrayInputStream(outputStream.toByteArray()), expectedNumberOfCrls, expectedNumberOfOcsps);
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
