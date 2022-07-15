package com.itextpdf.commons.bouncycastle.cms.jcajce;

import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;

import java.security.cert.X509Certificate;

/**
 * This interface represents the wrapper for JcaSimpleSignerInfoVerifierBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJcaSimpleSignerInfoVerifierBuilder {
    /**
     * Calls actual {@code setProvider} method for the wrapped JcaSimpleSignerInfoVerifierBuilder object.
     *
     * @param provider provider name
     *
     * @return {@link IJcaSimpleSignerInfoVerifierBuilder} this wrapper object.
     */
    IJcaSimpleSignerInfoVerifierBuilder setProvider(String provider);

    /**
     * Calls actual {@code build} method for the wrapped JcaSimpleSignerInfoVerifierBuilder object.
     *
     * @param certificate X509Certificate
     *
     * @return {@link ISignerInformationVerifier} the wrapper for built SignerInformationVerifier object.
     *
     * @throws AbstractOperatorCreationException wrapped OperatorCreationException.
     */
    ISignerInformationVerifier build(X509Certificate certificate) throws AbstractOperatorCreationException;
}
