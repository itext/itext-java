package com.itextpdf.commons.bouncycastle.operator.jcajce;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;

import java.security.PublicKey;

/**
 * This interface represents the wrapper for JcaContentVerifierProviderBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJcaContentVerifierProviderBuilder {
    /**
     * Calls actual {@code setProvider} method for the wrapped JcaContentVerifierProviderBuilder object.
     *
     * @param provider provider name
     *
     * @return {@link IJcaContentVerifierProviderBuilder} this wrapper object.
     */
    IJcaContentVerifierProviderBuilder setProvider(String provider);

    /**
     * Calls actual {@code build} method for the wrapped JcaContentVerifierProviderBuilder object.
     *
     * @param publicKey public key
     *
     * @return {@link IContentVerifierProvider} the wrapper for built ContentVerifierProvider object.
     *
     * @throws AbstractOperatorCreationException wrapped OperatorCreationException.
     */
    IContentVerifierProvider build(PublicKey publicKey) throws AbstractOperatorCreationException;
}
