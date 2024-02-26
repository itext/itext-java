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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.validation.ValidationReport.ValidationResult;
import com.itextpdf.signatures.validation.extensions.BasicConstraintsExtension;
import com.itextpdf.signatures.validation.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.extensions.KeyUsage;
import com.itextpdf.signatures.validation.extensions.KeyUsageExtension;

import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Validator class, which is expected to be used for certificates chain validation.
 */
public class CertificateChainValidator {
    static final String CERTIFICATE_CHECK = "Certificate check.";
    static final String VALIDITY_CHECK = "Certificate validity period check.";
    static final String EXTENSIONS_CHECK = "Required certificate extensions check.";

    static final String CERTIFICATE_TRUSTED =
            "Certificate {0} is trusted, revocation data checks are not required.";
    static final String EXTENSION_MISSING = "Required extension {0} is missing or incorrect.";
    private static final String GLOBAL_EXTENSION_MISSING = "Globally required extension {0} is missing or incorrect.";
    private static final String ISSUER_MISSING = "Certificate {0} isn't trusted and issuer certificate isn't provided.";
    private static final String EXPIRED_CERTIFICATE = "Certificate {0} is expired.";
    private static final String NOT_YET_VALID_CERTIFICATE = "Certificate {0} is not yet valid.";
    private static final String ISSUER_CANNOT_BE_VERIFIED =
            "Issuer certificate {0} for subject certificate {1} cannot be mathematically verified.";

    private IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
    private IOcspClient ocspClient = new OcspClientBouncyCastle(null);
    private ICrlClient crlClient = new CrlClientOnline();
    private CertificateChainValidator nextCertificateChainValidator = this;
    private List<CertificateExtension> globalRequiredExtensions = new ArrayList<>();
    private boolean proceedValidationAfterFail = true;

    /**
     * Create new instance of {@link CertificateChainValidator}.
     */
    public CertificateChainValidator() {
        // Empty constructor.
    }

    /**
     * Set {@link ICrlClient} to be used for CRL responses receiving.
     *
     * @param crlClient {@link ICrlClient} to be used for CRL responses receiving
     *
     * @return same instance of {@link CertificateChainValidator}
     */
    public CertificateChainValidator setCrlClient(ICrlClient crlClient) {
        this.crlClient = crlClient;
        return this;
    }

    /**
     * Set {@link IOcspClient} to be used for OCSP responses receiving.
     *
     * @param ocpsClient {@link IOcspClient} to be used for OCSP responses receiving
     *
     * @return same instance of {@link CertificateChainValidator}
     */
    public CertificateChainValidator setOcspClient(IOcspClient ocpsClient) {
        this.ocspClient = ocpsClient;
        return this;
    }

    /**
     * Set {@link IssuingCertificateRetriever} to be used for certificate chain building.
     *
     * @param certificateRetriever {@link IssuingCertificateRetriever} to be used for certificate chain building
     *
     * @return same instance of {@link CertificateChainValidator}
     */
    public CertificateChainValidator setIssuingCertificateRetriever(IssuingCertificateRetriever certificateRetriever) {
        this.certificateRetriever = certificateRetriever;
        return this;
    }

    /**
     * Set certificates {@link Collection} to be used as trusted roots.
     *
     * @param trustedCertificates certificates {@link Collection} to be used as trusted roots
     *
     * @return same instance of {@link CertificateChainValidator}
     */
    public CertificateChainValidator setTrustedCertificates(Collection<Certificate> trustedCertificates) {
        certificateRetriever.addTrustedCertificates(trustedCertificates);
        return this;
    }

    /**
     * Set certificates {@link Collection} to be used as possible certificates for chain building.
     *
     * @param knownCertificates certificates {@link Collection} to be used as possible certificates for chain building
     *
     * @return same instance of {@link CertificateChainValidator}
     */
    public CertificateChainValidator setKnownCertificates(Collection<Certificate> knownCertificates) {
        certificateRetriever.addKnownCertificates(knownCertificates);
        return this;
    }

    /**
     * Set {@link CertificateChainValidator} to be used as a validator for the next certificate in the chain.
     *
     * @param nextValidator {@link CertificateChainValidator} to be a validator for the next certificate in the chain
     *
     * @return same instance of {@link CertificateChainValidator}
     */
    public CertificateChainValidator setNextCertificateChainValidator(CertificateChainValidator nextValidator) {
        this.nextCertificateChainValidator = nextValidator;
        return this;
    }

    /**
     * Set certificate extension {@link List}, which are globally required for each descending certificate in the chain.
     *
     * @param globalRequiredExtensions list of globally required extensions
     *
     * @return same instance of {@link CertificateChainValidator}
     */
    public CertificateChainValidator setGlobalRequiredExtensions(List<CertificateExtension> globalRequiredExtensions) {
        this.globalRequiredExtensions = globalRequiredExtensions;
        return this;
    }

    /**
     * Set {@code boolean} value, which determines whether to proceed or abort validation in case of failure.
     *
     * @param proceedValidationAfterFail {@code true} to proceed validation in case of failure, {@code false} otherwise
     *
     * @return same instance of {@link CertificateChainValidator}
     */
    public CertificateChainValidator proceedValidationAfterFail(boolean proceedValidationAfterFail) {
        this.proceedValidationAfterFail = proceedValidationAfterFail;
        return this;
    }

