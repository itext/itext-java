package com.itextpdf.commons.bouncycastle.asn1;

import java.util.Enumeration;

/**
 * This interface represents the wrapper for ASN1Set that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1Set extends IASN1Primitive {
    /**
     * Calls actual {@code getObjects} method for the wrapped ASN1Set object.
     *
     * @return received objects.
     */
    Enumeration getObjects();

    /**
     * Calls actual {@code size} method for the wrapped ASN1Set object.
     *
     * @return set size.
     */
    int size();

    /**
     * Calls actual {@code getObjectAt} method for the wrapped ASN1Set object.
     *
     * @param index index
     *
     * @return {@link IASN1Encodable} wrapped ASN1Encodable object.
     */
    IASN1Encodable getObjectAt(int index);

    /**
     * Calls actual {@code toArray} method for the wrapped ASN1Set object.
     *
     * @return array of wrapped ASN1Encodable objects.
     */
    IASN1Encodable[] toArray();
}
