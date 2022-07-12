package com.itextpdf.bouncycastle.cert.jcajce;

import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaCertStore;

import java.util.Objects;
import org.bouncycastle.cert.jcajce.JcaCertStore;

public class JcaCertStoreBC implements IJcaCertStore {
    private final JcaCertStore jcaCertStore;

    public JcaCertStoreBC(JcaCertStore jcaCertStore) {
        this.jcaCertStore = jcaCertStore;
    }

    public JcaCertStore getJcaCertStore() {
        return jcaCertStore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JcaCertStoreBC that = (JcaCertStoreBC) o;
        return Objects.equals(jcaCertStore, that.jcaCertStore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jcaCertStore);
    }

    @Override
    public String toString() {
        return jcaCertStore.toString();
    }
}
