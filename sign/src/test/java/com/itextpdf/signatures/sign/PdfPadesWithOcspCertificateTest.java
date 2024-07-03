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
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPadesSigner;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.TestSignUtils;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.client.AdvancedTestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class PdfPadesWithOcspCertificateTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesWithOcspCertificateTest/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesWithOcspCertificateTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PdfPadesWithOcspCertificateTest/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(destinationFolder);
    }
    
    @Test
    public void signCertWithOcspTest() throws GeneralSecurityException, IOException, AbstractOperatorCreationException,
            AbstractPKCSException, AbstractOCSPException {
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signRsaWithOcsp.pem";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String rootCertFileName = certsSrc + "rootRsa2.pem";
        String ocspCertFileName = certsSrc + "ocspCert.pem";

        X509Certificate signRsaCert = (X509Certificate) PemFileHelper.readFirstChain(signCertFileName)[0];
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);
        X509Certificate ocspCert = (X509Certificate) PemFileHelper.readFirstChain(ocspCertFileName)[0];
        PrivateKey ocspPrivateKey = PemFileHelper.readFirstKey(ocspCertFileName, PASSWORD);

        SignerProperties signerProperties = createSignerProperties();
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        AdvancedTestOcspClient ocspClient = new AdvancedTestOcspClient(null);
        ocspClient.addBuilderForCertIssuer(signRsaCert, ocspCert, ocspPrivateKey);
        ocspClient.addBuilderForCertIssuer(ocspCert, ocspCert, ocspPrivateKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outputStream);
        padesSigner.setOcspClient(ocspClient);
        Certificate[] signRsaChain = new Certificate[] {signRsaCert, rootCert};
        padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, signRsaPrivateKey, testTsa);
        outputStream.close();
        
        TestSignUtils.basicCheckSignedDoc(new ByteArrayInputStream(outputStream.toByteArray()), "Signature1");

        Map<String, Integer> expectedNumberOfCrls = new HashMap<>();
        Map<String, Integer> expectedNumberOfOcsps = new HashMap<>();
        List<String> expectedCerts = Arrays.asList(getCertName(rootCert), getCertName(signRsaCert), getCertName(
                (X509Certificate) tsaChain[0]), getCertName((X509Certificate) tsaChain[1]), getCertName(ocspCert));
        // It is expected to have two OCSP responses, one for signing cert and another for OCSP response.
        expectedNumberOfOcsps.put(ocspCert.getSubjectX500Principal().getName(), 2);
        TestSignUtils.assertDssDict(new ByteArrayInputStream(outputStream.toByteArray()),
                expectedNumberOfCrls, expectedNumberOfOcsps, expectedCerts);
    }

    @Test
    public void signCertWithoutOcspTest()
            throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signRsaWithoutOcsp.pem";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String rootCertFileName = certsSrc + "rootRsa.pem";
        String ocspCertFileName = certsSrc + "ocspCert.pem";

        Certificate signRsaCert = PemFileHelper.readFirstChain(signCertFileName)[0];
        Certificate rootCert = PemFileHelper.readFirstChain(rootCertFileName)[0];
        Certificate[] signRsaChain = new Certificate[2];
        signRsaChain[0] = signRsaCert;
        signRsaChain[1] = rootCert;
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);
        Certificate ocspCert = PemFileHelper.readFirstChain(ocspCertFileName)[0];
        PrivateKey ocspPrivateKey = PemFileHelper.readFirstKey(ocspCertFileName, PASSWORD);

        SignerProperties signerProperties = createSignerProperties();
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        AdvancedTestOcspClient ocspClient = new AdvancedTestOcspClient(null);
        ocspClient.addBuilderForCertIssuer((X509Certificate) signRsaCert, (X509Certificate) ocspCert, ocspPrivateKey);
        ocspClient.addBuilderForCertIssuer((X509Certificate) ocspCert, (X509Certificate) ocspCert, ocspPrivateKey);

        try (OutputStream outputStream = new ByteArrayOutputStream()) {
            PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outputStream);
            padesSigner.setOcspClient(ocspClient);
            
            Exception exception = Assertions.assertThrows(PdfException.class, () -> 
                    padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, signRsaPrivateKey, testTsa));
            Assertions.assertEquals(SignExceptionMessageConstant.NO_REVOCATION_DATA_FOR_SIGNING_CERTIFICATE, exception.getMessage());
        }
    }

    @Test
    public void signCertWithOcspOcspCertSameAsSignCertTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException, AbstractOCSPException {
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signRsaWithOcsp.pem";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String rootCertFileName = certsSrc + "rootRsa2.pem";

        X509Certificate signRsaCert = (X509Certificate) PemFileHelper.readFirstChain(signCertFileName)[0];
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);

        SignerProperties signerProperties = createSignerProperties();
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        AdvancedTestOcspClient ocspClient = new AdvancedTestOcspClient(null);
        ocspClient.addBuilderForCertIssuer(signRsaCert, signRsaCert, signRsaPrivateKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outputStream);
        padesSigner.setOcspClient(ocspClient);
        X509Certificate[] signRsaChain = new X509Certificate[] {signRsaCert, rootCert};
        padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, signRsaPrivateKey, testTsa);

        TestSignUtils.basicCheckSignedDoc(new ByteArrayInputStream(outputStream.toByteArray()), "Signature1");

        Map<String, Integer> expectedNumberOfCrls = new HashMap<>();
        Map<String, Integer> expectedNumberOfOcsps = new HashMap<>();
        List<String> expectedCerts = Arrays.asList(getCertName(rootCert), getCertName(signRsaCert), getCertName(
                (X509Certificate) tsaChain[0]), getCertName((X509Certificate) tsaChain[1]));
        // It is expected to have one OCSP response, only for signing cert.
        expectedNumberOfOcsps.put(signRsaCert.getSubjectX500Principal().getName(), 1);
        TestSignUtils.assertDssDict(new ByteArrayInputStream(outputStream.toByteArray()),
                expectedNumberOfCrls, expectedNumberOfOcsps, expectedCerts);
    }

    private String getCertName(X509Certificate certificate) {
        return certificate.getSubjectX500Principal().getName();
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

    private PdfPadesSigner createPdfPadesSigner(String srcFileName, OutputStream outputStream) throws IOException {
        return new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)), outputStream);
    }
}
