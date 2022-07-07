package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.cms.IOriginatorInfo;

import org.bouncycastle.asn1.cms.OriginatorInfo;

public class OriginatorInfoBC extends ASN1EncodableBC implements IOriginatorInfo {
    public OriginatorInfoBC(OriginatorInfo originatorInfo) {
        super(originatorInfo);
    }

    public OriginatorInfo getOriginatorInfo() {
        return (OriginatorInfo) getEncodable();
    }
}
