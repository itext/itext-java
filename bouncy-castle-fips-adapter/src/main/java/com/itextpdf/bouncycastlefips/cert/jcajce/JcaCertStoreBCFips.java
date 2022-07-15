package com.itextpdf.bouncycastlefips.cert.jcajce;

import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaCertStore;

import java.util.Objects;
import org.bouncycastle.cert.jcajce.JcaCertStore;

/**
 * Wrapper class for {@link JcaCertStore}.
 */
public class JcaCertStoreBCFips implements IJcaCertStore {
    private final JcaCertStore jcaCertStore;

    /**
     * Creates new wrapper instance for {@link JcaCertStore}.
     *
     * @param jcaCertStore {@link JcaCertStore} to be wrapped
     */
    public JcaCertStoreBCFips(JcaCertStore jcaCertStore) {
        this.jcaCertStore = jcaCertStore;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaCertStore}.
     */
    public JcaCertStore getJcaCertStore() {
        return jcaCertStore;
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
        JcaCertStoreBCFips that = (JcaCertStoreBCFips) o;
        return Objects.equals(jcaCertStore, that.jcaCertStore);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(jcaCertStore);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return jcaCertStore.toString();
    }
}
