package com.itextpdf.bouncycastlefips.cert.jcajce;

import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JcaX509CertificateConverterBCFips that = (JcaX509CertificateConverterBCFips) o;
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
