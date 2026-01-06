/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.validation.AssertValidationReport;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Collections;

@Tag("BouncyCastleUnitTest")
class LotlTrustedStoreTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String SOURCE_FOLDER_LOTL_FILES = "./src/test/resources/com/itextpdf/signatures/validation" +
            "/lotl/LotlState2025_08_08/";

    private static X509Certificate crlRootCert;


    @BeforeAll
    public static void setUpOnce() throws CertificateException, IOException {
        crlRootCert = (X509Certificate) PemFileHelper.readFirstChain(SOURCE_FOLDER + "crlRoot.pem")[0];
    }

    @Test
    public void checkCertificateTest() {
        LotlTrustedStore store = new ValidatorChainBuilder().getLotlTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/CA/QC");
        context.addServiceChronologicalInfo(new ServiceChronologicalInfo(ServiceChronologicalInfo.GRANTED,
                LocalDateTime.of(1900, 1, 1, 0, 0)));
        store.addCertificatesWithContext(Collections.singletonList(context));

        ValidationReport report = new ValidationReport();
        Assertions.assertTrue(store.checkIfCertIsTrusted(report, new ValidationContext(
                        ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.CRL_ISSUER, TimeBasedContext.PRESENT),
                crlRootCert, TimeTestUtil.TEST_DATE_TIME));
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                        .withCheckName(LotlTrustedStore.CERTIFICATE_CHECK)
                        .withMessage("Certificate {0} is trusted, revocation data checks are not required.",
                                l -> crlRootCert.getSubjectX500Principal())
                        .withCertificate(crlRootCert)
                ));
    }

    @Test
    public void checkCertificateWithValidationContextChainTest() {
        LotlTrustedStore store = new ValidatorChainBuilder().getLotlTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL");
        context.addServiceChronologicalInfo(new ServiceChronologicalInfo(ServiceChronologicalInfo.GRANTED,
                LocalDateTime.of(1900, 1, 1, 0, 0)));
        store.addCertificatesWithContext(Collections.singletonList(context));

        ValidationReport report = new ValidationReport();
        ValidationContext previousValidationContext = new ValidationContext(
                ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.CRL_ISSUER, TimeBasedContext.PRESENT);
        ValidationContext validationContext = previousValidationContext
                .setCertificateSource(CertificateSource.OCSP_ISSUER);
        Assertions.assertTrue(store.checkIfCertIsTrusted(report, validationContext,
                crlRootCert, TimeTestUtil.TEST_DATE_TIME));
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                        .withCheckName(LotlTrustedStore.CERTIFICATE_CHECK)
                        .withMessage("Certificate {0} is trusted, revocation data checks are not required.",
                                l -> crlRootCert.getSubjectX500Principal())
                        .withCertificate(crlRootCert)
                ));
    }

    @Test
    public void incorrectContextTest() {
        LotlTrustedStore store = new ValidatorChainBuilder().getLotlTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/TSA/QTST");
        context.addServiceChronologicalInfo(new ServiceChronologicalInfo(ServiceChronologicalInfo.GRANTED,
                LocalDateTime.of(1900, 1, 1, 0, 0)));
        store.addCertificatesWithContext(Collections.singletonList(context));

        ValidationReport report = new ValidationReport();
        Assertions.assertFalse(store.checkIfCertIsTrusted(report, new ValidationContext(
                        ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.CRL_ISSUER, TimeBasedContext.PRESENT),
                crlRootCert, TimeTestUtil.TEST_DATE_TIME));
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                        .withCheckName(LotlTrustedStore.CERTIFICATE_CHECK)
                        .withMessage(LotlTrustedStore.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                                l -> crlRootCert.getSubjectX500Principal(),
                                l -> "http://uri.etsi.org/TrstSvc/Svctype/TSA/QTST")
                        .withCertificate(crlRootCert)
                ));
    }

    @Test
    public void serviceTypeNotRecognizedTest() {
        LotlTrustedStore store = new ValidatorChainBuilder().getLotlTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("invalid service type");
        context.addServiceChronologicalInfo(new ServiceChronologicalInfo(ServiceChronologicalInfo.GRANTED,
                LocalDateTime.of(1900, 1, 1, 0, 0)));
        store.addCertificatesWithContext(Collections.singletonList(context));

        ValidationReport report = new ValidationReport();
        Assertions.assertFalse(store.checkIfCertIsTrusted(report, new ValidationContext(
                        ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.CRL_ISSUER, TimeBasedContext.PRESENT),
                crlRootCert, TimeTestUtil.TEST_DATE_TIME));
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                        .withCheckName(LotlTrustedStore.CERTIFICATE_CHECK)
                        .withMessage(LotlTrustedStore.CERTIFICATE_SERVICE_TYPE_NOT_RECOGNIZED,
                                l -> crlRootCert.getSubjectX500Principal(), l -> "invalid service type")
                        .withCertificate(crlRootCert)
                ));
    }


    @Test
    public void incorrectTimeBeforeValidTest() {
        LotlTrustedStore store = new ValidatorChainBuilder().getLotlTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/CA/QC");
        context.addServiceChronologicalInfo(new ServiceChronologicalInfo(ServiceChronologicalInfo.GRANTED,
                LocalDateTime.of(2025, 1, 1, 0, 0)));
        store.addCertificatesWithContext(Collections.singletonList(context));

        ValidationReport report = new ValidationReport();
        Assertions.assertFalse(store.checkIfCertIsTrusted(report, new ValidationContext(
                        ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.CRL_ISSUER, TimeBasedContext.PRESENT),
                crlRootCert, TimeTestUtil.TEST_DATE_TIME));
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INVALID)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                        .withCheckName(LotlTrustedStore.CERTIFICATE_CHECK)
                        .withMessage("Certificate {0} is not yet valid.",
                                l -> crlRootCert.getSubjectX500Principal())
                        .withCertificate(crlRootCert)
                ));
    }

    @Test
    public void incorrectTimeAfterValidTest() {
        LotlTrustedStore store = new ValidatorChainBuilder().getLotlTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/CA/QC");
        context.addServiceChronologicalInfo(new ServiceChronologicalInfo(
                "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/withdrawn",
                LocalDateTime.of(1900, 1, 1, 0, 0)));
        store.addCertificatesWithContext(Collections.singletonList(context));

        ValidationReport report = new ValidationReport();
        Assertions.assertFalse(store.checkIfCertIsTrusted(report, new ValidationContext(
                        ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.CRL_ISSUER, TimeBasedContext.PRESENT),
                crlRootCert, TimeTestUtil.TEST_DATE_TIME));
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INVALID)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                        .withCheckName(LotlTrustedStore.CERTIFICATE_CHECK)
                        .withMessage("Certificate {0} is revoked.",
                                l -> crlRootCert.getSubjectX500Principal())
                        .withCertificate(crlRootCert)
                ));
    }

    @Test
    public void checkCertificateWithCorrectExtensionScopeTest() {
        LotlTrustedStore store = new ValidatorChainBuilder().getLotlTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/CA/QC");
        ServiceChronologicalInfo info = new ServiceChronologicalInfo(ServiceChronologicalInfo.GRANTED,
                LocalDateTime.of(1900, 1, 1, 0, 0));
        info.addServiceExtension(new AdditionalServiceInformationExtension(
                "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSignatures"));
        context.addServiceChronologicalInfo(info);
        store.addCertificatesWithContext(Collections.singletonList(context));

        ValidationReport report = new ValidationReport();
        Assertions.assertTrue(store.checkIfCertIsTrusted(report, new ValidationContext(
                        ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT),
                crlRootCert, TimeTestUtil.TEST_DATE_TIME));
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                        .withCheckName(LotlTrustedStore.CERTIFICATE_CHECK)
                        .withMessage("Certificate {0} is trusted, revocation data checks are not required.",
                                l -> crlRootCert.getSubjectX500Principal())
                        .withCertificate(crlRootCert)
                ));
    }

    @Test
    public void checkCertificateWithCorrectExtensionScope2Test() {
        LotlTrustedStore store = new ValidatorChainBuilder().getLotlTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/CA/QC");
        ServiceChronologicalInfo info = new ServiceChronologicalInfo(ServiceChronologicalInfo.GRANTED,
                LocalDateTime.of(1900, 1, 1, 0, 0));
        info.addServiceExtension(new AdditionalServiceInformationExtension(
                "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSignatures"));
        info.addServiceExtension(new AdditionalServiceInformationExtension(
                "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForWebSiteAuthentication"));
        context.addServiceChronologicalInfo(info);
        store.addCertificatesWithContext(Collections.singletonList(context));

        ValidationReport report = new ValidationReport();
        Assertions.assertTrue(store.checkIfCertIsTrusted(report, new ValidationContext(
                        ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT),
                crlRootCert, TimeTestUtil.TEST_DATE_TIME));
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                        .withCheckName(LotlTrustedStore.CERTIFICATE_CHECK)
                        .withMessage("Certificate {0} is trusted, revocation data checks are not required.",
                                l -> crlRootCert.getSubjectX500Principal())
                        .withCertificate(crlRootCert)
                ));
    }

    @Test
    public void checkCertificateWithIncorrectExtensionScopeTest() {
        LotlTrustedStore store = new ValidatorChainBuilder().getLotlTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/CA/QC");
        ServiceChronologicalInfo info = new ServiceChronologicalInfo(ServiceChronologicalInfo.GRANTED,
                LocalDateTime.of(1900, 1, 1, 0, 0));
        info.addServiceExtension(new AdditionalServiceInformationExtension(
                "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForWebSiteAuthentication"));
        context.addServiceChronologicalInfo(info);
        store.addCertificatesWithContext(Collections.singletonList(context));

        ValidationReport report = new ValidationReport();
        Assertions.assertFalse(store.checkIfCertIsTrusted(report, new ValidationContext(
                        ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT),
                crlRootCert, TimeTestUtil.TEST_DATE_TIME));
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INVALID)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(la -> la
                        .withCheckName(LotlTrustedStore.EXTENSIONS_CHECK)
                        .withMessage(LotlTrustedStore.SCOPE_SPECIFIED_WITH_INVALID_TYPES,
                                l -> crlRootCert.getSubjectX500Principal(),
                                k -> "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForWebSiteAuthentication")
                        .withCertificate(crlRootCert)
                ));
    }

    @Test
    public void createTrustedStoreWithValidLotl() {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        LotlFetchingProperties fetchingProperties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        try (LotlService lotlService = new LotlService(fetchingProperties)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            chainBuilder.withLotlService(() -> lotlService);
            lotlService.initializeCache();
        }

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        Assertions.assertFalse(trustedStore.getCertificates().isEmpty());
    }

    @Test
    public void createTrustedStoreWithInvalidLotl() {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        LotlFetchingProperties fetchingProperties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        try (LotlService lotlService = new LotlService(fetchingProperties)) {
            lotlService.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
            chainBuilder.withLotlService(() -> lotlService);
            lotlService.initializeCache();

            LotlValidator invalidValidator = new LotlValidator(lotlService) {
                @Override
                public ValidationReport validate() {
                    ValidationReport report = new ValidationReport();
                    report.addReportItem(new ReportItem("check", "test invalid", ReportItemStatus.INVALID));
                    return report;
                }
            };
            lotlService.withLotlValidator(() -> invalidValidator);
        }

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        Assertions.assertTrue(trustedStore.getCertificates().isEmpty());
    }
}
