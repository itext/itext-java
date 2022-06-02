package com.itextpdf.bouncycastlefips.jce;

import com.itextpdf.commons.bouncycastle.jce.IX509Principal;

import org.bouncycastle.asn1.x500.X500Name;

public class X509PrincipalBCFips implements IX509Principal {
    private final X500Name x500Name;

    public X509PrincipalBCFips(X500Name x500Name) {
        this.x500Name = x500Name;
    }

    public X500Name getX500Name() {
        return x500Name;
    }

    @Override
    public String getName() {
        return x500Name.toString();
    }
}
