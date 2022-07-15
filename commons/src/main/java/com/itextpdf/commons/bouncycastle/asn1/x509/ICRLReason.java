package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for CRLReason that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ICRLReason extends IASN1Encodable {
    /**
     * Gets {@code keyCompromise} constant for the wrapped CRLReason.
     *
     * @return CRLReason.keyCompromise value.
     */
    int getKeyCompromise();
}
