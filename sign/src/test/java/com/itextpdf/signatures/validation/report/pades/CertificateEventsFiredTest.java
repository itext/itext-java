package com.itextpdf.signatures.validation.report.pades;

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.validation.CertificateChainValidator;
import com.itextpdf.signatures.validation.dataorigin.CertificateOrigin;
import com.itextpdf.signatures.validation.RevocationDataValidator;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.events.CertificateIssuerExternalRetrievalEvent;
import com.itextpdf.signatures.validation.events.CertificateIssuerRetrievedOutsideDSSEvent;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;

@Tag("BouncyCastleUnitTest")
public class CertificateEventsFiredTest extends ExtendedITextTest {
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final ValidationContext VALIDATION_CONTEXT = new ValidationContext(
            ValidatorContext.REVOCATION_DATA_VALIDATOR, CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);
    private static final Date CURRENT_DATE = DateTimeUtil.getCurrentTimeDate();
    private RevocationEventsFiredTest.CustomReportGenerator customReportGenerator;
    private ValidatorChainBuilder builder;
    private X509Certificate dummyCertificate;
    private X509Certificate caCertificate;
    private IssuingCertificateRetriever certificateRetriever;

    @BeforeEach
    public void setUp() throws CertificateException, IOException {
        builder = new ValidatorChainBuilder();
        customReportGenerator = new RevocationEventsFiredTest.CustomReportGenerator();
        builder.withPAdESLevelReportGenerator(customReportGenerator);
        dummyCertificate = (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsa01.pem")[0];
        caCertificate = (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsa01.pem")[1];
        certificateRetriever = new IssuingCertificateRetriever();
        builder.withIssuingCertificateRetrieverFactory(() -> certificateRetriever);
        builder.withRevocationDataValidatorFactory(() -> new RevocationDataValidator(builder) {
            @Override
            public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate, Date validationDate) {
                // Do nothing.
            }
        });
    }

    @Test
    public void certificateFromDssNoEventsTest() {
        certificateRetriever.addKnownCertificates(Collections.singletonList(dummyCertificate), CertificateOrigin.LATEST_DSS);
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCertificate));

        CertificateChainValidator validator = builder.buildCertificateChainValidator();
        validator.validateCertificate(VALIDATION_CONTEXT, dummyCertificate, CURRENT_DATE);

        Assertions.assertEquals(0, customReportGenerator.firedEvents.size());
    }

    @Test
    public void certificateFromSignatureEventTest() {
        certificateRetriever.addKnownCertificates(Collections.singletonList(dummyCertificate), CertificateOrigin.SIGNATURE);
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCertificate));

        CertificateChainValidator validator = builder.buildCertificateChainValidator();
        validator.validateCertificate(VALIDATION_CONTEXT, dummyCertificate, CURRENT_DATE);

        Assertions.assertEquals(1, customReportGenerator.firedEvents.size());
        Assertions.assertTrue(customReportGenerator.firedEvents.get(0) instanceof CertificateIssuerRetrievedOutsideDSSEvent);
    }

    @Test
    public void twoDifferentCertificateEventsTest() {
        certificateRetriever.addKnownCertificates(Collections.singletonList(dummyCertificate), CertificateOrigin.OTHER);
        certificateRetriever.addKnownCertificates(Collections.singletonList(caCertificate), CertificateOrigin.SIGNATURE);

        CertificateChainValidator validator = builder.buildCertificateChainValidator();
        validator.validateCertificate(VALIDATION_CONTEXT, dummyCertificate, CURRENT_DATE);

        Assertions.assertEquals(2, customReportGenerator.firedEvents.size());
        Assertions.assertTrue(customReportGenerator.firedEvents.get(0) instanceof CertificateIssuerExternalRetrievalEvent);
        Assertions.assertTrue(customReportGenerator.firedEvents.get(1) instanceof CertificateIssuerRetrievedOutsideDSSEvent);
    }

    @Test
    public void certificateDataOriginNotSetTest() {
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCertificate));

        CertificateChainValidator validator = builder.buildCertificateChainValidator();
        validator.validateCertificate(VALIDATION_CONTEXT, dummyCertificate, CURRENT_DATE);

        Assertions.assertEquals(1, customReportGenerator.firedEvents.size());
        Assertions.assertTrue(customReportGenerator.firedEvents.get(0) instanceof CertificateIssuerExternalRetrievalEvent);
    }

    @Test
    public void certificateMultipleDataOriginsTest() {
        certificateRetriever.addKnownCertificates(Collections.singletonList(dummyCertificate), CertificateOrigin.OTHER);
        certificateRetriever.addKnownCertificates(Collections.singletonList(dummyCertificate), CertificateOrigin.LATEST_DSS);
        certificateRetriever.addKnownCertificates(Collections.singletonList(dummyCertificate), CertificateOrigin.SIGNATURE);
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCertificate));

        CertificateChainValidator validator = builder.buildCertificateChainValidator();
        validator.validateCertificate(VALIDATION_CONTEXT, dummyCertificate, CURRENT_DATE);

        Assertions.assertEquals(0, customReportGenerator.firedEvents.size());
    }
}
