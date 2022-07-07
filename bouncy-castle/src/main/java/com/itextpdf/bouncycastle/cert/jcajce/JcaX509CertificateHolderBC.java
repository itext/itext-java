package com.itextpdf.bouncycastle.cert.jcajce;

import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateHolder;

import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

public class JcaX509CertificateHolderBC extends X509CertificateHolderBC implements IJcaX509CertificateHolder {
    public JcaX509CertificateHolderBC(JcaX509CertificateHolder certificateHolder) {
        super(certificateHolder);
    }

    public JcaX509CertificateHolder getJcaCertificateHolder() {
        return (JcaX509CertificateHolder) getCertificateHolder();
    }
}
