package com.itextpdf.bouncycastlefips.cert;

import com.itextpdf.commons.bouncycastle.cert.IX509CRLHolder;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.X509CRLHolder;

/**
 * Wrapper class for {@link X509CRLHolder}.
 */
public class X509CRLHolderBCFips implements IX509CRLHolder {
    private final X509CRLHolder x509CRLHolder;

    /**
     * Creates new wrapper instance for {@link X509CRLHolder}.
     *
     * @param x509CRLHolder {@link X509CRLHolder} to be wrapped
     */
    public X509CRLHolderBCFips(X509CRLHolder x509CRLHolder) {
        this.x509CRLHolder = x509CRLHolder;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link X509CRLHolder}.
     */
    public X509CRLHolder getX509CRLHolder() {
        return x509CRLHolder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return x509CRLHolder.getEncoded();
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
        X509CRLHolderBCFips that = (X509CRLHolderBCFips) o;
        return Objects.equals(x509CRLHolder, that.x509CRLHolder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x509CRLHolder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return x509CRLHolder.toString();
    }
}
