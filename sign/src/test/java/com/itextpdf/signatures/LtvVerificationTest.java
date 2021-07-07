package com.itextpdf.signatures;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.LtvVerification.CertificateInclusion;
import com.itextpdf.signatures.LtvVerification.CertificateOption;
import com.itextpdf.signatures.LtvVerification.Level;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class LtvVerificationTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/LtvVerificationTest/";
    private static final String SRC_PDF = SOURCE_FOLDER + "pdfWithDssDictionary.pdf";
    private static final String SIG_FIELD_NAME = "Signature1";
    private static final String CRL_DISTRIBUTION_POINT = "http://example.com";

    private static LtvVerification TEST_VERIFICATION;

    @BeforeClass
    public static void before() throws IOException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC_PDF));
        TEST_VERIFICATION = new LtvVerification(pdfDoc);
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

    private static void validateOptionLevelInclusion(String crlUrl, CertificateOption certificateOption, Level level,
            CertificateInclusion inclusion, boolean expectedResult) throws IOException, GeneralSecurityException {

        IOcspClient ocsp = new OcspClientBouncyCastle(null);
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
