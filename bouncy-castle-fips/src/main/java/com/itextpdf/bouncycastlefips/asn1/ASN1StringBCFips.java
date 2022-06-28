package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1String;

import org.bouncycastle.asn1.ASN1String;

public class ASN1StringBCFips implements IASN1String {
    private final ASN1String asn1String;

    public ASN1StringBCFips(ASN1String asn1String) {
        this.asn1String = asn1String;
    }

    public ASN1String getAsn1String() {
        return asn1String;
    }

    @Override
    public String getString() {
        return asn1String.getString();
    }
}
