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
import com.itextpdf.signatures.PdfPadesSigner;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.TestSignUtils;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.client.AdvancedTestCrlClient;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class PdfPadesWithTimestampCertificateTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesWithTimestampCertificateTest/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesWithTimestampCertificateTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PdfPadesWithTimestampCertificateTest/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(destinationFolder);
    }
    
    @Test
    public void differentTimestampCertTest()
            throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException, AbstractOCSPException {
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signCertWithCrl.pem";
        String tsaCertFileName = certsSrc + "tsCertWithOcspCrl.pem";
        String caCertFileName = certsSrc + "rootCert.pem";

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, PASSWORD);

        SignerProperties signerProperties = createSignerProperties();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outputStream);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        
        AdvancedTestOcspClient ocspClient = new AdvancedTestOcspClient();
        ocspClient.addBuilderForCertIssuer((X509Certificate) tsaChain[0], caCert, caPrivateKey);
        ocspClient.addBuilderForCertIssuer((X509Certificate) tsaChain[1], caCert, caPrivateKey);
        padesSigner.setOcspClient(ocspClient);

        AdvancedTestCrlClient crlClient = new AdvancedTestCrlClient();
        crlClient.addBuilderForCertIssuer((X509Certificate) signRsaChain[0], caCert, caPrivateKey);
        padesSigner.setCrlClient(crlClient);
        
        padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, signRsaPrivateKey, testTsa);
        outputStream.close();

        TestSignUtils.basicCheckSignedDoc(new ByteArrayInputStream(outputStream.toByteArray()), "Signature1");
        
        Map<String, Integer> expectedNumberOfCrls = new HashMap<>();
        Map<String, Integer> expectedNumberOfOcsps = new HashMap<>();
        List<String> expectedCerts = Arrays.asList(getCertName(caCert), getCertName((X509Certificate) signRsaChain[0]),
                getCertName((X509Certificate) tsaChain[0]), getCertName((X509Certificate) tsaChain[1]));
        // It is expected to have two OCSP responses, one for timestamp cert and another timestamp root cert.
        expectedNumberOfOcsps.put(caCert.getSubjectX500Principal().getName(), 2);
        expectedNumberOfCrls.put(caCert.getSubjectX500Principal().getName(), 1);
        TestSignUtils.assertDssDict(new ByteArrayInputStream(outputStream.toByteArray()),
                expectedNumberOfCrls, expectedNumberOfOcsps, expectedCerts);
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

    private PdfPadesSigner createPdfPadesSigner(String srcFileName, OutputStream outputStream) throws IOException {
        return new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)), outputStream);
    }
}
