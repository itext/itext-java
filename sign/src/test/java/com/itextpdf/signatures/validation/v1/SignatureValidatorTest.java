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
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.validation.v1.context.CertificateSource;
import com.itextpdf.signatures.validation.v1.context.CertificateSources;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContext;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContexts;
import com.itextpdf.signatures.validation.v1.context.ValidatorContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContexts;
import com.itextpdf.signatures.validation.v1.mocks.MockChainValidator;
import com.itextpdf.signatures.validation.v1.mocks.MockChainValidator.ValidationCallBack;
import com.itextpdf.signatures.validation.v1.mocks.MockDocumentRevisionsValidator;
import com.itextpdf.signatures.validation.v1.mocks.MockIssuingCertificateRetriever;
import com.itextpdf.signatures.validation.v1.mocks.MockRevocationDataValidator;
import com.itextpdf.signatures.validation.v1.report.ReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;
import com.itextpdf.signatures.validation.v1.report.ValidationReport.ValidationResult;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.util.List;

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
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Category(BouncyCastleUnitTest.class)
public class SignatureValidatorTest extends ExtendedITextTest {
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/validation/v1/SignatureValidatorTest/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/v1/SignatureValidatorTest/";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final char[] PASSWORD = "testpassphrase".toCharArray();
    private SignatureValidationProperties parameters;
    private MockIssuingCertificateRetriever mockCertificateRetriever;

    private ValidatorChainBuilder builder;
    private MockChainValidator mockCertificateChainValidator;
    private MockDocumentRevisionsValidator mockDocumentRevisionsValidator;

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @Before
    public void setUp() {
        mockCertificateChainValidator = new MockChainValidator();
        parameters = new SignatureValidationProperties();
        mockCertificateRetriever = new MockIssuingCertificateRetriever();
        mockDocumentRevisionsValidator = new MockDocumentRevisionsValidator();
        builder = new ValidatorChainBuilder()
                .withIssuingCertificateRetriever(mockCertificateRetriever)
                .withSignatureValidationProperties(parameters)
                .withCertificateChainValidator(mockCertificateChainValidator)
                .withRevocationDataValidator(new MockRevocationDataValidator())
                .withDocumentRevisionsValidator(mockDocumentRevisionsValidator);
    }

    @Test
    public void latestSignatureIsTimestampTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        String privateKeyName = CERTS_SRC + "rootCertKey.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];
        PrivateKey rootPrivateKey = PemFileHelper.readFirstKey(privateKeyName, PASSWORD);

