package com.itextpdf.commons.bouncycastle.asn1;

import java.io.IOException;

/**
 * This interface represents the wrapper for ASN1Primitive that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1Primitive extends IASN1Encodable {
    /**
     * Calls actual {@code getEncoded} method for the wrapped ASN1Primitive object.
     *
     * @return the default encoding for the wrapped object.
     *
     * @throws IOException on encoding error.
     */
    byte[] getEncoded() throws IOException;

    /**
     * Calls actual {@code getEncoded} method for the wrapped ASN1Primitive object.
     *
     * @param encoding encoding value
     *
     * @return the default encoding for the wrapped object.
     *
     * @throws IOException on encoding error.
     */
    byte[] getEncoded(String encoding) throws IOException;
}
