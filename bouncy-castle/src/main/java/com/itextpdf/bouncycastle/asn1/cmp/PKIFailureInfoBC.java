package com.itextpdf.bouncycastle.asn1.cmp;

import com.itextpdf.bouncycastle.asn1.ASN1PrimitiveBC;
import com.itextpdf.commons.bouncycastle.asn1.cmp.IPKIFailureInfo;

import org.bouncycastle.asn1.cmp.PKIFailureInfo;

public class PKIFailureInfoBC extends ASN1PrimitiveBC implements IPKIFailureInfo {

    public PKIFailureInfoBC(PKIFailureInfo pkiFailureInfo) {
        super(pkiFailureInfo);
    }

    public PKIFailureInfo getPkiFailureInfo() {
        return (PKIFailureInfo) getEncodable();
    }

    @Override
    public int intValue() {
        return getPkiFailureInfo().intValue();
    }
}
