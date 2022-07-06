package com.itextpdf.bouncycastle.asn1.x500;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;

import org.bouncycastle.asn1.x500.X500Name;

public class X500NameBC extends ASN1EncodableBC implements IX500Name {
    public X500NameBC(X500Name x500Name) {
        super(x500Name);
    }

    public X500Name getX500Name() {
        return (X500Name) getEncodable();
    }
}
