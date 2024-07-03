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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.IIssuingCertificateRetriever;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.PdfPadesSigner;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.TestSignUtils;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.*;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Tag("BouncyCastleIntegrationTest")
public class PdfPadesWithCrlCertificateTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesWithCrlCertificateTest/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesWithCrlCertificateTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PdfPadesWithCrlCertificateTest/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void signCertWithCrlTest() throws GeneralSecurityException, IOException, AbstractOperatorCreationException,
            AbstractPKCSException, AbstractOCSPException {
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String rootCertFileName = certsSrc + "root.pem";
        String signCertFileName = certsSrc + "sign.pem";
        String rootCrlFileName = certsSrc + "crlRoot.crt";
        String crlCertFileName = certsSrc + "crlCert.pem";
        String tsaCertFileName = certsSrc + "tsCert.pem";

        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(signCertFileName)[0];
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
        X509Certificate crlCert = (X509Certificate) PemFileHelper.readFirstChain(crlCertFileName)[0];
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);

        SignerProperties signerProperties = createSignerProperties();
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);

        CrlClientOnline testCrlClient = new CrlClientOnline() {
            @Override
            protected InputStream getCrlResponse(X509Certificate cert, URL urlt) throws IOException {
                if (urlt.toString().contains("cert-crl")) {
                    return FileUtil.getInputStreamForFile(certsSrc + "crlSignedByCrlCert.crl");
                }
                return FileUtil.getInputStreamForFile(certsSrc + "crlSignedByCA.crl");
            }
        };

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outputStream);
        padesSigner.setCrlClient(testCrlClient);
        IIssuingCertificateRetriever issuingCertificateRetriever = new IssuingCertificateRetriever() {
            @Override
            protected InputStream getIssuerCertByURI(String uri) throws IOException {
                if (uri.contains("crl_cert")) {
                    return FileUtil.getInputStreamForFile(crlCertFileName);
                }
                return FileUtil.getInputStreamForFile(rootCrlFileName);
            }
        };
        padesSigner.setIssuingCertificateRetriever(issuingCertificateRetriever);


        Certificate[] signChain = new Certificate[]{signCert, rootCert};
        padesSigner.signWithBaselineLTProfile(signerProperties, signChain, signRsaPrivateKey, testTsa);
        outputStream.close();

        TestSignUtils.basicCheckSignedDoc(new ByteArrayInputStream(outputStream.toByteArray()), "Signature1");

        Map<String, Integer> expectedNumberOfCrls = new HashMap<>();
        Map<String, Integer> expectedNumberOfOcsps = new HashMap<>();
        // It is expected to have two CRL responses, one for signing cert and another for CRL response.
        expectedNumberOfCrls.put(rootCert.getSubjectX500Principal().getName(), 1);
        expectedNumberOfCrls.put(crlCert.getSubjectX500Principal().getName(), 1);
        TestSignUtils.assertDssDict(new ByteArrayInputStream(outputStream.toByteArray()),
                expectedNumberOfCrls, expectedNumberOfOcsps);
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
