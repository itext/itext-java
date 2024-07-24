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
package com.itextpdf.signatures.validation;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.TimestampConstants;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.CertificateSources;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.TimeBasedContexts;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.context.ValidatorContexts;
import com.itextpdf.signatures.validation.mocks.MockChainValidator;
import com.itextpdf.signatures.validation.mocks.MockIssuingCertificateRetriever;
import com.itextpdf.signatures.validation.mocks.MockTrustedCertificatesStore;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;

@Tag("BouncyCastleUnitTest")
public class OCSPValidatorTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/OCSPValidatorTest/";
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    private static X509Certificate caCert;
    private static PrivateKey caPrivateKey;
    private static X509Certificate checkCert;
    private static X509Certificate responderCert;
    private static PrivateKey ocspRespPrivateKey;
    private final ValidationContext baseContext = new ValidationContext(ValidatorContext.REVOCATION_DATA_VALIDATOR,
            CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);
    private IssuingCertificateRetriever certificateRetriever;
    private SignatureValidationProperties parameters;
    private ValidatorChainBuilder validatorChainBuilder;
    private MockChainValidator mockCertificateChainValidator;

    @BeforeAll
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

    @BeforeEach
    public void setUp() {
        certificateRetriever = new IssuingCertificateRetriever();
        parameters = new SignatureValidationProperties();
        mockCertificateChainValidator = new MockChainValidator();
        validatorChainBuilder = new ValidatorChainBuilder()
                .withSignatureValidationProperties(parameters)
                .withIssuingCertificateRetrieverFactory(()-> certificateRetriever)
                .withCertificateChainValidatorFactory(()-> mockCertificateChainValidator);
    }

    @Test
    public void happyPathTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        ValidationReport report = validateTest(checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID));
    }

    @Test
    public void ocpsIssuerChainValidationsUsesCorrectParametersTest() throws CertificateException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        validateTest(checkDate);

        Assertions.assertEquals(1, mockCertificateChainValidator.verificationCalls.size());
        Assertions.assertEquals(responderCert, mockCertificateChainValidator.verificationCalls.get(0).certificate);
        Assertions.assertEquals(ValidatorContext.OCSP_VALIDATOR, mockCertificateChainValidator.verificationCalls.get(0).context.getValidatorContext());
        Assertions.assertEquals(CertificateSource.OCSP_ISSUER, mockCertificateChainValidator.verificationCalls.get(0).context.getCertificateSource());
        Assertions.assertEquals(checkDate, mockCertificateChainValidator.verificationCalls.get(0).checkDate);
    }

    @Test
    public void ocspForSelfSignedCertShouldNotValdateFurtherTest() throws GeneralSecurityException, IOException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp caBasicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(caCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, caCert, caBasicOCSPResp.getResponses()[0], caBasicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasNumberOfLogs(1)
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(RevocationDataValidator.SELF_SIGNED_CERTIFICATE)
                        .withCertificate(caCert))
        );
        Assertions.assertEquals(0, mockCertificateChainValidator.verificationCalls.size());
    }

    @Test
    public void validationDateAfterNextUpdateTest() throws GeneralSecurityException, IOException {
        // Same next update is set in the test OCSP builder.
        Date nextUpdate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 30);
        Date checkDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 45);
        ValidationReport report = validateTest(checkDate, TimeTestUtil.TEST_DATE_TIME, 50);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.OCSP_IS_NO_LONGER_VALID, l -> checkDate, l -> nextUpdate)));
    }


    @Test
    public void serialNumbersDoNotMatchTest() throws GeneralSecurityException, IOException {
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
                checkDate, checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfLogs(1)
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.SERIAL_NUMBERS_DO_NOT_MATCH)
                        .withCertificate(checkCert))
        );
        Assertions.assertEquals(0, mockCertificateChainValidator.verificationCalls.size());
    }


    @Test
    public void issuersDoNotMatchTest() throws GeneralSecurityException, IOException {
        String wrongRootCertFileName = SOURCE_FOLDER + "rootCertForOcspTest.pem";

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        TestIssuingCertificateRetriever wrongRootCertificateRetriever = new TestIssuingCertificateRetriever(
                wrongRootCertFileName);
        validatorChainBuilder.withIssuingCertificateRetrieverFactory(()-> wrongRootCertificateRetriever);
        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();

        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.ISSUERS_DO_NOT_MATCH)
                        .withStatus(ReportItem.ReportItemStatus.INDETERMINATE)
                ));
    }

    @Test
    public void positiveFreshnessNegativeTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date thisUpdate = DateTimeUtil.addDaysToDate(checkDate, -3);
        ValidationReport report = validateTest(checkDate, thisUpdate, 2);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.FRESHNESS_CHECK,
                                l -> thisUpdate, l -> checkDate, l -> Duration.ofDays(2))
                )
        );
    }

    @Test
    public void nextUpdateNotSetResultsInValidStatusTest() throws CertificateEncodingException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, -20)));
        builder.setNextUpdate(DateTimeUtil.getCalendar((Date) TimestampConstants.UNDEFINED_TIMESTAMP_DATE));
        builder.setProducedAt(DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -20));
        TestOcspClient client = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(client.getEncoded(checkCert, caCert, ""))));

        certificateRetriever.addKnownCertificates(Collections.singleton(caCert));
        ValidationReport report = new ValidationReport();
        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();

        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp, checkDate, checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID));
    }

    @Test
    public void certificateWasRevokedBeforeCheckDateShouldFailTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date revocationDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -1);

        ValidationReport report = validateRevokedTestMocked(checkDate, revocationDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INVALID)
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.CERT_IS_REVOKED)
                        .withCertificate(checkCert)));
    }

    @Test
    public void certificateWasRevokedAfterCheckDateShouldSucceedTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date revocationDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 10);

        ValidationReport report = validateRevokedTestMocked(checkDate, revocationDate);
        AssertValidationReport.assertThat(report, a -> a
                .hasLogItem(la -> la
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED,
                                l -> revocationDate)
                )
                .hasStatus(ValidationReport.ValidationResult.VALID));
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
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp, checkDate, checkDate);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.CERT_STATUS_IS_UNKNOWN)
                        .withCertificate(checkCert)));

        Assertions.assertEquals(0, mockCertificateChainValidator.verificationCalls.size());
    }

    @Test
    public void ocspIssuerCertificateDoesNotVerifyWithCaPKTest()
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
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfFailures(1)
                .hasStatus(ValidationReport.ValidationResult.INVALID)
                .hasLogItem(al ->
                        al.withCheckName(OCSPValidator.OCSP_CHECK)
                                .withMessage(OCSPValidator.INVALID_OCSP)
                                // This should be the checked certificate, not the ocsp responder
                                //.withCertificate(checkCert)
                                .withCertificate(responderCert)
                )
        );
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
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasLogItem(la -> la
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.OCSP_COULD_NOT_BE_VERIFIED)
                )
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE));
    }

    @Test
    public void chainValidatorReportWrappingTest() throws CertificateException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        mockCertificateChainValidator.onCallDo(c -> {
                    c.report.addReportItem(
                            new ReportItem("test1", "test1", ReportItem.ReportItemStatus.INFO));
                    c.report.addReportItem(
                            new ReportItem("test2", "test2", ReportItem.ReportItemStatus.INDETERMINATE));
                    c.report.addReportItem(
                            new ReportItem("test3", "test3", ReportItem.ReportItemStatus.INVALID));
                }
        );
        ValidationReport report = validateTest(checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItems(0, 0, la -> la.withStatus(ReportItem.ReportItemStatus.INVALID))
                .hasLogItems(2, 2, la -> la.withStatus(ReportItem.ReportItemStatus.INDETERMINATE))
                .hasLogItem(la -> la.withStatus(ReportItem.ReportItemStatus.INFO)));
    }

    @Test
    public void noArchiveCutoffExtensionTest() throws Exception {
        Date producedAt = DateTimeUtil.addDaysToDate(checkCert.getNotAfter(), 5);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setProducedAt(producedAt);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.CERT_IS_EXPIRED, i -> checkCert.getNotAfter())
                        .withCertificate(checkCert))
        );
    }

    @Test
    public void noArchiveCutoffExtensionButRevokedStatusTest() throws Exception {
        Date producedAt = DateTimeUtil.addDaysToDate(checkCert.getNotAfter(), 5);
        Date revocationDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 5);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setProducedAt(producedAt);
        builder.setCertificateStatus(FACTORY.createRevokedStatus(revocationDate,
                FACTORY.createCRLReason().getKeyCompromise()));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasNumberOfFailures(0).hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED, i -> revocationDate)
                        .withCertificate(checkCert)));
    }

    @Test
    public void certExpiredBeforeArchiveCutoffDateTest() throws Exception {
        Date producedAt = DateTimeUtil.addDaysToDate(checkCert.getNotAfter(), 5);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setProducedAt(producedAt);
        builder.addResponseExtension(FACTORY.createOCSPObjectIdentifiers().getIdPkixOcspArchiveCutoff(),
                FACTORY.createDEROctetString(FACTORY.createASN1GeneralizedTime(
                        DateTimeUtil.addDaysToDate(producedAt, -3)).getEncoded()));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.CERT_IS_EXPIRED, i -> checkCert.getNotAfter())
                        .withCertificate(checkCert))
        );
    }

    @Test
    public void certExpiredAfterArchiveCutoffDateTest() throws Exception {
        Date producedAt = DateTimeUtil.addDaysToDate(checkCert.getNotAfter(), 5);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setProducedAt(producedAt);
        builder.addResponseExtension(FACTORY.createOCSPObjectIdentifiers().getIdPkixOcspArchiveCutoff(),
                FACTORY.createDEROctetString(FACTORY.createASN1GeneralizedTime(
                        DateTimeUtil.addDaysToDate(producedAt, -10)).getEncoded()));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasNumberOfFailures(0).hasNumberOfLogs(0));
    }


    @Test
    public void certificateRetrieverRetrieveIssuerCertificateFailureTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        MockIssuingCertificateRetriever mockCertificateRetriever = new MockIssuingCertificateRetriever();
        validatorChainBuilder.withIssuingCertificateRetrieverFactory(() -> mockCertificateRetriever);
        mockCertificateRetriever.onRetrieveIssuerCertificateDo(c -> {
            throw new RuntimeException("Test retrieveMissingCertificates failure");
        });

        ValidationReport report = validateTest(checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(l-> l.withMessage(OCSPValidator.UNABLE_TO_RETRIEVE_ISSUER)));
    }


    @Test
    public void certificateRetrieverRetrieveOCSPResponderCertificateFailureTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        MockIssuingCertificateRetriever mockCertificateRetriever =
                new MockIssuingCertificateRetriever(certificateRetriever);
        validatorChainBuilder.withIssuingCertificateRetrieverFactory(() -> mockCertificateRetriever);
        mockCertificateRetriever.onRetrieveOCSPResponderCertificateDo(c -> {
            throw new RuntimeException("Test retrieveMissingCertificates failure");
        });

        ValidationReport report = validateTest(checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(l-> l.withMessage(OCSPValidator.OCSP_RESPONDER_NOT_RETRIEVED)));
    }


    @Test
    public void certificateRetrieverIsCertificateTrustedFailureTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        MockIssuingCertificateRetriever mockCertificateRetriever =
                new MockIssuingCertificateRetriever(certificateRetriever);
        validatorChainBuilder.withIssuingCertificateRetrieverFactory(() -> mockCertificateRetriever);
        mockCertificateRetriever.onIsCertificateTrustedDo(c -> {
            throw new RuntimeException("Test isCertificateTrusted failure");
        });

        ValidationReport report = validateTest(checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(l-> l.withMessage(OCSPValidator.OCSP_RESPONDER_TRUST_NOT_RETRIEVED)));
    }

    @Test
    public void certificateRetrieverIsCertificateTrustedForOcspFailureTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        MockIssuingCertificateRetriever mockCertificateRetriever =
                new MockIssuingCertificateRetriever(certificateRetriever);
        validatorChainBuilder.withIssuingCertificateRetrieverFactory(() -> mockCertificateRetriever);
        mockCertificateRetriever.onIsCertificateTrustedDo(c -> false);
        MockTrustedCertificatesStore mockTrustedStore =
                new MockTrustedCertificatesStore(certificateRetriever.getTrustedCertificatesStore());
        mockCertificateRetriever.onGetTrustedCertificatesStoreDo(() -> mockTrustedStore);
        mockTrustedStore.onIsCertificateTrustedForOcspDo(c -> {
            throw new RuntimeException("Test isCertificateTrustedForOcsp failure");
        });

        ValidationReport report = validateTest(checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(l-> l.withMessage(OCSPValidator.OCSP_RESPONDER_TRUST_NOT_RETRIEVED)));
    }

    @Test
    public void certificateChainValidationFailureTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        mockCertificateChainValidator.onCallDo(c-> {
            throw new RuntimeException("Test chain validation failure");
        });

        ValidationReport report = validateTest(checkDate);


        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(l -> l.withMessage(OCSPValidator.OCSP_RESPONDER_NOT_VERIFIED)));
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
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp, checkDate, checkDate);
        return report;
    }

    private ValidationReport validateRevokedTestMocked(Date checkDate, Date revocationDate)
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
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp, checkDate, checkDate);
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
