package com.itextpdf.bouncycastle.cert.jcajce;

import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;

import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;

/**
 * Wrapper class for {@link JcaX509CertificateConverter}.
 */
public class JcaX509CertificateConverterBC implements IJcaX509CertificateConverter {
    private final JcaX509CertificateConverter certificateConverter;

    /**
     * Creates new wrapper instance for {@link JcaX509CertificateConverter}.
     *
     * @param certificateConverter {@link JcaX509CertificateConverter} to be wrapped
     */
    public JcaX509CertificateConverterBC(JcaX509CertificateConverter certificateConverter) {
        this.certificateConverter = certificateConverter;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaX509CertificateConverter}.
     */
    public JcaX509CertificateConverter getJsaX509CertificateConverter() {
        return certificateConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public X509Certificate getCertificate(IX509CertificateHolder certificateHolder) throws CertificateException {
        return certificateConverter.getCertificate(
                ((X509CertificateHolderBC) certificateHolder).getCertificateHolder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaX509CertificateConverter setProvider(Provider provider) {
        certificateConverter.setProvider(provider);
        return this;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
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

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(certificateConverter);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return certificateConverter.toString();
    }
}
