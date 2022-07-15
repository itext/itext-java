package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for DistributionPoint that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IDistributionPoint extends IASN1Encodable {
    /**
     * Calls actual {@code getDistributionPoints} method for the wrapped DistributionPoint object.
     *
     * @return {@link IDistributionPointName} wrapped distribution point.
     */
    IDistributionPointName getDistributionPoint();
}
