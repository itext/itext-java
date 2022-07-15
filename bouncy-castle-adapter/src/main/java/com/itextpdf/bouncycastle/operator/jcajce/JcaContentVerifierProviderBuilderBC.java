package com.itextpdf.bouncycastle.operator.jcajce;

import com.itextpdf.bouncycastle.operator.ContentVerifierProviderBC;
import com.itextpdf.bouncycastle.operator.OperatorCreationExceptionBC;
import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaContentVerifierProviderBuilder;

import java.security.PublicKey;
import java.util.Objects;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

/**
 * Wrapper class for {@link JcaContentVerifierProviderBuilder}.
 */
public class JcaContentVerifierProviderBuilderBC implements IJcaContentVerifierProviderBuilder {
    private final JcaContentVerifierProviderBuilder providerBuilder;

    /**
     * Creates new wrapper instance for {@link JcaContentVerifierProviderBuilder}.
     *
     * @param providerBuilder {@link JcaContentVerifierProviderBuilder} to be wrapped
     */
    public JcaContentVerifierProviderBuilderBC(JcaContentVerifierProviderBuilder providerBuilder) {
        this.providerBuilder = providerBuilder;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaContentVerifierProviderBuilder}.
     */
    public JcaContentVerifierProviderBuilder getProviderBuilder() {
        return providerBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaContentVerifierProviderBuilder setProvider(String provider) {
        providerBuilder.setProvider(provider);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IContentVerifierProvider build(PublicKey publicKey) throws OperatorCreationExceptionBC {
        try {
            return new ContentVerifierProviderBC(providerBuilder.build(publicKey));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBC(e);
        }
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
        JcaContentVerifierProviderBuilderBC that = (JcaContentVerifierProviderBuilderBC) o;
        return Objects.equals(providerBuilder, that.providerBuilder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(providerBuilder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return providerBuilder.toString();
    }
}
