package com.itextpdf.bouncycastlefips.operator.jcajce;

import com.itextpdf.bouncycastlefips.operator.ContentVerifierProviderBCFips;
import com.itextpdf.bouncycastlefips.operator.OperatorCreationExceptionBCFips;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

import java.security.PublicKey;

public class JcaContentVerifierProviderBuilderBCFips implements IJcaContentVerifierProviderBuilder {
    private final JcaContentVerifierProviderBuilder providerBuilder;
    
    public JcaContentVerifierProviderBuilderBCFips(JcaContentVerifierProviderBuilder providerBuilder) {
        this.providerBuilder = providerBuilder;
    }
    
    public JcaContentVerifierProviderBuilder getProviderBuilder() {
        return providerBuilder;
    }

    @Override
    public IJcaContentVerifierProviderBuilder setProvider(String provider) {
        providerBuilder.setProvider(provider);
        return this;
    }

    @Override
    public IContentVerifierProvider build(PublicKey publicKey) throws OperatorCreationExceptionBCFips {
        try {
            return new ContentVerifierProviderBCFips(providerBuilder.build(publicKey));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBCFips(e);
        }
    }
}
