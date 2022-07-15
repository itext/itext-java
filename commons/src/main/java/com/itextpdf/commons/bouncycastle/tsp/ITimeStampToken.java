package com.itextpdf.commons.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;

import java.io.IOException;

/**
 * This interface represents the wrapper for TimeStampToken that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ITimeStampToken {
    /**
     * Calls actual {@code getTimeStampInfo} method for the wrapped TimeStampToken object.
     *
     * @return {@link ITimeStampTokenInfo} the wrapper for the received TimeStampInfo object.
     */
    ITimeStampTokenInfo getTimeStampInfo();

    /**
     * Calls actual {@code validate} method for the wrapped TimeStampToken object.
     *
     * @param verifier SignerInformationVerifier wrapper
     *
     * @throws AbstractTSPException TSPException wrapper.
     */
    void validate(ISignerInformationVerifier verifier) throws AbstractTSPException;

    /**
     * Calls actual {@code getEncoded} method for the wrapped TimeStampToken object.
     *
     * @return the default encoding for the wrapped object.
     *
     * @throws IOException on encoding error.
     */
    byte[] getEncoded() throws IOException;
}
