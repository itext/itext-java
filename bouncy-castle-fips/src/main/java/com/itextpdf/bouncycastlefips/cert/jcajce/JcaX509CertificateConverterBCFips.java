package com.itextpdf.bouncycastlefips.cert.jcajce;

import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class JcaX509CertificateConverterBCFips implements IJcaX509CertificateConverter {
    private final JcaX509CertificateConverter certificateConverter;

    public JcaX509CertificateConverterBCFips(JcaX509CertificateConverter certificateConverter) {
        this.certificateConverter = certificateConverter;
    }

    public JcaX509CertificateConverter getCertificateConverter() {
        return certificateConverter;
    }

    @Override
    public X509Certificate getCertificate(IX509CertificateHolder certificateHolder) throws CertificateException {
        return certificateConverter.getCertificate(
                ((X509CertificateHolderBCFips) certificateHolder).getCertificateHolder());
    }
}
