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
package com.itextpdf.signatures.validation;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.TestCrlClientWrapper;
import com.itextpdf.signatures.testutils.client.TestOcspClientWrapper;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.validation.SignatureValidationProperties.OnlineFetching;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.CertificateSources;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.TimeBasedContexts;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.context.ValidatorContexts;
import com.itextpdf.signatures.validation.mocks.MockCrlValidator;
import com.itextpdf.signatures.validation.mocks.MockIssuingCertificateRetriever;
import com.itextpdf.signatures.validation.mocks.MockOCSPValidator;
import com.itextpdf.signatures.validation.mocks.MockSignatureValidationProperties;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;


import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import java.security.cert.X509CRL;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class RevocationDataValidatorTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/signatures/validation/RevocationDataValidatorTest/";
    private static final char[] PASSWORD = "testpassphrase".toCharArray();
    private static X509Certificate caCert;
    private static PrivateKey caPrivateKey;
    private static X509Certificate checkCert;
    private static X509Certificate responderCert;
    private static PrivateKey ocspRespPrivateKey;
    private static X509Certificate trustedOcspResponderCert;

    private IssuingCertificateRetriever certificateRetriever;
    private SignatureValidationProperties parameters;
    private final ValidationContext baseContext = new ValidationContext(ValidatorContext.SIGNATURE_VALIDATOR,
            CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);
    private ValidatorChainBuilder validatorChainBuilder;
    private MockCrlValidator mockCrlValidator;
    private MockOCSPValidator mockOCSPValidator;
    private MockSignatureValidationProperties mockParameters;

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

        trustedOcspResponderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
    }

    @BeforeEach
    public void setUp() {
        certificateRetriever = new IssuingCertificateRetriever();
        parameters = new SignatureValidationProperties();
        mockCrlValidator = new MockCrlValidator();
        mockOCSPValidator = new MockOCSPValidator();
        mockParameters = new MockSignatureValidationProperties(parameters);
        validatorChainBuilder = new ValidatorChainBuilder()
                .withIssuingCertificateRetrieverFactory(() -> certificateRetriever)
                .withSignatureValidationProperties(mockParameters)
                .withCRLValidatorFactory(() -> mockCrlValidator)
                .withOCSPValidatorFactory(() -> mockOCSPValidator);
    }

    @Test
    public void basicOCSPValidatorUsageTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setProducedAt(DateTimeUtil.addDaysToDate(checkDate, 5));
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 10)));
        TestOcspClientWrapper ocspClient = new TestOcspClientWrapper(new TestOcspClient().addBuilderForCertIssuer(caCert, builder));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addFreshnessResponse(Duration.ofDays(-2));
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();

        validator.addOcspClient(ocspClient);

        ReportItem reportItem = new ReportItem("validator", "message",
                ReportItem.ReportItemStatus.INFO);
        mockOCSPValidator.onCallDo(c -> c.report.addReportItem(reportItem));


        validator.validate(report, baseContext, checkCert, checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                // the logitem from the OCSP valdiation should be copied to the final report
                .hasNumberOfLogs(1)
                .hasLogItem(reportItem));
        // there should be one call per ocspClient
        Assertions.assertEquals(1, ocspClient.getCalls().size());

        // There was only one ocsp response so we expect 1 call to the ocsp validator
        Assertions.assertEquals(1, mockOCSPValidator.calls.size());

        // the validationDate should be passed as is
        Assertions.assertEquals(checkDate, mockOCSPValidator.calls.get(0).validationDate);

        // the response should be passed as is
        Assertions.assertEquals(ocspClient.getCalls().get(0).response, mockOCSPValidator.calls.get(0).ocspResp);

        // There should be a new report generated and any logs must be copied the actual report.
        Assertions.assertNotEquals(report, mockOCSPValidator.calls.get(0).report);
    }

    @Test
    public void basicCrlValidatorUsageTest() throws GeneralSecurityException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date revocationDate = DateTimeUtil.addDaysToDate(checkDate, -1);
        TestCrlBuilder builder = new TestCrlBuilder(caCert, caPrivateKey, checkDate);
        builder.setNextUpdate(DateTimeUtil.addDaysToDate(checkDate, 10));
        builder.addCrlEntry(checkCert, revocationDate, FACTORY.createCRLReason().getKeyCompromise());
        TestCrlClientWrapper crlClient = new TestCrlClientWrapper(new TestCrlClient().addBuilderForCertIssuer(builder));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addFreshnessResponse(Duration.ofDays(0));

        ReportItem reportItem = new ReportItem("validator", "message",
                ReportItem.ReportItemStatus.INFO);
        mockCrlValidator.onCallDo(c -> c.report.addReportItem(reportItem));

        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator()
                .addCrlClient(crlClient);
        validator.validate(report, baseContext, checkCert, checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfFailures(0)
                // the logitem from the CRL valdiation should be copied to the final report
                .hasNumberOfLogs(1)
                .hasLogItem(reportItem));
        // there should be one call per CrlClient
        Assertions.assertEquals(1, crlClient.getCalls().size());
        // since there was one response there should be one validator call
        Assertions.assertEquals(1, mockCrlValidator.calls.size());
        Assertions.assertEquals(checkCert, mockCrlValidator.calls.get(0).certificate);
        Assertions.assertEquals(checkDate, mockCrlValidator.calls.get(0).validationDate);
        // There should be a new report generated and any logs must be copied the actual report.
        Assertions.assertNotEquals(report, mockCrlValidator.calls.get(0).report);
        Assertions.assertEquals(crlClient.getCalls().get(0).responses.get(0), mockCrlValidator.calls.get(0).crl);
    }

    @Test
    public void crlResponseOrderingTest() throws CertificateEncodingException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        Date thisUpdate1 = DateTimeUtil.addDaysToDate(checkDate, -2);
        TestCrlBuilder builder1 = new TestCrlBuilder(caCert, caPrivateKey, thisUpdate1);
        builder1.setNextUpdate(DateTimeUtil.addDaysToDate(checkDate, -2));
        TestCrlClientWrapper crlClient1 = new TestCrlClientWrapper(
                new TestCrlClient().addBuilderForCertIssuer(builder1));

        TestCrlBuilder builder2 = new TestCrlBuilder(caCert, caPrivateKey, checkDate);
        builder2.setNextUpdate(checkDate);
        TestCrlClientWrapper crlClient2 = new TestCrlClientWrapper(
                new TestCrlClient().addBuilderForCertIssuer(builder2));

        Date thisUpdate3 = DateTimeUtil.addDaysToDate(checkDate, +2);
        TestCrlBuilder builder3 = new TestCrlBuilder(caCert, caPrivateKey, thisUpdate3);
        builder3.setNextUpdate(DateTimeUtil.addDaysToDate(checkDate, -2));
        TestCrlClientWrapper crlClient3 = new TestCrlClientWrapper(
                new TestCrlClient().addBuilderForCertIssuer(builder3));

        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator()
                .addCrlClient(crlClient1)
                .addCrlClient(crlClient2)
                .addCrlClient(crlClient3);

        mockCrlValidator.onCallDo(c -> c.report.addReportItem(new ReportItem("test", "test", ReportItem.ReportItemStatus.INDETERMINATE)));

        ValidationReport report = new ValidationReport();
        validator.validate(report, baseContext, checkCert, checkDate);

        Assertions.assertEquals(crlClient3.getCalls().get(0).responses.get(0), mockCrlValidator.calls.get(0).crl);
        Assertions.assertEquals(crlClient2.getCalls().get(0).responses.get(0), mockCrlValidator.calls.get(1).crl);
        Assertions.assertEquals(crlClient1.getCalls().get(0).responses.get(0), mockCrlValidator.calls.get(2).crl);
    }

    @Test
    public void ocspResponseOrderingTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        TestOcspResponseBuilder builder1 = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder1.setProducedAt(checkDate);
        builder1.setThisUpdate(DateTimeUtil.getCalendar(checkDate));
        builder1.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        TestOcspClientWrapper ocspClient1 = new TestOcspClientWrapper(
                new TestOcspClient().addBuilderForCertIssuer(caCert, builder1));

        TestOcspResponseBuilder builder2 = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder2.setProducedAt(DateTimeUtil.addDaysToDate(checkDate, 5));
        builder2.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        builder2.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 10)));
        TestOcspClientWrapper ocspClient2 = new TestOcspClientWrapper(
                new TestOcspClient().addBuilderForCertIssuer(caCert, builder2));

        TestOcspResponseBuilder builder3 = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder3.setProducedAt(DateTimeUtil.addDaysToDate(checkDate, 2));
        builder3.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 2)));
        builder3.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 8)));
        TestOcspClientWrapper ocspClient3 = new TestOcspClientWrapper(
                new TestOcspClient().addBuilderForCertIssuer(caCert, builder3));

        mockOCSPValidator.onCallDo(c -> c.report.addReportItem(
                new ReportItem("", "", ReportItem.ReportItemStatus.INDETERMINATE)));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                .addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                .addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                .addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                .addFreshnessResponse(Duration.ofDays(-2));
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator()
                .addOcspClient(ocspClient1)
                .addOcspClient(ocspClient2)
                .addOcspClient(ocspClient3);

        validator.validate(report, baseContext, checkCert, checkDate);

        Assertions.assertEquals(ocspClient2.getCalls().get(0).response, mockOCSPValidator.calls.get(0).ocspResp);
        Assertions.assertEquals(ocspClient3.getCalls().get(0).response, mockOCSPValidator.calls.get(1).ocspResp);
        Assertions.assertEquals(ocspClient1.getCalls().get(0).response, mockOCSPValidator.calls.get(2).ocspResp);
    }

    @Test
    public void validityAssuredTest() throws CertificateException, IOException {
        String checkCertFileName = SOURCE_FOLDER + "validityAssuredSigningCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        ValidationReport report = new ValidationReport();
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();

        validator.validate(report, baseContext, certificate, checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasLogItem(la -> la
                        .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                        .withMessage(RevocationDataValidator.VALIDITY_ASSURED)
                        .withCertificate(certificate)
                ));
    }

    @Test
    public void noRevAvailTest() throws CertificateException, IOException {
        String checkCertFileName = SOURCE_FOLDER + "noRevAvailCertWithoutCA.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        ValidationReport report = new ValidationReport();
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();

        validator.validate(report, baseContext, certificate, checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasLogItem(la -> la
                        .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                        .withMessage(RevocationDataValidator.NO_REV_AVAILABLE, m -> certificate.getSubjectX500Principal())
                        .withCertificate(certificate)
                ));
    }

    @Test
    public void noRevAvailWithCATest() throws CertificateException, IOException {
        String checkCertFileName = SOURCE_FOLDER + "noRevAvailCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        ValidationReport report = new ValidationReport();
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();

        validator.validate(report, baseContext, certificate, checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasLogItem(la -> la
                        .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                        .withMessage(RevocationDataValidator.NO_REV_AVAILABLE_CA, m -> certificate.getSubjectX500Principal())
                        .withCertificate(certificate)
                ));
    }

    @Test
    public void selfSignedCertificateIsNotValidatedTest() {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        ValidationReport report = new ValidationReport();
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();

        validator.validate(report, baseContext, caCert, checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasLogItem(la -> la
                        .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                        .withMessage(RevocationDataValidator.SELF_SIGNED_CERTIFICATE)
                        .withCertificate(caCert)
                ));
    }

    @Test
    public void nocheckExtensionShouldNotFurtherValidateTest() {
        ValidationReport report = new ValidationReport();

        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();

        validator.validate(report, baseContext.setCertificateSource(CertificateSource.OCSP_ISSUER),
                trustedOcspResponderCert, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasLogItem(la -> la
                        .withStatus(ReportItem.ReportItemStatus.INFO)
                        .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                        .withMessage(RevocationDataValidator.TRUSTED_OCSP_RESPONDER)
                ));
    }

    @Test
    public void noRevocationDataTest() {
        ValidationReport report = new ValidationReport();

        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                        TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                        Duration.ofDays(-2));
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();

        validator.validate(report, baseContext, checkCert, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasLogItem(la -> la
                        .withStatus(ReportItem.ReportItemStatus.INDETERMINATE)
                        .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                        .withMessage(RevocationDataValidator.NO_REVOCATION_DATA)
                ));
    }

    @Test
    public void doNotFetchOcspOnlineIfCrlAvailableTest() throws Exception {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        Date thisUpdate = DateTimeUtil.addDaysToDate(checkDate, -2);
        TestCrlBuilder builder = new TestCrlBuilder(caCert, caPrivateKey, thisUpdate);
        builder.setNextUpdate(DateTimeUtil.addDaysToDate(checkDate, 2));
        TestCrlClientWrapper crlClient = new TestCrlClientWrapper(new TestCrlClient().addBuilderForCertIssuer(builder));

        mockOCSPValidator.onCallDo(c -> c.report.addReportItem(
                new ReportItem("", "", ReportItem.ReportItemStatus.INDETERMINATE)));
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts
                        .all(), SignatureValidationProperties.OnlineFetching.FETCH_IF_NO_OTHER_DATA_AVAILABLE)
                .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                        Duration.ofDays(-2));
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator()
                .addCrlClient(crlClient);

        ValidationReport report = new ValidationReport();
        validator.validate(report, baseContext, checkCert, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasNumberOfFailures(0).hasNumberOfLogs(0));
    }

    @Test
    public void doNotFetchCrlOnlineIfOcspAvailableTest() throws Exception {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setProducedAt(checkDate);
        builder.setThisUpdate(DateTimeUtil.getCalendar(checkDate));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        TestOcspClientWrapper ocspClient = new TestOcspClientWrapper(
                new TestOcspClient().addBuilderForCertIssuer(caCert, builder));

        mockOCSPValidator.onCallDo(c -> c.report.addReportItem(
                new ReportItem("", "", ReportItem.ReportItemStatus.INFO)));
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts
                        .all(), SignatureValidationProperties.OnlineFetching.FETCH_IF_NO_OTHER_DATA_AVAILABLE)
                .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                        Duration.ofDays(-2));
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator()
                .addOcspClient(ocspClient);

        ValidationReport report = new ValidationReport();
        validator.validate(report, baseContext, checkCert, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasNumberOfFailures(0).hasNumberOfLogs(1));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate C=BY,O=iText,CN=iTextTestSignRsa",
                    logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url: Passed url can not be null.",
                    logLevel = LogLevelConstants.INFO)
    })
    public void tryToFetchCrlOnlineIfOnlyIndeterminateOcspAvailableTest() throws Exception {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setProducedAt(checkDate);
        builder.setThisUpdate(DateTimeUtil.getCalendar(checkDate));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        TestOcspClientWrapper ocspClient = new TestOcspClientWrapper(
                new TestOcspClient().addBuilderForCertIssuer(caCert, builder));

        mockOCSPValidator.onCallDo(c -> c.report.addReportItem(
                new ReportItem("", "", ReportItem.ReportItemStatus.INDETERMINATE)));
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        parameters.setRevocationOnlineFetching(ValidatorContexts.of(ValidatorContext.CRL_VALIDATOR),
                        CertificateSources.all(), TimeBasedContexts.all(),
                        SignatureValidationProperties.OnlineFetching.FETCH_IF_NO_OTHER_DATA_AVAILABLE)
                .setRevocationOnlineFetching(ValidatorContexts.of(ValidatorContext.OCSP_VALIDATOR),
                        CertificateSources.all(), TimeBasedContexts.all(),
                        SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                        Duration.ofDays(-2));
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator()
                .addOcspClient(ocspClient);

        ValidationReport report = new ValidationReport();
        validator.validate(report, baseContext, checkCert, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasLogItem(la -> la
                        .withStatus(ReportItem.ReportItemStatus.INDETERMINATE)
                        .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                        .withMessage(RevocationDataValidator.NO_REVOCATION_DATA)
                ));
    }

    @Test
    public void tryFetchRevocationDataOnlineTest() {
        ValidationReport report = new ValidationReport();
        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                        TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.ALWAYS_FETCH)
                .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                        Duration.ofDays(-2));
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();
        validator.validate(report, baseContext, checkCert, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(la -> la.withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                        .withMessage(RevocationDataValidator.NO_REVOCATION_DATA)
                ));
    }

    @Test
    public void crlEncodingErrorTest() throws Exception {
        byte[] crl = new TestCrlBuilder(caCert, caPrivateKey).makeCrl();
        crl[5] = 0;
        ValidationReport report = new ValidationReport();
        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                        TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                        Duration.ofDays(-2));
        parameters.setFreshness(ValidatorContexts.all(), CertificateSources.all(),
                TimeBasedContexts.all(), Duration.ofDays(2));
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();
        validator.addCrlClient(new ICrlClient() {
                    @Override
                    public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) {
                        return Collections.singletonList(crl);
                    }

                    @Override
                    public String toString() {
                        return "Test crl client.";
                    }
                })
                .validate(report, baseContext, checkCert, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(la -> la
                        .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                        .withMessage(MessageFormatUtil.format(RevocationDataValidator.CANNOT_PARSE_CRL, "Test crl client."))
                )
                .hasLogItem(la -> la
                        .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                        .withMessage(RevocationDataValidator.NO_REVOCATION_DATA)
                ));

    }

    @Test
    public void sortResponsesTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        // The oldest one, but the only one valid.
        TestOcspResponseBuilder ocspBuilder1 = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        ocspBuilder1.setProducedAt(checkDate);
        ocspBuilder1.setThisUpdate(DateTimeUtil.getCalendar(checkDate));
        ocspBuilder1.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 3)));
        TestOcspClientWrapper ocspClient1 = new TestOcspClientWrapper(
                new TestOcspClient().addBuilderForCertIssuer(caCert, ocspBuilder1));

        TestOcspResponseBuilder ocspBuilder2 = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        ocspBuilder2.setProducedAt(DateTimeUtil.addDaysToDate(checkDate, 3));
        ocspBuilder2.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 3)));
        ocspBuilder2.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        ocspBuilder2.setCertificateStatus(FACTORY.createUnknownStatus());
        TestOcspClientWrapper ocspClient2 = new TestOcspClientWrapper(
                new TestOcspClient().addBuilderForCertIssuer(caCert, ocspBuilder2));

        TestOcspResponseBuilder ocspBuilder3 = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        ocspBuilder3.setProducedAt(DateTimeUtil.addDaysToDate(checkDate, 5));
        ocspBuilder3.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        ocspBuilder3.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 10)));
        ocspBuilder3.setCertificateStatus(FACTORY.createUnknownStatus());
        TestOcspClientWrapper ocspClient3 = new TestOcspClientWrapper(
                new TestOcspClient().addBuilderForCertIssuer(caCert, ocspBuilder3));

        TestCrlBuilder crlBuilder1 = new TestCrlBuilder(caCert, caPrivateKey, checkDate);
        crlBuilder1.setNextUpdate(DateTimeUtil.addDaysToDate(checkDate, 2));

        TestCrlBuilder crlBuilder2 = new TestCrlBuilder(caCert, caPrivateKey, DateTimeUtil.addDaysToDate(checkDate, 2));
        crlBuilder2.setNextUpdate(DateTimeUtil.addDaysToDate(checkDate, 5));
        TestCrlClientWrapper crlClient = new TestCrlClientWrapper(new TestCrlClient()
                .addBuilderForCertIssuer(crlBuilder1)
                .addBuilderForCertIssuer(crlBuilder2));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                        TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                .setFreshness(ValidatorContexts.of(ValidatorContext.CRL_VALIDATOR), CertificateSources.all(),
                        TimeBasedContexts.all(), Duration.ofDays(-5));
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator()
                .addCrlClient(crlClient)
                .addOcspClient(ocspClient1)
                .addOcspClient(ocspClient2)
                .addOcspClient(ocspClient3);

        mockCrlValidator.onCallDo(c -> {
            c.report.addReportItem(new ReportItem("1", "2", ReportItem.ReportItemStatus.INDETERMINATE));
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        });
        mockOCSPValidator.onCallDo(c -> {
            c.report.addReportItem(new ReportItem("1", "2", ReportItem.ReportItemStatus.INDETERMINATE));
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        });
        validator.validate(report, baseContext, checkCert, checkDate);

        Assertions.assertTrue(mockOCSPValidator.calls.get(0).timeStamp.before(mockOCSPValidator.calls.get(1).timeStamp));
        Assertions.assertTrue(mockOCSPValidator.calls.get(1).timeStamp.before(mockCrlValidator.calls.get(0).timeStamp));
        Assertions.assertTrue(mockCrlValidator.calls.get(0).timeStamp.before(mockCrlValidator.calls.get(1).timeStamp));
        Assertions.assertTrue(mockCrlValidator.calls.get(1).timeStamp.before(mockOCSPValidator.calls.get(2).timeStamp));

        Assertions.assertEquals(ocspClient1.getCalls().get(0).response, mockOCSPValidator.calls.get(2).ocspResp);
        Assertions.assertEquals(ocspClient2.getCalls().get(0).response, mockOCSPValidator.calls.get(1).ocspResp);
        Assertions.assertEquals(ocspClient3.getCalls().get(0).response, mockOCSPValidator.calls.get(0).ocspResp);
        Assertions.assertEquals(crlClient.getCalls().get(0).responses.get(0), mockCrlValidator.calls.get(1).crl);
        Assertions.assertEquals(crlClient.getCalls().get(0).responses.get(1), mockCrlValidator.calls.get(0).crl);
    }

