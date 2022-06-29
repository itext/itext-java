package com.itextpdf.bouncycastlefips.operator.jcajce;

import com.itextpdf.bouncycastlefips.operator.DigestCalculatorProviderBCFips;
import com.itextpdf.bouncycastlefips.operator.OperatorCreationExceptionBCFips;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaDigestCalculatorProviderBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class JcaDigestCalculatorProviderBuilderBCFips implements IJcaDigestCalculatorProviderBuilder {
    private final JcaDigestCalculatorProviderBuilder providerBuilder;

    public JcaDigestCalculatorProviderBuilderBCFips(JcaDigestCalculatorProviderBuilder providerBuilder) {
        this.providerBuilder = providerBuilder;
    }

    public JcaDigestCalculatorProviderBuilder getProviderBuilder() {
        return providerBuilder;
    }

    @Override
    public IDigestCalculatorProvider build() throws OperatorCreationExceptionBCFips {
        try {
            return new DigestCalculatorProviderBCFips(providerBuilder.build());
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBCFips(e);
        }
    }
}
