package com.itextpdf.commons.bouncycastle.cert.ocsp;

import java.io.IOException;

/**
 * This interface represents the wrapper for OCSPResp that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IOCSPResp {
    /**
     * Calls actual {@code getEncoded} method for the wrapped OCSPResp object.
     *
     * @return the default encoding for the wrapped object.
     *
     * @throws IOException on encoding error.
     */
    byte[] getEncoded() throws IOException;

    /**
     * Calls actual {@code getStatus} method for the wrapped OCSPResp object.
     *
     * @return status value.
     */
    int getStatus();

    /**
     * Calls actual {@code getResponseObject} method for the wrapped OCSPResp object.
     *
     * @return response object.
     *
     * @throws AbstractOCSPException wrapped OCSPException.
     */
    Object getResponseObject() throws AbstractOCSPException;

    /**
     * Gets {@code SUCCESSFUL} constant for the wrapped OCSPResp.
     *
     * @return OCSPResp.SUCCESSFUL value.
     */
    int getSuccessful();
}
