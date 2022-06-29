package com.itextpdf.bouncycastle.operator.jcajce;

import com.itextpdf.bouncycastle.operator.ContentVerifierProviderBC;
import com.itextpdf.bouncycastle.operator.OperatorCreationExceptionBC;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

import java.security.PublicKey;

public class JcaContentVerifierProviderBuilderBC implements IJcaContentVerifierProviderBuilder {
    private final JcaContentVerifierProviderBuilder providerBuilder;

    public JcaContentVerifierProviderBuilderBC(JcaContentVerifierProviderBuilder providerBuilder) {
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
    public IContentVerifierProvider build(PublicKey publicKey) throws OperatorCreationExceptionBC {
        try {
            return new ContentVerifierProviderBC(providerBuilder.build(publicKey));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBC(e);
        }
    }
}
