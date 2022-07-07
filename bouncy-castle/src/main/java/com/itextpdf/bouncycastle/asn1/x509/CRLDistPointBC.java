package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.ICRLDistPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPoint;

import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;

public class CRLDistPointBC extends ASN1EncodableBC implements ICRLDistPoint {
    public CRLDistPointBC(CRLDistPoint crlDistPoint) {
        super(crlDistPoint);
    }

    public CRLDistPoint getCrlDistPoint() {
        return (CRLDistPoint) getEncodable();
    }

    @Override
    public IDistributionPoint[] getDistributionPoints() {
        DistributionPoint[] distributionPoints = getCrlDistPoint().getDistributionPoints();
        IDistributionPoint[] distributionPointsBC = new DistributionPointBC[distributionPoints.length];

        for (int i = 0; i < distributionPoints.length; ++i) {
            distributionPointsBC[i] = new DistributionPointBC(distributionPoints[i]);
        }

        return distributionPointsBC;
    }
}
