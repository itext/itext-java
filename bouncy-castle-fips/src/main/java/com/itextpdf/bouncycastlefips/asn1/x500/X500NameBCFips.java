package com.itextpdf.bouncycastlefips.asn1.x500;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;

import org.bouncycastle.asn1.x500.X500Name;

public class X500NameBCFips extends ASN1EncodableBCFips implements IX500Name {
    public X500NameBCFips(X500Name x500Name) {
        super(x500Name);
    }

    public X500Name getX500Name() {
        return (X500Name) getEncodable();
    }
}
