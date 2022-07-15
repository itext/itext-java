package com.itextpdf.commons.bouncycastle.operator.jcajce;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;

/**
 * This interface represents the wrapper for JcaDigestCalculatorProviderBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJcaDigestCalculatorProviderBuilder {
    /**
     * Calls actual {@code build} method for the wrapped JcaDigestCalculatorProviderBuilder object.
     *
     * @return {@link IDigestCalculatorProvider} the wrapper for built DigestCalculatorProvider object.
     *
     * @throws AbstractOperatorCreationException wrapped OperatorCreationException.
     */
    IDigestCalculatorProvider build() throws AbstractOperatorCreationException;
}
