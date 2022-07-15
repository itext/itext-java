package com.itextpdf.bouncycastlefips.operator.jcajce;

import com.itextpdf.bouncycastlefips.operator.DigestCalculatorProviderBCFips;
import com.itextpdf.bouncycastlefips.operator.OperatorCreationExceptionBCFips;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaDigestCalculatorProviderBuilder;

import java.util.Objects;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

/**
 * Wrapper class for {@link JcaDigestCalculatorProviderBuilder}.
 */
public class JcaDigestCalculatorProviderBuilderBCFips implements IJcaDigestCalculatorProviderBuilder {
    private final JcaDigestCalculatorProviderBuilder providerBuilder;

    /**
     * Creates new wrapper instance for {@link JcaDigestCalculatorProviderBuilder}.
     *
     * @param providerBuilder {@link JcaDigestCalculatorProviderBuilder} to be wrapped
     */
    public JcaDigestCalculatorProviderBuilderBCFips(JcaDigestCalculatorProviderBuilder providerBuilder) {
        this.providerBuilder = providerBuilder;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaDigestCalculatorProviderBuilder}.
     */
    public JcaDigestCalculatorProviderBuilder getProviderBuilder() {
        return providerBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDigestCalculatorProvider build() throws OperatorCreationExceptionBCFips {
        try {
            return new DigestCalculatorProviderBCFips(providerBuilder.build());
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
        JcaDigestCalculatorProviderBuilderBCFips that = (JcaDigestCalculatorProviderBuilderBCFips) o;
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
