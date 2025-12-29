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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.validation.EuropeanTrustedListConfigurationFactory;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

// This test suite is taken from https://eidas.ec.europa.eu/efda/validation-tests#/screen/home
@Tag("BouncyCastleIntegrationTest")
public class TSLVersion6Test extends ExtendedITextTest {
    private static final String CERTS = "./src/test/resources/com/itextpdf/signatures/validation/lotl/TSLVersion6Test/test_certificates/";
    private static final String SOURCE_FOLDER_LOTL_FILES = "./src/test/resources/com/itextpdf/signatures/validation/lotl/TSLVersion6Test/test_lotl_snapshot/";

    private static final ValidationContext SIGN_CONTEXT = new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);
    private static final Supplier<EuropeanTrustedListConfigurationFactory> FACTORY = EuropeanTrustedListConfigurationFactory.getFactory();

    private static final String LOTL_CERT = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDPDCCAiSgAwIBAgIBATANBgkqhkiG9w0BAQ0FADBQMRQwEgYDVQQDDAtDRVJU\n" +
            "LUxPVEwtNDEYMBYGA1UECgwPRVUgT3JnYW5pemF0aW9uMREwDwYDVQQLDAhQS0kt\n" +
            "VEVTVDELMAkGA1UEBhMCTFUwHhcNMjQxMjIxMDAwMDA2WhcNMjYxMjIxMDAwMDA2\n" +
            "WjBQMRQwEgYDVQQDDAtDRVJULUxPVEwtNDEYMBYGA1UECgwPRVUgT3JnYW5pemF0\n" +
            "aW9uMREwDwYDVQQLDAhQS0ktVEVTVDELMAkGA1UEBhMCTFUwggEiMA0GCSqGSIb3\n" +
            "DQEBAQUAA4IBDwAwggEKAoIBAQCiu8CP3OKq8DMOoJigZH8n1xssQhLtySOJ5tGS\n" +
            "6KOWTfDaTl3eq+4svjLzhqGNDgSYwk/khYZEoJntO3lrkDN6KsYSOBDFNpjYpxa7\n" +
            "p3EUjuJxDb1clqx27kkuMTIFl5pcs9oNuUORntyZ2SqjqqnqxkjsSpqNPl8nyZc9\n" +
            "gaAY13XU4D2ACsRmnpURmkFj4ppMucMjTCeqlFesvJELf06jfJcLHIJU/b2Wx8a2\n" +
            "0x3nN564anLIpBfL5Ws40ScRywp9tve2M77DXWXhXKSAaE5D7Fnb7NRb/pPbW5sd\n" +
            "fjhDoO7EghXsIDKgJPtdJlThikTkGTk59t6IODu9gnZ5x7uvAgMBAAGjITAfMB0G\n" +
            "A1UdDgQWBBROJGXazRDpyPzpJpUN2EBep0AzazANBgkqhkiG9w0BAQ0FAAOCAQEA\n" +
            "nOzoRYzYEhFdNqXwA89CaHTTEnfubTXqv+fx5t5SpXxr+TEYt4Vrhxepk+nHTfR9\n" +
            "zcwsECYRaZ8c436F7Gk/Fva99njCSJKvh8Awtbmi1+qv8hdkaaNTs2mH+6zR0iva\n" +
            "anq+G3ozGXQ20L6/HdYXrmrBl6i4JjZ2jbbc5whPx/urgAlngB9oD34YfaedLVUf\n" +
            "Qr8y9OCwFNh0zVvLwYbFyPFdvmt9bgxvxmAH+pD/k8Sxe8HkMbqVqMo2/PgHlEaW\n" +
            "7CoKoEnAsgGlvmTA4fA2rlUNIUWzZNCr6ee6pdfM6+wZS4v4301L1JezYzyJxJHN\n" +
            "/Ols4IYrBvATPiZl2kxVPQ==\n" +
            "-----END CERTIFICATE-----";

    @BeforeAll
    public static void beforeAll() {
        // Initialize the LotlService with a default EuropeanResourceFetcher
        LotlService service = new LotlService(new LotlFetchingProperties(new ThrowExceptionOnFailingCountryData()));
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
        EuropeanTrustedListConfigurationFactory.setFactory(() -> new EuropeanTrustedListConfigurationFactory() {
            @Override
            public String getTrustedListUri() {
                return "https://eidas.ec.europa.eu/efda/api/v2/validation-tests/testcase/tl/LOTL-4.xml";
            }

            @Override
            public String getCurrentlySupportedPublication() {
                return "https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2019.276.01.0001.01.ENG";
            }

            @Override
            public List<Certificate> getCertificates() {
                Certificate certificate = CertificateUtil.readCertificatesFromPem(
                        new ByteArrayInputStream(LOTL_CERT.getBytes(
                                StandardCharsets.UTF_8)))[0];
                return Collections.singletonList(certificate);
            }
        });
        service.withLotlValidator(() -> new LotlValidator(service));
        LotlService.GLOBAL_SERVICE = service;
        service.initializeCache();
    }

    @AfterAll
    public static void afterAll() {
        EuropeanTrustedListConfigurationFactory.setFactory(FACTORY);
        LotlService.GLOBAL_SERVICE.close();
        LotlService.GLOBAL_SERVICE = null;
    }

    @Test
    public void defaultCaseQESigTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_8.1.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(),
                SIGN_CONTEXT, trustedCert, DateTimeUtil.getCurrentTimeDate());

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void defaultCaseAdESigTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_8.1.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(),
                SIGN_CONTEXT, trustedCert, DateTimeUtil.getCurrentTimeDate());

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    // Example "2.TSL v6 - Wrong signature format" from a test suite is missing,
    // because wrong signature format results in exception during LOTL cache initialization, which is expected.
    // But because of this we have an exception much earlier than needed for a test.

    @Test
    public void serviceWithTLIssuerAndServiceWithQcCaTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_8.3.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(),
                SIGN_CONTEXT, trustedCert, DateTimeUtil.getCurrentTimeDate());

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }
}
