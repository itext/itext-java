package com.itextpdf.bouncycastle.operator;

import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;
import org.bouncycastle.operator.ContentVerifierProvider;

public class ContentVerifierProviderBC implements IContentVerifierProvider {
    private final ContentVerifierProvider provider;
    
    public ContentVerifierProviderBC(ContentVerifierProvider provider) {
        this.provider = provider;
    }

    public ContentVerifierProvider getProvider() {
        return provider;
    }
}
