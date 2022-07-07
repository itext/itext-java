package com.itextpdf.bouncycastlefips.cert.jcajce;

import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateHolder;

import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

public class JcaX509CertificateHolderBCFips extends X509CertificateHolderBCFips implements IJcaX509CertificateHolder {
    public JcaX509CertificateHolderBCFips(JcaX509CertificateHolder certificateHolder) {
        super(certificateHolder);
    }

    public JcaX509CertificateHolder getJcaCertificateHolder() {
        return (JcaX509CertificateHolder) getCertificateHolder();
    }
}
