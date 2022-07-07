package com.itextpdf.bouncycastle.operator;

import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;

import java.util.Objects;
import org.bouncycastle.operator.ContentVerifierProvider;

public class ContentVerifierProviderBC implements IContentVerifierProvider {
    private final ContentVerifierProvider provider;

    public ContentVerifierProviderBC(ContentVerifierProvider provider) {
        this.provider = provider;
    }

    public ContentVerifierProvider getContentVerifierProvider() {
        return provider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContentVerifierProviderBC that = (ContentVerifierProviderBC) o;
        return Objects.equals(provider, that.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider);
    }

    @Override
    public String toString() {
        return provider.toString();
    }
}
