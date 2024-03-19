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
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.validation.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.extensions.ExtendedKeyUsageExtension;
import com.itextpdf.signatures.validation.extensions.KeyUsage;
import com.itextpdf.signatures.validation.extensions.KeyUsageExtension;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Validator class, which is expected to be used for signatures validation.
 */
class SignatureValidator {
    static final String TIMESTAMP_VERIFICATION = "Timestamp verification check.";
    static final String SIGNATURE_VERIFICATION = "Signature verification check.";
    static final String CERTS_FROM_DSS = "Certificates from DSS check.";
    static final String CANNOT_PARSE_CERT_FROM_DSS =
            "Certificate {0} stored in DSS dictionary cannot be parsed.";
    static final String CANNOT_VERIFY_SIGNATURE = "Signature {0} cannot be mathematically verified.";
    static final String DOCUMENT_IS_NOT_COVERED = "Signature {0} doesn't cover entire document.";
    static final String CANNOT_VERIFY_TIMESTAMP = "Signature timestamp attribute cannot be verified";


    private final PdfDocument document;
    private CertificateChainValidator certificateChainValidator = new CertificateChainValidator();
    private boolean proceedValidationAfterFail = true;

    /**
     * Create new instance of {@link SignatureValidator}.
     */
    public SignatureValidator(PdfDocument document) {
        this.document = document;
    }

    /**
     * Get {@link CertificateChainValidator} which is currently used as a validator for signature certificates.
     *
     * @return {@link CertificateChainValidator} to be a validator for signature certificates
     */
    public CertificateChainValidator getCertificateChainValidator() {
        return certificateChainValidator;
    }

    /**
     * Set {@link CertificateChainValidator} to be used as a validator for signature certificates.
     *
     * @param certificateChainValidator {@link CertificateChainValidator} to be a validator for signature certificates
     *
     * @return same instance of {@link SignatureValidator}
     */
    public SignatureValidator setCertificateChainValidator(CertificateChainValidator certificateChainValidator) {
        this.certificateChainValidator = certificateChainValidator;
        return this;
    }

    /**
     * Set {@code boolean} value, which determines whether to proceed or abort validation in case of failure.
     *
     * @param proceedValidationAfterFail {@code true} to proceed validation in case of failure, {@code false} otherwise
     *
     * @return same instance of {@link SignatureValidator}
     */
    public SignatureValidator proceedValidationAfterFail(boolean proceedValidationAfterFail) {
        this.proceedValidationAfterFail = proceedValidationAfterFail;
        certificateChainValidator.proceedValidationAfterFail(proceedValidationAfterFail);
        return this;
    }

    /**
     * Set certificates {@link Collection} to be used as trusted roots.
     *
     * @param trustedCertificates certificates {@link Collection} to be used as trusted roots
     *
     * @return same instance of {@link SignatureValidator}
     */
    public SignatureValidator setTrustedCertificates(Collection<Certificate> trustedCertificates) {
        this.certificateChainValidator.setTrustedCertificates(trustedCertificates);
        return this;
    }

    /**
     * Set certificates {@link Collection} to be used as possible certificates for chain building.
     *
     * @param knownCertificates certificates {@link Collection} to be used as possible certificates for chain building
     *
     * @return same instance of {@link SignatureValidator}
     */
    public SignatureValidator setKnownCertificates(Collection<Certificate> knownCertificates) {
        this.certificateChainValidator.setKnownCertificates(knownCertificates);
        return this;
    }