        X509Certificate timeStampCert = (X509Certificate) PemFileHelper.readFirstChain(
                CERTS_SRC + "timestamp.pem")[0];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "timestampSignatureDoc.pdf"))) {
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

            TestOcspResponseBuilder ocspBuilder = new TestOcspResponseBuilder(rootCert, rootPrivateKey);
            Date currentDate = DateTimeUtil.getCurrentTimeDate();
            ocspBuilder.setProducedAt(currentDate);
            ocspBuilder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(currentDate, 3)));
            ocspBuilder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(currentDate, 30)));
            TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(rootCert, ocspBuilder);
            builder.getRevocationDataValidator().addOcspClient(ocspClient);
            parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                            TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                    .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                            Duration.ofDays(-2));

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            report = signatureValidator.validateLatestSignature(document);
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfLogs(1).hasNumberOfFailures(0));

        Assert.assertEquals(1, mockCertificateChainValidator.verificationCalls.size());
        MockChainValidator.ValidationCallBack call = mockCertificateChainValidator.verificationCalls.get(0);
        Assert.assertEquals(CertificateSource.TIMESTAMP, call.context.getCertificateSource());
        Assert.assertEquals(ValidatorContext.SIGNATURE_VALIDATOR, call.context.getValidatorContext());
        Assert.assertEquals(timeStampCert.getSubjectX500Principal(), call.certificate.getSubjectX500Principal());
    }

    @Test
    public void latestSignatureIsDocTimestampWithModifiedDateTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        String privateKeyName = CERTS_SRC + "rootCertKey.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];
        PrivateKey rootPrivateKey = PemFileHelper.readFirstKey(privateKeyName, PASSWORD);

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "modifiedDocTimestampDate.pdf"))) {
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

            TestOcspResponseBuilder ocspBuilder = new TestOcspResponseBuilder(rootCert, rootPrivateKey);
            Date currentDate = DateTimeUtil.getCurrentTimeDate();
            ocspBuilder.setProducedAt(currentDate);
            ocspBuilder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(currentDate, 3)));
            ocspBuilder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(currentDate, 30)));
            TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(rootCert, ocspBuilder);
            builder.getRevocationDataValidator().addOcspClient(ocspClient);
            parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                            TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                    .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                            Duration.ofDays(-2));

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            report = signatureValidator.validateLatestSignature(document);
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfLogs(2).hasNumberOfFailures(1)
                .hasStatus(ValidationResult.INVALID)
                .hasLogItem(l -> l
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "timestampSignature1"))
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(MessageFormatUtil.format(SignatureValidator.CANNOT_VERIFY_SIGNATURE,
                                "timestampSignature1"))
                        .withStatus(ReportItem.ReportItemStatus.INVALID))
        );
    }

    @Test
    public void latestSignatureWithModifiedTimestampDateTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        String privateKeyName = CERTS_SRC + "rootCertKey.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];
        PrivateKey rootPrivateKey = PemFileHelper.readFirstKey(privateKeyName, PASSWORD);

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "signatureWithModifiedTimestampDate.pdf"))) {
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

            TestOcspResponseBuilder ocspBuilder = new TestOcspResponseBuilder(rootCert, rootPrivateKey);
            Date currentDate = DateTimeUtil.getCurrentTimeDate();
            ocspBuilder.setProducedAt(currentDate);
            ocspBuilder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(currentDate, 3)));
            ocspBuilder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(currentDate, 30)));
            TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(rootCert, ocspBuilder);
            builder.getRevocationDataValidator().addOcspClient(ocspClient);
            parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                            TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                    .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                            Duration.ofDays(-2))
                    .setContinueAfterFailure(ValidatorContexts.all(), CertificateSources.all(), false);

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            report = signatureValidator.validateLatestSignature(document);
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfLogs(2).hasNumberOfFailures(1)
                .hasStatus(ValidationResult.INVALID)
                .hasLogItem(l -> l
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "Signature1"))
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.TIMESTAMP_VERIFICATION)
                        .withMessage(SignatureValidator.CANNOT_VERIFY_TIMESTAMP)
                        .withStatus(ReportItem.ReportItemStatus.INVALID))
        );
    }

    @Test
    public void latestSignatureWithBrokenTimestampTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "docWithBrokenTimestamp.pdf"))) {
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            report = signatureValidator.validateLatestSignature(document);
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INVALID)
                .hasLogItems(2, 2, al -> al
                        .withCheckName(SignatureValidator.TIMESTAMP_VERIFICATION)
                        .withMessage(SignatureValidator.CANNOT_VERIFY_TIMESTAMP)
                        .withStatus(ReportItem.ReportItemStatus.INVALID))
        );
    }

    @Test
    public void documentModifiedLatestSignatureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "modifiedDoc.pdf"))) {
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            report = signatureValidator.validateLatestSignature(document);
        }
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INVALID)
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.DOCUMENT_IS_NOT_COVERED, i -> "Signature1"))
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.CANNOT_VERIFY_SIGNATURE, i -> "Signature1"))

        );
    }

    @Test
    public void latestSignatureInvalidStopValidationTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;

        parameters.setContinueAfterFailure(ValidatorContexts.all(), CertificateSources.all(), false);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "modifiedDoc.pdf"))) {
            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

            report = signatureValidator.validateLatestSignature(document);
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INVALID)
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.DOCUMENT_IS_NOT_COVERED, i -> "Signature1")
                        .withStatus(ReportItem.ReportItemStatus.INVALID))
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.CANNOT_VERIFY_SIGNATURE, i -> "Signature1")
                        .withStatus(ReportItem.ReportItemStatus.INVALID))
        );
        // check that no requests are made after failure
        Assert.assertEquals(0, mockCertificateChainValidator.verificationCalls.size());
    }

    @Test
    public void certificatesNotInLatestSignatureButTakenFromDSSTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate signCert = (X509Certificate) certificateChain[0];

        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "docWithDss.pdf"))) {
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));


            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            signatureValidator.validateLatestSignature(document);
        }

        Assert.assertEquals(2, mockCertificateRetriever.addKnownCertificatesCalls.size());
        Collection<Certificate> dssCall = mockCertificateRetriever.addKnownCertificatesCalls.get(0);
        Assert.assertEquals(3, dssCall.size());
        Assert.assertEquals(1, dssCall.stream().filter(c -> ((X509Certificate) c).equals(rootCert)).count());
        Assert.assertEquals(1, dssCall.stream().filter(c -> ((X509Certificate) c).equals(intermediateCert)).count());
        Assert.assertEquals(1, dssCall.stream().filter(c -> ((X509Certificate) c).equals(signCert)).count());

    }

    @Test
    public void certificatesNotInLatestSignatureButTakenFromDSSOneCertIsBrokenTest() throws GeneralSecurityException,
            IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "docWithBrokenDss.pdf"))) {
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            report = signatureValidator.validateLatestSignature(document);
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withExceptionCauseType(GeneralSecurityException.class))
        );
    }

    @Test
    public void indeterminateChainValidationLeadsToIndeterminateResultTest() throws IOException {
        mockCertificateChainValidator.onCallDo(c -> c.report.addReportItem(
                new ReportItem("test", "test", ReportItem.ReportItemStatus.INDETERMINATE)));

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDoc.pdf"))) {
            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);

            report = signatureValidator.validateLatestSignature(document);
        }


        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasLogItem(al -> al
                        .withCheckName("test")
                        .withMessage("test"))
        );
    }

    @Test
    public void invalidChainValidationLeadsToInvalidResultTest() throws IOException {
        mockCertificateChainValidator.onCallDo(c -> c.report.addReportItem(
                new ReportItem("test", "test", ReportItem.ReportItemStatus.INVALID)));

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDoc.pdf"))) {
            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);

            report = signatureValidator.validateLatestSignature(document);
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INVALID)
                .hasNumberOfFailures(1)
                .hasLogItem(al -> al
                        .withCheckName("test")
                        .withMessage("test"))
        );
    }

    @Test
    public void invalidRevisionsValidationLeadsToInvalidResultTest() throws IOException {
        mockDocumentRevisionsValidator.setReportItemStatus(ReportItemStatus.INVALID);

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDoc.pdf"))) {
            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);

            report = signatureValidator.validateSignatures();
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INVALID)
                .hasNumberOfFailures(1)
                .hasLogItem(al -> al
                        .withCheckName("test")
                        .withMessage("test"))
        );
    }

    @Test
    public void validateMultipleSignatures() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "docWithMultipleSignaturesAndTimeStamp.pdf"))) {
            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            ValidationReport report = signatureValidator.validateSignatures();

            AssertValidationReport.assertThat(report, r -> r
                    .hasStatus(ValidationResult.VALID)
                    .hasNumberOfLogs(5).hasNumberOfFailures(0)
                    .hasLogItem(l -> l
                            .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                            .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "Signature1"))
                    .hasLogItem(l -> l
                            .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                            .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "Signature2"))
                    .hasLogItem(l -> l
                            .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                            .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "Signature3"))
                    .hasLogItem(l -> l
                            .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                            .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "signer1"))
                    .hasLogItem(l -> l
                            .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                            .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "signer2"))
            );

            Date date1 = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 1);
            Date date2 = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 10);
            Date date3 = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 20);

            // 2 signatures with timestamp
            // 3 document timestamps
            List<ValidationCallBack> verificationCalls = mockCertificateChainValidator.verificationCalls;
            Assert.assertEquals(7, verificationCalls.size());
            Assert.assertEquals(TimeBasedContext.PRESENT, verificationCalls.get(0).context.getTimeBasedContext());
            for (int i = 1; i < verificationCalls.size(); ++i) {
                Assert.assertEquals(TimeBasedContext.HISTORICAL, verificationCalls.get(i).context.getTimeBasedContext());
            }
            Assert.assertTrue(verificationCalls.stream().anyMatch(c ->
                    c.certificate.getSerialNumber().toString().equals("1491571297")
                            && c.checkDate.equals(date3)));
            Assert.assertTrue(verificationCalls.stream().anyMatch(c ->
                    c.certificate.getSerialNumber().toString().equals("1491571297")
                            && c.checkDate.equals(date2)));
            Assert.assertTrue(verificationCalls.stream().anyMatch(c ->
                    c.certificate.getSerialNumber().toString().equals("1491571297")
                            && c.checkDate.equals(date1)));
            Assert.assertTrue(verificationCalls.stream().anyMatch(c ->
                    c.certificate.getSerialNumber().toString().equals("1550593058")
                            && c.checkDate.equals(date2)));
            Assert.assertTrue(verificationCalls.stream().anyMatch(c ->
                    c.certificate.getSerialNumber().toString().equals("1701704311986")
                            && c.checkDate.equals(date1)));
        }
    }

    @Test
    public void signatureChainValidatorFailureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDoc.pdf"))) {
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
            mockCertificateChainValidator.onCallDo(c -> {
                throw new RuntimeException("Test chain validation failure");
            });

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            ValidationReport report = signatureValidator.validateLatestSignature(document);
            AssertValidationReport.assertThat(report, r ->
                    r.hasLogItem(l -> l
                            .withMessage(SignatureValidator.CHAIN_VALIDATION_FAILED)));
        }
    }

    @Test
    public void timeStampChainValidatorFailureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "timestampSignatureDoc.pdf"))) {
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
            mockCertificateChainValidator.onCallDo(c -> {
                throw new RuntimeException("Test chain validation failure");
            });

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            ValidationReport report = signatureValidator.validateLatestSignature(document);
            AssertValidationReport.assertThat(report, r -> r.hasLogItem(l -> l
                    .withMessage(SignatureValidator.CHAIN_VALIDATION_FAILED)));
        }
    }

    @Test
    public void certificateRetrieverAddKnownCertificatesFromDSSFailureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "docWithDss.pdf"))) {
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

            mockCertificateRetriever.onAddKnownCertificatesDo(c -> {
                throw new RuntimeException("Test add know certificates failure");
            });

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            ValidationReport report = signatureValidator.validateLatestSignature(document);
            AssertValidationReport.assertThat(report, r -> r
                    .hasLogItems(1, Integer.MAX_VALUE, l ->
                            l.withMessage(SignatureValidator.ADD_KNOWN_CERTIFICATES_FAILED)));
        }
    }

    @Test
    public void certificateRetrieverAddKnownCertificatesFromSignatureFailureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDoc.pdf"))) {
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
            mockCertificateRetriever.onAddKnownCertificatesDo(c -> {
                throw new RuntimeException("Test add know certificates failure");
            });

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            ValidationReport report = signatureValidator.validateLatestSignature(document);
            AssertValidationReport.assertThat(report, r -> r
                    .hasLogItems(1, Integer.MAX_VALUE, l ->
                            l.withMessage(SignatureValidator.ADD_KNOWN_CERTIFICATES_FAILED)));
        }
    }

    @Test
    public void certificateRetrieverAddKnownCertificatesFromTimestampFailureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "timestampSignatureDoc.pdf"))) {
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
            mockCertificateRetriever.onAddKnownCertificatesDo(c -> {
                throw new RuntimeException("Test add know certificates failure");
            });

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            ValidationReport report = signatureValidator.validateLatestSignature(document);
            AssertValidationReport.assertThat(report, r -> r
                    .hasLogItems(1, Integer.MAX_VALUE, l ->
                            l.withMessage(SignatureValidator.ADD_KNOWN_CERTIFICATES_FAILED)));
        }
    }

    @Test
    public void documentRevisionValidatorFailureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDoc.pdf"))) {
            mockCertificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
            mockDocumentRevisionsValidator.onCallDo(c -> {
                throw new RuntimeException("Test add know certificates failure");
            });


            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            ValidationReport report = signatureValidator.validateSignatures();
            AssertValidationReport.assertThat(report, r -> r
                    .hasLogItem(l -> l.withMessage(SignatureValidator.REVISIONS_VALIDATION_FAILED)));
        }
    }

    @Test
    public void throwExceptionOnTheSecondValidationAttempt() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "timestampSignatureDoc.pdf"))) {
            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            signatureValidator.validateSignatures();
            Exception exception = Assert.assertThrows(PdfException.class,
                    () -> signatureValidator.validateSignatures());
            Assert.assertEquals(SignatureValidator.VALIDATION_PERFORMED, exception.getMessage());
            exception = Assert.assertThrows(PdfException.class,
                    () -> signatureValidator.validateSignature("Signature1"));
            Assert.assertEquals(SignatureValidator.VALIDATION_PERFORMED, exception.getMessage());
        }
    }

    @Test
    public void signatureWithSpecifiedNameNotFound() throws IOException {
        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "timestampSignatureDoc.pdf"))) {
            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            report = signatureValidator.validateSignature("Invalid signature name");
        }
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasNumberOfLogs(1).hasNumberOfFailures(1)
                .hasLogItem(l -> l
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.SIGNATURE_NOT_FOUND, p -> "Invalid signature name")
                        .withStatus(ReportItemStatus.INDETERMINATE)));
    }
}
