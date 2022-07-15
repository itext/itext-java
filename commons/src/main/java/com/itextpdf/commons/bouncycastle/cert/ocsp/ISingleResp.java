package com.itextpdf.commons.bouncycastle.cert.ocsp;

import java.util.Date;

/**
 * This interface represents the wrapper for SingleResp that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ISingleResp {
    /**
     * Calls actual {@code getCertID} method for the wrapped SingleResp object.
     *
     * @return {@link ICertificateID} the wrapper for the received CertificateID.
     */
    ICertificateID getCertID();

    /**
     * Calls actual {@code getCertStatus} method for the wrapped SingleResp object.
     *
     * @return {@link ICertificateStatus} the wrapper for the received CertificateStatus.
     */
    ICertificateStatus getCertStatus();

    /**
     * Calls actual {@code getNextUpdate} method for the wrapped SingleResp object.
     *
     * @return date of next update.
     */
    Date getNextUpdate();

    /**
     * Calls actual {@code getThisUpdate} method for the wrapped SingleResp object.
     *
     * @return date of this update.
     */
    Date getThisUpdate();
}
