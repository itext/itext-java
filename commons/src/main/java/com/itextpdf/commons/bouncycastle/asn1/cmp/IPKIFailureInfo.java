package com.itextpdf.commons.bouncycastle.asn1.cmp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

/**
 * This interface represents the wrapper for PKIFailureInfo that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IPKIFailureInfo extends IASN1Primitive {
    /**
     * Calls actual {@code intValue} method for the wrapped PKIFailureInfo object.
     *
     * @return integer value.
     */
    int intValue();
}
