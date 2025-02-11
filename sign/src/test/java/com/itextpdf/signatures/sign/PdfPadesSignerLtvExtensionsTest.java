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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfPadesSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.TestSignUtils;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleIntegrationTest")
public class PdfPadesSignerLtvExtensionsTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesSignerLtvExtensionsTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesSignerLtvExtensionsTest/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }
    
    @Test
    public void ocspNoCheckExtensionTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException, AbstractOCSPException {
        String srcFileName = SOURCE_FOLDER + "helloWorldDoc.pdf";
        String signCertFileName = CERTS_SRC + "signCertRsa01.pem";
        String rootCertFileName = CERTS_SRC + "rootRsa.pem";
        String tsaCertFileName = CERTS_SRC + "tsCertRsa.pem";
        String ocspCertFileName = CERTS_SRC + "ocspCert.pem";

        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(signCertFileName)[0];
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        PrivateKey rootPrivateKey = PemFileHelper.readFirstKey(rootCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);
        X509Certificate ocspCert = (X509Certificate) PemFileHelper.readFirstChain(ocspCertFileName)[0];
        PrivateKey ocspPrivateKey = PemFileHelper.readFirstKey(ocspCertFileName, PASSWORD);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);

        TestOcspClient testOcspClient = new TestOcspClient();
        TestOcspResponseBuilder ocspBuilderRootCert = new TestOcspResponseBuilder(ocspCert, ocspPrivateKey);
        TestOcspResponseBuilder ocspBuilderOcspCert = new TestOcspResponseBuilder(rootCert, rootPrivateKey);
        testOcspClient.addBuilderForCertIssuer(rootCert, ocspBuilderRootCert);
        testOcspClient.addBuilderForCertIssuer(ocspCert, ocspBuilderOcspCert);

        SignerProperties signerProperties = createSignerProperties();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfPadesSigner padesSigner = new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)),
                    outputStream);
            padesSigner.setOcspClient(testOcspClient);

            IExternalSignature pks =
                    new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
            Certificate[] signRsaChain = new Certificate[] {signCert, rootCert};
            
            padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, pks, testTsa);

            TestSignUtils.basicCheckSignedDoc(new ByteArrayInputStream(outputStream.toByteArray()), "Signature1");
            Map<String, Integer> expectedNumberOfOcsps = new HashMap<>();
            expectedNumberOfOcsps.put(ocspCert.getSubjectX500Principal().getName(), 3);
            List<String> expectedCerts = Arrays.asList(getCertName(rootCert), getCertName(signCert),
                    getCertName((X509Certificate) tsaChain[0]), getCertName(ocspCert));
            TestSignUtils.assertDssDict(new ByteArrayInputStream(outputStream.toByteArray()), new HashMap<>(),
                    expectedNumberOfOcsps, expectedCerts);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.REVOCATION_DATA_NOT_ADDED_VALIDITY_ASSURED, logLevel = LogLevelConstants.INFO))
    public void validityAssuredExtensionTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException, AbstractOCSPException {
        String srcFileName = SOURCE_FOLDER + "helloWorldDoc.pdf";
        String signCertFileName = CERTS_SRC + "validityAssuredSigningCert.pem";
        String rootCertFileName = CERTS_SRC + "rootRsa.pem";
        String tsaCertFileName = CERTS_SRC + "tsCertRsa.pem";

        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(signCertFileName)[0];
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        PrivateKey rootPrivateKey = PemFileHelper.readFirstKey(rootCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);

        TestOcspClient testOcspClient = new TestOcspClient();
        testOcspClient.addBuilderForCertIssuer(rootCert, rootPrivateKey);

        SignerProperties signerProperties = createSignerProperties();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfPadesSigner padesSigner = new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)),
                    outputStream);
            padesSigner.setOcspClient(testOcspClient);

            IExternalSignature pks =
                    new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
            Certificate[] signRsaChain = new Certificate[] {signCert, rootCert};

            padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, pks, testTsa);

            TestSignUtils.basicCheckSignedDoc(new ByteArrayInputStream(outputStream.toByteArray()), "Signature1");
            Map<String, Integer> expectedNumberOfOcsps = new HashMap<>();
            expectedNumberOfOcsps.put(rootCert.getSubjectX500Principal().getName(), 2);
            List<String> expectedCerts = Arrays.asList(getCertName(rootCert), getCertName(signCert),
                    getCertName((X509Certificate) tsaChain[0]));
            TestSignUtils.assertDssDict(new ByteArrayInputStream(outputStream.toByteArray()), new HashMap<>(),
                    expectedNumberOfOcsps, expectedCerts);
        }
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
