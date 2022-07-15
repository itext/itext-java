package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for DistributionPointName that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IDistributionPointName extends IASN1Encodable {
    /**
     * Calls actual {@code getType} method for the wrapped DistributionPointName object.
     *
     * @return type value.
     */
    int getType();

    /**
     * Calls actual {@code getName} method for the wrapped DistributionPointName object.
     *
     * @return {@link IASN1Encodable} ASN1Encodable wrapper.
     */
    IASN1Encodable getName();

    /**
     * Gets {@code FULL_NAME} constant for the wrapped DistributionPointName.
     *
     * @return DistributionPointName.FULL_NAME value.
     */
    int getFullName();
}
