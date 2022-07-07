package com.itextpdf.bouncycastle.cert.jcajce;

import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;

import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;

public class JcaX509CertificateConverterBC implements IJcaX509CertificateConverter {
    private final JcaX509CertificateConverter certificateConverter;

    public JcaX509CertificateConverterBC(JcaX509CertificateConverter certificateConverter) {
        this.certificateConverter = certificateConverter;
    }

    public JcaX509CertificateConverter getJsaX509CertificateConverter() {
        return certificateConverter;
    }

    @Override
    public X509Certificate getCertificate(IX509CertificateHolder certificateHolder) throws CertificateException {
        return certificateConverter.getCertificate(
                ((X509CertificateHolderBC) certificateHolder).getCertificateHolder());
    }

    @Override
    public IJcaX509CertificateConverter setProvider(Provider provider) {
        certificateConverter.setProvider(provider);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JcaX509CertificateConverterBC that = (JcaX509CertificateConverterBC) o;
        return Objects.equals(certificateConverter, that.certificateConverter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateConverter);
    }

    @Override
    public String toString() {
        return certificateConverter.toString();
    }
}
