/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.signutils.Pkcs12FileHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class LtvSigTest extends ExtendedITextTest {
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/LtvSigTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/LtvSigTest/";

    private static final char[] PASSWORD = "testpass".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void ltvEnabledTest01() throws IOException, GeneralSecurityException {
        String tsaCertP12FileName = CERTS_SRC + "tsCertRsa.p12";
        String caCertP12FileName = CERTS_SRC + "rootRsa.p12";
        String srcFileName = SOURCE_FOLDER + "signedDoc.pdf";
        String ltvFileName = DESTINATION_FOLDER + "ltvEnabledTest01.pdf";
        String ltvTsFileName = DESTINATION_FOLDER + "ltvEnabledTsTest01.pdf";

        TestCrlClient testCrlClient = prepareCrlClientForIssuer(caCertP12FileName);
        TestOcspClient testOcspClient = prepareOcspClientForIssuer(caCertP12FileName);
        TestTsaClient testTsa = prepareTsaClient(tsaCertP12FileName);

        PdfDocument document = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(ltvFileName),
                new StampingProperties().useAppendMode());
        LtvVerification ltvVerification = new LtvVerification(document);
        ltvVerification.addVerification("Signature1", testOcspClient, testCrlClient,
                LtvVerification.CertificateOption.SIGNING_CERTIFICATE, LtvVerification.Level.OCSP_CRL,
                LtvVerification.CertificateInclusion.YES);
        ltvVerification.merge();
        document.close();

        PdfSigner signer = new PdfSigner(new PdfReader(ltvFileName), new FileOutputStream(ltvTsFileName),
                new StampingProperties().useAppendMode());
        signer.timestamp(testTsa, "timestampSig1");

        basicCheckLtvDoc("ltvEnabledTsTest01.pdf", "timestampSig1");

        Assert.assertNull(
                SignaturesCompareTool.compareSignatures(ltvTsFileName, SOURCE_FOLDER + "cmp_ltvEnabledTsTest01.pdf"));
    }

    @Test
    public void ltvEnabledSingleSignatureNoCrlDataTest() throws IOException, GeneralSecurityException {
        String signCertP12FileName = CERTS_SRC + "signCertRsaWithChain.p12";
        String tsaCertP12FileName = CERTS_SRC + "tsCertRsa.p12";
        String intermediateCertP12FileName = CERTS_SRC + "intermediateRsa.p12";
        String caCertP12FileName = CERTS_SRC + "rootRsa.p12";
        String srcFileName = SOURCE_FOLDER + "helloWorldDoc.pdf";
        String ltvFileName = DESTINATION_FOLDER + "ltvEnabledSingleSignatureNoCrlDataTest.pdf";

        Certificate[] signChain = Pkcs12FileHelper.readFirstChain(signCertP12FileName, PASSWORD);
        IExternalSignature pks = prepareSignatureHandler(signCertP12FileName);
        TestTsaClient testTsa = prepareTsaClient(tsaCertP12FileName);
        TestOcspClient testOcspClient = prepareOcspClientForIssuer(intermediateCertP12FileName, caCertP12FileName);
        Collection<ICrlClient> crlNotAvailableList = Arrays.<ICrlClient>asList((ICrlClient)null, new ICrlClient() {
            @Override
            public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) {
                return null;
            }
        });

        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), new FileOutputStream(ltvFileName),
                new StampingProperties());
        signer.setFieldName("Signature1");

        signer.signDetached(new BouncyCastleDigest(), pks, signChain, crlNotAvailableList, testOcspClient, testTsa, 0,
                PdfSigner.CryptoStandard.CADES);

        Assert.assertNull(SignaturesCompareTool.compareSignatures(
                ltvFileName, SOURCE_FOLDER + "cmp_ltvEnabledSingleSignatureNoCrlDataTest.pdf"));
    }

    @Test
    public void ltvEnabledSingleSignatureNoOcspDataTest() throws IOException, GeneralSecurityException {
        String signCertP12FileName = CERTS_SRC + "signCertRsaWithChain.p12";
        String tsaCertP12FileName = CERTS_SRC + "tsCertRsa.p12";
        String intermediateCertP12FileName = CERTS_SRC + "intermediateRsa.p12";
        String caCertP12FileName = CERTS_SRC + "rootRsa.p12";
        String srcFileName = SOURCE_FOLDER + "helloWorldDoc.pdf";
        String ltvFileName = DESTINATION_FOLDER + "ltvEnabledSingleSignatureNoOcspDataTest.pdf";

        Certificate[] signChain = Pkcs12FileHelper.readFirstChain(signCertP12FileName, PASSWORD);
        IExternalSignature pks = prepareSignatureHandler(signCertP12FileName);
        TestTsaClient testTsa = prepareTsaClient(tsaCertP12FileName);
        TestCrlClient testCrlClient = prepareCrlClientForIssuer(caCertP12FileName, intermediateCertP12FileName);

        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), new FileOutputStream(ltvFileName),
                new StampingProperties());
        signer.setFieldName("Signature1");
        signer.signDetached(new BouncyCastleDigest(), pks, signChain, Collections.<ICrlClient>singletonList(testCrlClient), null,
                testTsa, 0, PdfSigner.CryptoStandard.CADES);

        Assert.assertNull(SignaturesCompareTool.compareSignatures(
                ltvFileName, SOURCE_FOLDER + "cmp_ltvEnabledSingleSignatureNoOcspDataTest.pdf"));
    }

    @Test
    public void secondLtvOriginalHasNoVri01() throws IOException, GeneralSecurityException {
        String tsaCertFileName = CERTS_SRC + "tsCertRsa.p12";
        String caCertFileName = CERTS_SRC + "rootRsa.p12";
        String srcFileName = SOURCE_FOLDER + "ltvEnabledNoVriEntry.pdf";
        String ltvFileName = DESTINATION_FOLDER + "secondLtvOriginalHasNoVri01.pdf";
        String ltvTsFileName = DESTINATION_FOLDER + "secondLtvOriginalHasNoVriTs01.pdf";

        TestCrlClient testCrlClient = prepareCrlClientForIssuer(caCertFileName);
        TestOcspClient testOcspClient = prepareOcspClientForIssuer(caCertFileName);
        TestTsaClient testTsa = prepareTsaClient(tsaCertFileName);

        PdfDocument document = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(ltvFileName),
                new StampingProperties().useAppendMode());
        LtvVerification ltvVerification = new LtvVerification(document);
        ltvVerification.addVerification("timestampSig1", testOcspClient, testCrlClient,
                LtvVerification.CertificateOption.SIGNING_CERTIFICATE, LtvVerification.Level.OCSP_CRL,
                LtvVerification.CertificateInclusion.YES);
        ltvVerification.merge();
        document.close();

        PdfSigner signer = new PdfSigner(new PdfReader(ltvFileName), new FileOutputStream(ltvTsFileName),
                new StampingProperties().useAppendMode());
        signer.timestamp(testTsa, "timestampSig2");

        basicCheckLtvDoc("secondLtvOriginalHasNoVriTs01.pdf", "timestampSig2");

        Assert.assertNull(SignaturesCompareTool.compareSignatures(
                ltvTsFileName, SOURCE_FOLDER + "cmp_secondLtvOriginalHasNoVriTs01.pdf"));
    }

    private static IExternalSignature prepareSignatureHandler(String signCertP12FileName)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        PrivateKey signPrivateKey = Pkcs12FileHelper.readFirstKey(signCertP12FileName, PASSWORD, PASSWORD);
        return new PrivateKeySignature(signPrivateKey, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
    }

    private static TestCrlClient prepareCrlClientForIssuer(String... issuerCertP12FileNames)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        TestCrlClient testCrlClient = new TestCrlClient();
        for (String issuerP12File : issuerCertP12FileNames) {
            X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(issuerP12File, PASSWORD)[0];
            PrivateKey caPrivateKey = Pkcs12FileHelper.readFirstKey(issuerP12File, PASSWORD, PASSWORD);
            testCrlClient.addBuilderForCertIssuer(caCert, caPrivateKey);
        }
        return testCrlClient;
    }

    private static TestOcspClient prepareOcspClientForIssuer(String... issuerCertP12FileNames)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {

        TestOcspClient ocspClient = new TestOcspClient();
        for (String issuerP12File : issuerCertP12FileNames) {
            X509Certificate issuerCertificate =
                    (X509Certificate) Pkcs12FileHelper.readFirstChain(issuerP12File, PASSWORD)[0];
            PrivateKey issuerPrivateKey = Pkcs12FileHelper.readFirstKey(issuerP12File, PASSWORD, PASSWORD);
            ocspClient.addBuilderForCertIssuer(issuerCertificate, issuerPrivateKey);
        }

        return ocspClient;
    }

    private static TestTsaClient prepareTsaClient(String tsaCertP12FileName)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        Certificate[] tsaChain = Pkcs12FileHelper.readFirstChain(tsaCertP12FileName, PASSWORD);
        PrivateKey tsaPrivateKey = Pkcs12FileHelper.readFirstKey(tsaCertP12FileName, PASSWORD, PASSWORD);
        return new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
    }

    private void basicCheckLtvDoc(String outFileName, String tsSigName) throws IOException, GeneralSecurityException {
        PdfDocument outDocument = new PdfDocument(new PdfReader(DESTINATION_FOLDER + outFileName));
        PdfDictionary dssDict = outDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        Assert.assertNotNull(dssDict);
        Assert.assertEquals(4, dssDict.size());
        outDocument.close();

        PadesSigTest.basicCheckSignedDoc(DESTINATION_FOLDER + outFileName, tsSigName);
    }
}
