package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPointName;
import org.bouncycastle.asn1.x509.DistributionPointName;

public class DistributionPointNameBC extends ASN1EncodableBC implements IDistributionPointName {
    private static final DistributionPointNameBC INSTANCE = new DistributionPointNameBC(null);

    private static final int FULL_NAME = DistributionPointName.FULL_NAME;
    
    public DistributionPointNameBC(DistributionPointName distributionPointName) {
        super(distributionPointName);
    }

    public static DistributionPointNameBC getInstance() {
        return INSTANCE;
    }

    public DistributionPointName getDistributionPointName() {
        return (DistributionPointName) getEncodable();
    }

    @Override
    public int getType() {
        return getDistributionPointName().getType();
    }
    
    @Override
    public IASN1Encodable getName() {
        return new ASN1EncodableBC(getDistributionPointName().getName());
    }

    @Override
    public int getFullName() {
        return FULL_NAME;
    }
}
