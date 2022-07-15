package com.itextpdf.commons.bouncycastle.cert.ocsp;

/**
 * This interface represents the wrapper for Req that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IReq {
    /**
     * Calls actual {@code getCertID} method for the wrapped Req object.
     *
     * @return {@link ICertificateID} the wrapper for the received CertificateID.
     */
    ICertificateID getCertID();
}
