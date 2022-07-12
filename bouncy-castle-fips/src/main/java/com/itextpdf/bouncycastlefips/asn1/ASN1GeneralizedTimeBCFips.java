package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1GeneralizedTime;

import org.bouncycastle.asn1.ASN1GeneralizedTime;

public class ASN1GeneralizedTimeBCFips extends ASN1PrimitiveBCFips implements IASN1GeneralizedTime {
    public ASN1GeneralizedTimeBCFips(ASN1GeneralizedTime asn1GeneralizedTime) {
        super(asn1GeneralizedTime);
    }

    public ASN1GeneralizedTime getASN1GeneralizedTime() {
        return (ASN1GeneralizedTime) getEncodable();
    }
}
