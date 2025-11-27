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
package com.itextpdf.signatures.validation.report.pades;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.TimestampConstants;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.AdvancedTestOcspClient;
import com.itextpdf.signatures.validation.SignatureValidationProperties;
import com.itextpdf.signatures.validation.SignatureValidator;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.CertificateSources;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.TimeBasedContexts;
import com.itextpdf.signatures.validation.context.ValidatorContexts;
import com.itextpdf.test.ExtendedITextTest;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Calendar;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleIntegrationTest")
public class PAdESLevelIntegrationTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/report/pades/PAdESLevelIntegrationTest/";
    private static final String CERT_SOURCE =
            "./src/test/resources/com/itextpdf/signatures/validation/report/pades/certs/";
    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    private static final IssuingCertificateRetriever CERTIFICATE_RETRIEVER = new IssuingCertificateRetriever();
    private static final AdvancedTestOcspClient TEST_OCSP_CLIENT = new AdvancedTestOcspClient();
    private PAdESLevelReportGenerator reportGenerator;
    private ValidatorChainBuilder builder;


    @BeforeAll
    public static void setUpOnce()
            throws CertificateException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        addCertInfo(CERT_SOURCE + "tsCertRsa.pem", CERT_SOURCE + "rootRsa.pem");
        addCertInfo(CERT_SOURCE + "signCertOcspNoCrl.pem", CERT_SOURCE + "rootCertNoCrlNoOcsp.pem");
        addCertInfo(CERT_SOURCE + "advancedTsCert.pem", CERT_SOURCE + "rootCertNoCrlNoOcsp.pem");
    }

    private static void addCertInfo(String certFile, String rootCertFile)
            throws CertificateException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        X509Certificate cert = (X509Certificate) PemFileHelper.readFirstChain(certFile)[0];
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFile)[0];
        CERTIFICATE_RETRIEVER.addTrustedCertificates(Collections.singletonList(caCert));
        PrivateKey pk = PemFileHelper.readFirstKey(rootCertFile, PASSWORD);
        TestOcspResponseBuilder responseBuilder = new TestOcspResponseBuilder(caCert, pk);
        responseBuilder.setNextUpdate((Calendar) TimestampConstants.UNDEFINED_TIMESTAMP_DATE);
        TEST_OCSP_CLIENT.addBuilderForCertIssuer(cert, responseBuilder);
    }

    @BeforeEach
    public void setUp() {
        builder = new ValidatorChainBuilder();
        builder.withIssuingCertificateRetrieverFactory(() -> CERTIFICATE_RETRIEVER);
        builder.withOcspClient(() -> TEST_OCSP_CLIENT);
        reportGenerator = new PAdESLevelReportGenerator();
        builder.withPAdESLevelReportGenerator(reportGenerator);
        SignatureValidationProperties properties = new SignatureValidationProperties();
        properties.setFreshness(ValidatorContexts.all(), CertificateSources.all(),
                TimeBasedContexts.all(), Duration.ofDays(99999));
        builder.withSignatureValidationProperties(properties);
    }

    @Test
    public void bBTest() throws IOException {
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "B-B.pdf");
             PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_B, report.getDocumentLevel());
            Assertions.assertTrue(report.getSignatureReport("Signature1").getNonConformaties()
                    .get(PAdESLevel.B_T).stream().anyMatch(
                    nc -> AbstractPadesLevelRequirements.
                            THERE_MUST_BE_A_SIGNATURE_OR_DOCUMENT_TIMESTAMP_AVAILABLE.equals(nc)
            ));
            Assertions.assertTrue(report.getSignatureReport("Signature1").getWarnings().
                    get(PAdESLevel.B_B).stream().anyMatch(
                    w->  AbstractPadesLevelRequirements.
                            SIGNED_DATA_CERTIFICATES_SHOULD_INCLUDE_THE_ENTIRE_CERTIFICATE_CHAIN_AND_INCLUDE_CA
                            .equals(w)
            ));
        }
    }

    @Test
    public void bTTest() throws IOException {
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "B-T.pdf");
             PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_T, report.getDocumentLevel());
            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getNonConformaties().get(PAdESLevel.B_LT).stream()
                    .filter(nc-> AbstractPadesLevelRequirements.DSS_DICTIONARY_IS_MISSING.equals(nc))
                    .count());
            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getNonConformaties().get(PAdESLevel.B_LT).size());

            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_T).size());
            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_B).size());

        }
    }

    @Test
    public void bLTTest() throws IOException {
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "B-LT.pdf");
             PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_LT, report.getDocumentLevel());
            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getNonConformaties().get(PAdESLevel.B_LTA).stream()
                    .filter(nc -> AbstractPadesLevelRequirements.DSS_IS_NOT_COVERED_BY_TIMESTAMP.equals(nc))
                    .count());
            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getNonConformaties().get(PAdESLevel.B_LTA).stream()
                    .filter(nc -> AbstractPadesLevelRequirements.DOCUMENT_TIMESTAMP_IS_MISSING.equals(nc))
                    .count());
            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_LT).size());
            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_T).size());
            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_B).size());

        }
    }

    @Test
    public void testBLTCoveredWithTimestampAttributeTest() throws IOException {
        // In this test completely valid B-LT signature is covered by another signature, which contains timestamp attribute.
        // Such signature by itself is expected to have B-T level, but more importantly previous signature level shall remain B-LT.
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "B-LT_covered_with_timestamp_attribute.pdf");
             PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_LT, report.getSignatureReport("Signature1").getLevel());
            Assertions.assertEquals(PAdESLevel.B_T, report.getSignatureReport("Signature2").getLevel());

            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getNonConformaties().get(PAdESLevel.B_LTA).stream()
                    .filter(nc -> AbstractPadesLevelRequirements.DSS_IS_NOT_COVERED_BY_TIMESTAMP.equals(nc))
                    .count());
            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getNonConformaties().get(PAdESLevel.B_LTA).stream()
                    .filter(nc -> AbstractPadesLevelRequirements.DOCUMENT_TIMESTAMP_IS_MISSING.equals(nc)).count());
            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_LT).size());
            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_T).size());
            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_B).size());

            Assertions.assertEquals(1, report.getSignatureReport("Signature2")
                    .getNonConformaties().get(PAdESLevel.B_LT).stream()
                    .filter(nc-> AbstractPadesLevelRequirements.DSS_DICTIONARY_IS_MISSING.equals(nc))
                    .count());
            Assertions.assertEquals(1, report.getSignatureReport("Signature2")
                    .getNonConformaties().get(PAdESLevel.B_LT).size());

            Assertions.assertEquals(0, report.getSignatureReport("Signature2")
                    .getWarnings().get(PAdESLevel.B_T).size());
            Assertions.assertEquals(1, report.getSignatureReport("Signature2")
                    .getWarnings().get(PAdESLevel.B_B).size());

        }
    }

    @Test
    public void testBLTCoveredWithLTATest() throws IOException {
        // In this test completely valid B-LT signature is covered by a complete B-LTA structure.
        // Such operation makes the original signature B-LTA as well.
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "B-LT_covered_with_LTA.pdf");
             PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_LTA, report.getSignatureReport("Signature1").getLevel());
            Assertions.assertEquals(PAdESLevel.B_LTA, report.getSignatureReport("Signature2").getLevel());

            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_LTA).size());
            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_LT).size());
            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_T).size());
            Assertions.assertEquals(2, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_B).size());

            Assertions.assertEquals(0, report.getSignatureReport("Signature2")
                    .getWarnings().get(PAdESLevel.B_LTA).size());
            Assertions.assertEquals(0, report.getSignatureReport("Signature2")
                    .getWarnings().get(PAdESLevel.B_LT).size());
            Assertions.assertEquals(0, report.getSignatureReport("Signature2")
                    .getWarnings().get(PAdESLevel.B_T).size());
            Assertions.assertEquals(2, report.getSignatureReport("Signature2")
                    .getWarnings().get(PAdESLevel.B_B).size());
        }
    }

    @Test
    public void testBLTTimestampRevDataMissingTest() throws IOException {
        // In this test B-LT signature lacks timestamp attribute revocation data in the DSS, which results in B-T level.
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "B-LT_timestamp_rev_data_missing.pdf");
             PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_T, report.getSignatureReport("Signature1").getLevel());

            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getNonConformaties().get(PAdESLevel.B_LT).stream().filter(nc->
                            nc.contains(
                                    AbstractPadesLevelRequirements.REVOCATION_DATA_FOR_THESE_CERTIFICATES_IS_MISSING))
                    .count());
            // There should be only one certificate listed
            // each listed certificate contains a SerialNumber
            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getNonConformaties().get(PAdESLevel.B_LT).stream().filter(nc-> nc.contains("SerialNumber"))
                    .count());
            // it should be the correct certificate
            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getNonConformaties().get(PAdESLevel.B_LT).stream()
                    .filter(nc-> nc.contains("iTextAdvancedTSTest")).count());
        }
    }

    @Test
    public void bLTATest() throws IOException {
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "B-LTA.pdf");
             PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_LTA).size());
            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_LT).size());
            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_T).size());
            Assertions.assertEquals(2, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_B).size());
        }
    }

    @Test
    public void bLTAWithMissingRevDataTest() throws IOException {
        // In this test B-LTA signature doesn't have timestamp attribute revocation data in the DSS.
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "B-LTA_with_missing_rev_data.pdf");
             PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_T, report.getDocumentLevel());
            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getNonConformaties().get(PAdESLevel.B_LT).stream().filter(nc->
                            nc.contains(
                                    AbstractPadesLevelRequirements.REVOCATION_DATA_FOR_THESE_CERTIFICATES_IS_MISSING))
                    .count());
            // There should be only one certificated listed
            // each listed certificate contains a SerialNumber
            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getNonConformaties().get(PAdESLevel.B_LT).stream().filter(nc-> nc.contains("SerialNumber"))
                    .count());
            // it should be the correct certificate
            Assertions.assertEquals(1, report.getSignatureReport("Signature1")
                    .getNonConformaties().get(PAdESLevel.B_LT).stream()
                    .filter(nc-> nc.contains("iTextAdvancedTSTest")).count());
        }
    }

    @Test
    public void bLTAWithRevDataAfterTimestampTest() throws IOException {
        // In this test B-LTA signature doesn't have timestamp attribute revocation data in the DSS,
        // but it's added after the document timestamp. The expected level is B-LT.
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "B-LTA_with_rev_data_after_timestamp.pdf");
             PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_LT, report.getDocumentLevel());
        }
    }

    @Test
    public void bLTAWithMultipleProlongationsTest() throws IOException {
        // In this document prolongation is called multiple times after the B-LTA signature,
        // which includes DSS update and document timestamp. The expected result is B-LTA.
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "B-LTA_with_multiple_prolongations.pdf");
             PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
        }
    }

    @Test
    public void bLTAWithMultipleTimestampsTest() throws IOException {
        // In this document on top of B-LTA signature multiple timestamps are added, the expected result is B-LTA.
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "B-LTA_with_multiple_timestamps.pdf");
             PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_LTA).size());
            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_LT).size());
            Assertions.assertEquals(0, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_T).size());
            Assertions.assertEquals(2, report.getSignatureReport("Signature1")
                    .getWarnings().get(PAdESLevel.B_B).size());
        }
    }
}
