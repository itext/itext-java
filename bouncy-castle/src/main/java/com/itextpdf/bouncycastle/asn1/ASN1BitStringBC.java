package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1BitString;

import org.bouncycastle.asn1.ASN1BitString;

public class ASN1BitStringBC extends ASN1PrimitiveBC implements IASN1BitString {
    public ASN1BitStringBC(ASN1BitString asn1BitString) {
        super(asn1BitString);
    }

    public ASN1BitString getASN1BitString() {
        return (ASN1BitString) getEncodable();
    }

    @Override
    public String getString() {
        return getASN1BitString().getString();
    }
}
