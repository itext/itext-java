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
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.AdvancedTestOcspClient;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.validation.SignatureValidator;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PAdESLevelIntegrationTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/report/pades/";
    private static final String CERT_SOURCE =
            "./src/test/resources/com/itextpdf/signatures/validation/report/pades/certs/";
    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    private static IssuingCertificateRetriever certificateRetriever;
    private static AdvancedTestOcspClient testOcspClient;
    private static TestCrlClient testCrlClient;


    private PAdESLevelReportGenerator reportGenerator;
    private ValidatorChainBuilder builder;


    @BeforeAll
    public static void setUpOnce()
            throws CertificateException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        certificateRetriever = new IssuingCertificateRetriever();
        testOcspClient = new AdvancedTestOcspClient();
        testCrlClient = new TestCrlClient();
        addRootCertInfo(CERT_SOURCE + "rootCertCrlNoOcsp.pem",0);
        addRootCertInfo(CERT_SOURCE + "rootCertCrlOcsp.pem",0);
        addRootCertInfo(CERT_SOURCE + "rootCertNoCrlNoOcsp.pem",0);
        addRootCertInfo(CERT_SOURCE + "rootCertOcspNoCrl.pem",0);
        addRootCertInfo(CERT_SOURCE + "signCertCrlNoOcsp.pem",0);
        addRootCertInfo(CERT_SOURCE + "signCertNoOcspNoCrl.pem",0);
        addRootCertInfo(CERT_SOURCE + "signCertOcspNoCrl.pem",0);
        addRootCertInfo(CERT_SOURCE + "tsCertRsa.pem",0);
    }

    private static void addRootCertInfo(String pemFile, int rootCertIndex)
            throws CertificateException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        X509Certificate caCert = (X509Certificate)PemFileHelper.readFirstChain(pemFile)[rootCertIndex];
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));
        PrivateKey pk = PemFileHelper.readFirstKey( pemFile, PASSWORD);
        testOcspClient.addBuilderForCertIssuer(caCert, new TestOcspResponseBuilder(caCert, pk));
        testCrlClient.addBuilderForCertIssuer(caCert, pk);
    }

    @BeforeEach
    public void setUp() {
        builder = new ValidatorChainBuilder();
        builder.withIssuingCertificateRetrieverFactory(() -> certificateRetriever);
        builder.withOcspClient(() -> testOcspClient);
        reportGenerator = new PAdESLevelReportGenerator();
        builder.withPAdESLevelReportGenerator(reportGenerator);
    }

      @Test
    public void testBB() throws IOException {
        try (PdfReader reader = new PdfReader( SOURCE_FOLDER+"B-B.pdf");
                PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            ValidationReport validationReport = validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_B, report.getDocumentLevel());
        }
    }
  @Test
    public void testBT() throws IOException {
        try (PdfReader reader = new PdfReader( SOURCE_FOLDER+"B-T.pdf");
                PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            ValidationReport validationReport = validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_T, report.getDocumentLevel());
        }
    }

    @Test
    public void testBLT() throws IOException {
        try (PdfReader reader = new PdfReader( SOURCE_FOLDER+"B-LT.pdf");
                PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            ValidationReport validationReport = validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_T, report.getDocumentLevel());
        }
    }

 @Test
    public void testBLTA() throws IOException {
        try (PdfReader reader = new PdfReader( SOURCE_FOLDER+"B-LTA.pdf");
                PdfDocument doc = new PdfDocument(reader)) {
            SignatureValidator validator = builder.buildSignatureValidator(doc);
            ValidationReport validationReport = validator.validateSignatures();
            DocumentPAdESLevelReport report = reportGenerator.getReport();
            Assertions.assertEquals(PAdESLevel.B_T, report.getDocumentLevel());
        }
    }


}
