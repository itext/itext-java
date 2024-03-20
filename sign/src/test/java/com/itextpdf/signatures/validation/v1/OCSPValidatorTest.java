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
package com.itextpdf.signatures.validation.v1;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.OID;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.validation.v1.context.CertificateSource;
import com.itextpdf.signatures.validation.v1.context.CertificateSources;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContext;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContexts;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContexts;
import com.itextpdf.signatures.validation.v1.report.CertificateReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;

@Category(BouncyCastleUnitTest.class)
public class OCSPValidatorTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/v1/OCSPValidatorTest/";
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    private static X509Certificate caCert;
    private static PrivateKey caPrivateKey;
    private static X509Certificate checkCert;
    private static X509Certificate responderCert;
    private static PrivateKey ocspRespPrivateKey;
    private IssuingCertificateRetriever certificateRetriever;
    private SignatureValidationProperties parameters;
    private final ValidationContext baseContext = new ValidationContext(ValidatorContext.REVOCATION_DATA_VALIDATOR,
            CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);
    private ValidatorChainBuilder validatorChainBuilder;

    @BeforeClass
    public static void before()
            throws CertificateException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        Security.addProvider(FACTORY.getProvider());

        String rootCertFileName = SOURCE_FOLDER + "rootCert.pem";
        String checkCertFileName = SOURCE_FOLDER + "signCert.pem";
        String ocspResponderCertFileName = SOURCE_FOLDER + "ocspResponderCert.pem";

        caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        caPrivateKey = PemFileHelper.readFirstKey(rootCertFileName, PASSWORD);
        checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];
        responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
        ocspRespPrivateKey = PemFileHelper.readFirstKey(ocspResponderCertFileName, PASSWORD);
    }

    @Before
    public void setUp() {
        certificateRetriever = new IssuingCertificateRetriever();
        parameters = new SignatureValidationProperties();
        validatorChainBuilder = new ValidatorChainBuilder()
                .withSignatureValidationProperties(parameters)
                .withIssuingCertificateRetriever(certificateRetriever);
    }

    @Test
    public void validateResponderOcspNoCheckTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        ValidationReport report = validateTest(checkDate);

        new AssertValidationReport(report)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(2)
                .hasLogItem(l -> l.getCheckName().equals(RevocationDataValidator.REVOCATION_DATA_CHECK)
                                && l.getMessage().equals(RevocationDataValidator.TRUSTED_OCSP_RESPONDER),
                        "Revocation data check with trusted responder")
                .hasLogItem(l -> l.getCheckName().equals(CertificateChainValidator.CERTIFICATE_CHECK)
                                && l.getMessage().equals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                ((CertificateReportItem) l).getCertificate().getSubjectX500Principal())),
                        "ChainValidator certificate trusted")
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .doAssert();
    }

    @Test
    public void validateAuthorizedOCSPResponderWithOcspTest()
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        ValidationReport report = verifyResponderWithOcsp(false);

        new AssertValidationReport(report)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(2)
                .hasLogItems(l -> l.getCheckName().equals(CertificateChainValidator.CERTIFICATE_CHECK)
                                && l.getMessage().equals(MessageFormatUtil.format(
                                        CertificateChainValidator.CERTIFICATE_TRUSTED,
                                ((CertificateReportItem) l).getCertificate().getSubjectX500Principal())),
                        2, "Certificate check with trusted certificate")
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .doAssert();
    }

    @Test
    public void validateAuthorizedOCSPResponderWithOcspRevokedTest()
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        String ocspResponderCertFileName = SOURCE_FOLDER + "ocspResponderCertForOcspTest.pem";
        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
        certificateRetriever.addKnownCertificates(Collections.singleton(responderCert));

        ValidationReport report = verifyResponderWithOcsp(true);

        new AssertValidationReport(report)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals(OCSPValidator.OCSP_CHECK)
                                && l.getMessage().equals(OCSPValidator.CERT_IS_REVOKED)
                                && l.getStatus().equals(ReportItem.ReportItemStatus.INDETERMINATE),
                        "Certificate revoked")
                .doAssert();
    }

    @Test
    public void validateAuthorizedOCSPResponderFromTheTrustedStoreTest() throws GeneralSecurityException, IOException {
        ValidationReport report = validateOcspWithoutCertsTest(true);

        Assert.assertEquals(0, report.getFailures().size());
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void noResponderFoundInCertsTest() throws GeneralSecurityException, IOException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setOcspCertsChain(new IX509CertificateHolder[]{FACTORY.createJcaX509CertificateHolder(caCert)});
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);

        new AssertValidationReport(report)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals(OCSPValidator.OCSP_CHECK)
                                && l.getMessage().equals(OCSPValidator.OCSP_COULD_NOT_BE_VERIFIED),
                        "OCSP responder not found")
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .doAssert();
    }

    @Test
    public void noResponderFoundTest() throws GeneralSecurityException, IOException {
        ValidationReport report = validateOcspWithoutCertsTest(false);

        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getFailures().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(OCSPValidator.OCSP_COULD_NOT_BE_VERIFIED, item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void validationDateAfterNextUpdateTest() throws GeneralSecurityException, IOException {
        // Same next update is set in the test OCSP builder.
        Date nextUpdate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 30);
        Date checkDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 45);
        ValidationReport report = validateTest(checkDate, TimeTestUtil.TEST_DATE_TIME, 50);
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(OCSPValidator.OCSP_IS_NO_LONGER_VALID, checkDate, nextUpdate),
                item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void certificateWasRevokedAfterCheckDateTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date revocationDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 10);

        ValidationReport report = validateRevokedTest(checkDate, revocationDate);
        new AssertValidationReport(report)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(3)
                .hasLogItem(l -> l.getCheckName().equals(OCSPValidator.OCSP_CHECK)
                        && l.getMessage().equals(MessageFormatUtil.format(SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED,
                        revocationDate)), "valid certificate is revoked")
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .doAssert();
    }

    @Test
    public void certificateWasRevokedBeforeCheckDateTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date revocationDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -1);

        ValidationReport report = validateRevokedTest(checkDate, revocationDate);

        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem ocspCheckItem = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, ocspCheckItem.getCheckName());
        Assert.assertEquals(OCSPValidator.CERT_IS_REVOKED, ocspCheckItem.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INVALID, report.getValidationResult());
    }

    @Test
    public void certificateStatusIsUnknownTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setCertificateStatus(FACTORY.createUnknownStatus());
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp, checkDate);
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem ocspCheckItem = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, ocspCheckItem.getCheckName());
        Assert.assertEquals(OCSPValidator.CERT_STATUS_IS_UNKNOWN, ocspCheckItem.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void serialNumbersDoesNotMatchTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 1)));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp caBasicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(caCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.setTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();

        validator.validate(report, baseContext, checkCert, caBasicOCSPResp.getResponses()[0], caBasicOCSPResp,
                checkDate);
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getFailures().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(OCSPValidator.SERIAL_NUMBERS_DO_NOT_MATCH, item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void ocspForSelfSignedCertTest() throws GeneralSecurityException, IOException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp caBasicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(caCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, caCert, caBasicOCSPResp.getResponses()[0], caBasicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);
        Assert.assertEquals(0, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(RevocationDataValidator.SELF_SIGNED_CERTIFICATE, item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void issuersDoesNotMatchTest() throws GeneralSecurityException, IOException {
        String wrongRootCertFileName = SOURCE_FOLDER + "rootCertForOcspTest.pem";

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        validatorChainBuilder.withIssuingCertificateRetriever(
                new TestIssuingCertificateRetriever(wrongRootCertFileName));
        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();

        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);

        new AssertValidationReport(report)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals(OCSPValidator.OCSP_CHECK) &&
                                l.getMessage().equals(OCSPValidator.ISSUERS_DO_NOT_MATCH) &&
                                l.getStatus().equals(ReportItem.ReportItemStatus.INDETERMINATE),
                        OCSPValidator.ISSUERS_DO_NOT_MATCH)
                .doAssert();
    }

    @Test
    public void certificateDoesNotVerifyWithSuppliedKeyTest()
            throws CertificateException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        String ocspResponderCertFileName = SOURCE_FOLDER + "ocspResponderCertForOcspTest.pem";
        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
        PrivateKey ocspRespPrivateKey = PemFileHelper.readFirstKey(ocspResponderCertFileName, PASSWORD);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);

        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);

        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem ocspCheckItem = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, ocspCheckItem.getCheckName());
        Assert.assertEquals(OCSPValidator.INVALID_OCSP, ocspCheckItem.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INVALID, report.getValidationResult());
    }

    @Test
    public void trustedOcspResponderDoesNotHaveOcspSigningExtensionTest() throws GeneralSecurityException, IOException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp caBasicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        // Configure OCSP signing authority for the certificate in question
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, caBasicOCSPResp.getResponses()[0], caBasicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);

        new AssertValidationReport(report)
                .hasNumberOfFailures(0)
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .doAssert();
    }

    @Test
    public void authorizedOcspResponderDoesNotHaveOcspSigningExtensionTest()
            throws GeneralSecurityException, IOException {
        String ocspResponderCertFileName = SOURCE_FOLDER + "ocspResponderCertWithoutOcspSigning.pem";
        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 1)));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);

        new AssertValidationReport(report)
                .hasNumberOfFailures(1)
                .hasLogItem(l -> l.getCheckName().equals(CertificateChainValidator.EXTENSIONS_CHECK)
                        && l.getMessage().equals(MessageFormatUtil.format(CertificateChainValidator.EXTENSION_MISSING,
                                OID.X509Extensions.EXTENDED_KEY_USAGE)), "OCSP_SIGNING extended key usage is missing")
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .doAssert();
    }

    @Test
    public void positiveFreshnessPositiveTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        ValidationReport report = validateTest(checkDate, DateTimeUtil.addDaysToDate(checkDate, -3), 5);
        Assert.assertEquals(0, report.getFailures().size());
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void positiveFreshnessNegativeTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date thisUpdate = DateTimeUtil.addDaysToDate(checkDate, -3);
        ValidationReport report = validateTest(checkDate, thisUpdate, 2);
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(OCSPValidator.FRESHNESS_CHECK,
                thisUpdate, checkDate, Duration.ofDays(2)), item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void negativeFreshnessPositiveTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        ValidationReport report = validateTest(checkDate, DateTimeUtil.addDaysToDate(checkDate, 5), -3);
        Assert.assertEquals(0, report.getFailures().size());
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void negativeFreshnessNegativeTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date thisUpdate = DateTimeUtil.addDaysToDate(checkDate, 2);
        ValidationReport report = validateTest(checkDate, thisUpdate, -3);
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(OCSPValidator.FRESHNESS_CHECK,
                thisUpdate, checkDate, Duration.ofDays(-3)), item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    private ValidationReport validateTest(Date checkDate) throws CertificateException, IOException {
        return validateTest(checkDate, DateTimeUtil.addDaysToDate(checkDate, 1), 0);
    }

    private ValidationReport validateTest(Date checkDate, Date thisUpdate, long freshness)
            throws CertificateException, IOException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setThisUpdate(DateTimeUtil.getCalendar(thisUpdate));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        parameters.setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                Duration.ofDays(freshness));
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp, checkDate);
        return report;
    }

    private ValidationReport validateRevokedTest(Date checkDate, Date revocationDate)
            throws IOException, CertificateException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setCertificateStatus(FACTORY.createRevokedStatus(revocationDate,
                FACTORY.createCRLReason().getKeyCompromise()));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp, checkDate);
        return report;
    }

    private ValidationReport validateOcspWithoutCertsTest(boolean addResponderToTrusted)
            throws IOException, CertificateException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setOcspCertsChain(new IX509CertificateHolder[0]);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));
        if (addResponderToTrusted) {
            certificateRetriever.addTrustedCertificates(Collections.singletonList(responderCert));
        }

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);
        return report;
    }

    private ValidationReport verifyResponderWithOcsp(boolean revokedOcsp)
            throws IOException, CertificateException, AbstractOperatorCreationException, AbstractPKCSException {
        String rootCertFileName = SOURCE_FOLDER + "rootCertForOcspTest.pem";
        String checkCertFileName = SOURCE_FOLDER + "signCertForOcspTest.pem";
        String ocspResponderCertFileName = SOURCE_FOLDER + "ocspResponderCertForOcspTest.pem";
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(rootCertFileName, PASSWORD);
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];
        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
        PrivateKey ocspRespPrivateKey = PemFileHelper.readFirstKey(ocspResponderCertFileName, PASSWORD);

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, -5)));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        TestOcspResponseBuilder builder2 = revokedOcsp ? new TestOcspResponseBuilder(caCert, caPrivateKey,
                FACTORY.createRevokedStatus(
                        DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -5),
                        FACTORY.createCRLReason().getKeyCompromise())) :
                new TestOcspResponseBuilder(caCert, caPrivateKey);
        builder2.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 20)));
        builder2.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 30)));
        TestOcspClient ocspClient2 = new TestOcspClient().addBuilderForCertIssuer(caCert, builder2);

        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                        TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                        Duration.ofDays(5));
        if (revokedOcsp) {
            parameters.setContinueAfterFailure(ValidatorContexts.all(), CertificateSources.all(), false);
        }
        validatorChainBuilder.getRevocationDataValidator().addOcspClient(ocspClient);
        validatorChainBuilder.getRevocationDataValidator().addOcspClient(ocspClient2);
        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp, checkDate);
        return report;
    }

    private static class TestIssuingCertificateRetriever extends IssuingCertificateRetriever {
        Certificate issuerCertificate;

        public TestIssuingCertificateRetriever(String issuerPath) throws CertificateException, IOException {
            super();
            this.issuerCertificate = PemFileHelper.readFirstChain(issuerPath)[0];
        }

        @Override
        public Certificate retrieveIssuerCertificate(Certificate certificate) {
            return issuerCertificate;
        }
    }
}
