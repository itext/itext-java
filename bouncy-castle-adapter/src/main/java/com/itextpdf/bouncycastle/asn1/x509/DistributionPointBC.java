package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPointName;

import org.bouncycastle.asn1.x509.DistributionPoint;

/**
 * Wrapper class for {@link DistributionPoint}.
 */
public class DistributionPointBC extends ASN1EncodableBC implements IDistributionPoint {
    /**
     * Creates new wrapper instance for {@link DistributionPoint}.
     *
     * @param distributionPoint {@link DistributionPoint} to be wrapped
     */
    public DistributionPointBC(DistributionPoint distributionPoint) {
        super(distributionPoint);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DistributionPoint}.
     */
    public DistributionPoint getPoint() {
        return (DistributionPoint) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDistributionPointName getDistributionPoint() {
        return new DistributionPointNameBC(getPoint().getDistributionPoint());
    }
}