    /**
     * Validate the latest signature in the document.
     *
     * @return {@link ValidationReport} which contains detailed validation results
     */
    public ValidationReport validateLatestSignature() {
        ValidationReport validationReport = new ValidationReport();
        PdfPKCS7 pkcs7 = mathematicallyVerifySignature(validationReport);
        if (stopValidation(validationReport)) {
            return validationReport;
        }

        List<Certificate> certificatesFromDss = getCertificatesFromDss(validationReport);
        certificateChainValidator.setKnownCertificates(certificatesFromDss);

        if (pkcs7.isTsp()) {
            return validateTimestampChain(validationReport, pkcs7.getCertificates(), pkcs7.getSigningCertificate());
        }

        Date signingDate = DateTimeUtil.getCurrentTimeDate();
        if (pkcs7.getTimeStampTokenInfo() != null) {
            try {
                if (!pkcs7.verifyTimestampImprint()) {
                    validationReport.addReportItem(new ReportItem(TIMESTAMP_VERIFICATION, CANNOT_VERIFY_TIMESTAMP,
                            ReportItemStatus.INVALID));
                }
            } catch (GeneralSecurityException e) {
                validationReport.addReportItem(new ReportItem(TIMESTAMP_VERIFICATION, CANNOT_VERIFY_TIMESTAMP, e,
                        ReportItemStatus.INVALID));
            }
            if (stopValidation(validationReport)) {
                return validationReport;
            }
            Certificate[] timestampCertificates = pkcs7.getTimestampCertificates();
            validateTimestampChain(validationReport, timestampCertificates, (X509Certificate) timestampCertificates[0]);
            if (stopValidation(validationReport)) {
                return validationReport;
            }
            signingDate = pkcs7.getTimeStampDate().getTime();
        }

        Certificate[] certificates = pkcs7.getCertificates();
        certificateChainValidator.setKnownCertificates(Arrays.asList(certificates));
        List<CertificateExtension> requiredExtensions = new ArrayList<>();
        requiredExtensions.add(new KeyUsageExtension(KeyUsage.NON_REPUDIATION));
        X509Certificate signingCertificate = pkcs7.getSigningCertificate();

        return certificateChainValidator.validate(validationReport, signingCertificate,
                signingDate, requiredExtensions);
    }

    private PdfPKCS7 mathematicallyVerifySignature(ValidationReport validationReport) {
        SignatureUtil signatureUtil = new SignatureUtil(document);
        List<String> signatures = signatureUtil.getSignatureNames();
        String latestSignatureName = signatures.get(signatures.size() - 1);
        PdfPKCS7 pkcs7 = signatureUtil.readSignatureData(latestSignatureName);
        if (!signatureUtil.signatureCoversWholeDocument(latestSignatureName)) {
            validationReport.addReportItem(new ReportItem(SIGNATURE_VERIFICATION,
                    MessageFormatUtil.format(DOCUMENT_IS_NOT_COVERED, latestSignatureName), ReportItemStatus.INVALID));
        }
        try {
            if (!pkcs7.verifySignatureIntegrityAndAuthenticity()) {
                validationReport.addReportItem(new ReportItem(SIGNATURE_VERIFICATION, MessageFormatUtil.format(
                        CANNOT_VERIFY_SIGNATURE, latestSignatureName), ReportItemStatus.INVALID));
            }
        } catch (GeneralSecurityException e) {
            validationReport.addReportItem(new ReportItem(SIGNATURE_VERIFICATION, MessageFormatUtil.format(
                    CANNOT_VERIFY_SIGNATURE, latestSignatureName), e, ReportItemStatus.INVALID));
        }
        return pkcs7;
    }

    private ValidationReport validateTimestampChain(ValidationReport validationReport, Certificate[] knownCerts,
                                                    X509Certificate signingCert) {
        List<CertificateExtension> requiredTimestampExtensions = new ArrayList<>();
        requiredTimestampExtensions.add(new ExtendedKeyUsageExtension(
                Collections.singletonList(ExtendedKeyUsageExtension.TIME_STAMPING)));
        certificateChainValidator.setKnownCertificates(Arrays.asList(knownCerts));
        Date signingDate = DateTimeUtil.getCurrentTimeDate();

        return certificateChainValidator.validate(
                validationReport, signingCert, signingDate, requiredTimestampExtensions);
    }

    private List<Certificate> getCertificatesFromDss(ValidationReport validationReport) {
        PdfDictionary dss = document.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        List<Certificate> certificatesFromDss = new ArrayList<>();
        if (dss != null) {
            PdfArray certs = dss.getAsArray(PdfName.Certs);
            if (certs != null) {
                for (int i = 0; i < certs.size(); ++i) {
                    PdfStream certStream = certs.getAsStream(i);
                    try {
                        certificatesFromDss.add(CertificateUtil.generateCertificate(
                                new ByteArrayInputStream(certStream.getBytes())));
                    } catch (GeneralSecurityException e) {
                        validationReport.addReportItem(new ReportItem(CERTS_FROM_DSS, MessageFormatUtil.format(
                                CANNOT_PARSE_CERT_FROM_DSS, certStream), e, ReportItemStatus.INFO));
                    }
                }
            }
        }
        return certificatesFromDss;
    }

    private boolean stopValidation(ValidationReport result) {
        return !proceedValidationAfterFail && result.getValidationResult() != ValidationReport.ValidationResult.VALID;
    }
}
