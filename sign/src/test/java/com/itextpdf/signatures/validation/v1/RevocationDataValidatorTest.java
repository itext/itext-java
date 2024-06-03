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
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.TestCrlClientWrapper;
import com.itextpdf.signatures.testutils.client.TestOcspClientWrapper;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.validation.v1.context.CertificateSource;
import com.itextpdf.signatures.validation.v1.context.CertificateSources;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContext;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContexts;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContexts;
import com.itextpdf.signatures.validation.v1.mocks.MockCrlValidator;
import com.itextpdf.signatures.validation.v1.mocks.MockOCSPValidator;
import com.itextpdf.signatures.validation.v1.mocks.MockSignatureValidationProperties;
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
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Category(BouncyCastleUnitTest.class)
public class RevocationDataValidatorTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/signatures/validation/v1/RevocationDataValidatorTest/";
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

        trustedOcspResponderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
    }

    @Before
    public void setUp() {
        certificateRetriever = new IssuingCertificateRetriever();
        parameters = new SignatureValidationProperties();
        mockCrlValidator = new MockCrlValidator();
        mockOCSPValidator = new MockOCSPValidator();
        mockParameters = new MockSignatureValidationProperties(parameters);
        validatorChainBuilder = new ValidatorChainBuilder()
                .withIssuingCertificateRetriever(certificateRetriever)
                .withSignatureValidationProperties(mockParameters)
                .withCRLValidator(mockCrlValidator)
                .withOCSPValidator(mockOCSPValidator);
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
        Assert.assertEquals(1, ocspClient.getCalls().size());

        // There was only one ocsp response so we expect 1 call to the ocsp validator
        Assert.assertEquals(1,mockOCSPValidator.calls.size());

        // the validationDate should be passed as is
        Assert.assertEquals(checkDate, mockOCSPValidator.calls.get(0).validationDate);

        // the response should be passed as is
        Assert.assertEquals(ocspClient.getCalls().get(0).response , mockOCSPValidator.calls.get(0).ocspResp);

        // There should be a new report generated and any logs must be copied the actual report.
        Assert.assertNotEquals(report, mockOCSPValidator.calls.get(0).report);
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
        Assert.assertEquals(1, crlClient.getCalls().size());
        // since there was one response there should be one validator call
        Assert.assertEquals(1 , mockCrlValidator.calls.size());
        Assert.assertEquals(checkCert, mockCrlValidator.calls.get(0).certificate);
        Assert.assertEquals(checkDate, mockCrlValidator.calls.get(0).validationDate);
        // There should be a new report generated and any logs must be copied the actual report.
        Assert.assertNotEquals(report, mockCrlValidator.calls.get(0).report);
        Assert.assertEquals(crlClient.getCalls().get(0).responses.get(0), mockCrlValidator.calls.get(0).crl);
    }

    @Test
    public void crlResponseOrderingTest() throws CertificateEncodingException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        Date thisUpdate1 = DateTimeUtil.addDaysToDate(checkDate, -2);
        TestCrlBuilder builder1 = new TestCrlBuilder(caCert, caPrivateKey, thisUpdate1);
        builder1.setNextUpdate(DateTimeUtil.addDaysToDate(checkDate, -2));
        TestCrlClientWrapper crlClient1 = new TestCrlClientWrapper(
                new TestCrlClient().addBuilderForCertIssuer(builder1));

        Date thisUpdate2 = checkDate;
        TestCrlBuilder builder2 = new TestCrlBuilder(caCert, caPrivateKey, thisUpdate2);
        builder2.setNextUpdate(checkDate);
        TestCrlClientWrapper crlClient2 =new TestCrlClientWrapper(
                new TestCrlClient().addBuilderForCertIssuer(builder2));

        Date thisUpdate3 = DateTimeUtil.addDaysToDate(checkDate, +2);
        TestCrlBuilder builder3 = new TestCrlBuilder(caCert, caPrivateKey, thisUpdate3);
        builder3.setNextUpdate(DateTimeUtil.addDaysToDate(checkDate, -2));
        TestCrlClientWrapper crlClient3 =new TestCrlClientWrapper(
                new TestCrlClient().addBuilderForCertIssuer(builder3));

        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator()
                .addCrlClient(crlClient1)
                .addCrlClient(crlClient2)
                .addCrlClient(crlClient3);

        mockCrlValidator.onCallDo(c -> c.report.addReportItem(new ReportItem("test", "test", ReportItem.ReportItemStatus.INDETERMINATE)));

        ValidationReport report = new ValidationReport();
        validator.validate(report, baseContext, checkCert, checkDate);

        Assert.assertEquals(crlClient3.getCalls().get(0).responses.get(0), mockCrlValidator.calls.get(0).crl);
        Assert.assertEquals(crlClient2.getCalls().get(0).responses.get(0), mockCrlValidator.calls.get(1).crl);
        Assert.assertEquals(crlClient1.getCalls().get(0).responses.get(0), mockCrlValidator.calls.get(2).crl);
    }

    @Test
    public void ocspResponseOrderingTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        TestOcspResponseBuilder builder1 = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder1.setProducedAt(checkDate);
        builder1.setThisUpdate(DateTimeUtil.getCalendar(checkDate));
        builder1.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        TestOcspClientWrapper ocspClient1 =  new TestOcspClientWrapper(
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
                new ReportItem("","", ReportItem.ReportItemStatus.INDETERMINATE)));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        mockParameters.addRevocationOnlineFetchingResponse(SignatureValidationProperties .OnlineFetching.NEVER_FETCH)
                .addFreshnessResponse(Duration.ofDays(-2));
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator()
                .addOcspClient(ocspClient1)
                .addOcspClient(ocspClient2)
                .addOcspClient(ocspClient3);

        validator.validate(report, baseContext, checkCert, checkDate);

        Assert.assertEquals(ocspClient2.getCalls().get(0).response, mockOCSPValidator.calls.get(0).ocspResp);
        Assert.assertEquals(ocspClient3.getCalls().get(0).response, mockOCSPValidator.calls.get(1).ocspResp);
        Assert.assertEquals(ocspClient1.getCalls().get(0).response, mockOCSPValidator.calls.get(2).ocspResp);

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
    public void nocheckExtensionShouldNotFurtherValdiateTest() {
        ValidationReport report = new ValidationReport();

        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                        TimeBasedContexts.all(), SignatureValidationProperties .OnlineFetching.NEVER_FETCH);
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
                        TimeBasedContexts.all(), SignatureValidationProperties .OnlineFetching.NEVER_FETCH)
                .setFreshness(ValidatorContexts.all(), CertificateSources.all(),TimeBasedContexts.all(),
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
    public void tryFetchRevocationDataOnlineTest() {
        ValidationReport report = new ValidationReport();
        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                        TimeBasedContexts.all(), SignatureValidationProperties .OnlineFetching.FETCH_IF_NO_OTHER_DATA_AVAILABLE)
                .setFreshness(ValidatorContexts.all(), CertificateSources.all(),TimeBasedContexts.all(),
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
        byte[] crl = new TestCrlBuilder(caCert,  caPrivateKey).makeCrl();
        crl[5] = 0;
        ValidationReport report = new ValidationReport();
        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                        TimeBasedContexts.all(), SignatureValidationProperties .OnlineFetching.NEVER_FETCH)
                .setFreshness(ValidatorContexts.all(), CertificateSources.all(),TimeBasedContexts.all(),
                        Duration.ofDays(-2));
        parameters.setFreshness(ValidatorContexts.all(), CertificateSources.all(),
                TimeBasedContexts.all(),Duration.ofDays(2));
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();
        validator.addCrlClient(new ICrlClient() {
                    @Override
                    public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) {
                        return Collections.singletonList(crl);
                    }
                })
                .validate(report, baseContext, checkCert, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(la -> la
                    .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                    .withMessage(RevocationDataValidator.CRL_PARSING_ERROR)
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
                        TimeBasedContexts.all(), SignatureValidationProperties .OnlineFetching.NEVER_FETCH)
                .setFreshness(ValidatorContexts.of(ValidatorContext.CRL_VALIDATOR), CertificateSources.all(),
                        TimeBasedContexts.all(), Duration.ofDays(-5));
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator()
                .addCrlClient(crlClient)
                .addOcspClient(ocspClient1)
                .addOcspClient(ocspClient2)
                .addOcspClient(ocspClient3);

        mockCrlValidator.onCallDo(c -> {
                c.report.addReportItem(new ReportItem("1","2", ReportItem.ReportItemStatus.INDETERMINATE));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
        });
        mockOCSPValidator.onCallDo(c -> {
            c.report.addReportItem(new ReportItem("1","2", ReportItem.ReportItemStatus.INDETERMINATE));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        });
        validator.validate(report, baseContext, checkCert, checkDate);

        Assert.assertTrue (mockOCSPValidator.calls.get(0).timeStamp.before(mockOCSPValidator.calls.get(1).timeStamp));
        Assert.assertTrue (mockOCSPValidator.calls.get(1).timeStamp.before(mockCrlValidator.calls.get(0).timeStamp));
        Assert.assertTrue (mockCrlValidator.calls.get(0).timeStamp.before( mockCrlValidator.calls.get(1).timeStamp));
        Assert.assertTrue (mockCrlValidator.calls.get(1).timeStamp.before( mockOCSPValidator.calls.get(2).timeStamp));

        Assert.assertEquals(ocspClient1.getCalls().get(0).response, mockOCSPValidator.calls.get(2).ocspResp);
        Assert.assertEquals(ocspClient2.getCalls().get(0).response, mockOCSPValidator.calls.get(1).ocspResp);
        Assert.assertEquals(ocspClient3.getCalls().get(0).response, mockOCSPValidator.calls.get(0).ocspResp);
        Assert.assertEquals(crlClient.getCalls().get(0).responses.get(0), mockCrlValidator.calls.get(1).crl);
        Assert.assertEquals(crlClient.getCalls().get(0).responses.get(1), mockCrlValidator.calls.get(0).crl);
    }
}
