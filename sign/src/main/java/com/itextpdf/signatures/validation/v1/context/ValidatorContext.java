package com.itextpdf.signatures.validation.v1.context;

import com.itextpdf.signatures.validation.v1.CRLValidator;
import com.itextpdf.signatures.validation.v1.CertificateChainValidator;
import com.itextpdf.signatures.validation.v1.OCSPValidator;
import com.itextpdf.signatures.validation.v1.RevocationDataValidator;

/**
 * This enum lists all possible contexts related to the validator in which the validation is taking place.
 */
public enum ValidatorContext {
    /**
     * This value is expected to be used in {@link OCSPValidator} context.
     */
    OCSP_VALIDATOR,
    /**
     * This value is expected to be used in {@link CRLValidator} context.
     */
    CRL_VALIDATOR,
    /**
     * This value is expected to be used in {@link RevocationDataValidator} context.
     */
    REVOCATION_DATA_VALIDATOR,
    /**
     * This value is expected to be used in {@link CertificateChainValidator} context.
     */
    CERTIFICATE_CHAIN_VALIDATOR,
    /**
     * This value is expected to be used in SignatureValidator context.
     */
    SIGNATURE_VALIDATOR,
}
