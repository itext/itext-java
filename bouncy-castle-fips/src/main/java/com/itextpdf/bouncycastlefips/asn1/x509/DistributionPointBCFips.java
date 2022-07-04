package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPointName;
import org.bouncycastle.asn1.x509.DistributionPoint;

public class DistributionPointBCFips extends ASN1EncodableBCFips implements IDistributionPoint {
    public DistributionPointBCFips(DistributionPoint distributionPoint) {
        super(distributionPoint);
    }

    public DistributionPoint getPoint() {
        return (DistributionPoint) getEncodable();
    }

    @Override
    public IDistributionPointName getDistributionPoint() {
        return new DistributionPointNameBCFips(getPoint().getDistributionPoint());
    }
}
