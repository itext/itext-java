package com.itextpdf.signatures.validation.v1.context;

/**
 * This enum lists all possible contexts related to the certificate origin in which a validation may take place
 */
public enum CertificateSource {
    /**
     * The context while validating a CRL issuer certificate.
     */
    CRL_ISSUER,
    /**
     * The context while validating a OCSP issuer certificate that is neither trusted nor CA.
     */
    OCSP_ISSUER,
    /**
     * The context while validating a certificate issuer certificate.
     */
    CERT_ISSUER,
    /**
     * The context while validating a signer certificate.
     */
    SIGNER_CERT,
    /**
     * A certificate that is on a trusted list.
     */
    TRUSTED,
    /**
     * The context while validating a timestamp issuer certificate.
     */
    TIMESTAMP
}
