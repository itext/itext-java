package com.itextpdf.commons.bouncycastle.operator.jcajce;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.security.PrivateKey;

/**
 * This interface represents the wrapper for JcaContentSignerBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJcaContentSignerBuilder {
    /**
     * Calls actual {@code build} method for the wrapped JcaContentSignerBuilder object.
     *
     * @param pk private key
     *
     * @return {@link IContentSigner} the wrapper for built ContentSigner object.
     *
     * @throws AbstractOperatorCreationException wrapped OperatorCreationException.
     */
    IContentSigner build(PrivateKey pk) throws AbstractOperatorCreationException;

    /**
     * Calls actual {@code setProvider} method for the wrapped JcaContentSignerBuilder object.
     *
     * @param providerName provider name
     *
     * @return {@link IJcaContentSignerBuilder} this wrapper object.
     */
    IJcaContentSignerBuilder setProvider(String providerName);
}
