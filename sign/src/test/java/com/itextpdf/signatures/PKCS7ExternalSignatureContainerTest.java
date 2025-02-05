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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class PKCS7ExternalSignatureContainerTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final boolean FIPS_MODE = "BCFIPS".equals(FACTORY.getProviderName());

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/PKCS7ExternalSignatureContainerTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/PKCS7ExternalSignatureContainerTest/";
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    private final static String POLICY_IDENTIFIER = "2.16.724.1.3.1.1.2.1.9";
    private final static String POLICY_HASH_BASE64 = "G7roucf600+f03r/o0bAOQ6WAs0=";
    private final static byte[] POLICY_HASH = Base64.decode(POLICY_HASH_BASE64);
    private final static String POLICY_DIGEST_ALGORITHM = "SHA-256";
    private final static String POLICY_URI = "https://sede.060.gob.es/politica_de_firma_anexo_1.pdf";

    private Certificate[] chain;
    private PrivateKey pk;

    private X509Certificate caCert;
    private PrivateKey caPrivateKey;

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void init()
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {
        pk = PemFileHelper.readFirstKey(CERTS_SRC + "signCertRsa01.pem", PASSWORD);
        chain = PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsa01.pem");

        String caCertP12FileName = CERTS_SRC + "rootRsa.pem";
        caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertP12FileName)[0];
        caPrivateKey = PemFileHelper.readFirstKey(caCertP12FileName, PASSWORD);
    }

    @Test
    public void testTroughPdfSigner() throws IOException, GeneralSecurityException {
        String outFileName = DESTINATION_FOLDER + "testTroughPdfSigner.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_testTroughPdfSigner.pdf";
        PdfSigner pdfSigner = new PdfSigner(new PdfReader(createSimpleDocument()),
                FileUtil.getFileOutputStream(outFileName), new StampingProperties());
        PKCS7ExternalSignatureContainer pkcs7ExternalSignatureContainer = new PKCS7ExternalSignatureContainer(
                pk, chain, DigestAlgorithms.SHA256);
        pdfSigner.signExternalContainer(pkcs7ExternalSignatureContainer, 12000);

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @Test
    public void testTroughPdfSignerWithCrlClient() throws IOException, GeneralSecurityException {
        String outFileName = DESTINATION_FOLDER + "testTroughPdfSignerWithCrlClient.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_testTroughPdfSignerWithCrlClient.pdf";
        PdfSigner pdfSigner = new PdfSigner(new PdfReader(createSimpleDocument()),
                FileUtil.getFileOutputStream(outFileName), new StampingProperties());
        PKCS7ExternalSignatureContainer pkcs7ExternalSignatureContainer = new PKCS7ExternalSignatureContainer(
                pk, chain, DigestAlgorithms.SHA256);

        TestCrlClient crlClient = new TestCrlClient();

        TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, caPrivateKey, DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -1));
        crlClient.addBuilderForCertIssuer(crlBuilder);
        pkcs7ExternalSignatureContainer.setCrlClient(crlClient);

        pdfSigner.signExternalContainer(pkcs7ExternalSignatureContainer, 12000);

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @Test
    public void testTroughPdfSignerWithOcspClient() throws IOException, GeneralSecurityException {
        String outFileName = DESTINATION_FOLDER + "testTroughPdfSignerWithOcspClient.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_testTroughPdfSignerWithOcspClient.pdf";
        PdfSigner pdfSigner = new PdfSigner(new PdfReader(createSimpleDocument()),
                FileUtil.getFileOutputStream(outFileName), new StampingProperties());
        PKCS7ExternalSignatureContainer pkcs7ExternalSignatureContainer = new PKCS7ExternalSignatureContainer(
                pk, chain, DigestAlgorithms.SHA256);

        TestOcspClient ocspClient = new TestOcspClient();

        ocspClient.addBuilderForCertIssuer(caCert, caPrivateKey);
        pkcs7ExternalSignatureContainer.setOcspClient(ocspClient);
        pdfSigner.signExternalContainer(pkcs7ExternalSignatureContainer, 12000);

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @Test
    public void testTroughPdfSignerWithTsaClient() throws IOException, GeneralSecurityException, AbstractOperatorCreationException, AbstractPKCSException {
        String outFileName = DESTINATION_FOLDER + "testTroughPdfSignerWithTsaClient.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_testTroughPdfSignerWithTsaClient.pdf";
        if (FIPS_MODE) {
            cmpFileName = cmpFileName.replace(".pdf", "_FIPS.pdf");
        }
        PdfSigner pdfSigner = new PdfSigner(new PdfReader(createSimpleDocument()),
                FileUtil.getFileOutputStream(outFileName), new StampingProperties());
        PKCS7ExternalSignatureContainer pkcs7ExternalSignatureContainer = new PKCS7ExternalSignatureContainer(
                pk, chain, DigestAlgorithms.SHA256);
        String tsaCertP12FileName = CERTS_SRC + "tsCertRsa.pem";

        pkcs7ExternalSignatureContainer.setTsaClient(prepareTsaClient(tsaCertP12FileName));

        pdfSigner.signExternalContainer(pkcs7ExternalSignatureContainer, 12000);

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @Test
    public void testTroughPdfSignerWithCadesType() throws IOException, GeneralSecurityException {
        String outFileName = DESTINATION_FOLDER + "testTroughPdfSignerWithCadesType.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_testTroughPdfSignerWithCadesType.pdf";
        PdfSigner pdfSigner = new PdfSigner(new PdfReader(createSimpleDocument()),
                FileUtil.getFileOutputStream(outFileName), new StampingProperties());
        PKCS7ExternalSignatureContainer pkcs7ExternalSignatureContainer = new PKCS7ExternalSignatureContainer(
                pk, chain, DigestAlgorithms.SHA256);
        pkcs7ExternalSignatureContainer.setSignatureType(PdfSigner.CryptoStandard.CADES);
        pdfSigner.signExternalContainer(pkcs7ExternalSignatureContainer, 12000);

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @Test
    public void testTroughPdfSignerWithSignaturePolicy() throws IOException, GeneralSecurityException {
        String outFileName = DESTINATION_FOLDER + "testTroughPdfSignerWithSignaturePolicy.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_testTroughPdfSignerWithSignaturePolicy.pdf";
        PdfSigner pdfSigner = new PdfSigner(new PdfReader(createSimpleDocument()),
                FileUtil.getFileOutputStream(outFileName), new StampingProperties());
        PKCS7ExternalSignatureContainer pkcs7ExternalSignatureContainer = new PKCS7ExternalSignatureContainer(
                pk, chain, DigestAlgorithms.SHA256);
        SignaturePolicyInfo policy = new SignaturePolicyInfo(POLICY_IDENTIFIER, POLICY_HASH, POLICY_DIGEST_ALGORITHM, POLICY_URI);

        pkcs7ExternalSignatureContainer.setSignaturePolicy(policy);
        pdfSigner.signExternalContainer(pkcs7ExternalSignatureContainer, 12000);

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    private static ByteArrayInputStream createSimpleDocument() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0);
        PdfDocument document = new PdfDocument(new PdfWriter(outputStream, writerProperties));
        document.addNewPage();
        document.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private static TestTsaClient prepareTsaClient(String tsaCertP12FileName)
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertP12FileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertP12FileName, PASSWORD);
        return new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
    }
}
