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
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;
import com.itextpdf.signatures.validation.report.CertificateReportItem;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleIntegrationTest.class)
public class SignatureValidatorTest extends ExtendedITextTest {
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/validation/SignatureValidatorTest/certs/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/SignatureValidatorTest/";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @Test
    public void validLatestSignatureTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDoc.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));
            addRevDataClients(signatureValidator);

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(3, report.getLogs().size());

        for (int i = 0; i < 3; ++i) {
            CertificateReportItem item = report.getCertificateLogs().get(i);
            Assert.assertEquals(rootCert, item.getCertificate());
            Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                    rootCert.getSubjectX500Principal()), item.getMessage());
        }
    }

    @Test
    public void latestSignatureIsTimestampTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        String privateKeyName = CERTS_SRC + "rootCertKey.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];
        PrivateKey rootPrivateKey = PemFileHelper.readFirstKey(privateKeyName, PASSWORD);

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "timestampSignatureDoc.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));

            TestOcspResponseBuilder builder = new TestOcspResponseBuilder(rootCert, rootPrivateKey);
            Date currentDate = DateTimeUtil.getCurrentTimeDate();
            builder.setProducedAt(currentDate);
            builder.setThisUpdate(DateTimeUtil.getCalendar(currentDate));
            builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(currentDate, 30)));
            TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(rootCert, builder);
            signatureValidator.getCertificateChainValidator().addOcspClient(ocspClient).getRevocationDataValidator()
                    .setOnlineFetching(RevocationDataValidator.OnlineFetching.NEVER_FETCH);

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(2, report.getLogs().size());

        CertificateReportItem item = report.getCertificateLogs().get(0);
        Assert.assertEquals(rootCert, item.getCertificate());
        Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                rootCert.getSubjectX500Principal()), item.getMessage());
        item = report.getCertificateLogs().get(1);
        Assert.assertEquals(rootCert, item.getCertificate());
        Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                rootCert.getSubjectX500Principal()), item.getMessage());
    }

    @Test
    public void validLatestSignatureWithTimestampTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDocWithTimestamp.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));
            addRevDataClients(signatureValidator);

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(5, report.getLogs().size());

        for (int i = 0; i < 5; ++i) {
            CertificateReportItem item1 = report.getCertificateLogs().get(i);
            Assert.assertEquals(rootCert, item1.getCertificate());
            Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item1.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                    rootCert.getSubjectX500Principal()), item1.getMessage());
        }
    }

    @Test
    public void latestSignatureWithBrokenTimestampTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "docWithBrokenTimestamp.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));
            addRevDataClients(signatureValidator);

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(6, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));

        ReportItem failure = report.getFailures().get(0);
        Assert.assertEquals(SignatureValidator.TIMESTAMP_VERIFICATION, failure.getCheckName());
        Assert.assertEquals(SignatureValidator.CANNOT_VERIFY_TIMESTAMP, failure.getMessage());

        for (int i = 0; i < 5; ++i) {
            CertificateReportItem item1 = report.getCertificateLogs().get(i);
            Assert.assertEquals(rootCert, item1.getCertificate());
            Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item1.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                    rootCert.getSubjectX500Principal()), item1.getMessage());
        }
    }

    @Test
    public void documentModifiedLatestSignatureTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "modifiedDoc.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));
            addRevDataClients(signatureValidator);

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(2, report.getFailures().size());
        Assert.assertEquals(5, report.getLogs().size());
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

        for (int i = 0; i < 3; ++i) {
            CertificateReportItem item = report.getCertificateLogs().get(i);
            Assert.assertEquals(rootCert, item.getCertificate());
            Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                    rootCert.getSubjectX500Principal()), item.getMessage());
        }
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
            signatureValidator.getCertificateChainValidator().getRevocationDataValidator()
                    .setOnlineFetching(RevocationDataValidator.OnlineFetching.NEVER_FETCH);

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.INDETERMINATE, report.getValidationResult());
        Assert.assertEquals(2, report.getFailures().size());
        Assert.assertEquals(2, report.getLogs().size());

        CertificateReportItem item = report.getCertificateFailures().get(0);
        Assert.assertEquals(signingCert, item.getCertificate());
        Assert.assertEquals(RevocationDataValidator.REVOCATION_DATA_CHECK, item.getCheckName());
        Assert.assertEquals(RevocationDataValidator.NO_REVOCATION_DATA, item.getMessage());
        item = report.getCertificateFailures().get(1);
        Assert.assertEquals(signingCert, item.getCertificate());
        Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.ISSUER_MISSING,
                signingCert.getSubjectX500Principal()), item.getMessage());
    }

    @Test
    public void certificatesNotInLatestSignatureButSetAsKnownTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDocWithoutChain.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));
            signatureValidator.setKnownCertificates(Collections.singletonList(intermediateCert));
            addRevDataClients(signatureValidator);

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(3, report.getLogs().size());

        for (int i = 0; i < 3; ++i) {
            CertificateReportItem item = report.getCertificateLogs().get(i);
            Assert.assertEquals(rootCert, item.getCertificate());
            Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                    rootCert.getSubjectX500Principal()), item.getMessage());
        }
    }

    @Test
    public void certificatesNotInLatestSignatureButTakenFromDSSTest() throws GeneralSecurityException, IOException,
            AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "docWithDss.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));
            addRevDataClients(signatureValidator);

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(3, report.getLogs().size());

        for (int i = 0; i < 3; ++i) {
            CertificateReportItem item = report.getCertificateLogs().get(i);
            Assert.assertEquals(rootCert, item.getCertificate());
            Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                    rootCert.getSubjectX500Principal()), item.getMessage());
        }
    }

    @Test
    public void certificatesNotInLatestSignatureButTakenFromDSSOneCertIsBrokenTest() throws GeneralSecurityException,
            IOException, AbstractOperatorCreationException, AbstractPKCSException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "docWithBrokenDss.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.setTrustedCertificates(Collections.singletonList(rootCert));
            addRevDataClients(signatureValidator);

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(4, report.getLogs().size());

        ReportItem reportItem = report.getLogs().get(0);
        Assert.assertEquals(SignatureValidator.CERTS_FROM_DSS, reportItem.getCheckName());
        Assert.assertTrue(reportItem.getExceptionCause() instanceof GeneralSecurityException);

        for (int i = 0; i < 3; ++i) {
            CertificateReportItem item = report.getCertificateLogs().get(i);
            Assert.assertEquals(rootCert, item.getCertificate());
            Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                    rootCert.getSubjectX500Principal()), item.getMessage());
        }
    }

    @Test
    public void rootIsNotTrustedInLatestSignatureTest() throws GeneralSecurityException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        ValidationReport report;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "validDoc.pdf"))) {
            SignatureValidator signatureValidator = new SignatureValidator(document);
            signatureValidator.getCertificateChainValidator().getRevocationDataValidator()
                    .setOnlineFetching(RevocationDataValidator.OnlineFetching.NEVER_FETCH);

            report = signatureValidator.validateLatestSignature();
        }

        Assert.assertEquals(ValidationResult.INDETERMINATE, report.getValidationResult());
        Assert.assertEquals(3, report.getFailures().size());
        Assert.assertEquals(4, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));
        Assert.assertEquals(report.getFailures().get(1), report.getLogs().get(1));
        Assert.assertEquals(report.getFailures().get(2), report.getLogs().get(3));

        CertificateReportItem item = report.getCertificateFailures().get(0);
        Assert.assertEquals(certificateChain[0], item.getCertificate());
        Assert.assertEquals(RevocationDataValidator.REVOCATION_DATA_CHECK, item.getCheckName());
        Assert.assertEquals(RevocationDataValidator.NO_REVOCATION_DATA, item.getMessage());
        item = report.getCertificateFailures().get(1);
        Assert.assertEquals(certificateChain[1], item.getCertificate());
        Assert.assertEquals(RevocationDataValidator.REVOCATION_DATA_CHECK, item.getCheckName());
        Assert.assertEquals(RevocationDataValidator.NO_REVOCATION_DATA, item.getMessage());

        item = report.getCertificateFailures().get(2);
        Assert.assertEquals(rootCert, item.getCertificate());
        Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.ISSUER_MISSING,
                rootCert.getSubjectX500Principal()), item.getMessage());
    }

    private void addRevDataClients(SignatureValidator signatureValidator)
            throws AbstractOperatorCreationException, IOException, AbstractPKCSException, CertificateException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        String privateKeyName = CERTS_SRC + "rootCertKey.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];
        PrivateKey rootPrivateKey = PemFileHelper.readFirstKey(privateKeyName, PASSWORD);

        Date currentDate = DateTimeUtil.getCurrentTimeDate();
        TestOcspResponseBuilder builder1 = new TestOcspResponseBuilder(rootCert, rootPrivateKey);
        builder1.setProducedAt(currentDate);
        builder1.setThisUpdate(DateTimeUtil.getCalendar(currentDate));
        builder1.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(currentDate, 30)));
        TestOcspResponseBuilder builder2 = new TestOcspResponseBuilder(rootCert, rootPrivateKey);
        builder2.setProducedAt(currentDate);
        builder2.setThisUpdate(DateTimeUtil.getCalendar(currentDate));
        builder2.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(currentDate, 30)));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(rootCert, builder1)
                .addBuilderForCertIssuer(intermediateCert, builder2);
        signatureValidator.getCertificateChainValidator().addOcspClient(ocspClient).getRevocationDataValidator()
                .setOnlineFetching(RevocationDataValidator.OnlineFetching.NEVER_FETCH);
    }
}
