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

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.OID.X509Extensions;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.validation.v1.context.CertificateSource;
import com.itextpdf.signatures.validation.v1.context.CertificateSources;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContext;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContexts;
import com.itextpdf.signatures.validation.v1.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.v1.extensions.KeyUsage;
import com.itextpdf.signatures.validation.v1.extensions.KeyUsageExtension;
import com.itextpdf.signatures.validation.v1.report.CertificateReportItem;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;
import com.itextpdf.signatures.validation.v1.report.ValidationReport.ValidationResult;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Collections;

import java.util.Date;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleUnitTest.class)
public class CertificateChainValidatorTest extends ExtendedITextTest {
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/validation/v1/CertificateChainValidatorTest/";

    private ValidatorChainBuilder validatorChainBuilder;
    private SignatureValidationProperties properties;
    private IssuingCertificateRetriever certificateRetriever;
    private final ValidationContext baseContext = new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
            CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);

    @Before
    public void setup() {
        properties = new SignatureValidationProperties();
        certificateRetriever = new IssuingCertificateRetriever();
        validatorChainBuilder = new ValidatorChainBuilder()
                .withIssuingCertificateRetriever(certificateRetriever)
                .withSignatureValidationProperties(properties);
        validatorChainBuilder.withRevocationDataValidator(new MockRevocationDataValidator(validatorChainBuilder));
    }

    @Test
    public void validChainTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.<Certificate>singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.<Certificate>singletonList(rootCert));

        ValidationReport report =
                validator.validateCertificate(baseContext, signingCert, TimeTestUtil.TEST_DATE_TIME);
        new AssertValidationReport(report)
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                        && l.getMessage().equals(MessageFormatUtil.format(
                                CertificateChainValidator.CERTIFICATE_TRUSTED,
                                rootCert.getSubjectX500Principal()))
                        && ((CertificateReportItem) l).getCertificate().equals(rootCert),
                        CertificateChainValidator.CERTIFICATE_TRUSTED)
                .doAssert();
    }

    @Test
    public void severalFailuresWithProceedAfterFailTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "invalidCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

        properties.setContinueAfterFailure(ValidatorContexts.all() , CertificateSources.all(),true);
        // Set random extension as a required one to force the test to fail.
        properties.setRequiredExtensions(CertificateSources.of(CertificateSource.CERT_ISSUER),
                Collections.<CertificateExtension>singletonList(new KeyUsageExtension(KeyUsage.DECIPHER_ONLY)));

        ValidationReport report = validator.validateCertificate(baseContext, signingCert, DateTimeUtil.getCurrentTimeDate());

        Assert.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(3, report.getFailures().size());
        Assert.assertEquals(4, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));
        Assert.assertEquals(report.getFailures().get(1), report.getLogs().get(1));
        Assert.assertEquals(report.getFailures().get(2), report.getLogs().get(2));

        CertificateReportItem failure1 = report.getCertificateFailures().get(0);
        Assert.assertEquals(signingCert, failure1.getCertificate());
        Assert.assertEquals("Required certificate extensions check.", failure1.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Required extension {0} is missing or incorrect.", X509Extensions.KEY_USAGE), failure1.getMessage());

        CertificateReportItem failure2 = report.getCertificateFailures().get(1);
        Assert.assertEquals(intermediateCert, failure2.getCertificate());
        Assert.assertEquals("Required certificate extensions check.", failure2.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Required extension {0} is missing or incorrect.", X509Extensions.KEY_USAGE), failure2.getMessage());

        CertificateReportItem failure3 = report.getCertificateFailures().get(2);
        Assert.assertEquals(rootCert, failure3.getCertificate());
        Assert.assertEquals("Required certificate extensions check.", failure3.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Required extension {0} is missing or incorrect.", X509Extensions.KEY_USAGE), failure3.getMessage());
    }

    @Test
    public void severalFailuresWithoutProceedAfterFailTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "invalidCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

        properties.setContinueAfterFailure(ValidatorContexts.all() , CertificateSources.all(),false);
        // Set random extension as a required one to force the test to fail.
        properties.setRequiredExtensions(CertificateSources.of(CertificateSource.CERT_ISSUER),
                Collections.<CertificateExtension>singletonList(new KeyUsageExtension(KeyUsage.DECIPHER_ONLY)));

        ValidationReport report = validator.validateCertificate(baseContext, signingCert, DateTimeUtil.getCurrentTimeDate());

        Assert.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));

        CertificateReportItem failure1 = report.getCertificateFailures().get(0);
        Assert.assertEquals(signingCert, failure1.getCertificate());
        Assert.assertEquals("Required certificate extensions check.", failure1.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Required extension {0} is missing or incorrect.", X509Extensions.KEY_USAGE), failure1.getMessage());
    }

    @Test
    public void intermediateCertTrustedTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.setTrustedCertificates(Collections.singletonList(intermediateCert));

        ValidationReport report = validator.validateCertificate(baseContext, signingCert, DateTimeUtil.getCurrentTimeDate());

        new AssertValidationReport(report)
                        .hasNumberOfFailures(0)
                        .hasNumberOfLogs(1)
                        .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                                && l.getMessage().equals(MessageFormatUtil.format(
                                CertificateChainValidator.CERTIFICATE_TRUSTED,
                                intermediateCert.getSubjectX500Principal())),
                                CertificateChainValidator.CERTIFICATE_TRUSTED)
                        .doAssert();
    }

    @Test
    public void validChainRequiredExtensionPositiveTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

        ValidationReport report = validator.validateCertificate(baseContext, signingCert, DateTimeUtil.getCurrentTimeDate());

        new AssertValidationReport(report)
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                        && l.getMessage().equals(MessageFormatUtil.format(
                        CertificateChainValidator.CERTIFICATE_TRUSTED,
                        rootCert.getSubjectX500Principal()))
                        && ((CertificateReportItem) l).getCertificate().equals(rootCert),
                        CertificateChainValidator.CERTIFICATE_TRUSTED)
                .doAssert();
    }

    @Test
    public void validChainRequiredExtensionNegativeTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

        ValidationReport report = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.CERT_ISSUER),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        new AssertValidationReport(report)
                .hasNumberOfFailures(2)
                .hasNumberOfLogs(3)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                                && l.getMessage().equals(MessageFormatUtil.format(
                                CertificateChainValidator.CERTIFICATE_TRUSTED,
                                rootCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(rootCert),
                                CertificateChainValidator.CERTIFICATE_TRUSTED)
                .hasLogItem(l -> l.getCheckName().equals("Required certificate extensions check.")
                                && l.getMessage().equals(MessageFormatUtil.format(
                                        "Required extension {0} is missing or incorrect.",
                                        X509Extensions.KEY_USAGE))
                                        && ((CertificateReportItem) l).getCertificate().equals(signingCert),
                        "Required extension {0} is missing or incorrect.")
                .hasLogItem(l -> l.getCheckName().equals("Required certificate extensions check.")
                                && l.getMessage().equals(MessageFormatUtil.format(
                                "Required extension {0} is missing or incorrect.",
                                X509Extensions.BASIC_CONSTRAINTS))
                                && ((CertificateReportItem) l).getCertificate().equals(signingCert),
                        "Required extension {0} is missing or incorrect.")
                .doAssert();

    }

    @Test
    public void validChainTrustedRootIsnSetTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));

        ValidationReport report = validator.validateCertificate(baseContext, signingCert, DateTimeUtil.getCurrentTimeDate());

        new AssertValidationReport(report)
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                        && l.getMessage().equals(MessageFormatUtil.format(
                                CertificateChainValidator.ISSUER_MISSING
                                        ,intermediateCert.getSubjectX500Principal()))
                        && ((CertificateReportItem) l).getCertificate().equals( intermediateCert),
        CertificateChainValidator.ISSUER_MISSING)
                .doAssert();
    }

    @Test
    public void intermediateCertIsNotYetValidTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        String intermediateCertName = CERTS_SRC + "not-yet-valid-intermediate.cert.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) PemFileHelper.readFirstChain(intermediateCertName)[0];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

        ValidationReport report = validator.validateCertificate(baseContext, signingCert, TimeTestUtil.TEST_DATE_TIME);


        new AssertValidationReport(report)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(2)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                        && l.getMessage().equals(MessageFormatUtil.format(
                                CertificateChainValidator.CERTIFICATE_TRUSTED,
                                rootCert.getSubjectX500Principal()))
                        && ((CertificateReportItem) l).getCertificate().equals(rootCert),
                        CertificateChainValidator.CERTIFICATE_TRUSTED)
                .hasLogItem(l -> l.getCheckName().equals("Certificate validity period check.")
                                && l.getMessage().equals(MessageFormatUtil.format(
                                "Certificate {0} is not yet valid.", intermediateCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(intermediateCert)
                                && l.getExceptionCause() instanceof CertificateNotYetValidException,
                        "Certificate {0} is not yet valid.")
                .doAssert();
    }

    @Test
    public void intermediateCertIsExpiredTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        String intermediateCertName = CERTS_SRC + "expired-intermediate.cert.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) PemFileHelper.readFirstChain(intermediateCertName)[0];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));

        ValidationReport report = validator.validateCertificate(baseContext, signingCert, DateTimeUtil.getCurrentTimeDate());

        new AssertValidationReport(report)
                .hasStatus(ValidationResult.INVALID)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(2)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                                && l.getMessage().equals(MessageFormatUtil.format
                                        (CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        rootCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(rootCert), CertificateChainValidator.ISSUER_MISSING)
                .hasLogItem(l -> l.getCheckName().equals("Certificate validity period check.")
                                && l.getMessage().equals(MessageFormatUtil.format
                                ("Certificate {0} is expired.", intermediateCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(intermediateCert)
                                && l.getExceptionCause() instanceof CertificateExpiredException,
                        CertificateChainValidator.ISSUER_MISSING)
                .doAssert();
    }

    @Test
    public void certificateGenerallyTrustedTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
        certificateRetriever.getTrustedCertificatesStore().addGenerallyTrustedCertificates(Collections.singletonList(rootCert));

        // Remove required extensions to make test pass.
        properties.setRequiredExtensions(CertificateSources.all(), Collections.<CertificateExtension>emptyList());

        ValidationReport report1 = validator.validateCertificate(baseContext, signingCert, DateTimeUtil.getCurrentTimeDate());

        new AssertValidationReport(report1)
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                                && l.getMessage().equals(MessageFormatUtil.format
                                (CertificateChainValidator.CERTIFICATE_TRUSTED, rootCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(rootCert),
                        "Certificate {0} is trusted.")
                .doAssert();

        ValidationReport report2 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.OCSP_ISSUER),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        new AssertValidationReport(report2)
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                                && l.getMessage().equals(MessageFormatUtil.format
                                (CertificateChainValidator.CERTIFICATE_TRUSTED, rootCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(rootCert),
                        "Certificate {0} is trusted.")
                .doAssert();

        ValidationReport report3 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.TIMESTAMP),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        new AssertValidationReport(report3)
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                                && l.getMessage().equals(MessageFormatUtil.format
                                (CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        rootCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(rootCert),
                        "Certificate {0} is trusted.")
                .doAssert();
    }

    @Test
    public void rootCertificateTrustedForCATest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
        certificateRetriever.getTrustedCertificatesStore().addCATrustedCertificates(Collections.singletonList(rootCert));

        // Remove required extensions to make test pass.
        properties.setRequiredExtensions(CertificateSources.all(), Collections.<CertificateExtension>emptyList());

        ValidationReport report1 = validator.validateCertificate(baseContext, signingCert, DateTimeUtil.getCurrentTimeDate());

        new AssertValidationReport(report1)
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                                && l.getMessage().equals(MessageFormatUtil.format
                                (CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        rootCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(rootCert),
                        "Certificate {0} is trusted.")
                .doAssert();

        ValidationReport report2 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.OCSP_ISSUER),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        new AssertValidationReport(report2)
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                                && l.getMessage().equals(MessageFormatUtil.format
                                (CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        rootCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(rootCert),
                        "Certificate {0} is trusted.")
                .doAssert();

        ValidationReport report3 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.TIMESTAMP),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        new AssertValidationReport(report3)
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                                && l.getMessage().equals(MessageFormatUtil.format
                                (CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        rootCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(rootCert),
                        "Certificate {0} is trusted.")
                .doAssert();
    }

    @Test
    public void firstCertificateTrustedForCATest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
        certificateRetriever.getTrustedCertificatesStore().addCATrustedCertificates(Collections.singletonList(signingCert));

        // Remove required extensions to make test pass.
        properties.setRequiredExtensions(CertificateSources.all(), Collections.<CertificateExtension>emptyList());

        ValidationReport report1 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.CERT_ISSUER),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        // This works fine because certificate in question has CertificateSource.CERT_ISSUER context.
        new AssertValidationReport(report1)
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                                && l.getMessage().equals(MessageFormatUtil.format
                                (CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        signingCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(signingCert),
                        "Certificate {0} is trusted.")
                .doAssert();

        ValidationReport report2 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.TIMESTAMP),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        // This doesn't work because certificate in question has CertificateSource.TIMESTAMP context.
        new AssertValidationReport(report2)
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(2)
                .hasLogItem(l -> l.getMessage().equals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                                signingCert.getSubjectX500Principal(), "certificates generation")),
                        CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT)
                .hasLogItem(l -> l.getMessage().equals(MessageFormatUtil.format(CertificateChainValidator.ISSUER_MISSING,
                        intermediateCert.getSubjectX500Principal())), CertificateChainValidator.ISSUER_MISSING)
                .doAssert();
    }

    @Test
    public void rootCertificateTrustedForOCSPTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
        certificateRetriever.getTrustedCertificatesStore().addOcspTrustedCertificates(Collections.singletonList(rootCert));

        // Remove required extensions to make test pass.
        properties.setRequiredExtensions(CertificateSources.all(), Collections.<CertificateExtension>emptyList());

        ValidationReport report1 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.OCSP_ISSUER),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        // This works fine because even though root certificate has CertificateSource.CERT_ISSUER context,
        // the chain contains initial certificate with CertificateSource.OCSP_ISSUER context.
        new AssertValidationReport(report1)
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                                && l.getMessage().equals(MessageFormatUtil.format
                                (CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        rootCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(rootCert),
                        "Certificate {0} is trusted.")
                .doAssert();

        ValidationReport report2 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.TIMESTAMP),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        // This doesn't work because root certificate has CertificateSource.CERT_ISSUER context and
        // the chain doesn't contain any certificate with CertificateSource.OCSP_ISSUER context.
        new AssertValidationReport(report2)
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(2)
                .hasLogItem(l -> l.getMessage().equals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                                rootCert.getSubjectX500Principal(), "OCSP response generation")),
                        CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT)
                .hasLogItem(l -> l.getMessage().equals(MessageFormatUtil.format(CertificateChainValidator.ISSUER_MISSING,
                        rootCert.getSubjectX500Principal())), CertificateChainValidator.ISSUER_MISSING)
                .doAssert();
    }

    @Test
    public void rootCertificateTrustedForCRLTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
        certificateRetriever.getTrustedCertificatesStore().addCrlTrustedCertificates(Collections.singletonList(rootCert));

        // Remove required extensions to make test pass.
        properties.setRequiredExtensions(CertificateSources.all(), Collections.<CertificateExtension>emptyList());

        ValidationReport report1 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.CRL_ISSUER),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        // This works fine because even though root certificate has CertificateSource.CERT_ISSUER context,
        // the chain contains initial certificate with CertificateSource.CRL_ISSUER context.
        new AssertValidationReport(report1)
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                                && l.getMessage().equals(MessageFormatUtil.format
                                (CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        rootCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(rootCert),
                        "Certificate {0} is trusted.")
                .doAssert();

        ValidationReport report2 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.OCSP_ISSUER),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        // This doesn't work because root certificate has CertificateSource.CERT_ISSUER context and
        // the chain doesn't contain any certificate with CertificateSource.CRL_ISSUER context.
        new AssertValidationReport(report2)
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(2)
                .hasLogItem(l -> l.getMessage().equals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                                rootCert.getSubjectX500Principal(), "CRL generation")),
                        CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT)
                .hasLogItem(l -> l.getMessage().equals(MessageFormatUtil.format(CertificateChainValidator.ISSUER_MISSING,
                        rootCert.getSubjectX500Principal())), CertificateChainValidator.ISSUER_MISSING)
                .doAssert();
    }

    @Test
    public void rootCertificateTrustedForTimestampTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.singletonList(intermediateCert));
        certificateRetriever.getTrustedCertificatesStore().addTimestampTrustedCertificates(Collections.singletonList(rootCert));

        // Remove required extensions to make test pass.
        properties.setRequiredExtensions(CertificateSources.all(), Collections.<CertificateExtension>emptyList());

        ValidationReport report1 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.TIMESTAMP),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        // This works fine because even though root certificate has CertificateSource.CERT_ISSUER context,
        // the chain contains initial certificate with CertificateSource.TIMESTAMP context.
        new AssertValidationReport(report1)
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals("Certificate check.")
                                && l.getMessage().equals(MessageFormatUtil.format
                                (CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        rootCert.getSubjectX500Principal()))
                                && ((CertificateReportItem) l).getCertificate().equals(rootCert),
                        "Certificate {0} is trusted.")
                .doAssert();

        ValidationReport report2 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.CRL_ISSUER),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        // This doesn't work because root certificate has CertificateSource.CERT_ISSUER context and
        // the chain doesn't contain any certificate with CertificateSource.TIMESTAMP context.
        new AssertValidationReport(report2)
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(2)
                .hasLogItem(l -> l.getMessage().equals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                                rootCert.getSubjectX500Principal(), "timestamp generation")),
                        CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT)
                .hasLogItem(l -> l.getMessage().equals(MessageFormatUtil.format(CertificateChainValidator.ISSUER_MISSING,
                        rootCert.getSubjectX500Principal())), CertificateChainValidator.ISSUER_MISSING)
                .doAssert();
    }

    private static class MockRevocationDataValidator extends RevocationDataValidator {
        public MockRevocationDataValidator(ValidatorChainBuilder builder) {
            super(builder);
        }

        @Override
        public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate,
                Date validationDate) {
        }
    }
}
