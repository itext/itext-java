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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.LtvVerification.CertificateInclusion;
import com.itextpdf.signatures.LtvVerification.CertificateOption;
import com.itextpdf.signatures.LtvVerification.Level;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleUnitTest.class)
public class LtvVerificationTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/LtvVerificationTest/";
    private static final String SRC_PDF = SOURCE_FOLDER + "pdfWithDssDictionary.pdf";
    private static final String SIG_FIELD_NAME = "Signature1";
    private static final String CRL_DISTRIBUTION_POINT = "http://example.com";
    private static final String CERT_FOLDER_PATH = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    private static LtvVerification TEST_VERIFICATION;

    @BeforeClass
    public static void before() throws IOException {
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getProvider());
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC_PDF));
        TEST_VERIFICATION = new LtvVerification(pdfDoc);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Adding verification for TestSignature", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Certificate: C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "CRL added", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Certificate: C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRoot", logLevel = LogLevelConstants.INFO)
    })
    public void addVerificationToDocumentWithAlreadyExistedDss()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String input = SOURCE_FOLDER + "signingCertHasChainWithOcspOnlyForChildCert.pdf";
        String signatureHash = "C5CC1458AAA9B8BAB0677F9EA409983B577178A3";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(input))) {
            PdfDictionary dss = pdfDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
            Assert.assertNull(dss.get(PdfName.CRLs));
            PdfArray ocsps = dss.getAsArray(PdfName.OCSPs);
            Assert.assertEquals(1, ocsps.size());
            PdfIndirectReference pir = ocsps.get(0).getIndirectReference();

            PdfDictionary vri = dss.getAsDictionary(PdfName.VRI);
            Assert.assertEquals(1, vri.entrySet().size());
            PdfDictionary vriElem = vri.getAsDictionary(new PdfName(signatureHash));
            Assert.assertEquals(1, vriElem.entrySet().size());
            final PdfArray vriOcsp = vriElem.getAsArray(PdfName.OCSP);
            Assert.assertEquals(1, vriOcsp.size());
            Assert.assertEquals(pir, vriOcsp.get(0).getIndirectReference());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(input), new PdfWriter(baos), new StampingProperties().useAppendMode())) {
            LtvVerification verification = new LtvVerification(pdfDocument);

            String rootCertPath = CERT_FOLDER_PATH + "rootRsa.pem";
            X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertPath)[0];
            PrivateKey caPrivateKey = PemFileHelper.readFirstKey(rootCertPath, PASSWORD);

            verification.addVerification("TestSignature", null, new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey),
                    CertificateOption.SIGNING_CERTIFICATE, Level.CRL, CertificateInclusion.NO);

            verification.merge();
        }

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())))) {
            PdfDictionary dss = pdfDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
            Assert.assertNull(dss.get(PdfName.OCSPs));
            PdfArray crls = dss.getAsArray(PdfName.CRLs);
            Assert.assertEquals(1, crls.size());
            PdfIndirectReference pir = crls.get(0).getIndirectReference();

            PdfDictionary vri = dss.getAsDictionary(PdfName.VRI);
            Assert.assertEquals(1, vri.entrySet().size());
            PdfDictionary vriElem = vri.getAsDictionary(new PdfName(signatureHash));
            Assert.assertEquals(1, vriElem.entrySet().size());
            final PdfArray vriCrl = vriElem.getAsArray(PdfName.CRL);
            Assert.assertEquals(1, vriCrl.size());
            Assert.assertEquals(pir, vriCrl.get(0).getIndirectReference());
        }
    }

    @Test
    public void validateSigNameWithEmptyByteArrayCrlOcspCertTest() throws IOException, GeneralSecurityException {
        List<byte[]> crls = new ArrayList<>();
        crls.add(new byte[0]);
        List<byte[]> ocsps = new ArrayList<>();
        ocsps.add(new byte[0]);
        List<byte[]> certs = new ArrayList<>();
        certs.add(new byte[0]);

        Assert.assertTrue(TEST_VERIFICATION.addVerification(SIG_FIELD_NAME, ocsps, crls, certs));
    }
    
    @Test
    public void tryAddVerificationAfterMerge() throws IOException, GeneralSecurityException {
        List<byte[]> crls = new ArrayList<>();
        crls.add(new byte[0]);
        List<byte[]> ocsps = new ArrayList<>();
        ocsps.add(new byte[0]);
        List<byte[]> certs = new ArrayList<>();
        certs.add(new byte[0]);

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC_PDF), new PdfWriter(new ByteArrayOutputStream()))) {
            LtvVerification verificationWithWriter = new LtvVerification(pdfDoc);

            verificationWithWriter.merge();
            verificationWithWriter.addVerification(SIG_FIELD_NAME, ocsps, crls, certs);
            
            verificationWithWriter.merge();
            Exception exception1 = Assert.assertThrows(IllegalStateException.class,
                    () -> verificationWithWriter.addVerification(SIG_FIELD_NAME, ocsps, crls, certs));
            Assert.assertEquals(SignExceptionMessageConstant.VERIFICATION_ALREADY_OUTPUT, exception1.getMessage());

            verificationWithWriter.merge();
            Exception exception2 = Assert.assertThrows(IllegalStateException.class,
                    () -> verificationWithWriter.addVerification(null, null, null,
                            CertificateOption.SIGNING_CERTIFICATE, Level.CRL, CertificateInclusion.YES));
            Assert.assertEquals(SignExceptionMessageConstant.VERIFICATION_ALREADY_OUTPUT, exception2.getMessage());
        }
    }

    @Test
    public void validateSigNameWithNullCrlOcspCertTest() throws GeneralSecurityException, IOException {
        Assert.assertTrue(TEST_VERIFICATION.addVerification(SIG_FIELD_NAME, null, null, null));
    }

    @Test
    //TODO DEVSIX-5696 Sign: NPE is thrown because no such a signature
    public void exceptionWhenValidateNonExistentSigNameTest() {
        Assert.assertThrows(NullPointerException.class,
                () -> TEST_VERIFICATION.addVerification("nonExistentSigName", null, null, null));
    }

    @Test
    //TODO DEVSIX-5696 Sign: NPE is thrown because no such a signature
    public void exceptionWhenValidateParticularNonExistentSigNameTest() {
        Assert.assertThrows(NullPointerException.class,
                () -> TEST_VERIFICATION.addVerification("nonExistentSigName", null, null,
                        CertificateOption.SIGNING_CERTIFICATE, Level.OCSP_CRL, CertificateInclusion.YES));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url: Passed url can not be null", logLevel =
                    LogLevelConstants.INFO)
    })
    public void validateSigNameWithoutCrlAndOcspSigningOcspCrlYesTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.SIGNING_CERTIFICATE, Level.OCSP_CRL,
                CertificateInclusion.YES, false);
    }

    @Test
    public void validateSigNameWithoutCrlAndOcspSigningOcspYesTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.SIGNING_CERTIFICATE, Level.OCSP, CertificateInclusion.YES,
                false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRsaCert01", logLevel =
                    LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url: Passed url can not be null.", logLevel =
                    LogLevelConstants.INFO)
    })
    public void validateSigNameWithoutCrlAndOcspSigningCrlYesTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.SIGNING_CERTIFICATE, Level.CRL, CertificateInclusion.YES,
                false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url: Passed url can not be null.", logLevel =
                    LogLevelConstants.INFO)
    })
    public void validateSigNameWithoutCrlAndOcspSigningOcspOptCrlYesTest()
            throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.SIGNING_CERTIFICATE, Level.OCSP_OPTIONAL_CRL,
                CertificateInclusion.YES, false);
    }

    @Test
    public void validateSigNameWithoutCrlAndOcspWholeChainOcspYesTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.WHOLE_CHAIN, Level.OCSP, CertificateInclusion.YES, false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url: Passed url can not be null.", logLevel =
                    LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRoot", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameWithoutCrlAndOcspWholeChainCrlYesTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.WHOLE_CHAIN, Level.CRL, CertificateInclusion.YES, false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url: Passed url can not be null.", logLevel =
                    LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRoot", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameWithoutCrlAndOcspWholeChainOptCrlYesTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.WHOLE_CHAIN, Level.OCSP_OPTIONAL_CRL,
                CertificateInclusion.YES, false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url: Passed url can not be null.", logLevel =
                    LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRoot", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameWithoutCrlAndOcspWholeChainOcspCrlYesTest()
            throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.WHOLE_CHAIN, Level.OCSP_CRL,
                CertificateInclusion.YES, false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url: Passed url can not be null", logLevel =
                    LogLevelConstants.INFO)
    })
    public void validateSigNameWithoutCrlAndOcspSigningOcspCrlNoTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.SIGNING_CERTIFICATE, Level.OCSP_CRL,
                CertificateInclusion.NO, false);
    }

    @Test
    public void validateSigNameWithoutCrlAndOcspSigningOcspNoTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.SIGNING_CERTIFICATE, Level.OCSP, CertificateInclusion.NO,
                false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRsaCert01",
                    logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url: Passed url can not be null.", logLevel =
                    LogLevelConstants.INFO)
    })
    public void validateSigNameWithoutCrlAndOcspSigningCrlNoTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.SIGNING_CERTIFICATE, Level.CRL, CertificateInclusion.NO,
                false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url: Passed url can not be null.", logLevel =
                    LogLevelConstants.INFO)
    })
    public void validateSigNameWithoutCrlAndOcspSigningOcspOptCrlNoTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.SIGNING_CERTIFICATE, Level.OCSP_OPTIONAL_CRL,
                CertificateInclusion.NO, false);
    }

    @Test
    public void validateSigNameWithoutCrlAndOcspWholeChainOcspNoTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.WHOLE_CHAIN, Level.OCSP, CertificateInclusion.NO, false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url: Passed url can not be null.", logLevel =
                    LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRoot", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameWithoutCrlAndOcspWholeChainCrlNoTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.WHOLE_CHAIN, Level.CRL, CertificateInclusion.NO, false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url: Passed url can not be null.", logLevel =
                    LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRoot", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameWithoutCrlAndOcspWholeChainOptCrlNoTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.WHOLE_CHAIN, Level.OCSP_OPTIONAL_CRL,
                CertificateInclusion.NO, false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url: Passed url can not be null.", logLevel =
                    LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,L=Minsk,O=iText,OU=test,"
                    + "CN=iTextTestRoot", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameWithoutCrlAndOcspWholeChainOcspCrlNoTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(null, CertificateOption.WHOLE_CHAIN, Level.OCSP_CRL,
                CertificateInclusion.NO, false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Added CRL found at: http://example.com", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameSigningOcspCrlYesTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.SIGNING_CERTIFICATE, Level.OCSP_CRL,
                CertificateInclusion.YES, true);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameSigningOcspYesTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.SIGNING_CERTIFICATE, Level.OCSP,
                CertificateInclusion.YES, false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Added CRL found at: http://example.com", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameSigningCrlYesTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.SIGNING_CERTIFICATE, Level.CRL,
                CertificateInclusion.YES, true);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Added CRL found at: http://example.com", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameSigningOcspOptionalCrlYesTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.SIGNING_CERTIFICATE,
                Level.OCSP_OPTIONAL_CRL, CertificateInclusion.YES, true);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Added CRL found at: http://example.com", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameSigningOcspCrlNoTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.SIGNING_CERTIFICATE, Level.OCSP_CRL,
                CertificateInclusion.NO, true);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameSigningOcspNoTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.SIGNING_CERTIFICATE, Level.OCSP,
                CertificateInclusion.NO, false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Added CRL found at: http://example.com", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameSigningCrlNoTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.SIGNING_CERTIFICATE, Level.CRL,
                CertificateInclusion.NO, true);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Added CRL found at: http://example.com", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameSigningOcspOptionalCrlNoTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.SIGNING_CERTIFICATE,
                Level.OCSP_OPTIONAL_CRL, CertificateInclusion.NO, true);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://example.com", logLevel = LogLevelConstants.INFO,
                    count = 2),
            @LogMessage(messageTemplate = "Added CRL found at: http://example.com", logLevel = LogLevelConstants.INFO
                    , count = 2)
    })
    public void validateSigNameWholeChainOcspCrlYesTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.WHOLE_CHAIN, Level.OCSP_CRL,
                CertificateInclusion.YES, true);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://example.com", logLevel = LogLevelConstants.INFO,
                    count = 2),
            @LogMessage(messageTemplate = "Added CRL found at: http://example.com", logLevel = LogLevelConstants.INFO
                    , count = 2)
    })
    public void validateSigNameWholeChainOcspOptionalCrlYesTest() throws IOException, GeneralSecurityException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.WHOLE_CHAIN, Level.OCSP_OPTIONAL_CRL,
                CertificateInclusion.YES, true);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameWholeChainOcspYesTest() throws IOException, GeneralSecurityException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.WHOLE_CHAIN, Level.OCSP,
                CertificateInclusion.YES, false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://example.com", logLevel = LogLevelConstants.INFO,
                    count = 2),
            @LogMessage(messageTemplate = "Added CRL found at: http://example.com", logLevel = LogLevelConstants.INFO
                    , count = 2)
    })
    public void validateSigNameWholeChainCrlYesTest() throws IOException, GeneralSecurityException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.WHOLE_CHAIN, Level.CRL,
                CertificateInclusion.YES, true);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://example.com", logLevel = LogLevelConstants.INFO,
                    count = 2),
            @LogMessage(messageTemplate = "Added CRL found at: http://example.com", logLevel = LogLevelConstants.INFO
                    , count = 2)
    })
    public void validateSigNameWholeChainOcspCrlNoTest() throws GeneralSecurityException, IOException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.WHOLE_CHAIN, Level.OCSP_CRL,
                CertificateInclusion.NO, true);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://example.com", logLevel = LogLevelConstants.INFO,
                    count = 2),
            @LogMessage(messageTemplate = "Added CRL found at: http://example.com", logLevel = LogLevelConstants.INFO
                    , count = 2)
    })
    public void validateSigNameWholeChainOcspOptionalCrlNoTest() throws IOException, GeneralSecurityException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.WHOLE_CHAIN, Level.OCSP_OPTIONAL_CRL,
                CertificateInclusion.NO, true);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO)
    })
    public void validateSigNameWholeChainOcspNoTest() throws IOException, GeneralSecurityException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.WHOLE_CHAIN, Level.OCSP,
                CertificateInclusion.NO, false);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://example.com", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://example.com", logLevel = LogLevelConstants.INFO,
                    count = 2),
            @LogMessage(messageTemplate = "Added CRL found at: http://example.com", logLevel = LogLevelConstants.INFO
                    , count = 2)
    })
    public void validateSigNameWholeChainCrlNoTest() throws IOException, GeneralSecurityException {
        validateOptionLevelInclusion(CRL_DISTRIBUTION_POINT, CertificateOption.WHOLE_CHAIN, Level.CRL,
                CertificateInclusion.NO, true);
    }
    
    @Test
    public void getParentWithoutCertsTest() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            LtvVerification verification = new LtvVerification(document);
            Assert.assertNull(verification.getParent(null, new Certificate[0]));
        }
    }

    private static void validateOptionLevelInclusion(String crlUrl, CertificateOption certificateOption, Level level,
            CertificateInclusion inclusion, boolean expectedResult) throws IOException, GeneralSecurityException {

        IOcspClient ocsp = new OcspClientBouncyCastle();
        ICrlClient crl = null;
        if (null == crlUrl) {
            crl = new CrlClientOnline();
        } else {
            crl = new CrlClientOnline(crlUrl);
        }
        Assert.assertEquals(expectedResult,
                TEST_VERIFICATION.addVerification(SIG_FIELD_NAME, ocsp, crl, certificateOption, level, inclusion));
    }
}
