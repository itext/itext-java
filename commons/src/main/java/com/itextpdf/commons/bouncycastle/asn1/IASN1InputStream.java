package com.itextpdf.commons.bouncycastle.asn1;

import java.io.Closeable;
import java.io.IOException;

/**
 * This interface represents the wrapper for ASN1InputStream that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1InputStream extends Closeable {
    /**
     * Calls actual {@code readObject} method for the wrapped ASN1InputStream object.
     *
     * @return {@link IASN1Primitive} wrapped ASN1Primitive object.
     *
     * @throws IOException if an I/O error occurs.
     */
    IASN1Primitive readObject() throws IOException;

    /**
     * Delegates {@code close} method call to the wrapped stream.
     */
    @Override
    void close() throws IOException;
}
