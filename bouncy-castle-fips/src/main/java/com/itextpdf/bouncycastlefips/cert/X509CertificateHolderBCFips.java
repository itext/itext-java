package com.itextpdf.bouncycastlefips.cert;

import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.X509CertificateHolder;

public class X509CertificateHolderBCFips implements IX509CertificateHolder {
    private final X509CertificateHolder certificateHolder;

    public X509CertificateHolderBCFips(X509CertificateHolder certificateHolder) {
        this.certificateHolder = certificateHolder;
    }

    public X509CertificateHolderBCFips(byte[] bytes) throws IOException {
        this.certificateHolder = new X509CertificateHolder(bytes);
    }

    public X509CertificateHolder getCertificateHolder() {
        return certificateHolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        X509CertificateHolderBCFips that = (X509CertificateHolderBCFips) o;
        return Objects.equals(certificateHolder, that.certificateHolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateHolder);
    }

    @Override
    public String toString() {
        return certificateHolder.toString();
    }
}
