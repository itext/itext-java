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
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.validation.ValidationReport.ValidationResult;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleIntegrationTest.class)
public class SignatureValidatorTest extends ExtendedITextTest {
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/validation/SignatureValidatorTest/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/SignatureValidatorTest/";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @Test
    public void validLatestSignatureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDoc.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(1, report.getLogs().size());

        CertificateReportItem item = report.getCertificateLogs().get(0);
        Assert.assertEquals(rootCert, item.getCertificate());
        Assert.assertEquals("Certificate check.", item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), item.getMessage());
    }

    @Test
    public void latestSignatureIsTimestampTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "timestampSignatureDoc.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(1, report.getLogs().size());

        CertificateReportItem item = report.getCertificateLogs().get(0);
        Assert.assertEquals(rootCert, item.getCertificate());
        Assert.assertEquals("Certificate check.", item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), item.getMessage());
    }

    @Test
    public void validLatestSignatureWithTimestampTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDocWithTimestamp.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(2, report.getLogs().size());

        CertificateReportItem item1 = report.getCertificateLogs().get(0);
        Assert.assertEquals(rootCert, item1.getCertificate());
        Assert.assertEquals("Certificate check.", item1.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), item1.getMessage());

        CertificateReportItem item2 = report.getCertificateLogs().get(1);
        Assert.assertEquals(rootCert, item2.getCertificate());
        Assert.assertEquals("Certificate check.", item2.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), item2.getMessage());
    }

    @Test
    public void latestSignatureWithBrokenTimestampTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "docWithBrokenTimestamp.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(3, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));

        ReportItem failure = report.getFailures().get(0);
        Assert.assertEquals(SignatureValidator.TIMESTAMP_VERIFICATION, failure.getCheckName());
        Assert.assertEquals(SignatureValidator.CANNOT_VERIFY_TIMESTAMP, failure.getMessage());

        CertificateReportItem item1 = report.getCertificateLogs().get(0);
        Assert.assertEquals(rootCert, item1.getCertificate());
        Assert.assertEquals("Certificate check.", item1.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), item1.getMessage());

        CertificateReportItem item2 = report.getCertificateLogs().get(1);
        Assert.assertEquals(rootCert, item2.getCertificate());
        Assert.assertEquals("Certificate check.", item2.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), item2.getMessage());
    }

    @Test
    public void documentModifiedLatestSignatureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "modifiedDoc.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(2, report.getFailures().size());
        Assert.assertEquals(3, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));
        Assert.assertEquals(report.getFailures().get(1), report.getLogs().get(1));

        ReportItem item1 = report.getFailures().get(0);
        Assert.assertEquals(SignatureValidator.SIGNATURE_VERIFICATION, item1.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                SignatureValidator.DOCUMENT_IS_NOT_COVERED, "Signature1"), item1.getMessage());

        ReportItem item2 = report.getFailures().get(1);
        Assert.assertEquals(SignatureValidator.SIGNATURE_VERIFICATION, item2.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                SignatureValidator.CANNOT_VERIFY_SIGNATURE, "Signature1"), item2.getMessage());
    }

    @Test
    public void latestSignatureInvalidStopValidationTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "modifiedDoc.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));
            signatureValidator.proceedValidationAfterFail(false);

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(2, report.getFailures().size());
        Assert.assertEquals(2, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));
        Assert.assertEquals(report.getFailures().get(1), report.getLogs().get(1));

        ReportItem item1 = report.getFailures().get(0);
        Assert.assertEquals(SignatureValidator.SIGNATURE_VERIFICATION, item1.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                SignatureValidator.DOCUMENT_IS_NOT_COVERED, "Signature1"), item1.getMessage());

        ReportItem item2 = report.getFailures().get(1);
        Assert.assertEquals(SignatureValidator.SIGNATURE_VERIFICATION, item2.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                SignatureValidator.CANNOT_VERIFY_SIGNATURE, "Signature1"), item2.getMessage());
    }

    @Test
    public void certificatesNotInLatestSignatureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDocWithoutChain.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.INDETERMINATE, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));

        CertificateReportItem item = report.getCertificateFailures().get(0);
        Assert.assertEquals(signingCert, item.getCertificate());
        Assert.assertEquals("Certificate check.", item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} isn't trusted and issuer certificate isn't provided.",
                signingCert.getSubjectX500Principal()), item.getMessage());
    }

    @Test
    public void certificatesNotInLatestSignatureButSetAsKnownTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDocWithoutChain.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));
            signatureValidator.setKnownCertificates(Collections.singletonList(intermediateCert));

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(1, report.getLogs().size());

        CertificateReportItem item = report.getCertificateLogs().get(0);
        Assert.assertEquals(rootCert, item.getCertificate());
        Assert.assertEquals("Certificate check.", item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), item.getMessage());
    }

    @Test
    public void certificatesNotInLatestSignatureButTakenFromDSSTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "docWithDss.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());

        CertificateReportItem item = report.getCertificateLogs().get(0);
        Assert.assertEquals(rootCert, item.getCertificate());
        Assert.assertEquals("Certificate check.", item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), item.getMessage());
    }

    @Test
    public void certificatesNotInLatestSignatureButTakenFromDSSOneCertIsBrokenTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "docWithBrokenDss.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(2, report.getLogs().size());

        ReportItem reportItem = report.getLogs().get(0);
        Assert.assertEquals(SignatureValidator.CERTS_FROM_DSS, reportItem.getCheckName());
        Assert.assertTrue(reportItem.getExceptionCause() instanceof GeneralSecurityException);

        CertificateReportItem item = report.getCertificateLogs().get(0);
        Assert.assertEquals(rootCert, item.getCertificate());
        Assert.assertEquals("Certificate check.", item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), item.getMessage());
    }

    @Test
    public void rootIsNotTrustedInLatestSignatureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDoc.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.INDETERMINATE, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));

        CertificateReportItem item = report.getCertificateFailures().get(0);
        Assert.assertEquals(rootCert, item.getCertificate());
        Assert.assertEquals("Certificate check.", item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} isn't trusted and issuer certificate isn't provided.",
                rootCert.getSubjectX500Principal()), item.getMessage());
    }
}
