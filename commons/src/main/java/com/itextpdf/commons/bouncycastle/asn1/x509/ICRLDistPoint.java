package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for CRLDistPoint that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ICRLDistPoint extends IASN1Encodable {
    /**
     * Calls actual {@code getDistributionPoints} method for the wrapped CRLDistPoint object.
     *
     * @return array of the wrapped distribution points {@link IDistributionPoint}.
     */
    IDistributionPoint[] getDistributionPoints();
}
