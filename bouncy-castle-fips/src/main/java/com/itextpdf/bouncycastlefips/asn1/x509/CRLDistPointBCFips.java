package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.ICRLDistPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPoint;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;

public class CRLDistPointBCFips extends ASN1EncodableBCFips implements ICRLDistPoint {
    public CRLDistPointBCFips(CRLDistPoint crlDistPoint) {
        super(crlDistPoint);
    }

    public CRLDistPoint getCrlDistPoint() {
        return (CRLDistPoint) getEncodable();
    }

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
