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
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfPadesSigner;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public PdfPadesAdvancedTest(Object signingCertName, Object rootCertName, Object isOcspRevoked, Object cmpFilePostfix) {
        this.signingCertName = (String) signingCertName;
        this.rootCertName = (String) rootCertName;
        this.isOcspRevoked = (Boolean) isOcspRevoked;
        this.cmpFilePostfix = (String) cmpFilePostfix;
    }

    @Parameterized.Parameters(name = "{3}: signing cert: {0}; root cert: {1}; revoked: {2}")
    public static Iterable<Object[]> createParameters() {
        List<Object[]> parameters = new ArrayList<>();
        parameters.addAll(createParametersUsingRootName("rootCertNoCrlNoOcsp"));
        parameters.addAll(createParametersUsingRootName("rootCertCrlOcsp"));
        parameters.addAll(createParametersUsingRootName("rootCertCrlNoOcsp"));
        parameters.addAll(createParametersUsingRootName("rootCertOcspNoCrl"));
        return parameters;
    }
    
    private static List<Object[]> createParametersUsingRootName(String rootCertName) {
        return Arrays.asList(
                new Object[] {"signCertCrlOcsp.pem", rootCertName + ".pem", false, "_signCertCrlOcsp_" + rootCertName},
                new Object[] {"signCertCrlOcsp.pem", rootCertName + ".pem", true, "_signCertCrlOcsp_" + rootCertName + "_revoked"},
                new Object[] {"signCertOcspNoCrl.pem", rootCertName + ".pem", false, "_signCertOcspNoCrl_" + rootCertName},
                new Object[] {"signCertOcspNoCrl.pem", rootCertName + ".pem", true, "_signCertOcspNoCrl_" + rootCertName + "_revoked"},
                new Object[] {"signCertNoOcspNoCrl.pem", rootCertName + ".pem", false, "_signCertNoOcspNoCrl_" + rootCertName},
                new Object[] {"signCertCrlNoOcsp.pem", rootCertName + ".pem", false, "_signCertCrlNoOcsp_" + rootCertName}
        );
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.OCSP_STATUS_IS_REVOKED), ignore = true)
    public void signWithAdvancedClientsTest()
            throws IOException, GeneralSecurityException, AbstractOperatorCreationException, AbstractPKCSException {
        String fileName = "signedWith" + cmpFilePostfix + ".pdf";
        String outFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;
        String srcFileName = SOURCE_FOLDER + "helloWorldDoc.pdf";
        String signCertFileName = CERTS_SRC + signingCertName;
        String rootCertFileName = CERTS_SRC + rootCertName;
        String tsaCertFileName = CERTS_SRC + "tsCertRsa.pem";

        Certificate signRsaCert = PemFileHelper.readFirstChain(signCertFileName)[0];
        Certificate rootCert = PemFileHelper.readFirstChain(rootCertFileName)[0];
        Certificate[] signRsaChain = new Certificate[2];
        signRsaChain[0] = signRsaCert;
        signRsaChain[1] = rootCert;
        
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        PrivateKey rootPrivateKey = PemFileHelper.readFirstKey(rootCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);

        AdvancedTestOcspClient testOcspClient = new AdvancedTestOcspClient(null);
        TestOcspResponseBuilder ocspBuilderMainCert = new TestOcspResponseBuilder((X509Certificate) signRsaChain[1], rootPrivateKey);
        if ((boolean) isOcspRevoked) {
            ocspBuilderMainCert.setCertificateStatus(FACTORY.createRevokedStatus(TimeTestUtil.TEST_DATE_TIME,
                    FACTORY.createCRLReason().getKeyCompromise()));
        }
        TestOcspResponseBuilder ocspBuilderRootCert = new TestOcspResponseBuilder((X509Certificate) signRsaChain[1], rootPrivateKey);
        testOcspClient.addBuilderForCertIssuer((X509Certificate) signRsaChain[0], ocspBuilderMainCert);
        testOcspClient.addBuilderForCertIssuer((X509Certificate) signRsaChain[1], ocspBuilderRootCert);

        AdvancedTestCrlClient testCrlClient = new AdvancedTestCrlClient();
        TestCrlBuilder crlBuilderMainCert = new TestCrlBuilder((X509Certificate) signRsaChain[1], rootPrivateKey);
        crlBuilderMainCert.addCrlEntry((X509Certificate) signRsaChain[0], FACTORY.createCRLReason().getKeyCompromise());
        crlBuilderMainCert.addCrlEntry((X509Certificate) signRsaChain[1], FACTORY.createCRLReason().getKeyCompromise());
        
        TestCrlBuilder crlBuilderRootCert = new TestCrlBuilder((X509Certificate) signRsaChain[1], rootPrivateKey);
        crlBuilderRootCert.addCrlEntry((X509Certificate) signRsaChain[1], FACTORY.createCRLReason().getKeyCompromise());
        testCrlClient.addBuilderForCertIssuer((X509Certificate) signRsaChain[0], crlBuilderMainCert);
        testCrlClient.addBuilderForCertIssuer((X509Certificate) signRsaChain[1], crlBuilderRootCert);

        PdfSigner signer = createPdfSigner(srcFileName, outFileName);

        PdfPadesSigner padesSigner = new PdfPadesSigner();
        padesSigner.setOcspClient(testOcspClient);
        padesSigner.setCrlClient(testCrlClient);

        IExternalSignature pks =
                new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        padesSigner.signWithBaselineLTAProfile(signer, signRsaChain, pks, testTsa);

        PadesSigTest.basicCheckSignedDoc(outFileName, "Signature1");

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    private PdfSigner createPdfSigner(String srcFileName, String outFileName) throws IOException {
        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), FileUtil.getFileOutputStream(outFileName),
                new StampingProperties());
        signer.setFieldName("Signature1");
        signer.getSignatureAppearance()
                .setPageRect(new Rectangle(50, 650, 200, 100))
                .setReason("Test")
                .setLocation("TestCity")
                .setLayer2Text("Approval test signature.\nCreated by iText.");
        return signer;
    }
}
