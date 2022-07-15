package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.ICRLDistPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPoint;

import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;

/**
 * Wrapper class for {@link CRLDistPoint}.
 */
public class CRLDistPointBCFips extends ASN1EncodableBCFips implements ICRLDistPoint {
    /**
     * Creates new wrapper instance for {@link CRLDistPoint}.
     *
     * @param crlDistPoint {@link CRLDistPoint} to be wrapped
     */
    public CRLDistPointBCFips(CRLDistPoint crlDistPoint) {
        super(crlDistPoint);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link CRLDistPoint}.
     */
    public CRLDistPoint getCrlDistPoint() {
        return (CRLDistPoint) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDistributionPoint[] getDistributionPoints() {
        DistributionPoint[] distributionPoints = getCrlDistPoint().getDistributionPoints();
        IDistributionPoint[] distributionPointsBC = new DistributionPointBCFips[distributionPoints.length];

        for (int i = 0; i < distributionPoints.length; ++i) {
            distributionPointsBC[i] = new DistributionPointBCFips(distributionPoints[i]);
        }

        return distributionPointsBC;
    }
}
