package com.itextpdf.signatures.validation;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.validation.CertificateValidationReport.ValidationResult;
import com.itextpdf.signatures.validation.extensions.BasicConstraintsExtension;
import com.itextpdf.signatures.validation.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.extensions.KeyUsage;
import com.itextpdf.signatures.validation.extensions.KeyUsageExtension;

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
    private static final String CERTIFICATE_CHECK = "Certificate check.";
    private static final String VALIDITY_CHECK = "Certificate validity period check.";
    private static final String EXTENSIONS_CHECK = "Required certificate extensions check.";

    private static final String CERTIFICATE_TRUSTED =
            "Certificate {0} is trusted, revocation data checks are not required.";
    private static final String EXTENSION_MISSING = "Required extension {0} is missing or incorrect.";
    private static final String GLOBAL_EXTENSION_MISSING = "Globally required extension {0} is missing or incorrect.";
    private static final String ISSUER_MISSING = "Certificate {0} isn't trusted and issuer certificate isn't provided.";
    private static final String EXPIRED_CERTIFICATE = "Certificate {0} is expired.";
    private static final String NOT_YET_VALID_CERTIFICATE = "Certificate {0} is not yet valid.";

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
     * Set {@link CertificateChainValidator} to be as a validator for the next certificate in the chain.
     *
     * @param nextValidator {@link CertificateChainValidator} to be as a validator for the next certificate in the chain
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
     * @return {@link CertificateValidationReport} which contains detailed validation results
     */
    public CertificateValidationReport validateCertificate(X509Certificate certificate, Date validationDate,
            List<CertificateExtension> requiredExtensions) {
        CertificateValidationReport result = new CertificateValidationReport(certificate);
        return validate(result, certificate, validationDate, requiredExtensions);
    }


    /**
     * Validate given certificate using provided validation date and required extensions.
     * Result is added into provided report.
     *
     * @param result {@link CertificateValidationReport} which is populated with detailed validation results
     * @param certificate {@link X509Certificate} to be validated
     * @param validationDate {@link Date} against which certificate is expected to be validated. Usually signing date
     * @param requiredExtensions certificate extension {@link List}, which are required for the provided certificate
     *
     * @return {@link CertificateValidationReport} which contains both provided and new validation results
     */
    public CertificateValidationReport validate(CertificateValidationReport result, X509Certificate certificate,
            Date validationDate, List<CertificateExtension> requiredExtensions) {
        validateValidityPeriod(result, certificate, validationDate);
        validateRequiredExtensions(result, certificate, requiredExtensions);
        if (stopValidation(result)) {
            return result;
        }
        if (certificateRetriever.isCertificateTrusted(certificate)) {
            result.addLog(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                    CERTIFICATE_TRUSTED, certificate.getSubjectX500Principal()));
            return result;
        }
        validateRevocationData(result, certificate, validationDate);
        if (stopValidation(result)) {
            return result;
        }
        validateChain(result, certificate, validationDate);
        return result;
    }

    private boolean stopValidation(CertificateValidationReport result) {
        return !proceedValidationAfterFail && result.getValidationResult() != ValidationResult.VALID;
    }

    private void validateValidityPeriod(CertificateValidationReport result, X509Certificate certificate,
            Date validationDate) {
        try {
            certificate.checkValidity(validationDate);
        } catch (CertificateExpiredException e) {
            result.addFailure(certificate, VALIDITY_CHECK, MessageFormatUtil.format(
                    EXPIRED_CERTIFICATE, certificate.getSubjectX500Principal()), e);
        } catch (CertificateNotYetValidException e) {
            result.addFailure(certificate, VALIDITY_CHECK, MessageFormatUtil.format(
                    NOT_YET_VALID_CERTIFICATE, certificate.getSubjectX500Principal()), e);
        }
    }

    private void validateRequiredExtensions(CertificateValidationReport result, X509Certificate certificate,
                                          List<CertificateExtension> requiredExtensions) {
        if (requiredExtensions != null) {
            for (CertificateExtension requiredExtension : requiredExtensions) {
                if (!requiredExtension.existsInCertificate(certificate)) {
                    result.addFailure(certificate, EXTENSIONS_CHECK, MessageFormatUtil.format(
                            EXTENSION_MISSING, requiredExtension.getExtensionOid()));
                }
            }
        }
        if (globalRequiredExtensions != null) {
            for (CertificateExtension requiredExtension : globalRequiredExtensions) {
                if (!requiredExtension.existsInCertificate(certificate)) {
                    result.addFailure(certificate, EXTENSIONS_CHECK, MessageFormatUtil.format(
                            GLOBAL_EXTENSION_MISSING, requiredExtension.getExtensionOid()));
                }
            }
        }
    }

    private void validateRevocationData(CertificateValidationReport result, X509Certificate certificate,
            Date validationDate) {
        validateOCSP(result, certificate, validationDate);
        validateCRL(result, certificate, validationDate);
    }

    private void validateCRL(CertificateValidationReport result, X509Certificate certificate, Date validationDate) {
        // TODO DEVSIX-8122 Implement CRLValidator
    }

    private void validateOCSP(CertificateValidationReport result, X509Certificate certificate, Date validationDate) {
        // TODO DEVSIX-8170 Implement OCSPValidator
    }

    private void validateChain(CertificateValidationReport result, X509Certificate certificate, Date validationDate) {
        List<CertificateExtension> requiredCertificateExtensions = new ArrayList<>();
        requiredCertificateExtensions.add(new KeyUsageExtension(KeyUsage.KEY_CERT_SIGN));
        requiredCertificateExtensions.add(new BasicConstraintsExtension(true));

        X509Certificate issuerCertificate =
                (X509Certificate) certificateRetriever.retrieveIssuerCertificate(certificate);
        if (issuerCertificate == null) {
            result.addFailure(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                    ISSUER_MISSING, certificate.getSubjectX500Principal()), ValidationResult.INDETERMINATE);
        } else {
            nextCertificateChainValidator.validate(result, issuerCertificate, validationDate,
                    requiredCertificateExtensions);
        }
    }
}
