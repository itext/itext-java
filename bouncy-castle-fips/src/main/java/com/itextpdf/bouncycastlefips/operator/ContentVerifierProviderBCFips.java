package com.itextpdf.bouncycastlefips.operator;

import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;
import org.bouncycastle.operator.ContentVerifierProvider;

public class ContentVerifierProviderBCFips implements IContentVerifierProvider {
    private final ContentVerifierProvider provider;

    public ContentVerifierProviderBCFips(ContentVerifierProvider provider) {
        this.provider = provider;
    }

    public ContentVerifierProvider getProvider() {
        return provider;
    }
}
