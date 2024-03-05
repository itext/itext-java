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
import com.itextpdf.signatures.OID.X509Extensions;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.validation.ValidationReport.ValidationResult;
import com.itextpdf.signatures.validation.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.extensions.KeyUsage;
import com.itextpdf.signatures.validation.extensions.KeyUsageExtension;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleUnitTest.class)
public class CertificateChainValidatorTest extends ExtendedITextTest {
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/validation/CertificateChainValidatorTest/";

    @Test
    public void validChainTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = new CertificateChainValidator();
        validator.setKnownCertificates(Collections.singletonList(intermediateCert));
        validator.setTrustedCertificates(Collections.singletonList(rootCert));

        ValidationReport report =
                validator.validateCertificate(signingCert, DateTimeUtil.getCurrentTimeDate(), null);

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(1, report.getLogs().size());
        Assert.assertTrue(report.getFailures().isEmpty());

        CertificateReportItem item = report.getCertificateLogs().get(0);
        Assert.assertEquals(rootCert, item.getCertificate());
        Assert.assertEquals("Certificate check.", item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), item.getMessage());
    }

    @Test
    public void severalFailuresWithProceedAfterFailTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = new CertificateChainValidator();
        validator.setKnownCertificates(Collections.singletonList(intermediateCert));
        validator.setTrustedCertificates(Collections.singletonList(rootCert));
        validator.setGlobalRequiredExtensions(Collections.<CertificateExtension>singletonList(new KeyUsageExtension(KeyUsage.DIGITAL_SIGNATURE)));

        validator.proceedValidationAfterFail(true);

        ValidationReport report = validator.validateCertificate(signingCert, DateTimeUtil.getCurrentTimeDate(),
                Collections.<CertificateExtension>singletonList(new KeyUsageExtension(KeyUsage.DECIPHER_ONLY)));

        Assert.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(3, report.getFailures().size());
        Assert.assertEquals(4, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));
        Assert.assertEquals(report.getFailures().get(1), report.getLogs().get(1));
        Assert.assertEquals(report.getFailures().get(2), report.getLogs().get(2));

        CertificateReportItem log = report.getCertificateLogs().get(3);
        Assert.assertEquals(rootCert, log.getCertificate());
        Assert.assertEquals("Certificate check.", log.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), log.getMessage());

        CertificateReportItem failure1 = report.getCertificateFailures().get(0);
        Assert.assertEquals(signingCert, failure1.getCertificate());
        Assert.assertEquals("Required certificate extensions check.", failure1.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Required extension {0} is missing or incorrect.", X509Extensions.KEY_USAGE), failure1.getMessage());

        CertificateReportItem failure2 = report.getCertificateFailures().get(1);
        Assert.assertEquals(intermediateCert, failure2.getCertificate());
        Assert.assertEquals("Required certificate extensions check.", failure2.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Globally required extension {0} is missing or incorrect.", X509Extensions.KEY_USAGE), failure2.getMessage());

        CertificateReportItem failure3 = report.getCertificateFailures().get(2);
        Assert.assertEquals(rootCert, failure3.getCertificate());
        Assert.assertEquals("Required certificate extensions check.", failure3.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Globally required extension {0} is missing or incorrect.", X509Extensions.KEY_USAGE), failure3.getMessage());
    }

    @Test
    public void severalFailuresWithoutProceedAfterFailTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = new CertificateChainValidator();
        validator.setKnownCertificates(Collections.singletonList(intermediateCert));
        validator.setTrustedCertificates(Collections.singletonList(rootCert));
        validator.setGlobalRequiredExtensions(Collections.<CertificateExtension>singletonList(new KeyUsageExtension(KeyUsage.DIGITAL_SIGNATURE)));

        validator.proceedValidationAfterFail(false);

        ValidationReport report = validator.validateCertificate(signingCert, DateTimeUtil.getCurrentTimeDate(),
                Collections.<CertificateExtension>singletonList(new KeyUsageExtension(KeyUsage.DECIPHER_ONLY)));

        Assert.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));

        CertificateReportItem failure1 = report.getCertificateFailures().get(0);
        Assert.assertEquals(signingCert, failure1.getCertificate());
        Assert.assertEquals("Required certificate extensions check.", failure1.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Required extension {0} is missing or incorrect.", X509Extensions.KEY_USAGE), failure1.getMessage());
    }

    @Test
    public void intermediateCertTrustedTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];

        CertificateChainValidator validator = new CertificateChainValidator();
        validator.setTrustedCertificates(Collections.singletonList(intermediateCert));

        ValidationReport report =
                validator.validateCertificate(signingCert, DateTimeUtil.getCurrentTimeDate(), null);

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(1, report.getLogs().size());
        Assert.assertTrue(report.getFailures().isEmpty());

        CertificateReportItem item = report.getCertificateLogs().get(0);
        Assert.assertEquals(intermediateCert, item.getCertificate());
        Assert.assertEquals("Certificate check.", item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                intermediateCert.getSubjectX500Principal()), item.getMessage());
    }

    @Test
    public void validChainRequiredExtensionPositiveTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = new CertificateChainValidator();
        validator.setKnownCertificates(Collections.singletonList(intermediateCert));
        validator.setTrustedCertificates(Collections.singletonList(rootCert));

        ValidationReport report = validator.validateCertificate(signingCert, DateTimeUtil.getCurrentTimeDate(),
                        Collections.<CertificateExtension>singletonList(new KeyUsageExtension(KeyUsage.DIGITAL_SIGNATURE)));

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(1, report.getLogs().size());
        Assert.assertTrue(report.getFailures().isEmpty());

        CertificateReportItem item = report.getCertificateLogs().get(0);
        Assert.assertEquals(rootCert, item.getCertificate());
        Assert.assertEquals("Certificate check.", item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), item.getMessage());
    }

    @Test
    public void validChainGloballyRequiredExtensionPositiveTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = new CertificateChainValidator();
        validator.setKnownCertificates(Collections.singletonList(intermediateCert));
        validator.setTrustedCertificates(Collections.singletonList(rootCert));
        validator.setGlobalRequiredExtensions(Collections.<CertificateExtension>singletonList(new KeyUsageExtension(0)));

        ValidationReport report = validator.validateCertificate(signingCert, DateTimeUtil.getCurrentTimeDate(), null);

        Assert.assertEquals(ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(1, report.getLogs().size());
        Assert.assertTrue(report.getFailures().isEmpty());

        CertificateReportItem item = report.getCertificateLogs().get(0);
        Assert.assertEquals(rootCert, item.getCertificate());
        Assert.assertEquals("Certificate check.", item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), item.getMessage());
    }

    @Test
    public void validChainRequiredExtensionNegativeTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = new CertificateChainValidator();
        validator.setKnownCertificates(Collections.singletonList(intermediateCert));
        validator.setTrustedCertificates(Collections.singletonList(rootCert));

        ValidationReport report = validator.validateCertificate(signingCert, DateTimeUtil.getCurrentTimeDate(),
                Collections.<CertificateExtension>singletonList(new KeyUsageExtension(KeyUsage.KEY_CERT_SIGN)));

        Assert.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(2, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));

        CertificateReportItem log = report.getCertificateLogs().get(1);
        Assert.assertEquals(rootCert, log.getCertificate());
        Assert.assertEquals("Certificate check.", log.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), log.getMessage());

        CertificateReportItem failure = report.getCertificateFailures().get(0);
        Assert.assertEquals(signingCert, failure.getCertificate());
        Assert.assertEquals("Required certificate extensions check.", failure.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Required extension {0} is missing or incorrect.", X509Extensions.KEY_USAGE), failure.getMessage());
    }

    @Test
    public void validChainGloballyRequiredExtensionNegativeTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = new CertificateChainValidator();
        validator.setKnownCertificates(Collections.singletonList(intermediateCert));
        validator.setTrustedCertificates(Collections.singletonList(rootCert));
        validator.setGlobalRequiredExtensions(Collections.<CertificateExtension>singletonList(new KeyUsageExtension(KeyUsage.DIGITAL_SIGNATURE)));

        ValidationReport report = validator.validateCertificate(signingCert, DateTimeUtil.getCurrentTimeDate(), null);

        Assert.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(2, report.getFailures().size());
        Assert.assertEquals(3, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));
        Assert.assertEquals(report.getFailures().get(1), report.getLogs().get(1));

        CertificateReportItem log = report.getCertificateLogs().get(2);
        Assert.assertEquals(rootCert, log.getCertificate());
        Assert.assertEquals("Certificate check.", log.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), log.getMessage());

        CertificateReportItem failure1 = report.getCertificateFailures().get(0);
        Assert.assertEquals(intermediateCert, failure1.getCertificate());
        Assert.assertEquals("Required certificate extensions check.", failure1.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Globally required extension {0} is missing or incorrect.", X509Extensions.KEY_USAGE), failure1.getMessage());

        CertificateReportItem failure2 = report.getCertificateFailures().get(1);
        Assert.assertEquals(rootCert, failure2.getCertificate());
        Assert.assertEquals("Required certificate extensions check.", failure2.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Globally required extension {0} is missing or incorrect.", X509Extensions.KEY_USAGE), failure2.getMessage());
    }

    @Test
    public void validChainTrustedRootIsnSetTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = new CertificateChainValidator();
        validator.setKnownCertificates(Arrays.asList(intermediateCert, rootCert));

        ValidationReport report =
                validator.validateCertificate(signingCert, DateTimeUtil.getCurrentTimeDate(), null);

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

    @Test
    public void intermediateCertIsNotYetValidTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        String intermediateCertName = CERTS_SRC + "notYetValidIntermediateCert.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) PemFileHelper.readFirstChain(intermediateCertName)[0];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = new CertificateChainValidator();
        validator.setKnownCertificates(Collections.singletonList(intermediateCert));
        validator.setTrustedCertificates(Collections.singletonList(rootCert));

        ValidationReport report =
                validator.validateCertificate(signingCert, DateTimeUtil.getCurrentTimeDate(), null);

        Assert.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(2, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));

        CertificateReportItem log = report.getCertificateLogs().get(1);
        Assert.assertEquals(rootCert, log.getCertificate());
        Assert.assertEquals("Certificate check.", log.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), log.getMessage());

        CertificateReportItem item = report.getCertificateFailures().get(0);
        Assert.assertEquals(intermediateCert, item.getCertificate());
        Assert.assertEquals("Certificate validity period check.", item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is not yet valid.", intermediateCert.getSubjectX500Principal()), item.getMessage());
        Exception exception = item.getExceptionCause();
        Assert.assertTrue(exception instanceof CertificateNotYetValidException);
    }

    @Test
    public void intermediateCertIsExpiredTest() throws CertificateException, IOException {
        String chainName = CERTS_SRC + "validCertsChain.pem";
        String intermediateCertName = CERTS_SRC + "expiredIntermediateCert.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) PemFileHelper.readFirstChain(intermediateCertName)[0];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CertificateChainValidator validator = new CertificateChainValidator();
        validator.setKnownCertificates(Collections.singletonList(intermediateCert));
        validator.setTrustedCertificates(Collections.singletonList(rootCert));

        ValidationReport report =
                validator.validateCertificate(signingCert, DateTimeUtil.getCurrentTimeDate(), null);

        Assert.assertEquals(ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(2, report.getLogs().size());
        Assert.assertEquals(report.getFailures().get(0), report.getLogs().get(0));

        CertificateReportItem log = report.getCertificateLogs().get(1);
        Assert.assertEquals(rootCert, log.getCertificate());
        Assert.assertEquals("Certificate check.", log.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is trusted, revocation data checks are not required.",
                rootCert.getSubjectX500Principal()), log.getMessage());

        CertificateReportItem item = report.getCertificateFailures().get(0);
        Assert.assertEquals(intermediateCert, item.getCertificate());
        Assert.assertEquals("Certificate validity period check.", item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(
                "Certificate {0} is expired.", intermediateCert.getSubjectX500Principal()), item.getMessage());
        Exception exception = item.getExceptionCause();
        Assert.assertTrue(exception instanceof CertificateExpiredException);
    }
}
