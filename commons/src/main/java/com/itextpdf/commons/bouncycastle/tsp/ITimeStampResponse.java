package com.itextpdf.commons.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.asn1.cmp.IPKIFailureInfo;

import java.io.IOException;

/**
 * This interface represents the wrapper for TimeStampResponse that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ITimeStampResponse {
    /**
     * Calls actual {@code validate} method for the wrapped TimeStampResponse object.
     *
     * @param request TimeStampRequest wrapper
     *
     * @throws AbstractTSPException TSPException wrapper.
     */
    void validate(ITimeStampRequest request) throws AbstractTSPException;

    /**
     * Calls actual {@code getFailInfo} method for the wrapped TimeStampResponse object.
     *
     * @return {@link IPKIFailureInfo} the wrapper for the received PKIFailureInfo object.
     */
    IPKIFailureInfo getFailInfo();

    /**
     * Calls actual {@code getTimeStampToken} method for the wrapped TimeStampResponse object.
     *
     * @return {@link ITimeStampToken} the wrapper for the received TimeStampToken object.
     */
    ITimeStampToken getTimeStampToken();

    /**
     * Calls actual {@code getStatusString} method for the wrapped TimeStampResponse object.
     *
     * @return status string.
     */
    String getStatusString();

    /**
     * Calls actual {@code getEncoded} method for the wrapped TimeStampResponse object.
     *
     * @return the default encoding for the wrapped object.
     *
     * @throws IOException on encoding error.
     */
    byte[] getEncoded() throws IOException;
}
