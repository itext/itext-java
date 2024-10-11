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

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.crypto.OID.X509Extensions;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.CertificateSources;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.context.ValidatorContexts;
import com.itextpdf.signatures.validation.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.extensions.KeyUsage;
import com.itextpdf.signatures.validation.extensions.KeyUsageExtension;
import com.itextpdf.signatures.validation.mocks.MockIssuingCertificateRetriever;
import com.itextpdf.signatures.validation.mocks.MockRevocationDataValidator;
import com.itextpdf.signatures.validation.report.CertificateReportItem;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class CertificateChainValidatorTest extends ExtendedITextTest {
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/validation/CertificateChainValidatorTest/";

    private ValidatorChainBuilder validatorChainBuilder;
    private SignatureValidationProperties properties;
    private IssuingCertificateRetriever certificateRetriever;
    private final ValidationContext baseContext = new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
            CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);
    private MockRevocationDataValidator mockRevocationDataValidator;

    @BeforeEach
    public void setup() {
        mockRevocationDataValidator = new MockRevocationDataValidator();
        properties = new SignatureValidationProperties();
        certificateRetriever = new IssuingCertificateRetriever();
        validatorChainBuilder = new ValidatorChainBuilder()
                .withIssuingCertificateRetrieverFactory(()-> certificateRetriever)
                .withSignatureValidationProperties(properties)
                .withRevocationDataValidatorFactory(()-> mockRevocationDataValidator);
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
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                    .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                    .withMessage("Certificate {0} is trusted, revocation data checks are not required.",
                                    l -> rootCert.getSubjectX500Principal())
                    .withCertificate(rootCert)
                   ));
    }

    @Test
    public void validNumericBasicConstraintsTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "signChainWithValidNumericBasicConstraints.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.<Certificate>singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.<Certificate>singletonList(rootCert));

        ValidationReport report =
                validator.validateCertificate(baseContext, signingCert, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage("Certificate {0} is trusted, revocation data checks are not required.",
                                l -> rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert)
                ));
    }

    @Test
    public void invalidNumericBasicConstraintsTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "signChainWithInvalidNumericBasicConstraints.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.<Certificate>singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.<Certificate>singletonList(rootCert));

        ValidationReport report =
                validator.validateCertificate(baseContext, signingCert, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INVALID)
                .hasNumberOfFailures(2)
                .hasNumberOfLogs(3)
                .hasLogItem(la -> la
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage("Certificate {0} is trusted, revocation data checks are not required.",
                                l -> rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert)
                )
                .hasLogItem(la -> la
                        .withCheckName(CertificateChainValidator.EXTENSIONS_CHECK)
                        .withMessage(CertificateChainValidator.EXTENSION_MISSING,
                                l -> "2.5.29.19")
                        .withCertificate(rootCert)
                )
                .hasLogItem(la -> la
                        .withCheckName(CertificateChainValidator.EXTENSIONS_CHECK)
                        .withMessage(CertificateChainValidator.EXTENSION_MISSING,
                                l -> "2.5.29.19")
                        .withCertificate(intermediateCert)
                )
        );
    }

    @Test
    public void revocationValidationCallTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.<Certificate>singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.<Certificate>singletonList(rootCert));
        validator.validateCertificate(baseContext, signingCert, TimeTestUtil.TEST_DATE_TIME);


        Assertions.assertEquals(2, mockRevocationDataValidator.calls.size());

        MockRevocationDataValidator.RevocationDataValidatorCall call1 = mockRevocationDataValidator.calls.get(0);
        Assertions.assertEquals(signingCert, call1.certificate);
        Assertions.assertEquals(CertificateSource.SIGNER_CERT, call1.context.getCertificateSource());
        Assertions.assertEquals(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, call1.context.getValidatorContext());
        Assertions.assertEquals(TimeTestUtil.TEST_DATE_TIME, call1.validationDate);

        MockRevocationDataValidator.RevocationDataValidatorCall call2 = mockRevocationDataValidator.calls.get(1);
        Assertions.assertEquals(intermediateCert, call2.certificate);
        Assertions.assertEquals(CertificateSource.CERT_ISSUER, call2.context.getCertificateSource());
        Assertions.assertEquals(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, call2.context.getValidatorContext());
        Assertions.assertEquals(TimeTestUtil.TEST_DATE_TIME, call2.validationDate);
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

        properties.setContinueAfterFailure(ValidatorContexts.all() , CertificateSources.all(), true);
        // Set random extension as a required one to force the test to fail.
        properties.setRequiredExtensions(CertificateSources.of(CertificateSource.CERT_ISSUER),
                Collections.<CertificateExtension>singletonList(new KeyUsageExtension(KeyUsage.DECIPHER_ONLY)));

        ValidationReport report = validator.validateCertificate(baseContext, signingCert, DateTimeUtil.getCurrentTimeDate());

        Assertions.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assertions.assertEquals(3, report.getFailures().size());
        Assertions.assertEquals(4, report.getLogs().size());
        Assertions.assertEquals(report.getFailures().get(0), report.getLogs().get(0));
        Assertions.assertEquals(report.getFailures().get(1), report.getLogs().get(1));
        Assertions.assertEquals(report.getFailures().get(2), report.getLogs().get(2));

        CertificateReportItem failure1 = report.getCertificateFailures().get(0);
        Assertions.assertEquals(signingCert, failure1.getCertificate());
        Assertions.assertEquals("Required certificate extensions check.", failure1.getCheckName());
        Assertions.assertEquals(MessageFormatUtil.format(
                "Required extension {0} is missing or incorrect.", X509Extensions.KEY_USAGE), failure1.getMessage());

        CertificateReportItem failure2 = report.getCertificateFailures().get(1);
        Assertions.assertEquals(intermediateCert, failure2.getCertificate());
        Assertions.assertEquals("Required certificate extensions check.", failure2.getCheckName());
        Assertions.assertEquals(MessageFormatUtil.format(
                "Required extension {0} is missing or incorrect.", X509Extensions.KEY_USAGE), failure2.getMessage());

        CertificateReportItem failure3 = report.getCertificateFailures().get(2);
        Assertions.assertEquals(rootCert, failure3.getCertificate());
        Assertions.assertEquals("Required certificate extensions check.", failure3.getCheckName());
        Assertions.assertEquals(MessageFormatUtil.format(
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

        Assertions.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assertions.assertEquals(1, report.getFailures().size());
        Assertions.assertEquals(1, report.getLogs().size());
        Assertions.assertEquals(report.getFailures().get(0), report.getLogs().get(0));

        CertificateReportItem failure1 = report.getCertificateFailures().get(0);
        Assertions.assertEquals(signingCert, failure1.getCertificate());
        Assertions.assertEquals("Required certificate extensions check.", failure1.getCheckName());
        Assertions.assertEquals(MessageFormatUtil.format(
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

        AssertValidationReport.assertThat(report, a -> a
            .hasNumberOfFailures(0)
            .hasNumberOfLogs(1)
            .hasLogItem(la -> la
                .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                             l -> intermediateCert.getSubjectX500Principal())
               ));
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

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                    .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                    .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                            l -> rootCert.getSubjectX500Principal())
                    .withCertificate(rootCert)
                   ));
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

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfFailures(2)
                .hasNumberOfLogs(3)
                .hasLogItem(la -> la
                    .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                    .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                    l-> rootCert.getSubjectX500Principal())
                    .withCertificate(rootCert)
                   )
                .hasLogItem(la -> la
                    .withCheckName(CertificateChainValidator.EXTENSIONS_CHECK)
                    .withMessage(CertificateChainValidator.EXTENSION_MISSING,
                                            l ->X509Extensions.KEY_USAGE)
                    .withCertificate(signingCert)
                   )
                .hasLogItem(la -> la
                    .withCheckName(CertificateChainValidator.EXTENSIONS_CHECK)
                    .withMessage(CertificateChainValidator.EXTENSION_MISSING,
                                    l -> X509Extensions.BASIC_CONSTRAINTS)
                    .withCertificate(signingCert)
                   ));

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

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                    .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                    .withMessage(CertificateChainValidator.ISSUER_MISSING,
                                            l-> intermediateCert.getSubjectX500Principal())
                    .withCertificate(intermediateCert)
                   ));
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


        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(2)
                .hasLogItem(la -> la
                    .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                    .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                l->rootCert.getSubjectX500Principal())
                    .withCertificate(rootCert)
                   )
                .hasLogItem(la -> la
                    .withCheckName(CertificateChainValidator.VALIDITY_CHECK)
                    .withMessage(CertificateChainValidator.NOT_YET_VALID_CERTIFICATE,
                                    l-> intermediateCert.getSubjectX500Principal())
                    .withCertificate(intermediateCert)
                    .withExceptionCauseType(CertificateNotYetValidException.class)
                   ));
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

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INVALID)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(2)
                .hasLogItem(la -> la
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        l -> rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert))
                .hasLogItem(la -> la
                        .withCheckName(CertificateChainValidator.VALIDITY_CHECK)
                        .withMessage(CertificateChainValidator.EXPIRED_CERTIFICATE,
                                l-> intermediateCert.getSubjectX500Principal())
                        .withCertificate(intermediateCert)
                        .withExceptionCauseType(CertificateExpiredException.class))
        );
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

        AssertValidationReport.assertThat(report1, a-> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName("Certificate check.")
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                            i-> rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert))
        );

        ValidationReport report2 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.OCSP_ISSUER),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        AssertValidationReport.assertThat(report2, a-> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName("Certificate check.")
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                i-> rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert))
                );

        ValidationReport report3 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.TIMESTAMP),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        AssertValidationReport.assertThat(report3, a-> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName("Certificate check.")
                    .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                            i -> rootCert.getSubjectX500Principal())
                    .withCertificate(rootCert))
                );
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

        AssertValidationReport.assertThat(report1, a-> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName("Certificate check.")
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        i->rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert))
                );

        ValidationReport report2 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.OCSP_ISSUER),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        AssertValidationReport.assertThat(report2, a-> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName("Certificate check.")
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        i->rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert))
                );

        ValidationReport report3 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.TIMESTAMP),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        AssertValidationReport.assertThat(report3, a-> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName("Certificate check.")
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        i->rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert))
                );
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
        AssertValidationReport.assertThat(report1, a-> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName("Certificate check.")
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                     i-> signingCert.getSubjectX500Principal())
                        .withCertificate(signingCert))
                );

        ValidationReport report2 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.TIMESTAMP),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        // This doesn't work because certificate in question has CertificateSource.TIMESTAMP context.
        AssertValidationReport.assertThat(report2, a-> a
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(2)
                .hasLogItem(al->al
                    .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                                i -> signingCert.getSubjectX500Principal(), i -> "certificates generation"))
                .hasLogItem(al->al
                    .withMessage(CertificateChainValidator.ISSUER_MISSING,
                        i -> intermediateCert.getSubjectX500Principal()))
                );
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
        AssertValidationReport.assertThat(report1, a-> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName("Certificate check.")
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                       i-> rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert))
                );

        ValidationReport report2 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.TIMESTAMP),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        // This doesn't work because root certificate has CertificateSource.CERT_ISSUER context and
        // the chain doesn't contain any certificate with CertificateSource.OCSP_ISSUER context.
        AssertValidationReport.assertThat(report2, a-> a
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(2)
                .hasLogItem(l -> l
                    .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                                i-> rootCert.getSubjectX500Principal(),i-> "OCSP response generation"))
                .hasLogItem(l -> l
                    .withMessage(CertificateChainValidator.ISSUER_MISSING,
                        i-> rootCert.getSubjectX500Principal()))
                );
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
        AssertValidationReport.assertThat(report1, a-> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName("Certificate check.")
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                        i-> rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert))
                );

        ValidationReport report2 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.OCSP_ISSUER),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        // This doesn't work because root certificate has CertificateSource.CERT_ISSUER context and
        // the chain doesn't contain any certificate with CertificateSource.CRL_ISSUER context.
        AssertValidationReport.assertThat(report2, a-> a
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(2)
                .hasLogItem(l -> l
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                                i-> rootCert.getSubjectX500Principal(), i-> "CRL generation"))
                .hasLogItem(l -> l
                        .withMessage(CertificateChainValidator.ISSUER_MISSING,
                        i-> rootCert.getSubjectX500Principal()))
                );
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
        AssertValidationReport.assertThat(report1, a-> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName("Certificate check.")
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                       i-> rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert))
                );

        ValidationReport report2 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.CRL_ISSUER),
                signingCert, DateTimeUtil.getCurrentTimeDate());

        // This doesn't work because root certificate has CertificateSource.CERT_ISSUER context and
        // the chain doesn't contain any certificate with CertificateSource.TIMESTAMP context.
        AssertValidationReport.assertThat(report2, a-> a
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(2)
                .hasLogItem(l -> l
                        .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                                i-> rootCert.getSubjectX500Principal(), i-> "timestamp generation"))
                .hasLogItem(l -> l
                            .withMessage(CertificateChainValidator.ISSUER_MISSING,
                                i-> rootCert.getSubjectX500Principal()))
                );
    }


    @Test
    public void trustStoreFailureTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        MockIssuingCertificateRetriever mockCertificateRetriever =
                new MockIssuingCertificateRetriever(certificateRetriever)
                        .onGetTrustedCertificatesStoreDo(() -> {
                            throw new RuntimeException("Test trust store failure");
                        });

        validatorChainBuilder.withIssuingCertificateRetrieverFactory(()-> mockCertificateRetriever);

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.<Certificate>singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.<Certificate>singletonList(rootCert));



        ValidationReport report =
                validator.validateCertificate(baseContext, signingCert, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasLogItems(1,10, la -> la
                        .withMessage(CertificateChainValidator.TRUSTSTORE_RETRIEVAL_FAILED)
                ));
    }

    @Test
    public void issuerRetrievalFailureTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        MockIssuingCertificateRetriever mockCertificateRetriever =
                new MockIssuingCertificateRetriever(certificateRetriever)
                        .onRetrieveIssuerCertificateDo(c -> {
                            throw new RuntimeException("Test issuer retrieval failure");
                        });

        validatorChainBuilder.withIssuingCertificateRetrieverFactory(()-> mockCertificateRetriever);

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.<Certificate>singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.<Certificate>singletonList(rootCert));



        ValidationReport report =
                validator.validateCertificate(baseContext, signingCert, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasLogItems(1,10, la -> la
                        .withMessage(CertificateChainValidator.ISSUER_RETRIEVAL_FAILED)
                ));
    }

    @Test
    public void revocationValidationFailureTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        mockRevocationDataValidator.onValidateDo(c ->  {
            throw new RuntimeException("Test revocation validation failure");
        });

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.<Certificate>singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.<Certificate>singletonList(rootCert));



        ValidationReport report =
                validator.validateCertificate(baseContext, signingCert, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INDETERMINATE)
                .hasLogItems(1,10, la -> la
                        .withMessage(CertificateChainValidator.REVOCATION_VALIDATION_FAILED)
                ));
    }

    @Test
    public void testStopOnInvalidRevocationResultTest() throws CertificateException, IOException {
        mockRevocationDataValidator.onValidateDo(c ->
                c.report.addReportItem(new ReportItem("test", "test",
                        ReportItem.ReportItemStatus.INVALID)));

        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        properties.setContinueAfterFailure(ValidatorContexts.all(), CertificateSources.all(), false);
        MockIssuingCertificateRetriever mockCertificateRetriever =
                new MockIssuingCertificateRetriever(certificateRetriever);
        validatorChainBuilder.withIssuingCertificateRetrieverFactory(()-> mockCertificateRetriever);

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.addKnownCertificates(Collections.<Certificate>singletonList(intermediateCert));
        certificateRetriever.setTrustedCertificates(Collections.<Certificate>singletonList(rootCert));

        ValidationReport report =
                validator.validateCertificate(baseContext, signingCert, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INVALID)
                );
        Assertions.assertEquals(0, mockCertificateRetriever.getCrlIssuerCertificatesCalls.size());
        Assertions.assertEquals(0, mockCertificateRetriever.getCrlIssuerCertificatesByNameCalls.size());
        Assertions.assertEquals(1, mockRevocationDataValidator.calls.size());
    }
}