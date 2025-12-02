package com.itextpdf.signatures.validation;

/**
 * Enum representing an origin from where the revocation data comes from.
 */
public enum RevocationResponseOrigin {
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
     * Other possible sources.
     */
    OTHER
}
