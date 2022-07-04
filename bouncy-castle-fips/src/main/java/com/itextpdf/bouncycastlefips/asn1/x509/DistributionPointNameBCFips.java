package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPointName;
import org.bouncycastle.asn1.x509.DistributionPointName;

public class DistributionPointNameBCFips extends ASN1EncodableBCFips implements IDistributionPointName {
    private static final DistributionPointNameBCFips INSTANCE = new DistributionPointNameBCFips(null);
    
    private static final int FULL_NAME = DistributionPointName.FULL_NAME;

    public DistributionPointNameBCFips(DistributionPointName distributionPointName) {
        super(distributionPointName);
    }

    public static DistributionPointNameBCFips getInstance() {
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
        return new ASN1EncodableBCFips(getDistributionPointName().getName());
    }
    
    @Override
    public int getFullName() {
        return FULL_NAME;
    }
}
