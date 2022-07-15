package com.itextpdf.commons.bouncycastle.tsp;

import java.io.IOException;
import java.math.BigInteger;

/**
 * This interface represents the wrapper for TimeStampRequest that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ITimeStampRequest {
    /**
     * Calls actual {@code getEncoded} method for wrapped TimeStampRequest object.
     *
     * @return the default encoding for the wrapped object.
     *
     * @throws IOException on encoding error.
     */
    byte[] getEncoded() throws IOException;

    /**
     * Calls actual {@code getNonce} method for wrapped TimeStampRequest object.
     *
     * @return nonce value.
     */
    BigInteger getNonce();
}