    /**
     * Validate given certificate using provided validation date and required extensions.
     *
     * @param certificate {@link X509Certificate} to be validated
     * @param validationDate {@link Date} against which certificate is expected to be validated. Usually signing date
     * @param requiredExtensions certificate extension {@link List}, which are required for the provided certificate
     *
     * @return {@link ValidationReport} which contains detailed validation results
     */
    public ValidationReport validateCertificate(X509Certificate certificate, Date validationDate,
            List<CertificateExtension> requiredExtensions) {
        ValidationReport result = new ValidationReport();
        return validate(result, certificate, validationDate, requiredExtensions);
    }


    /**
     * Validate given certificate using provided validation date and required extensions.
     * Result is added into provided report.
     *
     * @param result {@link ValidationReport} which is populated with detailed validation results
     * @param certificate {@link X509Certificate} to be validated
     * @param validationDate {@link Date} against which certificate is expected to be validated. Usually signing date
     * @param requiredExtensions certificate extension {@link List}, which are required for the provided certificate
     *
     * @return {@link ValidationReport} which contains both provided and new validation results
     */
    public ValidationReport validate(ValidationReport result, X509Certificate certificate, Date validationDate,
            List<CertificateExtension> requiredExtensions) {
        validateValidityPeriod(result, certificate, validationDate);
        validateRequiredExtensions(result, certificate, requiredExtensions);
        if (stopValidation(result)) {
            return result;
        }
        if (certificateRetriever.isCertificateTrusted(certificate)) {
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                    CERTIFICATE_TRUSTED, certificate.getSubjectX500Principal()), ValidationResult.VALID));
            return result;
        }
        validateRevocationData(result, certificate, validationDate);
        if (stopValidation(result)) {
            return result;
        }
        validateChain(result, certificate, validationDate);
        return result;
    }

    private boolean stopValidation(ValidationReport result) {
        return !proceedValidationAfterFail && result.getValidationResult() != ValidationResult.VALID;
    }

    private void validateValidityPeriod(ValidationReport result, X509Certificate certificate,
            Date validationDate) {
        try {
            certificate.checkValidity(validationDate);
        } catch (CertificateExpiredException e) {
            result.addReportItem(new CertificateReportItem(certificate, VALIDITY_CHECK, MessageFormatUtil.format(
                    EXPIRED_CERTIFICATE, certificate.getSubjectX500Principal()), e, ValidationResult.INVALID));
        } catch (CertificateNotYetValidException e) {
            result.addReportItem(new CertificateReportItem(certificate, VALIDITY_CHECK, MessageFormatUtil.format(
                    NOT_YET_VALID_CERTIFICATE, certificate.getSubjectX500Principal()), e, ValidationResult.INVALID));
        }
    }

    private void validateRequiredExtensions(ValidationReport result, X509Certificate certificate,
                                          List<CertificateExtension> requiredExtensions) {
        if (requiredExtensions != null) {
            for (CertificateExtension requiredExtension : requiredExtensions) {
                if (!requiredExtension.existsInCertificate(certificate)) {
                    result.addReportItem(new CertificateReportItem(certificate, EXTENSIONS_CHECK, MessageFormatUtil.format(
                            EXTENSION_MISSING, requiredExtension.getExtensionOid()), ValidationResult.INVALID));
                }
            }
        }
        if (globalRequiredExtensions != null) {
            for (CertificateExtension requiredExtension : globalRequiredExtensions) {
                if (!requiredExtension.existsInCertificate(certificate)) {
                    result.addReportItem(new CertificateReportItem(certificate, EXTENSIONS_CHECK, MessageFormatUtil.format(
                            GLOBAL_EXTENSION_MISSING, requiredExtension.getExtensionOid()), ValidationResult.INVALID));
                }
            }
        }
    }

    private void validateRevocationData(ValidationReport result, X509Certificate certificate,
                                        Date validationDate) {
        // TODO DEVSIX-8176 Implement RevocationDataValidator class: take into account ID_PKIX_OCSP_NOCHECK extension
        validateOCSP(result, certificate, validationDate);
        validateCRL(result, certificate, validationDate);
    }

    private void validateCRL(ValidationReport result, X509Certificate certificate, Date validationDate) {
        // TODO DEVSIX-8176 Implement RevocationDataValidator class
    }

    private void validateOCSP(ValidationReport result, X509Certificate certificate, Date validationDate) {
        // TODO DEVSIX-8176 Implement RevocationDataValidator class
    }

    private void validateChain(ValidationReport result, X509Certificate certificate, Date validationDate) {
        List<CertificateExtension> requiredCertificateExtensions = new ArrayList<>();
        requiredCertificateExtensions.add(new KeyUsageExtension(KeyUsage.KEY_CERT_SIGN));
        requiredCertificateExtensions.add(new BasicConstraintsExtension(true));

        X509Certificate issuerCertificate =
                (X509Certificate) certificateRetriever.retrieveIssuerCertificate(certificate);
        if (issuerCertificate == null) {
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                    ISSUER_MISSING, certificate.getSubjectX500Principal()), ValidationResult.INDETERMINATE));
            return;
        }
        try {
            certificate.verify(issuerCertificate.getPublicKey());
        } catch (GeneralSecurityException e) {
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK,
                    MessageFormatUtil.format(ISSUER_CANNOT_BE_VERIFIED, issuerCertificate.getSubjectX500Principal(),
                            certificate.getSubjectX500Principal()), e, ValidationResult.INVALID));
            return;
        }
        nextCertificateChainValidator.validate(result, issuerCertificate,
                validationDate, requiredCertificateExtensions);
    }
}
