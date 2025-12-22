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
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

// This test suite is taken from https://eidas.ec.europa.eu/efda/validation-tests#/screen/home
@Tag("BouncyCastleIntegrationTest")
public class QualifiedValidatorTest extends ExtendedITextTest {

    private static final String CERTS = "./src/test/resources/com/itextpdf/signatures/validation/lotl/QualifiedValidatorTest/test_certificates/";
    private static final String SOURCE_FOLDER_LOTL_FILES = "./src/test/resources/com/itextpdf/signatures/validation/lotl/QualifiedValidatorTest/test_lotl_snapshot/";

    private static final ValidationContext SIGN_CONTEXT = new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);
    private static final Supplier<EuropeanTrustedListConfigurationFactory> FACTORY = EuropeanTrustedListConfigurationFactory.getFactory();
    private static final Date PRESENT_DATE = DateTimeUtil.createUtcDateTime(2025, 9, 26, 2, 0, 56);
    private static final Date PRE_EIDAS_DATE = DateTimeUtil.createUtcDateTime(2014, 2, 3, 15, 0, 0);
    private static final Date PRE_EIDAS_DATE2 = DateTimeUtil.createUtcDateTime(2015, 5, 6, 15, 0, 0);

    private static final String LOTL_CERT = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDPDCCAiSgAwIBAgIBATANBgkqhkiG9w0BAQ0FADBQMRQwEgYDVQQDDAtDRVJU\n" +
            "LUxPVEwtMzEYMBYGA1UECgwPRVUgT3JnYW5pemF0aW9uMREwDwYDVQQLDAhQS0kt\n" +
            "VEVTVDELMAkGA1UEBhMCTFUwHhcNMjQxMDI1MjMwMDAzWhcNMjYxMDI2MDAwMDAz\n" +
            "WjBQMRQwEgYDVQQDDAtDRVJULUxPVEwtMzEYMBYGA1UECgwPRVUgT3JnYW5pemF0\n" +
            "aW9uMREwDwYDVQQLDAhQS0ktVEVTVDELMAkGA1UEBhMCTFUwggEiMA0GCSqGSIb3\n" +
            "DQEBAQUAA4IBDwAwggEKAoIBAQDeU/iKtAqrfGrHB1N6gFh+d56+W46IxUFEWiS+\n" +
            "Q+zER1/6hZEKVk0IWhCw2yS5p43Z5h9H3LSMfexTLqSbwhve5+accma+Q6It0vg3\n" +
            "rrBGnMPGOqta7Zc5zZ3kv83jJCQ8EU6FnCp7OqQY2ymiqgIWHwbDWooNUsYnu+wv\n" +
            "bcYx/AYweMZLdWSogt3iu5Sh1zNhubU4tasn/A5x0pDV97BSGIvs5mmqIndF8uDc\n" +
            "mmxmjn105LGEQqwT6GN1r99kwd2UZewbVztlbvDoI6eTDkZ1ffomDHnNjEIBhcgG\n" +
            "TlI3zpRmIVcj6Vckh8zGmewTt6FJhGlIb83iqB9ah8ki03NzAgMBAAGjITAfMB0G\n" +
            "A1UdDgQWBBRJ79BepQX9cyVvwvG/Xp1yxwYvnTANBgkqhkiG9w0BAQ0FAAOCAQEA\n" +
            "PQJNKkMNUGO5gM/CC6D7e4EBvkCBwgjtIhAFoXEzmqij/0Da+dNY1xk6hPMR8jd3\n" +
            "YFpwsBP3h72hSoq8wZhJ3erP0uIo4qmOPDeJsmkpRsKqDFmTg04bE3bGV1pBI06o\n" +
            "AqwQr5JAoQAIrMFDobxXsTXC1abUKO9BId72rUy5Mxv227aVNx8nWcZoKeg37FVk\n" +
            "bLgd+mjfh8LzxM02i3WIM+Z2wdq/h8SVlupPPkrJr2edBv/CzCf1VFa8L7tDMpxP\n" +
            "9HdHBJz+nUfTe5mXzqHS0MxogW5sBUk8Rj9KCvNO5wdPZhfg8nGrEnGWXj8gl9Km\n" +
            "MwsoJseoWfQ6GjmQCv0kpQ==\n" +
            "-----END CERTIFICATE-----";

    @BeforeAll
    public static void beforeAll() {
        // Initialize the LotlService with a default EuropeanResourceFetcher
        LotlService service = new LotlService(new LotlFetchingProperties(new ThrowExceptionOnFailingCountryData()));
        service.withCustomResourceRetriever(new FromDiskResourceRetriever(SOURCE_FOLDER_LOTL_FILES));
        EuropeanTrustedListConfigurationFactory.setFactory(() -> new EuropeanTrustedListConfigurationFactory() {
            @Override
            public String getTrustedListUri() {
                return "https://eidas.ec.europa.eu/efda/api/v2/validation-tests/testcase/tl/LOTL-3.xml";
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

    //3. Matching SDI + Sti/aSI + status
    //3.1 matching service

    @Test
    public void trustedCertificateNotPresentInTLTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_3.1.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_APPLICABLE, conclusion);
    }

    @Test
    public void notQualifiedServiceTypeTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_3.1.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_APPLICABLE, conclusion);
    }

    @Test
    public void noMatchingServiceInformationTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_3.1.3.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_APPLICABLE, conclusion);
    }

    @Test
    public void serviceWithdrawnTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_3.1.4.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void supervisionCeasedServiceStatusBeforeEIDASTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_3.1.5.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRE_EIDAS_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void accreditationRevokedServiceStatusBeforeEIDASTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_3.1.6.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRE_EIDAS_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    // 3.2 matching service

    @Test
    public void standardCaseESigTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_3.2.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void noMatchingTSPNameTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_3.2.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    //3.3.Incoherences in TL

    @Test
    public void severalTLEntriesWithSameResultTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_3.3.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    //4. QC / notQC
    //4.1.notQC based on sigCert content

    @Test
    public void signingCertificateNotDeclaredQcTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.1.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void certPolicyBeforeEidasQcpPlusTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.1.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRE_EIDAS_DATE2);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void certPolicyBeforeEidasQcpAndQscdTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.1.3.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRE_EIDAS_DATE2);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void certPolicyBeforeEidasQcpTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.1.4.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRE_EIDAS_DATE2);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void certPolicyAfterEidasQcpTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.1.5.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        // Sat Jun 06 15:00:00 MSK 2020
        Date dateTime = DateTimeUtil.createUtcDateTime(2020, 5, 6, 15, 0, 0);

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, dateTime);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    // This certificate policy extension contains weird id, which is not recognizable.
    // The expected result is correct, but test name is somewhat confusing.
    public void certPolicyAfterEidasQcpAndQscdTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.1.6.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        // Sat Jun 06 15:00:00 MSK 2020
        Date dateTime = DateTimeUtil.createUtcDateTime(2020, 5, 6, 15, 0, 0);

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, dateTime);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void certPolicyBeforeEidasQcpAndQcpPlusTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.1.7.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRE_EIDAS_DATE2);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    // 4.2.Overrule to notQC by Sie:Q in TL

    @Test
    public void notQualifiedOverruleInTLCatchingTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.2.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESEAL, conclusion);
    }

    @Test
    public void notQualifiedOverruleInTLNotCatchingTypeTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.2.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void notQualifiedOverruleInTLNotCatchingCriteriaTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.2.3.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    // 4.3.Overrule to QC by Sie:Q in TL

    @Test
    public void qualifiedOverruleInTLCatchingTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.3.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingWithoutTypeTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.3.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.UNKNOWN_QC, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingWithSIESigAndESealTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.3.3.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.UNKNOWN_QC_AND_QSCD, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingWithAdditionalQcESigTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.3.4.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLNotCatchingTypeTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.3.5.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        // The expected result from their side is "AdESeal". We get NOT_APPLICABLE,
        // because when no CA/QC catches signing cert, we don't say any information about the type.
        // Also, it's kind of a bad test from their side, not because of this,
        // but because it's not only not catching the signing cert because of the SI type, but also because of the criteria.
        // So it will fail even when type catching is ignored.
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_APPLICABLE, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLNotCatchingCriteriaTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.3.6.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESEAL, conclusion);
    }

    // 4.4.Overrule to QC with complex catching logic

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest1() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest2() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest3() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.3.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest4() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.4.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest5() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.5.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest6() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.6.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest7() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.7.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest8() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.8.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest9() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.9.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest10() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.10.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest11() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.11.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest12() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.12.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest13() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.13.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest14() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.14.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest15() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.15.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest16() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.16.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest17() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.17.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest18() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.18.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest19() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.19.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest20() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.20.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void qualifiedOverruleInTLCatchingComplexPolicyTest21() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.4.21.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    //4.5.Incoherences in TL

    @Test
    public void notQualifiedAndQualifiedInTlTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.5.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void notQualifiedAndQualifiedInSITest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.5.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void notQualifiedAndQualifiedInTwoElementsTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.5.3.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void certPolicyBeforeEidasStatusSupervisionTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_4.5.4.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_APPLICABLE, conclusion);
    }

    // 5. Type
    // 5.1.No overrule. Based on sigCert content

    @Test
    public void standardCaseEsealTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_5.1.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESEAL_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void wsaTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_5.1.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED, conclusion);
    }

    @Test
    public void multipleSIExtesnionsTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_5.1.3.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void noTypeInSigCertTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_5.1.4.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void multipleTypesInSigCertQCTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_5.1.5.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.UNKNOWN_QC, conclusion);
    }

    @Test
    public void multipleTypesInSigCertNotQCTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_5.1.6.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.UNKNOWN, conclusion);
    }

    // 5.2.Overrule of type by Sie:Q in TL

    @Test
    public void standardOverruleQcTypeIgnoredTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_5.2.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void typeOverruleCaughtButNotAppliedBcsNotQcTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_5.2.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESEAL, conclusion);
    }

    @Test
    public void typeOverruleCaughtButNotAppliedBcsOverruledToNotQcTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_5.2.3.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.UNKNOWN, conclusion);
    }

    @Test
    public void typeOverruleCaughtBcsOverruledToQcTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_5.2.4.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    // 5.3.Incoherences in TL

    @Test
    public void qcForXXNotAllignedWithSITest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_5.3.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_APPLICABLE, conclusion);
    }

    @Test
    public void twoOverruledTypesTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_5.3.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        // The expected result here is "N/A". No idea why they have N/A here. Seems to be UNKNOWN_QC_AND_QSCD.
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.UNKNOWN_QC_AND_QSCD, conclusion);
    }

    @Test
    public void twoConflictingTLValueWithOneOverruleTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_5.3.3.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_APPLICABLE, conclusion);
    }

    // 6. QSCD / no QSCD
    // 6.1.Certificate policies in sigCert

    @Test
    public void certPolicyQcpBeforeEidasTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.1.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRE_EIDAS_DATE2);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void certPolicyQcpPlusBeforeEidasTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.1.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRE_EIDAS_DATE2);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void certPolicyAfterEidasQCPAndQSCDTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.1.3.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    // 6.2.Overrule to QSCD by Sie:Q in TL

    @Test
    public void overruleToQscdNotCatchingTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.2.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void overruleToQscdNotApplyingBcsNotQcTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.2.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void overruleToQscdNotApplyingBcsOverruleToNotQcTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.2.3.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void sscdBeforeEidasTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.2.4.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRE_EIDAS_DATE2);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void qscdAfterEidasTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.2.5.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    //6.3.Overrule to no QSCD by Sie:Q in TL

    @Test
    public void overruleToNotQscdNotCatchingTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.3.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void noQscdBeforeEidasTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.3.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRE_EIDAS_DATE2);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void noQscdAfterEidasTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.3.3.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void qscdManagedOnBehalfTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.3.4.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    //6.4.Incoherences in TL

    @Test
    public void qscdAndNoQscdTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.4.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void sscdAndNoSscdTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.4.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void qscdStatusAsInCertAndQscdTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.4.3.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void sscdStatusAsInCertAndSscdTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.4.4.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void qscdBeforeEidasTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.4.5.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRE_EIDAS_DATE2);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void sscdAfterEidasTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.4.6.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void notQscdBeforeEidasTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.4.7.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRE_EIDAS_DATE2);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    @Test
    public void notSscdAfterEidasTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_6.4.8.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    // 7. Discrepancy betw. time of issuance & time of signing
    // 7.1.QC / notQC
    // In this paragraph our results are different from those posted as expected in the original test suite.
    // Our understanding is that there is something wrong with the test suite.

    @Test
    public void qcAtIssuingNotQcAtSigningTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_7.1.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.NOT_QUALIFIED_ESIG, conclusion);
    }

    @Test
    public void notQcAtIssuingQcAtSigningTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_7.1.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    //7.2.Type

    @Test
    public void eSigAtIssuingESealAtSigningTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_7.2.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESEAL_WITH_QC_AND_QSCD, conclusion);
    }

    //7.3.QSCD / no QSCD

    @Test
    public void noQscdAtIssuingQscdAtSigningTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_7.3.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }

    @Test
    public void qscdAtIssuingNoQscdAtSigningTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_7.3.2.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC, conclusion);
    }

    //7.4.Before / after eIDAS

    @Test
    public void beforeEidasAtIssuingAfterEidasAtSigningTest() throws CertificateException, IOException {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.trustEuropeanLotl(true);
        QualifiedValidator qualifiedValidator = new QualifiedValidator();
        qualifiedValidator.startSignatureValidation("signature1");
        chainBuilder.withQualifiedValidator(qualifiedValidator);

        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS + "certificate_7.4.1.pem");
        X509Certificate signCertificate = (X509Certificate) certChain[0];
        X509Certificate trustedCert = (X509Certificate) certChain[certChain.length - 1];

        LotlTrustedStore trustedStore = chainBuilder.getLotlTrustedStore();
        trustedStore.setPreviousCertificates(Collections.singletonList(signCertificate)).checkIfCertIsTrusted(new ValidationReport(), SIGN_CONTEXT, trustedCert, PRESENT_DATE);

        QualifiedValidator.QualificationConclusion conclusion =
                qualifiedValidator.obtainQualificationValidationResultForSignature("signature1").getQualificationConclusion();
        Assertions.assertEquals(QualifiedValidator.QualificationConclusion.ESIG_WITH_QC_AND_QSCD, conclusion);
    }
}
