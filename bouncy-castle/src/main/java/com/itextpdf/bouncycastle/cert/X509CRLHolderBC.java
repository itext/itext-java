package com.itextpdf.bouncycastle.cert;

import com.itextpdf.commons.bouncycastle.cert.IX509CRLHolder;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.X509CRLHolder;

public class X509CRLHolderBC implements IX509CRLHolder {
    private final X509CRLHolder x509CRLHolder;

    public X509CRLHolderBC(X509CRLHolder x509CRLHolder) {
        this.x509CRLHolder = x509CRLHolder;
    }

    public X509CRLHolder getX509CRLHolder() {
        return x509CRLHolder;
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return x509CRLHolder.getEncoded();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        X509CRLHolderBC that = (X509CRLHolderBC) o;
        return Objects.equals(x509CRLHolder, that.x509CRLHolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x509CRLHolder);
    }

    @Override
    public String toString() {
        return x509CRLHolder.toString();
    }
}
