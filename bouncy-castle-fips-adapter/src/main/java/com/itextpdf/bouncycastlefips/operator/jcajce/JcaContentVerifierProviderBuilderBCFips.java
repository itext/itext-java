package com.itextpdf.bouncycastlefips.operator.jcajce;

import com.itextpdf.bouncycastlefips.operator.ContentVerifierProviderBCFips;
import com.itextpdf.bouncycastlefips.operator.OperatorCreationExceptionBCFips;
import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaContentVerifierProviderBuilder;

import java.security.PublicKey;
import java.util.Objects;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

/**
 * Wrapper class for {@link JcaContentVerifierProviderBuilder}.
 */
public class JcaContentVerifierProviderBuilderBCFips implements IJcaContentVerifierProviderBuilder {
    private final JcaContentVerifierProviderBuilder providerBuilder;

    /**
     * Creates new wrapper instance for {@link JcaContentVerifierProviderBuilder}.
     *
     * @param providerBuilder {@link JcaContentVerifierProviderBuilder} to be wrapped
     */
    public JcaContentVerifierProviderBuilderBCFips(JcaContentVerifierProviderBuilder providerBuilder) {
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
    public IContentVerifierProvider build(PublicKey publicKey) throws OperatorCreationExceptionBCFips {
        try {
            return new ContentVerifierProviderBCFips(providerBuilder.build(publicKey));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBCFips(e);
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
        JcaContentVerifierProviderBuilderBCFips that = (JcaContentVerifierProviderBuilderBCFips) o;
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
