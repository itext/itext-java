package com.itextpdf.commons.bouncycastle.asn1.tsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for MessageImprint that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IMessageImprint extends IASN1Encodable {
    /**
     * Calls actual {@code getHashedMessage} method for the wrapped MessageImprint object.
     *
     * @return hashed message byte array.
     */
    byte[] getHashedMessage();
}
