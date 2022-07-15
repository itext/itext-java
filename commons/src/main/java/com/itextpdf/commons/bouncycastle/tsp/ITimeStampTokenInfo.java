package com.itextpdf.commons.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import java.io.IOException;
import java.util.Date;

/**
 * This interface represents the wrapper for TimeStampTokenInfo that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ITimeStampTokenInfo {
    /**
     * Calls actual {@code getHashAlgorithm} method for the wrapped TimeStampTokenInfo object.
     *
     * @return {@link IAlgorithmIdentifier} the wrapper for the received AlgorithmIdentifier object.
     */
    IAlgorithmIdentifier getHashAlgorithm();

    /**
     * Calls actual {@code toASN1Structure} method for the wrapped TimeStampTokenInfo object.
     *
     * @return {@link ITSTInfo} TSTInfo wrapper.
     */
    ITSTInfo toASN1Structure();

    /**
     * Calls actual {@code getGenTime} method for the wrapped TimeStampTokenInfo object.
     *
     * @return {@link Date} the received genTime.
     */
    Date getGenTime();

    /**
     * Calls actual {@code getEncoded} method for the wrapped TimeStampTokenInfo object.
     *
     * @return the default encoding for the wrapped object.
     *
     * @throws IOException on encoding error.
     */
    byte[] getEncoded() throws IOException;
}
