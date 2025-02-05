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
package com.itextpdf.signatures.validation;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.extensions.DynamicCertificateExtension;
import com.itextpdf.signatures.validation.report.CertificateReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import static com.itextpdf.signatures.validation.SafeCalling.onExceptionLog;
import static com.itextpdf.signatures.validation.SafeCalling.onRuntimeExceptionLog;

/**
 * Validator class, which is expected to be used for certificates chain validation.
 */
public class CertificateChainValidator {
    static final String CERTIFICATE_CHECK = "Certificate check.";
    static final String VALIDITY_CHECK = "Certificate validity period check.";
    static final String EXTENSIONS_CHECK = "Required certificate extensions check.";

    static final String CERTIFICATE_TRUSTED =
            "Certificate {0} is trusted, revocation data checks are not required.";
    static final String CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT = "Certificate {0} is trusted for {1}, "
            + "but it is not used in this context. Validation will continue as usual.";
    static final String EXTENSION_MISSING = "Required extension validation failed: {0}";
    static final String ISSUER_MISSING = "Certificate {0} isn't trusted and issuer certificate isn't provided.";
    static final String EXPIRED_CERTIFICATE = "Certificate {0} is expired.";
    static final String NOT_YET_VALID_CERTIFICATE = "Certificate {0} is not yet valid.";
    static final String ISSUER_CANNOT_BE_VERIFIED =
            "Issuer certificate {0} for subject certificate {1} cannot be mathematically verified.";

    static final String ISSUER_VERIFICATION_FAILED =
            "Unexpected exception occurred while verifying issuer certificate.";
    static final String ISSUER_RETRIEVAL_FAILED =
            "Unexpected exception occurred while retrieving certificate issuer from IssuingCertificateRetriever.";
    static final String TRUSTSTORE_RETRIEVAL_FAILED =
            "Unexpected exception occurred while retrieving trust store from IssuingCertificateRetriever.";
    static final String REVOCATION_VALIDATION_FAILED =
            "Unexpected exception occurred while validating certificate revocation.";
    static final String VALIDITY_PERIOD_CHECK_FAILED =
            "Unexpected exception occurred while validating certificate validity period.";


    private final SignatureValidationProperties properties;
    private final IssuingCertificateRetriever certificateRetriever;
    private final RevocationDataValidator revocationDataValidator;

    /**
     * Create new instance of {@link CertificateChainValidator}.
     *
     * @param builder See {@link  ValidatorChainBuilder}
     */
    protected CertificateChainValidator(ValidatorChainBuilder builder) {
        this.certificateRetriever = builder.getCertificateRetriever();
        this.properties = builder.getProperties();
        this.revocationDataValidator = builder.getRevocationDataValidator();
    }

    /**
     * Validate given certificate using provided validation date and required extensions.
     *
     * @param context        the validation context in which to validate the certificate chain
     * @param certificate    {@link X509Certificate} to be validated
     * @param validationDate {@link Date} against which certificate is expected to be validated. Usually signing
     *                       date
     *
     * @return {@link ValidationReport} which contains detailed validation results.
     */
    public ValidationReport validateCertificate(ValidationContext context, X509Certificate certificate,
            Date validationDate) {
        ValidationReport result = new ValidationReport();
        return validate(result, context, certificate, validationDate);
    }

    /**
     * Validate given certificate using provided validation date and required extensions.
     * Result is added into provided report.
     *
     * @param result         {@link ValidationReport} which is populated with detailed validation results
     * @param context        the context in which to perform the validation
     * @param certificate    {@link X509Certificate} to be validated
     * @param validationDate {@link Date} against which certificate is expected to be validated. Usually signing
     *                       date
     *
     * @return {@link ValidationReport} which contains both provided and new validation results.
     */
    public ValidationReport validate(ValidationReport result, ValidationContext context, X509Certificate certificate,
            Date validationDate) {
        return validate(result, context, certificate, validationDate, 0);
    }

