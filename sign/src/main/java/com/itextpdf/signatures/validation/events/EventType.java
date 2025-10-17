package com.itextpdf.signatures.validation.events;

/**
 * This enumeration alleviates the need for instanceof on all IValidationEvents.
 */
public enum EventType {
    /**
     * Event triggered for every signature validation being started.
     */
    SIGNATURE_VALIDATION_STARTED,
    /**
     * Event triggered for every validation success, including timestamp validation.
     */
    SIGNATURE_VALIDATION_SUCCESS,
    /**
     * Event triggered for every validation failure, including timestamp validation.
     */
    SIGNATURE_VALIDATION_FAILURE,
    /**
     * Event triggered for every timestamp validation started.
     */
    PROOF_OF_EXISTENCE_FOUND,
    /**
     * Event triggered for every certificate issuer that
     * is retrieved via Authority Information Access extension.
     */
    CERTIFICATE_ISSUER_EXTERNAL_RETRIEVAL,
    /**
     * Event triggered for every certificate issuer available in the document
     * that was not in the most recent DSS.
     */
    CERTIFICATE_ISSUER_OTHER_INTERNAL_SOURCE_USED,
    /**
     * Event triggered for every outgoing OCSP request.
     */
    OCSP_REQUEST,
    /**
     * Event triggered for every OCSP response from the document that was not in the most recent DSS.
     */
    OCSP_OTHER_INTERNAL_SOURCE_USED,
    /**
     * Event triggered for every outgoing CRL request.
     */
    CRL_REQUEST,
    /**
     * Event triggered for every CRL response from the document that was not in the most recent DSS.
     */
    CRL_OTHER_INTERNAL_SOURCE_USED,
    /**
     * Event triggered when the most recent DSS has been processed.
     */
    DSS_ENTRY_PROCESSED,
    /**
     * Event triggered when the certificate chain was validated successfully.
     */
    CERTIFICATE_CHAIN_SUCCESS,
    /**
     * Event triggered when the certificate chain validated failed.
     */
    CERTIFICATE_CHAIN_FAILURE,
    /**
     * Event triggered when a certificate is proven not te be revoked by a CRL response.
     */
    CRL_VALIDATION_SUCCESS,
    /**
     * Event triggered when a certificate is revoked by a CRL response.
     */
    CRL_VALIDATION_FAILURE,
    /**
     * Event triggered when a certificate is proven not te be revoked by a OCSP response.
     */
    OCSP_VALIDATION_SUCCESS,
    /**
     * Event triggered when a certificate is revoked by a OCSP response.
     */
    OCSP_VALIDATION_FAILURE,
    /**
     * Event triggered for every algorithm being used during signature validation.
     */
    ALGORITHM_USAGE
}