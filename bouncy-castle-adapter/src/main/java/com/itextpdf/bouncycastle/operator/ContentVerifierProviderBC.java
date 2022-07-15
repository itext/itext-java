package com.itextpdf.bouncycastle.operator;

import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;

import java.util.Objects;
import org.bouncycastle.operator.ContentVerifierProvider;

/**
 * Wrapper class for {@link ContentVerifierProvider}.
 */
public class ContentVerifierProviderBC implements IContentVerifierProvider {
    private final ContentVerifierProvider provider;

    /**
     * Creates new wrapper instance for {@link ContentVerifierProvider}.
     *
     * @param provider {@link ContentVerifierProvider} to be wrapped
     */
    public ContentVerifierProviderBC(ContentVerifierProvider provider) {
        this.provider = provider;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ContentVerifierProvider}.
     */
    public ContentVerifierProvider getContentVerifierProvider() {
        return provider;
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
        ContentVerifierProviderBC that = (ContentVerifierProviderBC) o;
        return Objects.equals(provider, that.provider);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(provider);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return provider.toString();
    }
}
