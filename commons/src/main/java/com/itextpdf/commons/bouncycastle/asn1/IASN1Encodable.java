package com.itextpdf.commons.bouncycastle.asn1;

/**
 * This interface represents the wrapper for ASN1Encodable that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1Encodable {
    /**
     * Calls actual {@code toASN1Primitive} method for the wrapped ASN1Encodable object.
     *
     * @return {@link IASN1Primitive} wrapped ASN1Primitive object.
     */
    IASN1Primitive toASN1Primitive();

    /**
     * Checks if wrapped object is null.
     *
     * @return true if {@code null} is wrapped, false otherwise.
     */
    boolean isNull();
}
