package com.itextpdf.bouncycastle.cert;

import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.X509CertificateHolder;

/**
 * Wrapper class for {@link X509CertificateHolder}.
 */
public class X509CertificateHolderBC implements IX509CertificateHolder {
    private final X509CertificateHolder certificateHolder;

    /**
     * Creates new wrapper instance for {@link X509CertificateHolder}.
     *
     * @param certificateHolder {@link X509CertificateHolder} to be wrapped
     */
    public X509CertificateHolderBC(X509CertificateHolder certificateHolder) {
        this.certificateHolder = certificateHolder;
    }

    /**
     * Creates new wrapper instance for {@link X509CertificateHolder}.
     *
     * @param bytes bytes array to create {@link X509CertificateHolder} to be wrapped
     */
    public X509CertificateHolderBC(byte[] bytes) throws IOException {
        this.certificateHolder = new X509CertificateHolder(bytes);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link X509CertificateHolder}.
     */
    public X509CertificateHolder getCertificateHolder() {
        return certificateHolder;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        X509CertificateHolderBC that = (X509CertificateHolderBC) o;
        return Objects.equals(certificateHolder, that.certificateHolder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(certificateHolder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return certificateHolder.toString();
    }
}
