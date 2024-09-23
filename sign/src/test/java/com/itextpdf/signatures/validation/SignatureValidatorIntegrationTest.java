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
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.CertificateSources;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.TimeBasedContexts;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.context.ValidatorContexts;
import com.itextpdf.signatures.validation.mocks.MockRevocationDataValidator;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;
import com.itextpdf.test.ExtendedITextTest;

import java.util.List;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class SignatureValidatorIntegrationTest extends ExtendedITextTest {
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/validation/SignatureValidatorIntegrationTest/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/SignatureValidatorIntegrationTest/";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final char[] PASSWORD = "testpassphrase".toCharArray();
    private SignatureValidationProperties parameters;
    private IssuingCertificateRetriever certificateRetriever;
    private ValidatorChainBuilder builder;

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @BeforeEach
    public void setUp() {
        parameters = new SignatureValidationProperties();
        certificateRetriever = new IssuingCertificateRetriever();
        builder = new ValidatorChainBuilder()
                .withIssuingCertificateRetrieverFactory(()-> certificateRetriever)
                .withSignatureValidationProperties(parameters);
    }

    @Test
    public void validLatestSignatureTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDoc.pdf"))) {
            certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
            addRevDataClients();

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            report = signatureValidator.validateSignatures();
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasLogItem(al -> al
                        .withCertificate(rootCert)
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                i -> rootCert.getSubjectX500Principal()))
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.OCSP_RESPONDER_TRUSTED))
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.OCSP_RESPONDER_IS_CA))
        );
    }

    @Test
    public void shortValidityCertsWithOcspTest() throws GeneralSecurityException, IOException {
        String rootCertName = CERTS_SRC + "short_validity_root_cert.pem";
        String tsRootCertName = CERTS_SRC + "ts_root_cert.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertName)[0];
        X509Certificate tsRootCert = (X509Certificate) PemFileHelper.readFirstChain(tsRootCertName)[0];

        // We need to set infinite freshness for first timestamp validation. Otherwise, test will fail.
        builder.getProperties().setFreshness(ValidatorContexts.of(ValidatorContext.OCSP_VALIDATOR), CertificateSources.of(
                CertificateSource.TIMESTAMP), TimeBasedContexts.of(TimeBasedContext.PRESENT), Duration.ofDays(999999));

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "shortValidityCertsWithOcsp.pdf"))) {
            certificateRetriever.setTrustedCertificates(Arrays.asList(rootCert, tsRootCert));

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            report = signatureValidator.validateSignatures();
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasLogItem(al -> al
                        .withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                        .withMessage(DocumentRevisionsValidator.UNEXPECTED_ENTRY_IN_XREF, i -> 30))
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, i -> "timestampSig1"))
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, i -> "Signature1"))
                .hasLogItem(al -> al
                        .withCertificate(rootCert)
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED, i -> rootCert.getSubjectX500Principal()))
                .hasLogItems(2, al -> al
                        .withCertificate(tsRootCert)
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED, i -> tsRootCert.getSubjectX500Principal()))
        );
    }

    @Test
    public void shortValidityCertsWithCrlTest() throws GeneralSecurityException, IOException {
        String rootCertName = CERTS_SRC + "short_validity_root_cert.pem";
        String tsRootCertName = CERTS_SRC + "ts_root_cert.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertName)[0];
        X509Certificate tsRootCert = (X509Certificate) PemFileHelper.readFirstChain(tsRootCertName)[0];

        // We need to set infinite freshness for first timestamp validation. Otherwise, test will fail.
        builder.getProperties().setFreshness(ValidatorContexts.of(ValidatorContext.CRL_VALIDATOR), CertificateSources.of(
                CertificateSource.TIMESTAMP), TimeBasedContexts.of(TimeBasedContext.PRESENT), Duration.ofDays(999999));

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "shortValidityCertsWithCrl.pdf"))) {
            certificateRetriever.setTrustedCertificates(Arrays.asList(rootCert, tsRootCert));

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            report = signatureValidator.validateSignatures();
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasLogItem(al -> al
                        .withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                        .withMessage(DocumentRevisionsValidator.UNEXPECTED_ENTRY_IN_XREF, i -> 32))
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, i -> "timestampSig1"))
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, i -> "Signature1"))
                .hasLogItems(2, al -> al
                        .withCertificate(rootCert)
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED, i -> rootCert.getSubjectX500Principal()))
                .hasLogItems(4, al -> al
                        .withCertificate(tsRootCert)
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED, i -> tsRootCert.getSubjectX500Principal()))
        );
    }

    @Test
    public void validateMultipleDocumentsTest() throws GeneralSecurityException, IOException {
        String rootCertName = CERTS_SRC + "short_validity_root_cert.pem";
        String tsRootCertName = CERTS_SRC + "ts_root_cert.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertName)[0];
        X509Certificate tsRootCert = (X509Certificate) PemFileHelper.readFirstChain(tsRootCertName)[0];

        // We need to set infinite freshness for first timestamp validation. Otherwise, test will fail.
        builder.getProperties().setFreshness(ValidatorContexts.of(ValidatorContext.CRL_VALIDATOR), CertificateSources.of(
                CertificateSource.TIMESTAMP), TimeBasedContexts.of(TimeBasedContext.PRESENT), Duration.ofDays(999999));

        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "shortValidityCertsWithCrl.pdf"))) {
            certificateRetriever.setTrustedCertificates(Arrays.asList(rootCert, tsRootCert));

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            signatureValidator.validateSignatures();
        }
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "shortValidityCertsWithOcsp.pdf"))) {
            certificateRetriever.setTrustedCertificates(Arrays.asList(rootCert, tsRootCert));

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            signatureValidator.validateSignatures();
        }

        List<ICrlClient> crlClients = builder.getProperties().getCrlClients();
        List<IOcspClient> ocspClients = builder.getProperties().getOcspClients();
        Assertions.assertEquals(1, crlClients.size());
        Assertions.assertEquals(1, ocspClients.size());
        Assertions.assertTrue(crlClients.get(0) instanceof ValidationCrlClient);
        Assertions.assertTrue(ocspClients.get(0) instanceof ValidationOcspClient);
        ValidationCrlClient validationCrlClient = (ValidationCrlClient) crlClients.get(0);
        ValidationOcspClient validationOcspClient = (ValidationOcspClient) ocspClients.get(0);
        Assertions.assertEquals(2, validationCrlClient.getCrls().size());
        Assertions.assertEquals(2, validationOcspClient.getResponses().size());
    }

    @Test
    public void validateSingleSignatureTest1() throws Exception {
        String rootCertName = CERTS_SRC + "root_cert.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertName)[0];

        ValidationReport report1;
        ValidationReport report2;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validateSingleSignature1.pdf"))) {
            certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
            addRevDataClients();

            SignatureValidator signatureValidator1 = builder.buildSignatureValidator(document);
            report1 = signatureValidator1.validateSignature("Signature1");

            SignatureValidator signatureValidator2 = builder.buildSignatureValidator(document);
            report2 = signatureValidator2.validateSignature("Signature2");
        }

        // Signature1 set access permissions to level 3, Signature2 - to level 1, after that annotation was added.
        AssertValidationReport.assertThat(report1, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfLogs(4).hasNumberOfFailures(0)
                .hasLogItem(al -> al
                        .withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                        .withMessage(DocumentRevisionsValidator.UNEXPECTED_ENTRY_IN_XREF, i -> 17)
                        .withStatus(ReportItem.ReportItemStatus.INFO))
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "Signature1"))
                .hasLogItem(al -> al
                        .withCertificate(rootCert)
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                i -> rootCert.getSubjectX500Principal()))
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.OCSP_RESPONDER_IS_CA))
        );

        AssertValidationReport.assertThat(report2, a -> a
                .hasStatus(ValidationResult.INVALID)
                .hasNumberOfLogs(4).hasNumberOfFailures(1)
                .hasLogItem(al -> al
                        .withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                        .withMessage(DocumentRevisionsValidator.PAGE_ANNOTATIONS_MODIFIED)
                        .withStatus(ReportItem.ReportItemStatus.INVALID))
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "Signature2"))
                .hasLogItem(al -> al
                        .withCertificate(rootCert)
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                i -> rootCert.getSubjectX500Principal()))
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.OCSP_RESPONDER_IS_CA))
        );
    }

    @Test
    public void validateSingleSignatureTest2() throws Exception {
        String rootCertName = CERTS_SRC + "root_cert.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertName)[0];

        ValidationReport report1;
        ValidationReport report2;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validateSingleSignature2.pdf"))) {
            certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
            addRevDataClients();

            SignatureValidator signatureValidator1 = builder.buildSignatureValidator(document);
            report1 = signatureValidator1.validateSignature("Signature1");

            SignatureValidator signatureValidator2 = builder.buildSignatureValidator(document);
            report2 = signatureValidator2.validateSignature("Signature2");
        }

        // Signature1 set access permissions to level 1, after that annotation was added and then Signature2 applied.
        AssertValidationReport.assertThat(report1, a -> a
                .hasStatus(ValidationResult.INVALID)
                .hasNumberOfFailures(3)
                .hasLogItem(al -> al
                        .withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                        .withMessage(DocumentRevisionsValidator.PAGE_ANNOTATIONS_MODIFIED)
                        .withStatus(ReportItem.ReportItemStatus.INVALID))
                .hasLogItem(al -> al
                        .withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                        .withMessage(DocumentRevisionsValidator.UNEXPECTED_FORM_FIELD, p -> "Signature2")
                        .withStatus(ReportItem.ReportItemStatus.INVALID))
                .hasLogItem(al -> al
                        .withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                        .withMessage(DocumentRevisionsValidator.NOT_ALLOWED_ACROFORM_CHANGES)
                        .withStatus(ReportItem.ReportItemStatus.INVALID))
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "Signature1"))
        );

        AssertValidationReport.assertThat(report2, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "Signature2"))
        );
    }

    @Test
    public void retrieveRevocationDataFromTheSignatureContainerTest() throws GeneralSecurityException, IOException {
        String rootCertName = CERTS_SRC + "rootRsa.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertName)[0];

        // We need to set infinite freshness for the signature validation. Otherwise, test will fail.
        builder.getProperties().setFreshness(
                ValidatorContexts.of(ValidatorContext.OCSP_VALIDATOR, ValidatorContext.CRL_VALIDATOR),
                CertificateSources.of(CertificateSource.SIGNER_CERT),
                TimeBasedContexts.of(TimeBasedContext.PRESENT), Duration.ofDays(999999));

        ValidationReport report;
        // Signature container stores OCSP response with indeterminate status and less fresh but valid CRL response.
        try (PdfDocument document = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "revDataInTheSignatureContainer.pdf"))) {
            certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            report = signatureValidator.validateSignatures();
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfLogs(4).hasNumberOfFailures(0)
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, i -> "Signature1"))
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.CERT_STATUS_IS_UNKNOWN)
                        .withStatus(ReportItem.ReportItemStatus.INFO))
                .hasLogItems(2, al -> al
                        .withCertificate(rootCert)
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                i -> rootCert.getSubjectX500Principal()))
        );
    }

    @Test
    public void retrieveRevocationDataStoredInTheSignerInfoTest() throws GeneralSecurityException, IOException {
        String rootCertName = CERTS_SRC + "rootRsa.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertName)[0];

        // We need to set infinite freshness for the embedded timestamp validation. Otherwise, test will fail.
        builder.getProperties().setFreshness(
                        ValidatorContexts.of(ValidatorContext.OCSP_VALIDATOR, ValidatorContext.CRL_VALIDATOR),
                        CertificateSources.of(CertificateSource.TIMESTAMP),
                        TimeBasedContexts.of(TimeBasedContext.PRESENT), Duration.ofDays(999999))
                .setFreshness(
                        ValidatorContexts.of(ValidatorContext.CRL_VALIDATOR),
                        CertificateSources.of(CertificateSource.SIGNER_CERT),
                        TimeBasedContexts.of(TimeBasedContext.HISTORICAL), Duration.ofDays(2));

        ValidationReport report;
        // Signer info authenticated attributes store OCSP response with indeterminate status and valid CRL response.
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "revDataInTheSignerInfo.pdf"))) {
            certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            report = signatureValidator.validateSignatures();
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfLogs(6).hasNumberOfFailures(0)
                .hasLogItem(al -> al
                        .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                        .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, i -> "Signature1"))
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.CERT_STATUS_IS_UNKNOWN)
                        .withStatus(ReportItem.ReportItemStatus.INFO))
                .hasLogItems(4, al -> al
                        .withCertificate(rootCert)
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                i -> rootCert.getSubjectX500Principal()))
        );
    }

    @Test
    public void latestSignatureIsTimestampTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        String privateKeyName = CERTS_SRC + "rootCertKey.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];
        PrivateKey rootPrivateKey = PemFileHelper.readFirstKey(privateKeyName, PASSWORD);

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "timestampSignatureDoc.pdf"))) {
            certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

            TestOcspResponseBuilder ocspBuilder = new TestOcspResponseBuilder(rootCert, rootPrivateKey);
            Date currentDate = DateTimeUtil.getCurrentTimeDate();
            ocspBuilder.setProducedAt(currentDate);
            ocspBuilder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(currentDate, 3)));
            ocspBuilder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(currentDate, 30)));
            TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(rootCert, ocspBuilder);

            RevocationDataValidator revocationDataValidator = builder.buildRevocationDataValidator();
            revocationDataValidator.addOcspClient(ocspClient);
            builder.withRevocationDataValidatorFactory(() -> revocationDataValidator);
            parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                            TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                    .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                            Duration.ofDays(-2));

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);

            report = signatureValidator.validateLatestSignature(document);
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(3)
                .hasLogItem(la -> la
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                l -> rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert)
                ));
    }

    @Test
    public void certificatesNotInLatestSignatureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDocWithoutChain.pdf"))) {
            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
            parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                            TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                    .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                            Duration.ofDays(-2));

            report = signatureValidator.validateLatestSignature(document);
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasLogItem(al -> al
                        .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                        .withMessage(RevocationDataValidator.NO_REVOCATION_DATA)
                        .withCertificate(signingCert)
                        .withStatus(ReportItem.ReportItemStatus.INDETERMINATE))
                .hasLogItem(al -> al
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.ISSUER_MISSING,
                                i -> signingCert.getSubjectX500Principal())
                        .withStatus(ReportItem.ReportItemStatus.INDETERMINATE)
                        .withCertificate(signingCert))
        );
    }

    @Test
    public void certificatesNotInLatestSignatureButSetAsKnownTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDocWithoutChain.pdf"))) {
            certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
            certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
            addRevDataClients();

            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            report = signatureValidator.validateLatestSignature(document);
        }
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasLogItem(al -> al
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                i -> rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert))
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.OCSP_RESPONDER_TRUSTED))
                .hasLogItem(al -> al
                        .withCheckName(OCSPValidator.OCSP_CHECK)
                        .withMessage(OCSPValidator.OCSP_RESPONDER_IS_CA))
        );
    }

    @Test
    public void rootIsNotTrustedInLatestSignatureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDoc.pdf"))) {
            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                            TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH)
                    .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                            Duration.ofDays(-2));

            report = signatureValidator.validateLatestSignature(document);
        }

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(3)
                .hasLogItem(al -> al
                        .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                        .withMessage(RevocationDataValidator.NO_REVOCATION_DATA)
                        .withCertificate((X509Certificate) certificateChain[0]))
                .hasLogItem(al -> al
                        .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                        .withMessage(RevocationDataValidator.NO_REVOCATION_DATA)
                        .withCertificate((X509Certificate) certificateChain[1]))
                .hasLogItem(al -> al
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.ISSUER_MISSING,
                                i -> rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert))
        );
    }

    @Test
    public void validateMultipleSignaturesUsingLastKnownPoETest() throws Exception {
        String trustedCertsFileName = CERTS_SRC + "trustedCerts.pem";
        Certificate[] trustedCerts = PemFileHelper.readFirstChain(trustedCertsFileName);

        try (PdfDocument document = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "signatureSigningCertExpired.pdf"))) {
            SignatureValidator signatureValidator = new ValidatorChainBuilder()
                    .withTrustedCertificates(Arrays.asList(trustedCerts))
                    .withRevocationDataValidatorFactory(()->
                            new MockRevocationDataValidator()).buildSignatureValidator(document);
            ValidationReport report = signatureValidator.validateSignatures();

            AssertValidationReport.assertThat(report, r -> r
                    .hasStatus(ValidationResult.VALID)
                    .hasNumberOfLogs(5).hasNumberOfFailures(0)
                    .hasLogItem(l -> l
                            .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                            .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "timestampSig1"))
                    .hasLogItem(l -> l
                            .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                            .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "Signature1"))
                    // Document contains unused unexpected entry.
                    .hasLogItem(l -> l
                            .withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.UNEXPECTED_ENTRY_IN_XREF, p -> "28")
                            .withStatus(ReportItem.ReportItemStatus.INFO))
            );
        }
    }

    @Test
    public void stopAfterTimestampChainValidationFailureTest() throws Exception {
        try (PdfDocument document = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "validDocWithTimestamp.pdf"))) {
            SignatureValidator signatureValidator = new ValidatorChainBuilder()
                    .withSignatureValidationProperties(new SignatureValidationProperties()
                            .setContinueAfterFailure(ValidatorContexts.all(), CertificateSources.all(), false))
                    .withRevocationDataValidatorFactory(()->
                            new MockRevocationDataValidator()).buildSignatureValidator(document);
            ValidationReport report = signatureValidator.validateSignatures();

            AssertValidationReport.assertThat(report, r -> r
                    .hasStatus(ValidationResult.INDETERMINATE)
                    .hasNumberOfLogs(3).hasNumberOfFailures(2)
                    .hasLogItem(l -> l
                            .withCheckName(SignatureValidator.SIGNATURE_VERIFICATION)
                            .withMessage(SignatureValidator.VALIDATING_SIGNATURE_NAME, p -> "Signature1"))
                    .hasLogItems(2, l -> l
                            .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                            .withStatus(ReportItem.ReportItemStatus.INDETERMINATE))
            );
        }
    }

    private void addRevDataClients()
            throws AbstractOperatorCreationException, IOException, AbstractPKCSException, CertificateException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        String privateKeyName = CERTS_SRC + "rootCertKey.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];
        PrivateKey rootPrivateKey = PemFileHelper.readFirstKey(privateKeyName, PASSWORD);

        Date currentDate = DateTimeUtil.getCurrentTimeDate();
        TestOcspResponseBuilder builder1 = new TestOcspResponseBuilder(rootCert, rootPrivateKey);
        builder1.setProducedAt(currentDate);
        builder1.setThisUpdate(DateTimeUtil.getCalendar(currentDate));
        builder1.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(currentDate, 30)));
        TestOcspResponseBuilder builder2 = new TestOcspResponseBuilder(rootCert, rootPrivateKey);
        builder2.setProducedAt(currentDate);
        builder2.setThisUpdate(DateTimeUtil.getCalendar(currentDate));
        builder2.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(currentDate, 30)));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(rootCert, builder1)
                .addBuilderForCertIssuer(intermediateCert, builder2);
        RevocationDataValidator revocationDataValidator = builder.getRevocationDataValidator()
                .addOcspClient(ocspClient);
        builder.withRevocationDataValidatorFactory(() -> revocationDataValidator);
        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH);
    }
}
