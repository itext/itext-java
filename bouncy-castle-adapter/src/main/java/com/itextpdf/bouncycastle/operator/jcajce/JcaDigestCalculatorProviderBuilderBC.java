package com.itextpdf.bouncycastle.operator.jcajce;

import com.itextpdf.bouncycastle.operator.DigestCalculatorProviderBC;
import com.itextpdf.bouncycastle.operator.OperatorCreationExceptionBC;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaDigestCalculatorProviderBuilder;

import java.util.Objects;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class JcaDigestCalculatorProviderBuilderBC implements IJcaDigestCalculatorProviderBuilder {
    private final JcaDigestCalculatorProviderBuilder providerBuilder;

    public JcaDigestCalculatorProviderBuilderBC(JcaDigestCalculatorProviderBuilder providerBuilder) {
        this.providerBuilder = providerBuilder;
    }

    public JcaDigestCalculatorProviderBuilder getProviderBuilder() {
        return providerBuilder;
    }

    @Override
    public IDigestCalculatorProvider build() throws OperatorCreationExceptionBC {
        try {
            return new DigestCalculatorProviderBC(providerBuilder.build());
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBC(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JcaDigestCalculatorProviderBuilderBC that = (JcaDigestCalculatorProviderBuilderBC) o;
        return Objects.equals(providerBuilder, that.providerBuilder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerBuilder);
    }

    @Override
    public String toString() {
        return providerBuilder.toString();
    }
}
