package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1UTCTime;

import org.bouncycastle.asn1.ASN1UTCTime;

public class ASN1UTCTimeBCFips extends ASN1PrimitiveBCFips implements IASN1UTCTime {
    public ASN1UTCTimeBCFips(ASN1UTCTime asn1UTCTime) {
        super(asn1UTCTime);
    }

    public ASN1UTCTime getASN1UTCTime() {
        return (ASN1UTCTime) getEncodable();
    }
}
