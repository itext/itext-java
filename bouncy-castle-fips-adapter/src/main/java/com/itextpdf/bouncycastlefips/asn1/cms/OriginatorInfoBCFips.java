package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.cms.IOriginatorInfo;

import org.bouncycastle.asn1.cms.OriginatorInfo;

public class OriginatorInfoBCFips extends ASN1EncodableBCFips implements IOriginatorInfo {
    public OriginatorInfoBCFips(OriginatorInfo originatorInfo) {
        super(originatorInfo);
    }

    public OriginatorInfo getOriginatorInfo() {
        return (OriginatorInfo) getEncodable();
    }
}
