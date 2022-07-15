package com.itextpdf.commons.bouncycastle.asn1;

/**
 * This interface represents the wrapper for ASN1String that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1String {
    /**
     * Calls actual {@code getString} method for the wrapped ASN1String object.
     *
     * @return the resulting string.
     */
    String getString();
}
