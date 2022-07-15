package com.itextpdf.commons.bouncycastle.asn1;

/**
 * This interface represents the wrapper for ASN1ObjectIdentifier that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1ObjectIdentifier extends IASN1Primitive {
    /**
     * Calls actual {@code getId} method for the wrapped ASN1ObjectIdentifier object.
     *
     * @return string ID.
     */
    String getId();
}
