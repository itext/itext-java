package com.itextpdf.commons.bouncycastle.cert;

import java.io.IOException;

/**
 * This interface represents the wrapper for X509CRLHolder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IX509CRLHolder {
    /**
     * Calls actual {@code getEncoded} method for the wrapped X509CRLHolder object.
     *
     * @return the default encoding for the wrapped object.
     *
     * @throws IOException on encoding error.
     */
    byte[] getEncoded() throws IOException;
}
