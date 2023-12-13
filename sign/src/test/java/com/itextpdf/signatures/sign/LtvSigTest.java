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

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.signutils.Pkcs12FileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;

@Category(IntegrationTest.class)
public class LtvSigTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/LtvSigTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/LtvSigTest/";

    private static final char[] password = "testpass".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void ltvEnabledTest01() throws IOException, GeneralSecurityException {
        String tsaCertFileName = certsSrc + "tsCertRsa.p12";
        String caCertFileName = certsSrc + "rootRsa.p12";
        String srcFileName = sourceFolder + "signedDoc.pdf";
        String ltvFileName = destinationFolder + "ltvEnabledTest01.pdf";
        String ltvTsFileName = destinationFolder + "ltvEnabledTsTest01.pdf";

        Certificate[] tsaChain = Pkcs12FileHelper.readFirstChain(tsaCertFileName, password);
        PrivateKey tsaPrivateKey = Pkcs12FileHelper.readFirstKey(tsaCertFileName, password, password);
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(caCertFileName, password)[0];
        PrivateKey caPrivateKey = Pkcs12FileHelper.readFirstKey(caCertFileName, password, password);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        TestOcspClient testOcspClient = new TestOcspClient()
                .addBuilderForCertIssuer(caCert, caPrivateKey);
        TestCrlClient testCrlClient = new TestCrlClient(caCert, caPrivateKey);

        PdfDocument document = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(ltvFileName), new StampingProperties().useAppendMode());
        LtvVerification ltvVerification = new LtvVerification(document);
        ltvVerification.addVerification("Signature1", testOcspClient, testCrlClient, LtvVerification.CertificateOption.SIGNING_CERTIFICATE, LtvVerification.Level.OCSP_CRL, LtvVerification.CertificateInclusion.YES);
        ltvVerification.merge();
        document.close();

        PdfSigner signer = new PdfSigner(new PdfReader(ltvFileName), new FileOutputStream(ltvTsFileName), new StampingProperties().useAppendMode());
        signer.timestamp(testTsa, "timestampSig1");

        basicCheckLtvDoc("ltvEnabledTsTest01.pdf", "timestampSig1");
    }

    @Test
    public void ltvEnabledSingleSignatureTest01() throws IOException, GeneralSecurityException {
        String signCertFileName = certsSrc + "signCertRsaWithChain.p12";
        String tsaCertFileName = certsSrc + "tsCertRsa.p12";
        String intermediateCertFileName = certsSrc + "intermediateRsa.p12";
        String caCertFileName = certsSrc + "rootRsa.p12";
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String ltvFileName = destinationFolder + "ltvEnabledSingleSignatureTest01.pdf";

        Certificate[] tsaChain = Pkcs12FileHelper.readFirstChain(tsaCertFileName, password);
        PrivateKey tsaPrivateKey = Pkcs12FileHelper.readFirstKey(tsaCertFileName, password, password);

        X509Certificate intermediateCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(intermediateCertFileName, password)[0];
        PrivateKey intermediatePrivateKey = Pkcs12FileHelper.readFirstKey(intermediateCertFileName, password, password);
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(caCertFileName, password)[0];
        PrivateKey caPrivateKey = Pkcs12FileHelper.readFirstKey(caCertFileName, password, password);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        TestOcspClient testOcspClient = new TestOcspClient()
                .addBuilderForCertIssuer(intermediateCert, intermediatePrivateKey)
                .addBuilderForCertIssuer(caCert, caPrivateKey);

        Certificate[] signChain = Pkcs12FileHelper.readFirstChain(signCertFileName, password);
        PrivateKey signPrivateKey = Pkcs12FileHelper.readFirstKey(signCertFileName, password, password);
        IExternalSignature pks = new PrivateKeySignature(signPrivateKey, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);

        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), new FileOutputStream(ltvFileName), new StampingProperties());
        signer.setFieldName("Signature1");
        signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, testOcspClient, testTsa, 0, PdfSigner.CryptoStandard.CADES);

        PadesSigTest.basicCheckSignedDoc(destinationFolder + "ltvEnabledSingleSignatureTest01.pdf", "Signature1");
    }

    @Test
    public void secondLtvOriginalHasNoVri01() throws IOException, GeneralSecurityException {
        String tsaCertFileName = certsSrc + "tsCertRsa.p12";
        String caCertFileName = certsSrc + "rootRsa.p12";
        String srcFileName = sourceFolder + "ltvEnabledNoVriEntry.pdf";
        String ltvFileName = destinationFolder + "secondLtvOriginalHasNoVri01.pdf";
        String ltvTsFileName = destinationFolder + "secondLtvOriginalHasNoVriTs01.pdf";

        Certificate[] tsaChain = Pkcs12FileHelper.readFirstChain(tsaCertFileName, password);
        PrivateKey tsaPrivateKey = Pkcs12FileHelper.readFirstKey(tsaCertFileName, password, password);
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(caCertFileName, password)[0];
        PrivateKey caPrivateKey = Pkcs12FileHelper.readFirstKey(caCertFileName, password, password);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        TestOcspClient testOcspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        TestCrlClient testCrlClient = new TestCrlClient(caCert, caPrivateKey);

        PdfDocument document = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(ltvFileName), new StampingProperties().useAppendMode());
        LtvVerification ltvVerification = new LtvVerification(document);
        ltvVerification.addVerification("timestampSig1", testOcspClient, testCrlClient, LtvVerification.CertificateOption.SIGNING_CERTIFICATE, LtvVerification.Level.OCSP_CRL, LtvVerification.CertificateInclusion.YES);
        ltvVerification.merge();
        document.close();

        PdfSigner signer = new PdfSigner(new PdfReader(ltvFileName), new FileOutputStream(ltvTsFileName), new StampingProperties().useAppendMode());
        signer.timestamp(testTsa, "timestampSig2");

        basicCheckLtvDoc("secondLtvOriginalHasNoVriTs01.pdf", "timestampSig2");
    }

    private void basicCheckLtvDoc(String outFileName, String tsSigName) throws IOException, GeneralSecurityException {
        PdfDocument outDocument = new PdfDocument(new PdfReader(destinationFolder + outFileName));
        PdfDictionary dssDict = outDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        Assert.assertNotNull(dssDict);
        Assert.assertEquals(4, dssDict.size());
        outDocument.close();

        PadesSigTest.basicCheckSignedDoc(destinationFolder + outFileName, tsSigName);
    }
}
