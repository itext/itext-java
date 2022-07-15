package com.itextpdf.commons.bouncycastle.asn1;

import java.math.BigInteger;

/**
 * This interface represents the wrapper for ASN1Integer that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1Integer extends IASN1Primitive {
    /**
     * Calls actual {@code getValue} method for the wrapped ASN1Integer object.
     *
     * @return BigInteger value.
     */
    BigInteger getValue();
}