    private ValidationReport validate(ValidationReport result, ValidationContext context, X509Certificate certificate,
            Date validationDate, int certificateChainSize) {
        ValidationContext localContext = context.setValidatorContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR);
        validateValidityPeriod(result, certificate, validationDate);
        validateRequiredExtensions(result, localContext, certificate, certificateChainSize);
        if (stopValidation(result, localContext)) {
            return result;
        }
        if (onExceptionLog(() -> checkIfCertIsTrusted(result, localContext, certificate), Boolean.FALSE, result,
                e -> new CertificateReportItem(certificate, CERTIFICATE_CHECK, TRUSTSTORE_RETRIEVAL_FAILED,
                        e, ReportItemStatus.INFO))) {
            return result;
        }

        validateRevocationData(result, localContext, certificate, validationDate);
        if (stopValidation(result, localContext)) {
            return result;
        }
        validateChain(result, localContext, certificate, validationDate, certificateChainSize);
        return result;
    }

    private boolean checkIfCertIsTrusted(ValidationReport result, ValidationContext context,
            X509Certificate certificate) {
        if (CertificateSource.TRUSTED == context.getCertificateSource()) {
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                    CERTIFICATE_TRUSTED, certificate.getSubjectX500Principal()), ReportItemStatus.INFO));
            return true;
        }
        TrustedCertificatesStore store = certificateRetriever.getTrustedCertificatesStore();
        if (store.isCertificateGenerallyTrusted(certificate)) {
            // Certificate is trusted for everything.
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                    CERTIFICATE_TRUSTED, certificate.getSubjectX500Principal()), ReportItemStatus.INFO));
            return true;
        }
        if (store.isCertificateTrustedForCA(certificate)) {
            // Certificate is trusted to be CA, we need to make sure it wasn't used to directly sign anything else.
            if (CertificateSource.CERT_ISSUER == context.getCertificateSource()) {
                result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                        CERTIFICATE_TRUSTED, certificate.getSubjectX500Principal()), ReportItemStatus.INFO));
                return true;
            }
            // Certificate is trusted to be CA, but is not used in CA context.
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                    CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT, certificate.getSubjectX500Principal(),
                    "certificates generation"), ReportItemStatus.INFO));
        }
        if (store.isCertificateTrustedForTimestamp(certificate)) {
            // Certificate is trusted for timestamp signing,
            // we need to make sure this chain is responsible for timestamping.
            if (ValidationContext.checkIfContextChainContainsCertificateSource(context, CertificateSource.TIMESTAMP)) {
                result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                        CERTIFICATE_TRUSTED, certificate.getSubjectX500Principal()), ReportItemStatus.INFO));
                return true;
            }
            // Certificate is trusted for timestamps generation, but is not used in timestamp generation context.
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                    CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT, certificate.getSubjectX500Principal(),
                    "timestamp generation"), ReportItemStatus.INFO));
        }
        if (store.isCertificateTrustedForOcsp(certificate)) {
            // Certificate is trusted for OCSP response signing,
            // we need to make sure this chain is responsible for OCSP response generation.
            if (ValidationContext.checkIfContextChainContainsCertificateSource(
                    context, CertificateSource.OCSP_ISSUER)) {
                result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                        CERTIFICATE_TRUSTED, certificate.getSubjectX500Principal()), ReportItemStatus.INFO));
                return true;
            }
            // Certificate is trusted for OCSP response generation, but is not used in OCSP response generation context.
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                    CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT, certificate.getSubjectX500Principal(),
                    "OCSP response generation"), ReportItemStatus.INFO));
        }
        if (store.isCertificateTrustedForCrl(certificate)) {
            // Certificate is trusted for CRL signing,
            // we need to make sure this chain is responsible for CRL generation.
            if (ValidationContext.checkIfContextChainContainsCertificateSource(context, CertificateSource.CRL_ISSUER)) {
                result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                        CERTIFICATE_TRUSTED, certificate.getSubjectX500Principal()), ReportItemStatus.INFO));
                return true;
            }
            // Certificate is trusted for CRL generation, but is not used in CRL generation context.
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                    CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT, certificate.getSubjectX500Principal(),
                    "CRL generation"), ReportItemStatus.INFO));
        }
        return false;
    }

    private boolean stopValidation(ValidationReport result, ValidationContext context) {
        return !properties.getContinueAfterFailure(context)
                && result.getValidationResult() == ValidationReport.ValidationResult.INVALID;
    }

    private void validateValidityPeriod(ValidationReport result, X509Certificate certificate,
            Date validationDate) {
        try {
            certificate.checkValidity(validationDate);
        } catch (CertificateExpiredException e) {
            result.addReportItem(new CertificateReportItem(certificate, VALIDITY_CHECK, MessageFormatUtil.format(
                    EXPIRED_CERTIFICATE, certificate.getSubjectX500Principal()), e, ReportItemStatus.INVALID));
        } catch (CertificateNotYetValidException e) {
            result.addReportItem(new CertificateReportItem(certificate, VALIDITY_CHECK, MessageFormatUtil.format(
                    NOT_YET_VALID_CERTIFICATE, certificate.getSubjectX500Principal()), e, ReportItemStatus.INVALID));
        } catch (RuntimeException e) {
            result.addReportItem(new CertificateReportItem(certificate, VALIDITY_CHECK, MessageFormatUtil.format(
                    VALIDITY_PERIOD_CHECK_FAILED, certificate.getSubjectX500Principal()), e, ReportItemStatus.INVALID));
        }
    }

    private void validateRequiredExtensions(ValidationReport result, ValidationContext context,
            X509Certificate certificate, int certificateChainSize) {
        List<CertificateExtension> requiredExtensions = properties.getRequiredExtensions(context);
        if (requiredExtensions != null) {
            for (CertificateExtension requiredExtension : requiredExtensions) {
                if (requiredExtension instanceof DynamicCertificateExtension) {
                    ((DynamicCertificateExtension) requiredExtension).withCertificateChainSize(certificateChainSize);
                }
                if (!requiredExtension.existsInCertificate(certificate)) {
                    result.addReportItem(new CertificateReportItem(certificate, EXTENSIONS_CHECK,
                            MessageFormatUtil.format(EXTENSION_MISSING, requiredExtension.getMessage()),
                            ReportItemStatus.INVALID));
                }
            }
        }
    }

    private void validateRevocationData(ValidationReport report, ValidationContext context, X509Certificate certificate,
            Date validationDate) {
        onRuntimeExceptionLog(() ->
                revocationDataValidator.validate(report, context, certificate, validationDate), report, e ->
                new CertificateReportItem(certificate, CERTIFICATE_CHECK,
                        REVOCATION_VALIDATION_FAILED, e, ReportItemStatus.INDETERMINATE));
    }

    private void validateChain(ValidationReport result, ValidationContext context, X509Certificate certificate,
            Date validationDate, int certificateChainSize) {
        List<X509Certificate> issuerCertificates;
        try {
            issuerCertificates = certificateRetriever.retrieveIssuerCertificate(certificate);
        } catch (RuntimeException e) {
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK,
                    ISSUER_RETRIEVAL_FAILED, e, ReportItemStatus.INDETERMINATE));
            return;
        }
        if (issuerCertificates.isEmpty()) {
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                    ISSUER_MISSING, certificate.getSubjectX500Principal()), ReportItemStatus.INDETERMINATE));
            return;
        }
        ValidationReport[] candidateReports = new ValidationReport[issuerCertificates.size()];
        for (int i = 0; i < issuerCertificates.size(); i++) {
            candidateReports[i] = new ValidationReport();
            try {
                certificate.verify(issuerCertificates.get(i).getPublicKey());
            } catch (GeneralSecurityException e) {
                candidateReports[i].addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK,
                        MessageFormatUtil.format(ISSUER_CANNOT_BE_VERIFIED,
                                issuerCertificates.get(i).getSubjectX500Principal(),
                                certificate.getSubjectX500Principal()), e, ReportItemStatus.INVALID));
                continue;
            } catch (RuntimeException e) {
                candidateReports[i].addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK,
                        MessageFormatUtil.format(ISSUER_VERIFICATION_FAILED,
                                issuerCertificates.get(i).getSubjectX500Principal(),
                                certificate.getSubjectX500Principal()), e, ReportItemStatus.INVALID));
                continue;
            }
            this.validate(candidateReports[i], context.setCertificateSource(CertificateSource.CERT_ISSUER),
                    issuerCertificates.get(i), validationDate, certificateChainSize + 1);
            if (candidateReports[i].getValidationResult() == ValidationResult.VALID) {
                // We found valid issuer, no need to try other ones.
                result.merge(candidateReports[i]);
                return;
            }
        }
        // Valid issuer wasn't found, add all the reports
        for (ValidationReport candidateReport : candidateReports) {
            result.merge(candidateReport);
        }
    }
}
