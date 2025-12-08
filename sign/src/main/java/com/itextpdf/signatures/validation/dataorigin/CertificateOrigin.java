package com.itextpdf.signatures.validation.dataorigin;

/**
 * Enum representing an origin from where certificates come from.
 */
public enum CertificateOrigin {
    /**
     * Latest DSS dictionary in a PDF document.
     */
    LATEST_DSS,
    /**
     * DSS dictionary, corresponding to previous PDF document revisions.
     */
    HISTORICAL_DSS,
    /**
     * Signature CMS container.
     */
    SIGNATURE,
    /**
     * OCSP response object.
     */
    OCSP_RESPONSE,
    /**
     * Other possible sources.
     */
    OTHER
}
