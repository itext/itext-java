/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
