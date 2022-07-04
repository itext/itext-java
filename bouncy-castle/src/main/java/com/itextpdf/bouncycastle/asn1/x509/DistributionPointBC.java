package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPointName;
import org.bouncycastle.asn1.x509.DistributionPoint;

public class DistributionPointBC extends ASN1EncodableBC implements IDistributionPoint {
    public DistributionPointBC(DistributionPoint distributionPoint) {
        super(distributionPoint);
    }

    public DistributionPoint getPoint() {
        return (DistributionPoint) getEncodable();
    }
    
    @Override
    public IDistributionPointName getDistributionPoint() {
        return new DistributionPointNameBC(getPoint().getDistributionPoint());
    }
}
