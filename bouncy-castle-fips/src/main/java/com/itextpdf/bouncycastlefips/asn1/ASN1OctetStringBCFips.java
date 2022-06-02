package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;

import org.bouncycastle.asn1.ASN1OctetString;

public class ASN1OctetStringBCFips extends ASN1PrimitiveBCFips implements IASN1OctetString {
    public ASN1OctetStringBCFips(ASN1OctetString string) {
        super(string);
    }

    public ASN1OctetString getOctetString() {
        return (ASN1OctetString) getPrimitive();
    }

    @Override
    public byte[] getOctets() {
        return getOctetString().getOctets();
    }
}
