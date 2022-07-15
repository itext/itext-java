package com.itextpdf.bouncycastle.operator.jcajce;

import com.itextpdf.bouncycastle.operator.ContentSignerBC;
import com.itextpdf.bouncycastle.operator.OperatorCreationExceptionBC;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaContentSignerBuilder;

import java.security.PrivateKey;
import java.util.Objects;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * Wrapper class for {@link JcaContentSignerBuilder}.
 */
public class JcaContentSignerBuilderBC implements IJcaContentSignerBuilder {
    private final JcaContentSignerBuilder jcaContentSignerBuilder;

    /**
     * Creates new wrapper instance for {@link JcaContentSignerBuilder}.
     *
     * @param jcaContentSignerBuilder {@link JcaContentSignerBuilder} to be wrapped
     */
    public JcaContentSignerBuilderBC(JcaContentSignerBuilder jcaContentSignerBuilder) {
        this.jcaContentSignerBuilder = jcaContentSignerBuilder;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaContentSignerBuilder}.
     */
    public JcaContentSignerBuilder getJcaContentSignerBuilder() {
        return jcaContentSignerBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IContentSigner build(PrivateKey pk) throws OperatorCreationExceptionBC {
        try {
            return new ContentSignerBC(jcaContentSignerBuilder.build(pk));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBC(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaContentSignerBuilder setProvider(String providerName) {
        jcaContentSignerBuilder.setProvider(providerName);
        return this;
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
        JcaContentSignerBuilderBC that = (JcaContentSignerBuilderBC) o;
        return Objects.equals(jcaContentSignerBuilder, that.jcaContentSignerBuilder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(jcaContentSignerBuilder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return jcaContentSignerBuilder.toString();
    }
}
