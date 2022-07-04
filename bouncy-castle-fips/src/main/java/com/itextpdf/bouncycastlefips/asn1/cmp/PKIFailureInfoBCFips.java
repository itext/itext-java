package com.itextpdf.bouncycastlefips.asn1.cmp;

import com.itextpdf.bouncycastlefips.asn1.ASN1PrimitiveBCFips;
import com.itextpdf.commons.bouncycastle.asn1.cmp.IPKIFailureInfo;

import org.bouncycastle.asn1.cmp.PKIFailureInfo;

public class PKIFailureInfoBCFips extends ASN1PrimitiveBCFips implements IPKIFailureInfo  {

    public PKIFailureInfoBCFips(PKIFailureInfo pkiFailureInfo) {
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
