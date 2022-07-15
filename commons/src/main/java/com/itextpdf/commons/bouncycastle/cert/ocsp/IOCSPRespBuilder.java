package com.itextpdf.commons.bouncycastle.cert.ocsp;

/**
 * This interface represents the wrapper for OCSPRespBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IOCSPRespBuilder {
    /**
     * Gets {@code SUCCESSFUL} constant for the wrapped OCSPRespBuilder.
     *
     * @return OCSPRespBuilder.SUCCESSFUL value.
     */
    int getSuccessful();

    /**
     * Calls actual {@code build} method for the wrapped OCSPRespBuilder object.
     *
     * @param i             status
     * @param basicOCSPResp BasicOCSPResp wrapper
     *
     * @return {@link IOCSPResp} the wrapper for built OCSPResp object.
     *
     * @throws AbstractOCSPException OCSPException wrapper.
     */
    IOCSPResp build(int i, IBasicOCSPResp basicOCSPResp) throws AbstractOCSPException;
}
