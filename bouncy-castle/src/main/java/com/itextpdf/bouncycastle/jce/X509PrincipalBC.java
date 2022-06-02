package com.itextpdf.bouncycastle.jce;

import com.itextpdf.commons.bouncycastle.jce.IX509Principal;

import org.bouncycastle.jce.X509Principal;

public class X509PrincipalBC implements IX509Principal {
    private final X509Principal x509Principal;

    public X509PrincipalBC(X509Principal x509Principal) {
        this.x509Principal = x509Principal;
    }

    public X509Principal getX509Principal() {
        return x509Principal;
    }

    @Override
    public String getName() {
        return x509Principal.getName();
    }
}
