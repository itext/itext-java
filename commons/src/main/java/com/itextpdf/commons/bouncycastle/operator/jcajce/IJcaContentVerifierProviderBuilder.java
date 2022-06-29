package com.itextpdf.commons.bouncycastle.operator.jcajce;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;

import java.security.PublicKey;

public interface IJcaContentVerifierProviderBuilder {
    IJcaContentVerifierProviderBuilder setProvider(String provider);

    IContentVerifierProvider build(PublicKey publicKey) throws AbstractOperatorCreationException;
}
