package com.itextpdf.bouncycastle.cert.jcajce;

import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class JcaX509CertificateConverterBC implements IJcaX509CertificateConverter {
    private final JcaX509CertificateConverter certificateConverter;
    
    public JcaX509CertificateConverterBC(JcaX509CertificateConverter certificateConverter) {
        this.certificateConverter = certificateConverter;
    }

    public JcaX509CertificateConverter getCertificateConverter() {
        return certificateConverter;
    }

    @Override
    public X509Certificate getCertificate(IX509CertificateHolder certificateHolder) throws CertificateException {
        return certificateConverter.getCertificate(
                ((X509CertificateHolderBC) certificateHolder).getCertificateHolder());
    }
}