@Test
    public void responsesFromValidationClientArePassedTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        Date ocspGeneration = DateTimeUtil.addDaysToDate(checkDate, 2);
        // Here we check that proper generation time was set.
        mockOCSPValidator.onCallDo(c -> Assertions.assertEquals(ocspGeneration, c.responseGenerationDate));

        Date crlGeneration = DateTimeUtil.addDaysToDate(checkDate, 3);
        // Here we check that proper generation time was set.
        mockCrlValidator.onCallDo(c -> Assertions.assertEquals(crlGeneration, c.responseGenerationDate));

        ValidationReport report = new ValidationReport();
        RevocationDataValidator validator = validatorChainBuilder.getRevocationDataValidator();

        ValidationOcspClient ocspClient = new ValidationOcspClient() {
            @Override
            public byte[] getEncoded(X509Certificate checkCert, X509Certificate issuerCert, String url) {
                Assertions.fail("This method shall not be called");
                return null;
            }
        };
        TestOcspResponseBuilder ocspBuilder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        byte[] ocspResponseBytes = new TestOcspClient().addBuilderForCertIssuer(caCert, ocspBuilder)
                .getEncoded(checkCert, caCert, null);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspResponseBytes)));
        ocspClient.addResponse(basicOCSPResp, ocspGeneration, TimeBasedContext.HISTORICAL);
        validator.addOcspClient(ocspClient);

        ValidationCrlClient crlClient = new ValidationCrlClient() {
            @Override
            public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) {
                Assertions.fail("This method shall not be called");
                return null;
            }
        };
        TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, caPrivateKey, checkDate);
        byte[] crlResponseBytes = new ArrayList<>(
                new TestCrlClient().addBuilderForCertIssuer(crlBuilder).getEncoded(checkCert, null)).get(0);
        crlClient.addCrl((X509CRL) CertificateUtil.parseCrlFromBytes(crlResponseBytes), crlGeneration, TimeBasedContext.HISTORICAL);
        validator.addCrlClient(crlClient);

        validator.validate(report, baseContext, checkCert, checkDate);
    }

    @Test
    public void timeBasedContextProperlySetValidationClientsTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        mockOCSPValidator.onCallDo(c -> Assertions.assertEquals(TimeBasedContext.HISTORICAL, c.context.getTimeBasedContext()));

        mockCrlValidator.onCallDo(c -> Assertions.assertEquals(TimeBasedContext.HISTORICAL, c.context.getTimeBasedContext()));

        ValidationReport report = new ValidationReport();
        RevocationDataValidator validator = validatorChainBuilder.getRevocationDataValidator();

        ValidationOcspClient ocspClient = new ValidationOcspClient();
        TestOcspResponseBuilder ocspBuilder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        byte[] ocspResponseBytes = new TestOcspClient().addBuilderForCertIssuer(caCert, ocspBuilder)
                .getEncoded(checkCert, caCert, null);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspResponseBytes)));
        ocspClient.addResponse(basicOCSPResp, checkDate, TimeBasedContext.HISTORICAL);
        validator.addOcspClient(ocspClient);

        ValidationCrlClient crlClient = new ValidationCrlClient();
        TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, caPrivateKey, checkDate);
        byte[] crlResponseBytes = new ArrayList<>(
                new TestCrlClient().addBuilderForCertIssuer(crlBuilder).getEncoded(checkCert, null)).get(0);
        crlClient.addCrl((X509CRL) CertificateUtil.parseCrlFromBytes(crlResponseBytes), checkDate, TimeBasedContext.HISTORICAL);
        validator.addCrlClient(crlClient);

        validator.validate(report, baseContext, checkCert, checkDate);
    }

    @Test
    public void timeBasedContextProperlySetRandomClientsTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        mockOCSPValidator.onCallDo(c -> Assertions.assertEquals(TimeBasedContext.PRESENT, c.context.getTimeBasedContext()));
        mockCrlValidator.onCallDo(c -> Assertions.assertEquals(TimeBasedContext.PRESENT, c.context.getTimeBasedContext()));

        ValidationReport report = new ValidationReport();
        RevocationDataValidator validator = validatorChainBuilder.getRevocationDataValidator();

        TestOcspResponseBuilder ocspBuilder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        validator.addOcspClient(new TestOcspClient().addBuilderForCertIssuer(caCert, ocspBuilder));

        TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, caPrivateKey, checkDate);
        validator.addCrlClient(new TestCrlClient().addBuilderForCertIssuer(crlBuilder));

        validator.validate(report, baseContext.setTimeBasedContext(TimeBasedContext.HISTORICAL), checkCert, checkDate);
    }

    @Test
    public void timeBasedContextProperlySetOnlineClientsTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        mockOCSPValidator.onCallDo(c -> Assertions.assertEquals(TimeBasedContext.PRESENT, c.context.getTimeBasedContext()));
        mockCrlValidator.onCallDo(c -> Assertions.assertEquals(TimeBasedContext.PRESENT, c.context.getTimeBasedContext()));

        ValidationReport report = new ValidationReport();
        RevocationDataValidator validator = validatorChainBuilder.getRevocationDataValidator();

        TestOcspResponseBuilder ocspBuilder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        TestOcspClient testOcspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, ocspBuilder);
        OcspClientBouncyCastle ocspClient = new OcspClientBouncyCastle() {
            @Override
            public byte[] getEncoded(X509Certificate checkCert, X509Certificate rootCert, String url) {
                return testOcspClient.getEncoded(checkCert, rootCert, url);
            }
        };
        validator.addOcspClient(ocspClient);

        TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, caPrivateKey, checkDate);
        TestCrlClient testCrlClient = new TestCrlClient().addBuilderForCertIssuer(crlBuilder);
        CrlClientOnline crlClient = new CrlClientOnline() {
            @Override
            public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) {
                return testCrlClient.getEncoded(checkCert, url);
            }
        };
        validator.addCrlClient(crlClient);

        validator.validate(report, baseContext.setTimeBasedContext(TimeBasedContext.HISTORICAL), checkCert, checkDate);
    }

    @Test
    public void basicOCSPValidatorFailureTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setProducedAt(DateTimeUtil.addDaysToDate(checkDate, 5));
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 10)));
        TestOcspClientWrapper ocspClient = new TestOcspClientWrapper(new TestOcspClient().addBuilderForCertIssuer(caCert, builder));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addFreshnessResponse(Duration.ofDays(-2));
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();

        validator.addOcspClient(ocspClient);

        mockOCSPValidator.onCallDo(c -> {throw new RuntimeException("Test OCSP client failure"); });


        validator.validate(report, baseContext, checkCert, checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                // the logitem from the OCSP valdiation should be copied to the final report
                .hasLogItem(l -> l.withMessage(RevocationDataValidator.OCSP_VALIDATOR_FAILURE)));
    }

    @Test
    public void OCSPValidatorFailureTest() throws GeneralSecurityException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date revocationDate = DateTimeUtil.addDaysToDate(checkDate, -1);
        TestCrlBuilder builder = new TestCrlBuilder(caCert, caPrivateKey, checkDate);
        builder.setNextUpdate(DateTimeUtil.addDaysToDate(checkDate, 10));
        builder.addCrlEntry(checkCert, revocationDate, FACTORY.createCRLReason().getKeyCompromise());
        TestCrlClientWrapper crlClient = new TestCrlClientWrapper(new TestCrlClient().addBuilderForCertIssuer(builder));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addFreshnessResponse(Duration.ofDays(0));

        mockCrlValidator.onCallDo(c -> {
            throw new RuntimeException("Test OCSP client failure");
        });

        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator()
                .addCrlClient(crlClient);
        validator.validate(report, baseContext, checkCert, checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                // the logitem from the OCSP valdiation should be copied to the final report
                .hasLogItem(l -> l.withMessage(RevocationDataValidator.CRL_VALIDATOR_FAILURE)));
    }

    //certificateRetriever.retrieveIssuerCertificate

    @Test
    public void certificateRetrieverRetrieveIssuerCertificateFailureTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setProducedAt(DateTimeUtil.addDaysToDate(checkDate, 5));
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 10)));
        TestOcspClientWrapper ocspClient = new TestOcspClientWrapper(new TestOcspClient().addBuilderForCertIssuer(caCert, builder));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addFreshnessResponse(Duration.ofDays(-2));

        MockIssuingCertificateRetriever mockCertificateRetreiver =
                new MockIssuingCertificateRetriever(certificateRetriever).onRetrieveIssuerCertificateDo(c -> {
                    throw new RuntimeException("Test retrieveIssuerCertificate failure");
                });
        validatorChainBuilder.withIssuingCertificateRetrieverFactory(()-> mockCertificateRetreiver);
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();

        validator.addOcspClient(ocspClient);

        ReportItem reportItem = new ReportItem("validator", "message",
                ReportItem.ReportItemStatus.INFO);
        mockOCSPValidator.onCallDo(c -> c.report.addReportItem(reportItem));

        validator.validate(report, baseContext, checkCert, checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(l -> l.withMessage(RevocationDataValidator.UNABLE_TO_RETRIEVE_REV_DATA_ONLINE)));
    }

    @Test
    public void ocspClientGetEncodedFailureTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setProducedAt(DateTimeUtil.addDaysToDate(checkDate, 5));
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 10)));
        TestOcspClientWrapper ocspClient = new TestOcspClientWrapper(new TestOcspClient().addBuilderForCertIssuer(caCert, builder));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addFreshnessResponse(Duration.ofDays(-2));

        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();

        validator.addOcspClient(ocspClient);

        ReportItem reportItem = new ReportItem("validator", "message",
                ReportItem.ReportItemStatus.INFO);
        mockOCSPValidator.onCallDo(c -> c.report.addReportItem(reportItem));


        ocspClient.onGetEncodedDo(c -> {throw new RuntimeException("Test onGetEncoded failure");});
        validator.validate(report, baseContext, checkCert, checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(l -> l.withMessage(RevocationDataValidator.OCSP_CLIENT_FAILURE, p -> ocspClient)));
    }

    @Test
    public void crlClientGetEncodedFailureTest() throws GeneralSecurityException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date revocationDate = DateTimeUtil.addDaysToDate(checkDate, -1);
        TestCrlBuilder builder = new TestCrlBuilder(caCert, caPrivateKey, checkDate);
        builder.setNextUpdate(DateTimeUtil.addDaysToDate(checkDate, 10));
        builder.addCrlEntry(checkCert, revocationDate, FACTORY.createCRLReason().getKeyCompromise());
        TestCrlClientWrapper crlClient = new TestCrlClientWrapper(new TestCrlClient().addBuilderForCertIssuer(builder));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
        mockParameters.addFreshnessResponse(Duration.ofDays(0));

        ReportItem reportItem = new ReportItem("validator", "message",
                ReportItem.ReportItemStatus.INFO);
        mockCrlValidator.onCallDo(c -> c.report.addReportItem(reportItem));

        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator()
                .addCrlClient(crlClient);

        crlClient.onGetEncodedDo(c -> {throw new RuntimeException("Test getEncoded failure");});
        validator.validate(report, baseContext, checkCert, checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(l -> l.withMessage(RevocationDataValidator.CRL_CLIENT_FAILURE,p -> crlClient.toString())));

    }

    @Test
    public void testCrlClientInjection() throws CertificateEncodingException, IOException {

        TestCrlClient testCrlClient = new TestCrlClient();
        TestCrlClientWrapper mockCrlClient = new TestCrlClientWrapper(testCrlClient);
        validatorChainBuilder.withCrlClient(() -> mockCrlClient);
        testCrlClient.addBuilderForCertIssuer(caCert, caPrivateKey);

        ValidationReport report = new ValidationReport();
        ValidationContext context = new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,CertificateSource.SIGNER_CERT, TimeBasedContext.HISTORICAL);

        validatorChainBuilder.buildRevocationDataValidator().validate(report, context, checkCert, TimeTestUtil.TEST_DATE_TIME);

        Assertions.assertEquals(1,mockCrlClient.getCalls().size());
    }

    @Test
    public void testOcspClientInjection() throws CertificateEncodingException, IOException {

        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setProducedAt(DateTimeUtil.addDaysToDate(checkDate, 5));
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 10)));
        TestOcspClientWrapper mockOcspClient = new TestOcspClientWrapper(new TestOcspClient().addBuilderForCertIssuer(caCert, builder));


        validatorChainBuilder.withOcspClient(() -> mockOcspClient);

        mockParameters.addRevocationOnlineFetchingResponse(OnlineFetching.ALWAYS_FETCH);
        certificateRetriever.addKnownCertificates(Arrays.asList(caCert, trustedOcspResponderCert));

        ValidationReport report = new ValidationReport();
        ValidationContext context = new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,CertificateSource.SIGNER_CERT, TimeBasedContext.HISTORICAL);

        validatorChainBuilder.buildRevocationDataValidator().validate(report, context, checkCert, TimeTestUtil.TEST_DATE_TIME);

        Assertions.assertEquals(2,mockOcspClient.getBasicResponceCalls().size());
    }
}
