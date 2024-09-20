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
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.TestSignUtils;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleIntegrationTest")
public class LtvSigTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/LtvSigTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/LtvSigTest/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void ltvEnabledTest01()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String tsaCertP12FileName = CERTS_SRC + "tsCertRsa.pem";
        String caCertP12FileName = CERTS_SRC + "rootRsa.pem";
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

        PdfSigner signer = new PdfSigner(new PdfReader(ltvFileName), FileUtil.getFileOutputStream(ltvTsFileName),
                new StampingProperties().useAppendMode());
        signer.timestamp(testTsa, "timestampSig1");

        basicCheckLtvDoc("ltvEnabledTsTest01.pdf", "timestampSig1");

        Assertions.assertNull(
                SignaturesCompareTool.compareSignatures(ltvTsFileName, SOURCE_FOLDER + "cmp_ltvEnabledTsTest01.pdf"));
    }

    @Test
    public void ltvEnabledSingleSignatureNoCrlDataTest()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String signCertP12FileName = CERTS_SRC + "signCertRsaWithChain.pem";
        String tsaCertP12FileName = CERTS_SRC + "tsCertRsa.pem";
        String intermediateCertP12FileName = CERTS_SRC + "intermediateRsa.pem";
        String caCertP12FileName = CERTS_SRC + "rootRsa.pem";
        String srcFileName = SOURCE_FOLDER + "helloWorldDoc.pdf";
        String ltvFileName = DESTINATION_FOLDER + "ltvEnabledSingleSignatureNoCrlDataTest.pdf";

        Certificate[] signChain = PemFileHelper.readFirstChain(signCertP12FileName);
        IExternalSignature pks = prepareSignatureHandler(signCertP12FileName);
        TestTsaClient testTsa = prepareTsaClient(tsaCertP12FileName);
        TestOcspClient testOcspClient = prepareOcspClientForIssuer(intermediateCertP12FileName, caCertP12FileName);
        Collection<ICrlClient> crlNotAvailableList = Arrays.<ICrlClient>asList((ICrlClient)null, new ICrlClient() {
            @Override
            public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) {
                return null;
            }
        });

        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), FileUtil.getFileOutputStream(ltvFileName),
                new StampingProperties());
        signer.setSignerProperties(new SignerProperties().setFieldName("Signature1"));

        signer.signDetached(new BouncyCastleDigest(), pks, signChain, crlNotAvailableList, testOcspClient, testTsa, 0,
                PdfSigner.CryptoStandard.CADES);

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(
                ltvFileName, SOURCE_FOLDER + "cmp_ltvEnabledSingleSignatureNoCrlDataTest.pdf"));
    }

    @Test
    public void ltvEnabledSingleSignatureNoOcspDataTest()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String signCertP12FileName = CERTS_SRC + "signCertRsaWithChain.pem";
        String tsaCertP12FileName = CERTS_SRC + "tsCertRsa.pem";
        String intermediateCertP12FileName = CERTS_SRC + "intermediateRsa.pem";
        String caCertP12FileName = CERTS_SRC + "rootRsa.pem";
        String srcFileName = SOURCE_FOLDER + "helloWorldDoc.pdf";
        String ltvFileName = DESTINATION_FOLDER + "ltvEnabledSingleSignatureNoOcspDataTest.pdf";

        Certificate[] signChain = PemFileHelper.readFirstChain(signCertP12FileName);
        IExternalSignature pks = prepareSignatureHandler(signCertP12FileName);
        TestTsaClient testTsa = prepareTsaClient(tsaCertP12FileName);
        TestCrlClient testCrlClient = prepareCrlClientForIssuer(caCertP12FileName, intermediateCertP12FileName);

        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), FileUtil.getFileOutputStream(ltvFileName),
                new StampingProperties());
        signer.setSignerProperties(new SignerProperties().setFieldName("Signature1"));
        signer.signDetached(new BouncyCastleDigest(), pks, signChain, Collections.<ICrlClient>singletonList(testCrlClient), null,
                testTsa, 0, PdfSigner.CryptoStandard.CADES);

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(
                ltvFileName, SOURCE_FOLDER + "cmp_ltvEnabledSingleSignatureNoOcspDataTest.pdf"));
    }

    @Test
    public void secondLtvOriginalHasNoVri01()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String tsaCertFileName = CERTS_SRC + "tsCertRsa.pem";
        String caCertFileName = CERTS_SRC + "rootRsa.pem";
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

        PdfSigner signer = new PdfSigner(new PdfReader(ltvFileName), FileUtil.getFileOutputStream(ltvTsFileName),
                new StampingProperties().useAppendMode());
        signer.timestamp(testTsa, "timestampSig2");

        basicCheckLtvDoc("secondLtvOriginalHasNoVriTs01.pdf", "timestampSig2");

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(
                ltvTsFileName, SOURCE_FOLDER + "cmp_secondLtvOriginalHasNoVriTs01.pdf"));
    }

    private static IExternalSignature prepareSignatureHandler(String signCertP12FileName)
            throws IOException, AbstractPKCSException, AbstractOperatorCreationException {
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertP12FileName, PASSWORD);
        return new PrivateKeySignature(signPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
    }

    private static TestCrlClient prepareCrlClientForIssuer(String... issuerCertP12FileNames)
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {
        TestCrlClient testCrlClient = new TestCrlClient();
        for (String issuerP12File : issuerCertP12FileNames) {
            X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(issuerP12File)[0];
            PrivateKey caPrivateKey = PemFileHelper.readFirstKey(issuerP12File, PASSWORD);
            testCrlClient.addBuilderForCertIssuer(caCert, caPrivateKey);
        }
        return testCrlClient;
    }

    private static TestOcspClient prepareOcspClientForIssuer(String... issuerCertP12FileNames)
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {

        TestOcspClient ocspClient = new TestOcspClient();
        for (String issuerP12File : issuerCertP12FileNames) {
            X509Certificate issuerCertificate =
                    (X509Certificate) PemFileHelper.readFirstChain(issuerP12File)[0];
            PrivateKey issuerPrivateKey = PemFileHelper.readFirstKey(issuerP12File, PASSWORD);
            ocspClient.addBuilderForCertIssuer(issuerCertificate, issuerPrivateKey);
        }

        return ocspClient;
    }

    private static TestTsaClient prepareTsaClient(String tsaCertP12FileName)
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertP12FileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertP12FileName, PASSWORD);
        return new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
    }

    private void basicCheckLtvDoc(String outFileName, String tsSigName) throws IOException, GeneralSecurityException {
        PdfDocument outDocument = new PdfDocument(new PdfReader(DESTINATION_FOLDER + outFileName));
        PdfDictionary dssDict = outDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        Assertions.assertNotNull(dssDict);
        Assertions.assertEquals(4, dssDict.size());
        outDocument.close();

        TestSignUtils.basicCheckSignedDoc(DESTINATION_FOLDER + outFileName, tsSigName);
    }
}
