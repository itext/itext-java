package com.itextpdf.commons.bouncycastle.asn1;

/**
 * This interface represents the wrapper for ASN1OctetString that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1OctetString extends IASN1Primitive {
    /**
     * Calls actual {@code getOctets} method for the wrapped ASN1OctetString object.
     *
     * @return octets byte array.
     */
    byte[] getOctets();
}
