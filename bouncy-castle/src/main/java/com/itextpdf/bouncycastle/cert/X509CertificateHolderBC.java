package com.itextpdf.bouncycastle.cert;

import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import org.bouncycastle.cert.X509CertificateHolder;

import java.io.IOException;

public class X509CertificateHolderBC implements IX509CertificateHolder {
    private final X509CertificateHolder certificateHolder;

    public X509CertificateHolderBC(X509CertificateHolder certificateHolder) {
        this.certificateHolder = certificateHolder;
    }

    public X509CertificateHolderBC(byte[] bytes) throws IOException {
        this.certificateHolder = new X509CertificateHolder(bytes);
    }

    public X509CertificateHolder getCertificateHolder() {
        return certificateHolder;
    }
}
