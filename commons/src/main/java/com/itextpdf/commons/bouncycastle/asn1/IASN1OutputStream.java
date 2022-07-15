package com.itextpdf.commons.bouncycastle.asn1;

import java.io.Closeable;
import java.io.IOException;

/**
 * This interface represents the wrapper for ASN1OutputStream that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1OutputStream extends Closeable {
    /**
     * Calls actual {@code writeObject} method for the wrapped ASN1OutputStream object.
     *
     * @param primitive wrapped ASN1Primitive object.
     *
     * @throws IOException if an I/O error occurs.
     */
    void writeObject(IASN1Primitive primitive) throws IOException;

    /**
     * Delegates {@code close} method call to the wrapped stream.
     */
    @Override
    void close() throws IOException;
}
