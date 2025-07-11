package com.itextpdf.signatures.validation;

import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
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
class LOTLTrustedStoreTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/certs/";

    private static X509Certificate crlRootCert;


    @BeforeAll
    public static void setUpOnce() throws CertificateException, IOException {
        crlRootCert= (X509Certificate) PemFileHelper.readFirstChain(SOURCE_FOLDER + "crlRoot.pem")[0];
    }

    @Test
    public void checkCertificateTest() {
        LOTLTrustedStore store = new LOTLTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/CA/QC");
        context.addNewServiceStatus(new ServiceStatusInfo(ServiceStatusInfo.GRANTED,
                LocalDateTime.of(1900,1,1, 0, 0)));
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
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage("Certificate {0} is trusted, revocation data checks are not required.",
                                l -> crlRootCert.getSubjectX500Principal())
                        .withCertificate(crlRootCert)
                ));
    }

    @Test
    public void checkCertificateWithValidationContextChainTest() {
        LOTLTrustedStore store = new LOTLTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL");
        context.addNewServiceStatus(new ServiceStatusInfo(ServiceStatusInfo.GRANTED,
                LocalDateTime.of(1900,1,1, 0, 0)));
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
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage("Certificate {0} is trusted, revocation data checks are not required.",
                                l -> crlRootCert.getSubjectX500Principal())
                        .withCertificate(crlRootCert)
                ));
    }

    @Test
    public void incorrectContextTest() {
        LOTLTrustedStore store = new LOTLTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("https://uri.etsi.org/TrstSvc/Svctype/TSA/QTST/");
        context.addNewServiceStatus(new ServiceStatusInfo(ServiceStatusInfo.GRANTED,
                LocalDateTime.of(1900,1,1, 0, 0)));
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
                        .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                        .withMessage("Certificate {0} is trusted for https://uri.etsi.org/TrstSvc/Svctype/TSA/QTST/, " +
                                        "but it is not used in this context. Validation will continue as usual.",
                                l -> crlRootCert.getSubjectX500Principal())
                        .withCertificate(crlRootCert)
                ));
    }


    @Test
    public void incorrectTimeBeforeValidTest() {
        LOTLTrustedStore store = new LOTLTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/CA/QC");
        context.addNewServiceStatus(new ServiceStatusInfo(ServiceStatusInfo.GRANTED,
                LocalDateTime.of(2025,1,1, 0, 0)));
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
                        .withCheckName(CertificateChainValidator.VALIDITY_CHECK)
                        .withMessage("Certificate {0} is not yet valid.",
                                l -> crlRootCert.getSubjectX500Principal())
                        .withCertificate(crlRootCert)
                ));
    }

    @Test
    public void incorrectTimeAfterValidTest() {
        LOTLTrustedStore store = new LOTLTrustedStore();
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(crlRootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/CA/QC");
        context.addNewServiceStatus(new ServiceStatusInfo(ServiceStatusInfo.WITHDRAWN,
                LocalDateTime.of(1900,1,1, 0, 0)));
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
                        .withCheckName(CertificateChainValidator.VALIDITY_CHECK)
                        .withMessage("Certificate {0} is revoked.",
                                l -> crlRootCert.getSubjectX500Principal())
                        .withCertificate(crlRootCert)
                ));
    }
}