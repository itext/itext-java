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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.LtvVerification.CertificateOption;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class LtvVerifierIntegrationTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/LtvVerifierIntegrationTest/";
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    @BeforeAll
    public static void before() {
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getProvider());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "The timestamp covers whole document.", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "The signed document has not been modified.", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking signature TestSignature", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Verifying signature.", logLevel = LogLevelConstants.INFO),
            // Checking of "All certificates are valid on ..." message is impossible because current time is used in message
            @LogMessage(messageTemplate = "C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRoot", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid OCSPs found: 0", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid CRLs found: 0", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Switching to previous revision.", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "No signatures in revision", logLevel =  LogLevelConstants.INFO)
    })
    public void verifySigningCertIsSelfSignedWithoutRevocationDataTest() throws IOException, GeneralSecurityException {
        String src = SOURCE_FOLDER + "signingCertIsSelfSignedWithoutRevocationData.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(src))) {
            LtvVerifier verifier = new LtvVerifier(pdfDocument);
            verifier.setVerifyRootCertificate(false);
            List<VerificationOK> verificationOKList = verifier.verifySignature();
            Assertions.assertTrue(verificationOKList.isEmpty());
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "The timestamp covers whole document.", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "The signed document has not been modified.", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking signature TestSignature", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Verifying signature.", logLevel = LogLevelConstants.INFO),
            // Checking of "All certificates are valid on ..." message is impossible because current time is used in message
            @LogMessage(messageTemplate = "C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid OCSPs found: 0", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid CRLs found: 0", logLevel = LogLevelConstants.INFO),
    })
    public void verifySigningCertHasChainWithoutRevocationDataTest() throws IOException, GeneralSecurityException {
        String src = SOURCE_FOLDER + "signingCertHasChainWithoutRevocationData.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(src))) {
            LtvVerifier verifier = new LtvVerifier(pdfDocument);
            verifier.setVerifyRootCertificate(false);
            Exception ex = Assertions.assertThrows(VerificationException.class, () -> verifier.verifySignature());
            Assertions.assertEquals("Certificate C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRsaCert01 failed: "
                    + "Couldn't verify with CRL or OCSP or trusted anchor", ex.getMessage());
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "The timestamp covers whole document.", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "The signed document has not been modified.", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "Checking document-level timestamp signature TestTimestamp", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Switching to previous revision.", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "Checking signature TestSignature", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Verifying signature.", logLevel = LogLevelConstants.INFO),
            // Checking of "All certificates are valid on ..." message is impossible because current time is used in message
            @LogMessage(messageTemplate = "C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid OCSPs found: 1", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid CRLs found: 0", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRoot", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid OCSPs found: 0", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "No signatures in revision", logLevel = LogLevelConstants.INFO)
    })
    public void verifySigningCertHasChainWithOcspOnlyForChildCertNotVerifyRootTest()
            throws IOException, GeneralSecurityException {

        String src = SOURCE_FOLDER + "signingCertHasChainWithOcspOnlyForChildCert.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(src))) {
            LtvVerifier verifier = new LtvVerifier(pdfDocument);
            verifier.setCertificateOption(CertificateOption.WHOLE_CHAIN);
            verifier.setVerifyRootCertificate(false);
            // iText doesn't allow adding\processing DSS with one revision in document, so document
            // "signingCertHasChainWithOcspOnlyForChildCert.pdf" contains 2 revision. The first is
            // dummy revision (signing cert of first revision has a chain without any revocation data).
            // The second is main revision which verifying we want to test.
            verifier.switchToPreviousRevision();

            List<VerificationOK> verificationOKList = verifier.verifySignature();

            Assertions.assertEquals(2, verificationOKList.size());
            VerificationOK verificationOK = verificationOKList.get(0);
            Assertions.assertEquals("C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRsaCert01",
                    BOUNCY_CASTLE_FACTORY.createX500Name(verificationOK.certificate).toString());
            Assertions.assertEquals("Valid OCSPs Found: 1", verificationOK.message);

            verificationOK = verificationOKList.get(1);
            Assertions.assertEquals("C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRoot",
                    BOUNCY_CASTLE_FACTORY.createX500Name(verificationOK.certificate).toString());
            Assertions.assertEquals("Root certificate passed without checking", verificationOK.message);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "The timestamp covers whole document.", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "The signed document has not been modified.", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "Checking document-level timestamp signature TestTimestamp", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Switching to previous revision.", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking signature TestSignature", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Verifying signature.", logLevel = LogLevelConstants.INFO),
            // Checking of "All certificates are valid on ..." message is impossible because current time is used in message
            @LogMessage(messageTemplate = "C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid OCSPs found: 1", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid CRLs found: 0", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRoot", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid OCSPs found: 0", logLevel = LogLevelConstants.INFO)
    })
    public void verifySigningCertHasChainWithOcspOnlyForChildCertVerifyRootTest()
            throws IOException, GeneralSecurityException {

        String src = SOURCE_FOLDER + "signingCertHasChainWithOcspOnlyForChildCert.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(src))) {
            LtvVerifier verifier = new LtvVerifier(pdfDocument);
            verifier.setCertificateOption(CertificateOption.WHOLE_CHAIN);
            verifier.setVerifyRootCertificate(true);
            // iText doesn't allow adding\processing DSS with one revision in document, so document
            // "signingCertHasChainWithOcspOnlyForChildCert.pdf" contains 2 revision. The first is
            // dummy revision (signing cert of first revision has a chain without any revocation data).
            // The second is main revision which verifying we want to test.
            verifier.switchToPreviousRevision();

            Exception ex = Assertions.assertThrows(VerificationException.class, () -> verifier.verifySignature());
            Assertions.assertEquals("Certificate C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRoot failed: "
                    + "Couldn't verify with CRL or OCSP or trusted anchor", ex.getMessage());
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "The timestamp covers whole document.", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "The signed document has not been modified.", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "Checking document-level timestamp signature TestTimestamp", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Switching to previous revision.", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking signature TestSignature", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Verifying signature.", logLevel = LogLevelConstants.INFO),
            // Checking of "All certificates are valid on ..." message is impossible because current time is used in message
            @LogMessage(messageTemplate = "C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRsaCertWithChain", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid OCSPs found: 1", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid CRLs found: 0", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestIntermediateRsa01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid OCSPs found: 0", logLevel = LogLevelConstants.INFO)
    })
    public void verifySigningCertHas3ChainWithOcspOnlyForChildCertVerifyRootTest()
            throws IOException, GeneralSecurityException {

        String src = SOURCE_FOLDER + "signingCertHas3ChainWithOcspOnlyForChildCert.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(src))) {
            LtvVerifier verifier = new LtvVerifier(pdfDocument);
            verifier.setCertificateOption(CertificateOption.WHOLE_CHAIN);
            verifier.setVerifyRootCertificate(true);
            // iText doesn't allow adding\processing DSS with one revision in document, so document
            // "signingCertHas3ChainWithOcspOnlyForChildCert.pdf" contains 2 revision. The first is
            // dummy revision (signing cert of first revision has a chain without any revocation data).
            // The second is main revision which verifying we want to test.
            verifier.switchToPreviousRevision();

            Exception ex = Assertions.assertThrows(VerificationException.class, () -> verifier.verifySignature());
            Assertions.assertEquals("Certificate C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestIntermediateRsa01 failed: "
                    + "Couldn't verify with CRL or OCSP or trusted anchor", ex.getMessage());
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "The timestamp covers whole document.", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "The signed document has not been modified.", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "Checking document-level timestamp signature TestTimestamp", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Switching to previous revision.", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "Checking signature TestSignature", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Verifying signature.", logLevel = LogLevelConstants.INFO),
            // Checking of "All certificates are valid on ..." message is impossible because current time is used in message
            @LogMessage(messageTemplate = "C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRsaCert01", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid OCSPs found: 1", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid CRLs found: 0", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRoot", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Valid OCSPs found: 0", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "No signatures in revision", logLevel = LogLevelConstants.INFO)
    })
    public void notTrustedRootCertificateInLatestRevisionTest()
            throws IOException, GeneralSecurityException {

        String src = SOURCE_FOLDER + "signingCertHasChainWithOcspOnlyForChildCert.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(src))) {
            LtvVerifier verifier = new LtvVerifier(pdfDocument);
            verifier.setCertificateOption(CertificateOption.WHOLE_CHAIN);
            verifier.setVerifyRootCertificate(true);
            // iText doesn't allow adding\processing DSS with one revision in document, so document
            // "signingCertHasChainWithOcspOnlyForChildCert.pdf" contains 2 revision. The first is
            // dummy revision (signing cert of first revision has a chain without any revocation data).
            // The second is main revision which verifying we want to test.
            verifier.switchToPreviousRevision();
            // TODO after implementing DEVSIX-6233, 1- pass local CRL for child certificate to LtvVerifier
            //  2- don't manually change latestRevision field 3- don't use first signature and DSS in test PDF document
            verifier.latestRevision = true;

            List<VerificationOK> verificationOKList = verifier.verifySignature();

            Assertions.assertEquals(3, verificationOKList.size());
            VerificationOK verificationOK = verificationOKList.get(0);
            Assertions.assertEquals("C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRsaCert01",
                    BOUNCY_CASTLE_FACTORY.createX500Name(verificationOK.certificate).toString());
            Assertions.assertEquals("Valid OCSPs Found: 1", verificationOK.message);

            verificationOK = verificationOKList.get(1);
            Assertions.assertEquals("C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRoot",
                    BOUNCY_CASTLE_FACTORY.createX500Name(verificationOK.certificate).toString());
            Assertions.assertEquals("Root certificate in final revision", verificationOK.message);

            verificationOK = verificationOKList.get(2);
            Assertions.assertEquals("C=BY,L=Minsk,O=iText,OU=test,CN=iTextTestRoot",
                    BOUNCY_CASTLE_FACTORY.createX500Name(verificationOK.certificate).toString());
            Assertions.assertEquals("Root certificate passed without checking", verificationOK.message);
        }
    }

    @Test
    public void switchBetweenSeveralRevisionsTest() throws IOException, GeneralSecurityException {
        String testInput = SOURCE_FOLDER + "severalConsequentSignatures.pdf";

        try(PdfReader pdfReader = new PdfReader(testInput); PdfDocument pdfDoc = new PdfDocument(pdfReader)) {

            LtvVerifier ltvVerifier = new LtvVerifier(pdfDoc);

            Assertions.assertEquals("timestampSig2", ltvVerifier.signatureName);
            ltvVerifier.switchToPreviousRevision();
            Assertions.assertEquals("Signature2", ltvVerifier.signatureName);
            ltvVerifier.switchToPreviousRevision();
            Assertions.assertEquals("timestampSig1", ltvVerifier.signatureName);
            ltvVerifier.switchToPreviousRevision();
            Assertions.assertEquals("Signature1", ltvVerifier.signatureName);
            ltvVerifier.switchToPreviousRevision();
        }
    }
}
