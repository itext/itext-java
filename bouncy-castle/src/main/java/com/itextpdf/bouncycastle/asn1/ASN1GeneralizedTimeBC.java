package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1GeneralizedTime;

import org.bouncycastle.asn1.ASN1GeneralizedTime;

public class ASN1GeneralizedTimeBC extends ASN1PrimitiveBC implements IASN1GeneralizedTime {
    public ASN1GeneralizedTimeBC(ASN1GeneralizedTime asn1GeneralizedTime) {
        super(asn1GeneralizedTime);
    }

    public ASN1GeneralizedTime getASN1GeneralizedTime() {
        return (ASN1GeneralizedTime) getEncodable();
    }
}
